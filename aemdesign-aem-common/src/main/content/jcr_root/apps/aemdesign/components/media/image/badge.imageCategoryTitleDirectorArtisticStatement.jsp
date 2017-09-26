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

    // object transformations
    Asset asset = thisResource.adaptTo(Asset.class);
    Resource thumbnail = getThumbnailPathName(asset);

    String thumbnailUrl = asset.getPath();
    if (thumbnail != null) {
        thumbnailUrl = thumbnail.getPath();
    }

    String FIELD_METADATA_TITLE = "dam:title";
    String FIELD_METADATA_DIRECTOR = "director";
    String FIELD_METADATA_PAGE_LINK = "dam:pagelink";
    String FIELD_METADATA_SOURCE = "dam:source";
    String FIELD_METADATA_URL = "dam:url";

    String iconType = "photo-gallery";

    // set title and description
    String withImage = hideThumbnail ? "" : "withImage";

    Resource metadataResource = thisResource.getChild("jcr:content/metadata");
    ValueMap map = ResourceUtil.getValueMap(metadataResource);
    String title = (String)map.get("dc:title");
    String category = (String)map.get("category");
    String director = (String)map.get("director");
    String artisticStatement = (String)map.get("artisticStatement");
    String sourceUrl = (String)map.get(DAM_SOURCE_URL);

    if (StringUtils.isBlank(title)) {
        title = asset.getName();
    }

    if (category == null) category = "";
    if (director == null) director = "";
    if (artisticStatement == null) artisticStatement = "";
    if (sourceUrl == null) sourceUrl = "";

%>
<div class="image imageCategoryTitleDescription">
    <a title="<%= escapeBody(category) %>" href="<%= mappedUrl(_resourceResolver, sourceUrl) %>">
        <img alt="<%= escapeBody(category) %>" src="<%= thumbnailUrl %>"/>
    </a>
    <div class="body">
        <p class="title" ><%= escapeBody(title) %></p>
        <p class="director"><%= escapeBody(director) %></p>
        <p class="artisticstatement"><%= escapeBody(artisticStatement) %></p>
    </div>
</div>
