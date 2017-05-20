<%@ page import="com.day.cq.commons.Doctype" %>
<%@ page import="com.day.cq.wcm.foundation.Image" %>
<%@ page import="org.apache.sling.api.resource.Resource" %>
<%@ page import="com.day.cq.dam.api.Asset" %>
<%@ page import="com.day.cq.dam.api.Rendition" %>
<%@ page import="com.day.cq.dam.commons.util.DamUtil" %>
<%@ page import="javax.jcr.RepositoryException" %>
<%@ page import="java.util.List" %>
<%@ page import="javax.jcr.Node" %>
<%@ page import="com.day.cq.wcm.api.Page" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="com.day.cq.dam.api.DamConstants" %>
<%@ page import="org.apache.commons.lang3.math.NumberUtils" %>
<%@ page import="java.util.regex.Matcher" %>
<%@ page import="com.day.image.Layer" %>


<%!

    final String DAM_ALT_TITLE = "titleAlt";
    final String DAM_CAPTION = "caption";
    final String DAM_CATEGORY = "category";
    final String DAM_DIRECTOR = "director";
    final String DAM_ARTISTSTATEMENT = "artisticStatement";
    final String DAM_SOURCE_URL = "sourceUrl";
    final String DAM_VIDEO_URL = "sourceUrl";

    final String DEFAULT_IMAGE_THUMB_SELECTOR = ".thumb.319.319.png";
    final String SMALL_IMAGE_THUMB_SELECTOR = ".thumb.140.100.png";
    final String DEFAULT_THUMB_SELECTOR_XSM = ".thumb.140.140.png";
    final String DEFAULT_THUMB_SELECTOR_SM = ".thumb.319.319.png";
    final String DEFAULT_THUMB_SELECTOR_MD = ".thumb.800.800.png";
    final String DEFAULT_THUMB_SELECTOR_LG = ".thumb.1280.1280.png";

    final String FORM_CHOOSER_SELECTOR_SERVLET = ".form";


    final String DEFAULT_IMAGE_PATH = "/content/dam/aemdesign/admin/defaults/blank.png";
    final String DEFAULT_IMAGE_PATH_RENDITION = "/content/dam/aemdesign/admin/defaults/blank".concat(DEFAULT_IMAGE_THUMB_SELECTOR);

    final String SMALL_IMAGE_PATH_SELECTOR = "cq5dam" + SMALL_IMAGE_THUMB_SELECTOR;
    final String DEFAULT_IMAGE_PATH_SELECTOR = "cq5dam" + DEFAULT_IMAGE_THUMB_SELECTOR;
    final String DEFAULT_DOWNLOAD_THUMB_ICON = "/etc/clientlibs/aemdesign/icons/file/file.gif";

    final String MEDIUM_THUMBNAIL_SIZE = "320";
    final String LARGE_THUMBNAIL_SIZE = "480";

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
    protected Resource getThumbnail(Asset asset, String renditionName) throws RepositoryException {
        List<Rendition> renditions = asset.getRenditions();

        if (renditions == null || renditions.size() == 0) {
            return null;
        }

        // cycle through renditions to find the specific rendition
        for (Rendition rendition : renditions) {
            if (rendition.getName().equals(renditionName))
                return rendition.adaptTo(Resource.class);
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
     */
    protected Image getScaledProcessedImage(Resource resource, String relativePath, int maxWidth) {
        Image image = this.getProcessedImage(resource, relativePath);

        // set the selector to be the thumbnail selector
        image.setSelector(".scale.thumbnail." + Integer.toString(maxWidth));

        return image;
    }

    protected String getPageImgReferencePath(Page page) throws RepositoryException{
        String imgReference = "";
        Resource imgResource = page.getContentResource("image");
        if(imgResource != null){
            Node imgNode = imgResource.adaptTo(Node.class);
            if(imgNode != null){
                if(imgNode.hasProperty("fileReference")){
                    imgReference = imgNode.getProperty("fileReference").getString();
                }
            }
        }
        return imgReference;
    }

    protected String getSecondaryImageReferencePath(Page thisPage, String componentPath)throws RepositoryException{
        String imagePath = "";
        Node secondaryImage = null;
        if (thisPage != null && componentPath != null) {
            Resource componentResource = thisPage.getContentResource(componentPath);
            if (componentResource != null) {
                secondaryImage = componentResource.adaptTo(Node.class);
                if(secondaryImage.hasProperty("fileReference")){
                    imagePath = secondaryImage.getProperty("fileReference").getString();
                }
            }
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
%>
