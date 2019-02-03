<%@page session="false" %>
<%@ page import="com.adobe.granite.ui.components.AttrBuilder" %>
<%@ page import="org.apache.sling.xss.XSSAPI" %>
<%@ page import="com.day.cq.dam.api.DamConstants" %>
<%@ page import="com.day.cq.wcm.api.components.Component" %>
<%@ page import="com.day.cq.wcm.api.components.ComponentContext" %>
<%@ page import="com.day.cq.wcm.api.components.ComponentManager" %>
<%@ page import="com.day.cq.wcm.api.designer.Style" %>
<%@ page import="com.day.cq.wcm.foundation.Placeholder" %>
<%@ page import="com.day.cq.wcm.webservicesupport.Configuration" %>
<%@ page import="com.day.cq.wcm.webservicesupport.ConfigurationConstants" %>
<%@ page import="com.day.cq.wcm.webservicesupport.ConfigurationManager" %>
<%@ page import="com.google.common.base.Throwables" %>
<%@ page import="org.apache.commons.io.IOUtils" %>
<%@ page import="org.apache.commons.lang3.ArrayUtils" %>
<%@ page import="org.apache.commons.lang3.RandomStringUtils" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="org.apache.commons.lang3.time.DateFormatUtils" %>
<%@ page import="org.apache.sling.api.SlingHttpServletRequest" %>
<%@ page import="org.apache.sling.api.resource.ResourceResolver" %>
<%@ page import="org.slf4j.Logger" %>
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
<%@ page import="org.apache.commons.lang3.BooleanUtils" %>
<%@ page import="org.apache.jackrabbit.core.fs.FileSystem" %>
<%@ include file="/apps/aemdesign/global/tags.jsp" %>
<%@ include file="/apps/aemdesign/global/theme.jsp" %>
<%@ include file="/apps/aemdesign/global/security.jsp" %>
<%!

    public static final int COUNT_CONTENT_NODE = 1;
    public static final int DEPTH_ROOTNODE = 1;
    public static final int DEPTH_HOMEPAGE = 2;
    public static final String COMPONENTS_RENDER_MOBILE = "aemdesign.component.render.mobile";

    public static final String NODE_PAR = "./article/par";

    public static final String DEFAULT_PATH_TAGS = "/content/cq:tags";

    public static final String NODE_DETAILS = "*-details";

    public static final String FIELD_REFERENCE_PATH = "path";

    public static final String FIELD_VARIANT = "variant";
    public static final String DEFAULT_VARIANT = "default";
    public static final String DEFAULT_VARIANT_HIDDEN = "hidden";
    public static final String DEFAULT_BADGE = "default";
    public static final String DEFAULT_ARIA_ROLE_ATTRIBUTE = "role";

    public static final String FIELD_REDIRECT_TARGET = "redirectTarget";

    public static final String DEFAULT_RSS_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss";

    private static final String DETAILS_MENU_COLOR = "menuColor";
    private static final String DETAILS_MENU_ICONSHOW = "menuIconShow";
    private static final String DETAILS_MENU_ICON = "menuIcon";
    private static final String DETAILS_MENU_ACCESS_KEY = "menuAccesskey";
    private static final String DETAILS_TAB_ICONSHOW = "tabIconShow";
    private static final String DETAILS_TAB_ICON = "tabIcon";
    private static final String DETAILS_TITLE_ICONSHOW = "titleIconShow";
    private static final String DETAILS_TITLE_ICON = "titleIcon";
    private static final String DETAILS_OVERLAY_ICONSHOW = "badgeOverlayIconShow";
    private static final String DETAILS_OVERLAY_ICON = "badgeOverlayIcon";
    private static final String DETAILS_CARD_STYLE = "cardStyle";
    private static final String DETAILS_CARD_SIZE = "cardSize";
    private static final String DETAILS_CARD_ICONSHOW = "cardIconShow";
    private static final String DETAILS_CARD_ICON = "cardIcon";

    //shared badge config passed from list to all badge elements
    private static final String DETAILS_LINK_TARGET = "badgeLinkTarget";
    private static final String DETAILS_LINK_TEXT = "badgeLinkText";
    private static final String DETAILS_LINK_TITLE = "badgeLinkTitle";
    private static final String DETAILS_LINK_STYLE = "badgeLinkStyle";
    private static final String DETAILS_TITLE_TRIM = "badgeTitleTrim";
    private static final String DETAILS_TITLE_TRIM_LENGTH_MAX = "badgeTitleTrimLengthMax";
    private static final String DETAILS_TITLE_TRIM_LENGTH_MAX_SUFFIX = "badgeTitleTrimLengthMaxSuffix";
    private static final String DETAILS_SUMMARY_TRIM = "badgeSummaryTrim";
    private static final String DETAILS_SUMMARY_TRIM_LENGTH_MAX = "badgeSummaryLengthMax";
    private static final String DETAILS_SUMMARY_TRIM_LENGTH_MAX_SUFFIX = "badgeSummaryLengthMaxSuffix";
    private static final String DETAILS_THUMBNAIL_WIDTH = "badgeThumbnailWidth";
    private static final String DETAILS_THUMBNAIL_HEIGHT = "badgeThumbnailHeight";
    private static final String DETAILS_THUMBNAIL_TYPE = "badgeThumbnailType";
    private static final String DETAILS_TITLE_TAG_TYPE = "badgeTitleType";
    private static final String DETAILS_THUMBNAIL_ID = "badgeThumbnailId";
    private static final String DETAILS_THUMBNAIL_LICENSE_INFO = "badgeThumbnailLicenseInfo";
    private static final String DETAILS_THUMBNAIL = "badgeThumbnail";
    //badge analytics
    private static final String DETAILS_BADGE_ANALYTICS_EVENT_TYPE = "badgeAnalyticsEventType";
    private static final String DETAILS_BADGE_ANALYTICS_LINK_TYPE = "badgeAnalyticsLinkType";
    private static final String DETAILS_BADGE_ANALYTICS_LINK_LOCATION = "badgeAnalyticsLinkLocation";
    private static final String DETAILS_BADGE_ANALYTICS_LINK_DESCRIPTION = "badgeAnalyticsLinkDescription";

    //page metadata pageMetaProperty
    private static final String DETAILS_PAGE_METADATA_PROPERTY = "pageMetaProperty";
    private static final String DETAILS_PAGE_METADATA_PROPERTY_CONTENT = "pageMetaPropertyContent";

    //analytics
    private static final String DETAILS_ANALYTICS_EVENT_TYPE = "analyticsEventType";
    private static final String DETAILS_ANALYTICS_LINK_TYPE = "analyticsLinkType";
    private static final String DETAILS_ANALYTICS_LINK_LOCATION = "analyticsLinkLocation";
    private static final String DETAILS_ANALYTICS_LINK_DESCRIPTION = "analyticsLinkDescription";


    private static final String FIELD_HIDEINMENU = "hideInMenu";


    private static final Pattern DEFAULT_RENDTION_PATTERN_OOTB = Pattern.compile("cq5dam\\.(.*)?\\.(\\d+)\\.(\\d+)\\.(.*)");
    private static final String DEFAULT_ASSET_RENDITION_PREFIX1 = "cq5dam.thumbnail.";
    private static final String DEFAULT_ASSET_RENDITION_PREFIX2 = "cq5dam.web.";

    private static final String DEFAULT_IMAGE_GENERATED_FORMAT = "{0}.img.png/{1}.png";

    private static final String DEFAULT_IMAGE_RESOURCETYPE = "aemdesign/components/media/image";
    private static final String DEFAULT_IMAGE_RESOURCETYPE_SUFFIX = "/components/media/image";

    private static final String COMPONENT_ATTRIBUTES = "componentAttributes";
    private static final String COMPONENT_INSTANCE_NAME = "instanceName";
    private static final String COMPONENT_TARGET_RESOURCE = "targetResource";
    private static final String COMPONENT_ATTRIBUTE_CLASS = "class";
    private static final String COMPONENT_INPAGEPATH = "componentInPagePath";
    private static final String COMPONENT_ATTRIBUTE_INPAGEPATH = "data-layer-componentpath";
    private static final String COMPONENT_BACKGROUND_ASSETS = "componentBackgroundAssets";


    private static final String COMPONENT_CANCEL_INHERIT_PARENT = "cancelInheritParent";



    private static final String PAR_PAGEDETAILS = "par/page-details";
    private static final String ARTICLE_PAR_PAGEDETAILS = "article/par/page-details";
    private static final String ARTICLE_CONTENTS = "article/par";

    private static final String FIELD_STYLE_COMPONENT_ID = "componentId";
    private static final String FIELD_STYLE_COMPONENT_THEME = "componentTheme";
    private static final String FIELD_STYLE_COMPONENT_MODIFIERS = "componentModifiers";
    private static final String FIELD_STYLE_COMPONENT_MODULES = "componentModules";
    private static final String FIELD_STYLE_COMPONENT_CHEVRON = "componentChevron";
    private static final String FIELD_STYLE_COMPONENT_ICON = "componentIcon";
    private static final String FIELD_STYLE_COMPONENT_BOOLEANATTR = "componentBooleanAttrs";
    private static final String FIELD_STYLE_COMPONENT_POSITIONX = "componentPositionX";
    private static final String FIELD_STYLE_COMPONENT_POSITIONY = "componentPositionY";
    private static final String FIELD_STYLE_COMPONENT_WIDTH = "componentWidth";
    private static final String FIELD_STYLE_COMPONENT_HEIGHT = "componentHeight";
    private static final String FIELD_STYLE_COMPONENT_SITETHEMECATEGORY = "siteThemeCategory";
    private static final String FIELD_STYLE_COMPONENT_SITETHEMECOLOR = "siteThemeColor";
    private static final String FIELD_STYLE_COMPONENT_SITETITLECOLOR = "siteTileColor";
    private static final String FIELD_STYLE_COMPONENT_ANIMATION_ENABLED = "animationEnabled";
    private static final String FIELD_STYLE_COMPONENT_ANIMATION_NAME = "animationName";
    private static final String FIELD_STYLE_COMPONENT_ANIMATION_ONCE = "animationOnce";
    private static final String FIELD_STYLE_COMPONENT_ANIMATION_EASING = "animationEasing";
    private static final String FIELD_STYLE_COMPONENT_ANIMATION_DELAY = "animationDelay";
    private static final String FIELD_STYLE_COMPONENT_ANIMATION_DURATION = "animationDuration";

    private static final String FIELD_ARIA_ROLE = "ariaRole";
    private static final String FIELD_ARIA_LABEL = "ariaLabel";
    private static final String FIELD_ARIA_DESCRIBEDBY = "ariaDescribedBy";
    private static final String FIELD_ARIA_LABELLEDBY = "ariaLabelledBy";
    private static final String FIELD_ARIA_CONTROLS = "ariaControls";
    private static final String FIELD_ARIA_LIVE = "ariaLive";
    private static final String FIELD_ARIA_HIDDEN = "ariaHidden";
    private static final String FIELD_ARIA_HASPOPUP = "ariaHaspopup";
    private static final String FIELD_ARIA_ACCESSKEY = "ariaAccessKey";

    private static final String FIELD_ARIA_DATA_ATTRIBUTE_ROLE = "role";

    private static final String FIELD_DATA_ANALYTICS_TYPE = "data-analytics-type";
    private static final String FIELD_DATA_ANALYTICS_HIT_TYPE = "data-analytics-hit-type";
    private static final String FIELD_DATA_ANALYTICS_METATYPE = "data-analytics-metatype";
    private static final String FIELD_DATA_ANALYTICS_FILENAME = "data-analytics-filename";
    private static final String FIELD_DATA_ANALYTICS_EVENT_CATEGORY = "data-analytics-event-category";
    private static final String FIELD_DATA_ANALYTICS_EVENT_ACTION = "data-analytics-event-action";
    private static final String FIELD_DATA_ANALYTICS_EVENT_LABEL = "data-analytics-event-label";
    private static final String FIELD_DATA_ANALYTICS_TRANSPORT = "data-analytics-transport";
    private static final String FIELD_DATA_ANALYTICS_NONINTERACTIVE = "data-analytics-noninteraction";

    private static final String FIELD_HREF = "href";
    private static final String FIELD_TITLE_TAG_TYPE = "titleType";

    private static final String FIELD_BADGE_TITLE_TAG_TYPE = "badgeTitleType";
    private static final String FIELD_BADGE_PAGE = "badgePage";

    private static final String JCR_NAME_SEPARATOR = "_";

    private static final String FIELD_LICENSE_INFO = "licenseInfo";
    private static final String FIELD_ASSETID = "asset-id";
    private static final String FIELD_ASSET_LICENSED = "asset-licensed";
    private static final String FIELD_ASSET_TRACKABLE = "asset-trackable";

    private static final String DETAILS_TITLE = "title";
    private static final String DETAILS_DESCRIPTION = "description";

    private static final String DETAILS_COLUMNS_LAYOUT_CLASS_SMALL = "layoutColumnClassSmall";
    private static final String DETAILS_COLUMNS_LAYOUT_CLASS_MEDIUM = "layoutColumnClassMedium";
    private static final String DETAILS_COLUMNS_LAYOUT_CLASS_LARGE = "layoutColumnClassLarge";
    private static final String DETAILS_COLUMNS_LAYOUT_CLASS_XLARGE = "layoutColumnClassExtraLarge";
    private static final String DETAILS_COLUMNS_LAYOUT_ROW_CLASS = "layoutRowClass";


    //COMPONENT STYLES
    // {
    //   1 required - property name,
    //   2 required - default value,
    //   3 optional - name of component attribute to add value into
    //   4 optional - canonical name of class for handling multivalues, String or Tag
    // }
    public static final Object[][] DEFAULT_FIELDS_STYLE = {
            {FIELD_STYLE_COMPONENT_ID, "","id"},
            {FIELD_STYLE_COMPONENT_THEME, new String[]{},"class", Tag.class.getCanonicalName()},
            {FIELD_STYLE_COMPONENT_MODIFIERS, new String[]{},"class", Tag.class.getCanonicalName()},
            {FIELD_STYLE_COMPONENT_MODULES, new String[]{},"data-modules", Tag.class.getCanonicalName()},
            {FIELD_STYLE_COMPONENT_CHEVRON, new String[]{},"class", Tag.class.getCanonicalName()},
            {FIELD_STYLE_COMPONENT_ICON, new String[]{},"class", Tag.class.getCanonicalName()},
            {FIELD_STYLE_COMPONENT_POSITIONX, "", "x"},
            {FIELD_STYLE_COMPONENT_POSITIONY, "", "y"},
            {FIELD_STYLE_COMPONENT_WIDTH, "", "width"},
            {FIELD_STYLE_COMPONENT_HEIGHT, "", "height"},
            {FIELD_STYLE_COMPONENT_SITETHEMECATEGORY, ""},
            {FIELD_STYLE_COMPONENT_SITETHEMECOLOR, ""},
            {FIELD_STYLE_COMPONENT_SITETITLECOLOR, ""},
            {FIELD_STYLE_COMPONENT_BOOLEANATTR, new String[]{}," ", Tag.class.getCanonicalName()}, //do not store content in data attributes
            {FIELD_STYLE_COMPONENT_ANIMATION_ENABLED, false},
            {FIELD_STYLE_COMPONENT_ANIMATION_NAME, StringUtils.EMPTY, "data-aos"},
            {FIELD_STYLE_COMPONENT_ANIMATION_ONCE, StringUtils.EMPTY, "data-aos-once"},
            {FIELD_STYLE_COMPONENT_ANIMATION_EASING, StringUtils.EMPTY, "data-aos-easing"},
            {FIELD_STYLE_COMPONENT_ANIMATION_DELAY, StringUtils.EMPTY, "data-aos-delay"},
            {FIELD_STYLE_COMPONENT_ANIMATION_DURATION, StringUtils.EMPTY, "data-aos-duration"},
    };

    //COMPONENT ACCESSIBILITY
    // {
    //   1 required - property name,
    //   2 required - default value,
    //   3 optional - name of component attribute to add value into
    //   4 optional - canonical name of class for handling multivalues, String or Tag
    // }
    public static final Object[][] DEFAULT_FIELDS_ACCESSIBILITY = {
            {FIELD_ARIA_ROLE, "", DEFAULT_ARIA_ROLE_ATTRIBUTE},
            {FIELD_ARIA_LABEL, "","aria-label"},
            {FIELD_ARIA_DESCRIBEDBY, "","aria-describedby"},
            {FIELD_ARIA_LABELLEDBY, "","aria-labelledby"},
            {FIELD_ARIA_CONTROLS, "","aria-controls"},
            {FIELD_ARIA_LIVE, "","aria-live"},
            {FIELD_ARIA_HIDDEN, "","aria-hidden"},
            {FIELD_ARIA_HASPOPUP, "","aria-haspopup"},
            {FIELD_ARIA_ACCESSKEY, "","accesskey"},
    };

    //DEFAULT NODE METADATA
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
    //   3 optional - name of component attribute to add value into, specifying "" will return values process as per canonical name
    //   4 optional - canonical name of class for handling multivalues, String or Tag
    // }
    public static final Object[][] DEFAULT_FIELDS_DETAILS_OPTIONS = {
            {DETAILS_MENU_COLOR, ""},
            {DETAILS_MENU_ICONSHOW, false},
            {DETAILS_MENU_ICON, new String[]{}, "", Tag.class.getCanonicalName()},
            {DETAILS_MENU_ACCESS_KEY, ""},
            {DETAILS_CARD_STYLE, new String[]{}, "", Tag.class.getCanonicalName()},
            {DETAILS_CARD_SIZE, "small"},
            {DETAILS_CARD_ICONSHOW, false},
            {DETAILS_CARD_ICON, new String[]{}, "", Tag.class.getCanonicalName()},
            {DETAILS_LINK_TARGET, "_blank"},
            {DETAILS_LINK_TEXT, ""}, //getPageNavTitle(_currentPage)
            {DETAILS_LINK_TITLE, ""}, //getPageTitle(_currentPage)
            {DETAILS_LINK_STYLE, new String[]{}, "", Tag.class.getCanonicalName()},
            {DETAILS_TITLE_TRIM, false},
            {DETAILS_TITLE_TRIM_LENGTH_MAX, DEFAULT_SUMMARY_TRIM_LENGTH},
            {DETAILS_TITLE_TRIM_LENGTH_MAX_SUFFIX, DEFAULT_SUMMARY_TRIM_SUFFIX},
            {DETAILS_SUMMARY_TRIM, false},
            {DETAILS_SUMMARY_TRIM_LENGTH_MAX, DEFAULT_SUMMARY_TRIM_LENGTH},
            {DETAILS_SUMMARY_TRIM_LENGTH_MAX_SUFFIX, DEFAULT_SUMMARY_TRIM_SUFFIX},
            {DETAILS_TAB_ICONSHOW, false},
            {DETAILS_TAB_ICON,  new String[]{}, "", Tag.class.getCanonicalName()},
            {DETAILS_TITLE_ICONSHOW, false},
            {DETAILS_TITLE_ICON,  new String[]{}, "", Tag.class.getCanonicalName()},
            {DETAILS_OVERLAY_ICONSHOW, false},
            {DETAILS_OVERLAY_ICON, new String[]{}, "", Tag.class.getCanonicalName()},
            {DETAILS_THUMBNAIL_WIDTH, DEFAULT_THUMB_WIDTH_SM},
            {DETAILS_THUMBNAIL_HEIGHT, ""},
            {DETAILS_THUMBNAIL_TYPE, IMAGE_OPTION_RENDITION},
            {DETAILS_TITLE_TAG_TYPE, DEFAULT_TITLE_TAG_TYPE_BADGE},
            {DETAILS_THUMBNAIL_ID, ""},
            {DETAILS_THUMBNAIL_LICENSE_INFO, ""},
            {DETAILS_THUMBNAIL, ""},
            {DETAILS_BADGE_ANALYTICS_EVENT_TYPE, StringUtils.EMPTY}, //basic
            {DETAILS_BADGE_ANALYTICS_LINK_TYPE, StringUtils.EMPTY}, //basic
            {DETAILS_BADGE_ANALYTICS_LINK_LOCATION, StringUtils.EMPTY}, //basic
            {DETAILS_BADGE_ANALYTICS_LINK_DESCRIPTION, StringUtils.EMPTY}, //basic
            {DETAILS_BADGE_ANALYTICS_LINK_DESCRIPTION, StringUtils.EMPTY}, //basic
            {DETAILS_PAGE_METADATA_PROPERTY, new String[]{}},
            {DETAILS_PAGE_METADATA_PROPERTY_CONTENT, new String[]{}},

    };

    // {
    //   1 required - property name,
    //   2 required - default value,
    //   3 optional - name of component attribute to add value into, specifying "" will return values process as per canonical name
    //   4 optional - canonical name of class for handling multivalues, String or Tag
    // }
    public static final Object[][] DEFAULT_FIELDS_DETAILS_OPTIONS_OVERRIDE = {
            {DETAILS_MENU_COLOR, ""},
            {DETAILS_MENU_ICONSHOW, ""},
            {DETAILS_MENU_ICON, new String[]{}, "", Tag.class.getCanonicalName()},
            {DETAILS_MENU_ACCESS_KEY, ""},
            {DETAILS_CARD_STYLE, new String[]{}, "", Tag.class.getCanonicalName()},
            {DETAILS_CARD_SIZE, ""},
            {DETAILS_CARD_ICONSHOW, ""},
            {DETAILS_CARD_ICON, new String[]{}, "", Tag.class.getCanonicalName()},
            {DETAILS_LINK_TARGET, ""},
            {DETAILS_LINK_TEXT, ""}, //getPageNavTitle(_currentPage)
            {DETAILS_LINK_TITLE, ""}, //getPageTitle(_currentPage)
            {DETAILS_LINK_STYLE, new String[]{}, "", Tag.class.getCanonicalName()},
            {DETAILS_TITLE_TRIM, ""},
            {DETAILS_TITLE_TRIM_LENGTH_MAX, ""},
            {DETAILS_TITLE_TRIM_LENGTH_MAX_SUFFIX, ""},
            {DETAILS_SUMMARY_TRIM, ""},
            {DETAILS_SUMMARY_TRIM_LENGTH_MAX, ""},
            {DETAILS_SUMMARY_TRIM_LENGTH_MAX_SUFFIX, ""},
            {DETAILS_TAB_ICONSHOW, ""},
            {DETAILS_TAB_ICON,  new String[]{}, "", Tag.class.getCanonicalName()},
            {DETAILS_TITLE_ICONSHOW, ""},
            {DETAILS_TITLE_ICON,  new String[]{}, "", Tag.class.getCanonicalName()},
            {DETAILS_OVERLAY_ICONSHOW, ""},
            {DETAILS_OVERLAY_ICON, new String[]{}, "", Tag.class.getCanonicalName()},
            {DETAILS_THUMBNAIL_WIDTH, ""},
            {DETAILS_THUMBNAIL_HEIGHT, ""},
            {DETAILS_THUMBNAIL_TYPE, ""},
            {DETAILS_TITLE_TAG_TYPE, ""},
            {DETAILS_THUMBNAIL_ID, ""},
            {DETAILS_THUMBNAIL_LICENSE_INFO, ""},
            {DETAILS_THUMBNAIL, ""},
            {DETAILS_BADGE_ANALYTICS_EVENT_TYPE, StringUtils.EMPTY}, //basic
            {DETAILS_BADGE_ANALYTICS_LINK_TYPE, StringUtils.EMPTY}, //basic
            {DETAILS_BADGE_ANALYTICS_LINK_LOCATION, StringUtils.EMPTY}, //basic
            {DETAILS_BADGE_ANALYTICS_LINK_DESCRIPTION, StringUtils.EMPTY}, //basic

    };

    //COMPONENT ANALYTICS
    // {
    //   1 required - property name,
    //   2 required - default value,
    //   3 optional - name of component attribute to add value into
    //   4 optional - canonical name of class for handling multivalues, String or Tag
    // }
    public static final Object[][] DEFAULT_FIELDS_ANALYTICS = {
            {DETAILS_ANALYTICS_EVENT_TYPE, StringUtils.EMPTY, "data-layer-event"}, //basic
            {DETAILS_ANALYTICS_LINK_TYPE, StringUtils.EMPTY, "data-layer-linktype"}, //basic
            {DETAILS_ANALYTICS_LINK_LOCATION, StringUtils.EMPTY, "data-layer-linklocation"}, //basic
            {DETAILS_ANALYTICS_LINK_DESCRIPTION, StringUtils.EMPTY, "data-layer-linkdescription"}, //basic
            {"analyticsEventType", StringUtils.EMPTY, "data-analytics-event"}, //advanced
            {"analyticsHitType", StringUtils.EMPTY, "data-analytics-hit-type"}, //advanced
            {"analyticsEventCategory", StringUtils.EMPTY, "data-analytics-event-category"}, //advanced
            {"analyticsEventAction", StringUtils.EMPTY, "data-analytics-event-action"}, //advanced
            {"analyticsEventLabel", StringUtils.EMPTY, "data-analytics-event-label"}, //advanced
            {"analyticsTransport", StringUtils.EMPTY, "data-analytics-transport"}, //advanced
            {"analyticsNonInteraction", StringUtils.EMPTY, "data-analytics-noninteraction"}, //advanced
    };

    //LINK ATTRIBUTES
    // {
    //   1 required - property name,
    //   2 required - default value,
    //   3 optional - name of component attribute to add value into
    //   4 optional - canonical name of class for handling multivalues, String or Tag
    // }
    public static final Object[][] DEFAULT_FIELDS_ATTRIBUTES = {
            {"dataType", "", "type"},
            {"dataTarget", "", "data-target"},
            {"dataToggle", "", "data-toggle"},
    };

    //ASSET IMAGE METADATA
    // {
    //   1 required - property name,
    //   2 required - default value,
    //   3 optional - name of component attribute to add value into
    //   4 optional - canonical name of class for handling multivalues, String or Tag
    // }
    public static final Object[][] DEFAULT_FIELDS_ASSET_IMAGE = {
            {DAM_TITLE, StringUtils.EMPTY},
            {DAM_DESCRIPTION, StringUtils.EMPTY},
            {DAM_CREDIT, StringUtils.EMPTY},
            {DAM_HEADLINE, StringUtils.EMPTY},
            {DAM_SOURCE, StringUtils.EMPTY},
            {DAM_SOURCE_URL, StringUtils.EMPTY},
    };

    //ASSET VIDEO METADATA
    // {
    //   1 required - property name,
    //   2 required - default value,
    //   3 optional - name of component attribute to add value into
    //   4 optional - canonical name of class for handling multivalues, String or Tag
    // }
    public static final Object[][] DEFAULT_FIELDS_ASSET_VIDEO = {
            {DAM_TITLE, StringUtils.EMPTY},
            {DAM_DESCRIPTION, StringUtils.EMPTY},
            {DAM_HEADLINE, StringUtils.EMPTY},
            {DAM_CREDIT, StringUtils.EMPTY},
            {DAM_SOURCE, StringUtils.EMPTY},
            {DAM_SOURCE_URL, StringUtils.EMPTY},
            {DAM_VIDEO_URL, StringUtils.EMPTY},
    };


    //DEFAULT NODE METADATA
    // {
    //   1 required - property name,
    //   2 required - default value,
    //   3 optional - name of component attribute to add value into
    //   4 optional - canonical name of class for handling multivalues, String or Tag
    // }
    public static final Object[][] DEFAULT_FIELDS_PAGE_THEME = {
            {"themeStyle", ""},
            {"faviconsPath", ""},
            {"favicon", ""},
            {"siteThemeColor", ""},
            {"siteTileColor", ""},
    };

    /** Local logging container. */
    private final Logger LOG = LoggerFactory.getLogger(getClass());

