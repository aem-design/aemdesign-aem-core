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
    String description = (String)map.get("dc:description");
    String category = (String)map.get("category");
    String sourceUrl = (String)map.get("sourceUrl");

    if (StringUtils.isBlank(title)) {
        title = asset.getName();
    }

    if (description == null) description = "";
    if (category == null) category = "";
    if (sourceUrl == null) sourceUrl = "";

%>

<div style="float: left; overflow: hidden; clear: none; width: 100%; max-width: 310px; background-color: #000000; position: relative; margin: 10px; z-index: 1; padding: 0; display: inline-block;">
    <div style="height: 280px; opacity: 1; filter: none; width: 310px;">
        <div style="position: relative;">
            <a title="<%= escapeBody(category) %>" href="<%= mappedUrl(sourceUrl) %>">
                <img alt="<%= escapeBody(category) %>" src="<%= thumbnailUrl %>" width="310" />
            </a>
        </div>
        <div class="body" style="background-position: right top; background-repeat: no-repeat; overflow: hidden; padding: 10px; margin: 0;">
            <h4 style="font-size: 20px; color: #ffffff; margin-bottom: 5px; padding: 0; font-family: 'ramblabold',Helvetica,Arial,'华文宋体','STSong','新細明體','PMingLiU',sans-serif;"><%= escapeBody(title) %></h4>
            <p style="font-size: 14px; color: #ffffff;"><%= escapeBody(description) %></p>
            <p>&nbsp;</p>
        </div>
    </div>
</div>

