<%@ page import="com.day.cq.wcm.foundation.Image" %>
<%@ page import="com.day.cq.dam.api.RenditionPicker" %>
<%@ page import="com.day.cq.dam.api.Rendition" %>
<%@ page import="java.util.List" %>
<%@ page import="com.day.cq.dam.commons.util.DamUtil" %>
<%@ page import="org.apache.sling.api.resource.ResourceResolver" %>
<%@ page import="java.util.Map" %>
<%@ include file="/apps/aemdesign/global/global.jsp"%>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ include file="/apps/aemdesign/global/media.jsp" %>
<%@ include file="/apps/aemdesign/components/media/mediaimagegallery/gallerydata.jsp" %>
<%
    // init
    Resource thisResource = (Resource) request.getAttribute("badgeResource");
    Boolean hideThumbnail = (Boolean) request.getAttribute("hideThumbnail");

    String iconType = "photo-gallery";

    // object transformations
    Resource firstResource = this.getFirstResourceInFolder(_resourceResolver, thisResource);
    Resource contentResource = _resourceResolver.getResource(thisResource, "jcr:content");
    Node contentNode = contentResource != null ? contentResource.adaptTo(Node.class) : null;

    // initialize variables
    Asset asset = null;
    String withImage = "";
    String displayTitle = (String) getPropertyWithDefault(contentNode, JCR_TITLE, thisResource.adaptTo(Node.class).getName());
    String displayDescription = (String) getPropertyWithDefault(contentNode, JCR_DESCRIPTION, "");

    if (firstResource != null) {
        asset = firstResource.adaptTo(Asset.class);
        if (!hideThumbnail) {
            withImage = "withImage";
        }
    }

    List<Map> pictures = getPicturesFromResource(_resourceResolver, thisResource);
    String galleryTheme = mappedUrl(GALLERIA_THEME);
%>
<c:choose>
    <c:when test="<%= asset == null %>">
        <p class="cq-info">Gallery points at empty folder</p>
    </c:when>

    <c:otherwise>
        <div class="listBox <%= withImage %>">
            <a
                role="popup"
                rel="gallery"
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
                        rel="gallery"
                        href="<%= mappedUrl(asset.getPath()) %>" class="linkTxt">
                            <img
                                src="<%= mappedUrl(asset.getPath()) %>"
                                class="list-thumbnail-image"
                                alt="<%= escapeBody(displayTitle) %>" />
                    </a>
                </div>

            </c:if>

            <ul class="gallery-content" data-theme="<%= galleryTheme %>">
                <c:forEach items="<%= pictures %>" var="picture" varStatus="pStatus">
                    <li>
                        <h3>${picture.title}</h3>
                        <p>${picture.description}</p>
                        <a href="${picture.image}" data-thumbnail="${picture.thumbnail}">View picture</a>
                    </li>
                </c:forEach>
            </ul>

        </div>

    </c:otherwise>
</c:choose>