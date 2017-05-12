<%@ page session="false" import="org.apache.commons.lang3.StringUtils"%>
<%@ page import="com.day.cq.wcm.api.Page"%>
<%@ page import="com.day.cq.wcm.foundation.Image" %>
<%@ page import="com.day.cq.wcm.foundation.Image" %>
<%@ page import="java.text.MessageFormat" %>

<%@ include file="/apps/aemdesign/global/global.jsp"%>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="./common.jsp" %>
<%
    // init
    Page thisPage = (Page) request.getAttribute("badgePage");
    // set title and description
    String displayTitle = getPageNavTitle(thisPage);

    String url = getPageUrl(thisPage);

    Object[][] componentFields = {
            {"title", _pageProperties.get(JcrConstants.JCR_TITLE, StringUtils.EMPTY)},
            {"description", _pageProperties.get(JcrConstants.JCR_DESCRIPTION, StringUtils.EMPTY)},
            {"eventStartDate", _pageProperties.get(JcrConstants.JCR_CREATED, Calendar.getInstance())},
            {"eventEndDate", _pageProperties.get(JcrConstants.JCR_CREATED, Calendar.getInstance())},
            {"eventTime", _pageProperties.get(JcrConstants.JCR_CREATED, Calendar.getInstance())},
            {"eventLoc", StringUtils.EMPTY},
            {"eventRefLabel", StringUtils.EMPTY},
            {"eventRefLink", StringUtils.EMPTY},
            {"cq:tags", new String[]{}},
            //subCategory is resolved for Event Detail's badge
            {"subCategory", StringUtils.EMPTY},
            {"menuColor", StringUtils.EMPTY},
            {"titleFormat",""},
            {"subTitleFormat",""},
            {"eventDisplayDateFormat",""}

    };

    ComponentProperties componentProperties = getComponentProperties(thisPage, componentPath, componentFields);

    componentProperties.put("url", mappedUrl(_resourceResolver, url));
    componentProperties.put("title", displayTitle);
    componentProperties.put("imgAlt", _i18n.get("filterByText", "eventdetail", displayTitle));

    componentProperties.putAll(processComponentFields(componentProperties,_i18n,_sling));

%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<a href="<c:out value="${componentProperties.url}"/>" title="<c:out value="${componentProperties.imgAlt}"/>">${componentProperties.titleFormatted}</a>
