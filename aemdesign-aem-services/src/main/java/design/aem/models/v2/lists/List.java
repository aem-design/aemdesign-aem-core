package design.aem.models.v2.lists;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.commons.RangeIterator;
import com.day.cq.i18n.I18n;
import com.day.cq.search.*;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.text.Text;
import com.fasterxml.jackson.annotation.JsonProperty;
import design.aem.components.ComponentProperties;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.net.URLDecoder;
import java.util.*;

import static design.aem.utils.components.CommonUtil.*;
import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.ConstantsUtil.*;
import static design.aem.utils.components.ImagesUtil.getAssetInfo;
import static design.aem.utils.components.ImagesUtil.getResourceImagePath;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class List extends WCMUsePojo {

    private static final Logger LOGGER = LoggerFactory.getLogger(List.class);

    private static final int LIMIT_DEFAULT = 100;
    private static final int PAGEMAX_DEFAULT = -1;
    private static final int PN_DEPTH_DEFAULT = 1;
    private static final int LISTSPLITEVERY_DEFAULT = 5;
    private static final String TAGS_MATCH_ANY_VALUE = "any";
    private static final String PN_DATE_FORMAT_DEFAULT = "yyyy-MM-dd";

    private static final String PN_SOURCE = "listFrom"; //SOURCE_PROPERTY_NAME
    private static final String PN_PAGES = "pages"; //PAGES_PROPERTY_NAME
    private static final String PN_PARENT_PAGE = "parentPage"; //PARENT_PAGE_PROPERTY_NAME
    private static final String PN_TAGS_PARENT_PAGE = "tagsSearchRoot";
    private static final String PN_TAGS = "tags"; //TAGS_PROPERTY_NAME
    private static final String PN_TAGS_MATCH = "tagsMatch"; //TAGS_MATCH_PROPERTY_NAME
    private static final String LIMIT_PROPERTY_NAME = "limit"; //TAGS_MATCH_PROPERTY_NAME
    private static final String PAGE_MAX_PROPERTY_NAME = "pageMax";
    private static final String ANCESTOR_PAGE_PROPERTY_NAME = "ancestorPage";
    private static final String SEARCH_IN_PROPERTY_NAME = "searchIn";
    private static final String SAVEDQUERY_PROPERTY_NAME = "savedquery";
    private static final String LISTSPLITEVERY = "listSplitEvery";
    private static final String SHOWHIDDEN = "showHidden";
    private static final String SHOWINVALID = "showInvalid";
    private static final String CHILDDEPTH = "childDepth";
    private static final String DETAILSBADGE = "detailsBadge";
    private static final int CHILDDEPTH_DESCENDANTS = 5;
    private static final int CHILDDEPTH_CHILDREN =1;

    public static final String LIST_ITEM_BADGE = "listItemBadge";
    public static final String LIST_TAG_ORDERED = "ol";
    public static final String LIST_TAG_UNORDERED = "ul";
    public static final String LIST_TAG = "listTag";
    public static final String LIST_ISORDERED = "isOrdered";
    public static final String LIST_ISPAGINATING = "isPaginating";
    public static final String LIST_ISEMPTY = "isEmpty";

//    private static final String PN_SHOW_DESCRIPTION = "showDescription";
//    private static final String PN_SHOW_MODIFICATION_DATE = "showModificationDate";
//    private static final String PN_LINK_ITEMS = "linkItems";
    private static final String PN_SEARCH_IN = "searchIn";
    private static final String PN_SORT_ORDER = "sortOrder";
    private static final String PN_ORDER_BY = "orderBy";
//    private static final String PN_DATE_FORMAT = "dateFormat";


    private static final Boolean DEFAULT_PRINT_STRUCTURE = true;
    private static final String DEFAULT_TITLE_TYPE = "h2";
    private static final String DEFAULT_I18N_CATEGORY = "list";
    private static final String DEFAULT_BADGE = "default";

    private ComponentProperties componentProperties = null;
    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }

    public final String LISTITEM_LINK_TEXT = "listItemLinkText";
    public final String LISTITEM_LINK_TITLE = "listItemLinkTitle";


    private java.util.List<Map<String,Object>> listItems;
