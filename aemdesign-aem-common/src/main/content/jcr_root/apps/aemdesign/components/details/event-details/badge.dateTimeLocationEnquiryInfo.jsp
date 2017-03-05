<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.day.cq.wcm.api.Page" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>

<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="./common.jsp" %>

<%
    String componentPath = "./"+PATH_DEFAULT_CONTENT+"/event-details";

    final String DIALOG_DATE_FORMAT = "HH:mm";
    final String I18N_CATEGORY = "eventdetail";

    //init
    Page thisPage = (Page) request.getAttribute("badgePage");

    Object[][] componentFields = {
            {"title", _pageProperties.get(JcrConstants.JCR_TITLE, StringUtils.EMPTY)},
            {"description", _pageProperties.get(JcrConstants.JCR_DESCRIPTION, StringUtils.EMPTY)},
            {"eventDisplayDate", StringUtils.EMPTY},
            {"eventStartDate", _pageProperties.get(JcrConstants.JCR_CREATED, Calendar.getInstance())},
            {"eventEndDate", _pageProperties.get(JcrConstants.JCR_CREATED, Calendar.getInstance())},
            {"eventLoc", StringUtils.EMPTY},
            {"eventRefLabel", StringUtils.EMPTY},
            {"eventRefLink", StringUtils.EMPTY},
            {"enquiryInfo", StringUtils.EMPTY},
            {"cq:tags", new String[]{}},
            //subCategory is resolved for Event Detail's badge
            {"subCategory", StringUtils.EMPTY},
            {"menuColor", StringUtils.EMPTY},
            {"eventDisplayTimeFormat",""}
    };

    ComponentProperties componentProperties = getComponentProperties(thisPage, componentPath, componentFields);

    componentProperties.putAll(processComponentFields(componentProperties,_i18n,_sling));

    if (componentProperties.get("enquiryInfo", StringUtils.EMPTY).length() > 0){
        String enquiryInfo =  componentProperties.get("enquiryInfo", StringUtils.EMPTY);
        enquiryInfo = enquiryInfo.replaceAll("(\r\n|\n)", "<br />");
        componentProperties.put("eventDisplayEnquiryInfo", enquiryInfo);
    }

    componentProperties.put("date", _i18n.get("date",I18N_CATEGORY));
    componentProperties.put("time", _i18n.get("time",I18N_CATEGORY));
    componentProperties.put("venue", _i18n.get("venue",I18N_CATEGORY));
    componentProperties.put("enquiries", _i18n.get("enquiries",I18N_CATEGORY));
%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<div class="compl_info" role="grid">
    <c:if test="${not empty componentProperties.eventDisplayDateFormatted}">
        <dl role="row">
            <dt role="columnheader">${componentProperties.date}</dt>
            <dd role="gridcell">${componentProperties.eventDisplayDateFormatted}</dd>
        </dl>
    </c:if>
    <c:if test="${not empty componentProperties.eventDisplayTimeFormatted}">
    <dl role="row">
        <dt role="columnheader">${componentProperties.time}</dt>
        <dd role="gridcell">${componentProperties.eventDisplayTimeFormatted}</dd>
    </dl>
    </c:if>

    <c:if test="${not empty componentProperties.eventLoc}">
        <dl role="row">
            <dt role="columnheader">${componentProperties.venue}</dt>
            <dd role="gridcell">${componentProperties.eventLoc}</dd>
        </dl>
    </c:if>

    <c:if test="${not empty componentProperties.eventDisplayEnquiryInfo}">
        <dl role="row">
            <dt role="columnheader">${componentProperties.enquiries}</dt>
            <dd role="gridcell">${componentProperties.eventDisplayEnquiryInfo}</dd>
        </dl>
    </c:if>

</div>