<?xml version="1.0" encoding="UTF-8" ?><%
%><%@ page session="false" %><%
%><%@ page import="com.day.cq.commons.Externalizer,
                   com.day.cq.wcm.foundation.List,
                   java.util.Iterator" %><%
%><%
%>
<%
%><%@ taglib prefix="atom" uri="http://sling.apache.org/taglibs/atom/1.0" %><%
%><%@ include file="/apps/aemdesign/global/global.jsp" %><%
%><%@ include file="/apps/aemdesign/global/components.jsp" %><%
    String
        url = mappedUrl(_resource.getPath() + ".html"),
        link = mappedUrl(_resource.getPath() + ".rss"),
        title = getPageTitle(_currentPage),
        subTitle = getFeedDescription(_currentPage, _properties);

    %><cq:include script="init.jsp" /><%

    com.day.cq.wcm.foundation.List list = (com.day.cq.wcm.foundation.List) request.getAttribute("list");

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

                <c:if test="<%= !StringUtils.isEmpty(pageDescription) %>">
                    <description><%= pageDescription %></description>
                </c:if>

                <link><%= mappedUrl(listPage.getPath()) + ".html" %></link>
                <guid><%= getUniquePageIdentifier(listPage) %></guid>

                <c:if test="<%= !StringUtils.isBlank(rssDate) %>">
                    <pubDate><%= rssDate %></pubDate>
                </c:if>

                <c:if test="<%= listPageImage.hasContent() %>">
                    <image><%= mappedUrl(listPageImage.getSrc()) %></image>
                </c:if>
            </item>

        <% } %>
    </channel>

</rss>