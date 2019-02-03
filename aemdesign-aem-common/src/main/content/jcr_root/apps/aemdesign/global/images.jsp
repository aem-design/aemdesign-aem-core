<%@ page import="com.adobe.xmp.XMPDateTime" %>
<%@ page import="com.day.cq.dam.api.Asset" %>
<%@ page import="com.day.cq.dam.api.DamConstants" %>
<%@ page import="com.day.cq.dam.api.Rendition" %>
<%@ page import="com.day.cq.dam.commons.util.DamUtil" %>
<%@ page import="com.day.cq.wcm.api.Page" %>
<%@ page import="com.day.cq.wcm.foundation.Image" %>
<%@ page import="com.day.image.Layer" %>
<%@ page import="com.google.common.collect.Lists" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="org.apache.sling.api.resource.Resource" %>
<%@ page import="org.apache.sling.api.resource.ResourceUtil" %>
<%@ page import="javax.jcr.Node" %>
<%@ page import="javax.jcr.RepositoryException" %>
<%@ page import="com.adobe.xmp.XMPDateTimeFactory" %>
<%@ page import="com.day.cq.commons.*" %>
<%@ page import="com.day.image.Layer" %>
<%@ page import="org.apache.commons.lang3.math.NumberUtils" %>
<%@ page import="java.net.URI" %>