//    /**
//     *
//     * New ComponentField
//     *
//     */
//    public static class ComponentProperties extends ValueMapDecorator {
//
//        /**
//         * Creates a new wrapper around a given map.
//         *
//         * @param base wrapped object
//         */
//        public ComponentProperties(Map<String, Object> base) {
//            super(base);
//        }
//
//        public Object get(String name) {
//            return super.get(name);
//        }
//
//        /**
//         * Created empty map
//         */
//        public ComponentProperties() {
//            super(new HashMap());
//        }
//
//        public AttrBuilder attr;
//
//        /***
//         * put map into existing map
//         * @param map new entries
//         * @param update update entries, skip if non blank value exist
//         */
//        public void putAll(Map<? extends String, ?> map, Boolean update) {
//            if (update == null || !update) {
//                //on null or not update do putAll
//                super.putAll(map);
//                return;
//            }
//            for (Map.Entry<? extends String, ?> entry : map.entrySet())
//            {
//                if (update) {
//                    if (super.containsKey(entry.getKey())) {
//                        //System.out.println("update: " + entry.getKey() + ", [" + entry.getValue().toString() + "],[" + super.get(entry.getKey(), "") + "]");
//                        //skip if non blank value exist
//                        if (isNotBlank(super.get(entry.getKey(), ""))) {
//                            continue;
//                        }
//                    }
//                }
//                super.put(entry.getKey(), entry.getValue());
//            }
//        }
//    }


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
        try {
            Resource componentResource = componentPage.getContentResource(componentPath);

            return getComponentProperties(pageContext, componentResource, fieldLists);

        } catch (Exception ex) {
            getLogger().error("getComponentProperties: " + componentPath + ", error: " + ex.toString());
        }


        return getNewComponentProperties(pageContext);
    }

    //TODO: move to class
    /***
     * helper to create new Component Properties
     * @param pageContext
     * @return
     */
    public ComponentProperties getNewComponentProperties(PageContext pageContext) {
        ComponentProperties componentProperties = new ComponentProperties();
        componentProperties.attr = new AttrBuilder(null, null);
        try {
            SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) pageContext.getAttribute("slingRequest");
            HttpServletRequest request = (HttpServletRequest) slingRequest;
            com.adobe.granite.xss.XSSAPI oldXssAPI = slingRequest.adaptTo(com.adobe.granite.xss.XSSAPI.class);
            componentProperties.attr = new AttrBuilder(request, oldXssAPI);

        } catch (Exception ex) {
            getLogger().error("getNewComponentProperties: could not configure componentProperties with attributeBuilder");
        }
        return componentProperties;
    }


    /**
     * returns component values with defaults from pageContent Properties
     * @param pageContext current page context
     * @param fieldLists list of fields definition Object{{name, defaultValue, attributeName, valueTypeClass},...}
     * @return
     */
    public ComponentProperties getComponentProperties(PageContext pageContext, Object[][]... fieldLists) {
        return getComponentProperties(pageContext, null, true, fieldLists);
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
        return getComponentProperties(pageContext, targetResource, true, fieldLists);
    }

    /**
     * returns component values with defaults from a targetResource, default to pageContext properties
     * @param pageContext current page context
     * @param targetResource resource to use as source
     * @param includeComponentAttributes include additional attibutes associated with component
     * @param fieldLists list of fields definition Object{{name, defaultValue, attributeName, valueTypeClass},...}
     * @return
     */
    @SuppressWarnings("unchecked")
    public ComponentProperties getComponentProperties(PageContext pageContext, Object targetResource, Boolean includeComponentAttributes, Object[][]... fieldLists) {
        ComponentProperties componentProperties = new ComponentProperties();

        boolean addMoreAttributes = includeComponentAttributes;

        SlingHttpServletRequest slingRequest = (SlingHttpServletRequest)pageContext.getAttribute("slingRequest");
        ResourceResolver resourceResolver = (ResourceResolver)pageContext.getAttribute("resourceResolver");

        SlingScriptHelper sling = (SlingScriptHelper)pageContext.getAttribute("sling");
        HttpServletRequest request = (HttpServletRequest)slingRequest;

        com.adobe.granite.xss.XSSAPI oldXssAPI = slingRequest.adaptTo(com.adobe.granite.xss.XSSAPI.class);

        ComponentContext componentContext = (ComponentContext) pageContext.getAttribute("componentContext");
        Component component = componentContext.getComponent();

        componentProperties.attr = new AttrBuilder(request, oldXssAPI);
        if (addMoreAttributes) {
            componentProperties.attr.addBoolean("component", true);
        }
//        AttrBuilder itemAttr = new AttrBuilder(request, oldXssAPI);

        final String CLASS_TYPE_RESOURCE = Resource.class.getCanonicalName();
        final String CLASS_TYPE_JCRNODERESOURCE = "org.apache.sling.jcr.resource.internal.helper.jcr.JcrNodeResource";
        final String CLASS_TYPE_ASSET = "com.adobe.granite.asset.core.impl.AssetImpl";

        ResourceResolver adminResourceResolver  = openAdminResourceResolver(sling);
        try {

            Node currentNode = (javax.jcr.Node)  pageContext.getAttribute("currentNode");

            TagManager tagManager = adminResourceResolver.adaptTo(TagManager.class);

            // if targetResource == null get defaults
            ValueMap properties = (ValueMap) pageContext.getAttribute("properties");
            Style currentStyle = (Style) pageContext.getAttribute("currentStyle");

            //if targetResource != null get the appropriate objects
            if (targetResource != null && targetResource.getClass().getCanonicalName().equals(CLASS_TYPE_ASSET)) {

                try {
                    com.adobe.granite.asset.api.Asset asset = (com.adobe.granite.asset.api.Asset) targetResource;

                    Resource resource = asset.getResourceResolver().getResource(asset, JcrConstants.JCR_CONTENT);

                    if (resource != null) {
                        resource = resource.getChild(DamConstants.METADATA_FOLDER);
                    }
                    properties = resource.adaptTo(ValueMap.class);
                    addMoreAttributes=false;

                } catch(Exception ex) {
                    getLogger().error("getComponentProperties: could not evaluate target asset",ex);
                    return componentProperties;
                }
            } else if (targetResource != null &&
                        (targetResource.getClass().getCanonicalName().equals(CLASS_TYPE_RESOURCE) ||
                        targetResource.getClass().getCanonicalName().equals(CLASS_TYPE_JCRNODERESOURCE)) ) {

                try {
                    Resource resource = (Resource) targetResource;

                    properties = resource.adaptTo(ValueMap.class);

                    Designer designer = resource.getResourceResolver().adaptTo(Designer.class);

                    currentStyle = designer.getStyle(resource);

                    ComponentManager componentManager = resource.getResourceResolver().adaptTo(ComponentManager.class);
                    Component resourceComponent = componentManager.getComponentOfResource(resource);
                    //set component to match target resource
                    if (resourceComponent != null) {
                        component = resourceComponent;
//                        componentProperties.put("resourceComponentCell", resourceComponent.getCellName());
//                        componentProperties.put("resourceComponentPath", resourceComponent.getPath());
//                        componentProperties.put("resourceComponentName", resourceComponent.getName());
//                        componentProperties.put("resourceComponentResourceType", resourceComponent.getResourceType());
                    }

                    //set currentnode to match target resource
                    Node resourceNode = (javax.jcr.Node)  resource.adaptTo(Node.class);
                    if (resourceNode != null) {
                        currentNode = resourceNode;
                    }

                    componentProperties.put(COMPONENT_TARGET_RESOURCE, resource.getPath());

                    //getComponentProperty(ValueMap componentProperties, Style pageStyle, String name, Object defaultValue, Boolean useStyle)
                    //fieldValue = getComponentProperty(resourceProperties, resourceStyle, fieldName, fieldDefaultValue, true);
                } catch (Exception ex) {
                    getLogger().error("getComponentProperties: could not evaluate target resource",ex);
                    return componentProperties;
                }
            } else if (targetResource != null) {
                getLogger().warn("getComponentProperties: processing is unsupported of target resource of type: " + targetResource.getClass().getCanonicalName());
            } else if (targetResource == null) {
                getLogger().warn("getComponentProperties: processing of NULL target resource of type return design defaults");
            }

            if (currentNode != null && addMoreAttributes) {
                componentProperties.put(COMPONENT_INSTANCE_NAME, currentNode.getName());
                //get/generate component id
                String componentId = getComponentId(currentNode);
                String componentInPagePath = getComponentInPagePath(currentNode);
                componentProperties.put(COMPONENT_INPAGEPATH, componentInPagePath);
                componentProperties.attr.add(COMPONENT_ATTRIBUTE_INPAGEPATH, componentInPagePath);
            }

            if (component != null && addMoreAttributes) {
                componentProperties.attr.add(COMPONENT_ATTRIBUTE_CLASS, component.getName().trim());
            }

            if (fieldLists !=null) {
                for (Object[][] fieldDefaults : fieldLists) {
                    for (Object[] field : fieldDefaults) {
                        if (field.length < 1) {
                            throw new IllegalArgumentException(MessageFormat.format("Key, Value, ..., Value-n expected, instead got {0} fields.", field.length));
                        }
                        String fieldName = field[0].toString();

                        if (componentProperties.containsKey(fieldName)) {
                            //skip entries that already exist
                            //first Object in fieldLists will set a field value
                            //we expect the additional Objects to not override
                            getLogger().debug("getComponentProperties: skipping property [{}] its already defined, {}", fieldName, componentContext.getResource().getPath());
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
//                            if (StringUtils.isEmpty(fieldDataName)) {
//                                fieldDataName = "other";
//                            }
                            String fieldValueString = "";
                            String fieldValueType;
                            if (field.length > 3) {
                                fieldValueType = (String) field[3];
                            } else {
                                fieldValueType = String.class.getCanonicalName();
                            }

                            if (fieldValue.getClass().isArray()) {
                                if (ArrayUtils.isNotEmpty((String[]) fieldValue)) {
                                    if (fieldValueType.equals(Tag.class.getCanonicalName())) {
                                        //if data-attribute not specified return values as map entry
                                        if (isEmpty(fieldDataName)) {
                                            fieldValue = getTagsValues(tagManager, resourceResolver, " ", (String[]) fieldValue);
                                        } else {
                                            fieldValueString = getTagsAsValues(tagManager, resourceResolver, " ", (String[]) fieldValue);
                                        }
                                    } else {
                                        fieldValueString = StringUtils.join((String[]) fieldValue, ",");
                                    }
                                } else {
                                    //if data-attribute not specified return empty if values array is empty
                                    if (isEmpty(fieldDataName)) {
                                        fieldValue = "";
                                    }
                                }
                            } else {
                                fieldValueString = fieldValue.toString();
                            }

                            if (isNotEmpty(fieldValueString) && isNotEmpty(fieldDataName) && !fieldDataName.equals(" ")) {
                                componentProperties.attr.add(fieldDataName, fieldValueString);
                            } else if (isNotEmpty(fieldValueString) && fieldDataName.equals(" ")) {
                                componentProperties.attr.addBoolean(fieldValueString,true);
                            }

                        }

                        try {
                            componentProperties.put(fieldName, fieldValue);
                        } catch (Exception ex) {
                            getLogger().error("error adding value. " + ex);
                        }
                    }
                }

                if (!componentProperties.attr.isEmpty() && addMoreAttributes) {
                    //check if component variant exist and add to class chain if not default
                    String variant = componentProperties.get(FIELD_VARIANT,"");
                    if (isNotEmpty(variant) && !variant.equals(DEFAULT_VARIANT)) {
                        componentProperties.attr.add(COMPONENT_ATTRIBUTE_CLASS, variant);
                    }
                    componentProperties.put(COMPONENT_ATTRIBUTES, buildAttributesString(componentProperties.attr.getData(),oldXssAPI));
                }
            }

        } catch (Exception ex) {
            getLogger().error("getComponentProperties: " + ex.getMessage(), ex);
            //out.write( Throwables.getStackTraceAsString(ex) );
        } finally {
            closeAdminResourceResolver(adminResourceResolver);
        }

        return componentProperties;
    }

    /***
     * build attributes from attributes data without encoding
     * @param data map of data attributes and values
     * @param xssAPI old xssi api
     * @return
     * @throws IOException
     */
    public String buildAttributesString(Map<String, String> data, com.adobe.granite.xss.XSSAPI xssAPI) throws IOException {
        return buildAttributesString(data,xssAPI,null);
    }

    /***
     * build attributes from attributes data
     * @param data map of data attributes and values
     * @param xssAPI old xssi api
     * @param encodings map of encoding per data attribute
     * @return
     * @throws IOException
     */
    public String buildAttributesString(Map<String, String> data, com.adobe.granite.xss.XSSAPI xssAPI, Map<String, String> encodings) throws IOException {
        try {
            StringWriter out = new StringWriter();
            String key;
            String value;

            Iterator items = data.entrySet().iterator();
            while (items.hasNext()) {
                Map.Entry<String, String> e = (Map.Entry)items.next();
                key = (String)e.getKey();
                value = (String)e.getValue();

                //encode values if encoding is specified
                if (encodings != null) {
                    String encoding = encodings.get(e.getKey());
                    if (encoding != null && value != null && value.length() > 0) {
                        switch (encoding) {
                            case "HREF":
                                value = xssAPI.getValidHref(value);
                                break;
                            case "HTML_ATTR":
                                value = xssAPI.encodeForHTMLAttr(value);
                                break;
                        }
                    }
                }

                out.append(" ");
                if (value.length() > 0) {
                    out.append(key).append("=\"").append(value).append("\"");
                } else {
                    out.append(key);
                }
            }
            //return string without invalid characters
            return out.toString().replaceAll("&#x20;"," ");
        } catch (Exception ex) {
            return "";
        }
    }

    /***
     *
     * @deprecated please use responsive getBackgroundImageRenditions or ComponentProperties.attr which is an AttributeBuilder
     *
     * add style tag to component attributes collection
     * @param componentProperties component attributes collection
     * @param resource resource to search for image
     * @param imageResourceName image node name
     * @return
     *
     */
    @Deprecated
    public String addComponentBackgroundToAttributes(ComponentProperties componentProperties, Resource resource, String imageResourceName) {
        String componentAttributes = componentProperties.get(COMPONENT_ATTRIBUTES, "");
        Resource imageResource = resource.getChild(imageResourceName);
        if (imageResource != null) {
            Resource fileReference = imageResource.getChild(IMAGE_FILEREFERENCE);
            if (fileReference != null) {
                String imageSrc = "";
                if (imageResource.getResourceType().equals(DEFAULT_IMAGE_RESOURCETYPE) || imageResource.getResourceType().endsWith(DEFAULT_IMAGE_RESOURCETYPE_SUFFIX)) {
                    Long lastModified = getLastModified(imageResource);
                    imageSrc = MessageFormat.format(DEFAULT_IMAGE_GENERATED_FORMAT, imageResource.getPath(), lastModified.toString());
                    componentAttributes += MessageFormat.format(" style=\"background-image: url({0})\"", mappedUrl(resource.getResourceResolver(), imageSrc));
                }
            }
        }

        return componentAttributes;
    }


    /***
     * add a new attribute to existing component attributes collection
     * @param componentProperties existing map of component attirbutes
     * @param keyValue attribute name and value list
     * @return
     */
    public String addComponentAttributes(ComponentProperties componentProperties, Object[][] keyValue) {

        String componentAttributes = componentProperties.get(COMPONENT_ATTRIBUTES, "");

        for (Object[] item : keyValue) {
            if (item.length >= 1) {
                String attributeName = item[0].toString();
                String attrbuteValue = item[1].toString();

                componentAttributes += MessageFormat.format(" {0}=\"{1}\"",attributeName,attrbuteValue);
            }
        }

        return componentAttributes;
    }

    /***
     * add a new attribute to existing component attributes collection
     * @param componentProperties existing map of component attirbutes
     * @param attributeName attribute name
     * @param attrbuteValue attribute value
     * @return
     */
    public String addComponentAttributes(ComponentProperties componentProperties, String attributeName, String attrbuteValue) {

        return  addComponentAttributes(componentProperties, new Object[][] {{attributeName,attrbuteValue}});

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
        String uniqueBase = listPage.getPath().substring(1).replace(FileSystem.SEPARATOR, "-");
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
    public String getPageDescription(Page page) {
        String pageDescription = page.getDescription();

        try {

            Resource pageResource = page.getContentResource();

            if (pageResource != null) {
                String[] listLookForDetailComponent = DEFAULT_LIST_DETAILS_SUFFIX;

                String detailsPath = findComponentInPage(page, listLookForDetailComponent);

                ResourceResolver resourceResolver = pageResource.getResourceResolver();

                if (resourceResolver != null) {
                    Resource detailsResource = resourceResolver.resolve(detailsPath);
                    if (detailsResource != null) {
                        Node detailsNode = detailsResource.adaptTo(Node.class);
                        if (detailsNode != null) {
                            if (detailsNode.hasProperty(DETAILS_DESCRIPTION)) {
                                return detailsNode.getProperty(DETAILS_DESCRIPTION).getString();
                            }
                            getLogger().error("getPageDescription: detailsNode does not have " + DETAILS_DESCRIPTION);
                        }
                        getLogger().error("getPageDescription: detailsNode is null");
                    }
                    getLogger().error("getPageDescription: detailsResource is null");
                }
                getLogger().error("getPageDescription: resourceResolver is null");
            }
            getLogger().error("getPageDescription: pageResource is null");

        } catch (Exception ex) {
            getLogger().error("getPageDescription:" + ex.toString());
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
        if (DETAILS_TITLE.equals(field)){
            defaultPageValue =  page.getTitle();
        }else if (DETAILS_DESCRIPTION.equals(field)){
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
     * @param formatFieldName field with format path
     * @param defaultFormat default format template
     * @param componentProperties
     * @param sling
     * @return
     */
    public String compileComponentMessage(String formatFieldName, String defaultFormat, ComponentProperties componentProperties, SlingScriptHelper sling) {

        if (componentProperties == null || sling == null) {
            return StringUtils.EMPTY;
        }


        String formatFieldTagPath = componentProperties.get(formatFieldName,StringUtils.EMPTY);

        //set default
        String fieldFormatValue = defaultFormat;

        if (isNotEmpty(formatFieldTagPath)) {
            fieldFormatValue = getTagValueAsAdmin(formatFieldTagPath,sling);
        }

        //getLogger().error("compileComponentMessage {}",fieldFormatValue);

        return compileMapMessage(fieldFormatValue,componentProperties);

    }

    /**
     * get path of resource in jcr:content
     * @param resource
     * @return
     */
    public String getResourceContentPath(Resource resource) {
        String returnPath = resource.getPath(); //StringUtils.split(resource.getPath(),JcrConstants.JCR_CONTENT)

        if (StringUtils.contains(returnPath, org.apache.jackrabbit.JcrConstants.JCR_CONTENT)) {
            String[] pathParts = StringUtils.splitByWholeSeparator(returnPath,org.apache.jackrabbit.JcrConstants.JCR_CONTENT);
            if (pathParts.length > 1) {
                returnPath = pathParts[1];
            }
        }

        return returnPath;
    }


    /***
     * encode a string using MD5
     * @param encode string to encode
     * @return
     */
    public String MD5(String encode) {
        try {

            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(encode.getBytes());

            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < digest.length; ++i) {
                sb.append(Integer.toHexString((digest[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();

        } catch (Exception ex) {
            LOG.error("MD5 could not encode string ({0})", ex.toString());
        }

        return encode;
    }


    /***
     * get or generate component id
     * @param componentNode component node
     * @return
     */
    public String getComponentId(Node componentNode) {

        String componentId = UUID.randomUUID().toString();
        if (componentNode == null) {
            return componentId;
        }
        String path = "";
        try {
            path = componentNode.getPath();

            if (!componentNode.hasProperty(FIELD_STYLE_COMPONENT_ID)) {
                String prefix = componentNode.getName();

                //cleanup prefix
                prefix = prefix.replaceAll("[^a-zA-Z0-9-_]","_");

                componentNode.setProperty(FIELD_STYLE_COMPONENT_ID,
                         MessageFormat.format(
                                "{0}_{1}",
                                 prefix,
                                RandomStringUtils.randomAlphanumeric(9).toUpperCase())
                );

                componentNode.getSession().save();
            }

            componentId = getProperty(componentNode, FIELD_STYLE_COMPONENT_ID);

        } catch (Exception ex) {
            LOG.error("Could not get id for component path={},id={},error {}",path,componentId,ex.toString());
        }

        return componentId;
    }


    public String getCloudConfigProperty(InheritanceValueMap pageProperties, String configurationName, String propertyName, SlingScriptHelper sling) {
        String returnValue = "";

        //TODO: move this admin session usage into function
        ResourceResolver adminResourceResolver  = openAdminResourceResolver(sling);

        try {

            // Getting attached facebook cloud service config in order to fetch appID
            ConfigurationManager cfgMgr = adminResourceResolver.adaptTo(ConfigurationManager.class);
            Configuration addthisConfiguration = null;
            String[] services = pageProperties.getInherited(ConfigurationConstants.PN_CONFIGURATIONS, new String[]{});
            if(cfgMgr != null) {
                addthisConfiguration = cfgMgr.getConfiguration(configurationName, services);
                if (addthisConfiguration != null) {
                    returnValue = addthisConfiguration.get(propertyName, "");
                }
            }


        } catch (Exception ex) {
            LOG.error(Throwables.getStackTraceAsString(ex) );
        } finally {
            this.closeAdminResourceResolver(adminResourceResolver);
        }

        return returnValue;
    }


    /***
     * find an ancestor resource matching current resource
     * @param currentPage
     * @param componentContext
     * @return
     */
    private Resource findInheritedResource(Page currentPage, ComponentContext componentContext) {
        final String pageResourcePath = currentPage.getContentResource().getPath(); // assume that page have resource
        final Resource thisResource = componentContext.getResource();
        final String nodeResourceType = thisResource.getResourceType();
        final String relativePath = thisResource.getPath().replaceFirst(pageResourcePath.concat(FileSystem.SEPARATOR),"");

        // defn of a parent node
        // 1. is from parent page
        // 2. same sling resource type
        // 3. same relative path

        Page curPage = currentPage.getParent();
        Resource curResource = null;
        Boolean curResourceTypeMatch = false;
        Boolean curCancelInheritParent = false;
        ValueMap curProperties = null;

        try {
            while (null != curPage) {
                // find by same relative path

                String error = format(
                        "findInheritedResource: looking for inherited resource for path=\"{0}\" by relative path=\"{1}\" in parent=\"{2}\""
                        ,pageResourcePath,relativePath,curPage.getPath());
                getLogger().info(error);

                try {
                    curResource = curPage.getContentResource(relativePath);
                } catch (Exception e) {
                    getLogger().info("Failed to get  " + relativePath + " from " + curPage.getContentResource().getPath());
                }

                if (null != curResource) {
                    //check for inherit flag + sling resource type
                    //Boolean cancelInheritParent = properties.get(COMPONENT_CANCEL_INHERIT_PARENT","").contentEquals("true");

                    curProperties = curResource.adaptTo(ValueMap.class);
                    curResourceTypeMatch = curResource.isResourceType(nodeResourceType);
                    curCancelInheritParent = curProperties.get(COMPONENT_CANCEL_INHERIT_PARENT, "").contentEquals("true");

                    if (curResourceTypeMatch && curCancelInheritParent) {
                        String found = format(
                                "findInheritedResource: FOUND looking for inherited resource for path=\"{0}\" by relative path=\"{1}\" in parent=\"{2}\""
                                ,pageResourcePath,relativePath,curPage.getPath());
                        getLogger().info(found);

                        break;
                    } else {
                        String notfound = format(
                                "findInheritedResource: NOT FOUND looking for inherited resource for path=\"{0}\" by relative path=\"{1}\" in parent=\"{2}\""
                                ,pageResourcePath,relativePath,curPage.getPath());
                        getLogger().info(notfound);

                    }
                }

                curPage = curPage.getParent();
            }
        } catch (Exception ex) {
            getLogger().warn("Failed to find inherited resource. {}", ex);
        }

        return curResource;
    }

    /***
     * get attribute from first item list of maps
     * @param sourceMap
     * @return
     */
    public String getFirstAttributeFromList(LinkedHashMap<String, Map> sourceMap, String attributeName) {
        if (sourceMap != null) {
            if (!sourceMap.values().isEmpty()) {
                Map firstItem = sourceMap.values().iterator().next();
                if (firstItem.containsKey(attributeName)) {
                    return firstItem.get(attributeName).toString();
                }
            }
        }
        return "";
    }

    /***
     * get value attribute from list of maps
     * @param sourceMap
     * @return
     */
    public String getFirstValueFromList(LinkedHashMap<String, Map> sourceMap) {
        return getFirstAttributeFromList(sourceMap,"value");
    }


    public String getComponentInPagePath(Node componentNode) {
        String componentInPagePath = "";
        if (componentNode == null) {
            return "";
        }

        try {
            componentInPagePath = componentNode.getPath();
            if (isNotEmpty(componentInPagePath) && componentInPagePath.contains(JcrConstants.JCR_CONTENT)) {
                String[] parts = componentInPagePath.split(JcrConstants.JCR_CONTENT);
                if (parts.length > 0) {
                    componentInPagePath = parts[1];
                    if (isNotEmpty(componentInPagePath) && componentInPagePath.startsWith(FileSystem.SEPARATOR)) {
                        componentInPagePath = componentInPagePath.replaceFirst(FileSystem.SEPARATOR,"");
                    }
                }
            }
        } catch (Exception ex) {

        }
        return componentInPagePath;
    }

%>
<c:set var="DEFAULT_VARIANT" value="<%= DEFAULT_VARIANT %>"/>
<c:set var="DEFAULT_VARIANT_HIDDEN" value="<%= DEFAULT_VARIANT_HIDDEN %>"/>
<c:set var="COMPONENT_ATTRIBUTE_INPAGEPATH" value="<%= COMPONENT_ATTRIBUTE_INPAGEPATH %>"/>
