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
    String sourceUrl = (String)map.get("sourceUrl");

    if (StringUtils.isBlank(title)) {
        title = asset.getName();
    }

    if (category == null) category = "";
    if (director == null) director = "";
    if (artisticStatement == null) artisticStatement = "";
    if (sourceUrl == null) sourceUrl = "";

%>

<div style="float: left; overflow: hidden; clear: none; width: 100%; max-width: 220px; background-color: #111; position: relative; margin: 10px; z-index: 1; padding: 0; display: inline-block;">
    <div style="height: 270px; opacity: 1; filter: none; width: 220px;">
        <div style="position: relative;">
            <a title="<%= escapeBody(category) %>" onclick="window.open('<%= mappedUrl(_resourceResolver, sourceUrl) %>','<%= escapeBody(title) %>','width=600,height=400,left='+(screen.availWidth/2-300)+',top='+(screen.availHeight/2-200)+'');return false;" href="<%= mappedUrl(_resourceResolver, sourceUrl) %>" target="_blank">
                <img alt="<%= escapeBody(title) %>" src="<%= thumbnailUrl %>" height="138" width="220" />
            </a>
        </div>
        <div class="body" style="background-position: right top; background-repeat: no-repeat; overflow: hidden; padding: 10px; margin: 0;">
            <h4 style="font-size: 19px; color: #ffffff; margin-bottom: 5px; padding: 0px; font-family: 'ramblabold',Helvetica,Arial,'华文宋体','STSong','新細明體','PMingLiU',sans-serif;"><%= escapeBody(title) %></h4>
            <p style="color: #ffffff; margin-bottom: 10px;">
                <%if (director.length() > 0) {%>
                    Director:&nbsp;<%= escapeBody(director) %>
                <%}%>
                <br /><br />
                <a style="color: #ffffff;" title="Google" onclick="window.open('<%= mappedUrl(_resourceResolver, artisticStatement) %>','The Strange Studio','width=1050,height=400,left='+(screen.availWidth/2-300)+',top='+(screen.availHeight/2-200)+'');return false;" href="<%= mappedUrl(_resourceResolver, artisticStatement) %>" target="_blank">Artistic Statement</a>
            </p>
        </div>
    </div>
</div>