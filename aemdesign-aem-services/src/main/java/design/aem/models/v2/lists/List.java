package design.aem.models.v2.lists;

import com.day.cq.replication.ReplicationStatus;
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
import design.aem.models.BaseComponent;
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

public class List extends BaseComponent {
    protected static final Logger LOGGER = LoggerFactory.getLogger(List.class);

    protected static final int LIMIT_DEFAULT = 100;
    protected static final int PAGEMAX_DEFAULT = -1;
    protected static final int LIST_SPLIT_EVERY_DEFAULT = 5;
    protected static final String TAGS_MATCH_ANY_VALUE = "any";

    protected static final String PN_SOURCE = "listFrom"; //SOURCE_PROPERTY_NAME
    protected static final String PN_PAGES = "pages"; //PAGES_PROPERTY_NAME
    protected static final String PN_PARENT_PAGE = "parentPage"; //PARENT_PAGE_PROPERTY_NAME
    protected static final String PN_PARENT_PAGE_DEFAULT = "/content";
    protected static final String PN_TAGS_PARENT_PAGE = "tagsSearchRoot";
    protected static final String PN_TAGS = "tags"; //TAGS_PROPERTY_NAME
    protected static final String PN_TAGS_MATCH = "tagsMatch"; //TAGS_MATCH_PROPERTY_NAME
    protected static final String LIMIT_PROPERTY_NAME = "limit"; //TAGS_MATCH_PROPERTY_NAME
    protected static final String PAGE_MAX_PROPERTY_NAME = "pageMax";
    protected static final String PAGE_START_PROPERTY_NAME = "pageStart";
    protected static final String ANCESTOR_PAGE_PROPERTY_NAME = "ancestorPage";
    protected static final String SEARCH_IN_PROPERTY_NAME = "searchIn";
    protected static final String SAVEDQUERY_PROPERTY_NAME = "savedquery";
    protected static final String LIST_SPLIT_EVERY = "listSplitEvery";
    protected static final String SHOW_HIDDEN = "showHidden";
    protected static final String SHOW_INVALID = "showInvalid";
    protected static final String DETAILS_BADGE = "detailsBadge";
    protected static final String PAGINATION_TYPE = "paginationType";

    protected static final String REQUEST_PARAM_MARKER_START = "start";
    protected static final String REQUEST_PARAM_MARKER_MAX = "max";
    protected static final String REQUEST_PARAM_QUERY = "q";
    protected static final String QUERY_ENCODING = "UTF-8";

    public static final String LIST_TAG_ORDERED = "ol";
    public static final String LIST_TAG_UNORDERED = "ul";
    public static final String LIST_TAG = "listTag";
    public static final String LIST_ISPAGINATING = "isPaginating";
    public static final String LIST_ISEMPTY = "isEmpty";

    protected static final String PN_SEARCH_IN = "searchIn";
    protected static final String PN_QUERY = "query";
    protected static final String PN_SORT_ORDER = "sortOrder";
    protected static final String PN_ORDER_BY = "orderBy";
    protected static final String PN_ORDER_BY_DEFAULT = "path";

    protected static final Boolean DEFAULT_PRINT_STRUCTURE = true;
    protected static final String DEFAULT_I18N_CATEGORY = "list";
    protected static final String DEFAULT_PAGINATION = "default";

    public static final String LISTITEM_LINK_TEXT = "listItemLinkText";
    public static final String LISTITEM_LINK_TITLE = "listItemLinkTitle";

    protected static final String FIELD_FEED_ENABLED = "feedEnabled";
    protected static final String FIELD_FEED_TYPE = "feedType";
    protected static final String FIELD_FEED_EXT = "feedExt";
    protected static final String FIELD_FEED_TITLE = "feedTitle";
    protected static final String FIELD_FEED_URL = "feedUrl";

