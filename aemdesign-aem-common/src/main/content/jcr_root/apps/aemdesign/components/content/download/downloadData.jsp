<%@ page import="com.day.cq.commons.jcr.JcrConstants" %>
<%@ page import="com.day.cq.dam.api.Asset" %>
<%@ page import="org.apache.sling.api.resource.Resource" %>
<%@ page import="org.apache.sling.api.resource.ValueMap" %>
<%@ page import="javax.jcr.RepositoryException" %>
<%!
    private static final String EMPTY_FILE = "empty file";
    private static final String ALT_TITLE = "title";
    private static final String DESCRIPTION = "description";

    //drop target css class = dd prefix + name of the drop target in the edit config
    private static final String  ddClassName = DropTarget.CSS_CLASS_PREFIX + "file";


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
    private String getFormattedDownloadSize(Download dld) {
        String size = "";
        try {
            if (dld.getData() != null) {
                size = getFileSize(dld.getData().getLength());
            }
        } catch (Exception ex) {
            getLogger().error("Exception occurred: " + ex.getMessage(), ex);
        }
        return size;
    }

    /**
     * Determine the filetype by extracting and uppercasing the last element of the mimetype
     *
     * @param download is the download to do this for
     * @return the filetype for the download
     * @throws RepositoryException thrown when the mimetype cannot be retrieved
     */
    private String getDownloadMimeType(ResourceResolver resolver, Download download) {
        String mimeTypeReturn = "";

        try {

            String filePath = download.getFileReference();
            Resource resource = resolver.resolve(filePath);
            Asset asset = resource.adaptTo(Asset.class);
            String mimeType = asset.getMimeType();
            mimeTypeReturn = mimeType.split("/")[1].toUpperCase();

        } catch (Exception ex) {
            getLogger().error("Exception occurred: " + ex.getMessage(), ex);
        }
        return mimeTypeReturn;
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
        String fileSizeReturn = EMPTY_FILE;

        try {

            String[] measures = { "bytes", "kB", "MB", "GB"};
            Double measurementIndex = Math.floor(Math.log10(fileSize) / 3.0f);
            Double decSize = fileSize / Math.pow(1024, measurementIndex);
            fileSizeReturn = String.format("%.0f %S", decSize, measures[measurementIndex.intValue()]);

        } catch (Exception ex) {
            getLogger().error("Exception occurred: " + ex.getMessage(), ex);
        }
        return fileSizeReturn;

    }

%>