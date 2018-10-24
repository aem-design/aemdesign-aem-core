<%@ page session="false" import="com.day.cq.contentsync.handler.util.RequestResponseFactory" %>
<%@ page import="com.day.cq.wcm.api.components.ComponentContext" %>
<%@ page import="com.day.cq.dam.api.Asset" %>
<%@ page import="com.day.cq.tagging.Tag" %>
<%@ page import="com.day.cq.tagging.TagConstants" %>
<%@ page import="com.day.cq.tagging.TagManager" %>
<%@ page import="com.day.cq.wcm.api.Page" %>
<%@ page import="com.day.cq.wcm.api.PageManager" %>
<%@ page import="org.apache.commons.lang3.StringEscapeUtils" %>
<%@ page import="org.apache.commons.lang3.text.StrSubstitutor" %>
<%@ page import="org.apache.sling.api.resource.ResourceResolver" %>
<%@ page import="org.apache.sling.api.wrappers.SlingHttpServletResponseWrapper" %>
<%@ page import="org.apache.sling.engine.SlingRequestProcessor" %>
<%@ page import="org.apache.sling.api.scripting.SlingScriptHelper" %>
<%@ page import="com.day.cq.replication.ReplicationStatus" %>
<%@ page import="javax.jcr.Node" %>
<%@ page import="javax.jcr.NodeIterator" %>
<%@ page import="javax.jcr.Property" %>
<%@ page import="javax.jcr.Value" %>
<%@ page import="java.io.*" %>
<%@ page import="java.math.BigInteger" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.security.MessageDigest" %>
<%@ page import="java.security.NoSuchAlgorithmException" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.regex.Pattern" %>
<%@ page import="org.apache.sling.api.SlingException" %>
<%@ page import="static java.text.MessageFormat.*" %>
<%@ page import="java.util.regex.Matcher" %>
<%@ page import="java.util.Date" %>
<%!

    private static final String PATH_MEDIA_ROOT = "/apps/aemdesign/components/media/";
    private static final String PATH_CONTENT_ROOT = "/apps/aemdesign/components/content/";
    private static final String PATH_DAM_ROOT = "/content/dam/";
    private static final String PATH_DEFAULT_BADGE_BASE = "/apps/aemdesign/components/details/page-details/";
    private static final String COMPONENT_DETAILS_SUFFIX = "-details";
    private static final String REQUEST_COMPONENT_DETAILS_SUFFIX = "COMPONENT_DETAILS_SUFFIX";
    private static final String PATH_DEFAULT_CONTENT = "article/par";
    private static final String DEFAULT_PAR_NAME = "par";
    private static final String DEFAULT_ARTICLE_NAME = "article";

    private static final String[] DEFAULT_LIST_DETAILS_SUFFIX = new String[] {COMPONENT_DETAILS_SUFFIX};
    private static final String[] DEFAULT_LIST_PAGE_CONTENT = new String[] {DEFAULT_PAR_NAME,PATH_DEFAULT_CONTENT};



    private static final String DAM_LICENSE_FORMAT = "© {4} {0} {1} {2} {3}";

    //http://www.photometadata.org/META-Resources-Field-Guide-to-Metadata
    private static final String DAM_TITLE = com.day.cq.dam.api.DamConstants.DC_TITLE;
    private static final String DAM_DESCRIPTION = com.day.cq.dam.api.DamConstants.DC_DESCRIPTION;
    private static final String DAM_HEADLINE = "photoshop:Headline";
    private static final String DAM_CREDIT = "photoshop:Credit";
    private static final String DAM_CATEGORY = "category";
    private static final String DAM_DIRECTOR = "director";
    private static final String DAM_ARTISTSTATEMENT = "artistStatement";
    private static final String DAM_SOURCE = "photoshop:Source";
    private static final String DAM_SOURCE_ORIGIN = "dc:source";
    private static final String DAM_SOURCE_RELATION = "dc:relation";
    private static final String DAM_SOURCE_URL = "sourceAsset";
    private static final String DAM_VIDEO_URL = "sourceVideo";
    private static final String DAM_DISCLAIMER = "dc:disclaimer";
    private static final String DAM_FIELD_LICENSE_COPYRIGHT_OWNER = "xmpRights:Owner";
    private static final String DAM_FIELD_LICENSE_USAGETERMS = "xmpRights:UsageTerms";
    private static final String DAM_FIELD_LICENSE_EXPIRY = "prism:expirationDate";

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

            pageUrl = page.getPath();

            ValueMap pageProps = page.getProperties();
            if (pageProps != null) {
                String redirectTarget = pageProps.get("redirectTarget", pageProps.get("cq:redirectTarget", pageProps.get("sling:redirect", "")));
                if (isNotEmpty(redirectTarget)) {
                    pageUrl = redirectTarget;
                }
            }

            if (pageUrl.startsWith("/content")
                    && !pageUrl.endsWith(DEFAULT_EXTENTION)
                    && !pageUrl.contains(DEFAULT_MARK_HASHBANG)
                    && !pageUrl.contains(DEFAULT_MARK_QUERYSTRING) ) {
                pageUrl = pageUrl.concat(DEFAULT_EXTENTION);
            }

        }

        return escapeBody(pageUrl);
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

    /***
     * find a component in a page root that matches required suffix
     * @param inputPage is the page to look through for the component
     * @param resourceTypeTail
     * @return
     */
    protected String findComponentInPage(Page inputPage, String[] resourceTypeTail) {
        return findComponentInPage(inputPage,resourceTypeTail,new String[] {PATH_DEFAULT_CONTENT});
    }

    /**
     * find a component in a page root that matches required suffix
     *
     * @param inputPage is the page to look through for component
     * @param resourceTypeTail array for suffixes to check as endsWith with sling:resourceType or node name (as failover)
     * @param pageRoots use matching page root as a staring point for search
     * @return the path to component
     *
     * @throws RepositoryException
     */
    protected String findComponentInPage(Page inputPage, String[] resourceTypeTail, String[] pageRoots) {

        if (inputPage == null) {
            return StringUtils.EMPTY;
        }

        //well lets look for something as a surprise
        if (resourceTypeTail == null ) {
            resourceTypeTail = DEFAULT_LIST_DETAILS_SUFFIX;
        }

        //lets just look in page root
        if (pageRoots == null) {
            pageRoots = new String[0];
        }

        try {
            //try to find a root page if specified
            Resource rootResource = null;
            for (String rootPath : pageRoots) {
                rootResource = inputPage.getContentResource(rootPath);

                if (rootResource != null) {
                    break;
                }
            }
            //if not found start at the top of the page jcr:content
            if (rootResource == null) {
                rootResource = inputPage.getContentResource();
            }

            //if not bail
            if (rootResource == null) {
                return StringUtils.EMPTY;
            }

            //if not bail
            NodeIterator nodeIterator = rootResource.adaptTo(Node.class).getNodes();
            if (nodeIterator == null) {
                return StringUtils.EMPTY;
            }

            while (nodeIterator.hasNext()) {
                Node node = nodeIterator.nextNode();
                String checkValue = node.getName();
                // has a resource type? lets use that then
                if (node.hasProperty(RESOURCE_TYPE)) {
                    // get it resource Type
                    checkValue = node.getProperty("sling:resourceType").getString();
                }

                //try to match tail of resource
                for (String item : resourceTypeTail) {
                    // get the resource type and sanitize the path
                    if (StringUtils.isNotEmpty(item) && checkValue.endsWith(item)) {
                        return node.getPath();
                    }
                }
            }
        } catch (Exception ex) {
            getLogger().error("findComponentInPage: " + ex.toString());
            return StringUtils.EMPTY;
        }

        // apparently, nothing found
        return StringUtils.EMPTY;
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
     * Return a JCR node for a first found matching path
     *
     * @param thisPage is the page to inspect for newsdetails
     * @return a JCR node or null when not found
     */
    private String getComponentNodePath(Page thisPage,String[] nodePaths) {
        if (thisPage == null) {
            return "";
        }

        try {
            Node detailsNode = getComponentNode(thisPage, nodePaths);
            return detailsNode.getPath();
        } catch (Exception ex) {
            getLogger().error("getComponentNodePath: " + thisPage + ", " + StringUtils.join(nodePaths));
        }
        return "";
    }

    /**
     * Return a JCR node for a first found matching path
     *
     * @param thisPage is the page to inspect for newsdetails
     * @return a JCR node or null when not found
     */
    private Node getComponentNode(Page thisPage,String[] nodePaths) {
        if (thisPage == null) {
            return null;
        }

        Node detailsNode = null;

        for (String nodePath : nodePaths) {
            Resource detailResource = thisPage.getContentResource(nodePath);
            if (detailResource != null) {
                detailsNode = detailResource.adaptTo(Node.class);
                return detailsNode;
            }
        }
        return null;
    }


    /**
     * Return a JCR node for a first found matching path
     *
     * @param thisPage is the page to inspect for newsdetails
     * @return a JCR node or null when not found
     */
    private String getComponentNodePath(Page thisPage,String nodePath) {
        if (thisPage == null) {
            return "";
        }

        try {
            Node detailsNode = getComponentNode(thisPage, nodePath);
            return detailsNode.getPath();
        } catch (Exception ex) {
            getLogger().error("getComponentNodePath: " + thisPage + ", " + nodePath);
        }
        return "";
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



    /***
     * request a resource similar to sling:include
     * @param path resource to include
     * @param response current response
     * @param request current request
     * @return html string of output
     */
    public String resourceIncludeAsHtml(com.day.cq.wcm.api.components.ComponentContext componentContext, String path, SlingHttpServletResponse response, SlingHttpServletRequest request) {
        if (componentContext == null || isEmpty(path) || response == null || request == null) {
            String error = format(
                    "resourceIncludeAsHtml1: params not specified componentContext=\"{0}\",path=\"{1}\",response=\"{2}\",request=\"{3}\"",
                    componentContext,path,response,request);
            getLogger().error(error);
            return "<!--".concat(error).concat("-->");
        }
        try {
            return resourceIncludeAsHtml(componentContext, path, response, request, null);
        } catch (SlingException ex) {
            return "<!--resourceIncludeAsHtml:".concat(ex.getMessage()).concat("-->");
        }
    }
    /***
     * request a resource similar to sling:include
     * @param path resource to include
     * @param response current response
     * @param request current request
     * @param mode mode to request resource with
     * @return html string of output
     */
    public String resourceIncludeAsHtml(com.day.cq.wcm.api.components.ComponentContext componentContext, String path, SlingHttpServletResponse response, SlingHttpServletRequest request, WCMMode mode) {
        if (componentContext == null || isEmpty(path) || response == null || request == null) {
            String error = format(
                    "resourceIncludeAsHtml2: params not specified componentContext=\"{0}\",path=\"{1}\",response=\"{2}\",request=\"{3}\",mode=\"{4}\"",
                    componentContext,path,response,request,mode);
            getLogger().error(error);
            return "<!--".concat(error).concat("-->");
        }

        WCMMode currMode = WCMMode.fromRequest(request);

        String defDecor = componentContext.getDefaultDecorationTagName();

        IncludeOptions includeOptions = IncludeOptions.getOptions(request,true);

        final Writer buffer = new StringWriter();

        try  {


            final ServletOutputStream stream = new ServletOutputStream() {
                @Override
                public void write(int b)
                        throws IOException
                {
                    buffer.append((char)b);
                }

                @Override
                public boolean isReady() {
                    return false;
                }

                @Override
                public void setWriteListener(javax.servlet.WriteListener writeListener) {

                }
            };

            SlingHttpServletResponseWrapper wrapper = new SlingHttpServletResponseWrapper(response) {
                public ServletOutputStream getOutputStream()
                {
                    return stream;
                }

                public PrintWriter getWriter() throws IOException {
                    return new PrintWriter(buffer);
                }

                public SlingHttpServletResponse getSlingResponse()
                {
                    return super.getSlingResponse();
                }
            };


            disableEditMode(componentContext,includeOptions,request);

            String key = "apps.aemdesign.components.content.reference:" + path;

            if (request.getAttribute(key) == null || Boolean.FALSE.equals(request.getAttribute(key))) {
                request.setAttribute(key, Boolean.TRUE);
            } else {
                //throw new IllegalStateException("Reference loop: " + path);
                getLogger().error("Reference loop: " + path);
                buffer.append("<!--Reference loop: ".concat(path).concat("-->"));
            }

            RequestDispatcher dispatcher = request.getRequestDispatcher(path + ".html");

            dispatcher.include(request, wrapper);

            request.removeAttribute(key);

        } catch (Exception e) {
            getLogger().error("Exception occured: " + e.getMessage(), e);
        } finally {

            enableEditMode(currMode, componentContext, defDecor, includeOptions, request);

            currMode.toRequest(request);

        }

        return buffer.toString();

    }

    //TODO: convert this to JSTL TAG
    /***
     * render a resource path as HTML to include in components that reuse content in other resources
     * @param path path to resources
     * @param resourceResolver resource resolver for request
     * @param sling sling helper
     * @return html string of output
     */
    public String resourceRenderAsHtml(String path, ResourceResolver resourceResolver, SlingScriptHelper sling) {
        if (isEmpty(path) || resourceResolver == null || sling ==null) {
            String error = format(
                    "resourceRenderAsHtml3: params not specified path=\"{0}\",resourceResolver=\"{1}\",sling=\"{2}\""
                    ,path,resourceResolver,sling);
            getLogger().error(error);
            return "<!--".concat(error).concat("-->");
        }
        try {
            return resourceRenderAsHtml(path, resourceResolver, sling, null, null);
        } catch (SlingException ex) {
            return "<!--resourceRenderAsHtml:".concat(ex.getMessage()).concat("-->");
        }
    }

    //TODO: convert this to JSTL TAG
    /***
     * render a resource path as HTML to include in components that reuse content in other resources
     * @param resource path to resources
     * @param resourceResolver resource resolver for request
     * @param sling sling helper
     * @return html string of output
     */
    public String resourceRenderAsHtml(Resource resource, ResourceResolver resourceResolver, SlingScriptHelper sling) {
        if (resource == null || resourceResolver == null || sling ==null) {
            String error = format(
                    "resourceRenderAsHtml4: params not specified path=\"{0}\",resourceResolver=\"{1}\",sling=\"{2}\""
                    ,resource,resourceResolver,sling);
            getLogger().error(error);
            return "<!--".concat(error).concat("-->");
        }
        try {
            return resourceRenderAsHtml(resource.getPath(), resourceResolver, sling, null, null);
        } catch (SlingException ex) {
            return "<!--resourceRenderAsHtml:".concat(ex.getMessage()).concat("-->");
        }
    }

    /***
     * render a resource path as HTML to include in components that reuse content in other resources
     * @param path path to resources
     * @param resourceResolver resource resolver for request
     * @param sling sling helper
     * @return html string of output
     */
    public String resourceRenderAsHtml(String path, ResourceResolver resourceResolver, SlingScriptHelper sling,  String requestAttributeName, ComponentProperties requestAttributes) {
        if (isEmpty(path) || resourceResolver == null || sling ==null) {
            String error = format(
                    "resourceRenderAsHtml4: params not specified path=\"{0}\",resourceResolver=\"{1}\",sling=\"{2}\""
                    ,path,resourceResolver,sling);
            getLogger().error(error);
            return "<!--".concat(error).concat("-->");
        }
        try {
            return resourceRenderAsHtml(path, resourceResolver, sling, null, requestAttributeName, requestAttributes);
        } catch (SlingException ex) {
            return "<!--resourceRenderAsHtml:".concat(ex.getMessage()).concat("-->");
        }
    }

    /***
     * render a resource path as HTML to include in components that reuse content in other resources
     * @param path path to resources
     * @param resourceResolver resource resolver for request
     * @param sling sling helper
     * @param mode mode to request resource with
     * @return html string of output
     */
    public String resourceRenderAsHtml(String path, ResourceResolver resourceResolver, SlingScriptHelper sling, WCMMode mode, String requestAttributeName, ComponentProperties requestAttributes) {
        if (isEmpty(path) || resourceResolver == null || sling ==null) {
            String error = format(
                    "resourceRenderAsHtml5: params not specified path=\"{0}\",resourceResolver=\"{1}\",sling=\"{2}\""
                    ,path,resourceResolver,sling);
            getLogger().error(error);
            return "<!--".concat(error).concat("-->");
        }
        try {
            final RequestResponseFactory _requestResponseFactory = sling.getService(RequestResponseFactory.class);
            final SlingRequestProcessor _requestProcessor = sling.getService(SlingRequestProcessor.class);

            String requestUrl =  path.concat(".html");

            final HttpServletRequest _req = _requestResponseFactory.createRequest("GET", requestUrl);

            WCMMode currMode = WCMMode.fromRequest(_req);

            if (mode != null) {
                mode.toRequest(_req);
            } else {
                WCMMode.DISABLED.toRequest(_req);
            }

            if (requestAttributes != null && isNotEmpty(requestAttributeName)) {
                _req.setAttribute(requestAttributeName,requestAttributes);
            }

            final ByteArrayOutputStream _out = new ByteArrayOutputStream();
            final HttpServletResponse _resp = _requestResponseFactory.createResponse(_out);

            _requestProcessor.processRequest(_req, _resp, resourceResolver);

            currMode.toRequest(_req);

            return _out.toString();

        } catch (Exception e) {
            getLogger().error("Exception occurred: " + e.getMessage(), e);
            return "<![CDATA["+e.getMessage()+"]]>";
        }
    }

    /***
     * return badge name from selector string
     * @param selectorString _slingRequest.getRequestPathInfo().getSelectorString()
     * @return
     */
    public String getBadgeFromSelectors(String selectorString) {
        String badge = "";

        if (selectorString != null) {

            String badgePattern = "((badge{1,})(.\\w+){0,1})";

            Pattern pattern = Pattern.compile(badgePattern);

            Matcher matcher = pattern.matcher(selectorString);

            boolean matches = matcher.matches();

            if (matches) {
                badge = matcher.group(1);
            }
        }

        return badge;
    }


    /***
     * disable decoration of component
     * @param componentContext current component context
     * @param includeOptions include options
     */
    public void forceNoDecoration(ComponentContext componentContext, IncludeOptions includeOptions) {
        componentContext.setDecorate(false);
        componentContext.setDecorationTagName("");
        componentContext.setDefaultDecorationTagName("");

        includeOptions.forceSameContext(Boolean.FALSE).setDecorationTagName("");

    }

    /***
     * set component decoration
     * @param componentContext current component context
     * @param includeOptions include option
     * @param defDecoration defaut decoration
     */
    public void setDecoration(ComponentContext componentContext, IncludeOptions includeOptions, String defDecoration) {
        componentContext.setDecorate(true);
        componentContext.setDecorationTagName(defDecoration);
        componentContext.setDefaultDecorationTagName(defDecoration);

        includeOptions.forceSameContext(Boolean.FALSE).setDecorationTagName(defDecoration);

    }

    /**
     * disables edit mode for the request
     * @param request
     */
    @SuppressWarnings("unchecked")
    public void disableEditMode(ComponentContext componentContext, IncludeOptions includeOptions, SlingHttpServletRequest request) {
        forceNoDecoration(componentContext,includeOptions);

        WCMMode.DISABLED.toRequest(request);
    }

    /**
     * enables edit mode and resets component decoration
     * @param toWCMMode current WCM mode
     * @param componentContext
     * @param defDecoration default decoration
     * @param componentContext component context
     * @param includeOptions
     * @param request
     */

    @SuppressWarnings("unchecked")
    public void enableEditMode(WCMMode toWCMMode, ComponentContext componentContext, String defDecoration, IncludeOptions includeOptions, SlingHttpServletRequest request) {
        setDecoration(componentContext,includeOptions,defDecoration);

        toWCMMode.toRequest(request);
    }

    /**
     * return published date for a page
     * @param page
     * @param defaultValue
     * @return
     */
    public Date getPageLastPublished(Page page, Date defaultValue) {

        if (page == null) {
            return defaultValue;
        }

        ValueMap pageProps = page.getProperties();

        if (pageProps != null) {
            return pageProps.get(ReplicationStatus.NODE_PROPERTY_LAST_REPLICATED, defaultValue);
        }

        return defaultValue;
    }

%>
