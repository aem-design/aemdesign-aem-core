<%@ page import="java.util.*" %>
<%@ page import="com.day.cq.dam.video.servlet.VideoClipServlet" %>
<%@ page import="com.day.cq.dam.video.VideoProfile" %>
<%@ page import="com.day.cq.dam.commons.util.DamUtil" %>
<%@ page import="com.day.cq.dam.api.*" %>

<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%

    Object[][] componentFields = {
            {"lightboxHeight","70"},
            {"lightboxWidth","70"},
            {"thumbnailHeight", "auto"},
            {"thumbnailWidth", "auto"},
            {"assetTitlePrefix",StringUtils.EMPTY},
            {"fileReference", StringUtils.EMPTY},
            {FIELD_VARIANT, DEFAULT_VARIANT}

    };
    String msgStart = "";
    String thumbnail = "";
    String metaTitle = "";
    String metaDesc = "";
    String metaCreator = "";
    String metaCopyRight = "";

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);

    String path = (String)componentProperties.get("fileReference");

    componentProperties.put("href", path);
    msgStart = (String)componentProperties.get("assetTitlePrefix");

    if(!StringUtils.isBlank(path)){
        Asset asset = (Asset)_resourceResolver.getResource(path).adaptTo(Asset.class);
        String videoWidth = asset.getMetadataValue("tiff:ImageWidth");
        String videoHeight = asset.getMetadataValue("tiff:ImageLength");
        Rendition rd = asset.getRendition(DEFAULT_IMAGE_PATH_SELECTOR);
        thumbnail = (rd == null)? "" : rd.getPath();
        componentProperties.put("thumbnail", thumbnail);

        componentProperties.put("videoWidth", videoWidth);
        componentProperties.put("videoHeight", videoHeight);

        metaTitle = StringUtils.isBlank(asset.getMetadataValue(DamConstants.DC_TITLE)) ? "" : asset.getMetadataValue(DamConstants.DC_TITLE);
        metaDesc = StringUtils.isBlank(asset.getMetadataValue(DamConstants.DC_DESCRIPTION)) ? "" : asset.getMetadataValue(DamConstants.DC_DESCRIPTION);
        metaCreator = StringUtils.isBlank(asset.getMetadataValue(DamConstants.DC_CREATOR)) ? "" : asset.getMetadataValue(DamConstants.DC_CREATOR);
        metaCopyRight = StringUtils.isBlank(asset.getMetadataValue(DamConstants.DC_RIGHTS)) ? "" : "&amp;copy;"+asset.getMetadataValue(DamConstants.DC_RIGHTS);

        componentProperties.put("msg", msgStart + metaTitle);
        componentProperties.put("metaTitle", metaTitle);
        componentProperties.put("metaDesc", metaDesc);
        componentProperties.put("metaCreator", metaCreator);
        componentProperties.put("metaCopyRight", metaCopyRight);

        Node media = getFirstMediaNode(_currentPage);
        //set display area size to first media node
        if(media != null && !media.getPath().equals(_currentNode.getPath())){
            if(media.hasProperty("lightboxHeight")){
                componentProperties.put("lightboxHeight", media.getProperty("lightboxHeight").getValue().toString());
            }else{
                componentProperties.put("lightboxHeight","");
            }

            if(media.hasProperty("lightboxWidth")){
                componentProperties.put("lightboxWidth", media.getProperty("lightboxWidth").getValue().toString());
            }else{
                componentProperties.put("lightboxWidth","");
            }
        }

    }



%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:choose>
    <c:when test="${componentProperties.variant == 'default' and not empty componentProperties.href}">
        <%@ include file="variant.default.jsp" %>
    </c:when>

    <c:otherwise>
        <%@ include file="variant.empty.jsp" %>
    </c:otherwise>
</c:choose>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
