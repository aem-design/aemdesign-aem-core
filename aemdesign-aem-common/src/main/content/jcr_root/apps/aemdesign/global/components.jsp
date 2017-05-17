<%@page session="false" %>
<%@ page import="com.day.cq.wcm.api.components.Component" %>
<%@ page import="com.day.cq.wcm.api.components.ComponentContext" %>
<%@ page import="com.day.cq.wcm.api.designer.Style" %>
<%@ page import="com.day.cq.wcm.foundation.Placeholder" %>
<%@ page import="com.google.common.base.Throwables" %>
<%@ page import="org.apache.commons.io.IOUtils" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="org.apache.commons.lang3.time.DateFormatUtils, org.apache.sling.api.SlingHttpServletRequest, org.apache.sling.api.resource.ResourceResolver, org.apache.sling.api.scripting.SlingScriptHelper" %>
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
     * Component data field class, used to specify fields for the component
     *
     */
    @Deprecated
    public static class ComponentField {
        private String defaultValue = DEFAULT_VALUE_STRING_NOT_FOUND;
        private String name = null;
        private String dataName = null;

        public ComponentField(String name) {
            this.name = name;
        }

        public ComponentField(String name, String defaultValue) {
            this.name = name;
            this.defaultValue = defaultValue;
        }

        public ComponentField(String name, String defaultValue, String dataName) {
            this.name = name;
            this.defaultValue = defaultValue;
            this.dataName = dataName;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDataName() {
            if (this.dataName == null) {
                return this.name;
            }
            return dataName;
        }

        public void setDataName(String dataName) {
            this.dataName = dataName;
        }

        @Override
        public String toString() {
            return "ComponentField{" +
                    "defaultValue='" + defaultValue + '\'' +
                    ", name='" + name + '\'' +
                    ", dataName='" + dataName + '\'' +
                    '}';
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
    @Deprecated
    public String getComponentProperty(PageContext pageContext, String name, String defaultValue) {
        if (pageContext == null) {
            return "";
        }
        return getComponentProperty(pageContext, name, defaultValue, false);
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
    @Deprecated
    public String getComponentProperty(PageContext pageContext, String name, String defaultValue, Boolean useStyle) {
        //quick fail
        if (pageContext == null) {
            return "";
        }

        ValueMap properties = (ValueMap) pageContext.getAttribute("properties");

        if (useStyle) {
            Style currentStyle = (Style) pageContext.getAttribute("currentStyle");

            return properties.get(name, currentStyle.get(name, defaultValue));
        } else {

            return properties.get(name, defaultValue);
        }
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
            return "";
        }

        ValueMap properties = (ValueMap) pageContext.getAttribute("properties");

        if (useStyle) {
            Style currentStyle = (Style) pageContext.getAttribute("currentStyle");

            Object currentStyleValue = currentStyle.get(name, defaultValue);

            return properties.get(name, currentStyleValue);
        } else {

            return properties.get(name, defaultValue);
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
            return "";
        }

        ValueMap properties = componentResource.adaptTo(ValueMap.class);

        if (useStyle) {

            return properties.get(name, pageStyle.get(name, defaultValue));
        } else {

            return properties.get(name, defaultValue);
        }
    }

    /**
     * Return default place holder for the component, use classic UI placeholder is needed
     * @param slingRequest Current Sling reqsuest
     * @param component Current component
     * @return
     */
    public String getDefaultPlaceholder(SlingHttpServletRequest slingRequest, Component component) {
        return Placeholder.getDefaultPlaceholder(slingRequest, component, DEFAULT_CLASSIC_PLACEHOLDER);
    }

    /**
     * returns component values with defaults
     * @param pageContext current page context
     * @param fieldDefaults list of component fields
     * @return
     */

    public ComponentProperties getComponentProperties(PageContext pageContext, Object[][] fieldDefaults) {
        ComponentProperties componentProperties = new ComponentProperties();

        if (fieldDefaults == null) {
            return componentProperties;
        }

        StringBuilder dataAttributes = new StringBuilder("");

        for (int i = 0; i < fieldDefaults.length; i++) {
            Object[] field = fieldDefaults[i];
            if (field.length < 1) {
                throw new IllegalArgumentException(MessageFormat.format("Key, Value, ..., Value-n expected, instead got {0} fields.",field.length));
            }
            String fieldName = field[0].toString();
            Object fieldDefaultValue = field[1];
            Object fieldValue = getComponentProperty(pageContext, fieldName, fieldDefaultValue, true);
            //Empty array with empty string will set the default value
            if (fieldValue instanceof String && StringUtils.isEmpty(fieldValue.toString())) {
                fieldValue = fieldDefaultValue;
            }else if (fieldValue instanceof String [] && fieldValue != null && (StringUtils.isEmpty(StringUtils.join((String[]) fieldValue, "")))) {
                fieldValue = fieldDefaultValue;
            }

            if (field.length > 2) {
                //if (fieldValue != fieldDefaultValue) {
                    String fieldValueString;
                    if (fieldValue.getClass().isArray()) {
                        fieldValueString = StringUtils.join((String[])fieldValue,",");
                    } else {
                        fieldValueString = fieldValue.toString();
                    }

                    String fieldDataName = field[2].toString();
                    //using single quote to avoid the conflict which within JSON object
                    dataAttributes.append(MessageFormat.format(" data-{0}=\''{1}\''",fieldDataName,fieldValueString));
                //}
            }

            try {
                componentProperties.put(fieldName, fieldValue);
            } catch (Exception ex) {
                LOG.error("error adding value. " + ex);
            }
        }

        componentProperties.put("dataAttributes",dataAttributes.toString());

        return componentProperties;
    }


    /**
     * returns component values with defaults
     * @param page current page
     * @param fieldDefaults list of component fields
     * @return
     */
    public ComponentProperties getComponentProperties(Page page, String componentPath, Object[][] fieldDefaults) {
        ComponentProperties componentProperties = new ComponentProperties();

        Resource resource = page.adaptTo(Resource.class);

        Resource componentResource = page.getContentResource(componentPath);

        if (componentResource == null) {
            return componentProperties;
        }

        Designer designer = resource.getResourceResolver().adaptTo(Designer.class);

        Style currentStyle = designer.getStyle(resource);

        for (int i = 0; i < fieldDefaults.length; i++) {
            Object[] field = fieldDefaults[i];
            if (field.length != 2) {
                throw new IllegalArgumentException("Key Value pair expected");
            }
            String fieldName = field[0].toString();
            Object fieldDefaultValue = field[1];

            ValueMap properties = componentResource.adaptTo(ValueMap.class);

            Object fieldValue = StringUtils.EMPTY;

            if (currentStyle != null) {
                fieldValue = properties.get(fieldName, currentStyle.get(fieldName, fieldDefaultValue));
            } else {
                fieldValue = properties.get(fieldName, fieldDefaultValue);
            }

            try {
                componentProperties.put(fieldName, fieldValue);
            } catch (Exception ex) {
                LOG.error("error adding value. " + ex);
            }
        }

        return componentProperties;
    }

    /**
     *
     * @param pageContext current page context
     * @param fields list of component fields
     * @return
     */
    @Deprecated
    public Map<String, Object> getComponentProperties(PageContext pageContext, ComponentField[] fields) {
        Map<String, Object> properties = new HashMap<String, Object>();

        for (ComponentField field : fields) {
            properties.put(field.name,
                    getComponentProperty(pageContext, field.getDataName(), field.getDefaultValue())
            );
        }

        return properties;
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
        return DateFormatUtils.format(cal, "EEE, dd MMM yyyy HH:mm:ss");
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


    /**
     * load default style properties form the component
     * @param pageContext
     * @return
     */

    public ComponentProperties getComponentStyleProperties(PageContext pageContext) {

        Object[][] styleFields = {
                {"componentId", ""},
                {"componentTheme", new String[]{}},
                {"componentModifiers", new String[]{}},
                {"componentModule", new String[]{}},
                {"componentChevron", new String[]{}},
                {"componentIcon", new String[]{}},
                {"positionX", ""},
                {"positionY", ""},

        };

        return getComponentProperties(pageContext,styleFields);

    }

    public String compileComponentAttributesAsAdmin(ComponentProperties componentProperties, Component component, SlingScriptHelper sling) {
        String componentAttributes="";

        if (component == null || sling == null) {
            return componentAttributes;
        }

        ResourceResolver adminResourceResolver  = openAdminResourceResolver(sling);
        try {
            TagManager _adminTagManager = adminResourceResolver.adaptTo(TagManager.class);

            componentAttributes = compileComponentAttributes(_adminTagManager, componentProperties, component);

        } catch (Exception ex) {
            LOG.error("compileComponentAttributesAsAdmin: " + ex.getMessage(), ex);
            //out.write( Throwables.getStackTraceAsString(ex) );
        } finally {
            closeAdminResourceResolver(adminResourceResolver);
        }

        return componentAttributes;
    }

    /**
     * compile component tag attributes based on default styling structure
     * @param tagManager
     * @param componentProperties
     * @param component
     * @return
     */

    public String compileComponentAttributes(TagManager tagManager, ComponentProperties componentProperties, Component component) {
        String valueId = componentProperties.get("componentId","");
        String valueTheme = getTagsAsValues(tagManager," ",componentProperties.get("componentTheme",new String[]{}));
        String valueModifiers = getTagsAsValues(tagManager," ",componentProperties.get("componentModifiers",new String[]{}));
        String valueModule = getTagsAsValues(tagManager," ",componentProperties.get("componentModule",new String[]{}));
        String valueChevron = getTagsAsValues(tagManager," ",componentProperties.get("componentChevron",new String[]{}));
        String valueIcon = getTagsAsValues(tagManager," ",componentProperties.get("componentIcon",new String[]{}));
        String valueAriaLabel = componentProperties.get("ariaLabel","");
        String valueAriaRole = componentProperties.get("ariaRole","");

        String attrTheme = MessageFormat.format("class=\"{0}\"", addClasses(component.getName().trim(),valueTheme,valueModifiers,valueChevron,valueIcon));

        String attrId="";
        if (isNotEmpty(valueId)) {
            attrId = MessageFormat.format("id=\"{0}\"", valueId);
        }

        String attrModule="";
        if (isNotEmpty(valueModule)) {
            attrModule = MessageFormat.format("data-modules=\"{0}\"", valueModule);
        }

        String attrAriaLabel="";
        if (isNotEmpty(valueAriaLabel)) {
            attrAriaLabel = MessageFormat.format("aria-label=\"{0}\"", valueAriaLabel);
        }

        String attrAriaRole="";
        if (isNotEmpty(valueAriaRole)) {
            attrAriaRole = MessageFormat.format("role=\"{0}\"", valueAriaRole);
        }

        return addClasses(attrId,attrTheme,attrModule,attrAriaLabel,attrAriaRole);
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