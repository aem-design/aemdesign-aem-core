<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.day.cq.dam.api.DamConstants" %>
<%@ page import="org.apache.sling.resource.collection.ResourceCollectionManager" %>
<%@ page import="org.apache.sling.resource.collection.ResourceCollection" %>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%!


    //http://dev.day.com/docs/en/cq/current/javadoc/constant-values.html
    private String getAssetTitle(Asset asset) {
        if (asset==null) {
            return "";
        }
        String title = asset.getMetadataValue(DamConstants.DC_TITLE);
        if (StringUtils.isBlank(title)) {
            title = asset.getName();
        }
        return title;
    }


    public String getTags(String tags)
    {
        StringBuilder tagList = new StringBuilder();
        String tagName = "";

        if (StringUtils.isNotEmpty(tags)) {
            if (tags.indexOf(",") != -1) {
                String[] tagsArray = tags.split(",");
                for (String t : tagsArray) {
                    if (t.indexOf(":") != -1) {
                        tagName = t.split(":")[1];
                    } else {
                        tagName = t;
                    }
                    if (tagName.indexOf("/") != -1) {
                        String[] tagArray = tagName.split("/");
                        String tag = tagArray[tagArray.length - 1];
                        tagList.append(tag + " ");
                    } else {
                        tagList.append(tagName + " ");
                    }
                }
                tagList.setLength(tagList.length()-1);
            } else {

                if (tags.indexOf(":") != -1) {
                    tagName = tags.split(":")[1];
                }

                if (tagName.indexOf("/") != -1) {
                    String[] tagArray = tagName.split("/");
                    String tag = tagArray[tagArray.length - 1];
                    tagList.append(tag + " ");
                } else {
                    tagList.append(tagName + " ");
                }
                return " " + tagList.toString();
            }
        } else {
            return StringUtils.EMPTY;
        }
        return " " + tagList.toString();
    }

    public Map getGalleryTags(String tags)
    {
        Map tagInfo = new HashMap();
        StringBuilder typeList = new StringBuilder();
        StringBuilder modelList = new StringBuilder();

        if (StringUtils.isNotEmpty(tags)) {
            if (tags.indexOf(",") != -1) {
                String[] tagsArray = tags.split(",");
                for (String tt : tagsArray) {
                    String t = tt.replaceAll("(^ )|( $)", "");

                    if (t.indexOf(":") != -1) {
                        String typeTagName = t.split(":")[1];

                        if (typeTagName.indexOf("/") != -1) {
                            String[] tagArray = typeTagName.split("/");
                            String typeTag = tagArray[tagArray.length - 1];

                            typeList.append(" " + typeTag + " ");
                        } else {
                            typeList.append(" " + typeTagName + " ");
                        }
                    }
                }
            }
        }

        if (typeList != null) {
            tagInfo.put("types", typeList.toString().trim());
        } else {
            tagInfo.put("types", StringUtils.EMPTY);
        }

        return tagInfo;
    }