<%!

    final String DEFAULT_IMAGE_THUMB_SELECTOR = ".thumb.319.319.png";
    final String SMALL_IMAGE_THUMB_SELECTOR = ".thumb.140.100.png";
    final String DEFAULT_THUMB_SELECTOR_XSM = ".thumb.140.140.png";
    final String DEFAULT_THUMB_SELECTOR_SM = ".thumb.319.319.png";
    final String DEFAULT_THUMB_SELECTOR_MD = ".thumb.800.800.png";
    final String DEFAULT_THUMB_SELECTOR_LG = ".thumb.1280.1280.png";

    final String FORM_CHOOSER_SELECTOR_SERVLET = ".form";

    final String DEFAULT_IMAGE_PATH = "/content/dam/aemdesign/common/placeholder.png";
    final String DEFAULT_IMAGE_PATH_RENDITION = "/content/dam/aemdesign/common/placeholder".concat(DEFAULT_IMAGE_THUMB_SELECTOR);

    final String SMALL_IMAGE_PATH_SELECTOR = "cq5dam" + SMALL_IMAGE_THUMB_SELECTOR;
    final String DEFAULT_IMAGE_PATH_SELECTOR = "cq5dam" + DEFAULT_IMAGE_THUMB_SELECTOR;
    final String DEFAULT_DOWNLOAD_THUMB_ICON = "/content/dam/aemdesign/common/download.png";

    final String MEDIUM_THUMBNAIL_SIZE = "320";
    final String LARGE_THUMBNAIL_SIZE = "480";

    final String DEFAULT_BACKGROUND_IMAGE_NODE_NAME = "bgimage";
    final String DEFAULT_BACKGROUND_VIDEO_NODE_NAME = "bgvideo";
    final String DEFAULT_SECONDARY_IMAGE_NODE_NAME = "secondaryImage";
    final String DEFAULT_THUMBNAIL_IMAGE_NODE_NAME = "thumbnail";
    final String DEFAULT_BADGETHUMBNAIL_IMAGE_NODE_NAME = "badgeThumbnail";
    final String DEFAULT_IMAGE_NODE_NAME = "image";

    final String FIELD_RENDITIONS_VIDEO = "renditionsVideo";


    final static String RENDITION_PROFILE_CUSTOM = "cq5dam.custom.";

    final static String RENDITION_REGEX_PATTERN = "^(\\w*)\\.(\\w*)\\.(\\d*)\\.(\\d*)\\.(\\S{3,4})$";

    //this is used to store admin config for image component
    final static String OSGI_CONFIG_MEDIA_IMAGE = "aemdesign.components.media.image";


    final String FIELD_RENDITION_PREFIX = "renditionPrefix";
    final String FIELD_RESPONSIVE_MAP = "renditionImageMapping";
    final String FIELD_ADAPTIVE_MAP = "adaptiveImageMapping";
    final String FIELD_MEDIAQUERYRENDITION_KEY = "assetMediaQuery";
    final String FIELD_MEDIAQUERYRENDITION_VALUE = "assetMediaQueryRendition";
    final String FIELD_IMAGE_OPTION = "imageOption";


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

    //DEFAULT NODE METADATA
    // {
    //   1 required - property name,
    //   2 required - default value,
    //   3 optional - name of component attribute to add value into
    //   4 optional - canonical name of class for handling multivalues, String or Tag
    // }
    public final Object[][] DEFAULT_FIELDS_BACKGROUNDIMAGE_OPTIONS = {
            {FIELD_IMAGE_OPTION, IMAGE_OPTION_RESPONSIVE},
            {ImageResource.PN_HTML_WIDTH, ""},
            {ImageResource.PN_HTML_HEIGHT, ""},
            {ImageResource.PN_WIDTH, 0},
            {ImageResource.PN_HEIGHT, 0},
            {IMAGE_FILEREFERENCE, ""},
            {FIELD_ADAPTIVE_MAP, DEFAULT_ADAPTIVE_IMAGE_MAP},
            {FIELD_RESPONSIVE_MAP, DEFAULT_RENDITION_IMAGE_MAP},
            {FIELD_RENDITION_PREFIX,StringUtils.EMPTY},
            {FIELD_MEDIAQUERYRENDITION_KEY,new String[]{}, "", Tag.class.getCanonicalName()},
            {FIELD_MEDIAQUERYRENDITION_VALUE,new String[0]},
    };


    //DEFAULT NODE METADATA
    // {
    //   1 required - property name,
    //   2 required - default value,
    //   3 optional - name of component attribute to add value into
    //   4 optional - canonical name of class for handling multivalues, String or Tag
    // }
    final Object[][] DEFAULT_FIELDS_BACKGROUNDVIDEO_OPTIONS = {
            {IMAGE_FILEREFERENCE, ""},
    };




    /***
     * get attributes from asset
     * @param pageContext
     * @param asset
     * @param fieldsLists list of fields definition Object{{name, defaultValue, attributeName, valueTypeClass},...}
     * @return
     */
    public ComponentProperties getAssetProperties(PageContext pageContext, com.adobe.granite.asset.api.Asset asset, Object[][]... fieldsLists) {
        return getComponentProperties(pageContext, asset, fieldsLists);
    }

    /**
     * Read properties for the Asset, use page properties to override asset metadata if page properties are set
     * @param pageContext
     * @param asset
     * @param name
     * @param usePage
     * @return Object
     */
    public Object getAssetProperty(PageContext pageContext, com.adobe.granite.asset.api.Asset asset, String name, Boolean usePage) {

        if (pageContext == null || asset == null) {
            return "";
        }

        if (usePage) {

            ValueMap properties = (ValueMap) pageContext.getAttribute("properties");


            String metadataValue = properties.get(name, String.class);
            if (StringUtils.isEmpty(metadataValue)){
                metadataValue = getMetadataStringForKey(asset , name);
            }

            return metadataValue;
        } else {

            return getMetadataStringForKey(asset , name);
        }
    }


    /**
     * Necessary to make sure some of the weird behavior CQ exhibits gets worked around. In
     * some cases (don't know when exactly), the asset dc:title and dc:description keys are
     * returned as Object[] with one element instead of a String. This method tests that
     * and returns the first element from the list or just the element itself
     *
     * @param assetNode is the asset to interogate
     * @param key is the key to get the metadata for
     * @return the value or null when nothing is found
     */
    protected String getMetadataStringForKey(Node assetNode, String key, String defaultValue) {
        if (assetNode == null) {
            return null;
        }
        if (StringUtils.isBlank(key)) {
            return null;
        }

        String returnVal = "";

        final String PROPERTY_METADATA = JcrConstants.JCR_CONTENT + "/metadata";
        try {

            if (assetNode.hasNode(PROPERTY_METADATA)) {
                Node metadataNode = assetNode.getNode(PROPERTY_METADATA);
                returnVal = DamUtil.getValue(metadataNode, key, defaultValue);
            }
        } catch ( RepositoryException rex) {
            // If this fails it's ok, we return 0 as fallback
        }
        return returnVal;

    }

    /**
     * The same workaround as below getMetadataStringForKey(com.day.cq.dam.api.Asset asset, String key)
     * Just with different signature
     * @param asset is the asset to interogate
     * @param key is the key to get the metadata for
     * @return the value or null when nothing is found
     */
    protected String getMetadataStringForKey(com.adobe.granite.asset.api.Asset asset, String key) {
        if (asset == null) {
            return null;
        }
        if (StringUtils.isBlank(key)) {
            return null;
        }

        Object metadataObj = null;
        Node node = asset.adaptTo(Node.class);
        try {

            if (node != null) {

                Resource metadataRes = asset.getResourceResolver().getResource(node.getPath() + "/jcr:content/metadata");

                if (metadataRes != null) {
                    ValueMap map = metadataRes.adaptTo(ValueMap.class);
                    if (map != null && map.containsKey(key)) {
                        metadataObj = map.get(key).toString();
                    }
                }
            }

        }catch (Exception ex){
            LOG.error("Exception : " + ex.getMessage(), ex);
        }


        if (metadataObj == null) {
            return null;
        }
        if (metadataObj.getClass().isArray()) {
            return ((Object[]) metadataObj)[0].toString();
        }
        return metadataObj.toString();
    }

    /**
     * Necessary to make sure some of the weird behavior CQ exhibits gets worked around. In
     * some cases (don't know when exactly), the asset dc:title and dc:description keys are
     * returned as Object[] with one element instead of a String. This method tests that
     * and returns the first element from the list or just the element itself
     *
     * @param asset is the asset to interogate
     * @param key is the key to get the metadata for
     * @return the value or null when nothing is found
     */
    protected String getMetadataStringForKey(com.day.cq.dam.api.Asset asset, String key) {
        if (asset == null) {
            return null;
        }
        if (StringUtils.isBlank(key)) {
            return null;
        }

        Object metadataObj = asset.getMetadata(key);
        if (metadataObj == null) {
            return null;
        }
        if (metadataObj.getClass().isArray()) {
            return ((Object[]) metadataObj)[0].toString();
        }
        return metadataObj.toString();
    }

    /**
     * Try to find an image thumbnail associated to this audio badge
     *
     * @param asset is the asset to look through
     * @return the thumbnail path
     */
    protected Resource getThumbnailPathName(Asset asset) throws RepositoryException {
        List<Rendition> renditions = asset.getRenditions();

        if (renditions == null || renditions.size() == 0) {
            return null;
        }

        // cycle through renditions to find the first thumbnail
        for (Rendition rendition : renditions) {
            Node rNode = rendition.adaptTo(Node.class);
            if (DamUtil.isThumbnail(rNode)) {
                return rendition.adaptTo(Resource.class);
            }
        }

        return null;
    }

    /**
     * Try to find an image thumbnail associated to this audio badge
     *
     * @param asset is the asset to look through
     * @param renditionName is the specific redition of interest
     * @return the thumbnail path
     */
    protected Resource getThumbnail(Asset asset, String renditionName) {
        if (asset == null) {
            return null;
        }

        try {

            List<Rendition> renditions = asset.getRenditions();

            if (renditions == null || renditions.size() == 0) {
                return null;
            }

            // cycle through renditions to find the specific rendition
            for (Rendition rendition : renditions) {
                if (rendition.getName().equals(renditionName))
                    return rendition.adaptTo(Resource.class);
            }

        } catch (Exception ex) {
            getLogger().error("Exception occurred: " + ex.getMessage(), ex);
        }
        return null;
    }


    /***
     * get rendition matching selected width
     * @param asset asset to use
     * @param minWidth width to find
     * @return
     */
    protected Rendition getThumbnail(Asset asset, int minWidth) {
        if (asset == null) {
            return null;
        }

        try {

            return DamUtil.getBestFitRendition(minWidth, asset.getRenditions());

        } catch (Exception ex) {
            getLogger().error("Exception occurred: " + ex.getMessage(), ex);
        }
        return null;
    }

    protected String getThumbnailUrl(Page page, ResourceResolver _resourceResolver) {
        return _resourceResolver.map(page.getPath().concat(DEFAULT_THUMB_SELECTOR_MD));
    }

    /**
     * Get a thumbnail for an image
     * if no rendition is matched, return img
     * @param img
     * @param renditionName
     * @param _resourceResolver
     * @return
     * @throws RepositoryException
     */
    protected String getThumbnail(String img, String renditionName, ResourceResolver _resourceResolver) throws RepositoryException {

        String imageURL = img;

        if (StringUtils.isNotEmpty(img)) {
            Resource rs = _resourceResolver.getResource(img);
            if (rs != null) {
                Asset asset = rs.adaptTo(Asset.class);
                Resource renditionRes = getThumbnail(asset, renditionName);

                if (renditionRes != null) {
                    imageURL = _resourceResolver.map(renditionRes.getPath());
                }

            }
        }

        return imageURL;
    }

    /**
     * Get width of the asset
     *
     * @return width of asset
     * @throws javax.jcr.RepositoryException
     */
    public int getWidth(Node assetNode) throws RepositoryException {
        int width = 0;
        final String PROPERTY_METADATA = "jcr:content/metadata";
        if (assetNode.hasNode(PROPERTY_METADATA)) {
            Node metadataNode = assetNode.getNode(PROPERTY_METADATA);
            try {
                width = Integer.valueOf(
                        DamUtil.getValue(metadataNode, "tiff:ImageWidth",
                                DamUtil.getValue(metadataNode, "exif:PixelXDimension", "")));
            } catch (Exception e) {
                // If this fails it's ok, we return 0 as fallback
            }
        }
        return width;
    }

    /**
     * Get height of the asset
     *
     * @return height of asset
     * @throws javax.jcr.RepositoryException
     */
    public int getHeight(Node assetNode) throws RepositoryException {
        int height = 0;
        final String PROPERTY_METADATA = "jcr:content/metadata";
        if (assetNode.hasNode(PROPERTY_METADATA)) {
            Node metadataNode = assetNode.getNode(PROPERTY_METADATA);
            try {
                height = Integer.valueOf(
                        DamUtil.getValue(metadataNode, "tiff:ImageLength",
                                DamUtil.getValue(metadataNode, "exif:PixelYDimension", "")));
            } catch (Exception e) {
                // If this fails it's ok, we return 0 as fallback
            }
        }
        return height;
    }

    /**
     * Get the processed version of an image; it will have cropping etc applied to it
     *
     * @param resource is the resource to read from
     * @param relativePath is the path under which the image was stored
     * @return the image instance
     */
    protected Image getProcessedImage(Resource resource, String relativePath) {
        Image image = null;
        if (relativePath != null) {
            image = new Image(resource, relativePath);
        }
        else {
            image = new Image(resource);
        }

        // make it a servlet call so it processes the cropping etc.
        image.setSelector(".img");

        return image;
    }


    /**
     * Get a scaled down, cropped out version of the image at `relativePath` with a
     * maximum width of `width`.
     *
     * @param resource is the resource the image is a part of
     * @param relativePath is the relative path
     * @param maxWidth is the maximum width
     * @return is the image object that we will render.
     */
    protected Image getScaledProcessedImage(Resource resource, String relativePath, int maxWidth, int maxHeight) {
        Image image = this.getProcessedImage(resource, relativePath);

        // set the selector to be the thumbnail selector
        image.setSelector(".scale.thumbnail." + Integer.toString(maxWidth) + "." + Integer.toString(maxHeight));

        return image;
    }



    /**
     * Get a scaled down, cropped out version of the image at `relativePath` with a
     * maximum width of `width`.
     *
     * @param resource is the resource the image is a part of
     * @param relativePath is the relative path
     * @param maxWidth is the maximum width
     * @return is the image object that we will render.
     *
     * @deprecated use Image Server to do this
     */
    @Deprecated
    protected Image getScaledProcessedImage(Resource resource, String relativePath, int maxWidth) {
        Image image = this.getProcessedImage(resource, relativePath);

        // set the selector to be the thumbnail selector
        image.setSelector(".scale.thumbnail." + Integer.toString(maxWidth));

        return image;
    }

    /***
     * get image url for a resource if defined
     * @param resource
     * @param imageResourceName
     * @return
     */
    protected String getResourceImageCustomHref(Resource resource, String imageResourceName) {
        String imageSrc="";
        if (resource == null || isEmpty(imageResourceName)) {
            return imageSrc;
        }
        Resource imageResource = resource.getChild(imageResourceName);
        if (imageResource != null) {
            Resource fileReference = imageResource.getChild(IMAGE_FILEREFERENCE);
            if (fileReference != null) {
                if (imageResource.getResourceType().equals(DEFAULT_IMAGE_RESOURCETYPE) || imageResource.getResourceType().endsWith(DEFAULT_IMAGE_RESOURCETYPE_SUFFIX)) {
                    Long lastModified = getLastModified(imageResource);
                    imageSrc = MessageFormat.format(DEFAULT_IMAGE_GENERATED_FORMAT, imageResource.getPath(), lastModified.toString());
                    imageSrc = mappedUrl(resource.getResourceResolver(), imageSrc);
                }
            }
        }
        return imageSrc;
    }

    /***
     * get asset reference for image node from a page
     * @param page to use as source
     * @return path to image or return default reference to page thumbnail selector
     * @throws RepositoryException
     */
    protected String getPageImgReferencePath(Page page) {
        String imagePath = getResourceImagePath(page.getContentResource(),DEFAULT_IMAGE_NODE_NAME);

        if (isEmpty(imagePath)) {
            imagePath = page.getPath().concat(DEFAULT_IMAGE_THUMB_SELECTOR);
        }

        return imagePath;
    }

    /***
     * get fileReference from image node of a resource
     * @param resource resource to use
     * @param imageResourceName image node name
     * @return
     */
    protected String getResourceImagePath(Resource resource, String imageResourceName) {
        String fileReferencPath = "";
        if (resource == null || isEmpty(imageResourceName)) {
            return fileReferencPath;
        }
        try {
            Resource fileReference = resource.getChild(imageResourceName);
            if (fileReference != null) {
                Node fileReferenceNode = fileReference.adaptTo(Node.class);
                if (fileReferenceNode != null) {
                    if (fileReferenceNode.hasProperty(IMAGE_FILEREFERENCE)) {
                        fileReferencPath = fileReferenceNode.getProperty(IMAGE_FILEREFERENCE).getString();
                    }
                }
            }
        } catch (Exception ex) {
            getLogger().error("Exception occurred: " + ex.getMessage(), ex);
        }
        return fileReferencPath;
    }

    protected String getPageContentImagePath(Page thisPage, String componentPath) {
        String imagePath = "";
        try {
            Node secondaryImage = null;
            if (thisPage != null && componentPath != null) {
                Resource componentResource = thisPage.getContentResource(componentPath);
                if (componentResource != null) {
                    secondaryImage = componentResource.adaptTo(Node.class);
                    if (secondaryImage.hasProperty(IMAGE_FILEREFERENCE)) {
                        imagePath = secondaryImage.getProperty(IMAGE_FILEREFERENCE).getString();
                    }
                }
            }
        } catch (Exception ex) {
            getLogger().error("Exception occurred: " + ex.getMessage(), ex);
        }
        return imagePath;
    }

    protected ComponentProperties getPageNamedImage(SlingScriptHelper _sling, Page page, String width) throws RepositoryException{
        ComponentProperties componentProperties = new ComponentProperties();;
        String imgReference = getPageImgReferencePath(page);


        if (StringUtils.isNotEmpty(imgReference)){

            componentProperties.put("imgUrl",imgReference);

            Resource imgResource = page.getContentResource("image");

            LOG.info("imgResource 1 : "+ imgResource +" size " +width);
            Image image1 = new Image(imgResource);

            if (image1.hasContent()) {
                LOG.info("imgResource 1 : hasContent");
            }
            imgResource = page.getContentResource().getResourceResolver().getResource(page.getPath() + "/jcr:content/image");

            Image image2 = new Image(imgResource);

            if (image2.hasContent()) {
                LOG.info("imgResource 2 : hasContent");
            }
            LOG.info("imgResource 2 : "+ imgResource +" size " +width);
            int originalHeight = 0;
            int originalWidth = 0;

            int profileHeight = 0;
            int profileWidth = 0;
            try {

                Image image = new Image(imgResource);

                if (image.hasContent()) {

                    Layer layer = image.getLayer(false, false, false);//imageHelper.scaleThisImage(image, 460, 0, null);
                    LOG.info("imgResource 3 : hasContent");
                    originalHeight = layer.getHeight();
                    originalWidth = layer.getWidth();
                    LOG.info("imgResource 3 : originalHeight " + originalHeight);
                    LOG.info("imgResource 3 : originalWidth " + originalWidth);


                    org.osgi.service.cm.ConfigurationAdmin configAdmin = _sling.getService(org.osgi.service.cm.ConfigurationAdmin.class);

                    //Configuration instances[] = cm.listConfigurations("(service.factoryPid=com.adobe.acs.commons.images.impl.NamedImageTransformerImpl)");
                    org.osgi.service.cm.Configuration[] instances = configAdmin.listConfigurations("(service.factoryPid=com.adobe.acs.commons.images.impl.NamedImageTransformerImpl)");

                    if (instances == null || instances.length == 0){
                        LOG.error("Failed to find OSGI Configuration [service.factoryPid=com.adobe.acs.commons.images.impl.NamedImageTransformerImpl]");
                    }

                    for (org.osgi.service.cm.Configuration c : instances) {


                        String profileName = c.getProperties().get("name").toString();


                        LOG.debug("profileName : " + profileName + " size " + width);
                        if (profileName.equals("masonry-" + width)) {
                            String[] formats = (String[]) c.getProperties().get("transforms");
                            if (formats.length > 0) {
                                String[] params = formats[0].split("&");
                                Map<String, String> map = new HashMap<String, String>();
                                for (String param : params) {
                                    String name = param.split("=")[0];
                                    String value = param.split("=")[1];
                                    map.put(name, value);
                                }
                                //out.println("width : "+ map );
                                String widthConfig = map.get("resize:width");
                                String upscaleString = map.get("upscale");
                                //out.println("width : "+ width );


                                profileWidth = Integer.valueOf(widthConfig);

                                LOG.debug("map : " + map + " width " + width);
                            }


                        }
                    }

                    if (profileWidth == 0){
                        LOG.error("Failed to find the NamedImageServlet Profile masonry-["+width+"]");
                    }
                }
            }catch(Exception e){
                LOG.error("Failed to get OSGI Configuration " + e.getMessage(), e);
            }


            if (profileWidth > 0){

                LOG.error("imgResource 3 : Configuration.getProperties ..............."+profileWidth);
                imgReference = imgReference +".transform/masonry-" + width + "/image.jpg";

                componentProperties.put("imgUrl",imgReference);

                float aspect = (float)originalHeight/(float)originalWidth;
                profileHeight = Math.round(profileWidth * aspect);

                componentProperties.put("profileHeight",profileHeight);
                componentProperties.put("profileWidth",profileWidth);
            }


        }else{

            componentProperties.put("emptyImagePlaceHolder"," placeholder lilac");
        }


        return componentProperties;
    }

    protected String getPageImgReferencePath(PageManager pageManager, String pagePath) throws RepositoryException{
        String imgPath = "";
        if(pagePath!= null && !pagePath.equals("")){
            Page page = pageManager.getPage(pagePath);
            imgPath = getPageImgReferencePath(page);
        }
        return imgPath;
    }

    /***
     * get asset metadata value and return default value if its empty
     * @param asset asset to use
     * @param name name of metadata field
     * @param defaultValue default value to return
     * @return
     */
    protected String getAssetPropertyValueWithDefault(Asset asset, String name, String defaultValue) {
        String valueReturn = defaultValue;
        if (asset == null) {
            return valueReturn;
        }

        try {

            String assetValue = asset.getMetadataValue(name);

            if (isNotEmpty(assetValue)) {
                return assetValue;
            }

        } catch (Exception ex) {
            getLogger().error("Exception occurred: " + ex.getMessage(), ex);
        }

        return valueReturn;
    }



    /***
     * get formatted copyright info text for an asset
     * @param asset asset to use
     * @param format format to use with fields merge
     * @return
     */
    protected String getAssetCopyrightInfo(Asset asset, String format) {
        String copyrightInfo = "";
        if (asset == null) {
            return copyrightInfo;
        }
        if (isEmpty(format)) {
            format = DAM_LICENSE_FORMAT;
        }

        try {
            String assetCreator = getAssetPropertyValueWithDefault(asset, DamConstants.DC_CREATOR, "");
            String assetContributor = getAssetPropertyValueWithDefault(asset, DamConstants.DC_CONTRIBUTOR, "");
            String assetLicense = getAssetPropertyValueWithDefault(asset, DamConstants.DC_RIGHTS, "");
            String assetCopyrightOwner = getAssetPropertyValueWithDefault(asset, DAM_FIELD_LICENSE_COPYRIGHT_OWNER, "");
            String assetExpiresYear = "";
            String assetExpires = asset.getMetadataValue(DAM_FIELD_LICENSE_EXPIRY);

            if (assetExpires != null) {
                XMPDateTime assetExpiresDate = XMPDateTimeFactory.createFromISO8601(assetExpires);
                assetExpiresYear = Integer.toString(assetExpiresDate.getCalendar().get(Calendar.YEAR));
            }

            String values = StringUtils.join(assetCreator, assetContributor, assetLicense, assetCopyrightOwner, assetExpiresYear).trim();
            if (isNotEmpty(values)) {
                copyrightInfo = MessageFormat.format(format, assetCreator, assetContributor, assetLicense, assetCopyrightOwner, assetExpiresYear);
            }
        } catch (Exception ex) {
            getLogger().error("Exception occurred: " + ex.getMessage(), ex);
        }

        return copyrightInfo;

    }

    /***
     * allow asset rendition compare by width
     */
    protected class WidthBasedRenditionComparator implements Comparator<com.adobe.granite.asset.api.Rendition>
    {
        public int compare(com.adobe.granite.asset.api.Rendition r1, com.adobe.granite.asset.api.Rendition r2)
        {
            int w1 = getWidth(r1);
            int w2 = getWidth(r2);
            if (w1 < w2) {
                return -1;
            }
            if (w1 == w2) {
                return 0;
            }
            return 1;
        }
    }

    protected  int getWidth(com.adobe.granite.asset.api.Rendition r)
    {
        return getDimension(r, "tiff:ImageWidth");
    }

    protected  int getHeight(com.adobe.granite.asset.api.Rendition r)
    {
        return getDimension(r, "tiff:ImageLength");
    }

    protected int getDimension(com.adobe.granite.asset.api.Rendition r, String dimensionProperty)
    {
        if (r == null)
        {
            LOG.debug("Null rendition at", new Exception("Null rendition"));
            return 0;
        }
        if ((dimensionProperty == null) || ((!dimensionProperty.equals("tiff:ImageLength")) && (!dimensionProperty.equals("tiff:ImageWidth"))))
        {
            LOG.warn("Incorrect dimension property for {}", r.getPath(), new Exception("Invalid property name " + dimensionProperty));
            return 0;
        }
        String name = r.getName();
        if (name == null)
        {
            LOG.warn("Null name returned at {}", r.getPath());
            return 0;
        }
        try
        {
            if (name.equals("original"))
            {
                com.adobe.granite.asset.api.Asset asset = (com.adobe.granite.asset.api.Asset)r.adaptTo(com.adobe.granite.asset.api.Asset.class);
                if (asset == null)
                {
                    LOG.debug("Rendition at {} is not adaptable to an asset.", r.getPath());
                    return 0;
                }

                String val = null;
                Node assetNode = (Node)asset.adaptTo(Node.class);
                if (!assetNode.hasNode("jcr:content/metadata")) {
                    Node assetMetadata = assetNode.getNode("jcr:content/metadata");
                    if (assetMetadata.hasProperty(dimensionProperty)) {
                        val = assetMetadata.getProperty(dimensionProperty).getString();
                    }
                }

                if ((val == null) || (val.length() == 0))
                {
                    LOG.debug("Unable to find metadata property {} for {}", dimensionProperty, asset.getPath());
                    return 0;
                }
                try
                {
                    return Integer.parseInt(val);
                }
                catch (NumberFormatException nfe)
                {
                    LOG.warn("Metadata property {} was {} and not a number at {}", new Object[] { dimensionProperty, val, asset.getPath() });
                    return 0;
                }
            }
            Matcher matcher = DEFAULT_RENDTION_PATTERN_OOTB.matcher(name);
            if (matcher.matches())
            {
                int matcherIndex;
                if ("tiff:ImageLength".equals(dimensionProperty)) {
                    matcherIndex = 3;
                } else {
                    matcherIndex = 2;
                }
                return Integer.parseInt(matcher.group(matcherIndex));
            }
            LOG.debug("Unknown naming format for name {} at {}", name, r.getPath());
            return 0;
        }
        catch (Exception e)
        {
            LOG.warn("Unexpected exception finding dimension for asset at {} " + r.getPath(), e);
        }
        return 0;
    }

    /***
     * get a best fitting rendition for an asset resolved from asset path
     * @param assetPath
     * @param renditionWidth
     * @param resourceResolver
     * @return
     */
    public String getBestFitRendition(String assetPath, int renditionWidth, ResourceResolver resourceResolver) {
        String renditionPath = assetPath;

        if (isEmpty(assetPath)) {
          return "";
        }

        try {

            com.adobe.granite.asset.api.AssetManager assetManager = resourceResolver.adaptTo(com.adobe.granite.asset.api.AssetManager.class);
            com.adobe.granite.asset.api.Asset asset = assetManager.getAsset(assetPath);

            if (asset != null) {

                com.adobe.granite.asset.api.Rendition bestRendition = getBestFitRendition(renditionWidth, asset);
                if (bestRendition != null) {
                    renditionPath = bestRendition.getPath();
                }

            }

        } catch (Exception ex) {
            LOG.error("getBestFitRendition error {} ", ex);
        }
        return renditionPath;
    }

    /***
     * allow picking of best rendition by width based on default prefixes
     * @param width min width
     * @param renditions
     * @return
     */
    protected com.adobe.granite.asset.api.Rendition getBestFitRendition(int width, List<com.adobe.granite.asset.api.Rendition> renditions) {
        return getBestFitRendition(width,renditions,null);
    }

    /***
     * allow picking of best rendition by width based on default prefixes
     * @param width min width
     * @param asset asset with renditions
     * @return
     */
    protected com.adobe.granite.asset.api.Rendition getBestFitRendition(int width, com.adobe.granite.asset.api.Asset asset) {
        List<com.adobe.granite.asset.api.Rendition> renditions = Lists.newArrayList(asset.listRenditions());
        return getBestFitRendition(width,renditions,null);
    }

    /***
     * allow picking of best rendition by width with optional prefix
     * @param width min width
     * @param asset asset with renditions
     * @param renditionPrefix rendition prefix
     * @return
     */
    protected com.adobe.granite.asset.api.Rendition getBestFitRendition(int width, com.adobe.granite.asset.api.Asset asset, String renditionPrefix)
    {
        List<com.adobe.granite.asset.api.Rendition> renditions = Lists.newArrayList(asset.listRenditions());
        return getBestFitRendition(width,renditions,renditionPrefix);

    }
    /***
     * allow picking of best rendition by width with optional prefix
     * @param width min width
     * @param renditions list of renditions
     * @param renditionPrefix rendition prefix
     * @return
     */
    protected com.adobe.granite.asset.api.Rendition getBestFitRendition(int width, List<com.adobe.granite.asset.api.Rendition> renditions, String renditionPrefix)
    {
        com.adobe.granite.asset.api.Rendition bestFitRendition = null;
        //try custom prefix directly
        if (renditionPrefix != null) {
            bestFitRendition = getRenditionByPrefix(renditions.iterator(), renditionPrefix + width);
        }
        //try default prefixes directly
        if (bestFitRendition == null) {

            bestFitRendition = getRenditionByPrefix(renditions.iterator(), DEFAULT_ASSET_RENDITION_PREFIX1 + width);
            if (bestFitRendition == null) {
                bestFitRendition = getRenditionByPrefix(renditions.iterator(), DEFAULT_ASSET_RENDITION_PREFIX2 + width);
            }
        }
        if (bestFitRendition != null) {
            return bestFitRendition;
        }
        //if not found directly, find first rendition bigger that what we need
        WidthBasedRenditionComparator comp = new WidthBasedRenditionComparator();
        Collections.sort(renditions, comp);
//        Collections.reverse(renditions);
        Iterator<com.adobe.granite.asset.api.Rendition> itr = renditions.iterator();
        com.adobe.granite.asset.api.Rendition bestFit = null;
        com.adobe.granite.asset.api.Rendition original = null;
        while (itr.hasNext())
        {
            com.adobe.granite.asset.api.Rendition rend = itr.next();
            if (canRenderOnWeb(rend.getMimeType()))
            {
                int w = getWidth(rend);
//                LOG.error("Comparing widths {} >= {}: {}", w, width, rend.getName());
                if (w >= width) {
                    bestFit = rend;
                    if (renditionPrefix != null) {
                        //if matches width and type continue
                        if (rend.getName().startsWith(renditionPrefix)) {
//                            LOG.error("Bingo! {}", rend.getName());
//                            return bestFit;
                            break;
                        }
                    } else {
                        if (rend.getName().startsWith(DEFAULT_ASSET_RENDITION_PREFIX1) || rend.getName().startsWith(DEFAULT_ASSET_RENDITION_PREFIX2)) {
//                            LOG.error("Width Match Default Rendition! {}", rend.getName());
//                            return bestFit;
                            break;
                        }
                    }
                }
            }
        }

        //find first rendition that can be rendered
        if (bestFit == null) {
            itr = renditions.iterator();
            while (itr.hasNext())
            {
                com.adobe.granite.asset.api.Rendition rend = itr.next();
                if (canRenderOnWeb(rend.getMimeType()))
                {
                    bestFit = rend;
                    break;
                }
            }
        }

        return bestFit;
    }

    public static boolean canRenderOnWeb(String mimeType)
    {
        return (mimeType != null) && ((mimeType.toLowerCase().contains("jpeg")) || (mimeType.toLowerCase().contains("jpg")) || (mimeType.toLowerCase().contains("gif")) || (mimeType.toLowerCase().contains("png")));
    }

    public com.adobe.granite.asset.api.Rendition getRenditionByPrefix(com.adobe.granite.asset.api.Asset asset, String prefix, boolean returnOriginal)
    {
        Iterator renditions = asset.listRenditions();
        return getRenditionByPrefix(renditions,prefix,returnOriginal);
    }
    public com.adobe.granite.asset.api.Rendition getRenditionByPrefix(com.adobe.granite.asset.api.Asset asset, String prefix)
    {
        Iterator renditions = asset.listRenditions();
        return getRenditionByPrefix(renditions,prefix,false);
    }

    public com.adobe.granite.asset.api.Rendition getRenditionByPrefix(Iterator<com.adobe.granite.asset.api.Rendition> renditions, String prefix)
    {
        return getRenditionByPrefix(renditions,prefix,false);
    }

    public com.adobe.granite.asset.api.Rendition getRenditionByPrefix(Iterator<com.adobe.granite.asset.api.Rendition> renditions, String prefix, boolean returnOriginal)
    {
        com.adobe.granite.asset.api.Rendition original = null;
        while (renditions.hasNext())
        {
            com.adobe.granite.asset.api.Rendition rendition = renditions.next();
            if ("original".equals(rendition.getName())) {
                original = rendition;
            }
            if (rendition.getName().startsWith(prefix)) {
                return rendition;
            }
        }
        if (returnOriginal) {
            return original;
        }
        return null;
    }

    /***
     * get basic asset info and return default if asset not found
     * @param resourceResolver
     * @param assetPath
     * @param infoPrefix
     * @return
     */
    public Map<String,String> getAssetInfo(ResourceResolver resourceResolver, String assetPath, String infoPrefix, String defaultPath){

        Map<String, String> assetInfo = getAssetInfo(resourceResolver,assetPath,infoPrefix);

        if (assetInfo.size() == 0) {
            assetInfo.put(infoPrefix, defaultPath);
        }

        return assetInfo;
    }

    /***
     * get basic asset info
     * @param resourceResolver
     * @param assetPath
     * @param infoPrefix
     * @return
     */
    public Map<String,String> getAssetInfo(ResourceResolver resourceResolver, String assetPath, String infoPrefix){
        if (isEmpty(infoPrefix)) {
            infoPrefix = "image";
        }
        Map<String, String> assetInfo = new HashMap<String, String>();

        if (isNotEmpty(assetPath)) {

            assetInfo.put(infoPrefix, assetPath);

            Resource pageImageResource = resourceResolver.resolve(assetPath);
            if (!ResourceUtil.isNonExistingResource(pageImageResource)) {
                Asset pageImageAsset = pageImageResource.adaptTo(Asset.class);
                if (pageImageAsset != null) {
                    assetInfo.put(infoPrefix + "LicenseInfo", getAssetCopyrightInfo(pageImageAsset, DAM_LICENSE_FORMAT));

                    assetInfo.put(infoPrefix + "Title", getAssetPropertyValueWithDefault(pageImageAsset, DamConstants.DC_TITLE, "") );
                    assetInfo.put(infoPrefix + "Description", getAssetPropertyValueWithDefault(pageImageAsset, DamConstants.DC_DESCRIPTION, ""));

                    try {
                        assetInfo.put(infoPrefix + "Id", ((Node)pageImageResource.adaptTo(Node.class)).getProperty("jcr:uuid").getString() );
                    } catch (Exception ex) {
                        LOG.error("getAssetInfo: could not get assetID {}", ex.toString());
                    }

                }
            }
        }
        return assetInfo;
    }

    /***
     * get asset video renditions if any
     * @param asset to use
     * @return
     */
    public TreeMap<String, String> getAssetRenditionsVideo(com.adobe.granite.asset.api.Asset asset) {
        TreeMap<String, String> renditionsSet = new TreeMap<String, String>();

        if (asset != null) {
            Iterator renditions = asset.listRenditions();

            while (renditions.hasNext()) {
                com.adobe.granite.asset.api.Rendition rendition = (com.adobe.granite.asset.api.Rendition)renditions.next();
                if (rendition != null && (rendition.getName().contains(".video.") || rendition.getName().equals("original"))) {
                    renditionsSet.put(rendition.getPath(), rendition.getMimeType());
                }
            }

        }

        return renditionsSet;
    }

    /***
     * get background video settings from shared background video tab
     * @param pageContext
     * @return
     */
    public ComponentProperties getBackgroundVideoRenditions(PageContext pageContext) {
        Resource resource = (org.apache.sling.api.resource.Resource) pageContext.getAttribute("resource");
        ResourceResolver resourceResolver = (org.apache.sling.api.resource.ResourceResolver) pageContext.getAttribute("resourceResolver");

        Resource backgroundResource = resource.getChild(DEFAULT_BACKGROUND_VIDEO_NODE_NAME);

        if (backgroundResource != null) {

            ComponentProperties imageProperties = getComponentProperties(
                    pageContext,
                    backgroundResource,
                    false,
                    DEFAULT_FIELDS_BACKGROUNDVIDEO_OPTIONS);

            String fileReference = imageProperties.get(IMAGE_FILEREFERENCE,"");

            Resource assetR = resourceResolver.resolve(fileReference);
            if (!ResourceUtil.isNonExistingResource(assetR)) {

                com.adobe.granite.asset.api.AssetManager assetManager = resourceResolver.adaptTo(com.adobe.granite.asset.api.AssetManager.class);
                com.adobe.granite.asset.api.Asset videoAsset = assetManager.getAsset(fileReference);

                if (videoAsset != null) {

                    imageProperties.putAll(
                            getAssetInfo(
                                    resourceResolver,
                                    fileReference,
                                    FIELD_VIDEO_BACKGROUND
                            )
                    );

                    imageProperties.put(FIELD_RENDITIONS_VIDEO, getAssetRenditionsVideo(videoAsset));
                }
            }

            return imageProperties;
        }
        return null;
    }

    /***
     * get background image settings from shared background image tab
     * @param pageContext
     * @return
     */
    public ComponentProperties getBackgroundImageRenditions(PageContext pageContext) {
        Resource resource = (org.apache.sling.api.resource.Resource) pageContext.getAttribute("resource");
        ResourceResolver resourceResolver = (org.apache.sling.api.resource.ResourceResolver) pageContext.getAttribute("resourceResolver");
        org.apache.sling.api.scripting.SlingScriptHelper sling = (org.apache.sling.api.scripting.SlingScriptHelper) pageContext.getAttribute("sling");


        Resource backgroundResource = resource.getChild(DEFAULT_BACKGROUND_IMAGE_NODE_NAME);

        if (backgroundResource != null) {

            ComponentProperties imageProperties = getComponentProperties(
                    pageContext,
                    backgroundResource,
                    false,
                    DEFAULT_FIELDS_BACKGROUNDIMAGE_OPTIONS);

            String fileReference = imageProperties.get(IMAGE_FILEREFERENCE,"");

            Resource assetR = resourceResolver.resolve(fileReference);
            if (!ResourceUtil.isNonExistingResource(assetR)) {

                com.adobe.granite.asset.api.AssetManager assetManager = resourceResolver.adaptTo(com.adobe.granite.asset.api.AssetManager.class);
                com.adobe.granite.asset.api.Asset asset = assetManager.getAsset(fileReference);

                if (asset != null) {

                    try {

                        String imageOption = imageProperties.get(FIELD_IMAGE_OPTION, "");
                        //imageProperties.put(COMPONENT_BACKGROUND_ASSETS + "Options", imageOption);

                        Map<String, String> responsiveImageSet = new LinkedHashMap<String, String>();

                        switch (imageOption) {
                            case IMAGE_OPTION_GENERATED:
                                String imageHref = "";
                                Long lastModified = getLastModified(resource);
                                imageHref = MessageFormat.format(DEFAULT_IMAGE_GENERATED_FORMAT, resource.getPath(), lastModified.toString());

                                //imageProperties.put(COMPONENT_BACKGROUND_ASSETS, imageHref);
                                responsiveImageSet.put("",imageHref);
                                break;
                            case IMAGE_OPTION_RENDITION:
                                int targetWidth = imageProperties.get(ImageResource.PN_WIDTH, 0);
                                com.adobe.granite.asset.api.Rendition bestRendition = getBestFitRendition(targetWidth, asset);
                                if (bestRendition != null) {
                                    //imageProperties.put(COMPONENT_BACKGROUND_ASSETS, bestRendition.getPath());
                                    responsiveImageSet.put("",bestRendition.getPath());
                                }
                                break;
                            case IMAGE_OPTION_ADAPTIVE:
                                String[] adaptiveImageMapping = imageProperties.get(FIELD_ADAPTIVE_MAP, new String[]{});

                                responsiveImageSet = getAdaptiveImageSet(adaptiveImageMapping, resourceResolver, resource.getPath(), fileReference, null, false, sling);

                                break;
                            case IMAGE_OPTION_MEDIAQUERYRENDITION:
                                //map of media queries to renditions
                                String[] mediaQueryList = imageProperties.get(FIELD_MEDIAQUERYRENDITION_KEY, new String[]{});
                                String[] renditionList = imageProperties.get(FIELD_MEDIAQUERYRENDITION_VALUE, new String[]{});

                                if (mediaQueryList.length != renditionList.length ) {
                                    LOG.error(MessageFormat.format("fields {0} and {1} need to be equal length",FIELD_MEDIAQUERYRENDITION_KEY,FIELD_MEDIAQUERYRENDITION_VALUE));
                                    break;
                                }

                                for (int i = 0; i < mediaQueryList.length; i++) {
                                    responsiveImageSet.put(mediaQueryList[i],fileReference+FIELD_ASSET_RENDITION_PATH_SUFFIX+renditionList[i]);
                                }

                                break;
                            default: //IMAGE_OPTION_RESPONSIVE
                                String[] renditionImageMapping = imageProperties.get(FIELD_RESPONSIVE_MAP, new String[]{});

                                // Check if the image suffix is '.svg' or '.gif', if it is skip any rendition checks and simply return
                                // the path to it as no scaling or modifications should be applied.
                                if (fileReference.endsWith(".svg") || fileReference.endsWith(".gif")) {
                                    imageProperties.put(FIELD_IMAGE_BACKGROUND, fileReference);
                                    imageProperties.put(FIELD_IMAGE_OPTION, "simple");
                                } else {
                                    //get rendition profile prefix selected
                                    String renditionPrefix = imageProperties.get(FIELD_RENDITION_PREFIX,"");

                                    //get best fit renditions set
                                    responsiveImageSet = getBestFitMediaQueryRenditionSet(asset, renditionImageMapping, renditionPrefix);

                                }
                        }

                        imageProperties.put(COMPONENT_BACKGROUND_ASSETS, responsiveImageSet);

                        //pick last one from collection
                        if (responsiveImageSet.values().size() > 0) {
                            imageProperties.put(FIELD_IMAGE_BACKGROUND, responsiveImageSet.values()
                                    .toArray()[responsiveImageSet.values().size() - 1]);
                        }

                    } catch (Exception ex) {
                        LOG.error("failed to create Width and Image Mapping : " + ex.getMessage(), ex);
                    }

                }
            }

            return imageProperties;
        }
        return null;
    }


    /**
     * Get the allowedDimension for the Image
     * @param targetDimension
     * @param _currentStyle
     * @return
     */
    public Integer getDimension(Integer targetDimension, Style _currentStyle){
        Integer dimension = null;
        if (targetDimension != null && targetDimension.intValue() > 0) {
            int max = _currentStyle.get(Image.PN_MAX_WIDTH,Integer.class);
            int min = _currentStyle.get(Image.PN_MIN_WIDTH,Integer.class);
            if (min <= targetDimension && targetDimension <= max){
                dimension = targetDimension;

            }else if (min > targetDimension){
//                image.set(Image.PN_HTML_WIDTH, String.valueOf(min));

                dimension = min;
            }else if (max < targetDimension){
//                image.set(Image.PN_HTML_WIDTH, String.valueOf(max));
                dimension = max;
            }
        }
        return dimension;
    }

    /***
     * function to filter out the design dialog values which are not matching rendition profile using default rendition prefix names
     * @param asset asset to use
     * @param widthRenditionProfileMapping profile widths map
     * @return
     */
    public Map<Integer, String> getBestFitRenditionSet(com.adobe.granite.asset.api.Asset asset,  Map<Integer, String> widthRenditionProfileMapping) {
        return getBestFitRenditionSet(asset,widthRenditionProfileMapping,null);
    }

    /**
     * function to filter out the design dialog values which are not matching rendition profile
     * @param asset asset to use
     * @param widthRenditionProfileMapping profile widths map
     * @param renditionPrefix prefix to use
     * @return Map<Integer, String> return profile with substituted paths
     */
    public Map<Integer, String> getBestFitRenditionSet(com.adobe.granite.asset.api.Asset asset,  Map<Integer, String> widthRenditionProfileMapping, String renditionPrefix){

        Map<Integer, String> profileRendtiions = new TreeMap<Integer, String>();

        if (isEmpty(renditionPrefix))

            if (asset != null && widthRenditionProfileMapping != null) {

                for (Integer minWidth : widthRenditionProfileMapping.keySet()) {
                    String profileWidth = widthRenditionProfileMapping.get(minWidth);
                    com.adobe.granite.asset.api.Rendition rendition = getBestFitRendition( tryParseInt(profileWidth,0), asset, defaultIfEmpty(renditionPrefix,null));

//                LOG.warn("Best Rendition: [" + tryParseInt(profileWidth,0) + "] found rendition : [" + rendition.getPath() + "] profile name : " + profileWidth );

                    String renditionPath = rendition.getPath();

                    //don't return paths to original rendition return path to asset instead
                    if (renditionPath.endsWith("/original")) {
                        String assetPath = renditionPath.substring(0,renditionPath.indexOf(JcrConstants.JCR_CONTENT)-1);
                        ResourceResolver resourceResolver = asset.getResourceResolver();
                        if (resourceResolver != null) {
                            Resource assetPathResource = resourceResolver.resolve(assetPath);
                            if (!ResourceUtil.isNonExistingResource(assetPathResource)) {
                                renditionPath = assetPath;
                            }
                        }
                    }

                    profileRendtiions.put(minWidth,renditionPath);
                }

            }
        return profileRendtiions;

    }
    /**
     * function to filter out the design dialog values which are not matching rendition profile
     * @param asset asset to use
     * @param renditionImageMapping array of minWidth=mediaQuery
     * @param renditionPrefix prefix to use
     * @return Map<Integer, String> return profile with substituted paths
     */
    public Map<String, String> getBestFitMediaQueryRenditionSet(com.adobe.granite.asset.api.Asset asset,  String[] renditionImageMapping, String renditionPrefix){

        Map<String, String> profileRendtiions = new LinkedHashMap<String, String>();

        if (isEmpty(renditionPrefix))

            if (asset != null && renditionImageMapping != null) {

                for (String entry : renditionImageMapping){
                    String [] entryArray = StringUtils.split(entry, "=");
                    if (entryArray == null || entryArray.length != 2){
                        LOG.error("getBestFitMediaQueryRenditionSet ["+entry+"] is invalid");
                        continue;
                    }
                    String minWidth = entryArray[0];
                    if (StringUtils.isEmpty(minWidth)  || (NumberUtils.isDigits(minWidth) == false)){
                        LOG.error("getBestFitMediaQueryRenditionSet ["+entry+"] is invalid, incorrect width ["+minWidth+"]");
                        continue;
                    }

                    String mediaQuery = entryArray[1];

                    com.adobe.granite.asset.api.Rendition rendition = getBestFitRendition( tryParseInt(minWidth,0), asset, defaultIfEmpty(renditionPrefix,null));

                    String renditionPath = rendition.getPath();

                    //don't return paths to original rendition return path to asset instead
                    if (renditionPath.endsWith("/original")) {
                        String assetPath = renditionPath.substring(0,renditionPath.indexOf(JcrConstants.JCR_CONTENT)-1);
                        ResourceResolver resourceResolver = asset.getResourceResolver();
                        if (resourceResolver != null) {
                            Resource assetPathResource = resourceResolver.resolve(assetPath);
                            if (!ResourceUtil.isNonExistingResource(assetPathResource)) {
                                renditionPath = assetPath;
                            }
                        }
                    }

                    profileRendtiions.put(mediaQuery,renditionPath);
                }

            }
        return profileRendtiions;

    }

    /**
     * Get the targetWith which is within the range from the Site
     * @param style
     * @param targetWidth
     * @return
     */
    public Map<Integer, String> getWidthProfileMap(Style style, int targetWidth){

        Map<Integer, String> widthRenditionProfileMap = new LinkedHashMap<Integer, String>();

        Integer maxWidth = style.get(ImageResource.PN_MAX_WIDTH, Integer.class);
        Integer minWidth = style.get(ImageResource.PN_MIN_WIDTH, Integer.class);

        targetWidth = Math.min(targetWidth, maxWidth);
        targetWidth = Math.max(targetWidth, minWidth);
        widthRenditionProfileMap.put(targetWidth, String.valueOf(targetWidth));

        return widthRenditionProfileMap;
    }


    /**
     * Validate the List of the widthImageMapping from Design dialog and convert it into Map<Integer, String>
     * @param widthImageMapping
     * @return Map<Integer, String>
     * @throws IllegalAccessException
     */
    public Map<Integer, String> getWidthProfileMap(String [] widthImageMapping) throws IllegalAccessException{

        Map<Integer, String> widthRenditionProfileMap = new LinkedHashMap<Integer, String>();


        if (widthImageMapping != null && widthImageMapping.length > 0){

            for (String entry : widthImageMapping){
                String [] entryArray = StringUtils.split(entry, "=");
                if (entryArray == null || entryArray.length != 2){
                    LOG.error("design widthImageMapping ["+entry+"] is invalid");
                    new IllegalAccessException("design widthImageMapping ["+entry+"] is invalid");
                }
                String imageWidth = entryArray[0];
                if (StringUtils.isEmpty(imageWidth)){
                    LOG.error("profile ["+imageWidth+"] is invalid");
                    new IllegalAccessException("imageWidth ["+imageWidth+"] is invalid");
                }
                String minWidth = entryArray[1];
                if (StringUtils.isEmpty(minWidth)  || (NumberUtils.isDigits(minWidth) == false)){
                    LOG.error("minWidth ["+minWidth+"] is invalid");
                    new IllegalAccessException("minWidth ["+minWidth+"] is invalid");
                }

                widthRenditionProfileMap.put(Integer.valueOf(minWidth), imageWidth);

            }
        }

        return widthRenditionProfileMap;
    }


    /**
     * function to filter out the design dialog values which are not matching adaptive profile
     * @param adaptiveImageMapping
     * @param resolver
     * @param componentPath path to component doing the render
     * @param fileReference path to asset to use for render
     * @param outputFormat specify which output format to use
     * @param useFileReferencePathAsRender create paths using fileReference instead of using Component Path
     * @param sling
     * @return Map<Integer, String>
     */
    public Map<String, String> getAdaptiveImageSet (String[] adaptiveImageMapping, ResourceResolver resolver, String componentPath, String fileReference, String outputFormat, Boolean useFileReferencePathAsRender, org.apache.sling.api.scripting.SlingScriptHelper sling){

        Map<String, String> responsiveImageSet = new LinkedHashMap<String, String>();

        URI fileReferenceURI  = URI.create(fileReference);
        if (isBlank(outputFormat)) {
            String extension = fileReferenceURI.getPath();
            outputFormat = extension.substring(extension.lastIndexOf("."));
        }
        String suffix = defaultString(fileReferenceURI.getQuery(), "");

        String renderPath = componentPath;

        if (useFileReferencePathAsRender) {
            renderPath = fileReference;
        }

        int[] allowedSizes = getAdaptiveImageSupportedWidths(sling);

        for (String entry : adaptiveImageMapping){

            String [] entryArray = StringUtils.split(entry, "="); //320.medium=(min-width: 1px) and (max-width: 425px)
            if (entryArray == null || entryArray.length != 2){
                LOG.error("getAdaptiveImageSet ["+entry+"] is invalid");
                continue;
            }
            String adaptiveProfile = entryArray[0];
            if (StringUtils.isEmpty(adaptiveProfile) && (!adaptiveProfile.contains(".")) ){
                LOG.error("getAdaptiveImageSet ["+entry+"] is invalid, incorrect profile format ["+adaptiveProfile+"] expecting {width}.{quality}.{format}");
                continue;
            }

            String mediaQuery = entryArray[1];

            String [] adaptiveProfileArray = StringUtils.split(adaptiveProfile, ".");

            Integer profileWidth = tryParseInt(adaptiveProfileArray[0],0);

            String profileOutputFormat = outputFormat;
            if (adaptiveProfileArray.length == 3) {
                profileOutputFormat = "";
            }

            if (adaptiveProfile.equals("full") || ArrayUtils.contains(allowedSizes,profileWidth) ) {
                responsiveImageSet.put(mediaQuery,
                        MessageFormat.format("{0}.img.{1}{2}{3}",
                                renderPath,
                                adaptiveProfile,
                                profileOutputFormat,
                                suffix
                        )
                );
            } else {
                LOG.error("getAdaptiveImageSet rendition selected size is not allowed ["+profileWidth+"], ["+entry+"]");
                continue;
            }

        }

        return responsiveImageSet;
    }

    /**
     * return list of configured widths in com.day.cq.wcm.foundation.impl.AdaptiveImageComponentServlet
     * @param sling
     * @return int []
     */
    public int []  getAdaptiveImageSupportedWidths(org.apache.sling.api.scripting.SlingScriptHelper sling) {

        int [] defaultWidths  = {480,640,720,800,960,1024,1280};
        int [] supportedWidths = new int[0];

        try{
            org.osgi.service.cm.ConfigurationAdmin configAdmin = sling.getService(org.osgi.service.cm.ConfigurationAdmin.class);

            org.osgi.service.cm.Configuration config = configAdmin.getConfiguration(OSGI_CONFIG_MEDIA_IMAGE);

            Object obj = org.apache.sling.commons.osgi.PropertiesUtil.toStringArray(config.getProperties().get("adapt.supported.widths"));

            if (obj instanceof String []){

                String[] strings = (String [])obj;
                supportedWidths = new int[strings.length];
                for (int i=0; i < strings.length; i++) {
                    supportedWidths[i] = Integer.parseInt(strings[i]);
                }
            }

            if (obj instanceof long []){

                long [] longs = (long [])obj;
                supportedWidths = new int[longs.length];
                for (int i=0; i < longs.length; i++) {
                    supportedWidths[i] = (int)longs[i];
                }
            }

        } catch(Exception ex){
            LOG.warn("using default adapt.supported.widths=[{}] as config is missing OSGI configuration: {}", defaultWidths, OSGI_CONFIG_MEDIA_IMAGE);
            return defaultWidths;
        }
        return supportedWidths;

    }



