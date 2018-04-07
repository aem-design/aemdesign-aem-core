<?xml version="1.0" encoding="UTF-8" ?><%
%><%@ page session="false" %><%
%><%@ page import="java.util.Iterator" %><%
%><%
%>
<%
%><%@ taglib prefix="atom" uri="http://sling.apache.org/taglibs/atom/1.0" %><%
%><%@ include file="/apps/aemdesign/global/global.jsp" %><%
%><%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="init.jsp"  %><%

    String
        url = mappedUrl(_resourceResolver, _resource.getPath().concat(DEFAULT_EXTENTION)),
        link = mappedUrl(_resourceResolver, _resource.getPath() + ".rss"),
        title = getPageTitle(_currentPage),
        subTitle = getFeedDescription(_currentPage, _properties);


//    com.day.cq.wcm.foundation.List list = (com.day.cq.wcm.foundation.List) request.getAttribute("list");

    response.setHeader("Content-Type", "text/xml");
%>
<rss version="2.0">
    <channel>

        <c:set var="list" value="<%= list %>" />

        <title><%= escapeBody(title) %></title>

        <c:if test="<%= !StringUtils.isEmpty(subTitle) %>">
            <description><%= escapeBody(subTitle) %></description>
        </c:if>
        <ttl>1800</ttl>
        <language>en</language>
        <link><%= url %></link>
        <copyright>AEM.Design</copyright>

        <%
            Iterator<Page> pageIterator = list.getPages();
            while (pageIterator.hasNext()) {
                Page listPage = pageIterator.next();

                String pageTitle =
                        htmlToXmlEntities(
                                StringEscapeUtils.escapeHtml4(getPageTitle(listPage))
                            );

                String pageDescription =
                        htmlToXmlEntities(
                                StringEscapeUtils.escapeHtml4(getPageDescription(listPage))
                            );

                Image listPageImage = new Image(listPage.getContentResource(), "image");
                String rssDate = formattedRssDate(listPage.getLastModified());
        %>

            <item>
                <title><%= pageTitle %></title>

                <c:if test="<%= StringUtils.isNotEmpty(pageDescription) %>">
                    <description><%= pageDescription %></description>
                </c:if>

                <link><%= mappedUrl(_resourceResolver, listPage.getPath()).concat(DEFAULT_EXTENTION) %></link>
                <guid><%= getUniquePageIdentifier(listPage) %></guid>

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