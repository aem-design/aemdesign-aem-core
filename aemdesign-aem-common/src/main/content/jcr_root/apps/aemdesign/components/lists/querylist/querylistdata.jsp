<%@ page import="java.util.Iterator,
				javax.jcr.RepositoryException,
				javax.jcr.Node,
				javax.jcr.query.RowIterator,
				com.day.cq.wcm.api.Page,
				org.slf4j.Logger,
				com.day.cq.wcm.api.PageManager,
				javax.jcr.query.Row,
				javax.jcr.query.Query,
				javax.jcr.query.QueryResult" %>
<%!

    /**
    * Prepare the list instance with the query string and set the page row iterator
    *
    * @param page is the current page
    * @param query is the jcr-sql2 query string to execute
    * @param offset start point
    * @param limit result count
    * @throws RepositoryException
    */
    private Iterator<Page> preparePageIteratorForQuery(Page page, String query, Long offset, Long limit) throws RepositoryException {

        // execute the query
        Query queryInstance =
               page
               .adaptTo(Node.class)
               .getSession().getWorkspace().getQueryManager()
               .createQuery(query, Query.JCR_SQL2);

        if(offset != null) {
        	queryInstance.setOffset(offset);
        }
        
        if(limit != null) {
        	queryInstance.setLimit(limit);	
        }
        
        // get query results
        QueryResult qResult = queryInstance.execute();

        // get row iterator
        final RowIterator rowIterator = qResult.getRows();

        // convert the row iterator into a page iterator
        return rowToPageIterator(page.getPageManager(), rowIterator);
    }


    /**
     * Prepare the list instance with the query string and set the page row iterator
     *
     * @param page is the current page
     * @param list is the list to operate
     * @param query is the jcr-sql2 query string to execute
     * @throws RepositoryException
     */
    private void prepareListWithQuery(Page page, com.day.cq.wcm.foundation.List list, String query) throws RepositoryException {
    	prepareListWithQuery(page, list, query, null, null);
    }

    private void prepareListWithQuery(Page page, com.day.cq.wcm.foundation.List list, String query, Long offset, Long limit) throws RepositoryException {
    	list.setQuery(query, Query.JCR_SQL2);
        Iterator<Page> pageIterator = preparePageIteratorForQuery(page, query, offset, limit);
        list.setPageIterator(pageIterator);
    }

    
    /**
     * This iterator translates between a RowIterator from a JCR SQL2 query which may have multiple node selections
     * attached to it, to a page iterator that is used to iterate through the List component's results.
     *
     * @param _pageManager Page manager to query for pages
     * @param rowIterator is the rowIterator to go through
     * @return a page iterator instance to uses the row iterator to cycle through the results
     */
    private Iterator<Page> rowToPageIterator(final PageManager _pageManager, final RowIterator rowIterator) {
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
                    return null;
                }
            }

            public void remove() {
                throw new UnsupportedOperationException("not removing stuff");
            }
        };
    }


    /**
     * Concatenate two page iterators and wrap them in a new page iterator so that we can
     * make it look like the first and second query are one.
     *
     * @param head is the first iterator
     * @param tail is the second iterator (which itself could be a concatenated iterator)
     * @return
     */
    private Iterator<Page> concatIterators(final Iterator<Page> head, final Iterator<Page> tail) {

        return new Iterator<Page>() {

            private Iterator<Page> current = head;

            public boolean hasNext() {
                if (!current.hasNext() && current == head) {
                    current = tail;
                }
                return current.hasNext();
            }

            public Page next() {
                return current.next();
            }

            public void remove() {
                throw new UnsupportedOperationException("not removing stuff");
            }
        };
    }

%>