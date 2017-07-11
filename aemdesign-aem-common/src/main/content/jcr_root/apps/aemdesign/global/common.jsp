<%@ page session="false" import="com.day.cq.dam.api.Asset" %>
<%@ page import="com.day.cq.tagging.Tag" %>
<%@ page import="com.day.cq.tagging.TagConstants" %>
<%@ page import="com.day.cq.tagging.TagManager" %>
<%@ page import="com.day.cq.wcm.api.Page" %>
<%@ page import="com.day.cq.wcm.api.PageManager" %>
<%@ page import="org.apache.commons.lang3.StringEscapeUtils" %>
<%@ page import="org.apache.commons.lang3.text.StrSubstitutor" %>
<%@ page import="org.apache.sling.api.resource.ResourceResolver" %>
<%@ page import="javax.jcr.Node" %>
<%@ page import="javax.jcr.NodeIterator" %>
<%@ page import="javax.jcr.Property" %>
<%@ page import="javax.jcr.Value" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.io.UnsupportedEncodingException" %>
<%@ page import="java.math.BigInteger" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.security.MessageDigest" %>
<%@ page import="java.security.NoSuchAlgorithmException" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.regex.Pattern" %>
<%!

    private static final String PATH_MEDIA_ROOT = "/apps/aemdesign/components/media/";
    private static final String PATH_CONTENT_ROOT = "/apps/aemdesign/components/content/";
    private static final String PATH_DAM_ROOT = "/content/dam/";
    private static final String PATH_DEFAULT_BADGE_BASE = "/apps/aemdesign/components/details/page-details/";
    private static final String COMPONENT_DETAILS_SUFFIX = "-details";
    private static final String PATH_DEFAULT_CONTENT = "article/par";


    private static final String[] MONTH_NAMES = new String[]{
            "Jan", "Feb", "Mar", "Apr",
            "May", "Jun", "Jul", "Aug",
            "Sep", "Oct", "Nov", "Dec"
    };

    /**
     * sling resource type property name
     */
    private static final String RESOURCE_TYPE = "sling:resourceType";

    /**
     * This function html4-escapes the provided string/object.
     * @param body The string/object to encode.
     * @return A properly encoded html4 string.
     * @see StringEscapeUtils#escapeHtml4
     */
    protected String escapeBody(Object body) {
        if (body == null) {
            return null;
        }
        //final XSSAPI xssAPI = sling.getService(XSSAPI.class).getRequestSpecificAPI(slingRequest);
        //return xssAPI.encodeForHTML(body.toString());
        return StringEscapeUtils.escapeHtml4(body.toString());
    }

    /**
     * Get a page's url
     *
     * @param page
     *            is the page to get the url for
     * @return a string with the page url
     */
    protected String getPageUrl(Page page) {
        // get page url
        String pageUrl = "#";

        if (page != null) {
            if (page.getProperties().get("redirectTarget") != null) {
                pageUrl = escapeBody(page.getProperties().get("redirectTarget").toString());
            } else {
                pageUrl = page.getPath().concat(DEFAULT_EXTENTION);
            }
        }

        return pageUrl;
    }

    /**
     * get resource last modified attribute
     * @param resource
     * @return
     */

    protected Long getLastModified(Resource resource) {
        long lastMod = 0L;
        ValueMap values = resource.adaptTo(ValueMap.class);
        if(values != null) {
            Long value = values.get("jcr:lastModified", Long.class);
            if(value == null) {
                value = values.get("jcr:created", Long.class);
            }

            if(value != null) {
                lastMod = value.longValue();
            }
        }

        return lastMod;
    }

    /**
     * Get a page's title, nv title navigation title, or name
     *
     * @param page is the page to get the title for
     * @return a string with the page title
     */
    protected String getPageTitle(Page page) {
        // get page title
        String pageTitle = page.getPageTitle();
        if (StringUtils.isEmpty(pageTitle)) {
            pageTitle = page.getTitle();
        }
        if (StringUtils.isEmpty(pageTitle)) {
            pageTitle = page.getName();
        }
        return pageTitle;

    }

    protected String getPageTitle(Page page, String defaultTitle) {

        String pageTitle = getPageTitle(page);

        if (StringUtils.isEmpty(pageTitle)) {
            pageTitle = defaultTitle;
        }
        return pageTitle;
    }

    /**
     * Get a page's navigation title, or normal title
     *
     * @param page is the page to get the title for
     * @return a string with the page title
     */
    protected String getPageNavTitle(Page page) {
        // get page nav title
        String pageTitle = page.getNavigationTitle();
        if (StringUtils.isEmpty(pageTitle)) {
            pageTitle = page.getTitle();
        }
        if (StringUtils.isEmpty(pageTitle)) {
            pageTitle = page.getName();
        }
        return pageTitle;

    }

    /**
     * get tags for a page
     *
     * @param page is the page to look for the image
     * @return is the relative path to the image
     */
    @Deprecated
    protected String[] getPageContentTypeTag(Page page) {
        if (page == null) {
            return null;
        }
        if (page.getProperties() == null) {
            return null;
        }
        if (page.getContentResource().getValueMap().containsKey(TagConstants.PN_TAGS)== false) {
            return null;
        }
        return page.getContentResource().getValueMap().get(TagConstants.PN_TAGS, new String[]{});
    }

    /**
     * Retrieve the location of an image that was dragged onto the page properties image tab
     *
     * @param page is the page to look for the image
     * @return is the relative path to the image
     */
    protected Image getPageImage(Page page) {
        if (page == null) {
            return null;
        }
        if (page.getProperties() == null) {
            return null;
        }
        return new Image(page.getContentResource(), "image");
    }

    /**
     * Get a JCR value or, if it's not available, return null
     *
     * @param node is the jcr node to find
     * @param property is the property on the node to query
     * @return the value in the property, or null if nothing was found
     * @throws RepositoryException when JCR is having troubles
     */
    protected String getProperty(Node node, String property) throws RepositoryException {
        return this.getPropertyWithDefault(node, property, null);
    }

    /**
     * Get a JCR value or, if it's not available, return the defaultValue
     *
     * @param node is the jcr node to find
     * @param property is the property on the node to query
     * @param defaultValue is the default value to return
     * @return the value in the property, or null if nothing was found
     *
     * @throws RepositoryException when JCR is having troubles
     */
    protected String getPropertyWithDefault(Node node, String property, String defaultValue) throws RepositoryException {
        return StringUtils.defaultString(getSingularPropertyString(node, property), defaultValue);
    }

    protected String getMultiResultPropertyWithDefault(Node node, String property, String defaultValue) throws RepositoryException {
        String[] result = getMultiplePropertyString(node, property);
        return result == null ? defaultValue : StringUtils.defaultString(StringUtils.join(result, ", "), defaultValue);
    }

    /**
     * This function will URL-encode the provided string.
     * @param unencoded The unencoded string.
     * @return A properly encoded string.
     * @see URLEncoder#encode(String, String)
     */
    protected String escapeUrl(String unencoded) {
        try {
            return URLEncoder.encode(unencoded, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return unencoded; // hope for the best.
        }
    }

//    /**
//     * This function will URL-encode the provided string.
//     *
//     * @param unencoded
//     *            The unencoded string.
//     * @return A properly 'ISO-8859-1' encoded string.
//     * @see URLEncoder#encode(String, String)
//     */
//    protected String escapeUrlIso(String unencoded) {
//        try {
//            return URLEncoder.encode(unencoded, "ISO-8859-1");
//        } catch (UnsupportedEncodingException e) {
//            return unencoded; // hope for the best.
//        }
//    }

    /**
     * This function will encode a SQL parameter by replacing the '
     * and % with their correct encoded counter parts
     *
     * @param jcrSql is the variable to encode
     * @return is the encoded variable
     */
    public String escapeJcrSql(String jcrSql) {
        if (jcrSql == null) {
            return null;
        }
        return jcrSql.replace("'", "''").replace("%", "%%");
    }

    /**
     * Convenience method for grabbing the last item in an array.
     * @param array The array to get the item from.
     * @param <T> The type of data stored in the array.
     * @return The last item in the array, or null if the array is null or empty.
     */
    protected <T> T getLastItem(T[] array) {
        if (array == null || array.length < 1) {
            return null;
        }

        return array[array.length - 1];
    }


    /**
     * Determine the badge base, it is able to make a distinction between pages and assets
     *
     * @param pageManager is the page manager
     * @param resourceResolver is the resource resolver
     * @param resourcePath is the resource path to fetch
     * @return the base of the badge to render
     * @throws RepositoryException
     */
    private String getBadgeBase(PageManager pageManager, ResourceResolver resourceResolver, String resourcePath)
            throws RepositoryException {
        if (resourcePath == null) {
            return null;
        } else if (resourcePath.startsWith(PATH_DAM_ROOT)) {
            Resource resource = resourceResolver.getResource(resourcePath);
            return this.getAssetBadgeBase(resource);
        } else {
            Page pageInstance = pageManager.getPage(resourcePath);
            return this.getPageBadgeBase(pageInstance, true);
        }
    }

    /**
     * Get a list of maps with the structure {resource: .., script: ..} for all the media paths
     * that get passed in. This structure can then render the components properly
     *
     * @param resolver resolver to get the resources at the paths
     * @param variant is the badge variation we're displaying
     * @param paths are the paths
     * @return
     */
    protected ArrayList<HashMap> getBadgesForPaths(ResourceResolver resolver, String variant, String[] paths) {
        ArrayList<HashMap> badges = new ArrayList<HashMap>();
        for (String path : paths) {
            Resource resource = resolver.getResource(path);

            String badgeBase = getAssetBadgeBase(resource);
            if (badgeBase == null) {
                continue;
            }

            // setup badge location
            String badgeLocation = badgeBase + String.format("badge.%s.jsp", variant);

            HashMap badgeStruct = new HashMap();
            badgeStruct.put("resource", resource);
            badgeStruct.put("script", badgeLocation);

            badges.add(badgeStruct);
        }
        return badges;
    }

    /**
     * Get the asset badge base location depending on the type of asset.
     *
     * @param resource is the resource to analyze
     * @return the badge base folder
     */
    private String getAssetBadgeBase(Resource resource) {
        if (resource == null) {
            return null;
        }


        // transform to asset
        Asset asset = resource.adaptTo(Asset.class);
        if (asset == null) {
            if (resource.getResourceType().endsWith("Folder")) {
                return PATH_MEDIA_ROOT + "gallery/";
            }
            return null;
        }

        String mimeType = asset.getMimeType();
        if (mimeType.startsWith("video")) {
            return PATH_MEDIA_ROOT + "video/";
        } else if (mimeType.startsWith("audio")) {
            return PATH_MEDIA_ROOT + "audio/";
        } else if (mimeType.startsWith("image")) {
            return PATH_MEDIA_ROOT + "image/";
        } else if (mimeType.endsWith("flash")) {
            return PATH_MEDIA_ROOT + "flash/";
        } else if (mimeType.equals("application/pdf")) {
            return PATH_MEDIA_ROOT + "document/";
        } else if (mimeType.equals("application/reference")) {
            return PATH_MEDIA_ROOT + "reference/";
        }

        return null;
    }

    /**
     * Scans through a provided page for detail components and stores the resulting information in the provided request.
     * @param inputPage The page to scan through.
     * @param request The request to store the information in.
     * @return true if details were found, otherwise false.
     * @throws RepositoryException
     */
    private boolean scanPageForDetails(TagManager tagManager, Page inputPage, HttpServletRequest request) throws RepositoryException {
        setDetailRequestParams(request, null, null);

        if (inputPage == null || request == null) {
            return false;
        }

        // get parsys
        Resource parSys = inputPage.getContentResource(PATH_DEFAULT_CONTENT);
        if (parSys == null) {
            return false;
        }

        NodeIterator nodeIterator = parSys.adaptTo(Node.class).getNodes();
        if (nodeIterator == null) {
            return false;
        }

        // Set up the outputs.
        String detailsType = null;
        String[] shareTags = null;

        while (nodeIterator.hasNext()) {
            Node node = nodeIterator.nextNode();

            // has a resource type?
            if (!node.hasProperty(RESOURCE_TYPE)) {
                continue;
            }

            String resourceType = getSingularPropertyString(node, "sling:resourceType");

            // get the resource type and sanitize the path
            if (StringUtils.isBlank(resourceType) || !resourceType.endsWith(COMPONENT_DETAILS_SUFFIX)) {
                continue;
            }

            if (!resourceType.startsWith("/")) {
                resourceType = "/apps/" + resourceType;
            }

            if (!resourceType.endsWith("/")) {
                resourceType += "/";
            }

            // looking for a share details component
            if (!resourceType.contains("shareDetails")) {
                if (detailsType == null) {
                    detailsType = resourceType;
                }
                continue;
            }


            String shareType = getSingularPropertyString(node, "share-type");
            if (shareType == null) {
                shareType = "page";
            }

            detailsType = resourceType.replace("shareDetails", shareType + COMPONENT_DETAILS_SUFFIX);
            String[] rawShareTags = getMultiplePropertyString(node, "share-with");

            if (rawShareTags != null) {
                // resolve the raw tag values and get its proper ID
                shareTags = new String[rawShareTags.length];
                for (int idx = 0; idx < rawShareTags.length; ++idx) {
                    Tag tag = tagManager.resolve(rawShareTags[idx]);
                    if (tag != null) {
                        shareTags[idx] = tag.getTitle();
                    }
                }
            }

            break;
        }

        return setDetailRequestParams(request, detailsType, shareTags);
    }

    /**
     * A convenience method used by {@link #scanPageForDetails} to set the properties present in the request. Will
     * report on whether anything has been set.
     * @param request The request to add the attributes to.
     * @param resourceType The resource type of the detail component found.
     * @param shareTags The sharing tags for the sharing component found.
     * @return true if either of the inputs are not null, otherwise false.
     * @return true if either of the inputs are not null, otherwise false.
     */
    private boolean setDetailRequestParams(HttpServletRequest request, String resourceType, String[] shareTags) {
        request.setAttribute("detailResourceType", resourceType);
        request.setAttribute("detailShareTags", shareTags);
        return resourceType != null || shareTags != null;
    }

    /**
     * Get the page badge base path for this page
     *
     * @param inputPage is the page to look through for event details
     * @return the path to the page badget
     *
     * @throws RepositoryException
     */
    private String getPageBadgeBase(Page inputPage, boolean useDefault) throws RepositoryException {
        if (inputPage == null) {
            return null;
        }

        // get parsys
        Resource parSys = inputPage.getContentResource(PATH_DEFAULT_CONTENT);
        if (parSys == null) {
            return null;
        }

        NodeIterator nodeIterator = parSys.adaptTo(Node.class).getNodes();
        if (nodeIterator == null) {
            return null;
        }

        while (nodeIterator.hasNext()) {
            Node node = nodeIterator.nextNode();

            // has a resource type?
            if (node.hasProperty(RESOURCE_TYPE)) {

                // get it.
                String resourceType = node.getProperty("sling:resourceType").getString();

                // get the resource type and sanitize the path
                if (validDetailComponent(resourceType)) {

                    if (!resourceType.startsWith("/")) {
                        resourceType = "/apps/" + resourceType;
                    }

                    if (!resourceType.endsWith("/")) {
                        resourceType += "/";
                    }

                    return resourceType;
                }
            }

        }

        if (useDefault) {
            return PATH_DEFAULT_BADGE_BASE;
        }

        // apparently, nothing found
        return null;
    }

    private boolean validDetailComponent(String resourceType) {
        return
                !StringUtils.isBlank(resourceType) &&
                        resourceType.endsWith(COMPONENT_DETAILS_SUFFIX);
    }

    /**
     * Determine whether named script exists
     *
     * @param currentPage is the current page (for a reference to the root node)
     * @param scriptName is the scriptname to check for
     * @return true if the script exists
     *
     * @throws RepositoryException
     */
    private boolean nodeExists(Page currentPage, String scriptName) throws RepositoryException {
        Node rootNode = currentPage.getContentResource().adaptTo(Node.class).getSession().getRootNode();
        if (scriptName.startsWith("/")) {
            scriptName = scriptName.substring(1);
        }
        return rootNode.hasNode(scriptName);
    }


    /**
     * Determine whether the page is visible
     *
     * @param page is the page to determine this for
     * @return true if the page is on, otherwise false is returned.
     */
    private boolean pageIsOn(Page page) {
        Calendar cal = Calendar.getInstance();

        return
                (page.getOffTime() == null || cal.compareTo(page.getOffTime()) <= 0) &&
                        (page.getOnTime() == null || cal.compareTo(page.getOnTime()) >= 0);
    }

    /**
     * Utility function for hashing a string via MD5.
     * @param content The string to perform the hash on.
     * @return The hash if successful, or the original string if not.
     */
    private String hashMd5(String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] md5Arr = digest.digest(content.getBytes("UTF-8"));
            BigInteger bigInt = new BigInteger(1, md5Arr);
            return bigInt.toString(16);
        } catch (UnsupportedEncodingException ueEx) {
//			getLogger().debug("Unable to get UTF-8 version of pagepath");
            return content;
        } catch (NoSuchAlgorithmException nsaEx) { /* they be watchin' */
//			getLogger().warn("No MD5 algorithm found, cannot hash");
            return content;
        }
    }

    /**
     * Protects against the stupid multi-single property bug AEM seems to have. Will get the value off a node property
     * if it exists, or return null if anything goes wrong.
     * @param node The node to get the property value from.
     * @param key The property key to get the value for.
     * @return The value of the property, or null.
     * @throws RepositoryException
     */
    private Value getSingularProperty(Node node, String key) throws RepositoryException {
        if (node == null || !node.hasProperty(key)) {
            return null;
        }

        Property property = node.getProperty(key);
        if (!property.isMultiple()) {
            return property.getValue();
        }

        Value[] values = property.getValues();
        if (values.length < 1) {
            return null;
        }

        return values[0];
    }

    /**
     * Convenience method for safely retrieving a String value from a node.
     * @param node The node to get the property string from.
     * @param key The property key to get the string for.
     * @return The string value of the property, or null.
     * @throws RepositoryException
     */
    private String getSingularPropertyString(Node node, String key) throws RepositoryException {
        Value value = getSingularProperty(node, key);
        return value == null ? null : value.getString();
    }

    /**
     * The polar opposite of {@link #getSingularProperty}, this function makes sure the property being retrieved is
     * presented as an array of values.
     * @param node The node to get the property values from.
     * @param key The property key to get the values for.
     * @return The values of the property or null.
     * @throws RepositoryException
     */
    private Value[] getMultipleProperty(Node node, String key) throws RepositoryException {
        if (node == null || !node.hasProperty(key)) {
            return null;
        }

        Property property = node.getProperty(key);
        if (property.isMultiple()) {
            return property.getValues();
        }

        return new Value[]{property.getValue()};
    }

    /**
     * Convenience method that converts all the property values returned by {@link #getMultipleProperty} to strings.
     * @param node The node to get the property strings from.
     * @param key The property key to get the strings for.
     * @return The strings of the property, or null.
     * @throws RepositoryException
     */
    private String[] getMultiplePropertyString(Node node, String key) throws RepositoryException {
        Value[] values = getMultipleProperty(node, key);
        if (values == null) {
            return null;
        }

        String[] strings = new String[values.length];
        for (int index = 0; index < strings.length; index++) {
            strings[index] = values[index].getString();
        }

        return strings;
    }

    protected String getPageBadgeBase(Page inputPage) throws RepositoryException {
        return getPageBadgeBase(inputPage, StringUtils.EMPTY);
    }

    /**
     * Get the page badge base path for this page
     *
     * @param inputPage
     *            is the page to look through for details
     * @param resourceName
     *            which details component to search for, if blank will return first component with suffix Details
     * @return the path to the page badget
     *
     * @throws RepositoryException
     */
    protected String getPageBadgeBase(Page inputPage, String resourceName) throws RepositoryException {
        if (inputPage == null) {
            return null;
        }

        // get parsys
        Resource parSys = inputPage.getContentResource(PATH_DEFAULT_CONTENT);
        if (parSys == null) {
            return null;
        }

        NodeIterator nodeIterator = parSys.adaptTo(Node.class).getNodes();
        if (nodeIterator == null) {
            return null;
        }

        while (nodeIterator.hasNext()) {
            Node node = nodeIterator.nextNode();

            // has a resource type?
            if (node.hasProperty(RESOURCE_TYPE)) {

                // get it.
                String resourceType = node.getProperty("sling:resourceType").getString();

                if (isEmpty(resourceName)) {
                    resourceName = COMPONENT_DETAILS_SUFFIX;
                }
                // get the resource type and sanitize the path
                if (!StringUtils.isBlank(resourceType) && resourceType.endsWith(resourceName)) {
                    if (!resourceType.startsWith("/")) {
                        resourceType = "/apps/" + resourceType;
                    }
                    if (!resourceType.endsWith("/")) {
                        resourceType += "/";
                    }
                    return resourceType;
                }
            }

        }

        // apparently, nothing found
        return null;
    }


    /**
     * Get String value of a node property if the value of the node property is
     * null, it will get the value from design mode
     *
     * @param _properties   is the node properties
     * @param _currentStyle is the design properties
     * @param propertyName  is the property to be retrieved
     * @param defaultValue  is the default value
     */
    protected String getPropertyValue(ValueMap _properties,
                                      ValueMap _currentStyle, String propertyName, String defaultValue) {
        String propertyValue = _properties.get(propertyName, null);
        if (propertyValue == null) {
            return _currentStyle.get(propertyName, defaultValue);
        }
        return propertyValue;
    }

    /**
     * try parse int
     * @param value
     * @param defaultValue
     * @return
     */
    protected int tryParseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }

    /**
     * Return a JCR node for the component in <code>thisPage</code>
     *
     * @param thisPage
     *            is the page to inspect for component
     * @param componentPath
     *            is the path of the component eg par/venuedetails
     * @return a JCR node or null when not found
     */
    protected Node getComponentNode(Page thisPage, String componentPath) {
        Node componentDetails = null;
        if (thisPage != null && componentPath != null) {
            Resource componentResource = thisPage.getContentResource(componentPath);
            if (componentResource != null) {
                componentDetails = componentResource.adaptTo(Node.class);
            }
        }
        return componentDetails;
    }

    /**
     * Return first mediagallery or video node on <code>thisPage</code>
     *
     * @param thisPage
     *            is the page to inspect for component
     * @return a JCR node or null when not found
     */
    protected Node getFirstMediaNode(Page thisPage) throws RepositoryException{
        Node media = null;
        Node par = getComponentNode(thisPage, "article/par");
        NodeIterator ite = par.getNodes();
            while (ite.hasNext()) {
                Node node = (Node) ite.next();
                String type = node.getProperty(RESOURCE_TYPE).getValue().toString();
                if ((type.indexOf("mediagallery") != -1) || (type.indexOf("video") != -1)) {
                    media = node;
                    break;
                }

            }
        return media;

    }

    /**
     * Return a the property for the given pagepath, componentpath, propertyname
     *
     * @param pageManager  is pagemanager
     * @param pagePath     is path of page to inspect for component
     * @param nodePath     is the path of the component eg par/venuedetails
     * @param propertyName is name of the property
     * @param defaultValue is default value returned if the property is not found
     * @return a string of the property value if found, otherwise it will return
     * the specified default value
     */
    protected String getProperty(PageManager pageManager, String pagePath, String nodePath, String propertyName, String defaultValue)
            throws RepositoryException {
        String propertyValue = defaultValue;
        if (pagePath != null && !pagePath.equals("")) {
            Page page = pageManager.getPage(pagePath);
            Node node = getComponentNode(page, nodePath);
            propertyValue = getPropertyWithDefault(node, propertyName,
                    defaultValue);
        }
        return propertyValue;
    }


    /***
     * return a url to the resource
     * @param _pageManager
     * @param _resource
     * @return
     */
    protected String linkToPage(PageManager _pageManager, Resource _resource) {
        return _pageManager.getContainingPage(_resource).getPath().concat(DEFAULT_EXTENTION);
    }



    private static final Pattern ENTITY_PATTERN = Pattern.compile("(&[\\w\\d]+;)");

    /**
     * function to convert htmlentities into xml entities. Used in the RSS feed.
     *
     * @param html to be converted html
     */
    public String htmlToXmlEntities(String html) {
        return convertAsciiToXml(StringEscapeUtils.unescapeHtml4(html));
    }

    /**
     * Converts the specified string which is in ASCII format to legal XML
     * format. Inspired by XMLWriter by http://www.megginson.com/Software/
     */
    public String convertAsciiToXml(String string) {
        if (isEmpty(string)) {
            return string;
        }

        StringBuffer strBuf = new StringBuffer();
        char ch[] = string.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            switch (ch[i]) {
                case '&':
                    strBuf.append("&amp;");
                    break;
                case '<':
                    strBuf.append("&lt;");
                    break;
                case '>':
                    strBuf.append("&gt;");
                    break;
                case '\"':
                    strBuf.append("&quot;");
                    break;
                default:
                    if (ch[i] > '\u007f') {
                        strBuf.append("&#");
                        strBuf.append(Integer.toString(ch[i]));
                        strBuf.append(';');
                    }
                    else if (ch[i] == '\t') {
                        strBuf.append(' ');
                        strBuf.append(' ');
                        strBuf.append(' ');
                        strBuf.append(' ');
                    }
                    else if ((int) ch[i] >= 32 || (ch[i] == '\n' || ch[i] == '\r')) {
                        strBuf.append(ch[i]);
                    }
            }
        }
        return strBuf.toString();
    }


    public void doDebug(String text, JspWriter out) throws IOException {
        //out.write("<!-- "+text+" -->");
    }

    public void doDebug(String text, String code, JspWriter out) throws IOException {
        //out.write("<!--"+code+":"+text+" -->");
    }


    /***
     * format a message template with Map Values
     * http://commons.apache.org/proper/commons-lang/javadocs/api-3.1/org/apache/commons/lang3/text/StrSubstitutor.html
     * @param template
     * @param map
     * @return
     */

    public String compileMapMessage(String template, Map<String, Object> map) {
        //quick fail
        if (isEmpty(template) || map == null) {
            return "";
        }

        StrSubstitutor sub = new StrSubstitutor(map);
        return sub.replace(template);
    }

%>