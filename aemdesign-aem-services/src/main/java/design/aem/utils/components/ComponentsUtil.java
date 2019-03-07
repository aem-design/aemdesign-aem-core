package design.aem.utils.components;

import com.adobe.cq.sightly.WCMUsePojo;
import com.adobe.granite.ui.components.AttrBuilder;
import com.adobe.granite.xss.XSSAPI;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMMode;
import com.day.cq.wcm.api.components.Component;
import com.day.cq.wcm.api.components.ComponentContext;
import com.day.cq.wcm.api.components.ComponentManager;
import com.day.cq.wcm.api.designer.Design;
import com.day.cq.wcm.api.designer.Designer;
import com.day.cq.wcm.api.designer.Style;
import com.day.cq.wcm.foundation.Placeholder;
import com.day.cq.wcm.webservicesupport.Configuration;
import com.day.cq.wcm.webservicesupport.ConfigurationConstants;
import com.day.cq.wcm.webservicesupport.ConfigurationManager;
import com.google.common.base.Throwables;
import design.aem.components.ComponentProperties;
import design.aem.models.GenericModel;
import org.apache.commons.io.IOUtils;
import org.apache.commons.jexl3.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.jackrabbit.core.fs.FileSystem;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Pattern;

import static design.aem.utils.components.CommonUtil.resourceRenderAsHtml;
import static java.text.MessageFormat.format;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class ComponentsUtil {

    public static final Logger LOGGER = LoggerFactory.getLogger(ComponentsUtil.class);

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
    public static final String DEFAULT_BADGE = "badge.default";
    public static final String DEFAULT_ARIA_ROLE_ATTRIBUTE = "role";

    public static final String FIELD_REDIRECT_TARGET = "redirectTarget";

    public static final String DEFAULT_RSS_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss";

    public static final String DETAILS_MENU_COLOR = "menuColor";
    public static final String DETAILS_MENU_ICONSHOW = "menuIconShow";
    public static final String DETAILS_MENU_ICON = "menuIcon";
    public static final String DETAILS_MENU_ACCESS_KEY = "menuAccesskey";
    public static final String DETAILS_TAB_ICONSHOW = "tabIconShow";
    public static final String DETAILS_TAB_ICON = "tabIcon";
    public static final String DETAILS_TITLE_ICONSHOW = "titleIconShow";
    public static final String DETAILS_TITLE_ICON = "titleIcon";
    public static final String DETAILS_OVERLAY_ICONSHOW = "badgeOverlayIconShow";
    public static final String DETAILS_OVERLAY_ICON = "badgeOverlayIcon";
    public static final String DETAILS_CARD_STYLE = "cardStyle";
    public static final String DETAILS_CARD_SIZE = "cardSize";
    public static final String DETAILS_CARD_ICONSHOW = "cardIconShow";
    public static final String DETAILS_CARD_ICON = "cardIcon";

    //shared badge config passed from list to all badge elements
    public static final String DETAILS_LINK_TARGET = "badgeLinkTarget";
    public static final String DETAILS_LINK_TEXT = "badgeLinkText";
    public static final String DETAILS_LINK_TITLE = "badgeLinkTitle";
    public static final String DETAILS_LINK_STYLE = "badgeLinkStyle";
    public static final String DETAILS_TITLE_TRIM = "badgeTitleTrim";
    public static final String DETAILS_TITLE_TRIM_LENGTH_MAX = "badgeTitleTrimLengthMax";
    public static final int DETAILS_TITLE_TRIM_LENGTH_MAX_DEFAULT = 20;
    public static final String DETAILS_TITLE_TRIM_LENGTH_MAX_SUFFIX = "badgeTitleTrimLengthMaxSuffix";
    public static final String DETAILS_TITLE_TRIM_LENGTH_MAX_SUFFIX_DEFAULT = "...";
    public static final String DETAILS_SUMMARY_TRIM = "badgeSummaryTrim";
    public static final String DETAILS_SUMMARY_TRIM_LENGTH_MAX = "badgeSummaryLengthMax";
    public static final int DETAILS_SUMMARY_TRIM_LENGTH_MAX_DEFAULT = 20;
    public static final String DETAILS_SUMMARY_TRIM_LENGTH_MAX_SUFFIX = "badgeSummaryLengthMaxSuffix";
    public static final String DETAILS_SUMMARY_TRIM_LENGTH_MAX_SUFFIX_DEFAULT = "...";
    public static final String DETAILS_THUMBNAIL_WIDTH = "badgeThumbnailWidth";
    public static final String DETAILS_THUMBNAIL_HEIGHT = "badgeThumbnailHeight";
    public static final String DETAILS_THUMBNAIL_TYPE = "badgeThumbnailType";
    public static final String DETAILS_TITLE_TAG_TYPE = "badgeTitleType";
    public static final String DETAILS_THUMBNAIL_ID = "badgeThumbnailId";
    public static final String DETAILS_THUMBNAIL_LICENSE_INFO = "badgeThumbnailLicenseInfo";
    public static final String DETAILS_THUMBNAIL = "badgeThumbnail";

    public static final String DETAILS_BADGE_DESCRIPTION = "badgeDescription";
    public static final String DETAILS_BADGE_TITLE = "badgeTitle";
    public static final String DETAILS_BADGE_CLASS_ICON = "badgeClassIconAttr";
    public static final String DETAILS_BADGE_CLASS = "badgeClassAttr";
    public static final String DETAILS_BADGE_IMAGE_ATTR = "badgeImageAttr";
    public static final String DETAILS_BADGE_LINK_ATTR = "badgeLinkAttr";

    //badge analytics
    public static final String DETAILS_BADGE_ANALYTICS_EVENT_TYPE = "badgeAnalyticsEventType";
    public static final String DETAILS_BADGE_ANALYTICS_TRACK = "badgeAnalyticsTrack";
    public static final String DETAILS_BADGE_ANALYTICS_LOCATION = "badgeAnalyticsLocation";
    public static final String DETAILS_BADGE_ANALYTICS_LABEL = "badgeAnalyticsLabel";

    //page metadata pageMetaProperty
    public static final String DETAILS_PAGE_METADATA_PROPERTY = "pageMetaProperty";
    public static final String DETAILS_PAGE_METADATA_PROPERTY_CONTENT = "pageMetaPropertyContent";

    //analytics
    public static final String DETAILS_ANALYTICS_TRACK = "analyticsTrack";
    public static final String DETAILS_ANALYTICS_LOCATION = "analyticsLocation";
    public static final String DETAILS_ANALYTICS_LABEL = "analyticsLabel";
    public static final String DETAILS_ANALYTICS_PAGENAME = "analyticsPageName";
    public static final String DETAILS_ANALYTICS_PAGETYPE = "analyticsPageType";
    public static final String DETAILS_ANALYTICS_PLATFORM = "analyticsPlatform";
    public static final String DETAILS_ANALYTICS_ABORT = "analyticsAbort";
    public static final String DETAILS_DATA_ANALYTICS_TRACK = "data-layer-track";
    public static final String DETAILS_DATA_ANALYTICS_LOCATION = "data-layer-location";
    public static final String DETAILS_DATA_ANALYTICS_LABEL = "data-layer-label";


    public static final String FIELD_HIDEINMENU = "hideInMenu";


    public static final Pattern DEFAULT_RENDTION_PATTERN_OOTB = Pattern.compile("cq5dam\\.(.*)?\\.(\\d+)\\.(\\d+)\\.(.*)");
    public static final String DEFAULT_ASSET_RENDITION_PREFIX1 = "cq5dam.thumbnail.";
    public static final String DEFAULT_ASSET_RENDITION_PREFIX2 = "cq5dam.web.";

    public static final String DEFAULT_IMAGE_GENERATED_FORMAT = "{0}.img.png/{1}.png";

    public static final String DEFAULT_IMAGE_RESOURCETYPE = "aemdesign/components/media/image";
    public static final String DEFAULT_IMAGE_RESOURCETYPE_SUFFIX = "/components/media/image";

    public static final String COMPONENT_ATTRIBUTES = "componentAttributes";
    public static final String COMPONENT_INSTANCE_NAME = "instanceName";
    public static final String COMPONENT_TARGET_RESOURCE = "targetResource";
    public static final String COMPONENT_ATTRIBUTE_CLASS = "class";
    public static final String COMPONENT_INPAGEPATH = "componentInPagePath";
    public static final String COMPONENT_ATTRIBUTE_INPAGEPATH = "data-layer-componentpath";
    public static final String COMPONENT_BACKGROUND_ASSETS = "componentBackgroundAssets";
    public static final String COMPONENT_VARIANT_TEMPLATE = "variantTemplate";
    public static final String COMPONENT_VARIANT_TEMPLATE_FORMAT = "variant.{0}.html";
    public static final String COMPONENT_BADGE = "componentBadge";
    public static final String COMPONENT_BADGE_SELECTED = "componentBadgeSelected";
    public static final String COMPONENT_BADGE_CONFIG_SET = "badgeConfigSet";
    public static final String COMPONENT_BADGE_TEMPLATE = "componentBadgeTemplate";
    public static final String COMPONENT_BADGE_TEMPLATE_FORMAT = "{0}.html";



    public static final String COMPONENT_CANCEL_INHERIT_PARENT = "cancelInheritParent";


    public static final String PAR_PAGEDETAILS = "par/page-details";
    public static final String ARTICLE_PAR_PAGEDETAILS = "article/par/page-details";
    public static final String ARTICLE_CONTENTS = "article/par";

    public static final String FIELD_STYLE_COMPONENT_ID = "componentId";
    public static final String FIELD_STYLE_COMPONENT_THEME = "componentTheme";
    public static final String FIELD_STYLE_COMPONENT_MODIFIERS = "componentModifiers";
    public static final String FIELD_STYLE_COMPONENT_MODULES = "componentModules";
    public static final String FIELD_STYLE_COMPONENT_CHEVRON = "componentChevron";
    public static final String FIELD_STYLE_COMPONENT_ICON = "componentIcon";
    public static final String FIELD_STYLE_COMPONENT_BOOLEANATTR = "componentBooleanAttrs";
    public static final String FIELD_STYLE_COMPONENT_POSITIONX = "componentPositionX";
    public static final String FIELD_STYLE_COMPONENT_POSITIONY = "componentPositionY";
    public static final String FIELD_STYLE_COMPONENT_WIDTH = "componentWidth";
    public static final String FIELD_STYLE_COMPONENT_HEIGHT = "componentHeight";
    public static final String FIELD_STYLE_COMPONENT_SITETHEMECATEGORY = "siteThemeCategory";
    public static final String FIELD_STYLE_COMPONENT_SITETHEMECOLOR = "siteThemeColor";
    public static final String FIELD_STYLE_COMPONENT_SITETITLECOLOR = "siteTileColor";

    public static final String FIELD_ARIA_ROLE = "ariaRole";
    public static final String FIELD_ARIA_LABEL = "ariaLabel";
    public static final String FIELD_ARIA_DESCRIBEDBY = "ariaDescribedBy";
    public static final String FIELD_ARIA_LABELLEDBY = "ariaLabelledBy";
    public static final String FIELD_ARIA_CONTROLS = "ariaControls";
    public static final String FIELD_ARIA_LIVE = "ariaLive";
    public static final String FIELD_ARIA_HIDDEN = "ariaHidden";
    public static final String FIELD_ARIA_HASPOPUP = "ariaHaspopup";
    public static final String FIELD_ARIA_ACCESSKEY = "ariaAccessKey";

    public static final String FIELD_ARIA_DATA_ATTRIBUTE_ROLE = "role";

    public static final String FIELD_DATA_ANALYTICS_TYPE = "data-analytics-type";
    public static final String FIELD_DATA_ANALYTICS_HIT_TYPE = "data-analytics-hit-type";
    public static final String FIELD_DATA_ANALYTICS_METATYPE = "data-analytics-metatype";
    public static final String FIELD_DATA_ANALYTICS_FILENAME = "data-analytics-filename";
    public static final String FIELD_DATA_ANALYTICS_EVENT_CATEGORY = "data-analytics-event-category";
    public static final String FIELD_DATA_ANALYTICS_EVENT_ACTION = "data-analytics-event-action";
    public static final String FIELD_DATA_ANALYTICS_EVENT_LABEL = "data-analytics-event-label";
    public static final String FIELD_DATA_ANALYTICS_TRANSPORT = "data-analytics-transport";
    public static final String FIELD_DATA_ANALYTICS_NONINTERACTIVE = "data-analytics-noninteraction";

    public static final String FIELD_HREF = "href";
    public static final String FIELD_TITLE_TAG_TYPE = "titleType";
    public static final String FIELD_HIDE_TITLE = "hideTitle";
    public static final String FIELD_HIDE_DESCRIPTION = "hideDescription";
    public static final String FIELD_SHOW_BREADCRUMB = "showBreadcrumb";
    public static final String FIELD_SHOW_TOOLBAR = "showToolbar";
    public static final String FIELD_SHOW_PAGEDATE = "showPageDate";
    public static final String FIELD_SHOW_PARSYS = "showParsys";

    public static final String FIELD_BADGE_PAGE = "badgePage";

    public static final String JCR_NAME_SEPARATOR = "_";

    public static final String FIELD_LICENSE_INFO = "licenseInfo";
    public static final String FIELD_ASSETID = "asset-id";
    public static final String FIELD_ASSET_LICENSED = "asset-licensed";
    public static final String FIELD_ASSET_TRACKABLE = "asset-trackable";
    public static final String FIELD_DATA_ASSET_PRIMARY_ID = "data-asset-id-primary";
    public static final String FIELD_DATA_ASSET_PRIMARY_LICENSE = "data-asset-license-primary";
    public static final String FIELD_DATA_ASSET_SECONDARY_ID = "data-asset-id-secondary";
    public static final String FIELD_DATA_ASSET_SECONDARY_LICENSE= "data-asset-license-secondary";


    public static final String FIELD_DATA_ASSET_ROLLOVER_SRC= "data-rollover-src";


    public static final String FIELD_WIDTH = "width";
    public static final String FIELD_HEIGHT = "height";
    public static final String FIELD_CLASS = "class";
    public static final String FIELD_EXTERNAL = "external";
    public static final String FIELD_TARGET = "target";
    public static final String FIELD_LINK_TARGET = "linkTarget";
    public static final String FIELD_CANONICAL_URL = "canonicalUrl";


    public static final String FIELD_OG_URL = "og:url";
    public static final String FIELD_OG_IMAGE = "og:image";
    public static final String FIELD_OG_TITLE = "og:title";
    public static final String FIELD_OG_DESCRIPTION = "og:description";



    public static final String DETAILS_TITLE = "title";
    public static final String DETAILS_DESCRIPTION = "description";

    public static final String DETAILS_COLUMNS_LAYOUT_CLASS_SMALL = "layoutColumnClassSmall";
    public static final String DETAILS_COLUMNS_LAYOUT_CLASS_MEDIUM = "layoutColumnClassMedium";
    public static final String DETAILS_COLUMNS_LAYOUT_CLASS_LARGE = "layoutColumnClassLarge";
    public static final String DETAILS_COLUMNS_LAYOUT_CLASS_XLARGE = "layoutColumnClassExtraLarge";
    public static final String DETAILS_COLUMNS_LAYOUT_ROW_CLASS = "layoutRowClass";

    public static final String PAGECONTEXTMAP_SOURCE = "object";
    public static final String PAGECONTEXTMAP_SOURCE_TYPE = "objecttype";
    public static final String PAGECONTEXTMAP_SOURCE_TYPE_WCMUSEPOJO = "wcmusepojo";
    public static final String PAGECONTEXTMAP_SOURCE_TYPE_PAGECONTEXT = "pagecontext";
    public static final String PAGECONTEXTMAP_SOURCE_TYPE_SLINGMODEL = "slingmodel";
    public static final String PAGECONTEXTMAP_OBJECT_SLINGREQUEST = "slingRequest";
    public static final String PAGECONTEXTMAP_OBJECT_RESOURCERESOLVER = "resourceResolver";
    public static final String PAGECONTEXTMAP_OBJECT_SLING = "sling";
    public static final String PAGECONTEXTMAP_OBJECT_COMPONENTCONTEXT = "componentContext";
    public static final String PAGECONTEXTMAP_OBJECT_RESOURCE = "resource";
    public static final String PAGECONTEXTMAP_OBJECT_CURRENTNODE = "currentNode";
    public static final String PAGECONTEXTMAP_OBJECT_PROPERTIES = "properties";
    public static final String PAGECONTEXTMAP_OBJECT_CURRENTSTYLE = "currentStyle";
    public static final String PAGECONTEXTMAP_OBJECT_CURRENTPAGE = "currentPage";
    public static final String PAGECONTEXTMAP_OBJECT_RESOURCEPAGE = "resourcePage";
    public static final String PAGECONTEXTMAP_OBJECT_RESOURCEDESIGN = "resourceDesign";


    public static final String DETAILS_SELECTOR_BADGE = "badge";


    //COMPONENT STYLES
    // {
    //   1 required - property name,
    //   2 required - default value,
    //   3 optional - name of component attribute to add value into
    //   4 optional - canonical name of class for handling multivalues, String or Tag
    // }
    public static final Object[][] DEFAULT_FIELDS_STYLE = {
            {FIELD_STYLE_COMPONENT_ID, "", "id"},
            {FIELD_STYLE_COMPONENT_THEME, new String[]{}, "class", Tag.class.getCanonicalName()},
            {FIELD_STYLE_COMPONENT_MODIFIERS, new String[]{}, "class", Tag.class.getCanonicalName()},
            {FIELD_STYLE_COMPONENT_MODULES, new String[]{}, "data-modules", Tag.class.getCanonicalName()},
            {FIELD_STYLE_COMPONENT_CHEVRON, new String[]{}, "class", Tag.class.getCanonicalName()},
            {FIELD_STYLE_COMPONENT_ICON, new String[]{}, "class", Tag.class.getCanonicalName()},
            {FIELD_STYLE_COMPONENT_POSITIONX, "", "x"},
            {FIELD_STYLE_COMPONENT_POSITIONY, "", "y"},
            {FIELD_STYLE_COMPONENT_WIDTH, "", "width"},
            {FIELD_STYLE_COMPONENT_HEIGHT, "", "height"},
            {FIELD_STYLE_COMPONENT_SITETHEMECATEGORY, ""},
            {FIELD_STYLE_COMPONENT_SITETHEMECOLOR, ""},
            {FIELD_STYLE_COMPONENT_SITETITLECOLOR, ""},
            {FIELD_STYLE_COMPONENT_BOOLEANATTR, new String[]{}, " ", Tag.class.getCanonicalName()}, //#3" " =do not store content in data attributes
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
            {FIELD_ARIA_LABEL, "", "aria-label"},
            {FIELD_ARIA_DESCRIBEDBY, "", "aria-describedby"},
            {FIELD_ARIA_LABELLEDBY, "", "aria-labelledby"},
            {FIELD_ARIA_CONTROLS, "", "aria-controls"},
            {FIELD_ARIA_LIVE, "", "aria-live"},
            {FIELD_ARIA_HIDDEN, "", "aria-hidden"},
            {FIELD_ARIA_HASPOPUP, "", "aria-haspopup"},
            {FIELD_ARIA_ACCESSKEY, "", "accesskey"},
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
            {DETAILS_TITLE_TRIM_LENGTH_MAX, ConstantsUtil.DEFAULT_SUMMARY_TRIM_LENGTH},
            {DETAILS_TITLE_TRIM_LENGTH_MAX_SUFFIX, ConstantsUtil.DEFAULT_SUMMARY_TRIM_SUFFIX},
            {DETAILS_SUMMARY_TRIM, false},
            {DETAILS_SUMMARY_TRIM_LENGTH_MAX, ConstantsUtil.DEFAULT_SUMMARY_TRIM_LENGTH},
            {DETAILS_SUMMARY_TRIM_LENGTH_MAX_SUFFIX, ConstantsUtil.DEFAULT_SUMMARY_TRIM_SUFFIX},
            {DETAILS_TAB_ICONSHOW, false},
            {DETAILS_TAB_ICON, new String[]{}, "", Tag.class.getCanonicalName()},
            {DETAILS_TITLE_ICONSHOW, false},
            {DETAILS_TITLE_ICON, new String[]{}, "", Tag.class.getCanonicalName()},
            {DETAILS_OVERLAY_ICONSHOW, false},
            {DETAILS_OVERLAY_ICON, new String[]{}, "", Tag.class.getCanonicalName()},
            {DETAILS_THUMBNAIL_WIDTH, ConstantsUtil.DEFAULT_THUMB_WIDTH_SM},
            {DETAILS_THUMBNAIL_HEIGHT, ""},
            {DETAILS_THUMBNAIL_TYPE, ConstantsUtil.IMAGE_OPTION_RENDITION},
            {DETAILS_TITLE_TAG_TYPE, ConstantsUtil.DEFAULT_TITLE_TAG_TYPE_BADGE},
            {DETAILS_THUMBNAIL_ID, ""},
            {DETAILS_THUMBNAIL_LICENSE_INFO, ""},
            {DETAILS_THUMBNAIL, ""},
            {DETAILS_BADGE_ANALYTICS_TRACK, StringUtils.EMPTY}, //basic
            {DETAILS_BADGE_ANALYTICS_LOCATION, StringUtils.EMPTY}, //basic
            {DETAILS_BADGE_ANALYTICS_LABEL, "${" + DETAILS_LINK_TEXT + "}"}, //basic
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
            {DETAILS_TITLE_TRIM_LENGTH_MAX, DETAILS_TITLE_TRIM_LENGTH_MAX_DEFAULT},
            {DETAILS_TITLE_TRIM_LENGTH_MAX_SUFFIX, DETAILS_TITLE_TRIM_LENGTH_MAX_SUFFIX_DEFAULT},
            {DETAILS_SUMMARY_TRIM, ""},
            {DETAILS_SUMMARY_TRIM_LENGTH_MAX, DETAILS_SUMMARY_TRIM_LENGTH_MAX_DEFAULT},
            {DETAILS_SUMMARY_TRIM_LENGTH_MAX_SUFFIX, DETAILS_SUMMARY_TRIM_LENGTH_MAX_SUFFIX_DEFAULT},
            {DETAILS_TAB_ICONSHOW, ""},
            {DETAILS_TAB_ICON, new String[]{}, "", Tag.class.getCanonicalName()},
            {DETAILS_TITLE_ICONSHOW, ""},
            {DETAILS_TITLE_ICON, new String[]{}, "", Tag.class.getCanonicalName()},
            {DETAILS_OVERLAY_ICONSHOW, ""},
            {DETAILS_OVERLAY_ICON, new String[]{}, "", Tag.class.getCanonicalName()},
            {DETAILS_THUMBNAIL_WIDTH, ""},
            {DETAILS_THUMBNAIL_HEIGHT, ""},
            {DETAILS_THUMBNAIL_TYPE, ""},
            {DETAILS_TITLE_TAG_TYPE, ""},
            {DETAILS_THUMBNAIL_ID, ""},
            {DETAILS_THUMBNAIL_LICENSE_INFO, ""},
            {DETAILS_THUMBNAIL, ""},
            {DETAILS_BADGE_ANALYTICS_TRACK, StringUtils.EMPTY}, //basic
            {DETAILS_BADGE_ANALYTICS_LOCATION, StringUtils.EMPTY}, //basic
            {DETAILS_BADGE_ANALYTICS_LABEL, StringUtils.EMPTY}, //basic

    };

    //COMPONENT ANALYTICS
    // {
    //   1 required - property name,
    //   2 required - default value,
    //   3 optional - name of component attribute to add value into
    //   4 optional - canonical name of class for handling multivalues, String or Tag
    // }
    public static final Object[][] DEFAULT_FIELDS_ANALYTICS = {
            {DETAILS_ANALYTICS_TRACK, StringUtils.EMPTY, DETAILS_DATA_ANALYTICS_TRACK}, //basic
            {DETAILS_ANALYTICS_LOCATION, StringUtils.EMPTY, DETAILS_DATA_ANALYTICS_LOCATION}, //basic
            {DETAILS_ANALYTICS_LABEL, "${label}", DETAILS_DATA_ANALYTICS_LABEL}, //basic
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
            {CommonUtil.DAM_TITLE, StringUtils.EMPTY},
            {CommonUtil.DAM_DESCRIPTION, StringUtils.EMPTY},
            {CommonUtil.DAM_CREDIT, StringUtils.EMPTY},
            {CommonUtil.DAM_HEADLINE, StringUtils.EMPTY},
            {CommonUtil.DAM_SOURCE, StringUtils.EMPTY},
            {CommonUtil.DAM_SOURCE_URL, StringUtils.EMPTY},
    };

    //ASSET VIDEO METADATA
    // {
    //   1 required - property name,
    //   2 required - default value,
    //   3 optional - name of component attribute to add value into
    //   4 optional - canonical name of class for handling multivalues, String or Tag
    // }
    public static final Object[][] DEFAULT_FIELDS_ASSET_VIDEO = {
            {CommonUtil.DAM_TITLE, StringUtils.EMPTY},
            {CommonUtil.DAM_DESCRIPTION, StringUtils.EMPTY},
            {CommonUtil.DAM_HEADLINE, StringUtils.EMPTY},
            {CommonUtil.DAM_CREDIT, StringUtils.EMPTY},
            {CommonUtil.DAM_SOURCE, StringUtils.EMPTY},
            {CommonUtil.DAM_SOURCE_URL, StringUtils.EMPTY},
            {CommonUtil.DAM_VIDEO_URL, StringUtils.EMPTY},
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

    //COMMON COMPONENT LAYOUT FIELDS
    // {
    //   1 required - property name,
    //   2 required - default value,
    //   3 optional - name of component attribute to add value into
    //   4 optional - canonical name of class for handling multivalues, String or Tag
    // }
    public static final Object[][] DEFAULT_COMMON_COMPONENT_LAYOUT_FIELDS = {
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {FIELD_TITLE_TAG_TYPE, ""},
            {FIELD_HIDE_TITLE, false},
            {FIELD_HIDE_DESCRIPTION, false},
            {FIELD_SHOW_BREADCRUMB, true},
            {FIELD_SHOW_TOOLBAR, true},
            {FIELD_SHOW_PAGEDATE, true},
            {FIELD_SHOW_PARSYS, true},
    };

    /**
     * Get a include file contents
     *
     * @param resourceResolver is the resource
     * @param paths
     * @param separator
     * @return a string with the file contents
     */
    public static String getResourceContent(ResourceResolver resourceResolver, String[] paths, String separator) {
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
    public static String getResourceContent(Resource resource) {
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
                LOGGER.warn("Could not load file to be included {}", resource.getPath());
            }
        }
        return returnValue;
    }

    /**
     * Read properties for the Component do not use styles.
     * @param pageContext  current page context
     * @param name         name of the property
     * @param defaultValue default value to return if not set
     * @return found component
     */
    public static Object getComponentProperty(PageContext pageContext, String name, Object defaultValue) {
        if (pageContext == null) {
            return "";
        }
        return getComponentProperty(pageContext, name, defaultValue, false);
    }


    /**
     * Read properties for the Component, use component style to override properties if they are not set.
     * @param pageContext  current page context
     * @param name         name of the property
     * @param defaultValue default value for the property
     * @param useStyle     use styles properties if property is missing
     * @return component property
     */
    public static Object getComponentProperty(PageContext pageContext, String name, Object defaultValue, Boolean useStyle) {
        //quick fail
        if (pageContext == null) {
            LOGGER.warn("getComponentProperty, pageContext is ({0})", pageContext);
            return "";
        }

        ValueMap properties = (ValueMap) pageContext.getAttribute("properties");

        if (useStyle) {
            Style currentStyle = (Style) pageContext.getAttribute("currentStyle");
            return getComponentProperty(properties, currentStyle, name, defaultValue, useStyle);
        } else {
            return getComponentProperty(properties, null, name, defaultValue, useStyle);
        }
    }

    /**
     * Read properties for the Component, use component style to override properties if they are not set.
     * @param componentResource page to get properties from
     * @param name              name of the property
     * @param defaultValue      default value for the property
     * @param useStyle          use styles properties if property is missing
     * @return component property
     */
    public static Object getComponentProperty(Resource componentResource, Style pageStyle, String name, Object defaultValue, Boolean useStyle) {
        //quick fail
        if (componentResource == null) {
            LOGGER.warn("getComponentProperty, componentResource is ({0})", componentResource);
            return "";
        }

        ValueMap properties = componentResource.adaptTo(ValueMap.class);

        return getComponentProperty(properties, pageStyle, name, defaultValue, useStyle);

    }

    /**
     * Read properties for the Component, use component style to override properties if they are not set.
     * @param componentProperties component properties
     * @param name                name of the property
     * @param defaultValue        default value for the property
     * @param useStyle            use styles properties if property is missing
     * @return component property
     */
    public static Object getComponentProperty(ValueMap componentProperties, Style pageStyle, String name, Object defaultValue, Boolean useStyle) {
        //quick fail
        if (componentProperties == null) {
            LOGGER.warn("getComponentProperty, componentProperties is ({0})", componentProperties);
            return "";
        }
        if (useStyle && pageStyle == null) {
            LOGGER.warn("getComponentProperty, useStyle is ({0}) but pageStyle is {1}", useStyle, pageStyle);
            return "";
        }

        if (useStyle) {
            return componentProperties.get(name, pageStyle.get(name, defaultValue));
        } else {
            return componentProperties.get(name, defaultValue);
        }
    }

    /**
     * Return default place holder for the component, use classic UI placeholder is needed.
     * @param slingRequest Current Sling reqsuest
     * @param component    Current component
     * @return placeholder string
     */
    @SuppressWarnings("unchecked")
    public static String getDefaultPlaceholder(SlingHttpServletRequest slingRequest, Component component) {
        return Placeholder.getDefaultPlaceholder(slingRequest, component, ConstantsUtil.DEFAULT_CLASSIC_PLACEHOLDER);
    }


    /**
     * returns component values with defaults from target component on a page.
     * @param pageContext   current page context
     * @param componentPage target page
     * @param componentPath target component path
     * @param fieldLists    list of fields definition Object{{name, defaultValue, attributeName, valueTypeClass},...}
     * @return map of attributes
     */
    public static ComponentProperties getComponentProperties(PageContext pageContext, Page componentPage, String componentPath, Object[][]... fieldLists) {
        try {
            Resource componentResource = componentPage.getContentResource(componentPath);

            return getComponentProperties(pageContext, componentResource, fieldLists);

        } catch (Exception ex) {
            LOGGER.error("getComponentProperties: " + componentPath + ", error: " + ex.toString());
        }


        return getNewComponentProperties(pageContext);
    }


    /***
     * helper to create new Component Properties.
     * @param pageContext page context
     * @return map of attributes
     */
    public static ComponentProperties getNewComponentProperties(PageContext pageContext) {

        SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) pageContext.getAttribute("slingRequest");

        Map<String, Object> pageContextMap = new HashMap<>();
        pageContextMap.put("slingRequest", slingRequest);


        return getNewComponentProperties(pageContextMap);

    }

    //TODO: move to class

    /***
     * helper to create new Component Properties.
     * @param slingRequest sling request
     * @return map of attributes
     */
    public static ComponentProperties getNewComponentProperties(SlingHttpServletRequest slingRequest) {

        Map<String, Object> pageContextMap = new HashMap<>();
        pageContextMap.put("slingRequest", slingRequest);


        return getNewComponentProperties(pageContextMap);
    }


    /***
     * helper to create new Component Properties.
     * @param wcmUsePojoModel component model pojo
     * @return map of attributes
     */
    public static ComponentProperties getNewComponentProperties(WCMUsePojo wcmUsePojoModel) {

        SlingHttpServletRequest slingRequest = wcmUsePojoModel.getRequest();

        Map<String, Object> pageContextMap = new HashMap<>();
        pageContextMap.put("slingRequest", slingRequest);


        return getNewComponentProperties(pageContextMap);
    }

    /***
     * helper to create new Component Properties.
     * @param pageContext page content map
     * @return map of attributes
     */
    @SuppressWarnings("Depreciated")
    public static ComponentProperties getNewComponentProperties(Map<String, Object> pageContext) {
        ComponentProperties componentProperties = new ComponentProperties();
        componentProperties.attr = new AttrBuilder(null, null);
        try {
            SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) pageContext.get("slingRequest");
            HttpServletRequest request = (HttpServletRequest) slingRequest;
            com.adobe.granite.xss.XSSAPI oldXssAPI = slingRequest.adaptTo(com.adobe.granite.xss.XSSAPI.class);
            componentProperties.attr = new AttrBuilder(request, oldXssAPI);

        } catch (Exception ex) {
            LOGGER.error("getNewComponentProperties: could not configure componentProperties with attributeBuilder");
        }
        return componentProperties;
    }

    /**
     * returns component values with defaults from pageContent Properties.
     * @param pageContext current page context
     * @param fieldLists  list of fields definition Object{{name, defaultValue, attributeName, valueTypeClass},...}
     * @return map of attributes
     */
    public static ComponentProperties getComponentProperties(PageContext pageContext, Object[][]... fieldLists) {
        return getComponentProperties(pageContext, null, true, fieldLists);
    }

    /**
     * returns component values with defaults from pageContent Properties.
     * @param wcmUsePojoModel component model pojo
     * @param fieldLists      list of fields definition Object{{name, defaultValue, attributeName, valueTypeClass},...}
     * @return map of attributes
     */
    public static ComponentProperties getComponentProperties(WCMUsePojo wcmUsePojoModel, Object[][]... fieldLists) {
        return getComponentProperties(wcmUsePojoModel, null, true, fieldLists);
    }


    /**
     * returns component values with defaults from pageContent Properties.
     * @param genericModel generic model
     * @param fieldLists      list of fields definition Object{{name, defaultValue, attributeName, valueTypeClass},...}
     * @return map of attributes
     */
    public static ComponentProperties getComponentProperties(GenericModel genericModel, Object[][]... fieldLists) {
        return getComponentProperties(genericModel, null, true, fieldLists);
    }


    /**
     * returns component values with defaults from target component on a page.
     * @param wcmUsePojoModel            component model
     * @param targetResource             resource to use as source
     * @param fieldLists                 list of fields definition Object{{name, defaultValue, attributeName, valueTypeClass},...}
     * @return map of attributes
     */
    public static ComponentProperties getComponentProperties(WCMUsePojo wcmUsePojoModel, Object targetResource, Object[][]... fieldLists) {
        try {

            return getComponentProperties(getContextObjects(wcmUsePojoModel), targetResource, true, fieldLists);

        } catch (Exception ex) {
            LOGGER.error("getComponentProperties(WCMUsePojo) could not read required objects: " + wcmUsePojoModel + ", error: " + ex.toString());
        }


        return getNewComponentProperties(wcmUsePojoModel);
    }


    /**
     * returns component values with defaults from target component on a page.
     * @param genericModel               component model
     * @param targetResource             resource to use as source
     * @param fieldLists                 list of fields definition Object{{name, defaultValue, attributeName, valueTypeClass},...}
     * @return map of attributes
     */
    public static ComponentProperties getComponentProperties(GenericModel genericModel, Object targetResource, Object[][]... fieldLists) {
        try {

            return getComponentProperties(genericModel.getPageContextMap(), targetResource, true, fieldLists);

        } catch (Exception ex) {
            LOGGER.error("getComponentProperties(WCMUsePojo) could not read required objects: " + genericModel + ", error: " + ex.toString());
        }


        return getNewComponentProperties(genericModel.getPageContextMap());
    }


    /**
     * returns component values with defaults from a targetResource, default to pageContext properties.
     * @param pageContext    current page context
     * @param targetResource resource to use as source
     * @param fieldLists     list of fields definition Object{{name, defaultValue, attributeName, valueTypeClass},...}
     * @return map of attributes
     */
    @SuppressWarnings("unchecked")
    public static ComponentProperties getComponentProperties(PageContext pageContext, Object targetResource, Object[][]... fieldLists) {
        return getComponentProperties(pageContext, targetResource, true, fieldLists);
    }

    /**
     * get context objects.
     * @param wcmUsePojoModel model to use
     * @return map of objects
     */
    @SuppressWarnings("Duplicates")
    public static Map<String, Object> getContextObjects(WCMUsePojo wcmUsePojoModel) {
        SlingHttpServletRequest slingRequest = wcmUsePojoModel.getRequest();
        ResourceResolver resourceResolver = wcmUsePojoModel.getResourceResolver();
        SlingScriptHelper sling = wcmUsePojoModel.getSlingScriptHelper();
        ComponentContext componentContext = wcmUsePojoModel.getComponentContext();
        Resource resource = wcmUsePojoModel.getResource();
        Node currentNode = resource.adaptTo(Node.class);
        ValueMap properties = wcmUsePojoModel.getProperties();
        Style currentStyle = wcmUsePojoModel.getCurrentStyle();
        Page currentPage = wcmUsePojoModel.getCurrentPage();
        Page resourcePage = wcmUsePojoModel.getResourcePage();
        Design resourceDesign = wcmUsePojoModel.getResourceDesign();

        Map<String, Object> pageContextMap = new HashMap<>();
        pageContextMap.put(PAGECONTEXTMAP_OBJECT_SLINGREQUEST, slingRequest);
        pageContextMap.put(PAGECONTEXTMAP_OBJECT_RESOURCERESOLVER, resourceResolver);
        pageContextMap.put(PAGECONTEXTMAP_OBJECT_SLING, sling);
        pageContextMap.put(PAGECONTEXTMAP_OBJECT_COMPONENTCONTEXT, componentContext);
        pageContextMap.put(PAGECONTEXTMAP_OBJECT_RESOURCE, resource);
        pageContextMap.put(PAGECONTEXTMAP_OBJECT_CURRENTNODE, currentNode);
        pageContextMap.put(PAGECONTEXTMAP_OBJECT_PROPERTIES, properties);
        pageContextMap.put(PAGECONTEXTMAP_OBJECT_CURRENTSTYLE, currentStyle);
        pageContextMap.put(PAGECONTEXTMAP_OBJECT_CURRENTPAGE, currentPage);
        pageContextMap.put(PAGECONTEXTMAP_OBJECT_RESOURCEPAGE, resourcePage);
        pageContextMap.put(PAGECONTEXTMAP_OBJECT_RESOURCEDESIGN, resourceDesign);
        pageContextMap.put(PAGECONTEXTMAP_SOURCE, wcmUsePojoModel);
        pageContextMap.put(PAGECONTEXTMAP_SOURCE_TYPE, PAGECONTEXTMAP_SOURCE_TYPE_WCMUSEPOJO);

        return pageContextMap;
    }

    /**
     * get context objects.
     * @param pageContext page context to use
     * @return map of objects
     */
    @SuppressWarnings("Duplicates")
    public static Map<String, Object> getContextObjects(PageContext pageContext) {
        SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) pageContext.getAttribute(PAGECONTEXTMAP_OBJECT_SLINGREQUEST);
        ResourceResolver resourceResolver = (ResourceResolver) pageContext.getAttribute(PAGECONTEXTMAP_OBJECT_RESOURCERESOLVER);
        SlingScriptHelper sling = (SlingScriptHelper) pageContext.getAttribute(PAGECONTEXTMAP_OBJECT_SLING);
        ComponentContext componentContext = (ComponentContext) pageContext.getAttribute(PAGECONTEXTMAP_OBJECT_COMPONENTCONTEXT);
        Resource resource = (Resource) pageContext.getAttribute(PAGECONTEXTMAP_OBJECT_RESOURCE);
        Node currentNode = (Node) pageContext.getAttribute(PAGECONTEXTMAP_OBJECT_CURRENTNODE);
        ValueMap properties = (ValueMap) pageContext.getAttribute(PAGECONTEXTMAP_OBJECT_PROPERTIES);
        Style currentStyle = (Style) pageContext.getAttribute(PAGECONTEXTMAP_OBJECT_CURRENTSTYLE);
        Page currentPage = (Page) pageContext.getAttribute(PAGECONTEXTMAP_OBJECT_CURRENTPAGE);
        Page resourcePage = (Page) pageContext.getAttribute(PAGECONTEXTMAP_OBJECT_RESOURCEPAGE);
        Design resourceDesign = (Design) pageContext.getAttribute(PAGECONTEXTMAP_OBJECT_RESOURCEDESIGN);


        Map<String, Object> pageContextMap = new HashMap<>();
        pageContextMap.put(PAGECONTEXTMAP_OBJECT_SLINGREQUEST, slingRequest);
        pageContextMap.put(PAGECONTEXTMAP_OBJECT_RESOURCERESOLVER, resourceResolver);
        pageContextMap.put(PAGECONTEXTMAP_OBJECT_SLING, sling);
        pageContextMap.put(PAGECONTEXTMAP_OBJECT_COMPONENTCONTEXT, componentContext);
        pageContextMap.put(PAGECONTEXTMAP_OBJECT_RESOURCE, resource);
        pageContextMap.put(PAGECONTEXTMAP_OBJECT_CURRENTNODE, currentNode);
        pageContextMap.put(PAGECONTEXTMAP_OBJECT_PROPERTIES, properties);
        pageContextMap.put(PAGECONTEXTMAP_OBJECT_CURRENTSTYLE, currentStyle);
        pageContextMap.put(PAGECONTEXTMAP_OBJECT_CURRENTPAGE, currentPage);
        pageContextMap.put(PAGECONTEXTMAP_OBJECT_RESOURCEPAGE, resourcePage);
        pageContextMap.put(PAGECONTEXTMAP_OBJECT_RESOURCEDESIGN, resourceDesign);
        pageContextMap.put(PAGECONTEXTMAP_SOURCE, pageContext);
        pageContextMap.put(PAGECONTEXTMAP_SOURCE_TYPE, PAGECONTEXTMAP_SOURCE_TYPE_PAGECONTEXT);

        return pageContextMap;
    }


    /**
     * returns component values with defaults from target component on a page.
     * @param genericModel            component model
     * @param targetResource             resource to use as source
     * @param includeComponentAttributes include additional attibutes associated with component
     * @param fieldLists                 list of fields definition Object{{name, defaultValue, attributeName, valueTypeClass},...}
     * @return map of attributes
     */
    public static ComponentProperties getComponentProperties(GenericModel genericModel, Object targetResource, Boolean includeComponentAttributes, Object[][]... fieldLists) {
        try {

            return getComponentProperties(genericModel.getPageContextMap(), targetResource, includeComponentAttributes, fieldLists);

        } catch (Exception ex) {
            LOGGER.error("getComponentProperties(WCMUsePojo) could not read required objects: " + genericModel + ", error: " + ex.toString());
        }


        return getNewComponentProperties(genericModel.getPageContextMap());
    }

    /**
     * returns component values with defaults from target component on a page.
     * @param wcmUsePojoModel            component model
     * @param targetResource             resource to use as source
     * @param includeComponentAttributes include additional attibutes associated with component
     * @param fieldLists                 list of fields definition Object{{name, defaultValue, attributeName, valueTypeClass},...}
     * @return map of attributes
     */
    public static ComponentProperties getComponentProperties(WCMUsePojo wcmUsePojoModel, Object targetResource, Boolean includeComponentAttributes, Object[][]... fieldLists) {
        try {

            return getComponentProperties(getContextObjects(wcmUsePojoModel), targetResource, includeComponentAttributes, fieldLists);

        } catch (Exception ex) {
            LOGGER.error("getComponentProperties(WCMUsePojo) could not read required objects: " + wcmUsePojoModel + ", error: " + ex.toString());
        }


        return getNewComponentProperties(wcmUsePojoModel);
    }

    /**
     * returns component values with defaults from a targetResource, default to pageContext properties.
     * @param pageContext                current page context
     * @param targetResource             resource to use as source
     * @param includeComponentAttributes include additional attibutes associated with component
     * @param fieldLists                 list of fields definition Object{{name, defaultValue, attributeName, valueTypeClass},...}
     * @return map of attributes
     */
    @SuppressWarnings("unchecked")
    public static ComponentProperties getComponentProperties(PageContext pageContext, Object targetResource, Boolean includeComponentAttributes, Object[][]... fieldLists) {
        try {

            return getComponentProperties(getContextObjects(pageContext), targetResource, includeComponentAttributes, fieldLists);

        } catch (Exception ex) {
            LOGGER.error("getComponentProperties(PageContext) could not read required objects", ex.toString());
        }


        return getNewComponentProperties(pageContext);
    }

    /**
     * returns component values with defaults from a targetResource, default to pageContext properties.
     * @param pageContext                current page context
     * @param targetResource             resource to use as source
     * @param includeComponentAttributes include additional attibutes associated with component
     * @param fieldLists                 list of fields definition Object{{name, defaultValue, attributeName, valueTypeClass},...}
     * @return map of attributes
     */
    @SuppressWarnings({"unchecked","Depreciated"})
    public static ComponentProperties getComponentProperties(Map<String, Object> pageContext, Object targetResource, Boolean includeComponentAttributes, Object[][]... fieldLists) {
        ComponentProperties componentProperties = new ComponentProperties();

        boolean addMoreAttributes = includeComponentAttributes;

        SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) pageContext.get("slingRequest");
        ResourceResolver resourceResolver = (ResourceResolver) pageContext.get("resourceResolver");

        SlingScriptHelper sling = (SlingScriptHelper) pageContext.get("sling");
        HttpServletRequest request = (HttpServletRequest) slingRequest;

        com.adobe.granite.xss.XSSAPI oldXssAPI = slingRequest.adaptTo(com.adobe.granite.xss.XSSAPI.class);

        ComponentContext componentContext = (ComponentContext) pageContext.get("componentContext");
        Component component = componentContext.getComponent();

        componentProperties.attr = new AttrBuilder(request, oldXssAPI);
        if (addMoreAttributes) {
//            componentProperties.attr.addBoolean("component", true);
            componentProperties.attr.add("component", "true");
        }
