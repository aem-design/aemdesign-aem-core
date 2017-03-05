<%@ page import="com.day.cq.commons.jcr.JcrConstants" %>
<%@ page import="com.day.cq.dam.api.Asset" %>
<%@ page import="org.apache.sling.api.resource.Resource" %>
<%@ page import="org.apache.sling.api.resource.ValueMap" %>
<%@ page import="javax.jcr.RepositoryException" %>
<%!
    private static final String EMPTY_FILE = "empty file";
    private static final String ALT_TITLE = "alt-title";
    private static final String DESCRIPTION = "description";

    //drop target css class = dd prefix + name of the drop target in the edit config
    private static final String  ddClassName = DropTarget.CSS_CLASS_PREFIX + "file";


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

    private String getDownloadTitle(ValueMap _properties, String defaultTitle) {
        return _properties.get(ALT_TITLE, defaultTitle);
    }

    private String getDownloadDescription(ValueMap _properties, String defaultDescription) {
        return _properties.get(DESCRIPTION, defaultDescription);
    }

    /**
     * Determine whether file has an associated data blob, if so then get the formatted
     * date.
     *
     * @param dld is the download to check in
     * @return is the formatted size to return (or null)
     * @throws RepositoryException
     */
    private String getFormattedDownloadSize(Download dld) throws RepositoryException {
        return dld.getData() != null ? getFileSize(dld.getData().getLength()) : null;
    }

    /**
     * Determine the filetype by extracting and uppercasing the last element of the mimetype
     *
     * @param download is the download to do this for
     * @return the filetype for the download
     * @throws RepositoryException thrown when the mimetype cannot be retrieved
     */
    private String getDownloadMimeType(ResourceResolver resolver, Download download) throws RepositoryException {
	    String filePath = download.getFileReference();
	    Resource resource = resolver.resolve(filePath);
	    Asset asset = resource.adaptTo(Asset.class);
	    String mimeType = asset.getMimeType();
	    return mimeType.split("/")[1].toUpperCase();
    }

    /**
     * To determine the file size in humanly readable measurements we take the
     * following formula: measurementType = floor(log10(size) / 3)
     *
     * @param fileSize is the filesize in bytes
     * @return a formatted file size using humanly readable measurements
     */
    private String getFileSize(Long fileSize) {
        if (fileSize == null) {
            return EMPTY_FILE;
        }
        String[] measures = { "bytes", "kB", "MB", "GB"};
        Double measurementIndex = Math.floor(Math.log10(fileSize) / 3.0f);
        Double decSize = fileSize / Math.pow(1024, measurementIndex);
        return String.format("%.0f %S", decSize, measures[measurementIndex.intValue()]);
    }

%>