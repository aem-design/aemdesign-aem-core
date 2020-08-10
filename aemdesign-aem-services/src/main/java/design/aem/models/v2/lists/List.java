package design.aem.models.v2.lists;

import com.day.cq.replication.ReplicationStatus;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.SimpleSearch;
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
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.*;

import static design.aem.utils.components.CommonUtil.*;
import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.ConstantsUtil.*;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class List extends BaseComponent {
    protected static final Logger LOGGER = LoggerFactory.getLogger(List.class);

    protected static final String FIELD_LIST_SOURCE = "listFrom";
    protected static final String FIELD_LIST_SOURCE_CHILDREN_PARENT_PATH = "parentPage";
    protected static final String FIELD_LIST_SOURCE_DESCENDANTS_PARENT_PATH = "ancestorPage";
    protected static final String FIELD_LIST_SOURCE_STATIC_PAGES = "pages";
    protected static final String FIELD_LIST_SOURCE_SEARCH_PATH = "searchIn";
    protected static final String FIELD_LIST_SOURCE_SEARCH_QUERY = "query";
    protected static final String FIELD_LIST_SOURCE_TAGS_PARENT_PATH = "tagsSearchRoot";
    protected static final String FIELD_LIST_SOURCE_TAGS_SELECTION = "tags";
    protected static final String FIELD_LIST_SOURCE_TAGS_CONDITION = "tagsMatch";
    protected static final String FIELD_LIST_SOURCE_TAGS_SEARCH_DETAILS = "useDetailsTags";

    protected static final String FIELD_LIST_SORT_ORDER = "sortOrder";
    protected static final String FIELD_LIST_ORDER_BY = "orderBy";
    protected static final String FIELD_LIST_LIMIT = "limit";
    protected static final String FIELD_LIST_PAGINATION_AFTER = "pageMax";
    protected static final String FIELD_LIST_PAGINATION_TYPE = "paginationType";

    protected static final String FIELD_LIST_DETAILS_BADGE = "detailsBadge";
    protected static final String FIELD_LIST_SHOW_HIDDEN_PAGES = "showHidden";

    protected static final String FIELD_LIST_PRINT_STRUCTURE = "printStructure";
    protected static final String FIELD_LIST_TAG = "listTag";
    protected static final String FIELD_LIST_SPLIT = "listSplit";
    protected static final String FIELD_LIST_SPLIT_EVERY = "listSplitEvery";

    protected static final String FIELD_LIST_FEED_ENABLED = "feedEnabled";
    protected static final String FIELD_LIST_FEED_TYPE = "feedType";

    protected String DEFAULT_LIST_SOURCE = StringUtils.EMPTY;
    protected String DEFAULT_LIST_SOURCE_CHILDREN_PARENT_PATH = null;
    protected String DEFAULT_LIST_SOURCE_DESCENDANTS_PARENT_PATH = null;
    protected String DEFAULT_LIST_SOURCE_SEARCH_PATH = null;
    protected String DEFAULT_LIST_SOURCE_SEARCH_QUERY = StringUtils.EMPTY;
    protected String DEFAULT_LIST_SOURCE_TAGS_PARENT_PATH = null;
    protected String DEFAULT_LIST_SOURCE_TAGS_CONDITION = "any";
    protected boolean DEFAULT_LIST_SOURCE_TAGS_SEARCH_DETAILS = false;

    protected String DEFAULT_LIST_ORDER_BY = "path";
    protected String DEFAULT_LIST_SORT_ORDER = SortOrder.ASC.getValue();
    protected int DEFAULT_LIST_LIMIT = -1;
    protected int DEFAULT_LIST_PAGINATION_AFTER = -1;
    protected String DEFAULT_LIST_PAGINATION_TYPE = "hidden";

    protected String DEFAULT_LIST_DETAILS_BADGE = DEFAULT_BADGE;
    protected boolean DEFAULT_LIST_SHOW_HIDDEN_PAGES = false;

    protected boolean DEFAULT_LIST_PRINT_STRUCTURE = true;
    protected String DEFAULT_LIST_TAG = "ul";
    protected boolean DEFAULT_LIST_SPLIT = false;
    protected int DEFAULT_LIST_SPLIT_EVERY = 3;

    protected boolean DEFAULT_LIST_FEED_ENABLED = false;
    protected String DEFAULT_LIST_FEED_TYPE = null;

    protected String DEFAULT_CURRENT_PATH = null;
    protected String DEFAULT_SEARCH_IN_PATH = null;
    protected String DEFAULT_LIST_LINK_TEXT = null;
    protected String DEFAULT_LIST_LINK_TITLE = null;

    protected static String ATTR_LIST_SPLIT = "data-list-split-enabled";
    protected static String ATTR_LIST_SPLIT_EVERY = "data-list-split-every";

    protected static String PROP_FEED_TITLE = "feedTitle";
    protected static String PROP_FEED_URL = "feedUrl";
    protected static String PROP_LIST_EMPTY = "isEmpty";
    protected static String PROP_IS_PAGINATING = "isPaginating";
    protected static String PROP_NEEDS_PAGINATION = "needsPagination";
    protected static String PROP_PARAM_MARKER_MAX = "max";
    protected static String PROP_PARAM_MARKER_START = "start";
    protected static String PROP_RESULT_INFO = "resultInfo";
    protected static String PROP_QUERY_PARAM = "q";

    protected Map<String, ListFeed> listFeeds = new HashMap<>();
    protected java.util.List<Map<String, Object>> listItems;

    private String identifier;

    private SortOrder sortOrder;
    private int limit;
    private boolean needsPagination;
    private int paginationAfter;

    private String detailsBadge;
    private boolean showHidden;

    private int listSplitEvery;

    private boolean feedEnabled;
    private String feedType;

    private boolean isPaginating;
    private long pageStart;
    private long totalMatches;
    private long totalPages;
    private java.util.List<ResultPage> resultPages;

    public void ready() {
        LOGGER.info("Getting list component ready for: {}", getComponentName());

        generateUniqueIdentifier();

        registerListFeedTypes();

        generateComponentPropertiesFromFields();
    }

    /**
     * Define the literal component name.
     *
     * @return String literal of the component name
     */
    protected String getComponentName() {
        return "list"; // NOSONAR
    }

    /**
     * Define a list of detail component names to find in the results.
     *
     * @return List of component names
     */
    protected String[] getDetailsComponentLookupNames() {
        return DEFAULT_LIST_DETAILS_SUFFIX;
    }

    /**
     * Register the feed types that are available for the list.
     */
    protected void registerListFeedTypes() {
        if (Boolean.FALSE.equals(currentStyle.get("disableFeedTypeRSS", false))) {
            listFeeds.put("rss", new ListFeed(".rss", "RSS Feed", "application/rss+xml"));
        }

        if (Boolean.FALSE.equals(currentStyle.get("disableFeedTypeAtom", false))) {
            listFeeds.put("atom", new ListFeed(".feed", "Atom 1.0 (List)", "application/atom+xml"));
        }
    }

    /**
     * Process the component fields and generate the {@link #componentProperties} instance.
     */
    private void generateComponentPropertiesFromFields() {
        DEFAULT_CURRENT_PATH = getCurrentPage().getPath();

        setComponentFieldsDefaults();

        setComponentFields(new Object[][]{
            // Sources
            {FIELD_LIST_SOURCE, DEFAULT_LIST_SOURCE},
            {FIELD_LIST_SOURCE_CHILDREN_PARENT_PATH, DEFAULT_LIST_SOURCE_CHILDREN_PARENT_PATH},
            {FIELD_LIST_SOURCE_DESCENDANTS_PARENT_PATH, DEFAULT_LIST_SOURCE_DESCENDANTS_PARENT_PATH},
            {FIELD_LIST_SOURCE_STATIC_PAGES, new String[]{}},
            {FIELD_LIST_SOURCE_SEARCH_PATH, DEFAULT_LIST_SOURCE_SEARCH_PATH},
            {FIELD_LIST_SOURCE_SEARCH_QUERY, DEFAULT_LIST_SOURCE_SEARCH_QUERY},
            {FIELD_LIST_SOURCE_TAGS_PARENT_PATH, DEFAULT_LIST_SOURCE_TAGS_PARENT_PATH},
            {FIELD_LIST_SOURCE_TAGS_SELECTION, new String[]{}},
            {FIELD_LIST_SOURCE_TAGS_CONDITION, DEFAULT_LIST_SOURCE_TAGS_CONDITION},
            {FIELD_LIST_SOURCE_TAGS_SEARCH_DETAILS, DEFAULT_LIST_SOURCE_TAGS_SEARCH_DETAILS},

            // Options
            {FIELD_LIST_ORDER_BY, DEFAULT_LIST_ORDER_BY},
            {FIELD_LIST_SORT_ORDER, DEFAULT_LIST_SORT_ORDER},
            {FIELD_LIST_LIMIT, DEFAULT_LIST_LIMIT},
            {FIELD_LIST_PAGINATION_AFTER, DEFAULT_LIST_PAGINATION_AFTER},
            {FIELD_LIST_PAGINATION_TYPE, DEFAULT_LIST_PAGINATION_TYPE},

            // Items
            {FIELD_LIST_DETAILS_BADGE, DEFAULT_LIST_DETAILS_BADGE},
            {FIELD_LIST_SHOW_HIDDEN_PAGES, false},

            // Structure
            {FIELD_LIST_PRINT_STRUCTURE, DEFAULT_LIST_PRINT_STRUCTURE},
            {FIELD_LIST_TAG, DEFAULT_LIST_TAG},
            {FIELD_LIST_SPLIT, DEFAULT_LIST_SPLIT, ATTR_LIST_SPLIT},
            {FIELD_LIST_SPLIT_EVERY, DEFAULT_LIST_SPLIT_EVERY, ATTR_LIST_SPLIT_EVERY},

            // Feed
            {FIELD_LIST_FEED_ENABLED, DEFAULT_LIST_FEED_ENABLED},
            {FIELD_LIST_FEED_TYPE, DEFAULT_LIST_FEED_TYPE},
        });

        componentProperties = ComponentsUtil.getComponentProperties(
            this,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY,
            DEFAULT_FIELDS_ANALYTICS);

        processListSettingsAndConfiguration();

        handleAdditionalSetup();

        processBadgeRequestAttributes();

        componentProperties.put(PROP_NEEDS_PAGINATION, needsPagination);
        componentProperties.put(COMPONENT_ATTRIBUTES, buildAttributesString(componentProperties.attr.getData(), null));
    }

    /**
     * Helper method that allows additional setup to be completed.
     */
    protected void handleAdditionalSetup() {
        if (Boolean.TRUE.equals(feedEnabled)) {
            setupFeedForList();
        }
    }

    /**
     * Set the default component field values that are dynamic.
     */
    protected void setComponentFieldsDefaults() {
        DEFAULT_LIST_SOURCE_CHILDREN_PARENT_PATH = DEFAULT_CURRENT_PATH;
        DEFAULT_LIST_SOURCE_DESCENDANTS_PARENT_PATH = DEFAULT_CURRENT_PATH;
        DEFAULT_LIST_SOURCE_SEARCH_PATH = getResourcePage().getPath();
        DEFAULT_LIST_SOURCE_TAGS_PARENT_PATH = DEFAULT_CURRENT_PATH;

        DEFAULT_LIST_ORDER_BY = currentStyle.get(FIELD_LIST_ORDER_BY, DEFAULT_LIST_ORDER_BY);
        DEFAULT_LIST_SORT_ORDER = currentStyle.get(FIELD_LIST_SORT_ORDER, DEFAULT_LIST_SORT_ORDER);
        DEFAULT_LIST_LIMIT = currentStyle.get(FIELD_LIST_LIMIT, DEFAULT_LIST_LIMIT);
        DEFAULT_LIST_PAGINATION_AFTER = currentStyle.get(FIELD_LIST_PAGINATION_AFTER, DEFAULT_LIST_PAGINATION_AFTER);

        DEFAULT_LIST_SHOW_HIDDEN_PAGES = currentStyle.get(FIELD_LIST_SHOW_HIDDEN_PAGES, DEFAULT_LIST_SHOW_HIDDEN_PAGES);

        DEFAULT_LIST_SPLIT = currentStyle.get(FIELD_LIST_SPLIT, DEFAULT_LIST_SPLIT);
        DEFAULT_LIST_SPLIT_EVERY = currentStyle.get(FIELD_LIST_SPLIT_EVERY, DEFAULT_LIST_SPLIT_EVERY);
    }

    /**
     * Process the most common settings and configurations for the list.
     */
    private void processListSettingsAndConfiguration() {
        sortOrder = SortOrder.fromString(componentProperties.get(FIELD_LIST_SORT_ORDER, DEFAULT_LIST_SORT_ORDER));
        needsPagination = !componentProperties.get(FIELD_LIST_PAGINATION_TYPE).equals(DEFAULT_LIST_PAGINATION_TYPE);
        paginationAfter = componentProperties.get(FIELD_LIST_PAGINATION_AFTER, DEFAULT_LIST_PAGINATION_AFTER);

        limit = componentProperties.get(FIELD_LIST_LIMIT, DEFAULT_LIST_LIMIT);
        limit = limit < 1 ? Integer.MAX_VALUE : limit;

        detailsBadge = componentProperties.get(FIELD_LIST_DETAILS_BADGE, DEFAULT_LIST_DETAILS_BADGE);
        showHidden = componentProperties.get(FIELD_LIST_SHOW_HIDDEN_PAGES, DEFAULT_LIST_SHOW_HIDDEN_PAGES);

        listSplitEvery = componentProperties.get(FIELD_LIST_SPLIT_EVERY, DEFAULT_LIST_SPLIT_EVERY);

        feedEnabled = componentProperties.get(FIELD_LIST_FEED_ENABLED, DEFAULT_LIST_FEED_ENABLED);
        feedType = componentProperties.get(FIELD_LIST_FEED_TYPE, DEFAULT_LIST_FEED_TYPE);

        // Check the query string parameters for the start offset
        String requestPageStart = getParameter(PROP_PARAM_MARKER_START);

        if (isNotEmpty(requestPageStart)) {
            pageStart = tryParseLong(requestPageStart, 0);
        }
    }

    /**
     * Retrieve a request parameter with component id prefix.
     *
     * @param name Name of query string parameter
     * @return Parameter value
     */
    private String getParameter(String name) {
        return getRequest().getParameter(identifier + PATH_UNDERSCORE + name);
    }

    /**
     * Retrieve the configuration for the authored feed (if any).
     */
    private void setupFeedForList() {
        ListFeed listFeed = listFeeds.get(feedType);

        componentProperties.put(FIELD_LIST_FEED_TYPE, listFeed != null ? listFeed.type : null);
        componentProperties.put(PROP_FEED_TITLE, listFeed != null ? listFeed.title : null);
        componentProperties.put(PROP_FEED_URL, listFeed != null ? getResource().getPath().concat(listFeed.extension) : null);
    }

    /**
     * Process the badge configuration which will be passed onto each page during runtime.
     */
    private void processBadgeRequestAttributes() {
        ComponentProperties badgeRequestAttributes = ComponentsUtil.getComponentProperties(
            this,
            false,
            DEFAULT_FIELDS_DETAILS_OPTIONS_OVERRIDE);

//        TODO: Determine if this provides any real-world value
//        badgeRequestAttributes.putAll(
//            getAssetInfo(getResourceResolver(),
//            getResourceImagePath(getResource(), DETAILS_THUMBNAIL),
//            DETAILS_THUMBNAIL));

        componentProperties.put(BADGE_REQUEST_ATTRIBUTES, badgeRequestAttributes);

        try {
            getRequest().setAttribute(BADGE_REQUEST_ATTRIBUTES, badgeRequestAttributes);
            getRequest().setAttribute(COMPONENT_PROPERTIES, componentProperties);
        } catch (Exception ex) {
            LOGGER.error("Could not set badge request attributes.\n{}", ex.getMessage());
        }
    }

    /**
     * Generate a unique identifier to use in creating query string parameters.
     */
    private void generateUniqueIdentifier() {
        String path = getResource().getPath();
        String rootMarker = JcrConstants.JCR_CONTENT.concat(PATH_SEPARATOR);
        int root = path.indexOf(rootMarker);

        if (root >= 0) {
            path = path.substring(root + rootMarker.length());
        }

        identifier = path.replace(PATH_SEPARATOR, PATH_UNDERSCORE);
    }

    /**
     * Retrieve all of the content associated with the authored parameters.
     *
     * @return Collection of list items.
     */
    public Collection<Map<String, Object>> getListItems() {
        if (listItems == null) {
            populateListItems(getListType());
        }

        return listItems;
    }

    /**
     * Retrieve the authored list type.
     *
     * @return Authored list type
     */
    protected Source getListType() {
        String listFromValue = componentProperties.get(FIELD_LIST_SOURCE, DEFAULT_LIST_SOURCE);

        return Source.fromString(listFromValue);
    }

    /**
     * Populate the list of results using the {@code listType} provided.
     *
     * @param listType List type to execute
     */
    protected void populateListItems(Source listType) {
        String styleCheck = null;
        Runnable callback = null;

        listItems = new ArrayList<>();

        switch (listType) {
            case CHILDREN:
                styleCheck = "disableChildren";
                callback = this::populateChildListItems;
                break;
            case DESCENDANTS:
                styleCheck = "disableDescendants";
                callback = this::populateDescendantsListItems;
                break;
            case STATIC:
                styleCheck = "disableStatic";
                callback = this::populateStaticListItems;
                break;
            case SEARCH:
                styleCheck = "disableSearch";
                callback = this::populateSearchListItems;
                break;
            case TAGS:
                styleCheck = "disableTags";
                callback = this::populateTagListItems;
                break;
        }

        try {
            if (callback != null) {
                if (Boolean.FALSE.equals(currentStyle.get(styleCheck, false))) {
                    callback.run();
                } else {
                    LOGGER.warn("List instance attempted to access disabled list type builder!\nList Type: {}\nPath: {}",
                        listType,
                        getResource().getPath());
                }
            }
        } catch (Exception ex) {
            LOGGER.error("Unable to populate list items due to an expected error!\n{}", ex.getMessage());
        }

        componentProperties.put(PROP_LIST_EMPTY, totalMatches == 0);

        updateIsPaginating();

        if (Boolean.TRUE.equals(componentProperties.get(FIELD_LIST_SPLIT))) {
            chunkItemsIntoGroups();
        }
    }

    /**
     * Populate the list of results using depth based on the value of {@code flat}.
     *
     * @param path Resource path to search from
     * @param flat When {@code true}, only the same path will be searched
     */
    protected void populateChildListItems(String path, Boolean flat) {
        Map<String, String> predicates = new HashMap<>();

        predicates.put("path", getPageManager().getPage(path).getPath());
        predicates.put("path.flat", String.valueOf(flat));
        predicates.put("type", NameConstants.NT_PAGE);

        populateListItemsFromMap(predicates);
    }

    /**
     * Populate the list of results from only direct children of a parent path.
     */
    protected void populateChildListItems() {
        String path = componentProperties.get(FIELD_LIST_SOURCE_CHILDREN_PARENT_PATH, String.class);

        populateChildListItems(path, true);
    }

    /**
     * Populate the list of results from descendants of a parent path.
     */
    protected void populateDescendantsListItems() {
        String path = componentProperties.get(FIELD_LIST_SOURCE_DESCENDANTS_PARENT_PATH, String.class);

        populateChildListItems(path, false);
    }

    /**
     * Populate the list of results using a static list of pages.
     */
    protected void populateStaticListItems() {
        String[] resourcePaths = (String[]) componentProperties.get(FIELD_LIST_SOURCE_STATIC_PAGES);
        ResourceResolver resourceResolver = getResourceResolver();

        for (String path : resourcePaths) {
            Resource pathResource = resourceResolver.resolve(path);

            if (ResourceUtil.isNonExistingResource(pathResource)) {
                LOGGER.error("Skipping item as it does not exist!\nPath: {}", path);
                continue;
            }

            Map<String, Object> item = new HashMap<>();
            Page page = getPageManager().getContainingPage(path);

            if (page != null && includePageInList(page, showHidden)) {
                item.put("type", pathResource.getResourceType());
                item.put("resource", pathResource);
                item.put("page", page);
                item.putAll(getPageBadgeInfo(page));

                listItems.add(item);
            }
        }
    }

    /**
     * Populate the list of results using a simple content search.
     */
    protected void populateSearchListItems() {
        String searchInPath = componentProperties.get(FIELD_LIST_SOURCE_SEARCH_PATH, DEFAULT_LIST_SOURCE_SEARCH_PATH);
        String searchQuery = componentProperties.get(FIELD_LIST_SOURCE_SEARCH_QUERY, DEFAULT_LIST_SOURCE_SEARCH_QUERY);

        if (StringUtils.isBlank(searchQuery)) {
            return;
        }

        SimpleSearch search = getResource().adaptTo(SimpleSearch.class);

        if (search != null) {
            search.setSearchIn(searchInPath);
            search.setQuery(searchQuery);

            if (paginationAfter > 0) {
                search.setHitsPerPage(paginationAfter);
            } else if (limit > 0) {
                search.setHitsPerPage(limit);
            }

            try {
                collectSearchResults(search.getResult());
            } catch (RepositoryException ex) {
                LOGGER.error("Unable to retrieve search results for query!\nQuery: {}\nException: {}", searchQuery, ex);
            }
        }
    }

    /**
     * Populate the list of results using by searching for tags set on pages.
     */
    protected void populateTagListItems() {
        String[] tags = (String[]) componentProperties.get(FIELD_LIST_SOURCE_TAGS_SELECTION);

        if (!ArrayUtils.isEmpty(tags)) {
            return;
        }

        Page rootPage = getPageManager().getPage((String) componentProperties.get(FIELD_LIST_SOURCE_TAGS_PARENT_PATH));

        if (rootPage != null) {
            populateListItemsFromMap(getTagListPredicates(tags, rootPage.getPath()));
        }
    }

    /**
     * Generate the predicate map for the tags search.
     *
     * @param tags     List of tags
     * @param rootPath Path that will be used in the search
     * @return Predicates map
     */
    protected Map<String, String> getTagListPredicates(String[] tags, String rootPath) {
        Map<String, String> predicates = new HashMap<>();

        boolean matchAny = componentProperties.get(FIELD_LIST_SOURCE_TAGS_CONDITION)
            .equals(DEFAULT_LIST_SOURCE_TAGS_CONDITION);

        boolean matchDetailsTags = Boolean.TRUE.equals(componentProperties.get(FIELD_LIST_SOURCE_TAGS_SEARCH_DETAILS));

        String operator = matchAny ? "or" : "and";
        String groupPrefix = "group.0_group.";
        String tagSuffix = "_group.tagid";

        predicates.put("path", rootPath);
        predicates.put("group.p." + operator, "true");
        predicates.put(groupPrefix + "p." + operator, "true");

        int offset = 0;

        for (String tag : tags) {
            predicates.put(groupPrefix + offset + tagSuffix, tag);
            predicates.put(groupPrefix + offset + tagSuffix + ".property",
                JcrConstants.JCR_CONTENT.concat("/").concat(TagConstants.PN_TAGS));

            ++offset;

            if (matchDetailsTags) {
                predicates.put(groupPrefix + offset + tagSuffix, tag);
                predicates.put(groupPrefix + offset + tagSuffix + "property",
                    JcrConstants.JCR_CONTENT.concat("/article/par/*/").concat(TagConstants.PN_TAGS));
            }
        }

        return predicates;
    }

    /**
     * Take the items and determine where the list split on each should occur,
     */
    private void chunkItemsIntoGroups() {
        for (int i = 0; i < listItems.size(); i++) {
            if ((i + 1) % listSplitEvery == 0) {
                listItems.get(i).put("split", true);
            }
        }
    }

    /**
     * Execute a query against the content tree.
     *
     * @param predicates Predicate query map
     */
    @SuppressWarnings("Duplicates")
    protected void populateListItemsFromMap(Map<String, String> predicates) {
        try {
            QueryBuilder builder = getResourceResolver().adaptTo(QueryBuilder.class);

            if (builder != null) {
                Session session = getResourceResolver().adaptTo(Session.class);

                // Limit the number of results
                if (paginationAfter > 0) {
                    predicates.put("p.limit", String.valueOf(paginationAfter));
                } else if (limit > 0) {
                    predicates.put("p.limit", String.valueOf(limit));
                }

                if (pageStart > 0) {
                    predicates.put("p.offset", String.valueOf(pageStart));
                }

                String orderBy = componentProperties.get(FIELD_LIST_ORDER_BY, DEFAULT_LIST_ORDER_BY);

                predicates.put("orderby", orderBy);
                predicates.put("orderby.sort", sortOrder.getValue());

                PredicateGroup root = PredicateGroup.create(predicates);

                // Avoid slow //* xpath queries
                if (!root.isEmpty()) {
                    Query query = builder.createQuery(root, session);

                    if (query != null) {
                        collectSearchResults(query.getResult());
                    }
                }
            } else {
                LOGGER.error("Could not get Query Builder object!");
            }
        } catch (Exception ex) {
            LOGGER.error("Could not execute query!\nPredicates [{}]\nException={}", predicates, ex);
        }
    }

    /**
     * Process the results of the search result given.
     *
     * @param searchResult Search results instance
     * @throws RepositoryException when can't read content
     */
    @SuppressWarnings("Duplicates")
    private void collectSearchResults(SearchResult searchResult) throws RepositoryException {
        Map<String, Object> resultInfo = new HashMap<>();

        resultInfo.put("executionTime", searchResult.getExecutionTime());
        resultInfo.put("startIndex", searchResult.getStartIndex());
        resultInfo.put("hasMore", searchResult.hasMore());
        resultInfo.put("result", searchResult);

        pageStart = searchResult.getStartIndex();
        resultPages = searchResult.getResultPages();
        totalMatches = searchResult.getTotalMatches();
        totalPages = searchResult.getResultPages().size();

        long hitsPerPage = searchResult.getHitsPerPage();

        resultInfo.put("currentPage", (pageStart / hitsPerPage) + 1);
        resultInfo.put("hitsPerPage", hitsPerPage);
        resultInfo.put("resultPages", resultPages);
        resultInfo.put("totalMatches", totalMatches);
        resultInfo.put("totalPages", searchResult.getResultPages().size());
        resultInfo.put("pageStart", pageStart);

        isPaginating = paginationAfter > 0 && !searchResult.getResultPages().isEmpty();

        componentProperties.put(PROP_IS_PAGINATING, isPaginating);
        componentProperties.put(PROP_RESULT_INFO, resultInfo);

        for (Hit hit : searchResult.getHits()) {
            Map<String, Object> result = new HashMap<>();

            result.put("hit", hit);
            result.put("resource", hit.getResource());
            result.put("type", hit.getResource().getResourceType());

            Page containingPage = getPageManager().getContainingPage(hit.getResource());

            if (containingPage != null && includePageInList(containingPage, showHidden)) {
                result.put("page", containingPage);
                result.putAll(getPageBadgeInfo(containingPage));

                listItems.add(result);
            }
        }
    }

    /**
     * Determine if the page should be allowed to be used.
     *
     * @param page          Page to check
     * @param includeHidden Include in page is hidden
     * @return Whether or not the page should be included
     */
    protected boolean includePageInList(Page page, boolean includeHidden) {
        boolean pageIsDeactivated = false;
        boolean pageIsDeleted = false;
        boolean pageIsHidden = false;

        if (page != null && page.hasContent()) {
            Resource pageContent = page.getContentResource();

            if (pageContent != null) {
                ReplicationStatus replicationStatus = pageContent.adaptTo(ReplicationStatus.class);

                if (replicationStatus != null) {
                    pageIsDeactivated = replicationStatus.isDeactivated();
                }
            }

            pageIsDeleted = page.getDeleted() != null;
            pageIsHidden = page.isHideInNav();
        }

        return (includeHidden || !pageIsHidden) && !pageIsDeactivated && !pageIsDeleted;
    }

    /**
     * Get badge information for the given {@code page}.
     *
     * @param page Page to use for collection
     * @return Map of page badge attributes
     */
    protected Map<String, Object> getPageBadgeInfo(Page page) {
        return getPageBadgeInfo(page, getDetailsComponentLookupNames(), getResourceResolver(), detailsBadge);
    }

    /**
     * Get badge information for the given {@code page}.
     *
     * @param page              Page to use for collection
     * @param detailsNameSuffix Details component suffix(s) to look for
     * @param resourceResolver  Resource resolver to use
     * @param detailsBadge      Badge selectors to add
     * @return Map of page badge attributes
     */
    public static Map<String, Object> getPageBadgeInfo(
        Page page,
        String[] detailsNameSuffix,
        ResourceResolver resourceResolver,
        String detailsBadge
    ) {
        Map<String, Object> badge = new HashMap<>();

        try {
            String componentPath = findComponentInPage(page, detailsNameSuffix);

            if (isNotEmpty(componentPath)) {
                Resource componentResource = resourceResolver.getResource(componentPath);

                if (componentResource != null) {
                    badge.put("componentResourceType", componentResource.getResourceType());
                }

                badge.put("componentPath", componentPath);
                badge.put("componentPathSelectors", new String[]{DETAILS_SELECTOR_BADGE, detailsBadge});

                String pageRedirect = getPageRedirect(page);

                if (isEmpty(pageRedirect)) {
                    badge.put("redirectLink", pageRedirect);
                }
            } else {
                LOGGER.warn("Page does not have component with the given suffix!\nPath: {}\nSuffix: {}",
                    page.getPath(),
                    detailsNameSuffix);

                badge.put("componentMissing", true);
            }

            badge.put("pagePath", page.getPath());
        } catch (Exception ex) {
            LOGGER.error("Could not get badge information for the given page!\nPath: {}\nException: {}",
                page.getPath(),
                ex);
        }

        return badge;
    }

    /**
     * Get previous page url.
     *
     * @return URL for the previous page
     */
    private String getPreviousPageLink() {
        if (isPaginating && paginationAfter > 0 && !resultPages.isEmpty() && pageStart != 0) {
            long previousPageStart = pageStart > paginationAfter ? pageStart - paginationAfter : 0;

            List.PageLink link = new List.PageLink(getRequest());
            link.setParameter(PROP_PARAM_MARKER_START, previousPageStart);

            return link.toString();
        }

        return StringUtils.EMPTY;
    }

    /**
     * Get next page url.
     *
     * @return URL for the next page
     */
    private String getNextPageLink() {
        long nextPageStart = pageStart + paginationAfter;

        if (isPaginating && paginationAfter > 0 && !resultPages.isEmpty() && nextPageStart < totalMatches) {
            List.PageLink link = new List.PageLink(getRequest());
            link.setParameter(PROP_PARAM_MARKER_START, nextPageStart);

            return link.toString();
        }

        return StringUtils.EMPTY;
    }

    /**
     * Define the pagination attributes that front end technologies can make use of.
     */
    private void updateIsPaginating() {
        componentProperties.attr.add("data-has-pages", isPaginating);

        if (isPaginating) {
            componentProperties.attr.add("data-total-pages", String.valueOf(totalPages));
            componentProperties.attr.add("data-content-url", getResource().getPath().concat(DEFAULT_EXTENTION));
            componentProperties.attr.add("data-content-start", identifier.concat("_start"));

            componentProperties.put("nextPageLink", getNextPageLink());
            componentProperties.put("previousPageLink", getPreviousPageLink());
        }
    }

    public static class ListFeed {
        public String extension;
        public String title;
        public String type;

        public ListFeed(String extension, String title, String type) {
            this.extension = extension;
            this.title = title;
            this.type = type;
        }
    }

    public enum Source {
        CHILDREN("children"),
        DESCENDANTS("descendants"),
        STATIC("static"),
        SEARCH("search"),
        TAGS("tags");

        private final String value;

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

        private final String value;

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

    public class PageLink {
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
            if (!name.startsWith(List.this.identifier + "_")) {
                name = List.this.identifier + "_" + name;
            }

            return name;
        }

        private void initParams(String query) {
            this.params = new HashMap();
            String[] pairs = Text.explode(query, 38);
            String[] arr$ = pairs;
            int len$ = pairs.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                String pair = arr$[i$];
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
