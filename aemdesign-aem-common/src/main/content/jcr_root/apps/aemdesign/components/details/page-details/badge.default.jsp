<%--
    // TODO: change "img" at line 10 to the proper img size
--%>
<%@ page session="false" import="org.apache.commons.lang3.StringUtils"%>
<%@ page import="com.day.cq.wcm.api.Page"%>
<%@ page import="com.day.cq.wcm.foundation.Image" %>
<%@ page import="com.day.cq.wcm.foundation.Image" %>
<%@ page import="java.text.MessageFormat" %>

<%@ include file="/apps/aemdesign/global/global.jsp"%>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>

<%
    // init
    Page thisPage = (Page) request.getAttribute("badgePage");
    // set title and description
    String displayTitle = getPageNavTitle(thisPage);

    String url = getPageUrl(thisPage);

    ComponentProperties componentProperties = new ComponentProperties();

    componentProperties.put("url", url);
    componentProperties.put("title", displayTitle);
    componentProperties.put("imgAlt", _i18n.get("filterByText", "pagedetail", displayTitle));


%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<a href="<c:out value="${componentProperties.url}"/>" title="<c:out value="${componentProperties.imgAlt}"/>">${componentProperties.title}</a>
