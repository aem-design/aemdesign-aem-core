<%@ page import="com.day.cq.wcm.foundation.Download,
                     org.apache.sling.xss.ProtectionContext,
                     org.apache.sling.xss.XSSFilter,
					com.day.cq.dam.api.DamConstants" %>
<%@ page import="java.text.MessageFormat" %>
<%@ page import="org.apache.commons.lang3.*" %>

<%@ include file="/apps/aemdesign/global/global.jsp"%>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="downloadData.jsp" %>
<%

    final String DEFAULT_THUMBNAIL_CUSTOM = "no";

    Object[][] componentFields = {

    };

    ComponentProperties componentProperties = getComponentProperties(pageContext, componentFields);
    componentProperties.putAll(getComponentStyleProperties(pageContext));

    componentProperties.put("componentAttributes", compileComponentAttributesAsAdmin(componentProperties,_component,_sling));

    Download dld = new Download(_resource);
    if (dld.hasContent()) {
        dld.addCssClass(ddClassName);

        String displayText = dld.getInnerHtml() == null ? dld.getFileName() : dld.getInnerHtml().toString();
        String title = getDownloadTitle(_properties, displayText);
        String description = dld.getDescription();
        if (isEmpty(description)) {
            description = title;
        }
        String mimeType = getDownloadMimeType(_resourceResolver, dld);

        Asset asset = dld.getResourceResolver().resolve(dld.getHref()).adaptTo(Asset.class);
        Node assetN = asset.adaptTo(Node.class);

        String thumbnailImageDefaultString = _properties.get("thumbnailCustom", DEFAULT_THUMBNAIL_CUSTOM);
        boolean thumbnailImageDefault = BooleanUtils.toBoolean(thumbnailImageDefaultString);

        componentProperties.put("download", dld);
        componentProperties.put("isDownload", dld.hasContent());
        componentProperties.put("title", title);
        componentProperties.put("altTitle", _xssAPI.encodeForHTMLAttr(title));
        componentProperties.put("thumbnailImageDefault", thumbnailImageDefault);
        componentProperties.put("assetTitle", asset.getMetadataValue(DamConstants.DC_TITLE));
        componentProperties.put("assetDescription", getDownloadDescription(_properties, description));
        componentProperties.put("useDocumentIcon", _properties.get("useDocumentIcon", false));
        componentProperties.put("mimeType", _i18n.get(mimeType, "download"));
        componentProperties.put("href", mappedUrl(dld.getHref()));
        componentProperties.put("thumbnailWidth", _properties.get("thumbnailWidth", StringUtils.EMPTY));
        componentProperties.put("thumbnailHeight", _properties.get("thumbnailHeight", StringUtils.EMPTY));
        componentProperties.put("contentSize", getFormattedDownloadSize(dld));
        componentProperties.put("target", "target='_blank'");
        componentProperties.put("compCSSClass", _properties.get("cssClass", StringUtils.EMPTY));


        //if the image has a rendition lets try to yse it.
        Resource assetRendition = getThumbnail(asset, DEFAULT_IMAGE_PATH_SELECTOR);
        if (thumbnailImageDefault == false || assetRendition == null) {
            componentProperties.put("assetPath", DEFAULT_DOWNLOAD_THUMB_ICON);
        } else {
            componentProperties.put("assetPath", assetRendition.getPath());
        }

        XSSFilter xss = _sling.getService(XSSFilter.class);
        if (xss != null) {
            componentProperties.put("displayText", xss.filter(ProtectionContext.PLAIN_HTML_CONTENT, displayText));
            componentProperties.put("description", xss.filter(ProtectionContext.PLAIN_HTML_CONTENT, description));
        }

        //DTM
        String dtmEvent = getMetadataStringForKey(assetN, "dtmEvent", "");
        String assetTags = getMetadataStringForKey(assetN, TagConstants.PN_TAGS, "");

        if (isNotEmpty(dtmEvent)) {
            componentProperties.put("dtmEvent", MessageFormat.format("data-dtm=\"{0}\"", dtmEvent));
        }
        if (isNotEmpty(assetTags)) {
            componentProperties.put("assetTags", MessageFormat.format("data-tags=\"{0}\"", assetTags));
        }
    }

%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>

<c:choose>
    <c:when test="${componentProperties.isDownload}">
        <%@include file="style.default.jsp" %>
    </c:when>
    <c:when test="${CURRENT_WCMMODE eq WCMMODE_EDIT}">
        <%@include file="style.empty.jsp" %>
    </c:when>
    <c:otherwise>

    </c:otherwise>

</c:choose>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
