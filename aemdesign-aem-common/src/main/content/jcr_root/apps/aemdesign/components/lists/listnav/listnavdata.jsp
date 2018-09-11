<%@ page import="java.text.SimpleDateFormat" %>

<%@ page import="com.day.cq.search.result.Hit" %>
<%@ page import="com.day.cq.search.result.SearchResult" %>
<%@ page import="com.day.cq.search.*" %>
<%@ page import="com.day.cq.search.Query" %>
<%@ page import="com.day.cq.wcm.api.Page" %>


<%!
    private static final String FIELD_PAGE_TYPE_TAG = "jcr:content/cq:tags";

    private static final String NODE_ARTICLE_PAR = "jcr:content/article/par";

    private static final String FIELD_DETAIL_PAGE_PATTERN = NODE_ARTICLE_PAR + "/{0}/{1}";

    private static final String DATE_SUFFIX ="T00:00:00.000+08:00";

    /**
     * Retrieve the next pages including Page having the same publish Date
     * @param searchInPage
     * @param pageList
     * @param filterPage
     * @param numOfFetchItems
     * @param queryBuilder
     * @param jcrSession
     * @param log
     * @return
     * @throws RepositoryException
     */
    private List<Page> getNextPages(Page searchInPage, List<Page> pageList, Page filterPage, int numOfFetchItems, QueryBuilder queryBuilder, Session jcrSession, Logger log) throws RepositoryException{

        final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");

        final String ORDERING = "asc";
        final String DATERANGE_BOUND = "lowerBound";
        final String DATERANGE_OPERATOR = "lowerOperation";
        final String DATERANGE_OPERATOR_VALUE =">=";
        final String DATE_FIELD_NAME = "publishDate";

        Map<String, String> map = new HashMap<String, String>();

        map.put("path", searchInPage.getPath());

        map.put("type", NameConstants.NT_PAGE);

        //TAG SEARCH
        String[] tags = filterPage.getContentResource().getValueMap().get(TagConstants.PN_TAGS, new String[]{});
        if (tags.length > 0 ) {
            map.put("tagid.property", FIELD_PAGE_TYPE_TAG);
            map.put("tagid", tags[0]);
        }

        //DateRange Predicate
        Node node = findDetailNode(filterPage);

        int identifiableNodeCount = 0;

        if (node != null) {
            //Fetch the same Node count
            identifiableNodeCount = identifiableNodeCount(DATE_FIELD_NAME, node, log, queryBuilder, jcrSession, SDF, searchInPage);

            //Append DateRange
            this.appendOnDateRange(DATE_FIELD_NAME, map, node, DATERANGE_BOUND, DATERANGE_OPERATOR, DATERANGE_OPERATOR_VALUE, SDF);

            //Append filter for standard field such as On Off Time
            this.appendOnOfftimeFilter(map);

        }

        map.put("orderby.index", "true");
        map.put("orderby.sort", ORDERING); // same as query.setStart(0) below

         //can be done in map or with Query methods
        map.put("p.offset", "0"); // same as query.setStart(0) below
        map.put("p.limit", String.valueOf(numOfFetchItems + identifiableNodeCount)); // same as query.setHitsPerPage(20) below

        if(log.isInfoEnabled()){
            log.info("Search Critiria Map [" + map + "]");
        }

        return getListNav(PredicateConverter.createPredicates(map), pageList, queryBuilder, log, jcrSession, filterPage);
    }

    /**
     * Retrieve the previous pages including Page having the same publish Date
     * @param searchInPage
     * @param pageList
     * @param filterPage
     * @param numOfFetchItems
     * @param queryBuilder
     * @param jcrSession
     * @param log
     * @return
     * @throws RepositoryException
     */
    private List<Page> getPreviousPages(Page searchInPage, List<Page> pageList, Page filterPage, int numOfFetchItems, QueryBuilder queryBuilder, Session jcrSession, Logger log) throws RepositoryException {

        final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");

        final String ORDERING = "desc";
        final String DATERANGE_BOUND = "upperBound";
        final String DATERANGE_OPERATOR = "upperOperation";
        final String DATERANGE_OPERATOR_VALUE =">=";
        final String DATE_FIELD_NAME = "publishDate";

        Map<String, String> map = new HashMap<String, String>();

        map.put("path", searchInPage.getPath());

        map.put("type", NameConstants.NT_PAGE);

        //TAG SEARCH
        String[] tags = filterPage.getContentResource().getValueMap().get(TagConstants.PN_TAGS, new String[]{});
        if (tags.length > 0 ) {
            map.put("tagid.property", FIELD_PAGE_TYPE_TAG);
            map.put("tagid", tags[0]);
        }


        //DateRange Predicate

        Node node = findDetailNode(filterPage);

        int identifiableNodeCount = 0 ;

        if (node != null) {
            //Fetch the same Node count
            identifiableNodeCount = identifiableNodeCount(DATE_FIELD_NAME, node, log, queryBuilder, jcrSession, SDF, searchInPage);

            //Append DateRange
            this.appendOnDateRange(DATE_FIELD_NAME, map, node, DATERANGE_BOUND, DATERANGE_OPERATOR, DATERANGE_OPERATOR_VALUE, SDF);

            //Append filter for standard field such as On Off Time
            this.appendOnOfftimeFilter(map);

        }

        map.put("orderby.index", "true");
        map.put("orderby.sort", ORDERING); // same as query.setStart(0) below

        //can be done in map or with Query methods
        map.put("p.offset", "0"); // same as query.setStart(0) below
        map.put("p.limit", String.valueOf(numOfFetchItems + identifiableNodeCount)); // same as query.setHitsPerPage(20) below

        if (log.isInfoEnabled()) {
            log.info("Search Critiria Map [" + map + "]");
        }

        return getListNav(PredicateConverter.createPredicates(map), pageList, queryBuilder, log, jcrSession, filterPage);
    }

    private List<Page> getPagesByPath(Page searchInPage, List<Page> pageList, Page filterPage, QueryBuilder queryBuilder, Session jcrSession, Logger log) throws RepositoryException {
        final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");

        final String ORDERING = "desc";
        final String DATE_FIELD_NAME = "publishDate";

        Map<String, String> map = new HashMap<String, String>();

        map.put("path", searchInPage.getPath());

        map.put("type", NameConstants.NT_PAGE);

        //DateRange Predicate

        Node node = findDetailNode(filterPage);

        int identifiableNodeCount = 0 ;

        if (node != null) {
            //Fetch the same Node count
            identifiableNodeCount = identifiableNodeCount(DATE_FIELD_NAME, node, log, queryBuilder, jcrSession, SDF, searchInPage);

            //Append DateRange
            if (node.hasProperty(DATE_FIELD_NAME)){
                String fieldName = MessageFormat.format(FIELD_DETAIL_PAGE_PATTERN, node.getName(), DATE_FIELD_NAME);
                map.put("2_orderby", "@" + fieldName); // same as query.setStart(0) below
                map.put("2_orderby.sort", ORDERING);
                map.put("3_orderby", "@jcr:content/jcr:created" ); // same as query.setStart(0) below
                map.put("3_orderby.sort", ORDERING);

            }else{
                map.put("orderby", "@jcr:content/jcr:created" ); // same as query.setStart(0) below
                map.put("orderby.sort", ORDERING);
            }
            //Append filter for standard field such as On Off Time
            this.appendOnOfftimeFilter(map);

        }


        if (log.isInfoEnabled()) {
            log.info("Search Critiria Map [" + map + "]");
        }

        return getListNav(PredicateConverter.createPredicates(map), pageList, queryBuilder, log, jcrSession, filterPage);


    }

        /**
         * Append criteria for the Date Range
         * @param dateField
         * @param node
         * @param log
         * @param queryBuilder
         * @param jcrSession
         * @param sdf
         * @param searchInPage
         * @return
         * @throws RepositoryException
         */
    private int identifiableNodeCount(String dateField, Node node, Logger log, QueryBuilder queryBuilder, Session jcrSession, SimpleDateFormat sdf, Page searchInPage) throws RepositoryException{

        Map<String, String> mapSize = new HashMap<String, String>();

        mapSize.put("type", NameConstants.NT_PAGE);
        mapSize.put("path", searchInPage.getPath());

        if (node.hasProperty(dateField)){

            String fieldName = MessageFormat.format(FIELD_DETAIL_PAGE_PATTERN, node.getName(), dateField);
            Calendar publishDate = node.getProperty(dateField).getDate();

            mapSize.put("property.value", sdf.format(publishDate.getTime()) + DATE_SUFFIX);
            mapSize.put("property",fieldName);

        }
        //Append filter for standard field such as On Off Time
        this.appendOnOfftimeFilter(mapSize);

        return getFilterSize(PredicateConverter.createPredicates(mapSize), queryBuilder, log, jcrSession, searchInPage.getPageManager());
    }

    /**
     * Append criteria for the Date Range
     * @param map
     * @param node
     * @param dateRangeBound
     * @param dateRangeOperator
     * @param dateRangeOperatorValue
     * @param sdf
     * @throws RepositoryException
     */
    private void appendOnDateRange(String dateField, Map<String, String> map, Node node, String dateRangeBound, String dateRangeOperator, String dateRangeOperatorValue, SimpleDateFormat sdf) throws RepositoryException{

        if (node.hasProperty(dateField)){

            String fieldName = MessageFormat.format(FIELD_DETAIL_PAGE_PATTERN, node.getName(), dateField);
            Calendar publishDate = node.getProperty(dateField).getDate();
            map.put("daterange." + dateRangeBound, sdf.format(publishDate.getTime()));
            map.put("daterange.property", fieldName);
            map.put("daterange." + dateRangeOperator, dateRangeOperatorValue);
            map.put("orderby", "@" + fieldName); // same as query.setStart(0) below
            map.put("orderby.sort", "desc");

        }
    }

    /**
     * Standard criteria to filter out invalid page
     * It is the same as PageFilter(boolean includeInvalid, boolean includeHidden)
     * @param map
     */
    private void  appendOnOfftimeFilter(Map<String, String> map){

        map.put("1_group.p.and", "true");

        map.put("1_group.1_group.p.or", "true");
        map.put("1_group.1_group.1_property", "jcr:content/onTime");
        map.put("1_group.1_group.1_property.operation", "not");
        map.put("1_group.1_group.2_relativedaterange.property", "jcr:content/onTime");
        map.put("1_group.1_group.2_relativedaterange.upperBound", "0d");


        map.put("1_group.2_group.p.or", "true");
        map.put("1_group.2_group.1_property", "jcr:content/offTime");
        map.put("1_group.2_group.1_property.operation", "not");
        map.put("1_group.2_group.2_relativedaterange.property", "jcr:content/offTime");
        map.put("1_group.2_group.2_relativedaterange.lowerBound", "0d");

        map.put("1_group.3_group.p.or", "true");
        map.put("1_group.3_group.1_property", "jcr:content/hideInNav");
        map.put("1_group.3_group.1_property.operation", "not");
        map.put("1_group.3_group.1_property", "jcr:content/hideInNav");
        map.put("1_group.3_group.1_property.value", "false");

    }

    private List<Page> getListNav(PredicateGroup pred, List<Page> pageList, QueryBuilder queryBuilder, Logger log, Session jcrSession, Page filterPage){


        Query query = queryBuilder.createQuery(pred, jcrSession);

        SearchResult result = query.getResult();

        log.info("Total resultset count : "    + result.getTotalMatches());

        try {
            // iterating over the results
            for (Hit hit : result.getHits()) {
                Page page = filterPage.getPageManager().getPage(hit.getPath());
                if (pageList.contains(page)==false){
                    pageList.add(page);
                }
            }
        } catch (Exception e) {
            log.error("Failed to " + e.getMessage(), e);
        }
        return pageList;
    }

    private int getFilterSize(PredicateGroup pred, QueryBuilder queryBuilder, Logger log, Session jcrSession, PageManager pm){

        Query query = queryBuilder.createQuery(pred, jcrSession);

        SearchResult result = query.getResult();

        log.info("FilterSize count : "    + result.getTotalMatches());

        return new Long(result.getTotalMatches()).intValue();

    }

%>