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

    String iconType = "photo-gallery";

    // set title and description
    String withImage = hideThumbnail ? "" : "withImage";

    Resource metadataResource = thisResource.getChild("jcr:content/metadata");
    ValueMap map = ResourceUtil.getValueMap(metadataResource);
    String title = (String)map.get("dc:title");
    String category = (String)map.get("category");
    String sourceUrl = (String)map.get("sourceUrl");

    if (StringUtils.isBlank(title)) {
        title = asset.getName();
    }

    if (category == null) category = "";
    if (sourceUrl == null) sourceUrl = "";

%>

<div style="float: left; overflow: hidden; clear: none; width: 100%; max-width: 200px; background-color: #000000; position: relative; margin: 10px; z-index: 1; padding: 0; display: inline-block;">
    <div style="height: 120px; opacity: 1; filter: none; width: 200px;">
        <div style="position: relative;">
            <a title="<%= escapeBody(category) %>" onclick="window.open('<%= mappedUrl(sourceUrl) %>','<%= escapeBody(title) %>','width=600,height=400,left='+(screen.availWidth/2-300)+',top='+(screen.availHeight/2-200)+'');return false;" href="<%= mappedUrl(sourceUrl) %>" target="_blank">
                <img alt="<%= escapeBody(title) %>" src="<%= thumbnailUrl %>" width="200" />
            </a>
        </div>
    </div>
</div>