    protected java.util.List<Map<String, Object>> listItems;
    protected String startIn;
    protected List.SortOrder sortOrder;
    protected long pageMax;
    protected long totalPages;
    protected long pageStart;
    protected long totalMatches;
    protected long listSplitEvery;
    protected String id;
    protected boolean isPaginating;
    protected java.util.List<ResultPage> resultPages;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(intValues = LIMIT_DEFAULT)
    protected int limit;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = StringUtils.EMPTY)
    protected String query;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = StringUtils.EMPTY)
    protected String savedquery;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = StringUtils.EMPTY)
    protected String detailsBadge;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    protected String[] detailsNameSuffix;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(booleanValues = false)
    protected boolean showHidden;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(booleanValues = false)
    protected boolean showInvalid;

    @SuppressWarnings({"squid:S3776"})
    protected void ready() {
        componentProperties = ComponentsUtil.getComponentProperties(
            this,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY,
            DEFAULT_FIELDS_ANALYTICS);

        generateId();

        String resourcePath = getResource().getPath();

        //save tag info
        String[] tags = componentProperties.get(TagConstants.PN_TAGS, new String[]{});
        componentProperties.attr.add("data-search-tags", StringUtils.join(tags, ","));

        //collection info for variables
        startIn = componentProperties.get(PN_SEARCH_IN, getResourcePage().getPath());
        sortOrder = SortOrder.fromString(componentProperties.get(PN_SORT_ORDER, SortOrder.ASC.getValue()));
        savedquery = componentProperties.get(SAVEDQUERY_PROPERTY_NAME, StringUtils.EMPTY);
        pageMax = componentProperties.get(PAGE_MAX_PROPERTY_NAME, PAGEMAX_DEFAULT);
        listSplitEvery = componentProperties.get(LIST_SPLIT_EVERY, LIST_SPLIT_EVERY_DEFAULT);
        detailsBadge = componentProperties.get(DETAILS_BADGE, DEFAULT_BADGE);
        limit = componentProperties.get(LIMIT_PROPERTY_NAME, LIMIT_DEFAULT);
        showHidden = componentProperties.get(SHOW_HIDDEN, false);
        showInvalid = componentProperties.get(SHOW_INVALID, false);
        query = componentProperties.get(PN_QUERY, StringUtils.EMPTY);

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

        if (getRequest().getRequestParameter(REQUEST_PARAM_QUERY) != null) {
            try {
                RequestParameter requestParameter = getRequest().getRequestParameter(REQUEST_PARAM_QUERY);

                if (requestParameter != null) {
                    query = requestParameter.toString();
                }
            } catch (Exception ex) {
                LOGGER.error("could not read query param {}", ex);
            }
        }

        if ((Boolean) componentProperties.get(FIELD_FEED_ENABLED)) {
            if ("atom".equals(componentProperties.get(FIELD_FEED_TYPE))) {
                componentProperties.put(FIELD_FEED_EXT, ".feed");
                componentProperties.put(FIELD_FEED_TITLE, "Atom 1.0 (List)");
                componentProperties.put(FIELD_FEED_TYPE, "application/atom+xml");
            } else if ("ics".equals(componentProperties.get(FIELD_FEED_TYPE))) {
                componentProperties.put(FIELD_FEED_EXT, ".ics");
                componentProperties.put(FIELD_FEED_TITLE, "iCalendar Subscription List");
                componentProperties.put(FIELD_FEED_TYPE, "text/calendar");
            } else {
                componentProperties.put(FIELD_FEED_EXT, ".rss");
                componentProperties.put(FIELD_FEED_TITLE, "RSS Feed");
                componentProperties.put(FIELD_FEED_TYPE, "application/rss+xml");
            }

            if (isNotEmpty(componentProperties.get(FIELD_FEED_EXT, StringUtils.EMPTY))) {
                componentProperties.put(FIELD_FEED_URL, resourcePath.concat(componentProperties.get(FIELD_FEED_EXT, StringUtils.EMPTY)));
            } else {
                componentProperties.put(FIELD_FEED_URL, resourcePath);
            }
        }

        String strItemLimit = componentProperties.get(LIMIT_PROPERTY_NAME, StringUtils.EMPTY);
        String strPageItems = componentProperties.get(PAGE_MAX_PROPERTY_NAME, StringUtils.EMPTY);

        // no limit set, but pagination enabled, set limit to infinite
        if (StringUtils.isBlank(strItemLimit) && !StringUtils.isBlank(strPageItems)) {
            limit = Integer.MAX_VALUE;
        }

        Object[][] badgeComponentFields = {
            {FIELD_PAGE_TITLE, StringUtils.EMPTY},
            {FIELD_PAGE_TITLE_NAV, StringUtils.EMPTY},
        };

        //prepare request parms to pass to badges
        ComponentProperties badgeRequestAttributes = ComponentsUtil.getComponentProperties(
            this,
            false,
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
        componentProperties.put("paginationTemplate", paginationTemplate);

        componentProperties.put(COMPONENT_ATTRIBUTES, buildAttributesString(componentProperties.attr.getData(), null));
    }

    @Override
    protected void setFields() {
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
            {PN_QUERY, StringUtils.EMPTY},
            {PN_SORT_ORDER, SortOrder.ASC.getValue()},
            {PN_SEARCH_IN, getResourcePage().getPath()},
            {SAVEDQUERY_PROPERTY_NAME, StringUtils.EMPTY},
            {SEARCH_IN_PROPERTY_NAME, StringUtils.EMPTY},
            {LISTITEM_LINK_TEXT, componentDefaults.get(LISTITEM_LINK_TEXT)},
            {LISTITEM_LINK_TITLE, componentDefaults.get(LISTITEM_LINK_TITLE)},
        });
    }

    @Override
    protected void setFieldDefaults() {
        componentDefaults.put(LISTITEM_LINK_TEXT, getDefaultLabelIfEmpty(
            StringUtils.EMPTY,
            getComponentCategory(),
            DEFAULT_I18N_LIST_ITEM_LINK_TEXT,
            getComponentCategory(),
            i18n));

        componentDefaults.put(LISTITEM_LINK_TITLE, getDefaultLabelIfEmpty(
            StringUtils.EMPTY,
            getComponentCategory(),
            DEFAULT_I18N_LIST_ITEM_LINK_TITLE,
            getComponentCategory(),
            i18n));
    }

    protected String getComponentCategory() {
        return DEFAULT_I18N_CATEGORY;
    }

    /**
     * generate component id to use in creating querystring parameters.
     */
    protected void generateId() {
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
     *
     * @param name name of querystring param suffix
     * @return parameter value
     */
    protected String getParameter(String name) {
        return getRequest().getParameter(id + PATH_UNDERSCORE + name);
    }

    /**
     * get next page url.
     *
     * @return next page url
     */
    protected String getNextPageLink() {
        long nextPageStart = pageStart + pageMax;

        if (isPaginating && pageMax > 0 && resultPages.size() > 0 && nextPageStart < totalMatches) {
            List.PageLink link = new List.PageLink(getRequest());
            link.setParameter(REQUEST_PARAM_MARKER_START, nextPageStart);

            return link.toString();
        } else {
            return StringUtils.EMPTY;
        }
    }

    /**
     * get previous page url.
     *
     * @return previous page url
     */
    protected String getPreviousPageLink() {
        if (isPaginating && pageMax > 0 && resultPages.size() > 0 && pageStart != 0) {
            long previousPageStart = pageStart > pageMax ? pageStart - pageMax : 0;
            List.PageLink link = new List.PageLink(getRequest());
            link.setParameter(REQUEST_PARAM_MARKER_START, previousPageStart);

            return link.toString();
        }

        return StringUtils.EMPTY;
    }

    /**
     * get page badge info from a page.
     *
     * @param page page to use
     * @return map of badge attributes
     */
    protected Map<String, Object> getPageBadgeInfo(Page page) {
        return getPageBadgeInfo(page, detailsNameSuffix, getResourceResolver(), detailsBadge);
    }

    /**
     * return page badge info.
     *
     * @param page              page to use for collection
     * @param detailsNameSuffix details siffix to look for
     * @param resourceResolver  resource resolver to use
     * @param detailsBadge      badge selectors to add
     * @return map of page badge attributes
     */
    public static Map<String, Object> getPageBadgeInfo(Page page, String[] detailsNameSuffix, ResourceResolver resourceResolver, String detailsBadge) {
        Map<String, Object> badge = new HashMap<>();

        try {
            String componentPath = findComponentInPage(page, detailsNameSuffix);

            if (isNotEmpty(componentPath)) {
                Resource componentResource = resourceResolver.getResource(componentPath);

                if (componentResource != null) {
                    String componentResourceType = componentResource.getResourceType();

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
            LOGGER.error("getPageBadgeInfo: could not get page info {}", ex);
        }

        return badge;
    }

    /**
     * get list options type.
     *
     * @return selected list type
     */
    protected Source getListType() {
        String listFromValue = componentProperties.get(PN_SOURCE,
            getCurrentStyle().get(PN_SOURCE, StringUtils.EMPTY));

        return Source.fromString(listFromValue);
    }

    /**
     * get list items, used by HTL templates.
     *
     * @return collection of list types
     */
    public Collection<Map<String, Object>> getListItems() {
        if (listItems == null) {
            Source listType = getListType();
            populateListItems(listType);
        }

        return listItems;
    }

    /**
     * populate list items.
     *
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

        componentProperties.put(LIST_ISEMPTY, totalMatches == 0);

        updateIsPaginating();

        updateListSplit();

        componentProperties.put("nextPageLink", getNextPageLink());
        componentProperties.put("previousPageLink", getPreviousPageLink());
    }

    /**
     * set pagination helper attributes.
     */
    protected void updateIsPaginating() {
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
    protected void updateListSplit() {
        for (int i = 0; i < listItems.size(); i++) {
            if ((i + 1) % listSplitEvery == 0) {
                listItems.get(i).put("split", true);
            }
        }
    }

    /**
     * determine if the page should be shown in output.
     *
     * @param page           page to check
     * @param includeInvalid include if page is invalid
     * @param includeHidden  include in page is hidden
     * @return boolean if page should be included in the list, excludes hidden, invalid, deleted and deactivated pages.
     */
    public static boolean includePageInList(Page page, boolean includeInvalid, boolean includeHidden) {
        if (page != null && page.hasContent()) {
            boolean pageIsDeactivated = false;
            Resource pageContent = page.getContentResource();

            if (pageContent != null) {
                ReplicationStatus replicationStatus = pageContent.adaptTo(ReplicationStatus.class);

                if (replicationStatus != null) {
                    pageIsDeactivated = replicationStatus.isDeactivated();
                }
            }

            return (includeHidden || !page.isHideInNav())
                && (includeInvalid || page.isValid())
                && !pageIsDeactivated
                && page.getDeleted() == null;
        }

        return false;
    }

    /**
     * populates listItems with resources from pages list.
     * page object is also resolved and returned if available
     */
    @SuppressWarnings("Duplicates")
    protected void populateStaticListItems() {
        listItems = new ArrayList<>();

        String[] resourcePaths = componentProperties.get(PN_PAGES, new String[0]);
        ResourceResolver resourceResolver = getResourceResolver();

        for (String path : resourcePaths) {
            Map<String, Object> item = new HashMap<>();

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
                if (includePageInList(page, showInvalid, showHidden)) {
                    item.put("page", page);
                    item.putAll(getPageBadgeInfo(page));
                    listItems.add(item);
                }
            }
        }
    }

    /**
     * populate list items from only children of a root page.
     */
    protected void populateChildListItems() {
        String path = componentProperties.get(PN_PARENT_PAGE, PN_PARENT_PAGE_DEFAULT);

        populateChildListItems(path, true);
    }

    /**
     * populate list items from descendants of a root page.
     */
    protected void populateDescendantsListItems() {
        String path = componentProperties.get(ANCESTOR_PAGE_PROPERTY_NAME, PN_PARENT_PAGE_DEFAULT);

        populateChildListItems(path, false);
    }

    /**
     * populate list items from children of a root page.
     *
     * @param path path to use
     * @param flat only select children on root page
     */
    protected void populateChildListItems(String path, Boolean flat) {
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
    protected void populateTagListItems() {
        listItems = new ArrayList<>();

        String[] tags = componentProperties.get(PN_TAGS, new String[0]);
        boolean matchAny = componentProperties.get(PN_TAGS_MATCH, TAGS_MATCH_ANY_VALUE).equals(TAGS_MATCH_ANY_VALUE);

        if (ArrayUtils.isNotEmpty(tags)) {
            Page rootPage = getPageManager().getPage(componentProperties.get(PN_TAGS_PARENT_PAGE, StringUtils.EMPTY));

            if (rootPage != null) {
                Map<String, String> childMap = new HashMap<>();
                childMap.put("path", rootPage.getPath());

                String operator = matchAny ? "or" : "and";

                childMap.put("group.p." + operator, "true");

                String groupPrefix = "group.0_group.";
                String tagIdSuffix = "_group.tagid";

                childMap.put(groupPrefix + "p." + operator, "true");

                int offset = 0;

                for (String tag : tags) {
                    childMap.put(groupPrefix + offset + tagIdSuffix, tag);
                    childMap.put(groupPrefix + offset + tagIdSuffix + ".property", JcrConstants.JCR_CONTENT.concat("/cq:tags"));

                    // Offset the Page Details group by one so we don't conflict with the page properties query
                    offset++;

                    childMap.put(groupPrefix + offset + tagIdSuffix, tag);
                    childMap.put(groupPrefix + offset + tagIdSuffix + "property", JcrConstants.JCR_CONTENT.concat("/article/par/page_details/cq:tags"));
                }

                populateListItemsFromMap(childMap);
            }
        }
    }

    /**
     * populate listitems form search list type.
     */
    protected void populateSearchListItems() {
        listItems = new ArrayList<>();

        if (!StringUtils.isBlank(query)) {
            SimpleSearch search = getResource().adaptTo(SimpleSearch.class);

            if (search != null) {
                search.setQuery(query);
                search.setSearchIn(startIn);

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
     *
     * @param request reques instance
     * @return predicates converted from query string
     */
    public static PredicateGroup getPredicateGroupFromRequest(SlingHttpServletRequest request) {
        String queryParam = StringUtils.EMPTY;

        try {
            queryParam = request.getParameter(PN_QUERY);

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
     *
     * @param queryParam query string param, same as querybuilder
     * @return predicates converted from query string
     */
    protected static PredicateGroup getPredicateGroupFromQuery(String queryParam) {
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
     *
     * @param queryParam querystring param same as querybuilder
     */
    protected void populateListItemsFromQuery(String queryParam) {
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
                LOGGER.error("populateListItemsFromMap: could not get query builder object, q={}", queryParam);
            }
        } catch (Exception ex) {
            LOGGER.error("populateListItemsFromQuery: could not execute query q=[{}], ex={}", queryParam, ex);
        }
    }

    /**
     * doa query using a predicate map.
     *
     * @param map predicate map
     */
    @SuppressWarnings("Duplicates")
    protected void populateListItemsFromMap(Map<String, String> map) {
        try {
            QueryBuilder builder = getResourceResolver().adaptTo(QueryBuilder.class);

            if (builder != null) {
                Session session = getResourceResolver().adaptTo(Session.class);
                Query query = null;

                if (pageMax > 0) {
                    map.put("p.limit", String.valueOf(pageMax));
                } else if (limit > 0) {
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
                LOGGER.error("populateListItemsFromMap: could not get query builder object, map=[{}]", map);
            }
        } catch (Exception ex) {
            LOGGER.error("populateListItemsFromMap: could not execute query map=[{}], ex={}", map, ex);
        }
    }

    /**
     * allow passing of querybuilder queries.
     */
    @SuppressWarnings({"squid:S3776"})
    protected void populateQueryListItems() {
        listItems = new ArrayList<>();

        if (!StringUtils.isBlank(savedquery)) {
            try {
                if (getRequest().getRequestParameter(REQUEST_PARAM_QUERY) != null) {
                    String escapedQuery = StringUtils.EMPTY;
                    RequestParameter requestParameter = getRequest().getRequestParameter(REQUEST_PARAM_QUERY);

                    if (requestParameter != null) {
                        escapedQuery = requestParameter.toString();
                    }

                    String unescapedQuery = URLDecoder.decode(escapedQuery, QUERY_ENCODING);
                    QueryBuilder queryBuilder = getResourceResolver().adaptTo(QueryBuilder.class);

                    if (queryBuilder != null) {
                        java.util.Properties props = new java.util.Properties();
                        props.load(new ByteArrayInputStream(unescapedQuery.getBytes()));

                        PredicateGroup predicateGroup = PredicateConverter.createPredicates(props);
                        javax.jcr.Session jcrSession = getResourceResolver().adaptTo(javax.jcr.Session.class);

                        if (jcrSession != null) {
                            Query query = queryBuilder.createQuery(predicateGroup, jcrSession);

                            if (query != null) {
                                collectSearchResults(query.getResult());
                            }
                        } else {
                            LOGGER.error("populateQueryListItems: could not get sessions object");
                        }
                    } else {
                        LOGGER.error("populateQueryListItems: could not get query builder object");
                    }
                } else {
                    Session session = getResourceResolver().adaptTo(Session.class);
                    QueryBuilder queryBuilder = getResourceResolver().adaptTo(QueryBuilder.class);

                    if (session != null && queryBuilder != null) {
                        try {
                            Query query = queryBuilder.loadQuery(
                                getResource().getPath() + "/" + SAVEDQUERY_PROPERTY_NAME, session);

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
     *
     * @param result search results
     * @throws RepositoryException when can't read content
     */
    @SuppressWarnings("Duplicates")
    protected void collectSearchResults(SearchResult result) throws RepositoryException {
        Map<String, Object> resultInfo = new HashMap<>();
        resultInfo.put("executionTime", result.getExecutionTime());
        resultInfo.put("startIndex", result.getStartIndex());
        resultInfo.put("hasMore", result.hasMore());
        resultInfo.put("result", result);

        totalMatches = result.getTotalMatches();
        resultPages = result.getResultPages();
        long hitsPerPage = result.getHitsPerPage();
        totalPages = result.getResultPages().size();
        pageStart = result.getStartIndex();
        long currentPage = (pageStart / hitsPerPage) + 1;

        resultInfo.put("hitsPerPage", hitsPerPage);
        resultInfo.put("currentPage", currentPage);
        resultInfo.put("totalMatches", totalMatches);
        resultInfo.put("resultPages", resultPages);
        resultInfo.put("totalPages", totalPages);
        resultInfo.put(PAGE_START_PROPERTY_NAME, pageStart);

        isPaginating = (pageMax > 0 && result.getResultPages().size() > 0);
        componentProperties.put(LIST_ISPAGINATING, isPaginating);

        componentProperties.put("resultInfo", resultInfo);

        for (Hit hit : result.getHits()) {
            Map<String, Object> item = new HashMap<>();

            item.put("hit", hit);
            item.put("resource", hit.getResource());
            item.put("type", hit.getResource().getResourceType());

            Page containingPage = getPageManager().getContainingPage(hit.getResource());

            if (containingPage != null) {
                if (includePageInList(containingPage, showInvalid, showHidden)) {
                    item.put("page", containingPage);
                    item.putAll(getPageBadgeInfo(containingPage));
                    listItems.add(item);
                }
            }
        }
    }

    public enum Source {
        CHILDREN("children"),
        STATIC("static"),
        SEARCH("search"),
        TAGS("tags"),
        QUERYBUILDER("querybuilder"),
        DESCENDANTS("descendants"),
        EMPTY(StringUtils.EMPTY);

        private final String value;

        Source(String value) {
            this.value = value;
        }

        public static Source fromString(String value) {
            return Arrays.stream(values())
                .filter(s -> StringUtils.equals(value, s.value))
                .findFirst()
                .orElse(null);
        }
    }

    public enum SortOrder {
        ASC("asc"),
        DESC("desc");

        private final String value;

        public String getValue() {
            return value;
        }

        SortOrder(String value) {
            this.value = value;
        }

        public static SortOrder fromString(String value) {
            return Arrays.stream(values())
                .filter(s -> StringUtils.equals(value, s.value))
                .findFirst()
                .orElse(ASC);
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

            for (Iterator i$ = this.params.keySet().iterator(); i$.hasNext(); url = this.appendParam(url, param, this.params.get(param))) {
                param = (String) i$.next();
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
            int len$ = pairs.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                String pair = pairs[i$];
                String[] param = Text.explode(pair, 61, true);
                this.params.put(param[0], param[1]);
            }
        }

        private String appendParam(String url, String name, Object value) {
            char delim = url.indexOf(63) > 0 ? (char) 38 : (char) 63;
            return url + delim + name + '=' + value;
        }
    }
}
