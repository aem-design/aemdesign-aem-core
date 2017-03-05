<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.day.cq.dam.api.DamConstants" %>
<%!


    //http://dev.day.com/docs/en/cq/current/javadoc/constant-values.html
    private String getAssetTitle(Asset asset) {
        String title = asset.getMetadataValue(DamConstants.DC_TITLE);
        if (StringUtils.isBlank(title)) {
            title = asset.getName();
        }
        return title;
    }



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

    /**
     * Get the pictures from a resource which is the base folder of the pictures it contains
     *
     * @param resolver is the resolver that is able to query for certain resources
     * @param folder is the folder to rifle through
     * @return a list of picture map structure {title:, description:, image:, thumbnail: }
     * @throws RepositoryException
     */
    protected List<Map> getPicturesFromResource(ResourceResolver resolver, Resource folder) throws RepositoryException {
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
            String imageUrl = asset.getPath();
            Resource thumbnailResource = getThumbnailPathName(asset);
            String thumbnailUrl = thumbnailResource != null ? thumbnailResource.getPath() : null;
            String tags =  asset.getMetadataValue(TagConstants.PN_TAGS);
            if (StringUtils.isNotEmpty(tags)){
                tags = tags.split(":")[1];
            }


            String mimeType = asset.getMetadataValue(DamConstants.DC_FORMAT);
            boolean isImage = !StringUtils.isBlank(mimeType) && mimeType.startsWith("image");

            if (isImage) {

                // transform to picture map
                Map pictureInfo = new HashMap();
                pictureInfo.put("title", escapeBody(title));
                pictureInfo.put("description", escapeBody(description));
                pictureInfo.put("image", mappedUrl(imageUrl));
                pictureInfo.put("thumbnail", mappedUrl(thumbnailUrl));
                pictureInfo.put("tags", tags);

                pictures.add(pictureInfo);
            }
        }

        return pictures;
    }

%>