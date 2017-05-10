<%@ page import="java.util.HashMap" %><%@
    page import="java.util.Calendar" %><%@
    page import="java.text.SimpleDateFormat" %><%@
    page import="java.util.ArrayList" %><%@
    page import="javax.jcr.query.*" %><%@
    page import="java.util.GregorianCalendar" %><%!

    /**
     * Last modified property
     */
    private static final String PROPERTY_LAST_MODIFIED = "cq:lastModified";

    /**
     * Expression that is always true and can fit inside parentheses
     */
    private static final String EMPTY_RETURN_QUERY = "1 = 1";

    /**
     *  Default sitemap time format
     */
    private static final String SITEMAP_FORMAT = "yyyy-MM-dd";

    /**
     * Create a map with page information for in the sitemap
     *
     * @param page is the page to create a map for
     * @return the map information
     */
    protected Map newPage(Node page, Node jcrContent) throws RepositoryException {
        if (!jcrContent.hasProperty(PROPERTY_LAST_MODIFIED)) {
            return null;
        }

        Map<String, String> info = new HashMap<String, String>();
        Calendar lastModCal = null;

        // apparently not all dates are formatted properly
        // if not, just return today.
        try {
            lastModCal = jcrContent.getProperty(PROPERTY_LAST_MODIFIED).getDate();
        }
        catch (ValueFormatException vfEx) {
            lastModCal = GregorianCalendar.getInstance();
        }

        SimpleDateFormat sdFormat = new SimpleDateFormat(SITEMAP_FORMAT);

        info.put("location", escapeBody(mappedUrl(page.getPath()).concat(".html")));
        info.put("lastModified", sdFormat.format(lastModCal.getTime()));

        return info;
    }

    /**
     *
     * @param roots
     * @param qManager
     * @return
     * @throws RepositoryException
     * @deprecated
     * need to use predicates
     */
    @Deprecated /* NEED TO USE PREDICATES */
    private String getHideThesePathsQuery(String[] roots, QueryManager qManager) throws RepositoryException {
        String descendantsOf = getSitemapRootFilters(roots);

        //TODO: convert to predicates
        // get all hidden pages query
        String hideBeginningWithQuery =
                "SELECT * FROM [cq:Page] as page " +
                "INNER JOIN [cq:PageContent] as pageContent ON ISCHILDNODE(pageContent,page) " +
                "WHERE " +
                    "pageContent.[hideInNav] = 'true' AND " +
                    "(" + descendantsOf + ")";

        return hideBeginningWithQuery;
    }


    /**
     * This method sets up a piece of the query that will
     *
     * @return an SQL query for roots
     */
    protected String getSitemapRootFilters(String[] roots) {
        if (roots == null || roots.length == 0) {
            return null;
        }

        int idx = 0;
        int processed = 0;
        StringBuilder strB = new StringBuilder();

        // wrap in parenthesis
        strB.append("(");

        for (String root : roots) {

            ++idx;

            // is blank?
            if (StringUtils.isBlank(root)) {
                continue;
            }

            // add criteria for root element
            strB.append("ISDESCENDANTNODE(page,[").append(escapeJcrSql(root)).append("])");

            ++processed;

            // need to separate with "OR" ?
            if (idx != roots.length) {
                strB.append(" OR ");
            }
        }

        strB.append(")");

        if (processed == 0) {
            return EMPTY_RETURN_QUERY;
        }

        return strB.toString();
    }


    /**
     *
     * @param exclusionBasePaths
     * @return
     * @deprecated
     * need to use predicates
     */
    @Deprecated /* NEED TO USE PREDICATES */
    protected String getExclusionExpression(String[] roots, List<String> exclusionBasePaths) throws RepositoryException {

        String descendantsOf = getSitemapRootFilters(roots);


        // start building the query
        int idx = 0;
        StringBuilder strB = new StringBuilder();

        // actually has exclusions?
        if (exclusionBasePaths.size() > 0) {
            strB.append(" AND NOT (");
            for (String basePath : exclusionBasePaths) {

                // starswith basePath?
                strB.append("ISDESCENDANTNODE(page,[").append(escapeJcrSql(basePath)).append("])")
                    .append(" OR ")
                    .append("ISSAMENODE(page,[").append(escapeJcrSql(basePath)).append("])");
                ++idx;

                // not last one? add separator
                if (idx != exclusionBasePaths.size()) {
                    strB.append(" OR ");
                }
            }
            strB.append(")");
        }

        //TODO: convert to predicates
        // get all visible pages query
        String allVisiblePagesQuery =
                "SELECT * FROM [cq:Page] as page " +
                "INNER JOIN [cq:PageContent] as pageContent ON ISCHILDNODE(pageContent,page) " +
                        "WHERE " +
                        "(" + descendantsOf + ")" +
                        strB.toString();

        return allVisiblePagesQuery;
    }


    /**
     * Create a list of base paths from the query result of the Hide query
     *
     * @param queryResult is the query result
     * @return a list of base paths of the pages that have "hide in navigation" setup
     * @throws RepositoryException when cannot retrieve node etc.
     */
    protected List<String> getFilteredBasePathsFromResult(QueryResult queryResult) throws RepositoryException {

        if (queryResult == null) {
            return null;
        }

        List<String> basePaths = new ArrayList<String>();
        RowIterator rIterator = queryResult.getRows();

        // iterate through each element
        while (rIterator.hasNext()) {
            Row row = rIterator.nextRow();
            if (row == null) {
                continue;
            }

            Node pageNode = row.getNode("page");
            basePaths.add(pageNode.getPath());
        }

        return basePaths;
    }


    /**
     * Execute query and parse results into a sitemap list
     *
     * @param pagesQuery is the query to execute
     * @return a list of sitemap map information
     * @throws RepositoryException
     */
    private List<Map> getSitemapListFromQuery(Query pagesQuery) throws RepositoryException {
        QueryResult pagesResults = pagesQuery.execute();

        RowIterator rowIterator = pagesResults.getRows();
        List<Map> sitemap = new ArrayList<Map>();
        while (rowIterator.hasNext()) {

            // get node for page that belongs to this row
            Row pageRow = rowIterator.nextRow();
            Node pageNode = pageRow.getNode("page");
            Node jcrContentNode = pageNode.getNode(JcrConstants.JCR_CONTENT);

            // convert to map and store if succesful.
            Map sitemapPage = newPage(pageNode, jcrContentNode);

            if (sitemapPage != null) {
                sitemap.add(sitemapPage);
            }
        }

        return sitemap;
    }

    /**
     * Get a list of sitemap pages
     *
     * @param roots are a list of paths to roots we want to scan for
     * @return
     */
    public List<Map> getSitemapPages(Page currentPage, String[] roots) throws RepositoryException {

        // retrieve the query manager
        QueryManager qManager = currentPage.adaptTo(Node.class).getSession().getWorkspace().getQueryManager();

        // determine which paths not to read sitemap from
        String hideQuery = this.getHideThesePathsQuery(roots, qManager);
        Query hideQueryInstance = qManager.createQuery(hideQuery, Query.JCR_SQL2);
        QueryResult hideQueryResult = hideQueryInstance.execute();

        // get results
        List<String> basePathList = this.getFilteredBasePathsFromResult(hideQueryResult);
        if (basePathList == null) {
            return null;
        }

        // get visible pages query
        String excludedQuery = this.getExclusionExpression(roots, basePathList);
        Query pagesQuery = qManager.createQuery(excludedQuery, Query.JCR_SQL2);

        return this.getSitemapListFromQuery(pagesQuery);
    }
%>