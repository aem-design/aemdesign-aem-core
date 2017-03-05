<%@ page import="com.day.cq.wcm.api.components.DropTarget,
                 com.day.cq.wcm.foundation.Image,
                 com.day.cq.wcm.foundation.Placeholder" %>

<%@ page import="com.adobe.granite.asset.api.AssetManager" %>
<%@ page import="com.day.cq.commons.ImageResource" %>
<%@ page import="com.day.image.Layer" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ include file="imagedata.jsp" %>
<%

    Object[][] assetFields = {
            {DamConstants.DC_TITLE, StringUtils.EMPTY},
            {DamConstants.DC_DESCRIPTION, StringUtils.EMPTY},
            {WKCD_DAM_CAPTION, StringUtils.EMPTY},
            {WKCD_DAM_ALT_TITLE, StringUtils.EMPTY},
            {WKCD_DAM_SOURCE_URL, StringUtils.EMPTY}
    };

    Object[][] componentFields = {
            {"variant", "imageOnly"},
            {"imageOption", IMAGE_OPTION_RESPONSIVE_RENDITION},
            {ImageResource.PN_WIDTH, 0},
            {ImageResource.PN_HEIGHT, 0},
            {"renditionImageMapping", new String[0]},
            {"adaptiveImageMapping", new String[0]},
            {"pageURL",StringUtils.EMPTY}
    };

    ComponentProperties componentProperties = getComponentProperties(pageContext, componentFields) ;

    //Only support the Drag and Drop Images
    Image image = new Image(_resource);

    image.setIsInUITouchMode(Placeholder.isAuthoringUIModeTouch(_slingRequest));

    //drop target css class = dd prefix + name of the drop target in the edit config
    image.addCssClass(DROP_TARGET_CSS_PREFIX_IMAGE);

    image.loadStyleData(_currentStyle);
    image.setSelector(".img"); // use image script
    image.setDoctype(Doctype.fromRequest(_slingRequest));

    // add design information if not default (i.e. for reference paras)
    if (!_currentDesign.equals(_resourceDesign)) {
        image.setSuffix(_currentDesign.getId());
    }

    componentProperties.put("imageHasContent", image.hasContent());

    String divId = componentProperties.get("componentId", StringUtils.EMPTY);

    if (!StringUtils.isEmpty(image.getFileReference())) {

        AssetManager assetManager = _resourceResolver.adaptTo(AssetManager.class);
        com.adobe.granite.asset.api.Asset asset = assetManager.getAsset(image.getFileReference());
        ComponentProperties assetProperties = getAssetProperties(pageContext, asset, assetFields);

        componentProperties.putAll(assetProperties);

        image.setTitle(componentProperties.get(DamConstants.DC_TITLE, String.class));
        image.setDescription(componentProperties.get(DamConstants.DC_DESCRIPTION, String.class));
        image.setAlt(componentProperties.get(WKCD_DAM_ALT_TITLE, String.class));
        //Don't set linkURL because there is no option for Image.class to make anchor to open new Tab
        //image.set(ImageResource.PN_LINK_URL, componentProperties.get(WKCD_DAM_SOURCE_URL, String.class));

        String imageTargetURL = StringUtils.EMPTY;

        //ImageOnly does not apply pageURL
        if (componentProperties.get("variant", StringUtils.EMPTY).equals("imageOnly") == false) {

            imageTargetURL = componentProperties.get("pageURL", StringUtils.EMPTY);
            Page imageTargetPage = _pageManager.getPage(imageTargetURL);
            if (imageTargetPage != null) {
                imageTargetURL = getPageUrl(imageTargetPage);
            }

        }

        if (StringUtils.isEmpty(imageTargetURL)) {
            //Source URL does not use pathfield option
            //Garbage in Garbage Out
            imageTargetURL = componentProperties.get(WKCD_DAM_SOURCE_URL, StringUtils.EMPTY);

        }

        //Image
        componentProperties.put("image", image);

        componentProperties.put("imageTargetURL", imageTargetURL);

        Map<Integer, String> widthRenditionProfileMapping = new HashMap<Integer, String>();
        try {
            String[] renditionImageMapping = (String[]) componentProperties.get("renditionImageMapping", new String[0]);

            //out.println("widthRenditionProfileMapping : "+widthRenditionProfileMapping);

            Map<Integer, String> responsiveImageSet = new HashMap<Integer, String>();

            String imageOption = componentProperties.get("imageOption", IMAGE_OPTION_RESPONSIVE_RENDITION);
            //out.println("imageOption "+ imageOption);
            if (imageOption.equals(IMAGE_OPTION_FIXED_IMAGE_GENERATED)) {

                Integer width = componentProperties.get(Image.PN_WIDTH, Integer.class);
                Integer height = componentProperties.get(Image.PN_HEIGHT, Integer.class);
                if (width != null) {

                    width = this.getDimension(width, _currentStyle);

                    if (width != null) {
                        image.set(Image.PN_HTML_WIDTH, String.valueOf(width));
                    }

                }

                if (height != null) {

                    height = this.getDimension(height, _currentStyle);

                    if (height != null) {
                        image.set(Image.PN_HTML_HEIGHT, String.valueOf(height));
                    }

                }

            } else if (imageOption.equals(IMAGE_OPTION_FIXED_IMAGE_RENDITION)) {
                int targetWidth = componentProperties.get(ImageResource.PN_WIDTH, 0);
                widthRenditionProfileMapping = this.getWidthProfileMap(_currentStyle, targetWidth);
                //out.println("IMAGE_OPTION_RESPONSIVE_RENDITION : "+widthRenditionProfileMapping);
                responsiveImageSet = this.filterFixedRenditionImageSet(asset, widthRenditionProfileMapping, _resourceResolver);
            } else if (imageOption.equals(IMAGE_OPTION_RESPONSIVE_RENDITION)) {
                widthRenditionProfileMapping = this.getWidthProfileMap(renditionImageMapping);

                responsiveImageSet = this.filterRenditionImageSet(asset, widthRenditionProfileMapping, _resourceResolver, true);
                //out.println("responsiveRendition : "+widthRenditionProfileMapping);
                //out.println("responsiveRendition : "+responsiveImageSet);
            } else if (imageOption.equals(IMAGE_OPTION_RESPONSIVE_RENDITION_OVERRIDE)) {

                widthRenditionProfileMapping = this.getWidthProfileMap(renditionImageMapping);
                responsiveImageSet = this.filterRenditionImageSet(asset, widthRenditionProfileMapping, _resourceResolver, false);
                //out.println("responsiveRenditionOverride : "+widthRenditionProfileMapping);
                //out.println("responsiveRenditionOverride : "+responsiveImageSet);
            } else if (imageOption.equals(IMAGE_OPTION_RESPONSIVE_GENERATED)) {

                String[] adaptiveImageMapping = (String[]) componentProperties.get("adaptiveImageMapping", new String[0]);
                widthRenditionProfileMapping = this.getWidthProfileMap(adaptiveImageMapping);

                int[] supportedWidths = this.getAdaptiveImageSupportedWidths(_sling);

                String path = request.getContextPath() + _resource.getPath();

                // Handle extensions on both fileReference and file type images
                String extension = "jpg";
                String suffix = "";
                if (image.getFileReference().length() != 0) {
                    extension = image.getFileReference().substring(image.getFileReference().lastIndexOf(".") + 1);
                    suffix = image.getSuffix();
                    suffix = suffix.substring(0, suffix.indexOf('.') + 1) + extension;
                } else {
                    Resource fileJcrContent = _resource.getChild("file").getChild("jcr:content");
                    if (fileJcrContent != null) {
                        ValueMap fileProperties = fileJcrContent.adaptTo(ValueMap.class);
                        String mimeType = fileProperties.get("jcr:mimeType", "jpg");
                        extension = mimeType.substring(mimeType.lastIndexOf("/") + 1);
                    }
                }
                extension = _xssAPI.encodeForHTMLAttr(extension);

                path = path + ".img.{0}." + extension + suffix;

                responsiveImageSet = this.filterAdaptiveImageSet(supportedWidths, widthRenditionProfileMapping, _resourceResolver, path);
            }
            componentProperties.put("responsiveImageSet", responsiveImageSet);


        } catch (Exception ex) {
            LOG.error("failed to create Width and Image Mapping : " + ex.getMessage(), ex);
        }

        ResourceResolver adminResourceResolver = this.openAdminResourceResolver(_sling);

        try {
            TagManager _adminTagManager = adminResourceResolver.adaptTo(TagManager.class);

            componentProperties.putAll(getComponentStyleProperties(pageContext));

            componentProperties.put("componentAttributes", compileComponentAttributes(_adminTagManager, componentProperties, _component));

            List<Tag> tags = getTags(_adminTagManager, _currentNode, TagConstants.PN_TAGS);

            componentProperties.put("tags", tags);

        } catch (Exception ex) {
            out.write(Throwables.getStackTraceAsString(ex));
        } finally {
            this.closeAdminResourceResolver(adminResourceResolver);
        }
    }