//    public Map getAccessoryGalleryTags(ResourceResolver resolver, String tags)
//    {
//        Map tagInfo = new HashMap();
//        StringBuilder typeList = new StringBuilder();
//        StringBuilder modelList = new StringBuilder();
//
//        if (StringUtils.isNotEmpty(tags)) {
//            if (tags.indexOf(",") != -1) {
//                String[] tagsArray = tags.split(",");
//                for (String tt : tagsArray) {
//                    String t = tt.replaceAll("(^ )|( $)", "");
//
//                    if (t.indexOf(":") != -1) {
//                        String typeTagName = t.split(":")[1];
//
//                        if (typeTagName.indexOf("/") != -1) {
//                            String[] tagArray = typeTagName.split("/");
//                            String typeTag = tagArray[tagArray.length - 1];
//                            typeList.append(" " + typeTag + " ");
//                        } else {
//                            typeList.append(" " + typeTagName + " ");
//                        }
//                    }
//                }
//            } else {
//                if (tags.startsWith("honda-accessory-category")) {
//                    String typeTagName = tags.split(":")[1];
//
//                    if (typeTagName.indexOf("/") != -1) {
//                        String[] tagArray = typeTagName.split("/");
//                        String typeTag = tagArray[tagArray.length - 1];
//                        if (typeTag.equalsIgnoreCase("exterior") ||
//                                typeTag.equalsIgnoreCase("interior") ||
//                                typeTag.equalsIgnoreCase("accessories")) {
//                            typeList.append(" " + typeTag + " ");
//                        }
//                    } else {
//                        if (typeTagName.equalsIgnoreCase("exterior") ||
//                                typeTagName.equalsIgnoreCase("interior") ||
//                                typeTagName.equalsIgnoreCase("accessories")) {
//                            typeList.append(" " + typeTagName + " ");
//                        }
//                    }
//                } else if (tags.startsWith("honda-product")) {
//                    tags = "/etc/tags/" + tags.replace(':', '/');
//                    Node tagNode  = resolver.resolve(tags).adaptTo(Node.class);
//                    try {
//                        //get the title of the tag
//                        String modelTag = tagNode.getProperty("jcr:description").getString();
//                        if (modelTag.startsWith("VTI")) {
//                            modelTag = modelTag.replace("VTI", "VTi");
//                        }
//
//                        if (!(modelTag.equalsIgnoreCase("Accessory option"))) {
//                            modelList.append(modelTag + " ");
//                        }
//                        //modelList.append(tagNode.getProperty("jcr:title").getString());
//                    }
//                    catch(Exception e)
//                    {
//                        //do something if there is a problem
//                        getLogger().info("Error: data-option-models is wrong");
//                        modelList.append("");
//                    }
//                }
//            }
//        }
//
//        if (typeList != null) {
//            tagInfo.put("types", typeList.toString().trim());
//            if (typeList.indexOf("accessories") > -1) {
//                tagInfo.put("options", "Accessory option");
//            }
//        } else {
//            tagInfo.put("types", StringUtils.EMPTY);
//        }
//
//        if (modelList != null) {
//            if (modelList.indexOf("All") > -1) {
//                tagInfo.put("models", "All");
//            } else {
//                tagInfo.put("models", modelList.toString().trim());
//            }
//        } else {
//            tagInfo.put("models", StringUtils.EMPTY);
//        }
//
//        return tagInfo;
//    }
    /**
     * Is this is a DAM asset?
     * @param child the child
     * @return true if it is an asset
     *
     * @throws RepositoryException
     */
    private boolean isDamAsset(Node child) throws RepositoryException {
        return child.getPrimaryNodeType().getName().equals(DamConstants.NT_DAM_ASSET);
    }

    /**
     * Inspect the folder and retrieve the first child's resource
     *
     * @param resolver is the resolver that is able to retrieve resource sby path
     * @param folder is the folder resource to inspect
     * @return the first child resource
     * @throws RepositoryException
     */
    protected Resource getFirstResourceInFolder(ResourceResolver resolver, Resource folder) throws RepositoryException {
        if (folder == null) {
            return null;
        }
        Node folderNode = folder.adaptTo(Node.class);
        if (folderNode.hasNodes()) {
            NodeIterator nodeIterator = folderNode.getNodes();

            // find the first dam:Asset
            while (nodeIterator.hasNext()) {
                Node child = nodeIterator.nextNode();

                if (isDamAsset(child)) {
                    return resolver.getResource(child.getPath());
                }

            }
        }

        return null;
    }


    protected List<Map> getPicturesFromFixedList(ResourceResolver resolver, String[] images) throws RepositoryException
    {
        List<Map> pictures = new ArrayList<Map>();


        //Fixed list
        for(String path: images) {
            Resource image = resolver.getResource(path);



            if (image==null) {
                continue;
            }
            Asset asset = image.adaptTo(Asset.class);

            if (asset==null) {
                continue;
            }
            String title = getAssetTitle(asset);
            String description = asset.getMetadataValue(DamConstants.DC_DESCRIPTION);
            String copyright = asset.getMetadataValue(DamConstants.DC_RIGHTS);
            String creator = asset.getMetadataValue(DamConstants.DC_CREATOR);
            String disclaimer = asset.getMetadataValue("dc:disclaimer");
            String sourceURL = asset.getMetadataValue(DAM_SOURCE_URL);
            String videoURL = asset.getMetadataValue(DAM_VIDEO_URL);
            String url = asset.getPath();
  
            boolean hasVideoSource = false;
            boolean hasOtherSource = false;
            if(!StringUtils.isBlank(videoURL)){
                url = videoURL;
                hasVideoSource = true;
            }else if(!StringUtils.isBlank(sourceURL)){
                url = sourceURL;
                hasOtherSource = true;
            }
            //This pulls the all the tags
            String tagList = getTags(asset.getMetadataValue(TagConstants.PN_TAGS));


            Map dataMap = getGalleryTags(asset.getMetadataValue(TagConstants.PN_TAGS));

            //int modelCount  =  dataMap.get("models")==null?0:dataMap.get("models").toString().split(" ").length;
            /*String tags = asset.getMetadataValue(TagConstants.PN_TAGS);
            if (StringUtils.isNotEmpty(tags)) {
                if (tags.indexOf(":")>0) {
                    tags = tags.split(":")[1];
                }
            }*/

            String body = "";
            Resource imagemeta = image.getChild("jcr:content/metadata");
            if (imagemeta!=null) {
                ValueMap imagevm = imagemeta.adaptTo(ValueMap.class);
                body = imagevm.get("body", "");
                //This only pulls the first tag
                //tagList = getTags(imagevm.get(TagConstants.PN_TAGS, ""));
            }

            String mimeType = asset.getMetadataValue(DamConstants.DC_FORMAT);
            boolean isImage = !StringUtils.isBlank(mimeType) && mimeType.startsWith("image");
            boolean isDoc = !StringUtils.isBlank(mimeType) && mimeType.startsWith("application");
            boolean isVideo = !StringUtils.isBlank(mimeType) && mimeType.startsWith("video");
            boolean isAudio = !StringUtils.isBlank(mimeType) && mimeType.startsWith("audio");

            String href  = url;

            Resource thumbnailResource = null;
            Integer displayHeight = 360;
            Integer displayWidth = 480;
            String videoWidth = "640";
            String videoHeight = "480";
            if (isImage) {
                thumbnailResource = getThumbnailPathName(asset);
                Integer height = Integer.parseInt(asset.getMetadataValue("tiff:ImageLength"));
                Integer width = Integer.parseInt(asset.getMetadataValue("tiff:ImageWidth"));
                displayHeight = 360 ;
                displayWidth = displayHeight * width / height ;
            }else if(isVideo){
                thumbnailResource = getThumbnail(asset, DEFAULT_IMAGE_PATH_SELECTOR);
                videoWidth = asset.getMetadataValue("tiff:ImageWidth");
                videoHeight = asset.getMetadataValue("tiff:ImageLength");
            }

            String thumbnailUrl = thumbnailResource != null ? thumbnailResource.getPath() : null;

            if(hasOtherSource || hasVideoSource){
                thumbnailUrl = asset.getPath();
            }

            if (isImage || isVideo || isAudio) {

                // transform to picture map
                Map pictureInfo = new HashMap();
                pictureInfo.put("title", title==null?"":title);
                pictureInfo.put("description", description==null?"":description);
                pictureInfo.put("copyright", StringUtils.isBlank(escapeBody(copyright)) ? "" : "&amp;copy;"+ escapeBody(copyright));
                pictureInfo.put("disclaimer", disclaimer);
                pictureInfo.put("creator", creator);
                pictureInfo.put("disclaimer", disclaimer);
                pictureInfo.put("body", escapeBody(body));
                pictureInfo.put("thumbnail", mappedUrl(resolver, thumbnailUrl));
                pictureInfo.put("href", mappedUrl(resolver, href));
                pictureInfo.put("isvideo", isVideo);
                pictureInfo.put("isimage", isImage);
                pictureInfo.put("isaudio", isAudio);
                pictureInfo.put("hasVideoSource", hasVideoSource);
                pictureInfo.put("hasOtherSource", hasOtherSource);
                pictureInfo.put("tags", " " + tagList);
                pictureInfo.put("models",  dataMap.get("models")==null?"":dataMap.get("models"));
                pictureInfo.put("types", dataMap.get("types")==null?"":dataMap.get("types"));
                pictureInfo.put("displayWidth", displayWidth);
                pictureInfo.put("displayHeight", displayHeight);
                pictureInfo.put("videoWidth", videoWidth);
                pictureInfo.put("videoHeight", videoHeight);
                //pictureInfo.put("tags", tags);

                pictures.add(pictureInfo);
            }
        }
        return pictures;
    }

    /**
     * Collection is only for Montage.
     * Because the ListNavgation is navigating the collection
     * @param _sling
     * @param _resourceResolver
     * @param collectionPath
     * @return
     * @throws RepositoryException
     */
    protected List<Map> getPicturesFromCollection(SlingScriptHelper _sling, ResourceResolver _resourceResolver, String collectionPath, String assetViewerPagePath,String variant) throws RepositoryException
    {

        final String profile = "montage";
        List<Map> pictures = new ArrayList<Map>();
        Resource listPathR = _resourceResolver.resolve(collectionPath);
        ResourceCollectionManager rcm = (ResourceCollectionManager)_resourceResolver.adaptTo(ResourceCollectionManager.class);
        ResourceCollection rc = rcm.getCollection(listPathR);
        Iterator resCol = rc.getResources();

        List<String> images = traverseCollectionFolder(resCol);


        //Fixed list
        for(String path: images) {
            Resource image = _resourceResolver.getResource(path);

            if (image==null) {
                continue;
            }

            Asset asset = image.adaptTo(Asset.class);

            if (asset==null) {
                continue;
            }

            String title = getAssetTitle(asset);
            String description = asset.getMetadataValue("dam:authorDescription");
            String copyright = asset.getMetadataValue(DamConstants.DC_RIGHTS);

            String creator = asset.getMetadataValue(DamConstants.DC_CREATOR);
            String disclaimer = asset.getMetadataValue("dc:disclaimer");
            String url = asset.getPath();
            //This pulls the all the tags
            String tagList = getTags(asset.getMetadataValue(TagConstants.PN_TAGS));


            Map dataMap = getGalleryTags(asset.getMetadataValue(TagConstants.PN_TAGS));


            String body = "";
            Resource imagemeta = image.getChild("jcr:content/metadata");
            if (imagemeta!=null) {
                ValueMap imagevm = imagemeta.adaptTo(ValueMap.class);
                body = imagevm.get("body", "");
                //This only pulls the first tag
                //tagList = getTags(imagevm.get(TagConstants.PN_TAGS, ""));
            }

            String mimeType = asset.getMetadataValue(DamConstants.DC_FORMAT);
            boolean isImage = !StringUtils.isBlank(mimeType) && mimeType.startsWith("image");
            boolean isDoc = !StringUtils.isBlank(mimeType) && mimeType.startsWith("application");
            boolean isVideo = !StringUtils.isBlank(mimeType) && mimeType.startsWith("video");
            boolean isAudio = !StringUtils.isBlank(mimeType) && mimeType.startsWith("audio");

            String href  =  mappedUrl(_resourceResolver, url);

            if (variant.endsWith(".assetviewer") && StringUtils.isNotEmpty(assetViewerPagePath)){
                // /content/dam/aemdesign-showcase/galleries/m-plus-collection/2012-12-1.jpg.form.html/content/aemdesign-showcase/en/component/media/media-gallery-assetviewer/asset-viewer/av1.html

                String suffix = mappedUrl(_resourceResolver, assetViewerPagePath).concat(DEFAULT_EXTENTION);
                href = url.concat(FORM_CHOOSER_SELECTOR_SERVLET).concat(DEFAULT_EXTENTION).concat(suffix);
            }

            Resource thumbnailResource = null;
            int displayWidth = 300;
            int displayHeight = 300;
            Map pictureInfo = new LinkedHashMap();

            if (isImage) {

                thumbnailResource = getThumbnailPathName(asset);
                Integer height = Integer.parseInt(asset.getMetadataValue("tiff:ImageLength"));
                Integer width = Integer.parseInt(asset.getMetadataValue("tiff:ImageWidth"));

                String imgReference = asset.getPath();

                if (width > 0 && displayWidth > 0){

                   imgReference = imgReference +".transform/" + profile + "/image.jpg";

                    pictureInfo.put("imgUrl",imgReference);

                    float aspect = (float)width/(float)height;
                    displayWidth = Math.round(displayHeight * aspect);

                    pictureInfo.put("displayHeight",displayHeight);
                    pictureInfo.put("displayWidth",displayWidth);
                }

            }else if(isVideo){
                thumbnailResource = getThumbnail(asset, DEFAULT_IMAGE_PATH_SELECTOR);
                Integer width = Integer.valueOf(DEFAULT_IMAGE_THUMB_SELECTOR.split("\\.")[2]);
                Integer height = Integer.valueOf(DEFAULT_IMAGE_THUMB_SELECTOR.split("\\.")[3]);


                if (width > 0 && displayWidth > 0){

                    ///content/dam/images/dog.jpg.transform/feature/image.jpg
                    String imgReference = thumbnailResource.getPath() +".transform/" + profile + "/image.jpg";

                    pictureInfo.put("imgUrl",imgReference);

                    float aspect = (float)width/(float)height;
                    displayWidth = Math.round(displayHeight * aspect);

                    pictureInfo.put("profileHeight",displayHeight);
                    pictureInfo.put("profileWidth",displayWidth);
                }
            }

            String thumbnailUrl = thumbnailResource != null ? thumbnailResource.getPath() : null;
            if (isImage || isVideo || isAudio) {

                // transform to picture map

                pictureInfo.put("title", title==null?"":title);
                pictureInfo.put("description", description==null?"":description);
                pictureInfo.put("copyright", StringUtils.isBlank(escapeBody(copyright)) ? "" : "&amp;copy;"+ escapeBody(copyright));
                pictureInfo.put("disclaimer", disclaimer);
                pictureInfo.put("creator", creator);
                pictureInfo.put("disclaimer", disclaimer);
                pictureInfo.put("body", escapeBody(body));
                pictureInfo.put("thumbnail", mappedUrl(_resourceResolver, thumbnailUrl));
                pictureInfo.put("href", href);
                pictureInfo.put("isvideo", isVideo);
                pictureInfo.put("isimage", isImage);
                pictureInfo.put("isaudio", isAudio);
                pictureInfo.put("tags", " " + tagList);
                pictureInfo.put("models",  dataMap.get("models")==null?"":dataMap.get("models"));
                pictureInfo.put("types", dataMap.get("types")==null?"":dataMap.get("types"));
               // pictureInfo.put("displayWidth", displayWidth);
               // pictureInfo.put("displayHeight", displayHeight);
                //pictureInfo.put("tags", tags);

                pictures.add(pictureInfo);
            }
        }
        return pictures;
    }

    protected List<String> traverseCollectionFolder(Iterator collection){

        List<String> result = new ArrayList<String>();

        while (collection.hasNext()){
            Resource res = (Resource)collection.next();

            if (res.isResourceType("sling:OrderedFolder")){
                Iterator folderItem = res.listChildren();
                result.addAll(traverseCollectionFolder(folderItem));
            }else{
                result.add(res.getPath());
            }

        }
        return result;
    }

    protected List<Map> getGalleryPicturesFromFixedList(ResourceResolver resolver, String[] images) throws RepositoryException
    {
        List<Map> pictures = new ArrayList<Map>();


        //Fixed list
        for(String path: images) {
            Resource image = resolver.getResource(path);

            if (image==null) {
                continue;
            }
            Asset asset = image.adaptTo(Asset.class);

            if (asset==null) {
                continue;
            }
            String title = getAssetTitle(asset);
            title = title.replace("VTi", "VT<span class=\"force-lowercase\">i</span>");

            /*if (title.startsWith("VTi")) {
                title = title.replaceFirst("i", "<span class=\"force-lowercase\">i</span>");
            }*/
            String description = asset.getMetadataValue(DamConstants.DC_DESCRIPTION);
            String copyright = asset.getMetadataValue(DamConstants.DC_RIGHTS);
            String disclaimer = asset.getMetadataValue("dc:disclaimer");
            String url = asset.getPath();
            String video = asset.getMetadataValue("cq:videoembedcode");


//            Map dataMap = getAccessoryGalleryTags(resolver, asset.getMetadataValue(TagConstants.PN_TAGS));
            String tags = asset.getMetadataValue(TagConstants.PN_TAGS);
            if (StringUtils.isNotEmpty(tags)) {
                if (tags.indexOf(":")>0) {
                    tags = tags.split(":")[1];
                }
            }

            String body = "";
            Resource imagemeta = image.getChild("jcr:content/metadata");
            if (imagemeta!=null) {
                ValueMap imagevm = imagemeta.adaptTo(ValueMap.class);
                body = imagevm.get("body","");
            }

            boolean isVideo = false;
            String href  = url;
            String mimeType = asset.getMetadataValue(DamConstants.DC_FORMAT);
            boolean isImage = !StringUtils.isBlank(mimeType) && mimeType.startsWith("image");
            boolean isDoc = !StringUtils.isBlank(mimeType) && mimeType.startsWith("application");

            Resource thumbnailResource = null;

            if (isImage ) {
                thumbnailResource = getThumbnailPathName(asset);
            } /*else {
                thumbnailResource = getThumbnail(asset, "cq5dam.thumbnail.140.100.png");
            }*/

            String thumbnailUrl = thumbnailResource != null ? thumbnailResource.getPath() : null;


            if (isImage) {

                // transform to picture map
                Map pictureInfo = new HashMap();
                pictureInfo.put("title", title==null?"":title);
                pictureInfo.put("copyright", copyright==null?"":escapeBody(copyright));
                pictureInfo.put("description", description==null?"":description);
                pictureInfo.put("disclaimer", disclaimer==null?"":disclaimer);
                pictureInfo.put("thumbnail", mappedUrl(resolver, thumbnailUrl));
                pictureInfo.put("body", escapeBody(body));
//                pictureInfo.put("tags", dataMap.get("types")==null?"":" " + dataMap.get("types"));
//                pictureInfo.put("types", dataMap.get("types")==null?"":dataMap.get("types"));
                if (!body.equals(StringUtils.EMPTY)) {
                    pictureInfo.put("models", StringUtils.EMPTY);
                } else {
//                    pictureInfo.put("models", dataMap.get("models")==null?"":dataMap.get("models"));
                }
//                pictureInfo.put("options", dataMap.get("options")==null?"":dataMap.get("options"));
                //pictureInfo.put("models", dataMap.get("models")==null?"":dataMap.get("models"));
                //pictureInfo.put("tags", tags);
                //pictureInfo.put("tags", tags);
                //pictureInfo.put("href", mappedUrl(_resourceResolver, imageUrl));
                pictureInfo.put("href", mappedUrl(resolver, href));
                pictureInfo.put("isvideo", isVideo);
                pictures.add(pictureInfo);
            }
        }
        return pictures;
    }

    protected List<Map> getGalleryPicturesFromResource(ResourceResolver resolver, Resource folder) throws RepositoryException {
        if (folder == null) {
            return null;
        }
        Node folderNode = folder.adaptTo(Node.class);
        if (folderNode == null) {
            return null;
        }
        NodeIterator nIterator = folderNode.getNodes();

        List<Map> pictures = new ArrayList<Map>();

        while (nIterator.hasNext()) {
            Node child = nIterator.nextNode();

            // not an asset? don't bother
            if (!isDamAsset(child)) {
                continue;
            }

            Resource childResource = resolver.getResource(child.getPath());
            Asset asset = childResource.adaptTo(Asset.class);

            String title = getAssetTitle(asset);
            title = title.replace("VTi", "VT<span class=\"force-lowercase\">i</span>");
            String description = asset.getMetadataValue(DamConstants.DC_DESCRIPTION);
            String copyright = asset.getMetadataValue(DamConstants.DC_RIGHTS);
            String disclaimer = asset.getMetadataValue("dc:disclaimer");
            String imageUrl = asset.getPath();
            //String video = asset.getMetadataValue("videoEmbedCode");
            String video = asset.getMetadataValue("cq:videoembedcode");

            //String tagList = getTags(asset.getMetadataValue(TagConstants.PN_TAGS));
//            Map dataMap = getAccessoryGalleryTags(resolver, asset.getMetadataValue(TagConstants.PN_TAGS));

            String tags =  asset.getMetadataValue(TagConstants.PN_TAGS);

            if (StringUtils.isNotEmpty(tags)) {
                if (tags.indexOf(":")>0) {
                    tags = tags.split(":")[1];
                }
            }
            String body = "";
            Resource imagemeta = childResource.getChild("jcr:content/metadata");
            if (imagemeta!=null) {
                ValueMap imagevm = imagemeta.adaptTo(ValueMap.class);
                body = imagevm.get("body","");

            }

            String href  = "";
            boolean isVideo = false;
            if(isNotEmpty(video))
            {
                href = video;
                isVideo = true;
            }
            else
            {
                href = imageUrl;
            }

            String mimeType = asset.getMetadataValue(DamConstants.DC_FORMAT);
            boolean isImage = !StringUtils.isBlank(mimeType) && mimeType.startsWith("image");
            boolean isDoc = !StringUtils.isBlank(mimeType) && mimeType.startsWith("application");

            Resource thumbnailResource = null;

            if (isImage) {
                thumbnailResource = getThumbnailPathName(asset);
            } /*else {
                thumbnailResource = getThumbnail(asset, "cq5dam.thumbnail.140.100.png");
            }*/

            String thumbnailUrl = thumbnailResource != null ? thumbnailResource.getPath() : null;


            if (isImage) {

                // transform to picture map
                Map pictureInfo = new HashMap();
                pictureInfo.put("title", title==null?"":title);
                pictureInfo.put("description", description==null?"":description);
                pictureInfo.put("copyright", escapeBody(copyright));
                pictureInfo.put("disclaimer", disclaimer==null?"":disclaimer);
                pictureInfo.put("image", mappedUrl(resolver, imageUrl));
                pictureInfo.put("thumbnail", mappedUrl(resolver, thumbnailUrl));
                pictureInfo.put("isvideo", isVideo);
                pictureInfo.put("body", escapeBody(body));
//                pictureInfo.put("tags", dataMap.get("types")==null?"":" " + dataMap.get("types"));
//                pictureInfo.put("types", dataMap.get("types")==null?"":dataMap.get("types"));
                if (!body.equals(StringUtils.EMPTY)) {
                    pictureInfo.put("models", StringUtils.EMPTY);
                } else {
//                    pictureInfo.put("models", dataMap.get("models")==null?"":dataMap.get("models"));
                }
//                pictureInfo.put("options", dataMap.get("options")==null?"":dataMap.get("options"));
                //pictureInfo.put("href", mappedUrl(_resourceResolver, imageUrl));
                pictureInfo.put("href", mappedUrl(resolver, href));
                //pictureInfo.put("tags", tags);

                pictures.add(pictureInfo);
            } /*else if (isDoc) {

                // transform to doc map
                Map docInfo = new HashMap();
                docInfo.put("title", escapeBody(title));
                docInfo.put("description", escapeBody(description));
                docInfo.put("tags", tags);
                docInfo.put("thumbnail", mappedUrl(_resourceResolver, thumbnailUrl));
                pictures.add(docInfo);
            } else {

            }*/
        }

        return pictures;
    }



    /**
     * Get the pictures from a resource which is the base folder of the pictures it contains
     *
     * @param resolver is the resolver that is able to query for certain resources
     * @param folder is the folder to rifle through
     * @return a list of picture map structure {title:, description:, image:, thumbnail: }
     * @throws RepositoryException
     */
    protected List<Map> getPicturesFromResource(ResourceResolver resolver, Resource folder) throws RepositoryException {
        if (folder == null) {
            return null;
        }
        Node folderNode = folder.adaptTo(Node.class);
        if (folderNode == null) {
            return null;
        }
        NodeIterator nIterator = folderNode.getNodes();

        List<Map> pictures = new ArrayList<Map>();

        while (nIterator.hasNext()) {
            Node child = nIterator.nextNode();

            // not an asset? don't bother
            if (!isDamAsset(child)) {
                continue;
            }

            Resource childResource = resolver.getResource(child.getPath());
            Asset asset = childResource.adaptTo(Asset.class);

            String title = getAssetTitle(asset);
            String description = asset.getMetadataValue(DamConstants.DC_DESCRIPTION);
            String copyright = asset.getMetadataValue(DamConstants.DC_RIGHTS);
            String creator = asset.getMetadataValue(DamConstants.DC_CREATOR);
            String disclaimer = asset.getMetadataValue("dc:disclaimer");
            String sourceURL = asset.getMetadataValue(DAM_SOURCE_URL);
            String videoURL = asset.getMetadataValue(DAM_VIDEO_URL);
            String url = asset.getPath();
            boolean hasVideoSource = false;
            boolean hasOtherSource = false;
            if(!StringUtils.isBlank(videoURL)){
                url = videoURL;
                hasVideoSource = true;
            }else if(!StringUtils.isBlank(sourceURL)){
                url = sourceURL;
                hasOtherSource = true;
            }

            String video = asset.getMetadataValue("cq:videoembedcode");
            String tagList = "";//getTags(asset.getMetadataValue(TagConstants.PN_TAGS));
            //Returns all the tags
            tagList = getTags(asset.getMetadataValue(TagConstants.PN_TAGS));

            Map dataMap = getGalleryTags(asset.getMetadataValue(TagConstants.PN_TAGS));

            String body = "";
            Resource imagemeta = childResource.getChild("jcr:content/metadata");
            if (imagemeta!=null) {
                ValueMap imagevm = imagemeta.adaptTo(ValueMap.class);
                body = imagevm.get("body","");

            }

            String href  = url;
            String mimeType = asset.getMetadataValue(DamConstants.DC_FORMAT);
            boolean isImage = !StringUtils.isBlank(mimeType) && mimeType.startsWith("image");
            boolean isDoc = !StringUtils.isBlank(mimeType) && mimeType.startsWith("application");
            boolean isVideo = !StringUtils.isBlank(mimeType) && mimeType.startsWith("video");;

            Resource thumbnailResource = null;
            Integer displayHeight = 360 ;
            Integer displayWidth = 480;
            String videoWidth = "640";
            String videoHeight = "480";
            if (isImage) {
                thumbnailResource = getThumbnailPathName(asset);
                Integer height = Integer.parseInt(asset.getMetadataValue("tiff:ImageLength"));
                Integer width = Integer.parseInt(asset.getMetadataValue("tiff:ImageWidth"));
                displayHeight = 360 ;
                displayWidth = displayHeight * width / height ;
            }else if(isVideo){
                thumbnailResource = getThumbnail(asset, DEFAULT_IMAGE_PATH_SELECTOR);
                videoWidth = asset.getMetadataValue("tiff:ImageWidth");
                videoHeight = asset.getMetadataValue("tiff:ImageLength");
            }

            String thumbnailUrl = thumbnailResource != null ? thumbnailResource.getPath() : null;

            if(hasOtherSource || hasVideoSource){
                thumbnailUrl = asset.getPath();
            }

            if (isImage || isVideo) {

                // transform to picture map
                Map pictureInfo = new HashMap();
                pictureInfo.put("title", escapeBody(title));
                pictureInfo.put("description", escapeBody(description));
                pictureInfo.put("copyright", StringUtils.isBlank(escapeBody(copyright)) ? "" : "&amp;copy;"+ escapeBody(copyright));
                pictureInfo.put("disclaimer", disclaimer);
                pictureInfo.put("creator", creator);
                pictureInfo.put("image", mappedUrl(resolver, url));
                pictureInfo.put("body", escapeBody(body));
                pictureInfo.put("thumbnail", mappedUrl(resolver, thumbnailUrl));
                pictureInfo.put("tags", " " + tagList);
                pictureInfo.put("href", mappedUrl(resolver, href));
                pictureInfo.put("models", tagList);
                pictureInfo.put("isvideo", isVideo);
                pictureInfo.put("isimage", isImage);
                pictureInfo.put("hasVideoSource", hasVideoSource);
                pictureInfo.put("hasOtherSource", hasOtherSource);
                pictureInfo.put("models",  dataMap.get("models")==null?"":dataMap.get("models"));
                pictureInfo.put("types", dataMap.get("types")==null?"":dataMap.get("types"));

                pictureInfo.put("displayWidth", displayWidth);
                pictureInfo.put("displayHeight", displayHeight);
                pictureInfo.put("videoWidth", videoWidth);
                pictureInfo.put("videoHeight", videoHeight);

                //pictureInfo.put("tags", tags);

                pictures.add(pictureInfo);
            } /*else if (isDoc) {

                // transform to doc map
                Map docInfo = new HashMap();
                docInfo.put("title", escapeBody(title));
                docInfo.put("description", escapeBody(description));
                docInfo.put("tags", tags);
                docInfo.put("thumbnail", mappedUrl(resolver, thumbnailUrl));
                pictures.add(docInfo);
            } else {

            }*/
        }

        return pictures;
    }

    /*protected List<Map> getPicturesFromMerchandiseResource(ResourceResolver resolver, Resource folder) throws RepositoryException {
        if (folder == null) {
            return null;
        }
        Node folderNode = folder.adaptTo(Node.class);
        if (folderNode == null) {
            return null;
        }
        NodeIterator nIterator = folderNode.getNodes();

        List<Map> pictures = new ArrayList<Map>();

        while (nIterator.hasNext()) {
            Node child = nIterator.nextNode();

            // not an asset? don't bother
            if (!isDamAsset(child)) {
                continue;
            }

            Resource childResource = resolver.getResource(child.getPath());
            Asset asset = childResource.adaptTo(Asset.class);

            String title = getAssetTitle(asset);
            String description = asset.getMetadataValue(DamConstants.DC_DESCRIPTION);
            String copyright = asset.getMetadataValue(DamConstants.DC_RIGHTS);
            String imageUrl = asset.getPath();

            String tagList = getTags(asset.getMetadataValue(TagConstants.PN_TAGS));
            /*String tags =  asset.getMetadataValue(TagConstants.PN_TAGS);

            if (StringUtils.isNotEmpty(tags)) {
                if (tags.indexOf(":")>0) {
                    tags = tags.split(":")[1];
                }
            }*/


            /*String mimeType = asset.getMetadataValue(DamConstants.DC_FORMAT);
            boolean isImage = !StringUtils.isBlank(mimeType) && mimeType.startsWith("image");
            boolean isDoc = !StringUtils.isBlank(mimeType) && mimeType.startsWith("application");

            Resource thumbnailResource = null;

            if (isImage) {
                thumbnailResource = getThumbnailPathName(asset);
            } /*else {
                thumbnailResource = getThumbnail(asset, "cq5dam.thumbnail.140.100.png");
            }*/

            /*String thumbnailUrl = thumbnailResource != null ? thumbnailResource.getPath() : null;


            if (isImage) {

                // transform to picture map
                Map pictureInfo = new HashMap();
                pictureInfo.put("title", escapeBody(title));
                pictureInfo.put("description", escapeBody(description));
                pictureInfo.put("copyright", escapeBody(copyright));
                pictureInfo.put("image", mappedUrl(resolver, imageUrl));
                pictureInfo.put("thumbnail", mappedUrl(resolver, thumbnailUrl));
                pictureInfo.put("tags", tagList);
                //pictureInfo.put("tags", tags);

                pictures.add(pictureInfo);
            } /*else if (isDoc) {

                // transform to doc map
                Map docInfo = new HashMap();
                docInfo.put("title", escapeBody(title));
                docInfo.put("description", escapeBody(description));
                docInfo.put("tags", tags);
                docInfo.put("thumbnail", mappedUrl(resolver, thumbnailUrl));
                pictures.add(docInfo);
            } else {

            }*/
        /*}

        return pictures;
    }*/

    /**
     * Get the documents from a resource which is the base folder of the documents it contains
     *
     * @param resolver is the resolver that is able to query for certain resources
     * @param folder is the folder to rifle through
     * @return a list of documents map structure {title:, description:, thumbnail: }
     * @throws RepositoryException
     */
    protected List<Map> getDocumentsFromResource(ResourceResolver resolver, Resource folder) throws RepositoryException {
        if (folder == null) {
            return null;
        }
        Node folderNode = folder.adaptTo(Node.class);
        NodeIterator nIterator = folderNode.getNodes();

        List<Map> documents = new ArrayList<Map>();

        while (nIterator.hasNext()) {
            Node child = nIterator.nextNode();

            // not an asset? don't bother
            if (!isDamAsset(child)) {
                continue;
            }

            Resource childResource = resolver.getResource(child.getPath());
            Asset asset = childResource.adaptTo(Asset.class);

            String title = getAssetTitle(asset);
            String description = asset.getMetadataValue(DamConstants.DC_DESCRIPTION);
            /*String copyright = asset.getMetadataValue(DamConstants.DC_RIGHTS);
            String imageUrl = asset.getPath();*/
            String docURL = asset.getPath();

            String tagList = getTags(asset.getMetadataValue(TagConstants.PN_TAGS));
            /*String tags =  asset.getMetadataValue(TagConstants.PN_TAGS);

            if (StringUtils.isNotEmpty(tags)) {
                if (tags.indexOf(":")>0) {
                    tags = tags.split(":")[1];
                }
            }*/

            /*String mimeType = asset.getMetadataValue(DamConstants.DC_FORMAT);*/
            Resource thumbnailResource = resolver.getResource(asset.getPath() + "/jcr:content/renditions/" + DEFAULT_IMAGE_PATH_SELECTOR);
            String thumbnailResourcePath = asset.getPath() + DEFAULT_IMAGE_THUMB_SELECTOR;
            if (thumbnailResource != null) {
                // transform to doc map
                Map docInfo = new HashMap();
                docInfo.put("title", escapeBody(title));
                docInfo.put("description", escapeBody(description));
                docInfo.put("tags", tagList);
                //docInfo.put("tags", tags);
                //docInfo.put("image", thumbnailResource.getPath());
                docInfo.put("image", thumbnailResourcePath);
                docInfo.put("docURL", docURL);
                documents.add(docInfo);
            }

        }

        return documents;
    }

    protected List<Map<String, String>> getPDFDocumentsFromResource(ResourceResolver resolver, Resource folder) throws RepositoryException {
        if (folder == null) {
            return null;
        }
        Node folderNode = folder.adaptTo(Node.class);
        NodeIterator nIterator = folderNode.getNodes();

        List<Map<String, String>> documents = new ArrayList<Map<String, String>>();

        while (nIterator.hasNext()) {
            Node child = nIterator.nextNode();

            // not an asset? don't bother
            if (!isDamAsset(child)) {
                continue;
            }

            Resource childResource = resolver.getResource(child.getPath());
            Asset asset = childResource.adaptTo(Asset.class);

            String title = getAssetTitle(asset);
            String description = asset.getMetadataValue(DamConstants.DC_DESCRIPTION);
            /*
            String copyright = asset.getMetadataValue(DamConstants.DC_RIGHTS);
            String imageUrl = asset.getPath();
            */
            String docURL = asset.getPath();

            String tags = asset.getMetadataValue(TagConstants.PN_TAGS);
            if (StringUtils.isNotEmpty(tags)) {
                if (tags.indexOf(":")>0) {
                    tags = tags.split(":")[1];
                }
            }


            /*String mimeType = asset.getMetadataValue(DamConstants.DC_FORMAT);*/
            Resource thumbnailResource = resolver.getResource(asset.getPath() + "/jcr:content/renditions/" + DEFAULT_IMAGE_PATH_SELECTOR);
            String thumbnailResourcePath = asset.getPath() + DEFAULT_IMAGE_THUMB_SELECTOR;

            if (thumbnailResource != null) {
                // transform to doc map
                Map<String, String> docInfo = new HashMap<String, String>();
                docInfo.put("title", escapeBody(title));
                docInfo.put("description", escapeBody(description));
                docInfo.put("tags", tags);
                //docInfo.put("image", thumbnailResource.getPath());
                docInfo.put("image", thumbnailResourcePath);
                docInfo.put("docURL", docURL);
                documents.add(docInfo);
            }

            Collections.sort(documents, new MapComparator("title") );

        }

        return documents;
    }

    class MapComparator implements Comparator<Map<String, String>>
    {
        private final String key;

        public MapComparator(String key)
        {
            this.key = key;
        }

        public int compare(Map<String, String> first,
                           Map<String, String> second)
        {
            // TODO: Null checking, both for maps and values
            String firstValue = first.get(key);
            String secondValue = second.get(key);
            return secondValue.compareTo(firstValue);
        }
    }

%>