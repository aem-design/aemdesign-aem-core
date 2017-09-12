<%@ page trimDirectiveWhitespaces="true"  %>
<%@ page import="com.adobe.granite.asset.api.AssetManager" %>
<%@ page import="com.day.cq.commons.ImageResource" %>
<%@ page import="com.day.image.Layer" %>
<%@ page import="com.adobe.granite.asset.api.AssetMetadata" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ include file="imagedata.jsp" %>
<%

    final String DEFAULT_I18N_CATEGORY = "image";
    final String DEFAULT_I18N_LABEL_LICENSEINFO = "licenseinfo";
    final String DEFAULT_ARIA_ROLE = "banner";
    final String FIELD_LINKURL = "linkURL";
    final String DEFAULT_TITLE_TAG_TYPE = "h4";

    // {
    //   1 required - property name, [name]
    //   2 required - default value, [defaultValue]
    //   3 optional - name of component attribute to add value into [attributeName]
    //   4 optional - canonical name of class for handling multivalues, String or Tag [stringValueTypeClass]
    // }
    Object[][] componentFields = {
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {"imageOption", IMAGE_OPTION_RESPONSIVE},
            {ImageResource.PN_HTML_WIDTH, ""},
            {ImageResource.PN_HTML_HEIGHT, ""},
            {ImageResource.PN_WIDTH, 0},
            {ImageResource.PN_HEIGHT, 0},
            {IMAGE_FILEREFERENCE, ""},
            {"renditionImageMapping", new String[0]},
            {FIELD_LINKURL,StringUtils.EMPTY},
            {"renditionPrefix",StringUtils.EMPTY},
            {FIELD_ARIA_ROLE,DEFAULT_ARIA_ROLE, "role"},
            {FIELD_TITLE_TAG_TYPE, DEFAULT_TITLE_TAG_TYPE},
    };


    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_ASSET_IMAGE,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);


    String fileReference = componentProperties.get(IMAGE_FILEREFERENCE, "");

    if (isNotEmpty(fileReference)) {

        //get asset
        AssetManager assetManager = _resourceResolver.adaptTo(AssetManager.class);
        com.adobe.granite.asset.api.Asset asset = assetManager.getAsset(fileReference);
        Resource assetR = _resourceResolver.resolve(fileReference);
        Asset assetBasic = assetR.adaptTo(Asset.class);
        Node assetN = assetR.adaptTo(Node.class);

        //get asset metadata
        String assetTags = getMetadataStringForKey(assetN, TagConstants.PN_TAGS, "");
        String assetUsageTerms = assetBasic.getMetadataValue(DAM_FIELD_LICENSE_USAGETERMS);
        String licenseInfo = getAssetCopyrightInfo(assetBasic, _i18n.get(DEFAULT_I18N_LABEL_LICENSEINFO, DEFAULT_I18N_CATEGORY));
        componentProperties.put("licenseInfo", licenseInfo);

        //ensure licensed image meta does not get overwritten
        if (isNotEmpty(licenseInfo)) {
            //get asset properties and overwrite all existing ones specified in component
            ComponentProperties assetProperties = getAssetProperties(pageContext, asset, DEFAULT_FIELDS_ASSET_IMAGE);
            //need to remove all non field specific
            componentProperties.putAll(assetProperties);
            LOG.debug("image metadata can't be overridden for licensed image");
        }

        String title = componentProperties.get(DAM_TITLE, "");
        if (isEmpty(title)) {
            componentProperties.put(DAM_TITLE, assetBasic.getName());
        }

        //get page link
        String linkURL = componentProperties.get(FIELD_LINKURL, StringUtils.EMPTY);
        if (isNotEmpty(linkURL)) {
            Page imageTargetPage = _pageManager.getPage(linkURL);
            if (imageTargetPage != null) {
                linkURL = getPageUrl(imageTargetPage);
            }
        } else {
            //use dam link source from Asset if not assigned
            linkURL = _xssAPI.getValidHref(componentProperties.get(DAM_SOURCE_URL, StringUtils.EMPTY));
        }
        componentProperties.put("linkURL", linkURL);

        try {
            String imageOption = componentProperties.get("imageOption", IMAGE_OPTION_RESPONSIVE);

//            out.print(assetBasic.getMetadata(DAM_FIELD_LICENSE_EXPIRY));

            switch (imageOption) {
                case IMAGE_OPTION_GENERATED:
                    String imageHref = getResourceImageCustomHref(_resource,_component.getCellName());
                    componentProperties.put("imageURL", imageHref);
                    break;
                case IMAGE_OPTION_RENDITION:
                    int targetWidth = componentProperties.get(ImageResource.PN_WIDTH, 0);
                    componentProperties.put("imageURL", getBestFitRendition(targetWidth, asset));
                    break;
                default:
                    //get specified mapping values if any
                    String[] renditionImageMapping = componentProperties.get("renditionImageMapping", new String[0]);
                    //verify and convert to profile map
                    Map<Integer, String> widthRenditionProfileMapping = getWidthProfileMap(renditionImageMapping);
                    //get rendition profile prefix selected
                    String renditionPrefix = componentProperties.get("renditionPrefix", "");
                    //get best fit renditions set
                    Map<Integer, String> responsiveImageSet = getBestFitRenditionSet(asset, widthRenditionProfileMapping, renditionPrefix);
                    componentProperties.put("renditions", responsiveImageSet);
                    //pick last one from collection
                    Collection<String> values= responsiveImageSet.values();
                    if (values.size() > 0) {
                        componentProperties.put("imageURL", values.toArray()[values.size()-1]);
                    }
            }

        } catch (Exception ex) {
            LOG.error("failed to create Width and Image Mapping : " + ex.getMessage(), ex);
        }

    }
%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:choose>
    <c:when test="${empty componentProperties.fileReference}">
        <%@include file="variant.empty.jsp" %>
    </c:when>
    <c:when test="${componentProperties.variant eq 'imageTitleDescription'}">
        <%@include file="variant.imageTitleDescription.jsp" %>
    </c:when>
    <c:when test="${componentProperties.variant eq 'card'}">
        <%@include file="variant.card.jsp" %>
    </c:when>
    <c:otherwise>
        <%@include file="variant.default.jsp" %>
    </c:otherwise>
</c:choose>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>

