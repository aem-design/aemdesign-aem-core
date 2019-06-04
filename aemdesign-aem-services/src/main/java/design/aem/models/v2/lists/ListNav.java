package design.aem.models.v2.lists;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.ResultPage;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.Page;
import design.aem.components.ComponentProperties;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.List;
import java.util.*;

import static design.aem.models.v2.lists.List.SortOrder;
import static design.aem.utils.components.CommonUtil.DEFAULT_LIST_DETAILS_SUFFIX;
import static design.aem.utils.components.CommonUtil.getBadgeFromSelectors;
import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.ImagesUtil.FIELD_IMAGE_OPTION;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class ListNav extends WCMUsePojo {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ListNav.class);

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
    private static final String STATIC_ITEMS = "pages";
    private static final String DESCENDANT_PATH = "ancestorPath";
    private String PARENT_PATH_DEFAULT = "/content";
    private static final String LIMIT_PROPERTY_NAME = "limit";
    private static final int LIMIT_DEFAULT = 100;
    private static final String PN_SORT_ORDER = "sortOrder";
    private static final String PN_ORDER_BY = "orderBy";
    private static final String PN_ORDER_BY_DEFAULT = "path";
    private static final String LIST_ISEMPTY = "isEmpty";

    private static final String FIELD_IMAGE_OPTION_DEFAULT = "responsive";

    private static final String LIST_SEARCH_TYPE = "type";
    private static final String LIST_SEARCH_TYPE_DEFAULT = "cq:Page";

    private static final String LIST_LOOP = "listloop";
    private static final boolean LIST_LOOP_DEFAULT = false;

    private static final String DETAILSBADGE = "detailsBadge";


    private long totalMatches;
    private long hitsPerPage;
    private long totalPages;
    private long pageStart;
    private long currentPage;
    private List<ResultPage> resultPages;
    private SortOrder sortOrder;

    private boolean listLoop = false;

    private Page filterPage;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = StringUtils.EMPTY)
    private String detailsBadge;

    @Override
    public void activate() throws Exception {

        PARENT_PATH_DEFAULT = getCurrentPage().getPath();

        //COMPONENT STYLES
        // {
        //   1 required - property name,
        //   2 required - default value,
        //   3 optional - name of component attribute to add value into
        //   4 optional - canonical name of class for handling multivalues, String or Tag
        // }
        Object[][] componentFields = {
                {FIELD_VARIANT, DEFAULT_VARIANT},
                {STATIC_ITEMS, new String[0]},
                {DESCENDANT_PATH, PARENT_PATH_DEFAULT},
                {PN_SOURCE, PN_SOURCE_DEFAULT},
                {PARENT_PATH, PARENT_PATH_DEFAULT},
                {PN_ORDER_BY, StringUtils.EMPTY},
                {LIMIT_PROPERTY_NAME, LIMIT_DEFAULT},
                {PN_SORT_ORDER, SortOrder.ASC.getValue()},
                {FIELD_IMAGE_OPTION, FIELD_IMAGE_OPTION_DEFAULT},
                {LIST_LOOP, LIST_LOOP_DEFAULT},
                {"filterPage", ""},
                {DETAILSBADGE, DEFAULT_BADGE, "data-badge"},
        };

        componentProperties = ComponentsUtil.getComponentProperties(
                this,
                componentFields,
                DEFAULT_FIELDS_STYLE,
                DEFAULT_FIELDS_ACCESSIBILITY
        );

        sortOrder = SortOrder.fromString(componentProperties.get(PN_SORT_ORDER, SortOrder.ASC.getValue()));
        limit = componentProperties.get(LIMIT_PROPERTY_NAME, LIMIT_DEFAULT);
        listLoop = componentProperties.get(LIST_LOOP, LIST_LOOP_DEFAULT);
        detailsBadge = componentProperties.get(DETAILSBADGE, DEFAULT_BADGE);

        String filterPagePage = componentProperties.get("filterPage", "");

        if (isNotEmpty(filterPagePage)) {
            filterPage = getPageManager().getPage(filterPagePage);
        } else {
            filterPage = getCurrentPage();
        }

//        LOGGER.error("activate: parentPath={},parentPath2={},resourcePath={},filterPage={},ancestorPath={}",componentProperties.get(PARENT_PATH,""),getProperties().get("parentPath"),getResource().getPath(),filterPage.getPath(),componentProperties.get(DESCENDANT_PATH,""));

        //process badge selection
        String componentBadge = getBadgeFromSelectors(getRequest().getRequestPathInfo().getSelectorString());

        LOGGER.error("processCommonFields: componentBadge={}",componentBadge);

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
//        LOGGER.error("populateListItems: listType={}",listType);
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
        childMap.put(LIST_SEARCH_TYPE, LIST_SEARCH_TYPE_DEFAULT);

        populateListItemsFromMap(childMap);
    }

    /**
     * doa query using a predicate map.
     * @param map predicate map
     */
    @SuppressWarnings("Duplicates")
    private void populateListItemsFromMap(Map<String,String> map) {
        try {
//            LOGGER.error("populateListItemsFromMap: map={}",map);

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

                //LOGGER.error("populateListItemsFromMap: running query with map=[{}]", map);

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
            LOGGER.error("populateListItemsFromMap: could not execute query map=[{}], ex={}",map,ex.toString());
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
        ResourceResolver resourceResolver = getResourceResolver();

        Resource previousResource = null;
        Resource currentResource = null;
        Resource nextResource = null;
        Resource firstResource = null;

        for (String path : items) {

            Resource pathResource = resourceResolver.resolve(path);

            if (!ResourceUtil.isNonExistingResource(pathResource)) {

//            LOGGER.error("collectSearchResults: previousResource={},currentResource={},nextResource={},firstResource={}",previousResource,currentResource,nextResource,firstResource);
//            LOGGER.error("collectSearchResults: hit={}",hit.getPath());

                if (firstResource == null) {
                    firstResource = pathResource;
                }

                if (currentResource == null) {
                    if (pathResource.getPath().equals(filterPage.getPath())) {
                        //found current page
                        currentResource = pathResource;

                    } else {
                        previousResource = pathResource;
                    }
                } else {
                    nextResource = pathResource;

                    break;
                }

            } else {
                LOGGER.error("populateStaticListItems: skipping item as it does not exist {}", path);
                continue;
            }

        }

        addNavItems(previousResource,currentResource,nextResource,firstResource);


    }

    /**
     * add items to list items from results.
     * @param previousResource previous page in list
     * @param currentResource current page in list
     * @param nextResource next page in list
     * @param firstResource first page from list
     */
    private void addNavItems(Resource previousResource,Resource currentResource,Resource nextResource,Resource firstResource) {

//        LOGGER.error("addNavItems: previousResource={},currentResource={},nextResource={},firstResource={}", previousResource,currentResource,nextResource,firstResource);

        if (listItems != null) {
            if (currentResource != null) {
                if (previousResource != null) {
                    Page previousPage = getPageManager().getContainingPage(previousResource);
                    Map<String, Object> item = new HashMap<>();
                    item.put("resource", previousResource);
                    item.put("type", previousResource.getResourceType());
                    item.put("navType", "previous");

                    item.put("page", previousPage);
                    item.putAll(design.aem.models.v2.lists.List.getPageBadgeInfo(previousPage, DEFAULT_LIST_DETAILS_SUFFIX, getResourceResolver(), detailsBadge));

//                    LOGGER.error("addNavItems: previous item={}", item);

                    listItems.add(item);
                }


                if (nextResource != null) {
                    Page nextPage = getPageManager().getContainingPage(nextResource);
                    Map<String, Object> item = new HashMap<>();
                    item.put("resource", nextResource);
                    item.put("type", nextResource.getResourceType());
                    item.put("navType", "next");

                    item.put("page", nextPage);
                    item.putAll(design.aem.models.v2.lists.List.getPageBadgeInfo(nextPage, DEFAULT_LIST_DETAILS_SUFFIX, getResourceResolver(), detailsBadge));

//                    LOGGER.error("addNavItems: item={}", item);

                    listItems.add(item);
                }
            }

            if (currentResource == null || (listLoop && nextResource == null)) {
                if (firstResource != null) {
                    Page firstPage = getPageManager().getContainingPage(firstResource);
                    Map<String, Object> item = new HashMap<>();
                    item.put("resource", firstPage);
                    item.put("type", firstResource.getResourceType());
                    item.put("navType", "first");

                    item.put("page", firstPage);
                    item.putAll(design.aem.models.v2.lists.List.getPageBadgeInfo(firstPage, DEFAULT_LIST_DETAILS_SUFFIX, getResourceResolver(), detailsBadge));

//                    LOGGER.error("addNavItems: item={}", item);

                    listItems.add(item);
                }
            }
        }
    }

    /**
     * process search results.
     * @param result search results
     * @throws RepositoryException
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

//        LOGGER.error("collectSearchResults resultInfo={}", resultInfo);
        componentProperties.put("resultInfo",resultInfo);

        Resource previousResource = null;
        Resource currentResource = null;
        Resource nextResource = null;
        Resource firstResource = null;
        for (Hit hit : result.getHits()) {

//            LOGGER.error("collectSearchResults: previousResource={},currentResource={},nextResource={},firstResource={}",previousResource,currentResource,nextResource,firstResource);
//            LOGGER.error("collectSearchResults: hit={}",hit.getPath());

            if (firstResource == null) {
                firstResource = hit.getResource();
            }

            if (currentResource == null) {
                if (hit.getResource().getPath().equals(filterPage.getPath())) {
                    //found current page
                    currentResource = hit.getResource();

                } else {
                    previousResource = hit.getResource();
                }
            } else {
                nextResource = hit.getResource();

                break;
            }

        }

        addNavItems(previousResource,currentResource,nextResource,firstResource);

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