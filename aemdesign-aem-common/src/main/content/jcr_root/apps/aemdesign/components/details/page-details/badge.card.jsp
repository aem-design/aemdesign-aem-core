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
    final String DEFAULT_TITLE_TYPE = "h2";

    String componentPath = "./" + PATH_DEFAULT_CONTENT + "/page-details";

    //init
    Page thisPage = (Page) request.getAttribute(FIELD_BADGE_PAGE);
    String titleType = (String) request.getAttribute(FIELD_BADGE_TITLE_TAG_TYPE);

    String img = getPageImgReferencePath(thisPage);

    String url = getPageUrl(thisPage);

    Object[][] componentFields = {
            {"title", getPageTitle(thisPage)},
            {"description", thisPage.getDescription()},
            {TagConstants.PN_TAGS, new String[]{},"data-category", Tag.class.getCanonicalName()},
    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            thisPage,
            componentPath,
            componentFields);

    String pageTitle = getPageNavTitle(thisPage);

    componentProperties.put("title", pageTitle);
    componentProperties.put(FIELD_TITLE_TAG_TYPE, (isBlank(titleType)?DEFAULT_TITLE_TYPE:titleType));
    componentProperties.put("url", url);
    componentProperties.put("img", img);
    componentProperties.put("imgAlt", _i18n.get("readMoreAboutText", "pagedetail") + pageTitle);

    String[] tags = componentProperties.get(TagConstants.PN_TAGS, new String[]{});
    componentProperties.put("category",getTagsAsAdmin(_sling, tags, _slingRequest.getLocale()));

%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>

<a href="${componentProperties.url}" title="${componentProperties.imgAlt}">${componentProperties.title}
    <div class="card">
        <img src="${componentProperties.img}" data-src="${componentProperties.img}" alt="${componentProperties.imgAlt}" class="card-img-top"/>
        <div class="card-block">
            <${componentProperties.titleType}>${componentProperties.title}</${componentProperties.titleType}>
            <c:if test="${not empty componentProperties.category}">
            <div class="card-category">${componentProperties.category}</div>
            </c:if>
            <p class="card-text">${componentProperties.description}</p>
        </div>
    </div>
</a>
