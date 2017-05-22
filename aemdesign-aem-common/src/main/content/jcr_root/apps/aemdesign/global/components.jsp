<%@page session="false" %>
<%@ page import="com.adobe.granite.ui.components.AttrBuilder" %>
<%@ page import="com.day.cq.wcm.api.components.Component" %>
<%@ page import="com.day.cq.wcm.api.components.ComponentContext" %>
<%@ page import="com.day.cq.wcm.api.designer.Style" %>
<%@ page import="com.day.cq.wcm.foundation.Placeholder" %>
<%@ page import="com.google.common.base.Throwables" %>
<%@ page import="org.apache.commons.io.IOUtils" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="org.apache.commons.lang3.time.DateFormatUtils" %>
<%@ page import="org.apache.sling.api.SlingHttpServletRequest" %>
<%@ page import="org.apache.sling.api.resource.ResourceResolver" %>
<%@ page import="org.apache.sling.api.wrappers.ValueMapDecorator, org.slf4j.Logger" %>
<%@ page import="org.slf4j.LoggerFactory" %>
<%@ page import="javax.servlet.jsp.PageContext" %>
<%@ page import="java.io.BufferedInputStream" %>
<%@ page import="java.io.InputStream" %>
<%@ page import="java.io.UnsupportedEncodingException" %>
<%@ page import="java.math.BigInteger" %>
<%@ page import="java.security.MessageDigest" %>
<%@ page import="java.security.NoSuchAlgorithmException" %>
<%@ page import="java.text.MessageFormat" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="com.day.cq.tagging.Tag" %>
<%@ page import="com.adobe.granite.xss.XSSAPI" %>
<%@ page import="org.apache.commons.lang3.ArrayUtils" %>
<%@ include file="/apps/aemdesign/global/tags.jsp" %>
<%@ include file="/apps/aemdesign/global/theme.jsp" %>
<%@ include file="/apps/aemdesign/global/security.jsp" %>
<%!

    public static final int COUNT_CONTENT_NODE = 1;
    public static final int DEPTH_ROOTNODE = 1;
    public static final int DEPTH_HOMEPAGE = 2;
    public static final String COMPONENTS_RENDER_MOBILE = "aemdesign.component.render.mobile";

    public static final String NODE_PAR = "./article/par";

    public static final String NODE_DETAILS = "*-details";

    public static final String DEFAULT_VARIANT = "default";
    public static final String DEFAULT_BADGE = "default";

    public static final String DEFAULT_RSS_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss";

    // {
    //   1 required - property name,
    //   2 required - default value,
    //   3 optional - name of component attribute to add value into
    //   4 optional - canonical name of class for handling multivalues, String or Tag
    // }
    public static final Object[][] DEFAULT_FIELDS_STYLE = {
            {"componentId", "","id"},
            {"componentTheme", new String[]{},"class", Tag.class.getCanonicalName()},
            {"componentModifiers", new String[]{},"class", Tag.class.getCanonicalName()},
            {"componentModule", new String[]{},"data-module", Tag.class.getCanonicalName()},
            {"componentChevron", new String[]{},"class", Tag.class.getCanonicalName()},
            {"componentIcon", new String[]{},"class", Tag.class.getCanonicalName()},
            {"positionX", ""},
            {"positionY", ""},
            {"siteThemeCategory", ""},
            {"siteThemeColor", ""},
            {"siteTileColor", ""},
    };

    // {
    //   1 required - property name,
    //   2 required - default value,
    //   3 optional - name of component attribute to add value into
    //   4 optional - canonical name of class for handling multivalues, String or Tag
    // }
    public static final Object[][] DEFAULT_FIELDS_ACCESSIBILITY = {
            {"ariaRole", "","role"},
            {"ariaLabel", "","aria-label"},
            {"ariaDescribedBy", "","aria-describedby"},
            {"ariaLabelledBy", "","aria-labelledby"},
            {"ariaControls", "","aria-controls"},
            {"ariaLive", "","aria-live"},
            {"ariaHidden", "","aria-hidden"},
            {"ariaHaspopup", "","aria-haspopup"},
            {"ariaAccessKey", "","accesskey"},
    };

    // {
    //   1 required - property name,
    //   2 required - default value,
    //   3 optional - name of component attribute to add value into
    //   4 optional - canonical name of class for handling multivalues, String or Tag
    // }
    public static final Object[][] DEFAULT_FIELDS_METADATA = {
            {"metadataContentType", ""},
            {"cq:lastModified", ""},
            {"jcr:lastModified", ""},
            {"jcr:created", ""},
            {"cq:lastReplicated", ""},
    };

    // {
    //   1 required - property name,
    //   2 required - default value,
    //   3 optional - name of component attribute to add value into
    //   4 optional - canonical name of class for handling multivalues, String or Tag
    // }
    public static final Object[][] DEFAULT_FIELDS_ANALYTICS = {
            {"analyticsHitType", ""},
            {"analyticsEventCategory", ""},
            {"analyticsEventAction", ""},
            {"analyticsEventLabel", ""},
    };

    /** Local logging container. */
    private final Logger LOG = LoggerFactory.getLogger(getClass());


    /**
     * TODO: make site RESPONSIVE and remove this crap
     */
    public final void setMobile(HttpServletRequest request) {
        request.setAttribute(COMPONENTS_RENDER_MOBILE,true);
    }

    /**
     *
     * New ComponentField
     *
     */
    public static class ComponentProperties extends ValueMapDecorator {

        /**
         * Creates a new wrapper around a given map.
         *
         * @param base wrapped object
         */
        public ComponentProperties(Map<String, Object> base) {
            super(base);
        }

        public Object get(String name) {
            return super.get(name);
        }

        /**
         * Created empty map
         */
        public ComponentProperties() {
            super(new HashMap());
        }


    }


    /**
     * Get a include file contents
     *
     * @param resourceResolver is the resource
     * @param paths
     * @param separator
     * @return a string with the file contents
     */
    public String getResourceContent(ResourceResolver resourceResolver, String[] paths, String separator) {
        String returnValue = "";

        for (String path : paths) {
            Resource resource = resourceResolver.getResource(path);
            returnValue += getResourceContent(resource);
            if (StringUtils.isNotEmpty(separator) && separator != null) {
                returnValue += separator;
            }
        }

        return returnValue;
    }

    /**
     * Get a include file contents
     *
     * @param resource is the resource
     * @return a string with the file contents
     */
    public String getResourceContent(Resource resource) {
        String returnValue = "";

        if (resource != null) {

            try {
                Node resourceNode = resource.adaptTo(Node.class);

                if (resourceNode != null) {
                    if (resourceNode.hasNode(JcrConstants.JCR_CONTENT)) {
                        Node contentNode = resourceNode.getNode(JcrConstants.JCR_CONTENT);

                        if (contentNode.hasProperty(JcrConstants.JCR_DATA)) {
                            InputStream contentsStream = contentNode.getProperty(JcrConstants.JCR_DATA).getBinary().getStream();

                            byte[] result = null;

                            BufferedInputStream bin = new BufferedInputStream(contentsStream);
                            result = IOUtils.toByteArray(bin);
                            bin.close();
                            contentsStream.close();
                            if (result != null) {
                                returnValue = new String(result);
                            }
                        }
                    }
                }

            } catch (Exception ex) {
                LOG.warn("Could not load file to be included {}", resource.getPath());
            }
        }
        return returnValue;
    }

    /**
     * Read properties for the Component do not use styles
     * @param pageContext current page context
     * @param name name of the property
     * @param defaultValue default value to return if not set
     * @return
     */
    public Object getComponentProperty(PageContext pageContext, String name, Object defaultValue) {
        if (pageContext == null) {
            return "";
        }
        return getComponentProperty(pageContext, name, defaultValue, false);
    }


    /**
     * Read properties for the Component, use component style to override properties if they are not set
     * @param pageContext current page context
     * @param name name of the property
     * @param defaultValue default value for the property
     * @param useStyle use styles properties if property is missing
     * @return
     */
    public Object getComponentProperty(PageContext pageContext, String name, Object defaultValue, Boolean useStyle) {
        //quick fail
        if (pageContext == null) {
            LOG.warn("getComponentProperty, pageContext is ({0})", pageContext);
            return "";
        }

        ValueMap properties = (ValueMap) pageContext.getAttribute("properties");

        if (useStyle) {
            Style currentStyle = (Style) pageContext.getAttribute("currentStyle");
            return getComponentProperty(properties,currentStyle,name,defaultValue,useStyle);
        } else {
            return getComponentProperty(properties,null,name,defaultValue,useStyle);
        }
    }

    /**
     * Read properties for the Component, use component style to override properties if they are not set
     * @param componentResource page to get properties from
     * @param name name of the property
     * @param defaultValue default value for the property
     * @param useStyle use styles properties if property is missing
     * @return
     */
    public Object getComponentProperty(Resource componentResource, Style pageStyle, String name, Object defaultValue, Boolean useStyle) {
        //quick fail
        if (componentResource == null) {
            LOG.warn("getComponentProperty, componentResource is ({0})", componentResource);
            return "";
        }

        ValueMap properties = componentResource.adaptTo(ValueMap.class);

        return getComponentProperty(properties,pageStyle,name,defaultValue,useStyle);

    }
    /**
     * Read properties for the Component, use component style to override properties if they are not set
     * @param componentProperties component properties
     * @param name name of the property
     * @param defaultValue default value for the property
     * @param useStyle use styles properties if property is missing
     * @return
     */
    public Object getComponentProperty(ValueMap componentProperties, Style pageStyle, String name, Object defaultValue, Boolean useStyle) {
        //quick fail
        if (componentProperties == null) {
            LOG.warn("getComponentProperty, componentProperties is ({0})", componentProperties);
            return "";
        }
        if (useStyle && pageStyle == null) {
            LOG.warn("getComponentProperty, useStyle is ({0}) but pageStyle is {1}", useStyle, pageStyle);
            return "";
        }

        if (useStyle) {
            return componentProperties.get(name, pageStyle.get(name, defaultValue));
        } else {
            return componentProperties.get(name, defaultValue);
        }
    }

    /**
     * Return default place holder for the component, use classic UI placeholder is needed
     * @param slingRequest Current Sling reqsuest
     * @param component Current component
     * @return
     */
    @SuppressWarnings("unchecked")
    public String getDefaultPlaceholder(SlingHttpServletRequest slingRequest, Component component) {
        return Placeholder.getDefaultPlaceholder(slingRequest, component, DEFAULT_CLASSIC_PLACEHOLDER);
    }


    /**
     * returns component values with defaults from target component on a page
     * @param pageContext current page context
     * @param componentPage target page
     * @param componentPath target component path
     * @param fieldLists list of fields definition Object{{name, defaultValue, attributeName, valueTypeClass},...}
     * @return
     */
    public ComponentProperties getComponentProperties(PageContext pageContext, Page componentPage, String componentPath, Object[][]... fieldLists) {
        Resource componentResource = componentPage.getContentResource(componentPath);

        return getComponentProperties(pageContext, componentResource, fieldLists);
    }


    /**
     * returns component values with defaults from pageContent Properties
     * @param pageContext current page context
     * @param fieldLists list of fields definition Object{{name, defaultValue, attributeName, valueTypeClass},...}
     * @return
     */
    public ComponentProperties getComponentProperties(PageContext pageContext, Object[][]... fieldLists) {
        return getComponentProperties(pageContext, null, fieldLists);
    }

    /**
     * returns component values with defaults from a targetResource, default to pageContext properties
     * @param pageContext current page context
     * @param targetResource resource to use as source
     * @param fieldLists list of fields definition Object{{name, defaultValue, attributeName, valueTypeClass},...}
     * @return
     */
    @SuppressWarnings("unchecked")
    public ComponentProperties getComponentProperties(PageContext pageContext, Object targetResource, Object[][]... fieldLists) {
        ComponentProperties componentProperties = new ComponentProperties();

        SlingHttpServletRequest slingRequest = (SlingHttpServletRequest)pageContext.getAttribute("slingRequest");
        ResourceResolver resourceResolver = (ResourceResolver)pageContext.getAttribute("resourceResolver");

        SlingScriptHelper sling = (SlingScriptHelper)pageContext.getAttribute("sling");
        HttpServletRequest request = (HttpServletRequest)slingRequest;
        XSSAPI xssAPI = slingRequest.adaptTo(XSSAPI.class);

        ComponentContext componentContext = (ComponentContext) pageContext.getAttribute("componentContext");
        Component component = componentContext.getComponent();

        AttrBuilder itemAttr = new AttrBuilder(request, xssAPI);
        if (component != null) {
            itemAttr.add("class", component.getName().trim());
        }


        ResourceResolver adminResourceResolver  = openAdminResourceResolver(sling);
        try {
            TagManager tagManager = adminResourceResolver.adaptTo(TagManager.class);

            boolean useStyles = true;
            // if targetResource == null get defaults
            ValueMap properties = (ValueMap) pageContext.getAttribute("properties");
            Style currentStyle = (Style) pageContext.getAttribute("currentStyle");

            //if targetResource != null get the appropriate objects
            if (targetResource != null && targetResource.getClass().getCanonicalName().equals(com.adobe.granite.asset.api.Asset.class.getCanonicalName())) {
                try {
                    com.adobe.granite.asset.api.Asset asset = (com.adobe.granite.asset.api.Asset) targetResource;
                    Resource resource = asset.getResourceResolver().getResource(asset.getResourceMetadata().getResolutionPath());
                    properties = resource.adaptTo(ValueMap.class);
                    useStyles=false;
                    //fieldValue = getAssetProperty(pageContext, (com.adobe.granite.asset.api.Asset) targetResource, fieldName, true);
                } catch(Exception ex) {
                    LOG.error("getComponentProperties: could not evaluate target asset",ex);
                    return componentProperties;
                }
            } else if (targetResource != null && targetResource.getClass().getCanonicalName().equals(Resource.class.getCanonicalName())) {
                try {
                    Resource resource = (Resource) targetResource;

                    properties = resource.adaptTo(ValueMap.class);

                    Designer designer = resource.getResourceResolver().adaptTo(Designer.class);

                    currentStyle = designer.getStyle(resource);

                    //getComponentProperty(ValueMap componentProperties, Style pageStyle, String name, Object defaultValue, Boolean useStyle)
                    //fieldValue = getComponentProperty(resourceProperties, resourceStyle, fieldName, fieldDefaultValue, true);
                } catch (Exception ex) {
                    LOG.error("getComponentProperties: could not evaluate target resource",ex);
                    return componentProperties;
                }
            }


            for (Object[][] fieldDefaults: fieldLists) {
                for (Object[] field : fieldDefaults) {
                    if (field.length < 1) {
                        throw new IllegalArgumentException(MessageFormat.format("Key, Value, ..., Value-n expected, instead got {0} fields.", field.length));
                    }
                    String fieldName = field[0].toString();

                    if (componentProperties.containsKey(fieldName)) {
                        //skip entries that already exist
                        //first Object in fieldLists will set a field value
                        //we expect the additional Objects to not override
                        LOG.warn("getComponentProperties: skipping property [{}] its already defined, {}",fieldName, componentContext.getResource().getPath());
                        continue;
                    }

                    Object fieldDefaultValue = field[1];

                    Object fieldValue = getComponentProperty(properties, currentStyle, fieldName, fieldDefaultValue, true);

                    //Empty array with empty string will set the default value
                    if (fieldValue instanceof String && StringUtils.isEmpty(fieldValue.toString())) {
                        fieldValue = fieldDefaultValue;
                    } else if (fieldValue instanceof String[] && fieldValue != null && (StringUtils.isEmpty(StringUtils.join((String[]) fieldValue, "")))) {
                        fieldValue = fieldDefaultValue;
                    }

                    if (field.length > 2) {
                        //if (fieldValue != fieldDefaultValue) {
                        String fieldDataName = field[2].toString();
                        if (StringUtils.isEmpty(fieldDataName)) {
                            fieldDataName = "other";
                        }
                        String fieldValueString = "";
                        String fieldValueType;
                        if (field.length > 3) {
                            fieldValueType = (String)field[3];
                        } else {
                            fieldValueType = String.class.getCanonicalName();
                        }

                        if (fieldValue.getClass().isArray()) {
                            if (ArrayUtils.isNotEmpty((String[])fieldValue)) {
                                if (fieldValueType.equals(Tag.class.getCanonicalName())) {
                                    fieldValueString = getTagsAsValues(tagManager, " ", (String[]) fieldValue);
                                } else {
                                    fieldValueString = StringUtils.join((String[]) fieldValue, ",");
                                }
                            }
                        } else {
                            fieldValueString = fieldValue.toString();
                        }

                        if (StringUtils.isNotEmpty(fieldValueString)) {
                            itemAttr.add(fieldDataName, fieldValueString);
                        }

                    }

                    try {
                        componentProperties.put(fieldName, fieldValue);
                    } catch (Exception ex) {
                        LOG.error("error adding value. " + ex);
                    }
                }
            }

            if (!itemAttr.isEmpty()) {
                componentProperties.put("componentAttributes", itemAttr.build());
            }


        } catch (Exception ex) {
            LOG.error("getComponentProperties: " + ex.getMessage(), ex);
            //out.write( Throwables.getStackTraceAsString(ex) );
        } finally {
            closeAdminResourceResolver(adminResourceResolver);
        }

        return componentProperties;
    }

    /**
     * Depending on whether we're looking at the homepage get the page title from navigation title first
     * otherwise just get the page title
     *
     * @param page is the page to inspect
     * @return the page title
     */
    public String getPageTitleBasedOnDepth(Page page) {

        // if we're looking at the root node try to find the "site-title" property
        if (page.getDepth() == DEPTH_ROOTNODE + COUNT_CONTENT_NODE) {
            String alternativeTitle =
                    page.getProperties().get(
                            JcrConstants.JCR_TITLE,
                            getPageTitle(page)     // if not set, just use the root nodes page title
                    );

            return alternativeTitle;
        }
        // if we're on the homepage, show the navigation title (which is the site name)
        else if (page.getDepth() == DEPTH_HOMEPAGE + COUNT_CONTENT_NODE) {
            return getPageNavTitle(page);
        } else {
            return getPageTitle(page);
        }

    }


    /**
     * Get the unique identifier for this paqe
     *
     * @param listPage is the page to uniqify
     * @return the md5 hash of the page's content path
     */
    public String getUniquePageIdentifier(Page listPage) {
        String uniqueBase = listPage.getPath().substring(1).replace("/", "-");
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] md5Arr = digest.digest(uniqueBase.getBytes("UTF-8"));
            BigInteger bigInt = new BigInteger(1, md5Arr);
            return bigInt.toString(16);
        } catch (UnsupportedEncodingException ueEx) {
            LOG.debug("Unable to get UTF-8 version of pagepath");
            return uniqueBase;
        } catch (NoSuchAlgorithmException nsaEx) { /* they be watchin' */
            LOG.warn("No MD5 algorithm found, cannot hash");
            return uniqueBase;
        }
    }

    /**
     * Get the description of the feed from the page properties, otherwise retrieve
     * the jcr's description value. Null is returned when no description was found
     *
     * @param page the page to interrogage
     * @param properties the properties to fall back on
     * @return the description or null when none was found.
     */
    public String getFeedDescription(Page page, ValueMap properties) {
        if (StringUtils.isEmpty(page.getDescription())) {
            return properties.get("jcr:description", (String) null);
        } else {
            return page.getDescription();
        }
    }


    /**
     * Find the summary field in a 'detail' component or just return the page description
     *
     * @param page is the page to investiage
     * @return
     */
    public String getPageDescription(Page page) throws RepositoryException {
        String pageDescription = page.getDescription();

        Resource parsysR = page.getContentResource("par");
        if (parsysR != null) {
            Node parsysN = parsysR.adaptTo(Node.class);
            NodeIterator nodeIt = parsysN.getNodes();
            while (nodeIt.hasNext()) {
                Node child = nodeIt.nextNode();
                String childName = child.getName().toLowerCase();
                if (childName.endsWith(COMPONENT_DETAILS_SUFFIX)) {
                    return getPropertyWithDefault(child, "summary", pageDescription);
                }
            }
        }
        return pageDescription;
    }


    /**
     * Find the field in a 'detail' component or just return the page description
     *
     * @param page is the page to investiage
     * @return
     */
    public String getPageDetailsField(Page page, String field) throws RepositoryException {
        String defaultPageValue = StringUtils.EMPTY;
        if ("title".equals(field)){
            defaultPageValue =  page.getTitle();
        }else if ("description".equals(field)){
            defaultPageValue =  page.getDescription();
        }

        Resource parsysR = page.getContentResource("./article/par");
        if (parsysR != null) {
            Node parsysN = parsysR.adaptTo(Node.class);
            NodeIterator nodeIt = parsysN.getNodes();
            while (nodeIt.hasNext()) {
                Node child = nodeIt.nextNode();
                String childName = child.getName().toLowerCase();
                if (childName.endsWith(COMPONENT_DETAILS_SUFFIX)) {
                    return getPropertyWithDefault(child, field, defaultPageValue);
                }
            }
        }
        return defaultPageValue;
    }

    /**
     * Transform calendar into a publication date.
     *
     * @param cal is the calendar to transform
     * @return is the formatted RSS date.
     */
    public String formattedRssDate(Calendar cal) {
        if (cal == null) {
            return null;
        }
        return DateFormatUtils.format(cal, DEFAULT_RSS_DATE_FORMAT);
    }

    public void forceNoDecoration(ComponentContext componentContext, IncludeOptions includeOptions) {
        componentContext.setDecorate(false);
        componentContext.setDecorationTagName("");
        componentContext.setDefaultDecorationTagName("");

        includeOptions.forceSameContext(Boolean.FALSE).setDecorationTagName("");

    }

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
     * Find a page which has a Detail Node. The pattern under jcr:content is par/*Details
     * @param page
     * @return
     * @throws RepositoryException
     */
    public Node findDetailNode(Page page) throws RepositoryException{

        Node node = null;

        Resource pageRes = page.getContentResource(NODE_PAR);

        if (pageRes != null){

            Node parNode = pageRes.adaptTo(Node.class);

            NodeIterator detailNodes = parNode.getNodes(NODE_DETAILS);

            while (detailNodes.hasNext()){

                node = detailNodes.nextNode();
                break;
            }


        }else{
          //  log.error("Node ["+NODE_PAR+"] not find in " + page.getPath());
        }
        return node;
    }


    /***
     * compile a message from component properties using one of the component format tag fields
     * @param formatTagFieldName
     * @param defaultFormat
     * @param componentProperties
     * @param sling
     * @return
     */
    public String compileComponentMessage(String formatTagFieldName, String defaultFormat, ComponentProperties componentProperties, SlingScriptHelper sling) {

        if (componentProperties == null || sling == null) {
            return StringUtils.EMPTY;
        }


        String formatTagFieldPath = componentProperties.get(formatTagFieldName,StringUtils.EMPTY);

        String titleFormat = defaultFormat;

        if (isNotEmpty(formatTagFieldPath)) {
            titleFormat = getTagValueAsAdmin(componentProperties.get(formatTagFieldName,defaultFormat),sling);
        }

        return compileMapMessage(titleFormat,componentProperties);

    }

%>
<c:set var="DEFAULT_VARIANT" value="<%= DEFAULT_VARIANT %>"/>
