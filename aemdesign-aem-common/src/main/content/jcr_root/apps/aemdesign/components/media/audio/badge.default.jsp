<%@ page import="com.day.cq.wcm.foundation.Image" %>
<%@ page import="java.util.List" %>
<%@ page import="com.day.cq.dam.commons.util.DamUtil" %>
<%@ page import="com.day.cq.dam.api.*" %>
<%@ include file="/apps/aemdesign/global/global.jsp"%>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ include file="/apps/aemdesign/global/media.jsp" %>
<%
    // init
    Resource thisResource = (Resource) request.getAttribute("badgeResource");
    Boolean hideThumbnail = (Boolean) request.getAttribute("hideThumbnail");

    String iconType = "audio";

    // get asset information
    Asset asset = thisResource.adaptTo(Asset.class);
    List<Rendition> renditions = asset.getRenditions();
    Resource thumbnail = getThumbnailPathName(asset);

    String withImage = "withImage";
    Image img = null;
    if (!hideThumbnail && thumbnail != null) {
        img = new Image(thumbnail);
    }
    else {
        withImage = "";
    }

    // set title and description
    String displayTitle = getMetadataStringForKey(asset, DamConstants.DC_TITLE);
    String displayDescription = getMetadataStringForKey(asset, DamConstants.DC_DESCRIPTION);

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
        rel="audio"
        data-player-address="<%= PLAYER_ADDRESS %>"
        href="<%= mappedUrl(asset.getPath()) %>"
        class="linkTxt">
            <%= escapeBody(displayTitle) %>
    </a>
    <div class="longTxt">
        <p><%= escapeBody(displayDescription) %></p>
        <%@ include file="/apps/aemdesign/components/media/global/iconrow.jsp" %>
    </div>

    <c:if test="<%= img != null %>">

        <div class="image">
            <a
                role="popup"
                rel="audio"
                data-player-address="<%= PLAYER_ADDRESS %>"
                href="<%= mappedUrl(asset.getPath()) %>"
                class="linkTxt">
                    <img class="list-thumbnail-image" src="<%= img.getPath() %>" />
            </a>
        </div>

    </c:if>

</div>
