<%@ page import="com.day.cq.wcm.api.Page" %>
<%@ page import="com.day.cq.wcm.api.PageManager" %>
<%@ page import="com.day.cq.wcm.api.components.ComponentContext" %>
<%@ page import="org.slf4j.Logger" %>
<%@ page import="javax.jcr.query.Row" %>
<%@ page import="javax.jcr.query.RowIterator" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.List" %>

<%!
    /**
     * This iterator translates between a RowIterator from a JCR SQL2 query which may have multiple node selections
     * attached to it, to a page iterator that is used to iterate through the List component's results.
     *
     * @param _log logger.
     * @param _pageManager Page manager to query for pages
     * @param rowIterator is the rowIterator to go through
     * @return a page iterator instance to uses the row iterator to cycle through the results
     */
    private Iterator<Page> rowToPageIterator(final Logger _log, final PageManager _pageManager, final RowIterator rowIterator) {
        return new Iterator<Page>() {

            public boolean hasNext() {
                return rowIterator.hasNext();
            }

            public Page next() {
                Row row = rowIterator.nextRow();
                try {
                    return _pageManager.getPage(row.getNode("parent").getPath());
                }
                catch (Exception ex) {
                    _log.info("Couldn't parent from row result");
                    return null;
                }
            }

            public void remove() {
                throw new UnsupportedOperationException("not removing stuff");
            }
        };
    }


    private String getComponentPath(ResourceResolver _resourceResolver, ComponentContext _componentContext){

        String path = StringUtils.EMPTY;

        if (_componentContext != null && _componentContext.getResource() != null){

            String componentPath = _componentContext.getResource().getPath();

            path = _resourceResolver.map(componentPath) + com.day.cq.wcm.foundation.List.URL_EXTENSION;

        }
        return path;
    }

    private String generatePrefix(ComponentContext _componentContext, Page _currentPage, long timestamp){
        return generatePrefix( _componentContext,  _currentPage) + "_" + timestamp;
    }


    private String generatePrefix(ComponentContext _componentContext, Page _currentPage){

        String prefix = StringUtils.EMPTY;

        String componentPath = _componentContext.getResource().getPath();

        String pageResourcePath = _currentPage.getContentResource().getPath();

        prefix = componentPath.substring(pageResourcePath.length());

        if (prefix.indexOf("/") == 0){
            prefix = prefix.substring(1);
        }

        prefix = prefix.replace("/", "_");


        return prefix;
    }


    /***
     * generate a query string from map
     * @param queryStringMap
     * @return
     */
    private String generateQueryString(Map<String, String> queryStringMap){
        String queryString = StringUtils.EMPTY;

        for (Map.Entry<String, String> parameter : queryStringMap.entrySet()) {

            final String encodedKey = URLEncoder.encode(parameter.getKey());
            final String encodedValue = URLEncoder.encode(parameter.getValue());

            if (isEmpty(queryString)) {
                queryString = encodedKey + "=" + encodedValue;
            } else {
                queryString += "&" + encodedKey + "=" + encodedValue;
            }
        }

        return queryString;
    }

%>