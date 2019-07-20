<?xml version="1.0" encoding="UTF-8" ?>
<%@ page session="false" %>
<%@ page import="java.util.Iterator" %>
<%@ taglib prefix="atom" uri="http://sling.apache.org/taglibs/atom/1.0" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ page import="com.day.cq.search.Query" %>
<%@ page import="com.day.cq.search.QueryBuilder" %>
<%@ page import="com.day.cq.search.result.SearchResult" %>
<%@ page import="com.day.cq.wcm.api.PageFilter" %>
<%@ page import="design.aem.components.list.HitBasedPageIterator" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="static org.apache.commons.lang3.StringUtils.isEmpty" %>
<%@ page import="static org.apache.commons.lang3.StringUtils.isNotEmpty" %>
<%@ page import="java.io.ByteArrayInputStream" %>
<%@ page import="com.day.cq.search.PredicateConverter" %>
<%@ page import="com.day.cq.search.PredicateGroup" %>
<%@ page import="static design.aem.utils.components.ConstantsUtil.DEFAULT_EXTENTION" %>
<%@ page import="static design.aem.utils.components.ResolverUtil.mappedUrl" %>
<%@ page import="static design.aem.utils.components.CommonUtil.getPageTitle" %>
<%@ page import="static design.aem.utils.components.CommonUtil.*" %>
<%@ page import="static design.aem.utils.components.ComponentsUtil.formattedRssDate" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="static design.aem.utils.components.ComponentsUtil.getUniquePageIdentifier" %>
<%@ page import="com.day.cq.wcm.foundation.Image" %>
<%

    if (!_properties.get("feedEnabled",false)) {
        response.sendError(404);
    }

    boolean showHidden = _properties.get("showHidden", false);

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
                _log.error("error using querybuilder with query [{}]. {}", escapedQuery, ex);
            }
        }
    }

    request.setAttribute("list", list);


String
url = mappedUrl(_resourceResolver, _resource.getPath().concat(DEFAULT_EXTENTION)),
link = mappedUrl(_resourceResolver, _resource.getPath() + ".rss"),
title = getPageTitle(_currentPage , _resource),
subTitle = getPageDescription(_currentPage, _properties);

//    com.day.cq.wcm.foundation.List list = (com.day.cq.wcm.foundation.List) request.getAttribute("list");

response.setHeader("Content-Type", "text/xml");
%>
<rss version="2.0">
    <channel>

        <c:set var="list" value="<%= list %>" />

        <title><%= escapeBody(title) %></title>

        <c:if test="<%= !isEmpty(subTitle) %>">
            <description><%= escapeBody(subTitle) %></description>
        </c:if>
        <ttl>1800</ttl>
        <language>en</language>
        <link><%= url %></link>

        <%
        Iterator<Page> pageIterator = list.getPages();
        while (pageIterator.hasNext()) {
        Page listPage = pageIterator.next();

        String pageTitle = getPageTitle(listPage, null);

        String pageDescription = getPageDescription(listPage, null);

        Image listPageImage = new Image(listPage.getContentResource(), "image");
        String rssDate = formattedRssDate(listPage.getLastModified());
        %>

        <item>
            <title><%= pageTitle %></title>

            <c:if test="<%= isNotEmpty(pageDescription) %>">
                <description><%= pageDescription %></description>
            </c:if>

            <link><%= mappedUrl(_resourceResolver, listPage.getPath()).concat(DEFAULT_EXTENTION) %></link>
            <guid><%= hashMd5(listPage.getPath()) %></guid>

            <c:if test="<%= StringUtils.isNotBlank(rssDate) %>">
                <pubDate><%= rssDate %></pubDate>
            </c:if>

            <c:if test="<%= listPageImage.hasContent() %>">
                <image><%= mappedUrl(_resourceResolver, listPageImage.getSrc()) %></image>
            </c:if>
        </item>

        <% } %>
    </channel>

</rss>