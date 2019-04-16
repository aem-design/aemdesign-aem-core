<%@ page import="org.apache.sling.api.resource.*" %>
<%@ page import="com.day.cq.wcm.foundation.Image" %>
<%@ page import="com.day.cq.dam.api.RenditionPicker" %>
<%@ page import="com.day.cq.dam.api.Rendition" %>
<%@ page import="java.util.List" %>
<%@ page import="com.day.cq.dam.commons.util.DamUtil" %>
<%@ include file="/apps/aemdesign/global/global.jsp"%>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ include file="/apps/aemdesign/global/media.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%
    // init
    Resource thisResource = (Resource) request.getAttribute("badgeResource");
    Boolean hideThumbnail = (Boolean) request.getAttribute("hideThumbnail");

    ComponentProperties componentProperties = new ComponentProperties();

    // object transformations
    Asset asset = thisResource.adaptTo(Asset.class);
    Resource thumbnail = getThumbnailPathName(asset);

    String thumbnailUrl = asset.getPath();
    if (thumbnail != null) {
        thumbnailUrl = thumbnail.getPath();
    }

    Asset assetBasic = thisResource.adaptTo(Asset.class);
    String title = assetBasic.getMetadataValue(DAM_TITLE);
    String category = assetBasic.getMetadataValue(DAM_CATEGORY);
    String director = assetBasic.getMetadataValue(DAM_DIRECTOR);
    String artistStatement = assetBasic.getMetadataValue(DAM_ARTISTSTATEMENT);
    String sourceUrl = assetBasic.getMetadataValue(DAM_SOURCE_URL);

    if (StringUtils.isBlank(title)) {
        title = asset.getName();
    }

    if (isNotEmpty(director)) {
        director = _i18n.get(I18N_FORMAT_DIRECTOR, I18N_CATEGORY, director);
    }

    componentProperties.put("title",title);
    componentProperties.put("hideThumbnail",hideThumbnail);
    componentProperties.put("thumbnailUrl",thumbnailUrl);
    componentProperties.put("director",director);
    componentProperties.put("artistStatement",artistStatement);
    componentProperties.put("category",category);
    componentProperties.put("linkURL",mappedUrl(_resourceResolver, sourceUrl));

%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<div class="image imageCategoryTitleDescription">
    <a title="${componentProperties.category}" href="${componentProperties.linkURL}">
        <img alt="${componentProperties.category}" src="${componentProperties.thumbnailUrl}"/>
    </a>
    <div class="body">
        <div class="title" >${componentProperties.title}</div>
        <div class="director">${componentProperties.director}</div>
        <div class="artiststatement">${componentProperties.artistStatement}</div>
    </div>
</div>
