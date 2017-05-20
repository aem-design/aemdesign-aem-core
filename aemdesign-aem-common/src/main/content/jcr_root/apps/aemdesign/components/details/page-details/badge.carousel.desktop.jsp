<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="com.day.cq.wcm.api.Page" %>
<%@page import="com.day.cq.wcm.foundation.Image" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.day.cq.tagging.Tag" %>
<%@ page import="com.day.cq.tagging.TagManager" %>
<%@ page import="com.google.common.base.Throwables" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>

<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>

<%

    final String DEFAULT_IMAGE_PATH = "/content/dam/aemdesign/admin/defaults/environment.gif";

    String componentPath = "./" + PATH_DEFAULT_CONTENT + "/page-details";

    //init
    Page thisPage = (Page) request.getAttribute("badgePage");

    String img = this.getPageImgReferencePath(thisPage);

    String url = getPageUrl(thisPage);

    Object[][] componentFields = {
            {"title", thisPage.getTitle()},
            {"description", thisPage.getDescription()}
    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            thisPage,
            componentPath,
            componentFields);

    String pageTitle = getPageTitle(thisPage);

    componentProperties.put("title", pageTitle);
    componentProperties.put("url", url);
    componentProperties.put("img", img);
    componentProperties.put("imgAlt", _i18n.get("readMoreAboutText", "pagedetail") + pageTitle);

%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>

<a href="${componentProperties.url}" title="${componentProperties.imgAlt}">${componentProperties.title}</a>
<img src="${componentProperties.img}" data-src="${componentProperties.img}" alt="${componentProperties.imgAlt}" width="1400" height="500" />
<div class="caption">
    <div class="wrapper">
        <strong class="col-4 left">${componentProperties.title}</strong>
        <p>${componentProperties.description}</p>
    </div>
</div>
