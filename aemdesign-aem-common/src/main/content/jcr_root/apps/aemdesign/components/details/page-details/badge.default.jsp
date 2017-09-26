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
    Page thisPage = (Page) request.getAttribute(FIELD_BADGE_PAGE);
    // set title and description
    String displayTitle = getPageNavTitle(thisPage);

    String url = getPageUrl(thisPage);

    ComponentProperties componentProperties = new ComponentProperties();

    componentProperties.put("url", url);
    componentProperties.put("title", displayTitle);
    componentProperties.put("imgAlt", _i18n.get("filterByText", "pagedetail", displayTitle));

    String linkTarget = (String) request.getAttribute("linkTarget");
    if (isNotEmpty(linkTarget)){
        componentProperties.put("targetString", MessageFormat.format("target=\"{}\"",linkTarget));
    }

%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<a
        href="${componentProperties.url}"
        title="${componentProperties.imgAlt}"
        ${componentProperties.targetString}>${componentProperties.title}</a>
