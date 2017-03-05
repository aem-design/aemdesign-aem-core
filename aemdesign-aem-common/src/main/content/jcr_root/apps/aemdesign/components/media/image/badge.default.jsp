<%@ page import="com.day.cq.wcm.foundation.Image" %>
<%@ page import="com.day.cq.dam.api.RenditionPicker" %>
<%@ page import="com.day.cq.dam.api.Rendition" %>
<%@ page import="java.util.List" %>
<%@ page import="com.day.cq.dam.commons.util.DamUtil" %>
<%@ include file="/apps/aemdesign/global/global.jsp"%>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ include file="/apps/aemdesign/global/media.jsp" %>
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
    String displayTitle = getMetadataStringForKey(asset, FIELD_METADATA_TITLE);
    String displayDescription = getMetadataStringForKey(asset, FIELD_METADATA_DESCRIPTION);

    if (StringUtils.isBlank(displayTitle)) {
        displayTitle = asset.getName();
    }

    if (displayDescription == null) {
        displayDescription = "";
    }
%>
<div class="listBox <%= withImage %>">
    <a
        role="popup"
        rel="image"
        href="<%= mappedUrl(asset.getPath()) %>" class="linkTxt">
                <%= escapeBody(displayTitle) %>
    </a>
    <div class="longTxt">
        <p><%= escapeBody(displayDescription) %></p>
        <%@ include file="/apps/aemdesign/components/media/global/iconrow.jsp" %>
    </div>

    <c:if test="<%= !hideThumbnail %>">

        <div class="image">
            <a
                role="popup"
                rel="image"
                href="<%= mappedUrl(asset.getPath()) %>" class="linkTxt">
                    <img
                        src="<%= thumbnailUrl %>"
                        class="list-thumbnail-image"
                        alt="<%= escapeBody(displayTitle) %>" />
            </a>
        </div>

    </c:if>


</div>
