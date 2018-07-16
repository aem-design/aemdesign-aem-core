<%@ page import="com.day.cq.wcm.foundation.Image" %>
<%@ page import="com.day.cq.dam.api.RenditionPicker" %>
<%@ page import="com.day.cq.dam.api.Rendition" %>
<%@ page import="java.util.List" %>
<%@ page import="com.day.cq.dam.commons.util.DamUtil" %>
<%@ include file="/apps/aemdesign/global/global.jsp"%>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="/apps/aemdesign/global/media.jsp" %>
<%
    // init
    Resource thisResource = (Resource) request.getAttribute("badgeResource");
    Boolean hideThumbnail = (Boolean) request.getAttribute("hideThumbnail");
    Page thisPage = (Page) request.getAttribute(FIELD_BADGE_PAGE);

    ComponentProperties componentProperties = new ComponentProperties();

    // object transformations
    Asset asset = thisResource.adaptTo(Asset.class);
    Resource thumbnail = getThumbnailPathName(asset);
    String thumbnailUrl = asset.getPath();
    if (thumbnail != null) {
        thumbnailUrl = thumbnail.getPath();
    }


    String title = asset.getMetadataValue(DAM_TITLE);
    String description = asset.getMetadataValue(DAM_DESCRIPTION);

    if (StringUtils.isBlank(title)) {
        title = asset.getName();
    }

    componentProperties.put("title",title);
    componentProperties.put("hideThumbnail",hideThumbnail);
    componentProperties.put("thumbnailUrl",thumbnailUrl);
    componentProperties.put("description",description);
    componentProperties.put("linkURL",mappedUrl(_resourceResolver, asset.getPath()));


%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<a href="${componentProperties.linkURL}">${componentProperties.title}</a>
<div class="description">${componentProperties.description}</div>
<c:if test="${not componentProperties.hideThumbnail}">
    <div class="image">
        <a href="${componentProperties.linkURL}" class="thumbnail">
            <img src="${componentProperties.thumbnailUrl}" class="thumbnail" alt="${componentProperties.title}" />
        </a>
    </div>
</c:if>
