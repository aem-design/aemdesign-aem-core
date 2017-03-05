<%
    // This is here to stop statically defined pages from not appearing when 'Hide in Navigation' is set.
    String listType = _properties.get("listFrom", (String) null);
    boolean allowHidden = com.day.cq.wcm.foundation.List.SOURCE_STATIC.equals(listType);

    com.day.cq.wcm.foundation.List list = new com.day.cq.wcm.foundation.List(_slingRequest, new PageFilter(false, allowHidden));

    //for Children list set the current page as the Parent Page if the property is not set
    if (com.day.cq.wcm.foundation.List.SOURCE_CHILDREN.equals(_properties.get(com.day.cq.wcm.foundation.List.SOURCE_PROPERTY_NAME, com.day.cq.wcm.foundation.List.SOURCE_CHILDREN))) {
        String parentPage = _properties.get(com.day.cq.wcm.foundation.List.PARENT_PAGE_PROPERTY_NAME,"");
        if (isEmpty(parentPage)) {
            list.setStartIn(resource.getPath());
        }
    }

    request.setAttribute("list", list);

    String strItemLimit = _properties.get(com.day.cq.wcm.foundation.List.LIMIT_PROPERTY_NAME, (String) null);
    String strPageItems = _properties.get(com.day.cq.wcm.foundation.List.PAGE_MAX_PROPERTY_NAME, (String) null);

    // no limit set, but pagination enabled, set limit to infinite
    if (StringUtils.isBlank(strItemLimit) && !StringUtils.isBlank(strPageItems)) {
        list.setLimit(Integer.MAX_VALUE);
    }

    //
    // Determine whether to replace the ROOT path inside the query
    //

    boolean customType = "true".equals(_properties.get("customType", (String) null));
    String query = _properties.get("query", (String) null);
    String searchIn = _properties.get("searchIn", (String) null);

    //TOOD: Needs to be updated to Predicates

    // if we need to manually make this query better, let's do so.
    if (customType && "search".equals(listType) && query != null && searchIn != null) {

        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");
        String nowDate = sdFormat.format(GregorianCalendar.getInstance().getTime());

        query = query.replaceAll("%NOW%", nowDate);
        query = query.replaceAll("%ROOT%", searchIn);

        list.setQuery(query, Query.JCR_SQL2);

        // execute the query
        Query queryInstance =
                _currentPage
                        .adaptTo(Node.class)
                        .getSession().getWorkspace().getQueryManager()
                        .createQuery(query, Query.JCR_SQL2);

        QueryResult qResult = queryInstance.execute();
        final RowIterator rowIterator = qResult.getRows();

        // convert the row iterator into a page iterator
        list.setPageIterator(rowToPageIterator(_log, _pageManager, rowIterator));

    }

    request.setAttribute("emptyList", list.isEmpty());
%><c:set var="listIsPaginating" value="<%= list.isPaginating() %>"/>