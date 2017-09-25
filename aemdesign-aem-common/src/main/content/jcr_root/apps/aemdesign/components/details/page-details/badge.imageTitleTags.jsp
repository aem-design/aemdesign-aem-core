<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="com.day.cq.wcm.api.Page" %>
<%@page import="com.day.cq.wcm.foundation.Image" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map"%>
<%@ page import="com.day.cq.tagging.Tag" %>
<%@ page import="com.day.cq.tagging.TagManager" %>
<%@ page import="com.google.common.base.Throwables" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>

<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="./common.jsp" %>

<%

    final String THUMBNAIL_IMAGE_WIDTH = "220";

    String componentPath = "./"+PATH_DEFAULT_CONTENT+"/page-details";

    //init
    Page thisPage = (Page) request.getAttribute("badgePage");

    String img =  this.getPageImgReferencePath(thisPage);
    img = getThumbnail(img, DEFAULT_IMAGE_PATH_SELECTOR, _resourceResolver);

    String url = getPageUrl(thisPage);

    String emptyImagePlaceHolder = "";

    if (StringUtils.isEmpty(img)){
        emptyImagePlaceHolder = " placeholder lilac";
    }

    Object[][] componentFields = {
            {"title", getPageTitle(thisPage)},
            {"description", thisPage.getDescription()},
            {"cq:tags", new String[]{}},
            //subCategory is resolved for Event Detail's badge
            {"subCategory", ""},
            {DETAILS_FIELD_CARDSIZE, ""},
            {"thumbnailWidth",THUMBNAIL_IMAGE_WIDTH},
            {"menuColor",""},
            {"titleFormat",""}
    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            thisPage,
            componentPath,
            componentFields);

    String[] tags = componentProperties.get(TagConstants.PN_TAGS, new String[]{});

    componentProperties.put("category",getTagsAsAdmin(_sling, tags, _slingRequest.getLocale()));

    String pageTitle = getPageTitle(thisPage);

    //componentProperties.put("title", pageTitle);
    componentProperties.put("url",url);
    componentProperties.put("img",img);
    componentProperties.put("imgAlt", _i18n.get("readMoreAboutText","pagedetail") +pageTitle );

    componentProperties.put(DEFAULT_SECONDARY_IMAGE_NODE_NAME, getPageContentImagePath(thisPage, "article/par/page-details/secondaryImage"));

    componentProperties.putAll(processComponentFields(componentProperties,_i18n,_sling));
%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:set var="emptyImagePlaceHolder" value="<%= emptyImagePlaceHolder %>"/>

<section class="col-3 left">
    <div class="module">
        <div class="thumbnail ${emptyImagePlaceHolder}">
            <a href="${componentProperties.url}" title="${componentProperties.imgAlt}">${componentProperties.titleFormatted}</a>
            <c:if test="${not empty componentProperties.img}">
                <img alt="${componentProperties.imgAlt}" width="${componentProperties.thumbnailWidth}"
                     src="${componentProperties.img}"
                     <c:if test="${not empty componentProperties.secondaryImage}">class="rollover" data-img-rollover="${componentProperties.secondaryImage}"</c:if>
                >
            </c:if>
            <c:if test="${not empty componentProperties.category}">
                <div class="label">
                    <strong <c:if test="${not empty componentProperties.menuColor}">class="${componentProperties.menuColor}" </c:if>>${componentProperties.category}</strong>
                </div>
            </c:if>
        </div>
        <div class="body">
            <h3>
                <a href="${componentProperties.url}" title="${componentProperties.imgAlt}">${componentProperties.titleFormatted}</a>
            </h3>
            <p></p>
        </div>
    </div>
</section>