//    protected java.util.List<Page> listItems;

    private String startIn;
    private List.SortOrder sortOrder;
    private List.OrderBy orderBy;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(intValues = LIMIT_DEFAULT)
    private int limit;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(intValues = PN_DEPTH_DEFAULT)
    private int childDepth;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = StringUtils.EMPTY)
    private String query;
    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = StringUtils.EMPTY)
    private String savedquery;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(intValues = 0)
    private int maxItems;

    private int pageMaximum;
    private int pageStart;
    private int listSplitEvery;
    private String id;
    private boolean isPaginating;

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

    @Override
    public void activate() throws Exception {
        loadConfig();

    }

    protected void loadConfig() {

        I18n _i18n = new I18n(getRequest());

        LOGGER.error("List: loading config");

        //not using lamda is available so this is the best that can be done
        Object[][] componentFields = {
                {LIST_TAG, LIST_TAG_UNORDERED},
                {"feedEnabled", false},
                {"feedType", "rss"},
                {"listSplit", false, "data-list-split-enabled"},
                {LISTSPLITEVERY, LISTSPLITEVERY_DEFAULT, "data-list-split-every"},
                {"tags", new String[]{},"data-search-tags", Tag.class.getCanonicalName()},
                {PN_ORDER_BY, StringUtils.EMPTY},
                {DETAILSBADGE, DEFAULT_BADGE, "data-badge"},
                {"printStructure", DEFAULT_PRINT_STRUCTURE},
                {SHOWHIDDEN, false},
                {SHOWINVALID, false},
                {"paginationType", StringUtils.EMPTY},
                {LIMIT_PROPERTY_NAME, LIMIT_DEFAULT},
                {PAGE_MAX_PROPERTY_NAME, PAGEMAX_DEFAULT},
                {ANCESTOR_PAGE_PROPERTY_NAME, StringUtils.EMPTY},
                {PN_PARENT_PAGE, StringUtils.EMPTY},
                {PN_SOURCE, StringUtils.EMPTY},
                {PN_PAGES, StringUtils.EMPTY},
                {PN_TAGS_PARENT_PAGE, StringUtils.EMPTY},
                {PN_TAGS, StringUtils.EMPTY},
                {PN_TAGS_MATCH, StringUtils.EMPTY},
                {PN_ORDER_BY, StringUtils.EMPTY},
                {PN_SORT_ORDER, SortOrder.ASC.value},
                {PN_SEARCH_IN, getResourcePage().getPath()},
                {SAVEDQUERY_PROPERTY_NAME, StringUtils.EMPTY},
                {SEARCH_IN_PROPERTY_NAME, StringUtils.EMPTY},
                {CHILDDEPTH, CHILDDEPTH_DESCENDANTS}
        };

        componentProperties = ComponentsUtil.getComponentProperties(
                this,
                componentFields,
                DEFAULT_FIELDS_STYLE,
                DEFAULT_FIELDS_ACCESSIBILITY,
                DEFAULT_FIELDS_ANALYTICS);

        startIn = componentProperties.get(PN_SEARCH_IN, getResourcePage().getPath());
        sortOrder = SortOrder.fromString(componentProperties.get(PN_SORT_ORDER, SortOrder.ASC.value));
        orderBy = OrderBy.fromString(componentProperties.get(PN_ORDER_BY, StringUtils.EMPTY));
        childDepth = componentProperties.get(CHILDDEPTH, CHILDDEPTH_DESCENDANTS);
        savedquery = componentProperties.get(SAVEDQUERY_PROPERTY_NAME, "");
        pageMaximum = componentProperties.get(PAGE_MAX_PROPERTY_NAME, 0);
        listSplitEvery = componentProperties.get(LISTSPLITEVERY, LISTSPLITEVERY_DEFAULT);
        detailsBadge = componentProperties.get(DETAILSBADGE, DEFAULT_BADGE);
        limit = componentProperties.get(LIMIT_PROPERTY_NAME, LIMIT_DEFAULT);

        LOGGER.error("listSplitEvery {}",listSplitEvery);

        if (detailsNameSuffix == null) {
            detailsNameSuffix = DEFAULT_LIST_DETAILS_SUFFIX;
        }

        pageStart = tryParseInt(getParameter("start"),0);
        pageMaximum = tryParseInt(getParameter("max"), pageMaximum);
        generateId();

        if (getRequest().getRequestParameter("q") !=null) {
            try {
                query = getRequest().getRequestParameter("q").toString();
            } catch (Exception ex) {
                LOGGER.error("could not read query param {}", ex);
            }
        }

        showHidden = componentProperties.get(SHOWHIDDEN, false);
        showInvalid = componentProperties.get(SHOWINVALID, false);

        String resourcePath = getResource().getPath();

        if ((Boolean)componentProperties.get("feedEnabled")) {
            if ("atom".equals(componentProperties.get("feedType"))) {
                componentProperties.put("feedExt", ".feed");
                componentProperties.put("feedTitle", "Atom 1.0 (List)");
                componentProperties.put("feedType", "application/atom+xml");
            } else {
                componentProperties.put("feedExt", ".rss");
                componentProperties.put("feedTitle", "RSS Feed");
                componentProperties.put("feedType", "application/rss+xml");
            }
            if (isNotEmpty(componentProperties.get("feedExt",""))) {
                componentProperties.put("feedUrl", resourcePath.concat(componentProperties.get("feedExt","")));
            } else {
                componentProperties.put("feedUrl",resourcePath);
            }
        }



        String strItemLimit = componentProperties.get(LIMIT_PROPERTY_NAME, "");
        String strPageItems = componentProperties.get(PAGE_MAX_PROPERTY_NAME, "");

        // no limit set, but pagination enabled, set limit to infinite
        if (StringUtils.isBlank(strItemLimit) && !StringUtils.isBlank(strPageItems)) {
            limit = Integer.MAX_VALUE;
        }



        //prepare request parms to pass to badges
        ComponentProperties badgeRequestAttributes = ComponentsUtil.getComponentProperties(
                this,
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


        componentProperties.put(COMPONENT_ATTRIBUTES, buildAttributesString(componentProperties.attr.getData(), null));

        LOGGER.error("list loaded");
    }

    private void generateId() {
        String path = getResource().getPath();
        String rootMarker = JcrConstants.JCR_CONTENT.concat(PATH_SEPARATOR);
        int root = path.indexOf(rootMarker);
        if (root >= 0) {
            path = path.substring(root + rootMarker.length());
        }

        id = path.replace(PATH_SEPARATOR, PATH_UNDERSCORE);
    }

    private String getParameter(String name) {
        return getRequest().getParameter(id + PATH_UNDERSCORE + name);
    }

    public String getNextPageLink() {
        if (isPaginating && pageMaximum > 0 && pageStart + pageMaximum < listItems.size()) {
            int start = pageStart + pageMaximum;
            List.PageLink link = new List.PageLink(getRequest());
            link.setParameter("start", start);
            return link.toString();
        } else {
            return null;
        }
    }

    public String getPreviousPageLink() {
        if (isPaginating && pageStart > 0) {
            int start = pageMaximum > 0 && pageStart > pageMaximum ? pageStart - pageMaximum : 0;
            List.PageLink link = new List.PageLink(getRequest());
            link.setParameter("start", start);
            return link.toString();
        } else {
            return null;
        }
    }

    private Map<String,Object> getPageBadgeInfo(Page page) {

        Map<String,Object> badge = new HashMap<>();

        try {

            badge.put("listLookForDetailComponent", detailsNameSuffix);

            String componentPath = findComponentInPage(page, detailsNameSuffix);
            if (isNotEmpty(componentPath)) {
                Resource componentResource = getResourceResolver().getResource(componentPath);
                String componentResourceType = componentResource.getResourceType();
                if (isNull(componentResourceType)) {
                    componentResourceType = "";
                }
                badge.put("componentPath", componentPath);
                badge.put("componentResourceType", componentResourceType);

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

    protected Source getListType() {
        String listFromValue = componentProperties.get(PN_SOURCE, getCurrentStyle().get(PN_SOURCE, StringUtils.EMPTY));
        return Source.fromString(listFromValue);
    }

    public Collection<Map<String,Object>> getListItems() {
        LOGGER.error("loading items");
        if (listItems == null) {
            Source listType = getListType();
            populateListItems(listType);
        }

        return listItems;
//        //TODO: only collect pages needed
//        //return pages of results
//        Collection<Map<String,Object>> alist = new ArrayList<>();
//        int pageCounter = 0;
//        for(int i = 0; i < listItems.size(); ++i) {
//            if (i >= pageStart) {
//                alist.add(listItems.get(i));
//                ++pageCounter;
//                if (pageMaximum > 0 && pageCounter == pageMaximum) {
//                    break;
//                }
//            }
//        }

//        return alist;
    }

    protected void populateListItems(Source listType) {
        switch (listType) {
            case STATIC: //SOURCE_STATIC
                populateStaticListItems();
                break;
            case CHILDREN: //SOURCE_CHILDREN
                populateChildListItems(CHILDDEPTH_CHILDREN);
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
                populateChildListItems(childDepth);
                break;
            default:
                listItems = new ArrayList<>();
                break;
        }
        sortListItems();
        setMaxItems();

        componentProperties.put(LIST_ISEMPTY, listItems.isEmpty());

        componentProperties.put("test", "test1");


        isPaginating = listItems.size() > 0 && listItems.size() > pageMaximum;

        componentProperties.put(LIST_ISPAGINATING, isPaginating);

        if (isPaginating) {
            //copied from com.day.cq.wcm.foundation.List.generateId
            String path = getResource().getPath();
            String rootMarker = JcrConstants.JCR_CONTENT.concat("/");
            int root = path.indexOf(rootMarker);
            if (root >= 0) {
                path = path.substring(root + rootMarker.length());
            }
            String start_param = path.replace('/', '_').concat("_start");

            componentProperties.attr.add("data-has-pages", isPaginating);

            int totalPages = 0;
            int itemsPerPage = pageMaximum;

            // When the maximum number of pages is greater than zero, calculate the total number by checking
            // the modulus of each item compared to the total known number of items that can be shown per page.
            if (itemsPerPage > 0) {
                for (int i = 0; i < listItems.size(); i++) {
                    if (i > 0 && i % itemsPerPage == 0) {
                        totalPages++;
                    }
                }
            }

            componentProperties.attr.add("data-total-pages", String.valueOf(totalPages));
            componentProperties.attr.add("data-content-url", getResource().getPath().concat(DEFAULT_EXTENTION));
            componentProperties.attr.add("data-content-start", start_param);



        } else {
            componentProperties.attr.add("data-has-pages", isPaginating);
        }


        //parse list and set items that should force a split in a list
        for (int i=0; i < listItems.size();i++) {
            if ((i + 1) % listSplitEvery == 0) {
                listItems.get(i).put("split",true);
            }
        }
    }


    public boolean includePageInList(Page page, boolean includeInvalid, boolean includeHidden) {
        return (includeHidden || !page.isHideInNav()) && (includeInvalid || page.isValid()) && page.getDeleted() == null;
    }

    @SuppressWarnings("Duplicates")
    private void populateStaticListItems() {
        listItems = new ArrayList<>();
        String[] pagesPaths = componentProperties.get(PN_PAGES, new String[0]);
        for (String path : pagesPaths) {
            Page page = getPageManager().getContainingPage(path);
            if (page != null) {
                if (includePageInList(page,showInvalid,showHidden)) {
                    Map<String,Object> item = new HashMap<>();
                    item.put("page", page);
                    item.putAll(getPageBadgeInfo(page));
                    listItems.add(item);
                }
            }
        }
    }

    private void populateChildListItems(int childDepth) {
        LOGGER.error("loading child list");
        listItems = new ArrayList<>();
        Page rootPage = getRootPage(PN_PARENT_PAGE);
        if (rootPage != null) {
            collectChildren(rootPage.getDepth(), rootPage, childDepth);
        }
    }

    private void collectChildren(int startLevel, Page parent, int childDepth) {
        Iterator<Page> childIterator = parent.listChildren();
        int count = 0;
        while (childIterator.hasNext()) {
            Page child = childIterator.next();

            if (includePageInList(child,showInvalid,showHidden)) {
                Map<String,Object> item = new HashMap<>();
                item.put("page", child);
                item.putAll(getPageBadgeInfo(child));
                listItems.add(item);

                if (child.getDepth() - startLevel < childDepth) {
                    collectChildren(startLevel, child, childDepth);
                }
            }

            //collect only up to a limit
            count++;
            if (limit > 0 && count >= limit) {
                return;
            }

        }
    }

    @SuppressWarnings("Duplicates")
    private void populateTagListItems() {
        listItems = new ArrayList<>();
        String[] tags = getProperties().get(PN_TAGS, new String[0]);
        boolean matchAny = getProperties().get(PN_TAGS_MATCH, TAGS_MATCH_ANY_VALUE).equals(TAGS_MATCH_ANY_VALUE);
        if (ArrayUtils.isNotEmpty(tags)) {
            Page rootPage = getRootPage(PN_TAGS_PARENT_PAGE);
            if (rootPage != null) {

                //TODO: to tag query search with paging
                //tagid=aemdesign-showcase:content-type/asset/image
                //tagid.property=@jcr:content/cq:tags

                TagManager tagManager = getResourceResolver().adaptTo(TagManager.class);
                if (tagManager != null) {
                    RangeIterator<Resource> resourceRangeIterator = tagManager.find(rootPage.getPath(), tags, matchAny);
                    if (resourceRangeIterator != null) {
                        while (resourceRangeIterator.hasNext()) {
                            Page containingPage = getPageManager().getContainingPage(resourceRangeIterator.next());
                            if (containingPage != null) {
                                if (includePageInList(containingPage,showInvalid,showHidden)) {
                                    Map<String,Object> item = new HashMap<>();
                                    item.put("page", containingPage);
                                    item.putAll(getPageBadgeInfo(containingPage));
                                    listItems.add(item);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void populateSearchListItems() {
        listItems = new ArrayList<>();
        if (!StringUtils.isBlank(query)) {
            SimpleSearch search = getResource().adaptTo(SimpleSearch.class);
            if (search != null) {
                search.setQuery(query);
                search.setSearchIn(startIn);
                search.addPredicate(new Predicate("type", "type").set("type", NameConstants.NT_PAGE));
                //TODO: add limits and pages
//                search.setStart(pageStart);
//                search.addPredicate(new Predicate("p.guessTotal", "true")); //guess amount
                if (limit > 0) {
                    search.setHitsPerPage(limit);
                }
                try {
                    collectSearchResults(search.getResult());
                } catch (RepositoryException e) {
                    LOGGER.error("Unable to retrieve search results for query.", e);
                }
            }
        }
    }

    private void populateQueryListItems() {
        listItems = new ArrayList<>();
        if (!StringUtils.isBlank(savedquery)) {


            try {
                if (getRequest().getRequestParameter("q") != null) {
                    String escapedQuery = getRequest().getRequestParameter("q").toString();
                    String unescapedQuery = URLDecoder.decode(escapedQuery, "UTF-8");
                    QueryBuilder queryBuilder = getResourceResolver().adaptTo(QueryBuilder.class);
                    PageManager pm = getResourceResolver().adaptTo(PageManager.class);
                    //create props for query
                    java.util.Properties props = new java.util.Properties();
                    //load query candidate
                    props.load(new ByteArrayInputStream(unescapedQuery.getBytes()));
                    //create predicate from query candidate
                    PredicateGroup predicateGroup = PredicateConverter.createPredicates(props);
                    //TODO: add limits and pages
//                    predicateGroup.add(new Predicate("p.offset","0"));
                    if (limit > 0) {
                        predicateGroup.add(new Predicate("p.limit", Integer.toString(limit)));
                    }
//                    predicateGroup.add(new Predicate("p.guessTotal","true"));
                    boolean allowDuplicates = getProperties().get("allowDuplicates", false);
                    javax.jcr.Session jcrSession = getRequest().getResourceResolver().adaptTo(javax.jcr.Session.class);
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
                    Session session = getResourceResolver().adaptTo(Session.class);
                    QueryBuilder queryBuilder = getResourceResolver().adaptTo(QueryBuilder.class);
                    if (session != null && queryBuilder != null) {
                        try {
                            Query query = queryBuilder.loadQuery(getResource().getPath() + "/" + "savedquery", session);
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

    @SuppressWarnings("Duplicates")
    private void collectSearchResults(SearchResult result) throws RepositoryException {
        for (Hit hit : result.getHits()) {

            Page containingPage = getPageManager().getContainingPage(hit.getResource());
            if (containingPage != null) {
                if (includePageInList(containingPage,showInvalid,showHidden)) {
//                    listItems.add(containingPage);
                    Map<String,Object> item = new HashMap<>();
                    item.put("page", containingPage);
                    item.put("score", hit.getScore());
                    item.putAll(getPageBadgeInfo(containingPage));
                    listItems.add(item);
                }
            }
        }
    }

    private void sortListItems() {
        if (orderBy != null) {
            listItems.sort(new ListSort(orderBy, sortOrder));
            componentProperties.put(LIST_ISORDERED, true);
//            componentProperties.put(LIST_TAG, LIST_TAG_ORDERED);
        }
        componentProperties.put(LIST_TAG, LIST_TAG_UNORDERED);
    }

    private void setMaxItems() {
        if (maxItems != 0) {
            java.util.List<Map<String,Object>> tmpListItems = new ArrayList<>();
            for (Map<String,Object> item : listItems) {
                if (tmpListItems.size() < maxItems) {
                    tmpListItems.add(item);
                } else {
                    break;
                }
            }
            listItems = tmpListItems;
        }
    }


    private Page getRootPage(String fieldName) {
        String parentPath = getProperties().get(fieldName, getResourcePage().getPath());
        return getPageManager().getContainingPage(getResourceResolver().getResource(parentPath));
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


    private enum SortOrder {
        ASC("asc"),
        DESC("desc");

        private String value;

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

    private enum OrderBy {
        TITLE("title"),
        MODIFIED("modified"),
        CREATED("created"),
        SCORE("score"),
        TEMPLATE("template");

        private String value;

        OrderBy(String value) {
            this.value = value;
        }

        public static OrderBy fromString(String value) {
            for (OrderBy s : values()) {
                if (StringUtils.equals(value, s.value)) {
                    return s;
                }
            }
            return null;
        }
    }

//    @SuppressWarnings("Duplicates")
//    private static class ListSort implements Comparator<Page>, Serializable {
//
//
//        private static final long serialVersionUID = 7800223092954898107L;
//        private List.SortOrder sortOrder;
//        private List.OrderBy orderBy;
//
//        ListSort(OrderBy orderBy, SortOrder sortOrder) {
//            this.orderBy = orderBy;
//            this.sortOrder = sortOrder;
//        }
//
//        @Override
//        public int compare(Page item1, Page item2) {
//            int i = 0;
//            if (orderBy == OrderBy.MODIFIED) {
//                // getLastModified may return null, define null to be after nonnull values
//                i = ObjectUtils.compare(item1.getLastModified(), item2.getLastModified(), true);
//            } else if (orderBy == OrderBy.TITLE) {
//                // getTitle may return null, define null to be greater than nonnull values
//                i = ObjectUtils.compare(item1.getTitle(), item2.getTitle(), true);
//            }
//
//            if (sortOrder == SortOrder.DESC) {
//                i = i * -1;
//            }
//            return i;
//        }
//    }

    private static String getPageTemplate(Page page) {
        try {
           return page.getTemplate().getPath();
        } catch (Exception ex) {
            LOGGER.error("getPageTemplate: page does not have a template {}",page);
        }
        return "";
    }

    private static String getPageCreatedDate(Page page) {
        try {
           return page.getProperties().get(JcrConstants.JCR_CREATED,"");
        } catch (Exception ex) {
            LOGGER.error("getPageTemplate: page does not have a template {}",page);
        }
        return "";
    }

    private static double getParseDouble(Object value, double defaultValue) {

        double returnValue = defaultValue;

        if (value == null) {
            return returnValue;
        }

        try {
            returnValue = Double.parseDouble(value.toString());
        } catch (Exception ex) {
            LOGGER.error("getParseDouble: could not parse value [{}] to double returning {}",value,defaultValue);
        }

        return returnValue;

    }

    @SuppressWarnings("Duplicates")
    private static class ListSort implements Comparator<Map<String,Object>>, Serializable {


        private static final long serialVersionUID = 4846499002582007754L;
        private List.SortOrder sortOrder;
        private List.OrderBy orderBy;

        ListSort(List.OrderBy orderBy, SortOrder sortOrder) {
            this.orderBy = orderBy;
            this.sortOrder = sortOrder;
        }

        @Override
        public int compare(Map<String,Object> item1, Map<String,Object> item2) {
            int i = 0;
            Page page1 = (Page)item1.get("page");
            Page page2 = (Page)item2.get("page");
            if (orderBy == List.OrderBy.MODIFIED) {
                // getLastModified may return null, define null to be after nonnull values
                i = ObjectUtils.compare(page1.getLastModified(), page2.getLastModified(), true);
            } else if (orderBy == List.OrderBy.TITLE) {
                // getTitle may return null, define null to be greater than nonnull values
                i = ObjectUtils.compare(page1.getTitle(), page2.getTitle(), true);
            } else if (orderBy == List.OrderBy.TEMPLATE) {
                // getTitle may return null, define null to be greater than nonnull values
                String template1 = getPageTemplate(page1);
                String template2 = getPageTemplate(page2);
                i = ObjectUtils.compare(template1, template2, true);
            } else if (orderBy == List.OrderBy.CREATED) {
                String created1 = getPageCreatedDate(page1);
                String created2 = getPageCreatedDate(page2);
                i = ObjectUtils.compare(created1, created2, true);
            } else if (orderBy == List.OrderBy.SCORE) {
                double score1 = getParseDouble(item1.get("score"),1);
                double score2 = getParseDouble(item2.get("score"),1);
                i = ObjectUtils.compare(score1, score2, true);
            }

            if (sortOrder == SortOrder.DESC) {
                i = i * -1;
            }
            return i;
        }
    }



    private class PageLink {
        private String path;
        private HashMap<String, Object> params;

        public PageLink(SlingHttpServletRequest request) {
            this.path = request.getPathInfo();
            PageManager pm = request.getResourceResolver().adaptTo(PageManager.class);
            Page page = pm.getContainingPage(this.path);
            if (page != null) {

                this.path = page.getPath() + ".html";
            }

            this.initParams(request.getQueryString());
        }

        public void addParameter(String name, Object value) {
            name = this.prefixName(name);
            this.params.put(name, value);
        }

        public void setParameter(String name, Object value) {
            name = this.prefixName(name);
            if (this.params.containsKey(name)) {
                this.params.remove(name);
            }

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