/* TESTS
    out.println("responsiveRenditionOverride : "+widthRenditionProfileMapping);
    out.println("responsiveRenditionOverride1 : "+responsiveImageSet);
    out.println("filterRenditionImageSet(asset, widthRenditionProfileMapping, RENDITION_PROFILE_CUSTOM) : \n"+filterRenditionImageSet(asset, widthRenditionProfileMapping, RENDITION_PROFILE_CUSTOM));
    out.println("filterRenditionImageSet(asset, widthRenditionProfileMapping, \"cq5dam.custom.\") : \n"+filterRenditionImageSet(asset, widthRenditionProfileMapping, "cq5dam.custom."));
    out.println("getBestFitRendition(48, asset) : \n" + getBestFitRendition(48, asset) );
    out.println("getBestFitRendition(48, asset, RENDITION_PROFILE_CUSTOM) : \n" + getBestFitRendition(48, asset, RENDITION_PROFILE_CUSTOM) );
    out.println("getBestFitRendition(48, asset, \"cq5dam.custom.\") : \n" + getBestFitRendition(48, asset, "cq5dam.custom.") );
    out.println("getBestFitRendition(319, asset, \"cq5dam.custom.\") : \n" + getBestFitRendition(319, asset, "cq5dam.custom.") );
    out.println("getBestFitRendition(1900, asset, \"cq5dam.custom.\") : \n" + getBestFitRendition(1900, asset, "cq5dam.custom.") );
    out.println("getBestFitRendition(1280, asset, RENDITION_PROFILE_CUSTOM) : \n" + getBestFitRendition(1280, asset, RENDITION_PROFILE_CUSTOM) );
    out.println("getBestFitRendition(47, asset, RENDITION_PROFILE_CUSTOM) : \n" + getBestFitRendition(47, asset, RENDITION_PROFILE_CUSTOM) );
    out.println("getBestFitRendition(49, asset, \"cq5dam.custom.\") : \n" + getBestFitRendition(49, asset, "cq5dam.custom.") );
    out.println("getBestFitRendition(47, asset) : \n" + getBestFitRendition(47, asset) );
    out.println("getBestFitRendition(48, asset) : \n" + getBestFitRendition(48, asset) );
    out.println("getBestFitRendition(49, asset) : \n" + getBestFitRendition(49, asset) );
    out.println("getBestFitRendition(139, asset) : \n" + getBestFitRendition(139, asset) );
    out.println("getBestFitRendition(140, asset) : \n" + getBestFitRendition(140, asset) );
    out.println("getBestFitRendition(141, asset) : \n" + getBestFitRendition(141, asset) );
    out.println("getBestFitRendition(318, asset) : \n" + getBestFitRendition(318, asset) );
    out.println("getBestFitRendition(319, asset) : \n" + getBestFitRendition(319, asset) );
    out.println("getBestFitRendition(320, asset) : \n" + getBestFitRendition(320, asset) );
    out.println("getBestFitRendition(1279, asset) : \n" + getBestFitRendition(1279, asset) );
    out.println("getBestFitRendition(1280, asset) : \n" + getBestFitRendition(1280, asset) );
    out.println("getBestFitRendition(1281, asset) : \n" + getBestFitRendition(1281, asset) );
*/
%>
