package design.aem.models.v2.lists;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.ResultPage;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import design.aem.components.ComponentProperties;
import design.aem.models.ModelProxy;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static design.aem.models.v2.lists.List.SortOrder;
import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.TagUtil.TAG_VALUE;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class TagList extends ModelProxy {
    protected static final Logger LOGGER = LoggerFactory.getLogger(TagList.class);

    protected ComponentProperties componentProperties = null;
    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }

    private java.util.List<Map<String,Object>> listItems;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(intValues = LIMIT_DEFAULT)
    private int limit;

    private static final String PN_SOURCE = "listFrom"; //SOURCE_PROPERTY_NAME
    private static final String PN_SOURCE_DEFAULT = Source.STATIC.getValue(); //SOURCE_PROPERTY_NAME
    private static final String PARENT_TAG = "parentTag";
    private static final String STATIC_TAGS = "tags";
    private static final String DESCENDANT_TAG = "ancestorTag";
    private static final String PARENT_TAG_DEFAULT = "/content/cq:tags";
    private static final String LIMIT_PROPERTY_NAME = "limit"; //TAGS_MATCH_PROPERTY_NAME
    private static final int LIMIT_DEFAULT = 100;
    private static final String PN_SORT_ORDER = "sortOrder";
    private static final String PN_ORDER_BY = "orderBy";
    private static final String PN_ORDER_BY_DEFAULT = "path";
    private static final String LIST_ISEMPTY = "isEmpty";

    private long totalMatches;
    private long hitsPerPage;
    private long totalPages;
    private long pageStart;
    private long currentPage;
    private java.util.List<ResultPage> resultPages;
    private SortOrder sortOrder;

    protected void ready() {
        //COMPONENT STYLES
        // {
        //   1 required - property name,
        //   2 required - default value,
        //   3 optional - name of component attribute to add value into
        //   4 optional - canonical name of class for handling multivalues, String or Tag
        // }
        setComponentFields(new Object[][]{
                {FIELD_VARIANT, DEFAULT_VARIANT},
                {"displayType", StringUtils.EMPTY},
                {"emptyOption", true},
                {"emptyOptionTitle", "Select"},
                {"filter", false, "filter"},
                {"filterDefaults", StringUtils.EMPTY, "filter-defaults"},
                {"filterTopic", StringUtils.EMPTY, "filter-topic"},
                {STATIC_TAGS, new String[0]},
                {DESCENDANT_TAG, PARENT_TAG_DEFAULT},
                {PN_SOURCE, PN_SOURCE_DEFAULT},
                {PARENT_TAG, PARENT_TAG_DEFAULT},
                {PN_ORDER_BY, StringUtils.EMPTY},
                {LIMIT_PROPERTY_NAME, LIMIT_DEFAULT},
                {PN_SORT_ORDER, SortOrder.ASC.getValue()},
        });

        componentProperties = ComponentsUtil.getComponentProperties(
                this,
                componentFields,
                DEFAULT_FIELDS_STYLE,
                DEFAULT_FIELDS_ACCESSIBILITY,
                DEFAULT_FIELDS_ANALYTICS);

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
    protected void populateListItems(Source listType) {
//        LOGGER.error("populateListItemsL: listType={}",listType);
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
        String path = componentProperties.get(PARENT_TAG,PARENT_TAG_DEFAULT);
        populateChildListItems(path, true);
    }


    /**
     * populate list items from descendants of a root page.
     */
    private void populateDescendantsListItems() {
        String path = componentProperties.get(DESCENDANT_TAG,PARENT_TAG_DEFAULT);
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
        childMap.put("type", "cq:Tag");

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

//            LOGGER.error("populateListItemsFromMap: running query with map=[{}]",map);

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
        String[] tags = componentProperties.get(STATIC_TAGS, new String[0]);
        ResourceResolver resourceResolver = getResourceResolver();
        TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
        if (tagManager != null) {
            for (String tagId : tags) {
                Map<String, Object> item = new HashMap<>();

                Tag tag = tagManager.resolve(tagId);

                if (tag != null) {
                    item.put("tag", tag);
                    Resource tagResource = resourceResolver.resolve(tag.getPath());
                    if (tagResource != null) {
                        ValueMap tagValues = tagResource.getValueMap();
                        if (tagValues != null) {
                            item.put(TAG_VALUE, tagValues.get(TAG_VALUE));
                        }
                    }

                } else {
                    LOGGER.error("populateStaticListItems: could not find tagId {}", tagId);
                    continue;
                }

                listItems.add(item);
            }
        } else {
            LOGGER.error("populateStaticListItems: could not get TagManager object");
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

        for (Hit hit : result.getHits()) {
            Map<String,Object> item = new HashMap<>();
            item.put("hit", hit);

            Resource tagResource = hit.getResource();
            Tag tag = tagResource.adaptTo(Tag.class);


            if (tag != null) {
                item.put("tag", tag);

                ValueMap tagValues = tagResource.getValueMap();
                if (tagValues != null && tagValues.containsKey(TAG_VALUE)) {
                    item.put(TAG_VALUE, tagValues.get(TAG_VALUE));
                }
            }

            listItems.add(item);
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