<%@ page import="com.day.cq.wcm.foundation.Download,
                    com.day.cq.dam.api.DamConstants" %>
<%@ page import="java.text.MessageFormat" %>
<%@ include file="/apps/aemdesign/global/global.jsp"%>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="/apps/aemdesign/global/i18n.jsp" %>
<%@ include file="downloadData.jsp" %>
<%

    final String DEFAULT_I18N_CATEGORY = "download";
    final String DEFAULT_I18N_LABEL = "downloadlabel";
    final String DEFAULT_TITLE_TAG_TYPE = "h4";

    Object[][] componentFields = {
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {"thumbnailType", "icon"},
            {"label", getDefaultLabelIfEmpty("",DEFAULT_I18N_CATEGORY,DEFAULT_I18N_LABEL,DEFAULT_I18N_CATEGORY,_i18n)},
            {"thumbnailWidth","", "thumbnailWidth"},
            {"thumbnailHeight","","thumbnailHeight"},
            {"title",""},
            {FIELD_TITLE_TAG_TYPE, DEFAULT_TITLE_TAG_TYPE},
            {"description",""},
            {"fileName",""},
            {"fileReference",""},

    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY,
            DEFAULT_FIELDS_ANALYTICS);

    Download dld = new Download(_resource);
    componentProperties.put("hasContent", dld.hasContent());
    if (dld.hasContent()) {

        String mimeType = getDownloadMimeType(_resourceResolver, dld);
        String mimeTypeLabel = _i18n.get(mimeType, DEFAULT_I18N_CATEGORY);

        Asset asset = dld.getResourceResolver().resolve(dld.getHref()).adaptTo(Asset.class);
        Node assetN = asset.adaptTo(Node.class);

        String href = mappedUrl(_resourceResolver, dld.getHref());
        String assetDescription = asset.getMetadataValue(DamConstants.DC_DESCRIPTION);
        String assetTitle = asset.getMetadataValue(DamConstants.DC_TITLE);
        String assetTags = getMetadataStringForKey(assetN, TagConstants.PN_TAGS, "");

        String assetUsageTerms = asset.getMetadataValue(DAM_FIELD_LICENSE_USAGETERMS);
        String licenseInfo = getAssetCopyrightInfo(asset, _i18n.get("licenseinfo", DEFAULT_I18N_CATEGORY));

        //override title and description if image has rights
        String title = componentProperties.get("title","");
        if (isNotEmpty(licenseInfo) || (isEmpty(title) && isNotEmpty(assetTitle))) {
            componentProperties.put("title",assetTitle);
        }
        String description = componentProperties.get("description","");
        if (isNotEmpty(licenseInfo) || (isEmpty(description) && isNotEmpty(assetDescription))) {
            componentProperties.put("description",assetDescription);
        }

        componentProperties.put("licenseInfo", licenseInfo);


        componentProperties.put("mimeTypeLabel", mimeTypeLabel);
        componentProperties.put("assetTitle", assetTitle);
        componentProperties.put("assetDescription", assetDescription);
        componentProperties.put("href", href);

        String thumbnailType = componentProperties.get("thumbnailType","");

        componentProperties.put("thumbnail", DEFAULT_IMAGE_BLANK);

        if (thumbnailType.equals("dam")) {

            Rendition assetRendition = getThumbnail(asset, DEFAULT_THUMB_WIDTH_SM);

            if (assetRendition == null) {
                componentProperties.put("thumbnail", DEFAULT_DOWNLOAD_THUMB_ICON);
            } else {
                componentProperties.put("thumbnail", assetRendition.getPath());
            }
        } else if (thumbnailType.equals("customdam")) {

            String thumbnailImagePath = getResourceImagePath(_resource,"thumbnail");

            Resource thumbnailImage = _resourceResolver.resolve(thumbnailImagePath);

            if (thumbnailImage == null) {
                thumbnailImagePath = DEFAULT_DOWNLOAD_THUMB_ICON;
            } else {
                Rendition assetRendition = getThumbnail(thumbnailImage.adaptTo(Asset.class), DEFAULT_THUMB_WIDTH_SM);

                thumbnailImagePath = assetRendition.getPath();
            }

            componentProperties.put("thumbnail", thumbnailImagePath);

        } else if (thumbnailType.equals("custom")) {

            String thumbnailImage = getResourceImageCustomHref(_resource,"thumbnail");

            if (isEmpty(thumbnailImage)) {
                componentProperties.put("thumbnail", DEFAULT_DOWNLOAD_THUMB_ICON);
            } else {
                componentProperties.put("thumbnail", thumbnailImage);
            }

        } else if (thumbnailType.equals("icon") || isEmpty(thumbnailType)) {
            componentProperties.put("iconType",dld.getIconType());
        }

        Object[][] componentAttibutes = {
                {"href",href},
                {"data-tags",assetTags},
        };

        componentProperties.put(COMPONENT_ATTRIBUTES, addComponentAttributes(componentProperties,componentAttibutes));

        componentProperties.put("info",MessageFormat.format("({0}, {1})", getFormattedDownloadSize(dld), mimeTypeLabel));

    }

%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:choose>
    <c:when test="${componentProperties.hasContent and componentProperties.variant eq 'simple'}">
        <%@ include file="variant.simple.jsp" %>
    </c:when>
    <c:when test="${componentProperties.hasContent and componentProperties.variant eq 'default'}">
        <%@ include file="variant.default.jsp" %>
    </c:when>
    <c:when test="${componentProperties.hasContent and componentProperties.variant eq 'card'}">
        <%@ include file="variant.card.jsp" %>
    </c:when>
    <c:otherwise>
        <%@include file="variant.empty.jsp" %>
    </c:otherwise>
</c:choose>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
