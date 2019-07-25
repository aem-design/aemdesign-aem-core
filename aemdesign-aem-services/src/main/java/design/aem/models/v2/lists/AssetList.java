package design.aem.models.v2.lists;

import com.adobe.granite.asset.api.AssetManager;
import com.day.cq.commons.ImageResource;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.ResultPage;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.TagConstants;
import design.aem.components.ComponentProperties;
import design.aem.models.ModelProxy;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.vault.util.JcrConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.time.Duration;
import java.util.List;
import java.util.*;

import static design.aem.models.v2.lists.List.SortOrder;
import static design.aem.utils.components.CommonUtil.DAM_FIELD_LICENSE_USAGETERMS;
import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.ConstantsUtil.IMAGE_OPTION_ADAPTIVE;
import static design.aem.utils.components.ConstantsUtil.IMAGE_OPTION_RENDITION;
import static design.aem.utils.components.ImagesUtil.*;
import static design.aem.utils.components.TagUtil.getTagsAsAdmin;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class AssetList extends ModelProxy {
    protected static final Logger LOGGER = LoggerFactory.getLogger(AssetList.class);

    protected ComponentProperties componentProperties = null;
    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }

    private List<Map<String,Object>> listItems;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(intValues = LIMIT_DEFAULT)
    private int limit;

    private static final String PN_SOURCE = "listFrom"; //SOURCE_PROPERTY_NAME
    private static final String PN_SOURCE_DEFAULT = Source.STATIC.getValue(); //SOURCE_PROPERTY_NAME
    private static final String PARENT_PATH = "parentPath";
    private static final String STATIC_ITEMS = "assets";
    private static final String DESCENDANT_PATH = "ancestorPath";
    private static final String PARENT_PATH_DEFAULT = "/content/dam";
    private static final String LIMIT_PROPERTY_NAME = "limit";
    private static final int LIMIT_DEFAULT = 100;
    private static final String PN_SORT_ORDER = "sortOrder";
    private static final String PN_ORDER_BY = "orderBy";
    private static final String PN_ORDER_BY_DEFAULT = "path";
    private static final String LIST_ISEMPTY = "isEmpty";

    private static final String FIELD_IMAGEURL = "imageURL";
    private static final String FIELD_RENDITIONS = "renditions";
    private static final String FIELD_TITLE_TAG_TYPE_DEFAULT = "div";
    private static final String FIELD_IMAGE_OPTION_DEFAULT = "responsive";


    private static final String ASSET_LICENSEINFO = "© {4} {0} {1} {2} {3}";

    private long totalMatches;
    private long hitsPerPage;
    private long totalPages;
    private long pageStart;
    private long currentPage;
    private List<ResultPage> resultPages;
    private SortOrder sortOrder;

    @SuppressWarnings("Duplicates")
    protected void ready() {
        /*
          Component Fields Helper

          Structure:
          1 required - property name,
          2 required - default value,
          3 optional - name of component attribute to add value into
          4 optional - canonical name of class for handling multivalues, String or Tag
         */
        setComponentFields(new Object[][]{
                {FIELD_VARIANT, DEFAULT_VARIANT},
                {STATIC_ITEMS, new String[0]},
                {DESCENDANT_PATH, PARENT_PATH_DEFAULT},
                {PN_SOURCE, PN_SOURCE_DEFAULT},
                {PARENT_PATH, PARENT_PATH_DEFAULT},
                {PN_ORDER_BY, StringUtils.EMPTY},
                {LIMIT_PROPERTY_NAME, LIMIT_DEFAULT},
                {PN_SORT_ORDER, SortOrder.ASC.getValue()},
                {FIELD_IMAGE_OPTION, FIELD_IMAGE_OPTION_DEFAULT},
                {FIELD_TITLE_TAG_TYPE, FIELD_TITLE_TAG_TYPE_DEFAULT},
        });

        componentProperties = ComponentsUtil.getComponentProperties(
                this,
                componentFields,
                DEFAULT_FIELDS_IMAGE_OPTIONS,
                DEFAULT_FIELDS_STYLE,
                DEFAULT_FIELDS_ACCESSIBILITY,
                DEFAULT_FIELDS_ANALYTICS
        );

        sortOrder = SortOrder.fromString(componentProperties.get(PN_SORT_ORDER, SortOrder.ASC.getValue()));
        limit = componentProperties.get(LIMIT_PROPERTY_NAME, LIMIT_DEFAULT);
    }


    /**
     * get list options type.
     * @return list type
     */
    protected Source getListType() {
        if (componentProperties != null) {
            String listFromValue = componentProperties.get(PN_SOURCE, getCurrentStyle().get(PN_SOURCE, StringUtils.EMPTY));
            return Source.fromString(listFromValue);
        }
        return Source.STATIC;
    }

    /**
     * get list items, used by HTL templates.
     * @return list of items
     */
    public Collection<Map<String,Object>> getListItems() {

        if (listItems == null) {
            Source listType = getListType();

            populateListItems(listType);
        }

        return listItems;
    }

    /**
     * populate list items.
     * @param listType list type to execute
     */
    @SuppressWarnings("Duplicates")
    protected void populateListItems(Source listType) {

        switch (listType) {
            case STATIC: //SOURCE_STATIC
                populateStaticListItems();
                break;
            case CHILDREN: //SOURCE_CHILDREN
                populateChildListItems();
                break;
            case DESCENDANTS: //SOURCE_DESCENDANTS
                populateDescendantsListItems();
                break;
            default:
                listItems = new ArrayList<>();
                break;
        }
        componentProperties.put(LIST_ISEMPTY, totalMatches == 0);
    }

    /**
     * populate list items from only children of a root page.
     */
    private void populateChildListItems() {
        String path = componentProperties.get(PARENT_PATH,PARENT_PATH_DEFAULT);
        populateChildListItems(path, true);
    }


    /**
     * populate list items from descendants of a root page.
     */
    private void populateDescendantsListItems() {
        String path = componentProperties.get(DESCENDANT_PATH,PARENT_PATH_DEFAULT);
        populateChildListItems(path, false);
    }


    /**
     * populate list items from children of a root page.
     * @param flat only select children on root page
     */
    @SuppressWarnings("Duplicates")
    private void populateChildListItems(String path, Boolean flat) {
        listItems = new ArrayList<>();

        Map<String, String> childMap = new HashMap<>();
        childMap.put("path", path);
        if (flat) {
            childMap.put("path.flat", "true");
        } else {
            childMap.put("path.flat", "false");
        }
        childMap.put("type", DamConstants.NT_DAM_ASSET);

        populateListItemsFromMap(childMap);
    }

    /**
     * doa query using a predicate map.
     * @param map predicate map
     */
    @SuppressWarnings("Duplicates")
    private void populateListItemsFromMap(Map<String,String> map) {
        try {

            QueryBuilder builder = getResourceResolver().adaptTo(QueryBuilder.class);
            if (builder != null) {
                Session session = getResourceResolver().adaptTo(Session.class);

                Query query = null;

                //limit is set
                map.put("p.limit", String.valueOf(limit));

                String orderBy = componentProperties.get(PN_ORDER_BY, PN_ORDER_BY_DEFAULT);
                if (isNotEmpty(orderBy)) {
                    map.put("orderby", orderBy);
                } else {
                    map.put("orderby", PN_ORDER_BY_DEFAULT);
                }

                map.put("orderby.sort", sortOrder.getValue());

                PredicateGroup root = PredicateGroup.create(map);
                // avoid slow //* queries
                if (!root.isEmpty()) {
                    query = builder.createQuery(root, session);
                }

                if (query != null) {
                    collectSearchResults(query.getResult());
                }
            } else {
                LOGGER.error("populateListItemsFromMap: could not get query builder object, map=[{}]",map);
            }
        } catch (Exception ex) {
            LOGGER.error("populateListItemsFromMap: could not execute query map=[{}], ex={}",map,ex);
        }
    }

    /**
     * populates listItems with resources from pages list.
     * page object is also resolved and returned if available
     */
    @SuppressWarnings("Duplicates")
    private void populateStaticListItems() {
        listItems = new ArrayList<>();
        String[] items = componentProperties.get(STATIC_ITEMS, new String[0]);
        AssetManager assetManager = getResourceResolver().adaptTo(AssetManager.class);

        if (assetManager != null) {
            for (String item : items) {

                Resource assetResource = getResourceResolver().resolve(item);

                com.adobe.granite.asset.api.Asset asset = assetManager.getAsset(item);

                if (asset != null) {

                    ComponentProperties assetInfo = getAssetInfo(asset, assetResource, componentProperties, getSlingScriptHelper());

                    if (assetInfo != null) {
                        listItems.add(assetInfo);
                    }

                } else {
                    continue;
                }

            }
        } else {
            LOGGER.error("ImageImpl: could not get AssetManager object");
        }
    }

    private ComponentProperties getAssetInfo(com.adobe.granite.asset.api.Asset asset, Resource assetResource, ComponentProperties componentProperties, SlingScriptHelper sling) {

        final String PROPERTY_METADATA = JcrConstants.JCR_CONTENT + "/metadata";
        final String PROPERTY_METADATA_DURATION = JcrConstants.JCR_CONTENT + "/metadata/xmpDM:duration";
        try {
            if (asset != null && sling != null) {

                if (!ResourceUtil.isNonExistingResource(assetResource)) {
                    Asset assetBasic = assetResource.adaptTo(Asset.class);

                    String assetPath = assetResource.getPath();

                    String imageOption = componentProperties.get(FIELD_IMAGE_OPTION, FIELD_IMAGE_OPTION_DEFAULT);
                    String titleType = componentProperties.get(FIELD_TITLE_TAG_TYPE, FIELD_TITLE_TAG_TYPE_DEFAULT);

                    /*
                      Component Fields Helper

                      Structure:
                      1 required - property name,
                      2 required - default value,
                      3 optional - name of component attribute to add value into
                      4 optional - canonical name of class for handling multivalues, String or Tag
                     */
                    Object[][] assetField = {
                            {"name", assetBasic.getName()},
                            {"id", assetBasic.getID(),FIELD_ASSETID},
                            {"path", assetBasic.getPath()},
                            {"originalPath", assetBasic.getOriginal()},
                            {"mimeType", assetBasic.getMimeType(), "data-mimetype"},
                            {"lastModified", assetBasic.getLastModified()},
                            {"isSubAsset", assetBasic.isSubAsset()},
                            {FIELD_RENDITIONS, assetBasic.listRenditions()},
                            {"linkURL", ""},
                            {"imageOption", imageOption},
                            {"titleType", titleType},
                            {"href", assetPath, "data-href"},

                    };

                    //get asset properties
                    ComponentProperties assetProperties = ComponentsUtil.getComponentProperties(
                            this,
                            asset,
                            false,
                            assetField,
                            DEFAULT_FIELDS_ASSET
                    );

                    String assetType = assetProperties.get(DamConstants.DC_FORMAT,"");

                    boolean checkDuration = false;
                    boolean getRenditions = false;

                    if (assetType.startsWith("video/")) {
                        assetProperties.put("assetType","video");
                        checkDuration = true;
                        getRenditions = true;
                    } else if (assetType.startsWith("audio/") || assetType.startsWith("application/") ) {
                        assetProperties.put("assetType","audio");
                        checkDuration = true;
                    } else if (assetType.startsWith("image/")) {
                        assetProperties.put("assetType","image");
                        getRenditions = true;
                    } else {
                        assetProperties.put("assetType","other");
                        getRenditions = true;
                    }

                    if (checkDuration) {

                        Resource assetMetadataDurationResource = assetResource.getChild(PROPERTY_METADATA_DURATION);
                        if (assetMetadataDurationResource != null) {

                            ValueMap assetMetadataDurationValueMap = assetMetadataDurationResource.getValueMap();

                            try {

                                String durationScale = assetMetadataDurationValueMap.get("xmpDM:scale", "");
                                String durationValue = assetMetadataDurationValueMap.get("xmpDM:value", "");

                                Double scale = Double.parseDouble("1");
                                Double value = Double.parseDouble(durationValue);
                                if (durationScale.contains("/")) {
                                    String[] scaleList = durationScale.split("/");
                                    String stringOne = scaleList[0];
                                    String stringDivided = scaleList[1];

                                    Double doubleOne = Double.parseDouble(stringOne);
                                    Double doubleDivided = Double.parseDouble(stringDivided);

                                    scale = doubleOne / doubleDivided;
                                }

                                Double duration = scale * value;

                                Duration durationObject = Duration.ofSeconds(Math.round(duration));

                                assetProperties.put("duration",durationObject.toString());


                            } catch (Exception ex) {
                                LOGGER.error("getAssetInfo: could not extract duration assetMetadataDurationValueMap={}",assetMetadataDurationValueMap);
                            }
                        }
                    }


                    if (assetBasic != null) {
                        Resource assetMetadataResource = assetResource.getChild(PROPERTY_METADATA);


                        if (assetMetadataResource != null) {
                            ValueMap assetMetadata = assetMetadataResource.getValueMap();

                            assetProperties.put("assetTags", getTagsAsAdmin(sling, assetMetadata.get(TagConstants.PN_TAGS, new String[0]), getRequest().getLocale()));

                            String assetUsageTerms = assetMetadata.get(DAM_FIELD_LICENSE_USAGETERMS,"");
                            assetProperties.put("assetUsageTerms", assetUsageTerms);

                        }

                        String licenseInfo = getAssetCopyrightInfo(assetBasic, ASSET_LICENSEINFO);
                        assetProperties.put(FIELD_LICENSE_INFO, licenseInfo);

                        assetProperties.attr.add("data-license", licenseInfo);
                    }


                    Map<String, String> responsiveImageSet = new LinkedHashMap<String, String>();

                    if (getRenditions) {
                        try {
                            switch (imageOption) {
                                case IMAGE_OPTION_RENDITION:
                                    int targetWidth = componentProperties.get(ImageResource.PN_WIDTH, 0);
                                    com.adobe.granite.asset.api.Rendition bestRendition = getBestFitRendition(targetWidth, asset);
                                    if (bestRendition != null) {

                                        responsiveImageSet.put("", bestRendition.getPath());
                                    }
                                    break;
                                case IMAGE_OPTION_ADAPTIVE:
                                    String[] adaptiveImageMapping = componentProperties.get(FIELD_ADAPTIVE_MAP, new String[]{});

                                    responsiveImageSet = getAdaptiveImageSet(adaptiveImageMapping, getResourceResolver(), assetPath, assetPath, null, false, sling);

                                    break;
                                default: //IMAGE_OPTION_RESPONSIVE
                                    String[] renditionImageMapping = componentProperties.get(FIELD_RESPONSIVE_MAP, new String[]{});

                                    // Check if the image suffix is '.svg' or '.gif', if it is skip any rendition checks and simply return
                                    // the path to it as no scaling or modifications should be applied.
                                    if (assetPath.endsWith(".svg") || assetPath.endsWith(".gif")) {
                                        assetProperties.put(FIELD_IMAGEURL, assetPath);
                                        assetProperties.put(FIELD_IMAGE_OPTION, "simple");
                                    } else {
                                        //get rendition profile prefix selected
                                        String renditionPrefix = componentProperties.get(FIELD_RENDITION_PREFIX, "");

                                        //get best fit renditions set
                                        responsiveImageSet = getBestFitMediaQueryRenditionSet(asset, renditionImageMapping, renditionPrefix);

                                    }
                            }
                        } catch (Exception ex) {
                            LOGGER.error("getAssetInfo: could not collect renditions asset={}", asset);
                        }

                    }
                    assetProperties.put(FIELD_RENDITIONS, responsiveImageSet);

                    //pick last one from collection
                    if (responsiveImageSet.values().size() > 0) {
                        assetProperties.put(FIELD_IMAGEURL, responsiveImageSet.values()
                                .toArray()[responsiveImageSet.values().size() - 1]);
                    }


                    assetProperties.put(COMPONENT_ATTRIBUTES, buildAttributesString(assetProperties.attr.getData(), null));

                    return assetProperties;

                }
            } else {
                LOGGER.error("getAssetInfo: could not get asset info {}", asset);
            }
        } catch (Exception ex) {
            LOGGER.error("getAssetInfo: error processing asset asset={}, ex={}", asset, ex);
        }
        return null;
    }

    /**
     * process search results.
     * @param result search results
     * @throws RepositoryException when can't access content in repository
     */
    @SuppressWarnings("Duplicates")
    private void collectSearchResults(SearchResult result) throws RepositoryException {
        Map<String, Object> resultInfo = new HashMap<>();
        resultInfo.put("executionTime",result.getExecutionTime());
        resultInfo.put("result",result);

        totalMatches = result.getTotalMatches();
        resultPages = result.getResultPages();
        hitsPerPage = result.getHitsPerPage();
        totalPages = result.getResultPages().size();
        pageStart = result.getStartIndex();
        currentPage = (pageStart / hitsPerPage) + 1;

        resultInfo.put("hitsPerPage",hitsPerPage);
        resultInfo.put("currentPage",currentPage);
        resultInfo.put("totalMatches",totalMatches);
        resultInfo.put("resultPages",resultPages);
        resultInfo.put("totalPages",totalPages);

        componentProperties.put("resultInfo",resultInfo);

        AssetManager assetManager = getResourceResolver().adaptTo(AssetManager.class);

        if (assetManager != null) {
            for (Hit hit : result.getHits()) {
                Map<String, Object> item = new HashMap<>();
                item.put("hit", hit);

                Resource itemResource = hit.getResource();

                com.adobe.granite.asset.api.Asset asset = assetManager.getAsset(itemResource.getPath());

                if (asset != null) {

                    ComponentProperties assetInfo = getAssetInfo(asset, itemResource, componentProperties, getSlingScriptHelper());

                    if (assetInfo != null) {
                        listItems.add(assetInfo);
                    }

                } else {
                    LOGGER.error("populateStaticListItems: could not find asset {}", item);
                    continue;
                }


            }
        } else {
            LOGGER.error("ImageImpl: could not get AssetManager object");
        }
    }


    protected enum Source {
        CHILDREN("children"),
        STATIC("static"),
        DESCENDANTS("descendants"),
        EMPTY(StringUtils.EMPTY);

        private String value;

        public String getValue() {
            return value;
        }

        Source(String value) {
            this.value = value;
        }

        public static Source fromString(String value) {
            for (Source s : values()) {
                if (StringUtils.equals(value, s.value)) {
                    return s;
                }
            }
            return null;
        }
    }

}