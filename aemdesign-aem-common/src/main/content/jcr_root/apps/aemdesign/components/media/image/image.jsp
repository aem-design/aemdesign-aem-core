<%@ page trimDirectiveWhitespaces="true"  %>
<%@ page import="com.adobe.granite.asset.api.AssetManager" %>
<%@ page import="com.day.cq.commons.ImageResource" %>
<%@ page import="com.day.image.Layer" %>
<%@ page import="com.adobe.granite.asset.api.AssetMetadata" %>
<%@ page import="java.net.URI" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ include file="common.jsp" %>
<%

    final String DEFAULT_I18N_CATEGORY = "image";
    final String DEFAULT_I18N_LABEL_LICENSEINFO = "licenseinfo";
    final String DEFAULT_ARIA_ROLE = "banner";
    final String FIELD_LINKURL = "linkURL";
    final String FIELD_IMAGEURL = "imageURL";
    final String FIELD_RENDITIONS = "renditions";
    final String FIELD_RENDITION_PREFIX = "renditionPrefix";
    final String FIELD_RESPONSIVE_MAP = "renditionImageMapping";
    final String FIELD_ADAPTIVE_MAP = "adaptiveImageMapping";
    final String FIELD_IMAGE_OPTION = "imageOption";
    final String DEFAULT_TITLE_TAG_TYPE = "h4";
    final String[] DEFAULT_RENDITION_IMAGE_MAP = new String[]{
            "48=(min-width: 1px) and (max-width: 72px)",
            "140=(min-width: 73px) and (max-width: 210px)",
            "319=(min-width: 211px) and (max-width: 478px)",
            "1280=(min-width: 478px)"
    };


    final String[] DEFAULT_ADAPTIVE_IMAGE_MAP = new String[]{
            "480.medium=(min-width: 1px) and (max-width: 533px)",
            "640.medium=(min-width: 534px) and (max-width: 691px)",
            "720.medium=(min-width: 692px) and (max-width: 770px)",
            "800.medium=(min-width: 771px) and (max-width: 848px)",
            "960.medium=(min-width: 849px) and (max-width: 1008px)",
            "1024.medium=(min-width: 1009px) and (max-width: 1075px)",
            "1280.medium=(min-width: 1076px) and (max-width: 1331px)",
            "full=(min-width: 1332px)"
    };
    // {
    //   1 required - property name, [name]
    //   2 required - default value, [defaultValue]
    //   3 optional - name of component attribute to add value into [attributeName]
    //   4 optional - canonical name of class for handling multivalues, String or Tag [stringValueTypeClass]
    // }
    Object[][] componentFields = {
            {FIELD_VARIANT, IMAGE_OPTION_RENDITION},
            {FIELD_IMAGE_OPTION, IMAGE_OPTION_RESPONSIVE},
            {ImageResource.PN_HTML_WIDTH, ""},
            {ImageResource.PN_HTML_HEIGHT, ""},
            {ImageResource.PN_WIDTH, 0},
            {ImageResource.PN_HEIGHT, 0},
            {IMAGE_FILEREFERENCE, ""},
            {FIELD_ADAPTIVE_MAP, DEFAULT_ADAPTIVE_IMAGE_MAP},
            {FIELD_RESPONSIVE_MAP, DEFAULT_RENDITION_IMAGE_MAP},
            {FIELD_LINKURL,StringUtils.EMPTY},
            {FIELD_RENDITION_PREFIX,StringUtils.EMPTY},
            {FIELD_ARIA_ROLE,DEFAULT_ARIA_ROLE, FIELD_ARIA_DATA_ATTRIBUTE_ROLE},
            {FIELD_TITLE_TAG_TYPE, DEFAULT_TITLE_TAG_TYPE},
    };


    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY,
            DEFAULT_FIELDS_ANALYTICS);


    String fileReference = componentProperties.get(IMAGE_FILEREFERENCE, "");

    Boolean fileReferenceMissing = true;

    if (isNotEmpty(fileReference)) {

        //get asset
        Resource assetR = _resourceResolver.resolve(fileReference);
        if (!ResourceUtil.isNonExistingResource(assetR)) {

            fileReferenceMissing = false;

            AssetManager assetManager = _resourceResolver.adaptTo(AssetManager.class);
            com.adobe.granite.asset.api.Asset asset = assetManager.getAsset(fileReference);

            Asset assetBasic = assetR.adaptTo(Asset.class);
            Node assetN = assetR.adaptTo(Node.class);

            //get asset metadata
            String assetUID = asset.getIdentifier();
            String assetTags = getMetadataStringForKey(assetN, TagConstants.PN_TAGS, "");
            String assetUsageTerms = assetBasic.getMetadataValue(DAM_FIELD_LICENSE_USAGETERMS);
            String licenseInfo = getAssetCopyrightInfo(assetBasic, _i18n.get(DEFAULT_I18N_LABEL_LICENSEINFO, DEFAULT_I18N_CATEGORY));
            componentProperties.put(FIELD_LICENSE_INFO, licenseInfo);
            componentProperties.put(FIELD_ASSETID, assetUID);


            //get asset properties
            ComponentProperties assetProperties = getAssetProperties(pageContext, asset, DEFAULT_FIELDS_ASSET_IMAGE);
            //add asset properties to component properties and ensure licensed image meta does not get overwritten
            componentProperties.putAll(assetProperties, isEmpty(licenseInfo));

            //if asset is not licensed
            if (isEmpty(licenseInfo)) {
                //get asset properties override from component
                ComponentProperties assetPropertiesOverride = getComponentProperties(pageContext, null, false, DEFAULT_FIELDS_ASSET_IMAGE);

                //add asset properties override to component properties and ensure licensed image meta does not get overwritten
                componentProperties.putAll(assetPropertiesOverride, isEmpty(licenseInfo));

            }

            //ensure something is added as title
            String title = componentProperties.get(DAM_TITLE, "");
            if (isEmpty(title)) {
                componentProperties.put(DAM_TITLE, assetBasic.getName());
            }

            //update attributes - consider updating to using componentProperties.attr
            Object[][] componentAttibutes = {
                    {"data-" + FIELD_ASSETID, assetUID},
                    {"data-" + FIELD_ASSET_TRACKABLE, true},
                    {"data-" + FIELD_ASSET_LICENSED, isNotBlank(licenseInfo)},
                    {FIELD_DATA_ANALYTICS_EVENT_LABEL, componentProperties.get(DAM_TITLE)},
                    {FIELD_DATA_ANALYTICS_METATYPE, assetBasic.getMimeType()},
                    {FIELD_DATA_ANALYTICS_FILENAME, assetBasic.getPath()},
            };
            componentProperties.put(COMPONENT_ATTRIBUTES, addComponentAttributes(componentProperties, componentAttibutes));

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
            componentProperties.put(FIELD_LINKURL, linkURL);

            try {
                String imageOption = componentProperties.get(FIELD_IMAGE_OPTION, IMAGE_OPTION_RESPONSIVE);
                Map<String, String> responsiveImageSet = new LinkedHashMap<String, String>();

                switch (imageOption) {
                    case IMAGE_OPTION_GENERATED:
                        String imageHref = "";
                        Long lastModified = getLastModified(_resource);
                        imageHref = MessageFormat.format(DEFAULT_IMAGE_GENERATED_FORMAT, _resource.getPath(), lastModified.toString());

                        componentProperties.put(FIELD_IMAGEURL, imageHref);
                        break;
                    case IMAGE_OPTION_RENDITION:
                        int targetWidth = componentProperties.get(ImageResource.PN_WIDTH, 0);
                        com.adobe.granite.asset.api.Rendition bestRendition = getBestFitRendition(targetWidth, asset);
                        if (bestRendition != null) {
                            componentProperties.put(FIELD_IMAGEURL, bestRendition.getPath());
                        }
                        break;
                    case IMAGE_OPTION_ADAPTIVE:
                        String[] adaptiveImageMapping = componentProperties.get(FIELD_ADAPTIVE_MAP, DEFAULT_ADAPTIVE_IMAGE_MAP);

                        responsiveImageSet = getAdaptiveImageSet(adaptiveImageMapping, _resourceResolver, _resource.getPath(), fileReference, null, false, _sling);

                        componentProperties.put(FIELD_RENDITIONS, responsiveImageSet);

                        break;
                    default: //IMAGE_OPTION_RESPONSIVE
                        String[] renditionImageMapping = componentProperties.get(FIELD_RESPONSIVE_MAP, DEFAULT_RENDITION_IMAGE_MAP);

                        // Check if the image suffix is '.svg', if it is skip any rendition checks and simply return
                        // the path to it as no scaling or modifications should be applied.
                        if (fileReference.endsWith(".svg")) {
                            componentProperties.put(FIELD_IMAGEURL, fileReference);
                            componentProperties.put(FIELD_IMAGE_OPTION, "simple");
                        } else {
                            //get rendition profile prefix selected
                            String renditionPrefix = componentProperties.get(FIELD_RENDITION_PREFIX, "");

                            //get best fit renditions set
                            responsiveImageSet = getBestFitMediaQueryRenditionSet(asset, renditionImageMapping, renditionPrefix);

                            componentProperties.put(FIELD_RENDITIONS, responsiveImageSet);
                        }
                }

                //pick last one from collection
                if (responsiveImageSet.values().size() > 0) {
                    componentProperties.put(FIELD_IMAGEURL, responsiveImageSet.values()
                            .toArray()[responsiveImageSet.values().size() - 1]);
                }

            } catch (Exception ex) {
                LOG.error("failed to create Width and Image Mapping : " + ex.getMessage(), ex);
            }
        }
    }

    componentProperties.put("fileReferenceMissing",fileReferenceMissing);

%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:choose>
    <c:when test="${empty componentProperties.fileReference or componentProperties.fileReferenceMissing}">
        <%@include file="variant.empty.jsp" %>
    </c:when>
    <c:when test="${componentProperties.variant eq 'imageTitleDescription'}">
        <%@include file="variant.imageTitleDescription.jsp" %>
    </c:when>
    <c:when test="${componentProperties.variant eq 'imageDescription'}">
        <%@include file="variant.imageDescription.jsp" %>
    </c:when>
    <c:when test="${componentProperties.variant eq 'card'}">
        <%@include file="variant.card.jsp" %>
    </c:when>
    <c:otherwise>
        <%@include file="variant.default.jsp" %>
    </c:otherwise>
</c:choose>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
