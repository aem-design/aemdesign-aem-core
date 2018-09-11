<%@ page import="com.day.cq.search.PredicateConverter" %>
<%@ page import="com.day.cq.search.PredicateGroup" %>
<%@ page import="com.day.cq.search.Query" %>
<%@ page import="com.day.cq.search.QueryBuilder" %>
<%@ page import="com.day.cq.search.result.SearchResult" %>
<%@ page import="com.day.cq.wcm.api.PageFilter" %>
<%@ page import="design.aem.components.list.HitBasedPageIterator" %>
<%@ page import="java.net.URLDecoder" %>
<%

    Boolean showHidden = _properties.get("showHidden", false);

    com.day.cq.wcm.foundation.List list = new com.day.cq.wcm.foundation.List(_slingRequest, new PageFilter(false, showHidden));

    //for Children list set the current page as the Parent Page if the property is not set
    if (com.day.cq.wcm.foundation.List.SOURCE_CHILDREN.equals(_properties.get(com.day.cq.wcm.foundation.List.SOURCE_PROPERTY_NAME, com.day.cq.wcm.foundation.List.SOURCE_CHILDREN))) {
        String parentPage = _properties.get(com.day.cq.wcm.foundation.List.PARENT_PAGE_PROPERTY_NAME,"");
        if (isEmpty(parentPage)) {
            list.setStartIn(_resource.getPath());
        }
    }
    //allow passing of simple list query
    if (com.day.cq.wcm.foundation.List.SOURCE_CHILDREN.equals(_properties.get(com.day.cq.wcm.foundation.List.SOURCE_PROPERTY_NAME, ""))) {
        if (_slingRequest.getRequestParameter("q") !=null) {
            String escapedQuery = _slingRequest.getRequestParameter("q").toString();
            list.setQuery(escapedQuery);
        }
    }
    //allow passing of querybuilder queries
    if (com.day.cq.wcm.foundation.List.SOURCE_QUERYBUILDER.equals(_properties.get(com.day.cq.wcm.foundation.List.SOURCE_PROPERTY_NAME, ""))) {
        if (_slingRequest.getRequestParameter("q") !=null) {
            String escapedQuery = _slingRequest.getRequestParameter("q").toString();
            try {
                String unescapedQuery = URLDecoder.decode(escapedQuery,"UTF-8");
                QueryBuilder queryBuilder = (QueryBuilder)_resourceResolver.adaptTo(QueryBuilder.class);
                PageManager pm = (PageManager)_resourceResolver.adaptTo(PageManager.class);
                //create props for query
                java.util.Properties props = new java.util.Properties();
                //load query candidate
                props.load(new ByteArrayInputStream(unescapedQuery.getBytes()));
                //create predicate from query candidate
                PredicateGroup predicateGroup = PredicateConverter.createPredicates(props);
                boolean allowDuplicates = (Boolean)_properties.get("allowDuplicates", false);
                javax.jcr.Session jcrSession = _slingRequest.getResourceResolver().adaptTo(javax.jcr.Session.class);
                Query query = queryBuilder.createQuery(predicateGroup,jcrSession);
                if (query != null) {
                    SearchResult result = query.getResult();
                    HitBasedPageIterator newList = new HitBasedPageIterator(pm, result.getHits().iterator(), !allowDuplicates, new PageFilter(false, showHidden));
                    list.setPageIterator(newList);
                }
            } catch (Exception ex) {
                LOG.error("error using querybuilder with query [{}]. {}", escapedQuery, ex);
            }
        }
    }

    request.setAttribute("list", list);

%>