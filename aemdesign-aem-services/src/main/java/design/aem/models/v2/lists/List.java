package design.aem.models.v2.lists;

import com.day.cq.i18n.I18n;
import com.day.cq.search.*;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.ResultPage;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.TagConstants;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.text.Text;
import design.aem.components.ComponentProperties;
import design.aem.models.ModelProxy;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.vault.util.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestParameter;
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
import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.net.URLDecoder;
import java.util.*;

import static design.aem.utils.components.CommonUtil.*;
import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.ConstantsUtil.*;
import static design.aem.utils.components.I18nUtil.*;
import static design.aem.utils.components.ImagesUtil.getAssetInfo;
import static design.aem.utils.components.ImagesUtil.getResourceImagePath;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class List extends ModelProxy {
    protected static final Logger LOGGER = LoggerFactory.getLogger(List.class);

    private static final int LIMIT_DEFAULT = 100;
    private static final int PAGEMAX_DEFAULT = -1;
    private static final int PN_DEPTH_DEFAULT = 1;
    private static final int LIST_SPLIT_EVERY_DEFAULT = 5;
    private static final String TAGS_MATCH_ANY_VALUE = "any";
    private static final String PN_DATE_FORMAT_DEFAULT = "yyyy-MM-dd";

    private static final String PN_SOURCE = "listFrom"; //SOURCE_PROPERTY_NAME
    private static final String PN_PAGES = "pages"; //PAGES_PROPERTY_NAME
    private static final String PN_PARENT_PAGE = "parentPage"; //PARENT_PAGE_PROPERTY_NAME
    private static final String DESCENDANT_TAG = "ancestorTag";
    private static final String PN_PARENT_PAGE_DEFAULT = "/content";
    private static final String PN_TAGS_PARENT_PAGE = "tagsSearchRoot";
    private static final String PN_TAGS = "tags"; //TAGS_PROPERTY_NAME
    private static final String PN_TAGS_MATCH = "tagsMatch"; //TAGS_MATCH_PROPERTY_NAME
    private static final String LIMIT_PROPERTY_NAME = "limit"; //TAGS_MATCH_PROPERTY_NAME
    private static final String PAGE_MAX_PROPERTY_NAME = "pageMax";
    private static final String PAGE_START_PROPERTY_NAME = "pageStart";
    private static final String ANCESTOR_PAGE_PROPERTY_NAME = "ancestorPage";
    private static final String SEARCH_IN_PROPERTY_NAME = "searchIn";
    private static final String SAVEDQUERY_PROPERTY_NAME = "savedquery";
    private static final String LIST_SPLIT_EVERY = "listSplitEvery";
    private static final String SHOW_HIDDEN = "showHidden";
    private static final String SHOW_INVALID = "showInvalid";
    private static final String DETAILS_BADGE = "detailsBadge";
    private static final String PAGINATION_TYPE = "paginationType";

    private static final String REQUEST_PARAM_MARKER_START = "start";
    private static final String REQUEST_PARAM_MARKER_MAX = "max";
    private static final String REQUEST_PARAM_QUERY = "q";
    private static final String QUERY_ENCODING = "UTF-8";

    public static final String LIST_TAG_ORDERED = "ol";
    public static final String LIST_TAG_UNORDERED = "ul";
    public static final String LIST_TAG = "listTag";
    public static final String LIST_ISPAGINATING = "isPaginating";
    public static final String LIST_ISEMPTY = "isEmpty";

    private static final String PN_SEARCH_IN = "searchIn";
    private static final String PN_SORT_ORDER = "sortOrder";
    private static final String PN_ORDER_BY = "orderBy";
    private static final String PN_ORDER_BY_DEFAULT = "path";


    private static final Boolean DEFAULT_PRINT_STRUCTURE = true;
    private static final String DEFAULT_TITLE_TYPE = "h2";
    private static final String DEFAULT_I18N_CATEGORY = "list";
    private static final String DEFAULT_PAGINATION = "default";

    protected ComponentProperties componentProperties = null;
    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }

    public static final String LISTITEM_LINK_TEXT = "listItemLinkText";
    public static final String LISTITEM_LINK_TITLE = "listItemLinkTitle";

    private static final String FIELD_FEED_ENABLED = "feedEnabled";
    private static final String FIELD_FEED_TYPE = "feedType";
    private static final String FIELD_FEED_EXT = "feedExt";
    private static final String FIELD_FEED_TITLE = "feedTitle";
    private static final String FIELD_FEED_URL = "feedUrl";

    private java.util.List<Map<String,Object>> listItems;

    private String startIn;
    private List.SortOrder sortOrder;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(intValues = LIMIT_DEFAULT)
    private int limit;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = StringUtils.EMPTY)
    private String query;
    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = StringUtils.EMPTY)
    private String savedquery;

    private long pageMax;
    private long totalPages;
    private long pageStart;
	private long totalMatches;
	private long listSplitEvery;
    private String id;
    private boolean isPaginating;
    private java.util.List<ResultPage> resultPages;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = StringUtils.EMPTY)
    private String detailsBadge;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    protected String[] detailsNameSuffix;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(booleanValues = false)
    private boolean showHidden;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(booleanValues = false)
    private boolean showInvalid;

    protected void ready() {
        loadConfig();
    }

	@SuppressWarnings({"squid:S3776"})
    protected void loadConfig() {
        I18n i18n = new I18n(getRequest());
        String resourcePath = getResource().getPath();

        setComponentFields(new Object[][]{
                {LIST_TAG, LIST_TAG_UNORDERED},
                {FIELD_FEED_ENABLED, false},
                {FIELD_FEED_TYPE, "rss"},
                {"listSplit", false, "data-list-split-enabled"},
                {LIST_SPLIT_EVERY, LIST_SPLIT_EVERY_DEFAULT, "data-list-split-every"},
                {DETAILS_BADGE, DEFAULT_BADGE, "data-badge"},
                {"printStructure", DEFAULT_PRINT_STRUCTURE},
                {"topicQueue", StringUtils.EMPTY, "topicqueue"},
                {SHOW_HIDDEN, false},
                {SHOW_INVALID, false},
                {PAGINATION_TYPE, DEFAULT_PAGINATION},
                {LIMIT_PROPERTY_NAME, LIMIT_DEFAULT},
                {PAGE_MAX_PROPERTY_NAME, PAGEMAX_DEFAULT},
                {ANCESTOR_PAGE_PROPERTY_NAME, StringUtils.EMPTY},
                {PN_PARENT_PAGE, getCurrentPage().getPath()},
                {PN_SOURCE, StringUtils.EMPTY},
                {PN_PAGES, new String[]{}},
                {PN_TAGS_PARENT_PAGE, getCurrentPage().getPath()},
                {PN_TAGS, new String[]{}},
                {PN_TAGS_MATCH, TAGS_MATCH_ANY_VALUE},
                {PN_ORDER_BY, StringUtils.EMPTY},
                {PN_SORT_ORDER, SortOrder.ASC.getValue()},
                {PN_SEARCH_IN, getResourcePage().getPath()},
                {SAVEDQUERY_PROPERTY_NAME, StringUtils.EMPTY},
                {SEARCH_IN_PROPERTY_NAME, StringUtils.EMPTY},
                {LISTITEM_LINK_TEXT, getDefaultLabelIfEmpty("",DEFAULT_I18N_CATEGORY,DEFAULT_I18N_LIST_ITEM_LINK_TEXT,DEFAULT_I18N_CATEGORY,i18n)},
                {LISTITEM_LINK_TITLE, getDefaultLabelIfEmpty("",DEFAULT_I18N_CATEGORY,DEFAULT_I18N_LIST_ITEM_LINK_TITLE,DEFAULT_I18N_CATEGORY,i18n)}
        });

        componentProperties = ComponentsUtil.getComponentProperties(
                this,
                componentFields,
                DEFAULT_FIELDS_STYLE,
                DEFAULT_FIELDS_ACCESSIBILITY,
                DEFAULT_FIELDS_ANALYTICS);

        //generate id to be used when reading the query string
        generateId();

        //save tag info
        String[] tags = componentProperties.get(TagConstants.PN_TAGS, new String[]{});
        componentProperties.attr.add("data-search-tags", StringUtils.join(tags,","));

        //collection info for variables
        startIn = componentProperties.get(PN_SEARCH_IN, getResourcePage().getPath());
        sortOrder = SortOrder.fromString(componentProperties.get(PN_SORT_ORDER, SortOrder.ASC.getValue()));
        savedquery = componentProperties.get(SAVEDQUERY_PROPERTY_NAME, "");
        pageMax = componentProperties.get(PAGE_MAX_PROPERTY_NAME, PAGEMAX_DEFAULT);
        listSplitEvery = componentProperties.get(LIST_SPLIT_EVERY, LIST_SPLIT_EVERY_DEFAULT);
        detailsBadge = componentProperties.get(DETAILS_BADGE, DEFAULT_BADGE);
        limit = componentProperties.get(LIMIT_PROPERTY_NAME, LIMIT_DEFAULT);
        showHidden = componentProperties.get(SHOW_HIDDEN, false);
        showInvalid = componentProperties.get(SHOW_INVALID, false);

        //check default details suffix
        if (detailsNameSuffix == null) {
            detailsNameSuffix = DEFAULT_LIST_DETAILS_SUFFIX;
        }

        //check query string for parameters
        String requestPageStart = getParameter(REQUEST_PARAM_MARKER_START);
        if (isNotEmpty(requestPageStart)) {
            pageStart = tryParseLong(requestPageStart, 0);
            componentProperties.put(PAGE_START_PROPERTY_NAME, pageStart);
        }

        String requestPageMax = getParameter(REQUEST_PARAM_MARKER_MAX);
        if (isNotEmpty(requestPageMax)) {
            pageMax = tryParseLong(requestPageMax, pageMax);
            componentProperties.put(PAGE_MAX_PROPERTY_NAME, pageStart);
        }

        if (getRequest().getRequestParameter(REQUEST_PARAM_QUERY) !=null) {
            try {
                RequestParameter requestParameter = getRequest().getRequestParameter(REQUEST_PARAM_QUERY);
                if (requestParameter != null) {
                    query = requestParameter.toString();
                }
            } catch (Exception ex) {
                LOGGER.error("could not read query param {}", ex);
            }
        }


        //setup feed config
        if ((Boolean)componentProperties.get(FIELD_FEED_ENABLED)) {
            if ("atom".equals(componentProperties.get(FIELD_FEED_TYPE))) {
                componentProperties.put(FIELD_FEED_EXT, ".feed");
                componentProperties.put(FIELD_FEED_TITLE, "Atom 1.0 (List)");
                componentProperties.put(FIELD_FEED_TYPE, "application/atom+xml");
            } else if("ics".equals(componentProperties.get(FIELD_FEED_TYPE))) {
                componentProperties.put(FIELD_FEED_EXT, ".ics");
                componentProperties.put(FIELD_FEED_TITLE, "iCalendar Subscription List");
                componentProperties.put(FIELD_FEED_TYPE, "text/calendar");
            } else {
                componentProperties.put(FIELD_FEED_EXT, ".rss");
                componentProperties.put(FIELD_FEED_TITLE, "RSS Feed");
                componentProperties.put(FIELD_FEED_TYPE, "application/rss+xml");
            }
            if (isNotEmpty(componentProperties.get(FIELD_FEED_EXT,""))) {
                componentProperties.put(FIELD_FEED_URL, resourcePath.concat(componentProperties.get(FIELD_FEED_EXT,"")));
            } else {
                componentProperties.put(FIELD_FEED_URL,resourcePath);
            }
        }



        String strItemLimit = componentProperties.get(LIMIT_PROPERTY_NAME, "");
        String strPageItems = componentProperties.get(PAGE_MAX_PROPERTY_NAME, "");

        // no limit set, but pagination enabled, set limit to infinite
        if (StringUtils.isBlank(strItemLimit) && !StringUtils.isBlank(strPageItems)) {
            limit = Integer.MAX_VALUE;
        }

        Object[][] badgeComponentFields = {
                {FIELD_PAGE_TITLE, ""},
                {FIELD_PAGE_TITLE_NAV, ""},
        };

        //prepare request parms to pass to badges
        ComponentProperties badgeRequestAttributes = ComponentsUtil.getComponentProperties(
                this,
                badgeComponentFields,
                DEFAULT_FIELDS_DETAILS_OPTIONS_OVERRIDE);

        badgeRequestAttributes.putAll(getAssetInfo(getResourceResolver(),
                getResourceImagePath(getResource(), DETAILS_THUMBNAIL),
                DETAILS_THUMBNAIL));

        componentProperties.put(BADGE_REQUEST_ATTRIBUTES, badgeRequestAttributes);

        try {
            getRequest().setAttribute(BADGE_REQUEST_ATTRIBUTES, badgeRequestAttributes);
            getRequest().setAttribute(COMPONENT_PROPERTIES, componentProperties);
        } catch (Exception ex) {
            LOGGER.error("could not set request attributes for use in badges");
        }

        String paginationTemplate = String.format("pagination.%s.html", componentProperties.get(PAGINATION_TYPE, DEFAULT_PAGINATION));
        componentProperties.put("paginationTemplate",paginationTemplate);

        componentProperties.put(COMPONENT_ATTRIBUTES, buildAttributesString(componentProperties.attr.getData(), null));

    }

    /**
     * generate component id to use in creating querystring parameters.
     */
    private void generateId() {
        String path = getResource().getPath();
        String rootMarker = JcrConstants.JCR_CONTENT.concat(PATH_SEPARATOR);
        int root = path.indexOf(rootMarker);
        if (root >= 0) {
            path = path.substring(root + rootMarker.length());
        }

        id = path.replace(PATH_SEPARATOR, PATH_UNDERSCORE);
    }

    /**
     * get request parameter with component id prefix.
     * @param name name of querystring param suffix
     * @return
     */
    private String getParameter(String name) {
        return getRequest().getParameter(id + PATH_UNDERSCORE + name);
    }

    /**
     * get next page url.
     * @return
     */
    private String getNextPageLink() {
        long nextPageStart = pageStart + pageMax;
        if (isPaginating && pageMax > 0 && resultPages.size() > 0 && nextPageStart < totalMatches) {
            List.PageLink link = new List.PageLink(getRequest());
            link.setParameter(REQUEST_PARAM_MARKER_START, nextPageStart);
            return link.toString();
        } else {
            return "";
        }
    }

    /**
     * get previous page url.
     * @return
     */
    private String getPreviousPageLink() {
        if (isPaginating && pageMax > 0 && resultPages.size() > 0 && pageStart != 0) {
            long previousPageStart = pageStart > pageMax ? pageStart - pageMax : 0;
            List.PageLink link = new List.PageLink(getRequest());
            link.setParameter(REQUEST_PARAM_MARKER_START, previousPageStart);
            return link.toString();
        } else {
            return "";
        }
    }

    /**
     * get page badge info from a page.
     * @param page
     * @return
     */
    private Map<String,Object> getPageBadgeInfo(Page page) {
        return getPageBadgeInfo(page,detailsNameSuffix,getResourceResolver(),detailsBadge);
    }

    /**
     * return page badge info.
     * @param page page to use for collection
     * @param detailsNameSuffix details siffix to look for
     * @param resourceResolver resource resolver to use
     * @param detailsBadge badge selectors to add
     * @return
     */
    static Map<String,Object> getPageBadgeInfo(Page page,String[] detailsNameSuffix, ResourceResolver resourceResolver, String detailsBadge) {

        Map<String,Object> badge = new HashMap<>();

        try {

            String componentPath = findComponentInPage(page, detailsNameSuffix);
            if (isNotEmpty(componentPath)) {
                Resource componentResource = resourceResolver.getResource(componentPath);
                if (componentResource != null) {
                    String componentResourceType = componentResource.getResourceType();
                    if (isNull(componentResourceType)) {
                        componentResourceType = "";
                    }
                    badge.put("componentResourceType", componentResourceType);
                }

                badge.put("componentPath", componentPath);

                badge.put("componentPathSelectors", new String[]{DETAILS_SELECTOR_BADGE, detailsBadge});

                String pageRedirect = getPageRedirect(page);

                if (isEmpty(pageRedirect)) {
                    badge.put("redirectLink", pageRedirect);
                }

            } else {
                LOGGER.warn("getPageBadgeInfo: page {} does not have component with resource type suffix {}", page.getPath(), detailsNameSuffix);
                badge.put("componentMissing", true);
            }


            badge.put("pagePath", page.getPath());

        } catch (Exception ex) {
            LOGGER.error("getPageBadgeInfo: could not get page info {}",ex);
        }
        return badge;

    }

    /**
     * get list options type.
     * @return selected list type
     */
    protected Source getListType() {
        String listFromValue = componentProperties.get(PN_SOURCE, getCurrentStyle().get(PN_SOURCE, StringUtils.EMPTY));
        return Source.fromString(listFromValue);
    }

    /**
     * get list items, used by HTL templates.
     * @return collection of list types
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
    protected void populateListItems(Source listType) {
        switch (listType) {
            case STATIC: //SOURCE_STATIC
                populateStaticListItems();
                break;
            case CHILDREN: //SOURCE_CHILDREN
                populateChildListItems();
                break;
            case TAGS: //SOURCE_TAGS
                populateTagListItems();
                break;
            case SEARCH: //SOURCE_SEARCH
                populateSearchListItems();
                break;
            case QUERYBUILDER: //SOURCE_QUERYBUILDER
                populateQueryListItems();
                break;
            case DESCENDANTS: //SOURCE_DESCENDANTS
                populateDescendantsListItems();
                break;
            default:
                listItems = new ArrayList<>();
                break;
        }
        //sortListItems();

        componentProperties.put(LIST_ISEMPTY, totalMatches == 0);

        updateIsPaginating();

        updateListSplit();

        componentProperties.put("nextPageLink",getNextPageLink());
        componentProperties.put("previousPageLink",getPreviousPageLink());

//        setMaxItems();
    }

    /**
     * set pagination helper attributes.
     */
    private void updateIsPaginating() {

        //isPaginating = listItems.size() > 0 && listItems.size() > pageMax;

        //componentProperties.put(LIST_ISPAGINATING, isPaginating);

        componentProperties.attr.add("data-has-pages", isPaginating);


        if (isPaginating) {

            componentProperties.attr.add("data-total-pages", String.valueOf(totalPages));
            componentProperties.attr.add("data-content-url", getResource().getPath().concat(DEFAULT_EXTENTION));
            componentProperties.attr.add("data-content-start", id.concat("_start"));

        }

    }


    /**
     * calculate splits in list items
     */
    private void updateListSplit() {
        //parse list and set items that should force a split in a list
        for (int i=0; i < listItems.size();i++) {
            if ((i + 1) % listSplitEvery == 0) {
                listItems.get(i).put("split",true);
            }
        }
    }

    /**
     * determine if the page should be shown in output.
     * @param page page to check
     * @param includeInvalid include if page is invalid
     * @param includeHidden include in page is hidden
     * @return
     */
    static boolean includePageInList(Page page, boolean includeInvalid, boolean includeHidden) {
        return (includeHidden || !page.isHideInNav()) && (includeInvalid || page.isValid()) && page.getDeleted() == null;
    }

    /**
     * populates listItems with resources from pages list.
     * page object is also resolved and returned if available
     */
    @SuppressWarnings("Duplicates")
    private void populateStaticListItems() {
        listItems = new ArrayList<>();
        String[] resourcePaths = componentProperties.get(PN_PAGES, new String[0]);
        ResourceResolver resourceResolver = getResourceResolver();
        for (String path : resourcePaths) {
            Map<String,Object> item = new HashMap<>();

            Resource pathResource = resourceResolver.resolve(path);

            if (!ResourceUtil.isNonExistingResource(pathResource)) {
                item.put("type", pathResource.getResourceType());
                item.put("resource", pathResource);
            } else {
                LOGGER.error("populateStaticListItems: skipping item as it does not exist {}", path);
                continue;
            }

            Page page = getPageManager().getContainingPage(path);
            if (page != null) {
                if (includePageInList(page,showInvalid,showHidden)) {
                    item.put("page", page);
                    item.putAll(getPageBadgeInfo(page));
                }
            }
            listItems.add(item);
        }
    }

    /**
     * populate list items from only children of a root page.
     */
    private void populateChildListItems() {
        String path = componentProperties.get(PN_PARENT_PAGE,PN_PARENT_PAGE_DEFAULT);
        populateChildListItems(path,true);
    }

    /**
     * populate list items from descendants of a root page.
     */
    private void populateDescendantsListItems() {
        String path = componentProperties.get(DESCENDANT_TAG,PN_PARENT_PAGE_DEFAULT);
        populateChildListItems(path,false);
    }

    /**
     * populate list items from children of a root page.
     * @param flat only select children on root page
     */
    private void populateChildListItems(String path, Boolean flat) {
        listItems = new ArrayList<>();

        Map<String, String> childMap = new HashMap<>();
        Page rootPage = getPageManager().getPage(path);
        childMap.put("path", rootPage.getPath());
        if (flat) {
            childMap.put("path.flat", "true");
        } else {
            childMap.put("path.flat", "false");
        }
        childMap.put("type", NameConstants.NT_PAGE);

        populateListItemsFromMap(childMap);
    }

    /**
     * populate listitem from tag list type
     */
    private void populateTagListItems() {
        listItems = new ArrayList<>();
        String[] tags = componentProperties.get(PN_TAGS, new String[0]);
        boolean matchAny = componentProperties.get(PN_TAGS_MATCH, TAGS_MATCH_ANY_VALUE).equals(TAGS_MATCH_ANY_VALUE);

        if (ArrayUtils.isNotEmpty(tags)) {
            Page rootPage = getPageManager().getPage(componentProperties.get(PN_TAGS_PARENT_PAGE,""));

            if (rootPage != null) {
                Map<String, String> childMap = new HashMap<>();
                childMap.put("path", rootPage.getPath());

                String operator = matchAny ? "or" : "and";

                childMap.put("group.p." + operator, "true");
                childMap.put("group.0_group.p." + operator, "true");

                int offset = 0;

                for (String tag : tags) {
                    childMap.put("group.0_group." + offset + "_group.tagid", tag);
                    childMap.put("group.0_group." + offset + "_group.tagid.property", JcrConstants.JCR_CONTENT.concat("/cq:tags"));

                    // Offset the Page Details group by one so we don't conflict with the page properties query
                    offset++;

                    childMap.put("group.0_group." + offset + "_group.tagid", tag);
                    childMap.put("group.0_group." + offset + "_group.tagid.property", JcrConstants.JCR_CONTENT.concat("/article/par/page_details/cq:tags"));
                }

                populateListItemsFromMap(childMap);
            }
        }
    }

    /**
     * populate listitems form search list type.
     */
    private void populateSearchListItems() {
        listItems = new ArrayList<>();
        if (!StringUtils.isBlank(query)) {
            SimpleSearch search = getResource().adaptTo(SimpleSearch.class);
            if (search != null) {
                search.setQuery(query);
                search.setSearchIn(startIn);
//                search.addPredicate(new Predicate("type", "type").set("type", NameConstants.NT_PAGE));
                //TODO: add limits and pages
//                search.setStart(pageStart);
//                search.addPredicate(new Predicate("p.guessTotal", "true")); //guess amount
                if (limit > 0) {
                    search.setHitsPerPage(pageMax);
                }
                try {
                    collectSearchResults(search.getResult());
                } catch (RepositoryException e) {
                    LOGGER.error("Unable to retrieve search results for query.", e);
                }
            }
        }
    }


    /**
     * get predicate group from query string.
     * @param request reques instance
     * @return predicates converted from query string
     */
    public static PredicateGroup getPredicateGroupFromRequest(SlingHttpServletRequest request) {

        String queryParam = "";

        try {
            queryParam = request.getParameter("query");

            // check if we have to convert from the url format to the properties-style format
            String isURLQuery = request.getParameter("isURL");
            if (queryParam != null && "on".equals(isURLQuery)) {
                queryParam = Text.unescape(queryParam.replaceAll("&", "\n"));
            }
        } catch (Exception ex) {
            LOGGER.error("getPredicateGroupFromQuery: could not read query param q=[{}], ex={}", queryParam, ex);
            return null;
        }

        return getPredicateGroupFromQuery(queryParam);
    }

    /**
     * get predicate group config from querystring param.
     * @param queryParam query string param, same as querybuilder
     * @return predicates converted from query string
     */
    public static PredicateGroup getPredicateGroupFromQuery(String queryParam) {

        try {

            Properties props = new Properties();
            props.load(new StringReader(queryParam));
            return PredicateConverter.createPredicates(props);
        } catch (Exception ex) {
            LOGGER.error("getPredicateGroupFromQuery: could not create PredicateGroupFromQuery from query param q=[{}], ex={}", queryParam, ex);
        }

        return null;
    }

    /**
     * do a search based on querystring params.
     * @param queryParam querystring param same as querybuilder
     */
    private void populateListItemsFromQuery(String queryParam) {
        try {

            QueryBuilder builder = getResourceResolver().adaptTo(QueryBuilder.class);
            if (builder != null) {
                Session session = getResourceResolver().adaptTo(Session.class);

                Query query = null;

                PredicateGroup root = getPredicateGroupFromQuery(queryParam);
                // avoid slow //* queries
                if (root != null && !root.isEmpty()) {
                    query = builder.createQuery(root, session);
                }

                if (query != null) {
                    collectSearchResults(query.getResult());
                }
            } else {
                LOGGER.error("populateListItemsFromMap: could not get query builder object, q={}",queryParam);
            }
        } catch (Exception ex) {
            LOGGER.error("populateListItemsFromQuery: could not execute query q=[{}], ex={}",queryParam,ex);
        }
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

                //pagination limit is set
                if (pageMax > 0) {
                    map.put("p.limit", String.valueOf(pageMax));
                } else if (limit > 0) {
                    //limit is set
                    map.put("p.limit", String.valueOf(limit));
                }

                if (pageStart > 0) {
                    map.put("p.offset", String.valueOf(pageStart));
                }

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
     * allow passing of querybuilder queries.
     */
	@SuppressWarnings({"squid:S3776"})
    private void populateQueryListItems() {
        listItems = new ArrayList<>();
        if (!StringUtils.isBlank(savedquery)) {


            try {
                if (getRequest().getRequestParameter(REQUEST_PARAM_QUERY) != null) {
                    //if query passed read and process
                    String escapedQuery = "";
                    RequestParameter requestParameter = getRequest().getRequestParameter(REQUEST_PARAM_QUERY);
                    if (requestParameter != null) {
                        escapedQuery = requestParameter.toString();
                    }
                    String unescapedQuery = URLDecoder.decode(escapedQuery, QUERY_ENCODING);
                    QueryBuilder queryBuilder = getResourceResolver().adaptTo(QueryBuilder.class);
                    if (queryBuilder != null) {
                        PageManager pm = getResourceResolver().adaptTo(PageManager.class);
                        //create props for query
                        java.util.Properties props = new java.util.Properties();
                        //load query candidate
                        props.load(new ByteArrayInputStream(unescapedQuery.getBytes()));
                        //create predicate from query candidate
                        PredicateGroup predicateGroup = PredicateConverter.createPredicates(props);
                        //TODO: add limits and pages
//                    predicateGroup.add(new Predicate("p.offset","0"));
//                    if (limit > 0) {
//                        predicateGroup.add(new Predicate("p.limit", Integer.toString(limit)));
//                    }
//                    predicateGroup.add(new Predicate("p.guessTotal","true"));
//                    boolean allowDuplicates = componentProperties.get("allowDuplicates", false);
                        javax.jcr.Session jcrSession = getResourceResolver().adaptTo(javax.jcr.Session.class);
                        if (jcrSession != null) {
                            Query query = queryBuilder.createQuery(predicateGroup, jcrSession);
                            //TODO: add limits and pages


//                    query.setStart(0);
//                    query.setHitsPerPage(20);
                            if (query != null) {
//                        SearchResult result = query.getResult();
//                        HitBasedPageIterator newList = new HitBasedPageIterator(pm, result.getHits().iterator(), !allowDuplicates, new PageFilter(false, showHidden));

                                collectSearchResults(query.getResult());
                            }
                        } else {
                            LOGGER.error("populateQueryListItems: could not get sessions object");
                        }
                    } else {
                        LOGGER.error("populateQueryListItems: could not get query builder object");
                    }
                } else {
                    //if not passed read saved query
                    Session session = getResourceResolver().adaptTo(Session.class);
                    QueryBuilder queryBuilder = getResourceResolver().adaptTo(QueryBuilder.class);
                    if (session != null && queryBuilder != null) {
                        try {
                            Query query = queryBuilder.loadQuery(getResource().getPath() + "/" + SAVEDQUERY_PROPERTY_NAME, session);
                            if (query != null) {
                                collectSearchResults(query.getResult());
                            }
                        } catch (Exception ex) {
                            LOGGER.error("error loading stored querybuilder query from {},{}", getResource().getPath(), ex);
                        }
                    }

                }
            } catch (Exception ex) {
                LOGGER.error("LIST: could not do query {}", ex);
            }
        }
    }

    /**
     * process search results.
      * @param result search results
     * @throws RepositoryException when can't read content
     */
    @SuppressWarnings("Duplicates")
    private void collectSearchResults(SearchResult result) throws RepositoryException {
        Map<String, Object> resultInfo = new HashMap<>();
        resultInfo.put("executionTime",result.getExecutionTime());
        resultInfo.put("startIndex",result.getStartIndex());
        resultInfo.put("hasMore",result.hasMore());
        resultInfo.put("result",result);

        totalMatches = result.getTotalMatches();
        resultPages = result.getResultPages();
		long hitsPerPage = result.getHitsPerPage();
        totalPages = result.getResultPages().size();
        pageStart = result.getStartIndex();
		long currentPage = (pageStart / hitsPerPage) + 1;

        resultInfo.put("hitsPerPage", hitsPerPage);
        resultInfo.put("currentPage", currentPage);
        resultInfo.put("totalMatches",totalMatches);
        resultInfo.put("resultPages",resultPages);
        resultInfo.put("totalPages",totalPages);
        resultInfo.put(PAGE_START_PROPERTY_NAME,pageStart);

        isPaginating = (pageMax > 0 && result.getResultPages().size() > 0);
        componentProperties.put(LIST_ISPAGINATING, isPaginating);

        componentProperties.put("resultInfo",resultInfo);

        for (Hit hit : result.getHits()) {
            Map<String,Object> item = new HashMap<>();
            item.put("hit", hit);
            item.put("resource", hit.getResource());
            item.put("type", hit.getResource().getResourceType());

            Page containingPage = getPageManager().getContainingPage(hit.getResource());
            if (containingPage != null) {
                if (includePageInList(containingPage,showInvalid,showHidden)) {
                    item.put("page", containingPage);
                    item.putAll(getPageBadgeInfo(containingPage));
                }
            }

            listItems.add(item);
        }
    }


    protected enum Source {
        CHILDREN("children"),
        STATIC("static"),
        SEARCH("search"),
        TAGS("tags"),
        QUERYBUILDER("querybuilder"),
        DESCENDANTS("descendants"),
        EMPTY(StringUtils.EMPTY);

        private String value;

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


    public enum SortOrder {
        ASC("asc"),
        DESC("desc");

        private String value;

        public String getValue() {
            return value;
        }

        SortOrder(String value) {
            this.value = value;
        }

        public static SortOrder fromString(String value) {
            for (SortOrder s : values()) {
                if (StringUtils.equals(value, s.value)) {
                    return s;
                }
            }
            return ASC;
        }
    }

    /**
     * helper for generating next and previous links for a list.
     */
    private class PageLink {
        private String path;
        private HashMap<String, Object> params;

        public PageLink(SlingHttpServletRequest request) {
            this.path = request.getPathInfo();
            PageManager pm = request.getResourceResolver().adaptTo(PageManager.class);
            if (pm != null) {
                Page page = pm.getContainingPage(this.path);
                if (page != null) {

                    this.path = page.getPath() + ".html";
                }
            } else {
                LOGGER.error("PageLink: could not get PageManager object");
            }
            this.initParams(request.getQueryString());
        }

        public void addParameter(String name, Object value) {
            name = this.prefixName(name);
            this.params.put(name, value);
        }

        public void setParameter(String name, Object value) {
            name = this.prefixName(name);
            this.params.remove(name);

            this.addParameter(name, value);
        }

        public String toString() {
            String url = this.path;

            String param;
            for(Iterator i$ = this.params.keySet().iterator(); i$.hasNext(); url = this.appendParam(url, param, this.params.get(param))) {
                param = (String)i$.next();
            }

            return url;
        }

        private String prefixName(String name) {
            if (!name.startsWith(List.this.id + "_")) {
                name = List.this.id + "_" + name;
            }

            return name;
        }

        private void initParams(String query) {
            this.params = new HashMap();
            String[] pairs = Text.explode(query, 38);
            String[] arr$ = pairs;
            int len$ = pairs.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                String pair = arr$[i$];
                String[] param = Text.explode(pair, 61, true);
                this.params.put(param[0], param[1]);
            }

        }

        private String appendParam(String url, String name, Object value) {
            char delim = url.indexOf(63) > 0 ? (char)38 : (char)63;
            return url + delim + name + '=' + value;
        }
    }

}
