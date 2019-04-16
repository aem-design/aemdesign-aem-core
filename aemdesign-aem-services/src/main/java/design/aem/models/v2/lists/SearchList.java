package design.aem.models.v2.lists;

import com.adobe.cq.sightly.WCMUsePojo;
import com.adobe.granite.asset.api.AssetManager;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.i18n.I18n;
import com.day.cq.search.*;
import com.day.cq.search.facets.Bucket;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagConstants;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import design.aem.CustomSearchResult;
import design.aem.components.ComponentProperties;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.vault.util.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.io.ByteArrayInputStream;
import java.net.URLDecoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static design.aem.utils.components.CommonUtil.DEFAULT_LIST_DETAILS_SUFFIX;
import static design.aem.utils.components.CommonUtil.findComponentInPage;
import static design.aem.utils.components.ComponentDetailsUtil.processBadgeRequestConfig;
import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.ConstantsUtil.*;
import static design.aem.utils.components.I18nUtil.*;
import static design.aem.utils.components.ImagesUtil.*;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class SearchList extends WCMUsePojo {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchList.class);

    private ComponentProperties componentProperties = null;

    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(intValues = 0)
    protected int listSplitEvery;

    private final static String ASSET_LICENSEINFO = "© {4} {0} {1} {2} {3}";

    @Override
    public void activate() throws Exception {

//        LOGGER.error("searchlist loading");

        com.day.cq.i18n.I18n i18n = new I18n(getRequest());

        final String DEFAULT_I18N_CATEGORY = "searchlist";
        final String DEFAULT_ARIA_ROLE = "search";

        final java.util.List<CustomSearchResult> results = new ArrayList<>();

        // Perform the search
        final Query query = composeQueryBulilder(getRequest(), getResourceResolver());

        SearchResult result = null;
        String queryText = StringUtils.EMPTY;

        if (query != null) {
            result = query.getResult();

            Predicate fulltextPredicate = query.getPredicates().getByName("fulltext");

            if (fulltextPredicate != null) {
                queryText = fulltextPredicate.get("fulltext", StringUtils.EMPTY);
            }
        }

        final String escapedQuery = getXSSAPI().encodeForHTML(queryText);
        final String escapedQueryForAttr = getXSSAPI().encodeForHTMLAttr(queryText);
        final String escapedQueryForHref = getXSSAPI().getValidHref(queryText);


        // {
        //   { name, defaultValue, attributeName, valueTypeClass }
        // }
        Object[][] componentFields = {
                {FIELD_VARIANT, DEFAULT_VARIANT},
                {"emptyQueryText", getDefaultLabelIfEmpty("emptyQueryText", DEFAULT_I18N_CATEGORY, "Invalid query given!", i18n)},
                {"searchButtonText", getDefaultLabelIfEmpty("searchButtonText", DEFAULT_I18N_CATEGORY, "Search", i18n)},
                {"searchQueryInformation", getDefaultLabelIfEmpty("searchQueryInformation", DEFAULT_I18N_CATEGORY, "Search query information", i18n)},
                {"statisticsText", getDefaultLabelIfEmpty("statisticsText", DEFAULT_I18N_CATEGORY, "<div class='content'> <h2>Results {0} for <b>{1}</b></h2></div>", i18n)},
                {"noResultsText", getDefaultLabelIfEmpty("noResultsText", DEFAULT_I18N_CATEGORY, "Your search - <b>{0}</b> - did not match any documents.", i18n)},
                {"spellcheckText", getDefaultLabelIfEmpty("spellcheckText", DEFAULT_I18N_CATEGORY, "Did you mean:", i18n)},
                {"similarPagesText", getDefaultLabelIfEmpty("similarPagesText", DEFAULT_I18N_CATEGORY, "Similar Pages", i18n)},
                {"relatedSearchesText", getDefaultLabelIfEmpty("relatedSearchesText", DEFAULT_I18N_CATEGORY, "Related searches:", i18n)},
                {"searchTrendsText", getDefaultLabelIfEmpty("searchTrendsText", DEFAULT_I18N_CATEGORY, "Search Trends", i18n)},
                {"resultPagesText", getDefaultLabelIfEmpty("resultPagesText", DEFAULT_I18N_CATEGORY, "Results", i18n)},
                {"previousText", getDefaultLabelIfEmpty("previousText", DEFAULT_I18N_CATEGORY, "Previous", i18n)},
                {"nextText", getDefaultLabelIfEmpty("nextText", DEFAULT_I18N_CATEGORY, "Next", i18n)},
                {"assetActionText", getDefaultLabelIfEmpty("assetActionText", DEFAULT_I18N_CATEGORY, "Download", i18n)},
                {"pageActionText", getDefaultLabelIfEmpty("pageActionText", DEFAULT_I18N_CATEGORY, "Read More", i18n)},
                {"otherActionText", getDefaultLabelIfEmpty("otherActionText", DEFAULT_I18N_CATEGORY, "Find Out More", i18n)},
                {"componentPath", getResource().getResourceType()},
                {"escapedQuery", escapedQuery},
                {"escapedQueryForAttr", escapedQueryForAttr},
                {"escapedQueryForHref", escapedQueryForHref},
                {"printStructure", true},
                {"listTag", "ul"},
                {FIELD_ARIA_ROLE, DEFAULT_ARIA_ROLE, DEFAULT_ARIA_ROLE_ATTRIBUTE},
        };

        componentProperties = ComponentsUtil.getComponentProperties(
                this,
                componentFields,
                DEFAULT_FIELDS_STYLE,
                DEFAULT_FIELDS_ACCESSIBILITY,
                DEFAULT_FIELDS_DETAILS_OPTIONS);


        String pageUrl = getCurrentPage().getPath().concat(DEFAULT_EXTENTION);

//        componentProperties.put(COMPONENT_ATTRIBUTES,
//                addComponentBackgroundToAttributes(componentProperties,getResource(), DEFAULT_BACKGROUND_IMAGE_NODE_NAME));

        componentProperties.putAll(
                getAssetInfo(getResourceResolver(), getResourceImagePath(getResource(), DEFAULT_BACKGROUND_IMAGE_NODE_NAME), FIELD_PAGE_BACKGROUND_IMAGE));

        componentProperties.putAll(processBadgeRequestConfig(componentProperties, getResourceResolver(), getRequest()), true);

        componentProperties.attr.add("data-component-id", componentProperties.get(FIELD_STYLE_COMPONENT_ID, getResource().getName()));
//        componentProperties.put(COMPONENT_ATTRIBUTES,
//                addComponentAttributes(componentProperties, "data-component-id", componentName));

        if (result != null) {
            if (!isEmpty(componentProperties.get("statisticsText", ""))) {
                componentProperties.put("statisticsText", MessageFormat.format(componentProperties.get("statisticsText", ""),result.getTotalMatches(),escapedQuery));
            }

            // Normalise the results tree
            normaliseContentTree(results, getSlingScriptHelper(), getRequest(), result);

            // Pagination
            if (result.getResultPages().size() > 0 && result.getNextPage() != null) {
                boolean hasPages = false;

                if (result.getNextPage() != null) {
                    hasPages = true;


                    // This value is offset by one because the front end code is expecting the index to begin at '0'
                    componentProperties.attr.add("data-total-pages", String.valueOf(result.getResultPages().size() - 1));
                    componentProperties.attr.add("data-content-target", "#".concat(componentProperties.get(FIELD_STYLE_COMPONENT_ID, "")));
                    componentProperties.attr.add("data-page-offset", String.valueOf(result.getHits().size()));
                    componentProperties.attr.add("data-showing-text", componentProperties.get("statisticsText", ""));


                }

                componentProperties.attr.add("data-content-url", pageUrl.concat("?q=").concat(queryText));
                componentProperties.attr.add("data-content-start", "start");
                componentProperties.attr.add("data-has-pages", String.valueOf(hasPages));

            }


            componentProperties.put("facetsTitle", i18n.get("Tags"));

            //process facets
            if (result.getFacets() != null) {
                if (result.getFacets().get("tags") != null) {
                    if (result.getFacets().get("tags").getContainsHit()) {

                        TagManager tagManager = getResourceResolver().adaptTo(TagManager.class);
                        ArrayList<Map<String, Object>> bucketsInfo = new ArrayList<>();

                        for (Bucket bucket : result.getFacets().get("tags").getBuckets()) {

                            Map<String, Object> bucketInfo = new HashMap<>();

                            bucketInfo.put("bucket", bucketInfo);

                            Tag tag = tagManager.resolve(bucket.getValue());
                            if (tag != null) {
                                bucketInfo.put("tag", bucketInfo);

                            }

                            if (java.util.Arrays.asList(getRequest().getParameterValues("tag")).contains(bucket.getValue())) {
                                bucketInfo.put("filter", true);
                            }

                            bucketsInfo.add(bucketInfo);
                        }

                        componentProperties.put("bucketsInfo", bucketsInfo);
                    }
                }

            }


            listSplitEvery = 0;

            if (listSplitEvery > 0) {
                //create an array of list positions at which list should split
                ArrayList listSplitAtItem = new ArrayList();

                for (int i = 0; i < results.size(); i++) {
                    if ((i + 1) % listSplitEvery == 0) {
                        listSplitAtItem.add(i);
                    }
                }

                componentProperties.put("listSplitAtItem", listSplitAtItem);
            }
        } else {
            componentProperties.put("isEmpty", true);
        }

        componentProperties.put("results", results);
        componentProperties.put("includePagination", false);

        componentProperties.put("currentPageUrl", getCurrentPage().getPath().concat(DEFAULT_EXTENTION));


        //override properties
        componentProperties.put("listItemLinkText",
                getDefaultLabelIfEmpty("", DEFAULT_I18N_CATEGORY, DEFAULT_I18N_LIST_ITEM_LINK_TEXT, DEFAULT_I18N_CATEGORY, i18n));

        componentProperties.put("listItemLinkTitle",
                getDefaultLabelIfEmpty("", DEFAULT_I18N_CATEGORY, DEFAULT_I18N_LIST_ITEM_LINK_TITLE, DEFAULT_I18N_CATEGORY, i18n));


        componentProperties.put(COMPONENT_ATTRIBUTES, buildAttributesString(componentProperties.attr.getData(), null));


//        LOGGER.error("searchlist loaded");


    }


    /**
     * Composes the `QueryBuilder` instance using the query parameter sent from the client-side.
     *
     * @param slingRequest     `SlingHttpServletRequest` instance
     * @param resourceResolver `ResourceResolver` instance
     */
    public Query composeQueryBulilder(
            SlingHttpServletRequest slingRequest,
            ResourceResolver resourceResolver
    ) {
        Query query = null;

        if (slingRequest.getRequestParameter("q") != null) {
            String escapedQuery = slingRequest.getRequestParameter("q").toString();

            try {
                String unescapedQuery = URLDecoder.decode(escapedQuery, "UTF-8");
                QueryBuilder queryBuilder = resourceResolver.adaptTo(QueryBuilder.class);

                // Create props for query
                java.util.Properties props = new java.util.Properties();

                // Load query candidate
                props.load(new ByteArrayInputStream(unescapedQuery.getBytes()));

                // Create predicate from query candidate
                PredicateGroup predicateGroup = PredicateConverter.createPredicates(props);
                javax.jcr.Session jcrSession = slingRequest.getResourceResolver().adaptTo(javax.jcr.Session.class);

                query = queryBuilder.createQuery(predicateGroup, jcrSession);
            } catch (Exception ex) {
                LOGGER.error("Error using QueryBuilder with query [{}]. {}", escapedQuery, ex);
            }
        }

        return query;
    }

    /**
     * Normalise the result data for the final output.
     *
     * @param searchResults Array to store the normalised dataset in
     * @param sling         `SlingScriptHelper` instance
     * @param slingRequest  `SlingHttpServletRequest` instance
     */
    public void normaliseContentTree(
            java.util.List<CustomSearchResult> searchResults,
            SlingScriptHelper sling,
            SlingHttpServletRequest slingRequest,
            SearchResult result
    ) {
        try {
            if (!result.getHits().isEmpty()) {
                for (com.day.cq.search.result.Hit h : result.getHits()) {
                    CustomSearchResult newResult = new CustomSearchResult(h.getPath());
                    String jcrPrimaryType = h.getProperties().get("jcr:primaryType").toString();

//                    LOGGER.error("normaliseContentTree: jcrPrimaryType={},getExcerpt={},h.getExcerpts()={}",jcrPrimaryType,h.getExcerpt(),h.getExcerpts());

                    newResult.setTitle(h.getTitle());

                    if (h.getProperties().get("subtitle") != null) {
                        newResult.setSubTitle(h.getProperties().get("subtitle").toString());
                    }


                    if (jcrPrimaryType.equals("cq:PageContent")) {
                        newResult.setPathUrl(h.getPath().concat(DEFAULT_EXTENTION));

                        newResult.setExcerpt(h.getExcerpt());

                        Resource hitResource = h.getResource();

                        if (hitResource != null) {
                            ResourceResolver resourceResolver = slingRequest.getResourceResolver();

                            if (hitResource != null && !ResourceUtil.isNonExistingResource(hitResource)) {

                                Page hitPage = hitResource.adaptTo(Page.class);

                                String detailsNodePath = findComponentInPage(hitPage, DEFAULT_LIST_DETAILS_SUFFIX);

                                Resource detailsResource  = getResourceResolver().resolve(detailsNodePath);

                                if (!ResourceUtil.isNonExistingResource(detailsResource)) {

                                    String detailsPath = detailsResource.getPath();

                                    newResult.setDetailsPath(detailsPath);
                                    newResult.setIsPage(true);

                                    String thumbnailImagePath = getResourceImagePath(detailsResource, DEFAULT_THUMBNAIL_IMAGE_NODE_NAME);
                                    String backgroundImagePath = getResourceImagePath(detailsResource, DEFAULT_BACKGROUND_IMAGE_NODE_NAME);

//                                    LOGGER.error("normaliseContentTree: detailsPath={},thumbnailImagePath={},backgroundImagePath={}", detailsPath,thumbnailImagePath,backgroundImagePath);

                                    if (thumbnailImagePath != null) {
                                        newResult.setThumbnailUrl(thumbnailImagePath);
                                    } else if (backgroundImagePath != null) {
                                        newResult.setThumbnailUrl(backgroundImagePath);
                                    } else {
                                        newResult.setThumbnailUrl(DEFAULT_IMAGE_BLANK);
                                    }
                                }
                            }

                            // Get description from page else get the excerpt from search hit
                            newResult.setExcerpt(h.getProperties().get(JcrConstants.JCR_DESCRIPTION, h.getExcerpt()));

                        }
                    }

                    if (jcrPrimaryType.equals("dam:AssetContent")) {
                        String relativePath = h.getProperties().get("dam:relativePath").toString();

                        newResult.setExcerpt(h.getExcerpt());
                        newResult.setPathUrl(h.getPath());
                        newResult.setIsAsset(true);

                        Resource assetResource = h.getResource();
                        Node assetN = assetResource.adaptTo(Node.class);

                        AssetManager assetManager = getResourceResolver().adaptTo(AssetManager.class);
                        com.adobe.granite.asset.api.Asset asset = assetManager.getAsset(h.getPath());

                        Asset assetBasic = assetResource.adaptTo(Asset.class);

                        newResult.setThumbnailUrl(assetBasic.getPath());

//                        String assetTags = getMetadataStringForKey(assetN, TagConstants.PN_TAGS, "");

                        String licenseInfo = getAssetCopyrightInfo(assetBasic, ASSET_LICENSEINFO);

                        if (isNotEmpty(licenseInfo)) {
                            newResult.setSubTitle(licenseInfo);
                        }

                        if (assetBasic != null) {
                            newResult.setTitle(assetBasic.getMetadataValue(DamConstants.DC_TITLE));
                        }

                        String excerpt = h.getExcerpt();

//                        LOGGER.error("normaliseContentTree: DC_TITLE={},DC_DESCRIPTION={}",assetBasic.getMetadataValue(DamConstants.DC_TITLE),assetBasic.getMetadataValue(DamConstants.DC_DESCRIPTION));
//                        LOGGER.error("normaliseContentTree: h.getPath()={},excerpt is path={},getThumbnailUrl={}",h.getPath(),excerpt.equals(h.getPath()),newResult.getThumbnailUrl());

                        if (excerpt.equals(h.getPath()) && assetBasic != null) {
                            newResult.setExcerpt(assetBasic.getMetadataValue(DamConstants.DC_DESCRIPTION));
                        }



                    }

                    searchResults.add(newResult);
                }
            }
        } catch (Exception ex) {
            LOGGER.warn("Repository exception thrown: " + ex.toString());
        }
    }

    /**
     * Gets the Image File Reference from the Resource at the given path.
     *
     * @param resourceResolver `ResourceResolver` instance
     * @param path             The JCR path to the resource
     * @return A string containing the resource path
     */
    private String getPathFromImageResource(ResourceResolver resourceResolver, String path) {
        String imagePath = null;

        if (resourceResolver != null) {
            Resource image = resourceResolver.resolve(path);

            if (image != null && !ResourceUtil.isNonExistingResource(image)) {
                ValueMap valueMap = image.adaptTo(ValueMap.class);

                if (valueMap != null && valueMap.containsKey("fileReference")) {
                    imagePath = valueMap.get("fileReference", String.class);
                }
            }
        }

        return imagePath;
    }

}