//        AttrBuilder itemAttr = new AttrBuilder(request, oldXssAPI);

        final String CLASS_TYPE_RESOURCE = Resource.class.getCanonicalName();
        final String CLASS_TYPE_JCRNODERESOURCE = "org.apache.sling.jcr.resource.internal.helper.jcr.JcrNodeResource";
        final String CLASS_TYPE_ASSET = "com.adobe.granite.asset.core.impl.AssetImpl";

        ResourceResolver adminResourceResolver = SecurityUtil.openAdminResourceResolver(sling);
        try {

            Node currentNode = (javax.jcr.Node) pageContext.get("currentNode");

            TagManager tagManager = adminResourceResolver.adaptTo(TagManager.class);

            // if targetResource == null get defaults
            ValueMap properties = (ValueMap) pageContext.get("properties");
            Style currentStyle = (Style) pageContext.get("currentStyle");

            //if targetResource != null get the appropriate objects
            if (targetResource != null && targetResource.getClass().getCanonicalName().equals(CLASS_TYPE_ASSET)) {

                try {
                    com.adobe.granite.asset.api.Asset asset = (com.adobe.granite.asset.api.Asset) targetResource;

                    Resource resource = asset.getResourceResolver().getResource(asset, JcrConstants.JCR_CONTENT);

                    if (resource != null) {
                        resource = resource.getChild(DamConstants.METADATA_FOLDER);
                    }
                    properties = resource.adaptTo(ValueMap.class);
                    addMoreAttributes = false;

                } catch (Exception ex) {
                    LOGGER.error("getComponentProperties: could not evaluate target asset", ex);
                    return componentProperties;
                }
            } else if (targetResource != null &&
                    (targetResource.getClass().getCanonicalName().equals(CLASS_TYPE_RESOURCE) ||
                            targetResource.getClass().getCanonicalName().equals(CLASS_TYPE_JCRNODERESOURCE))) {

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
                    Node resourceNode = (javax.jcr.Node) resource.adaptTo(Node.class);
                    if (resourceNode != null) {
                        currentNode = resourceNode;
                    }

                    componentProperties.put(COMPONENT_TARGET_RESOURCE, resource.getPath());

                    //getComponentProperty(ValueMap componentProperties, Style pageStyle, String name, Object defaultValue, Boolean useStyle)
                    //fieldValue = getComponentProperty(resourceProperties, resourceStyle, fieldName, fieldDefaultValue, true);
                } catch (Exception ex) {
                    LOGGER.error("getComponentProperties: could not evaluate target resource", ex);
                    return componentProperties;
                }
            } else if (targetResource != null) {
                LOGGER.warn("getComponentProperties: processing is unsupported of target resource of type: " + targetResource.getClass().getCanonicalName());
            } else if (targetResource == null) {
                LOGGER.warn("getComponentProperties: processing of NULL target resource of type return design defaults");
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

            if (fieldLists != null) {
                JexlEngine jexl = new JexlBuilder().create();
                JxltEngine jxlt = jexl.createJxltEngine();
                JexlContext jc = new MapContext(componentProperties);

                for (Object[][] fieldDefaults : fieldLists) {
                    for (Object[] field : fieldDefaults) {
                        if (field.length < 1) {
                            throw new IllegalArgumentException(format("Key, Value, ..., Value-n expected, instead got {0} fields.", field.length));
                        }
                        String fieldName = field[0].toString();

                        if (componentProperties.containsKey(fieldName)) {
                            //skip entries that already exist
                            //first Object in fieldLists will set a field value
                            //we expect the additional Objects to not override
                            LOGGER.warn("getComponentProperties: skipping property [{}] its already defined, {}", fieldName, componentContext.getResource().getPath());
                            continue;
                        }

                        Object fieldDefaultValue = field[1];

                        if (fieldDefaultValue instanceof String && StringUtils.isNotEmpty(fieldDefaultValue.toString())) {

                            if (fieldDefaultValue.toString().matches("(\\$\\{.*\\})")) {
                                //try to evaluate default value expression
                                try {
                                    //expressions reference https://commons.apache.org/proper/commons-jexl/reference/syntax.html
                                    JxltEngine.Expression expr = jxlt.createExpression(fieldDefaultValue.toString());
                                    String defaultValueExpressionValue = expr.evaluate(jc).toString();
                                    if (isNotEmpty(defaultValueExpressionValue)) {
                                        fieldDefaultValue = defaultValueExpressionValue;
                                    }
                                } catch (JexlException jex) {
//                                    LOGGER.error("could not evaluate default value expression field={}, default value={}, error={}",fieldName, fieldDefaultValue.toString(), jex.getInfo());
                                    //remove expressions from string
                                    fieldDefaultValue = ((String) fieldDefaultValue).replaceAll("(\\$\\{.*\\})","");
                                }
                            }

                        }

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
                                            fieldValue = TagUtil.getTagsValues(tagManager, resourceResolver, " ", (String[]) fieldValue);
                                        } else {
                                            fieldValueString = TagUtil.getTagsAsValues(tagManager, resourceResolver, " ", (String[]) fieldValue);
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

                                if (fieldValueString.contains(" ")) {
                                    //multiple boolean attributes being added
                                    for (String item : fieldValueString.split(" ")) {
                                        if (!item.contains("=")) {
                                            componentProperties.attr.add(item, "true");
                                        } else {
                                            String[] items = item.split("=");
                                            componentProperties.attr.add(items[0],StringUtils.substringBetween(items[1],"\"","\""));
                                        }
                                    }
                                } else {
                                    if (!fieldValueString.contains("=")) {
                                        componentProperties.attr.add(fieldValueString, "true");
                                    } else {
                                        String[] items = fieldValueString.split("=");
                                        componentProperties.attr.add(items[0],StringUtils.substringBetween(items[1],"\"","\""));
                                    }
//                                componentProperties.attr.addBoolean(fieldValueString, true);
                                }
                            }

                        }

                        try {
                            componentProperties.put(fieldName, fieldValue);
                        } catch (Exception ex) {
                            LOGGER.error("error adding value. " + ex);
                        }
                    }
                }

                //get current variant value
                String variant = componentProperties.get(FIELD_VARIANT, "");

                //add variant to the end of class string
                if (!componentProperties.attr.isEmpty() && addMoreAttributes) {
                    //check if component variant exist and add to class chain if not default
                    if (isNotEmpty(variant) && !variant.equals(DEFAULT_VARIANT)) {
                        componentProperties.attr.add(COMPONENT_ATTRIBUTE_CLASS, variant);
                    }
                    componentProperties.put(COMPONENT_ATTRIBUTES, buildAttributesString(componentProperties.attr.getData(), oldXssAPI));
                }

                //use default variant if variant value not set
                if (isEmpty(variant)) {
                    variant = DEFAULT_VARIANT;
                }
                if (addMoreAttributes) {
                    //compile variantTemplate param
                    componentProperties.put(COMPONENT_VARIANT_TEMPLATE, format(COMPONENT_VARIANT_TEMPLATE_FORMAT, variant));
                }
            }

        } catch (Exception ex) {
            LOGGER.error("getComponentProperties: " + ex.getMessage(), ex);
            //out.write( Throwables.getStackTraceAsString(ex) );
        } finally {
            SecurityUtil.closeAdminResourceResolver(adminResourceResolver);
        }

        return componentProperties;
    }

    /***
     * build attributes from attributes data without encoding.
     * @param data map of data attributes and values
     * @param xssAPI old xssi api
     * @return attributes string
     * @throws IOException
     */
    @SuppressWarnings("Depreciated")
    public static String buildAttributesString(Map<String, String> data, com.adobe.granite.xss.XSSAPI xssAPI) {
        return buildAttributesString(data, xssAPI, null);
    }

    /***
     * build attributes from attributes data.
     * @param data map of data attributes and values
     * @param xssAPI old xssi api
     * @param encodings map of encoding per data attribute
     * @return attributes string
     * @throws IOException
     */
    @SuppressWarnings("Depreciated")
    public static String buildAttributesString(Map<String, String> data, com.adobe.granite.xss.XSSAPI xssAPI, Map<String, String> encodings) {
        try {
            StringWriter out = new StringWriter();
            String key;
            String value;

            Iterator items = data.entrySet().iterator();
            while (items.hasNext()) {
                Map.Entry<String, String> e = (Map.Entry) items.next();
                key = (String) e.getKey();
                value = (String) e.getValue();

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
            return out.toString().replaceAll("&#x20;", " ");
        } catch (Exception ex) {
            return "";
        }
    }

    /***
     *
     * @deprecated please use responsive getBackgroundImageRenditions or ComponentProperties.attr which is an AttributeBuilder.
     * add style tag to component attributes collection
     * @param componentProperties component attributes collection
     * @param resource resource to search for image
     * @param imageResourceName image node name
     * @return style with background image
     *
     */
    @Deprecated
    public static String addComponentBackgroundToAttributes(ComponentProperties componentProperties, Resource resource, String imageResourceName) {
        String componentAttributes = componentProperties.get(COMPONENT_ATTRIBUTES, "");
        Resource imageResource = resource.getChild(imageResourceName);
        if (imageResource != null) {
            Resource fileReference = imageResource.getChild(ConstantsUtil.IMAGE_FILEREFERENCE);
            if (fileReference != null) {
                String imageSrc = "";
                if (imageResource.getResourceType().equals(DEFAULT_IMAGE_RESOURCETYPE) || imageResource.getResourceType().endsWith(DEFAULT_IMAGE_RESOURCETYPE_SUFFIX)) {
                    Long lastModified = CommonUtil.getLastModified(imageResource);
                    imageSrc = format(DEFAULT_IMAGE_GENERATED_FORMAT, imageResource.getPath(), lastModified.toString());
                    componentAttributes += format(" style=\"background-image: url({0})\"", ResolverUtil.mappedUrl(resource.getResourceResolver(), imageSrc));
                }
            }
        }

        return componentAttributes;
    }


    /***
     * @deprecated please use responsive {@link #buildAttributesString(Map, XSSAPI)}
     * add a new attribute to existing component attributes collection.
     * @param componentProperties existing map of component attirbutes
     * @param keyValue attribute name and value list
     * @return string of component attributes
     */
    @Deprecated
    public static String addComponentAttributes(ComponentProperties componentProperties, Object[][] keyValue) {

        String componentAttributes = componentProperties.get(COMPONENT_ATTRIBUTES, "");

        for (Object[] item : keyValue) {
            if (item.length >= 1) {
                String attributeName = item[0].toString();
                String attrbuteValue = item[1].toString();

                componentAttributes += format(" {0}=\"{1}\"", attributeName, attrbuteValue);
            }
        }

        return componentAttributes;
    }

    /***
     * @deprecated please use responsive {@link #buildAttributesString(Map, XSSAPI)}
     * add a new attribute to existing component attributes collection.
     * @param componentProperties existing map of component attirbutes
     * @param attributeName attribute name
     * @param attrbuteValue attribute value
     * @return string of component attributes
     */
    @Deprecated
    public static String addComponentAttributes(ComponentProperties componentProperties, String attributeName, String attrbuteValue) {

        return addComponentAttributes(componentProperties, new Object[][]{{attributeName, attrbuteValue}});

    }

    /**
     * Depending on whether we're looking at the homepage get the page title from navigation title first
     * otherwise just get the page title
     *
     * @param page is the page to inspect
     * @return the page title
     */
    public static String getPageTitleBasedOnDepth(Page page) {

        // if we're looking at the root node try to find the "site-title" property
        if (page.getDepth() == DEPTH_ROOTNODE + COUNT_CONTENT_NODE) {
            String alternativeTitle =
                    page.getProperties().get(
                            JcrConstants.JCR_TITLE,
                            CommonUtil.getPageTitle(page)     // if not set, just use the root nodes page title
                    );

            return alternativeTitle;
        }
        // if we're on the homepage, show the navigation title (which is the site name)
        else if (page.getDepth() == DEPTH_HOMEPAGE + COUNT_CONTENT_NODE) {
            return CommonUtil.getPageNavTitle(page);
        } else {
            return CommonUtil.getPageTitle(page);
        }

    }


    /**
     * Get the unique identifier for this paqe
     *
     * @param listPage is the page to uniqify
     * @return the md5 hash of the page's content path
     */
    public static String getUniquePageIdentifier(Page listPage) {
        String uniqueBase = listPage.getPath().substring(1).replace(FileSystem.SEPARATOR, "-");
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] md5Arr = digest.digest(uniqueBase.getBytes("UTF-8"));
            BigInteger bigInt = new BigInteger(1, md5Arr);
            return bigInt.toString(16);
        } catch (UnsupportedEncodingException ueEx) {
            LOGGER.warn("Unable to get UTF-8 version of pagepath");
            return uniqueBase;
        } catch (NoSuchAlgorithmException nsaEx) { /* they be watchin' */
            LOGGER.warn("No MD5 algorithm found, cannot hash");
            return uniqueBase;
        }
    }

    /**
     * Get the description of the feed from the page properties, otherwise retrieve
     * the jcr's description value. Null is returned when no description was found
     *
     * @param page       the page to interrogage
     * @param properties the properties to fall back on
     * @return the description or null when none was found.
     */
    public static String getFeedDescription(Page page, ValueMap properties) {
        if (StringUtils.isEmpty(page.getDescription())) {
            return properties.get("jcr:description", (String) null);
        } else {
            return page.getDescription();
        }
    }


    /**
     * Find the summary field in a 'detail' component or just return the page description.
     * @param page is the page to investiage
     * @return page description
     */
    public static String getPageDescription(Page page) {
        String pageDescription = page.getDescription();

        try {

            Resource pageResource = page.getContentResource();

            if (pageResource != null) {
                String[] listLookForDetailComponent = CommonUtil.DEFAULT_LIST_DETAILS_SUFFIX;

                String detailsPath = CommonUtil.findComponentInPage(page, listLookForDetailComponent);

                ResourceResolver resourceResolver = pageResource.getResourceResolver();

                if (resourceResolver != null) {
                    Resource detailsResource = resourceResolver.resolve(detailsPath);
                    if (detailsResource != null) {
                        Node detailsNode = detailsResource.adaptTo(Node.class);
                        if (detailsNode != null) {
                            if (detailsNode.hasProperty(DETAILS_DESCRIPTION)) {
                                return detailsNode.getProperty(DETAILS_DESCRIPTION).getString();
                            } else {
//                                LOGGER.error("getPageDescription: detailsNode does not have {} in page [{}]", DETAILS_DESCRIPTION, page.getPath());
                            }
                        } else {
//                            LOGGER.error("getPageDescription: detailsNode is null in page [{}]", page.getPath());
                        }
                    } else {
//                        LOGGER.error("getPageDescription: detailsResource is null in page [{}]", page.getPath());
                    }

                } else {
//                    LOGGER.error("getPageDescription: resourceResolver is null in page [{}]", page.getPath());
                }

            } else {
//                LOGGER.error("getPageDescription: pageResource is null in page [{}]", page.getPath());
            }

        } catch (Exception ex) {
            LOGGER.error("getPageDescription:" + ex.toString());
        }
        return pageDescription;
    }


    /**
     * Find the field in a 'detail' component or just return the page description.
     * @param page is the page to investiage
     * @return page description
     */
    public static String getPageDetailsField(Page page, String field) throws RepositoryException {
        String defaultPageValue = StringUtils.EMPTY;
        if (DETAILS_TITLE.equals(field)) {
            defaultPageValue = page.getTitle();
        } else if (DETAILS_DESCRIPTION.equals(field)) {
            defaultPageValue = page.getDescription();
        }

        Resource parsysR = page.getContentResource("./article/par");
        if (parsysR != null) {
            Node parsysN = parsysR.adaptTo(Node.class);
            NodeIterator nodeIt = parsysN.getNodes();
            while (nodeIt.hasNext()) {
                Node child = nodeIt.nextNode();
                String childName = child.getName().toLowerCase();
                if (childName.endsWith(CommonUtil.COMPONENT_DETAILS_SUFFIX)) {
                    return CommonUtil.getPropertyWithDefault(child, field, defaultPageValue);
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
    public static String formattedRssDate(Calendar cal) {
        if (cal == null) {
            return null;
        }
        return DateFormatUtils.format(cal, DEFAULT_RSS_DATE_FORMAT);
    }


    /**
     * Find a page which has a Detail Node. The pattern under jcr:content is par/*Details.
     * @param page page to search
     * @return found node
     * @throws RepositoryException
     */
    public static Node findDetailNode(Page page) throws RepositoryException {

        Node node = null;

        Resource pageRes = page.getContentResource(NODE_PAR);

        if (pageRes != null) {

            Node parNode = pageRes.adaptTo(Node.class);

            NodeIterator detailNodes = parNode.getNodes(NODE_DETAILS);

            while (detailNodes.hasNext()) {

                node = detailNodes.nextNode();
                break;
            }


        } else {
            //  LOGGER.error("Node ["+NODE_PAR+"] not find in " + page.getPath());
        }
        return node;
    }


    /***
     * compile a message from component properties using one of the component format tag fields
     * @param formatFieldName field with format path
     * @param defaultFormat default format template
     * @param componentProperties component properties
     * @param sling sling helper
     * @return formatted string
     */
    public static String compileComponentMessage(String formatFieldName, String defaultFormat, ComponentProperties componentProperties, SlingScriptHelper sling) {

        if (componentProperties == null || sling == null) {
            return StringUtils.EMPTY;
        }


        String formatFieldTagPath = componentProperties.get(formatFieldName, StringUtils.EMPTY);

        //set default
        String fieldFormatValue = defaultFormat;

        if (isNotEmpty(formatFieldTagPath)) {
            fieldFormatValue = TagUtil.getTagValueAsAdmin(formatFieldTagPath, sling);
        }

        //LOGGER.error("compileComponentMessage {}",fieldFormatValue);

        return CommonUtil.compileMapMessage(fieldFormatValue, componentProperties);

    }

    /**
     * get path of resource in jcr:content.
     * @param resource resource to use
     * @return content path to resource
     */
    public static String getResourceContentPath(Resource resource) {
        String returnPath = resource.getPath(); //StringUtils.split(resource.getPath(),JcrConstants.JCR_CONTENT)

        if (StringUtils.contains(returnPath, org.apache.jackrabbit.JcrConstants.JCR_CONTENT)) {
            String[] pathParts = StringUtils.splitByWholeSeparator(returnPath, org.apache.jackrabbit.JcrConstants.JCR_CONTENT);
            if (pathParts.length > 1) {
                returnPath = pathParts[1];
            }
        }

        return returnPath;
    }


    /***
     * encode a string using MD5.
     * @param encode string to encode
     * @return encoded string
     */
    public static String MD5(String encode) {
        try {

            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(encode.getBytes());

            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < digest.length; ++i) {
                sb.append(Integer.toHexString((digest[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();

        } catch (Exception ex) {
            LOGGER.error("MD5 could not encode string ({0})", ex.toString());
        }

        return encode;
    }


    /***
     * get or generate component id.
     * @param componentNode component node
     * @return component id
     */
    public static String getComponentId(Node componentNode) {

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
                prefix = prefix.replaceAll("[^a-zA-Z0-9-_]", "_");

                componentNode.setProperty(FIELD_STYLE_COMPONENT_ID,
                        format(
                                "{0}_{1}",
                                prefix,
                                RandomStringUtils.randomAlphanumeric(9).toUpperCase())
                );

                componentNode.getSession().save();
            }

            componentId = CommonUtil.getProperty(componentNode, FIELD_STYLE_COMPONENT_ID);

        } catch (Exception ex) {
            LOGGER.error("Could not get id for component path={},id={},error {}", path, componentId, ex.toString());
        }

        return componentId;
    }


    public static String getCloudConfigProperty(InheritanceValueMap pageProperties, String configurationName, String propertyName, SlingScriptHelper sling) {
        String returnValue = "";

        //TODO: move this admin session usage into function
        ResourceResolver adminResourceResolver = SecurityUtil.openAdminResourceResolver(sling);

        try {

            // Getting attached facebook cloud service config in order to fetch appID
            ConfigurationManager cfgMgr = adminResourceResolver.adaptTo(ConfigurationManager.class);
            Configuration addthisConfiguration = null;
            String[] services = pageProperties.getInherited(ConfigurationConstants.PN_CONFIGURATIONS, new String[]{});
            if (cfgMgr != null) {
                addthisConfiguration = cfgMgr.getConfiguration(configurationName, services);
                if (addthisConfiguration != null) {
                    returnValue = addthisConfiguration.get(propertyName, "");
                }
            }


        } catch (Exception ex) {
            LOGGER.error(Throwables.getStackTraceAsString(ex));
        } finally {
            SecurityUtil.closeAdminResourceResolver(adminResourceResolver);
        }

        return returnValue;
    }


    /***
     * find an ancestor resource matching current resource.
     * @param page page to use
     * @param componentContext component context
     * @return found resource
     */
    public static Resource findInheritedResource(Page page, ComponentContext componentContext) {
        final String pageResourcePath = page.getContentResource().getPath(); // assume that page have resource
        final Resource thisResource = componentContext.getResource();
        final String nodeResourceType = thisResource.getResourceType();
        final String relativePath = thisResource.getPath().replaceFirst(pageResourcePath.concat(FileSystem.SEPARATOR), "");

        // defn of a parent node
        // 1. is from parent page
        // 2. same sling resource type
        // 3. same relative path

        Page curPage = page.getParent();
        Resource curResource = null;
        Boolean curResourceTypeMatch = false;
        Boolean curCancelInheritParent = false;
        ValueMap curProperties = null;

        try {
            while (null != curPage) {
                // find by same relative path

                String error = format(
                        "findInheritedResource: looking for inherited resource for path=\"{0}\" by relative path=\"{1}\" in parent=\"{2}\""
                        , pageResourcePath, relativePath, curPage.getPath());
                LOGGER.info(error);

                try {
                    curResource = curPage.getContentResource(relativePath);
                } catch (Exception e) {
                    LOGGER.info("Failed to get  " + relativePath + " from " + curPage.getContentResource().getPath());
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
                                , pageResourcePath, relativePath, curPage.getPath());
                        LOGGER.info(found);

                        break;
                    } else {
                        String notfound = format(
                                "findInheritedResource: NOT FOUND looking for inherited resource for path=\"{0}\" by relative path=\"{1}\" in parent=\"{2}\""
                                , pageResourcePath, relativePath, curPage.getPath());
                        LOGGER.info(notfound);

                    }
                }

                curPage = curPage.getParent();
            }
        } catch (Exception ex) {
            LOGGER.warn("Failed to find inherited resource. {}", ex);
        }

        return curResource;
    }

    /***
     * get attribute from first item list of maps.
     * @param sourceMap map to use
     * @return value of attribute from first found element
     */
    public static String getFirstAttributeFromList(LinkedHashMap<String, Map> sourceMap, String attributeName) {
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
     * get value attribute from list of maps.
     * @param sourceMap map to use
     * @return value of first element
     */
    public static String getFirstValueFromList(LinkedHashMap<String, Map> sourceMap) {
        return getFirstAttributeFromList(sourceMap, "value");
    }


    public static String getComponentInPagePath(Node componentNode) {
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
                        componentInPagePath = componentInPagePath.replaceFirst(FileSystem.SEPARATOR, "");
                    }
                }
            }
        } catch (Exception ex) {

        }
        return componentInPagePath;
    }


    /**
     * create a map of component fields matched to Dialog Title and Description
     * @param componentResource
     * @param resourceResolver
     * @param slingScriptHelper
     * @return
     */
    public static Map<String, Object> getComponentFieldsAndDialogMap(Resource componentResource , ResourceResolver resourceResolver, SlingScriptHelper slingScriptHelper) {
        Map<String, Object> firstComponentConfig = new HashMap<>();

        ValueMap firstComponentMap = componentResource.adaptTo(ValueMap.class);
        if (firstComponentMap != null) {


            Resource firstComponentR = resourceResolver.getResource(componentResource.getResourceType());

            if (firstComponentR != null) {
                Component firstComponentComp = firstComponentR.adaptTo(Component.class);
                Component firstComponentCompSuper = firstComponentComp.getSuperComponent();

                if (firstComponentComp != null) {
                    String dialogPath = firstComponentComp.getPath() + "/cq:dialog/content.html/" + componentResource.toString();
                    if (firstComponentCompSuper != null) {
                        dialogPath = firstComponentCompSuper.getPath() + "/cq:dialog/content.html/" + componentResource.toString();
                    }

                    String dialogHTML = resourceRenderAsHtml(
                            dialogPath,
                            resourceResolver,
                            slingScriptHelper,
                            WCMMode.DISABLED,
                            null,
                            null);

                    Document dialogContent = Jsoup.parse(dialogHTML);

                    for (Map.Entry<String, Object> field : firstComponentMap.entrySet()) {

                        String name = field.getKey();
                        Object value = field.getValue();

                        Map<String, Object> row = new HashMap<>();
                        row.put("value", value);
                        row.put("fieldDescription", "");
                        row.put("fieldLabel", "");
                        row.put("type", "");

                        if (isNotEmpty(dialogPath)) {
                            String fieldSelection = "[name='./" + name + "']";



                            Element htmlSection = dialogContent.selectFirst(".coral-Form-fieldwrapper:has("+fieldSelection+")");

                            Element fieldElement = dialogContent.selectFirst(fieldSelection);

                            if (fieldElement != null) {
                                Object[] classNames = fieldElement.classNames().toArray();
                                if (classNames.length > 0 ) {
                                    row.put("type", classNames[classNames.length - 1]);
                                } else {
                                    row.put("type", fieldElement.tagName());
                                }
                            }

                            if (htmlSection != null) {

                                Element fieldLabel = htmlSection.selectFirst(".coral-Form-fieldlabel");
                                Element fieldDescription = htmlSection.selectFirst(".coral-Form-fieldinfo");
                                Element fieldTooltip = htmlSection.selectFirst("coral-tooltip-content");

                                if (fieldLabel != null) {
                                    String fieldLabelText = fieldLabel.text();
                                    row.put("fieldLabel", fieldLabelText);
                                }

                                String fieldDescriptionString = "";

                                if (fieldDescription != null) {
                                    fieldDescriptionString = fieldDescription.attr("data-quicktip-content");
                                }
                                if (fieldTooltip != null) {
                                    fieldDescriptionString = fieldTooltip.text();
                                }

                                row.put("fieldDescription", fieldDescriptionString);


                            }

                        }

                        firstComponentConfig.put(name, row);
                    }
                }
            }
        }

        return firstComponentConfig;

    }
}
