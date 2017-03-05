<%@ page import="com.day.cq.tagging.Tag" %>
<%@ page import="com.day.cq.wcm.api.Page"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.apache.commons.lang3.StringEscapeUtils"%>

<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/images.jsp" %>

<%
    //init
    Page thisPage = (Page) request.getAttribute("badgePage");
    String cssClassItemLink = (String) request.getAttribute("cssClassItemLink");

    Map<String, Object> info = new HashMap<String, Object>();
    //info.put("displayTitle", StringEscapeUtils.escapeHtml4(thisPage.getTitle()));
    info.put("displayTitle", thisPage.getTitle());

    String thisPageUrl=getPageUrl(thisPage);

    //Url
    info.put("url", thisPageUrl);
    info.put("cssClassItemLink", cssClassItemLink);


    String targetString =" ";

    if (thisPageUrl.contains("https://") || thisPageUrl.contains("http://") ){
        targetString=" target=\"_blank\"";
    }
    info.put("targetString", targetString);
%>

<c:set var="info" value="<%= info %>" />
<a class="${info.cssClassItemLink}" href="${info.url}" ${info.targetString}>
    <c:if test="${info.displayTitle != null}">
        ${info.displayTitle}
    </c:if>
</a>