%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:choose>

    <c:when test="${componentProperties.imageHasContent eq false}">
        <div id="<%= xssAPI.encodeForHTMLAttr(divId) %>"><% image.draw(out); %></div>
    </c:when>
    <c:when test="${componentProperties.variant eq 'imageTitleDescription'}">
        <div ${componentProperties.componentAttributes} role="banner">
            <figure style="opacity: 1;">
                <c:if test="${not empty componentProperties.imageTargetURL}">
                    <a href="${componentProperties.imageTargetURL}"> ${componentProperties.image.title}</a>
                </c:if>
                <c:choose>
                    <%-- Responsive Image --%>
                    <c:when test="${componentProperties.imageOption eq 'fixedImageRendition' ||
                                    componentProperties.imageOption eq 'responsiveRendition' ||
                                    componentProperties.imageOption eq 'responsiveRenditionOverride'||
                                    componentProperties.imageOption eq 'responsiveGenerated'}">
                        <%@include file="template.responsiveImage.jsp" %>
                    </c:when>
                    <%-- Normal Image --%>
                    <c:otherwise>
                        <%@include file="template.normalImage.jsp" %>
                    </c:otherwise>
                </c:choose>

                <figcaption>
                    <c:if test="${not empty componentProperties.imageTargetURL}">
                    <a href="${componentProperties.imageTargetURL}" title="${componentProperties.image.title}">
                        </c:if>
                        <strong>${componentProperties.image.title}</strong>
                        <c:if test="${not empty componentProperties.imageTargetURL}">
                    </a>
                    </c:if>
                    <p>${componentProperties.image.description}</p>
                </figcaption>
            </figure>

        </div>
    </c:when>
    <c:otherwise>
        <div ${componentProperties.componentAttributes}>
            <c:choose>
                <%-- Responsive Image --%>
                <c:when test="${componentProperties.imageOption eq 'fixedImageRendition' ||
                                    componentProperties.imageOption eq 'responsiveRendition' ||
                                    componentProperties.imageOption eq 'responsiveRenditionOverride'||
                                    componentProperties.imageOption eq 'responsiveGenerated'}">
                    <%@include file="template.responsiveImage.jsp" %>
                </c:when>
                <%-- Normal Image --%>
                <c:otherwise>
                    <%@include file="template.normalImage.jsp" %>
                </c:otherwise>
            </c:choose>
        </div>
    </c:otherwise>
</c:choose>

<%-- for OOTB tracking  --%>

<%
    //Set linkURL for tracking only
    image.set(ImageResource.PN_LINK_URL, componentProperties.get(WKCD_DAM_SOURCE_URL, String.class));
%>
<%
    if (StringUtils.isNotEmpty(divId)){
%>
<%@include file="tracking-js.jsp" %>
<%
    }
%>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>