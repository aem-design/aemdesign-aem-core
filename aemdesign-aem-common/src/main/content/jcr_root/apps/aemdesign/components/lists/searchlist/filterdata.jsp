<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="java.util.Date" %>
<%!


    /**
     * Build the entire query
     *
     * @param request is the http servlet request we get to retrieve parameters from
     * @param componentName is the details component name we want to find the pages for
     * @param dateColumn is the date column to order by and to filter on
     * @return a search query we can execute
     * @deprecated
     * need to use predicates
     */
    @Deprecated /* NEED TO USE PREDICATES */
    protected String buildSearchQuery(HttpServletRequest request, String searchIn, String componentName, String dateColumn) {

        if (!searchIn.startsWith("/")) {
            throw new IllegalArgumentException("Make sure you setup your content path properly");
        }

        /// get the search box clauses
        String searchClauses = buildSearchClause(request, dateColumn);

        //TODO: convert to predicates
        // list query
        String query =
                "SELECT * FROM [cq:Page] AS parent\n" +
                "INNER JOIN [cq:PageContent] AS pageContent ON ISCHILDNODE(pageContent, parent)\n" +
                "INNER JOIN [nt:unstructured] AS child on ISDESCENDANTNODE(child, pageContent)\n" +
                "WHERE\n" +
                    "ISDESCENDANTNODE(parent, [" + escapeJcrSql(searchIn) + "]) AND\n" +
                    "child.[sling:resourceType] = 'aemdesign/components/content/" + componentName + "'\n" +
                    searchClauses +
                "ORDER BY child.[" + dateColumn + "] DESC";

        return query;
    }

    /**
     * Build a search clause using the request parameters set by the search box
     *
     * @param request is the request instance to read from
     * @param dateColumn is the date column
     * @return the search box filter clauses
     */
    protected String buildSearchClause(HttpServletRequest request, String dateColumn) {

        StringBuilder searchClauses = new StringBuilder();

        // add date filter
        String dateFilter =
                getDateFilter(
                    getIntegerRequestParameter(request, "month"),
                    getIntegerRequestParameter(request, "year")
                );

        if (dateFilter != null) {
            searchClauses.append("AND child.[" + dateColumn + "] LIKE '" + escapeJcrSql(dateFilter) + "'\n");
        }

        // add search keywords filter
        String searchFilter = getSearchFilter(request.getParameter("query"));
        if (searchFilter != null) {
            searchClauses.append("AND (" + searchFilter + ")\n");
        }

        // add category filter
        String categoryFilter = getCategoryFilter(request.getParameter("category"));
        if (categoryFilter != null) {
            searchClauses.append("AND (" + categoryFilter + ")\n");
        }

        return searchClauses.toString();
    }

    /**
     * Get the max number of items to display on one page from the page parameter,
     * but constrain it between 20 and 100 to make sure we don't overload the server.
     *
     * @param maxItemsParam is the input parameter
     * @return a value between 20 and 100, 20 if the parameter was empty or not a number
     */
    private int getMaxItemsCount(String maxItemsParam) {
        int maxItems = 20;
        if (maxItemsParam != null) {

            try {
                maxItems = (int)
                        Math.min(100,
                        Math.max(20,
                                Integer.parseInt(maxItemsParam, 10)));
            }
            catch (NumberFormatException nfEx) {
                // shouldn't change defaultMaxItems, will still be 20.
            }
        }

        return maxItems;
    }

    /**
     * Retrieve a request parameter and return a number, or null when it's not a proper integer
     *
     * @param request is the request ot use
     * @param paramName is the name of the parameter
     * @return an integer when available or null.
     */
    private Integer getIntegerRequestParameter(HttpServletRequest request, String paramName) {
        String strParam = request.getParameter(paramName);
        if (StringUtils.isBlank(strParam)) {
            return null;
        }
        try {
            return Integer.parseInt(strParam);
        }
        catch (NumberFormatException nfEx) {
            return null;
        }
    }

    /**
     * Get the LIKE term value for the news date.
     *
     * @param month is the month we want to search for, can be null
     * @param year is the year we want to search for, can be null
     * @return the expression we use in the LIKE operator
     */
    private String getDateFilter(Integer month, Integer year) {

        // year empty when month set, default to current year
        if (month != null && year == null) {
            year = new Date().getYear() + 1900;
        }

        if (year != null && month != null) {
            return String.format("%d-%02d-%%", year, month);
        }
        else if (year != null) {
            return year + "-%";
        }
        else {
            return null;
        }

    }

    /**
     * @return the search query to search inside the news detail element for certain text matches
     */
    private String getSearchFilter(String queryParam) {
        if (StringUtils.isBlank(queryParam)) {
            return null;
        }

        return
            String.format(
                "LOWER(child.[title]) LIKE '%%%s%%' OR " +
                "LOWER(child.[summary]) LIKE '%%%s%%' OR " +
                "LOWER(pageContent.[jcr:title]) LIKE '%%%s%%' OR " +
                "LOWER(pageContent.[jcr:description]) LIKE '%%%s%%'",
                escapeJcrSql(queryParam).toLowerCase(),
                escapeJcrSql(queryParam).toLowerCase(),
                escapeJcrSql(queryParam).toLowerCase(),
                escapeJcrSql(queryParam).toLowerCase()
            );
    }


    /**
     * @return the category filter if one was specified
     */
    private String getCategoryFilter(String categoryParam) {
        if (StringUtils.isBlank(categoryParam)) {
            return null;
        }

        return String.format(
                "LOWER(child.[tags]) LIKE '%%:%s'",
                escapeJcrSql(categoryParam).toLowerCase()
        );
    }
%>