package design.aem.utils.components;

import com.day.cq.contentsync.handler.util.RequestResponseFactory;
import com.day.cq.replication.ReplicationStatus;
import com.day.cq.tagging.TagConstants;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMMode;
import com.day.cq.wcm.api.components.ComponentContext;
import com.day.cq.wcm.api.components.IncludeOptions;
import design.aem.components.ComponentProperties;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.jackrabbit.vault.util.JcrConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.apache.sling.engine.SlingRequestProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static design.aem.utils.components.ComponentsUtil.DETAILS_DESCRIPTION;
import static design.aem.utils.components.ComponentsUtil.DETAILS_TITLE;
import static java.text.MessageFormat.format;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class CommonUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonUtil.class);

    public static final String COMPONENT_DETAILS_SUFFIX = "-details";
    public static final String PATH_DEFAULT_CONTENT = "article/par";
    public static final String DEFAULT_PAR_NAME = "par";

    public static final String[] DEFAULT_LIST_DETAILS_SUFFIX = new String[]{COMPONENT_DETAILS_SUFFIX}; //NOSONAR used by models
    public static final String[] DEFAULT_LIST_PAGE_CONTENT = new String[]{DEFAULT_PAR_NAME, PATH_DEFAULT_CONTENT}; //NOSONAR used by models


    public static final String DAM_LICENSE_FORMAT = "© {4} {0} {1} {2} {3}";

    //http://www.photometadata.org/META-Resources-Field-Guide-to-Metadata
    public static final String DAM_TITLE = com.day.cq.dam.api.DamConstants.DC_TITLE;
    public static final String DAM_DESCRIPTION = com.day.cq.dam.api.DamConstants.DC_DESCRIPTION;
    public static final String DAM_HEADLINE = "photoshop:Headline";
    public static final String DAM_CREDIT = "photoshop:Credit";
    public static final String DAM_SOURCE = "photoshop:Source";
    public static final String DAM_SOURCE_URL = "sourceAsset";
    public static final String DAM_VIDEO_URL = "sourceVideo";
    public static final String DAM_FIELD_LICENSE_COPYRIGHT_OWNER = "xmpRights:Owner";
    public static final String DAM_FIELD_LICENSE_USAGETERMS = "xmpRights:UsageTerms";
    public static final String DAM_FIELD_LICENSE_EXPIRY = "prism:expirationDate";

    public static final String PN_REDIRECT_TARGET = "cq:redirectTarget";
    public static final String SLING_REDIRECT_TARGET = "sling:redirect";

    /**
     * sling resource type property name.
     */
    public static final String RESOURCE_TYPE = "sling:resourceType";

    /**
     * This function html4-escapes the provided string/object.
     *
     * @param body The string/object to encode.
     * @return A properly encoded html4 string.
     * @see StringEscapeUtils#escapeHtml4
     */
    public static String escapeBody(Object body) {
        if (body == null) {
            return null;
        }
        return StringEscapeUtils.escapeHtml4(body.toString());
    }

    /**
     * ger redirect target for a page.
     *
     * @param page page to check
     * @return string with redirect value
     */
    public static String getPageRedirect(Page page) {
        if (page != null && page.getProperties() != null) {
            return page.getProperties().get(PN_REDIRECT_TARGET, page.getProperties().get(SLING_REDIRECT_TARGET, ""));
        } else {
            return "";
        }
    }

    /**
     * Get a page's url.
     *
     * @param page is the page to get the url for
     * @return a string with the page url
     */
    public static String getPageUrl(Page page) {
        // get page url
        String pageUrl = "#";

        if (page != null) {

            pageUrl = getPageRedirect(page);

            if (isEmpty(pageUrl)) {
                pageUrl = page.getPath();
            }

            if (pageUrl.startsWith("/content")
                && !pageUrl.endsWith(ConstantsUtil.DEFAULT_EXTENTION)
                && !pageUrl.contains(ConstantsUtil.DEFAULT_MARK_HASHBANG)
                && !pageUrl.contains(ConstantsUtil.DEFAULT_MARK_QUERYSTRING)) {
                pageUrl = pageUrl.concat(ConstantsUtil.DEFAULT_EXTENTION);
            }

        }

        return escapeBody(pageUrl);
    }

    /**
     * get resource last modified attribute.
     *
     * @param resource resource to use
     * @return resource last modified date
     */
    public static Long getLastModified(Resource resource) {
        long lastMod = 0L;
        ValueMap values = resource.adaptTo(ValueMap.class);
        if (values != null) {
            Long value = values.get(JcrConstants.JCR_LASTMODIFIED, Long.class);
            if (value == null) {
                value = values.get(JcrConstants.JCR_CREATED, Long.class);
            }

            if (value != null) {
                lastMod = value;
            }
        }

        return lastMod;
    }

    /**
     * get page resource created date with failover in order NameConstants.PN_ON_TIME, ReplicationStatus.NODE_PROPERTY_LAST_REPLICATED and JcrConstants.JCR_CREATED.
     *
     * @param pageProperties page properties
     * @return resource onTime or jcr:created value
     */
    public static Long getPageCreated(ValueMap pageProperties) {
        long longDate = 0L;
        if (pageProperties != null) {
            Long value = pageProperties.get(NameConstants.PN_ON_TIME, Long.class); //manually set
            if (value == null) {
                value = getPageLastReplicated(pageProperties);
            }

            if (value != null) {
                longDate = value;
            }
        }

        return longDate;
    }

    /**
     * get resource last replicated date.
     *
     * @param pageProperties page properties
     * @return resource ReplicationStatus.NODE_PROPERTY_LAST_REPLICATED value
     */
    public static Long getPageLastReplicated(ValueMap pageProperties) {
        long longDate = 0L;
        if (pageProperties != null) {
            Long value = pageProperties.get(ReplicationStatus.NODE_PROPERTY_LAST_REPLICATED, Long.class);
            if (value == null) {
                value = pageProperties.get(JcrConstants.JCR_CREATED, Long.class);
            }

            if (value != null) {
                longDate = value;
            }
        }

        return longDate;
    }

    /**
     * Get a page's description from Details component on the page with failover to page properties.
     *
     * @param page                       is the page to get the title for
     * @param detailsComponentProperties details component properties
     * @return a string with title from details component or from page
     */
    public static String getPageDescription(Page page, ValueMap detailsComponentProperties) {
        String pageDescription = "";

        if (detailsComponentProperties != null) {
            pageDescription = detailsComponentProperties.get(DETAILS_DESCRIPTION, "");
        }

        if (isEmpty(pageDescription)) {
            pageDescription = page.getDescription();
        }

        return pageDescription;
    }

    /**
     * Get a page's title from Details component on the page with failover to page properties.
     *
     * @param page             is the page to get the title for
     * @param detailsComponent details component resource
     * @return a string with the page title
     */
    @SuppressWarnings("Duplicates")
    public static String getPageTitle(Page page, Resource detailsComponent) {

        String pageTitle = "";

        if (detailsComponent != null) {

            if (!ResourceUtil.isNonExistingResource(detailsComponent)) {
                ValueMap dcvm = detailsComponent.adaptTo(ValueMap.class);
                if (dcvm != null) {
                    pageTitle = dcvm.get(DETAILS_TITLE, "");
                }
            }
        }

        if (isEmpty(pageTitle)) {
            return getPageTitle(page);
        }

        return pageTitle;

    }


    /**
     * Get a page's title, nv title navigation title, or name.
     *
     * @param page is the page to get the title for
     * @return a string with the page title
     */
    @SuppressWarnings("Duplicates")
    public static String getPageTitle(Page page) {
        // get page title
        String pageTitle = page.getPageTitle();
        if (isEmpty(pageTitle)) {
            pageTitle = page.getTitle();
        }
        if (isEmpty(pageTitle)) {
            pageTitle = page.getName();
        }
        return pageTitle;

    }


    /**
     * Get a page's navigation title, or normal title.
     *
     * @param page is the page to get the title for
     * @return a string with the page title
     */
    public static String getPageNavTitle(Page page) {
        // get page nav title
        String pageTitle = page.getNavigationTitle();
        if (isEmpty(pageTitle)) {
            pageTitle = page.getTitle();
        }
        if (isEmpty(pageTitle)) {
            pageTitle = page.getName();
        }
        return pageTitle;

    }

    /**
     * get tags for a page.
     *
     * @param page is the page to look for the image
     * @return is the relative path to the image
     */
    @Deprecated
    public static String[] getPageContentTypeTag(Page page) {
        if (page == null) {
            return null;
        }
        if (page.getProperties() == null) {
            return null;
        }
        if (page.getContentResource().getValueMap().containsKey(TagConstants.PN_TAGS) == false) {
            return null;
        }
        return page.getContentResource().getValueMap().get(TagConstants.PN_TAGS, new String[]{});
    }

    /**
     * Get a JCR value or, if it's not available, return null.
     *
     * @param node     is the jcr node to find
     * @param property is the property on the node to query
     * @return the value in the property, or null if nothing was found
     * @throws RepositoryException when JCR is having troubles
     */
    public static String getProperty(Node node, String property) throws RepositoryException {
        return getPropertyWithDefault(node, property, null);
    }

    /**
     * Get a JCR value or, if it's not available, return the defaultValue.
     *
     * @param node         is the jcr node to find
     * @param property     is the property on the node to query
     * @param defaultValue is the default value to return
     * @return the value in the property, or null if nothing was found
     * @throws RepositoryException when JCR is having troubles
     */
    public static String getPropertyWithDefault(Node node, String property, String defaultValue) throws RepositoryException {
        return StringUtils.defaultString(getSingularPropertyString(node, property), defaultValue);
    }

    public static boolean validDetailComponent(String resourceType) {
        return
            !StringUtils.isBlank(resourceType) &&
                resourceType.endsWith(COMPONENT_DETAILS_SUFFIX);
    }

    /**
     * Determine whether named script exists.
     *
     * @param page     is the current page (for a reference to the root node)
     * @param nodeName is the node name to check for
     * @return true if the script exists
     */
    public static boolean nodeExists(Page page, String nodeName) {
        if (page == null || isEmpty(nodeName)) {
            LOGGER.error("nodeExists: page={},nodeName={}", page, nodeName);
            return false;
        }

        try {

            Resource pageContent = page.getContentResource();

            if (pageContent == null) {
                LOGGER.error("nodeExists: pageContent={}", pageContent);
                return false;
            }

            Node pageContentNode = pageContent.adaptTo(Node.class);

            if (pageContentNode == null) {
                LOGGER.error("nodeExists: pageContentNode={}", pageContentNode);
                return false;
            }

            Session session = pageContentNode.getSession();

            if (session == null) {
                LOGGER.error("nodeExists: session={}", session);
                return false;
            }

            if (nodeName.startsWith("/")) {
                nodeName = nodeName.substring(1);
            }
            return pageContentNode.hasNode(nodeName);

        } catch (Exception ex) {
            LOGGER.error("nodeExists: ex={}", ex);
            return false;
        }
    }


    /**
     * Determine whether the page is visible.
     *
     * @param page is the page to determine this for
     * @return true if the page is on, otherwise false is returned.
     */
    public static boolean pageIsOn(Page page) {
        Calendar cal = Calendar.getInstance();

        return
            (page.getOffTime() == null || cal.compareTo(page.getOffTime()) <= 0) &&
                (page.getOnTime() == null || cal.compareTo(page.getOnTime()) >= 0);
    }

    /**
     * Utility function for hashing a string via MD5.
     *
     * @param content The string to perform the hash on.
     * @return The hash if successful, or the original string if not.
     */
    @SuppressWarnings({"squid:S4790"})
    public static String hashMd5(String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] md5Arr = digest.digest(content.getBytes(StandardCharsets.UTF_8));
            BigInteger bigInt = new BigInteger(1, md5Arr);
            return bigInt.toString(16);
        } catch (NoSuchAlgorithmException nsaEx) { /* they be watchin' */
            LOGGER.warn("No MD5 algorithm found, cannot hash");
            return content;
        }
    }

    /**
     * Protects against the stupid multi-single property bug AEM seems to have.
     * Will get the value off a node property if it exists, or return null if anything goes wrong.
     *
     * @param node The node to get the property value from.
     * @param key  The property key to get the value for.
     * @return The value of the property, or null.
     */
    public static Value getSingularProperty(Node node, String key) {
        try {
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
        } catch (Exception ex) {
            LOGGER.error("getSingularProperty: ex={0}", ex);
            return null;
        }
    }

    /**
     * Convenience method for safely retrieving a String value from a node.
     *
     * @param node The node to get the property string from.
     * @param key  The property key to get the string for.
     * @return The string value of the property, or null.
     * @throws RepositoryException when can't read content
     */
    public static String getSingularPropertyString(Node node, String key) throws RepositoryException {
        Value value = getSingularProperty(node, key);
        return value == null ? null : value.getString();
    }


    /***
     * find a component in a page root that matches required suffix.
     * @param inputPage is the page to look through for the component
     * @param resourceTypeTail string to use for matching resource type
     * @return path to component
     */
    public static String findComponentInPage(Page inputPage, String[] resourceTypeTail) {
        return findComponentInPage(inputPage, resourceTypeTail, new String[]{PATH_DEFAULT_CONTENT});
    }

    /**
     * find a component in a page root that matches required suffix.
     *
     * @param inputPage        is the page to look through for component
     * @param resourceTypeTail array for suffixes to check as endsWith with sling:resourceType or node name (as failover)
     * @param pageRoots        use matching page root as a staring point for search
     * @return the path to component
     */
    @SuppressWarnings("squid:S3776")
    public static String findComponentInPage(Page inputPage, String[] resourceTypeTail, String[] pageRoots) {

        if (inputPage == null) {
            return StringUtils.EMPTY;
        }

        //well lets look for something as a surprise
        if (resourceTypeTail == null) {
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
            LOGGER.error("findComponentInPage: {}", ex);
            return StringUtils.EMPTY;
        }

        // apparently, nothing found
        return StringUtils.EMPTY;
    }


    /**
     * try parse int.
     *
     * @param value        value to use
     * @param defaultValue default value
     * @return parsed value
     */
    public static int tryParseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }

    /**
     * try parse long.
     *
     * @param value        value to use
     * @param defaultValue default value
     * @return parsed value
     */
    public static long tryParseLong(String value, long defaultValue) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }


    /**
     * Return a JCR node for a first found matching path.
     *
     * @param thisPage  is the page to inspect for newsdetails
     * @param nodePaths paths to look for
     * @return a JCR node or null when not found
     */
    public static String getComponentNodePath(Page thisPage, String[] nodePaths) {
        if (thisPage == null) {
            return "";
        }

        try {
            Node detailsNode = getComponentNode(thisPage, nodePaths);
            if (detailsNode != null) {
                return detailsNode.getPath();
            }
        } catch (Exception ex) {
            LOGGER.error("getComponentNodePath: {}, {}", thisPage, StringUtils.join(nodePaths));
        }
        return "";
    }

    /**
     * Return a JCR node for a first found matching path.
     *
     * @param thisPage  is the page to inspect for newsdetails
     * @param nodePaths paths to look for
     * @return a JCR node or null when not found
     */
    public static Node getComponentNode(Page thisPage, String[] nodePaths) {
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
     * Return a JCR node for the component in <code>thisPage</code>.
     *
     * @param thisPage      is the page to inspect for component
     * @param componentPath is the path of the component eg par/venuedetails
     * @return a JCR node or null when not found
     */
    public static Node getComponentNode(Page thisPage, String componentPath) {
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
     * Return first mediagallery or video node on <code>thisPage</code>.
     *
     * @param thisPage is the page to inspect for component
     * @return a JCR node or null when not found
     */
    public static Node getFirstMediaNode(Page thisPage) {
        Node media = null;
        try {
            Node par = getComponentNode(thisPage, "article/par");
            if (par != null) {
                NodeIterator ite = par.getNodes();
                while (ite.hasNext()) {
                    Node node = (Node) ite.next();
                    String type = node.getProperty(RESOURCE_TYPE).getValue().toString();
                    if ((type.indexOf("mediagallery") != -1) || (type.indexOf("video") != -1)) {
                        media = node;
                        break;
                    }

                }
            }
        } catch (Exception ex) {
            LOGGER.error("getFirstMediaNode: Could not get media node in page {}, err: {}", thisPage, ex);
        }
        return media;

    }


    /***
     * format a message template with Map Values.
     * http://commons.apache.org/proper/commons-lang/javadocs/api-3.1/org/apache/commons/lang3/text/StrSubstitutor.html
     * @param template template to use
     * @param map map of attributes to use
     * @return formatted string
     */
    public static String compileMapMessage(String template, Map<String, Object> map) {
        //quick fail
        if (isEmpty(template) || map == null) {
            return "";
        }

        StrSubstitutor sub = new StrSubstitutor(map);
        return sub.replace(template);
    }


    /***
     * render a resource path as HTML to include in components that reuse content in other resources.
     * @param path path to resources
     * @param resourceResolver resource resolver for request
     * @param sling sling helper
     * @param mode mode to request resource with
     * @param requestAttributeName attribute name to set requestAttributes into
     * @param requestAttributes requestAttributes to set into requestAttributeName
     * @param appendHTMLExtention append .html to end of path
     * @return html string of output
     */
    @SuppressWarnings("unchecked")
    public static String resourceRenderAsHtml(String path, ResourceResolver resourceResolver, SlingScriptHelper sling, WCMMode mode, String requestAttributeName, ComponentProperties requestAttributes, boolean appendHTMLExtention) {
        if (isEmpty(path) || resourceResolver == null || sling == null) {
            String error = format(
                "resourceRenderAsHtml5: params not specified path=\"{0}\",resourceResolver=\"{1}\",sling=\"{2}\""
                , path, resourceResolver, sling);
            LOGGER.error(error);
            return "<!--".concat(error).concat("-->");
        }
        try {
            final RequestResponseFactory _requestResponseFactory = sling.getService(RequestResponseFactory.class);
            final SlingRequestProcessor _requestProcessor = sling.getService(SlingRequestProcessor.class);

            if (_requestResponseFactory != null && _requestProcessor != null) {
                String requestUrl = path;
                if (appendHTMLExtention) {
                    requestUrl = path.concat(".html");
                }

                final HttpServletRequest _req = _requestResponseFactory.createRequest("GET", requestUrl);

                WCMMode currMode = WCMMode.fromRequest(_req);

                if (mode != null) {
                    mode.toRequest(_req);
                } else {
                    WCMMode.DISABLED.toRequest(_req);
                }

                if (requestAttributes != null && isNotEmpty(requestAttributeName)) {
                    _req.setAttribute(requestAttributeName, requestAttributes);
                }

                final ByteArrayOutputStream _out = new ByteArrayOutputStream();
                final HttpServletResponse _resp = _requestResponseFactory.createResponse(_out);

                _requestProcessor.processRequest(_req, _resp, resourceResolver);

                currMode.toRequest(_req);

                return _out.toString();
            } else {
                LOGGER.error("resourceRenderAsHtml: could not get objects, _requestResponseFactory={},_requestProcessor={}", _requestResponseFactory, _requestProcessor);
            }
            return "<![CDATA[could not get objects]]>";
        } catch (Exception e) {
            LOGGER.error("Exception occurred: {}", e);
            return "<![CDATA[" + e.getMessage() + "]]>";
        }
    }

    /***
     * return badge name from selector string.
     * @deprecated use generic-details template and field pattern
     * @param selectorString _slingRequest.getRequestPathInfo().getSelectorString()
     * @return badge name
     */
    @SuppressWarnings({"squid:S4784"})
    @Deprecated
    public static String getBadgeFromSelectors(String selectorString) {
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
     * disable decoration of component.
     * @param componentContext current component context
     * @param includeOptions include options
     */
    public static void forceNoDecoration(ComponentContext componentContext, IncludeOptions includeOptions) {
        componentContext.setDecorate(false);
        componentContext.setDecorationTagName("");
        componentContext.setDefaultDecorationTagName("");

        includeOptions.forceSameContext(Boolean.FALSE).setDecorationTagName("");

    }

    /***
     * set component decoration.
     * @param componentContext current component context
     * @param includeOptions include option
     * @param defDecoration defaut decoration
     */
    public static void setDecoration(ComponentContext componentContext, IncludeOptions includeOptions, String defDecoration) {
        componentContext.setDecorate(true);
        componentContext.setDecorationTagName(defDecoration);
        componentContext.setDefaultDecorationTagName(defDecoration);

        includeOptions.forceSameContext(Boolean.FALSE).setDecorationTagName(defDecoration);

    }

    /**
     * check if object is null.
     *
     * @param source object
     * @return status
     */
    public static boolean isNull(Object source) {
        return source == null;
    }

    /**
     * check if object is not null.
     *
     * @param source object
     * @return status
     */
    public static boolean isNotNull(Object source) {
        return source != null;
    }


    /**
     * get content of a specified URL.
     *
     * @param Url url to load
     * @return string content
     */
    public static String getUrlContent(String Url) {

        if (isNotEmpty(Url)) {

            try {
                URL url = new URL(Url);
                URLConnection conn = url.openConnection();

                // open the stream and put it into BufferedReader
                BufferedReader bufferReader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();

                String inputLine;

                while ((inputLine = bufferReader.readLine()) != null) {
                    sb.append(inputLine);
                }
                bufferReader.close();

                return sb.toString();

            } catch (Exception ignored) {

            }
        }
        return "";
    }

}
