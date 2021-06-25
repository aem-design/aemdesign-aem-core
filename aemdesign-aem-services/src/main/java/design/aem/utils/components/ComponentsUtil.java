package design.aem.utils.components;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMMode;
import com.day.cq.wcm.api.components.Component;
import com.day.cq.wcm.api.components.ComponentContext;
import com.day.cq.wcm.api.components.ComponentManager;
import com.day.cq.wcm.api.designer.Design;
import com.day.cq.wcm.api.designer.Style;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;
import com.day.cq.wcm.webservicesupport.Configuration;
import com.day.cq.wcm.webservicesupport.ConfigurationConstants;
import com.day.cq.wcm.webservicesupport.ConfigurationManager;
import com.google.common.base.Throwables;
import design.aem.components.AttrBuilder;
import design.aem.components.ComponentField;
import design.aem.components.ComponentProperties;
import design.aem.models.GenericModel;
import design.aem.services.ContentAccess;
import org.apache.commons.io.IOUtils;
import org.apache.commons.jexl3.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.jackrabbit.core.fs.FileSystem;
import org.apache.jackrabbit.vault.util.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.apache.sling.xss.XSSAPI;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.jcr.Node;
import javax.servlet.jsp.PageContext;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static design.aem.components.ComponentField.FIELD_VALUES_ARE_ATTRIBUTES;
import static design.aem.utils.components.CommonUtil.*;
import static design.aem.utils.components.ConstantsUtil.*;
import static design.aem.utils.components.TagUtil.getTag;
import static java.text.MessageFormat.format;
import static org.apache.commons.lang3.StringUtils.*;

public class ComponentsUtil {

    public static final Logger LOGGER = LoggerFactory.getLogger(ComponentsUtil.class);

    public static final String DEFAULT_PATH_TAGS = "/content/cq:tags";

    public static final String DEFAULT_TENANT = "aemdesign";

    public static final String FIELD_VARIANT = "variant";
    public static final String FIELD_VARIANT_LEGACY = "legacyVariant"; // specify that variant field is derived from config / tag
    public static final String DEFAULT_VARIANT = "default";
    public static final String DEFAULT_VARIANT_TEMPLATE = "variant.default.html";
    public static final String DEFAULT_BADGE = "default";

    public static final String DEFAULT_ARIA_ROLE_ATTRIBUTE = "role";

    public static final String FIELD_REDIRECT_TARGET = "redirectTarget";

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
    public static final String DETAILS_CARD_ICONSHOW = "cardIconShow";
    public static final String DETAILS_CARD_ICON = "cardIcon";
    public static final String DETAILS_CARD_ADDITIONAL = "cardAdditional";

    //shared badge config passed from list to all badge elements
    public static final String DETAILS_LINK_TARGET = "badgeLinkTarget";
    public static final String DETAILS_LINK_TEXT = "badgeLinkText";
    public static final String DETAILS_LINK_TITLE = "badgeLinkTitle";
    public static final String DETAILS_LINK_STYLE = "badgeLinkStyle";
    public static final String DETAILS_LINK_FORMATTED = "badgeLinkFormatted";
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

    //badge custom badge
    public static final String DETAILS_BADGE_FIELDS_TEMPLATE = "badgeFieldsTemplate";
    public static final String DETAILS_BADGE_FIELDS = "badgeFields";
    public static final String DETAILS_BADGE_TEMPLATE = "badgeTemplate";
    public static final String DETAILS_BADGE_CUSTOM = "badgeCustom";

    //variant templates
    public static final String FIELD_VARIANT_FIELDS = "variantFields";
    public static final String FIELD_VARIANT_FIELDS_TEMPLATE = "variantFieldsTemplate";

    //config attributes for templates used for both custom badges and variants
    public static final String FIELD_TAG_TEMPLATE_CONFIG_TEMPLATES = "templates";
    public static final String FIELD_TAG_TEMPLATE_CONFIG_FIELDS = "fields";
    public static final String FIELD_TAG_TEMPLATE_CONFIG_VALUE = "value";
    public static final String FIELD_TAG_TEMPLATE_CONFIG_VALUEALT = "valuealt"; //used in dropdowns to show same options in dialogs

    //badge analytics
    public static final String DETAILS_BADGE_ANALYTICS_EVENT_TYPE = "badgeAnalyticsEventType"; //NOSONAR
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
    public static final String DETAILS_ANALYTICS_VARIANT = "analyticsVariant";
    public static final String DETAILS_DATA_ANALYTICS_TRACK = "data-layer-track";
    public static final String DETAILS_DATA_ANALYTICS_LOCATION = "data-layer-location";
    public static final String DETAILS_DATA_ANALYTICS_LABEL = "data-layer-label";

    //schema data
    public static final String DETAILS_DATA_SCHEMA_ITEMSCOPE = "itemscope";
    public static final String DETAILS_DATA_SCHEMA_ITEMTYPE = "itemtype";
    public static final String DETAILS_DATA_SCHEMA_ITEMTYPE_DEFAULT = "https://schema.org/Thing";

    public static final String FIELD_HIDEINMENU = "hideInMenu";


    public static final Pattern DEFAULT_RENDTION_PATTERN_OOTB = Pattern.compile("cq5dam\\.(.*)?\\.(\\d+)\\.(\\d+)\\.(.*)"); //NOSONAR its safe
    public static final String DEFAULT_ASSET_RENDITION_PREFIX1 = "cq5dam.thumbnail.";
    public static final String DEFAULT_ASSET_RENDITION_PREFIX2 = "cq5dam.web.";

    public static final String DEFAULT_IMAGE_GENERATED_FORMAT = "{0}.img.jpeg/{1}.jpeg";

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
    public static final String COMPONENT_BADGE_DEFAULT_TEMPLATE_FORMAT = "badge.{0}.html";



    public static final String COMPONENT_CANCEL_INHERIT_PARENT = "cancelInheritParent";

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

    public static final String FIELD_DATA_ANALYTICS_TYPE = "data-analytics-type"; //NOSONAR
    public static final String FIELD_DATA_ANALYTICS_HIT_TYPE = "data-analytics-hit-type";
    public static final String FIELD_DATA_ANALYTICS_METATYPE = "data-analytics-metatype";
    public static final String FIELD_DATA_ANALYTICS_FILENAME = "data-analytics-filename";
    public static final String FIELD_DATA_ANALYTICS_EVENT_CATEGORY = "data-analytics-event-category"; //NOSONAR
    public static final String FIELD_DATA_ANALYTICS_EVENT_ACTION = "data-analytics-event-action"; //NOSONAR
    public static final String FIELD_DATA_ANALYTICS_EVENT_LABEL = "data-analytics-event-label";
    public static final String FIELD_DATA_ANALYTICS_TRANSPORT = "data-analytics-transport"; //NOSONAR
    public static final String FIELD_DATA_ANALYTICS_NONINTERACTIVE = "data-analytics-noninteraction"; //NOSONAR

    public static final String FIELD_DATA_ARRAY_SEPARATOR = ",";
    public static final String FIELD_DATA_TAG_SEPARATOR = " ";

    public static final String FIELD_HREF = "href"; //NOSONAR
    public static final String FIELD_TITLE_TAG_TYPE = "titleType";
    public static final String FIELD_HIDE_TITLE = "hideTitle";
    public static final String FIELD_HIDE_DESCRIPTION = "hideDescription";
    public static final String FIELD_SHOW_BREADCRUMB = "showBreadcrumb";
    public static final String FIELD_SHOW_TOOLBAR = "showToolbar";
    public static final String FIELD_SHOW_PAGEDATE = "showPageDate";
    public static final String FIELD_SHOW_PARSYS = "showParsys";

    public static final String FIELD_BADGE_PAGE = "badgePage"; //NOSONAR

    public static final String JCR_NAME_SEPARATOR = "_"; //NOSONAR

    public static final String FIELD_LICENSE_INFO = "licenseInfo";
    public static final String FIELD_ASSETID = "asset-id";
    public static final String FIELD_ASSET_LICENSED = "asset-licensed";
    public static final String FIELD_ASSET_TRACKABLE = "asset-trackable";
    public static final String FIELD_DATA_ASSET_PRIMARY_ID = "data-asset-primary-id";
    public static final String FIELD_DATA_ASSET_PRIMARY_LICENSE = "data-asset-primary-license";
    public static final String FIELD_DATA_ASSET_SECONDARY_ID = "data-asset-secondary-id";
    public static final String FIELD_DATA_ASSET_SECONDARY_LICENSE= "data-asset-secondary-license";


    public static final String FIELD_DATA_ASSET_SECONDARY_SRC = "data-asset-secondary-src";
    public static final String FIELD_DATA_ASSET_SECONDARY_CLASS = "secondary";


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
    public static final String PAGECONTEXTMAP_OBJECT_XSSAPI = "xssAPI";


    public static final String DETAILS_SELECTOR_BADGE = "badge";

    private static final String STRING_EXPRESSION_CHECK = ".*(\\$\\{.*?\\}).*";

    /**
     * Component Style
     *
     * Structure:
     * 1 required - property name,
     * 2 required - default value,
     * 3 optional - name of component attribute to add value into
     * 4 optional - canonical name of class for handling multivalues, String or Tag
     */
    public static final Object[][] DEFAULT_FIELDS_STYLE = { //NOSONAR used by classes
            {FIELD_STYLE_COMPONENT_ID, StringUtils.EMPTY, "id"},
            {FIELD_STYLE_COMPONENT_THEME, new String[]{}, "class", Tag.class.getCanonicalName()},
            {FIELD_STYLE_COMPONENT_MODIFIERS, new String[]{}, "class", Tag.class.getCanonicalName()},
            {FIELD_STYLE_COMPONENT_MODULES, new String[]{}, "data-modules", Tag.class.getCanonicalName()},
            {FIELD_STYLE_COMPONENT_CHEVRON, new String[]{}, "class", Tag.class.getCanonicalName()},
            {FIELD_STYLE_COMPONENT_ICON, new String[]{}, "class", Tag.class.getCanonicalName()},
            {FIELD_STYLE_COMPONENT_POSITIONX, StringUtils.EMPTY, "x"},
            {FIELD_STYLE_COMPONENT_POSITIONY, StringUtils.EMPTY, "y"},
            {FIELD_STYLE_COMPONENT_WIDTH, "${value ? 'width:' + value + 'px;' : ''}", "style"},
            {FIELD_STYLE_COMPONENT_HEIGHT, "${value ? 'height:' + value + 'px;' : ''}", "style"},
            {FIELD_STYLE_COMPONENT_SITETHEMECATEGORY, StringUtils.EMPTY},
            {FIELD_STYLE_COMPONENT_SITETHEMECOLOR, StringUtils.EMPTY},
            {FIELD_STYLE_COMPONENT_SITETITLECOLOR, StringUtils.EMPTY},
            {FIELD_STYLE_COMPONENT_BOOLEANATTR, new String[]{}, FIELD_VALUES_ARE_ATTRIBUTES, Tag.class.getCanonicalName()},
    };

    /**
     * Component Accessibility
     *
     * Structure:
     * 1 required - property name,
     * 2 required - default value,
     * 3 optional - name of component attribute to add value into
     * 4 optional - canonical name of class for handling multivalues, String or Tag
     */
    public static final Object[][] DEFAULT_FIELDS_ACCESSIBILITY = { //NOSONAR used by classes
            {FIELD_ARIA_ROLE, StringUtils.EMPTY, DEFAULT_ARIA_ROLE_ATTRIBUTE},
            {FIELD_ARIA_LABEL, StringUtils.EMPTY, "aria-label"},
            {FIELD_ARIA_DESCRIBEDBY, StringUtils.EMPTY, "aria-describedby"},
            {FIELD_ARIA_LABELLEDBY, StringUtils.EMPTY, "aria-labelledby"},
            {FIELD_ARIA_CONTROLS, StringUtils.EMPTY, "aria-controls"},
            {FIELD_ARIA_LIVE, StringUtils.EMPTY, "aria-live"},
            {FIELD_ARIA_HIDDEN, StringUtils.EMPTY, "aria-hidden"},
            {FIELD_ARIA_HASPOPUP, StringUtils.EMPTY, "aria-haspopup"},
            {FIELD_ARIA_ACCESSKEY, StringUtils.EMPTY, "accesskey"},
    };

    /**
     * Default Node Metadata
     *
     * Structure:
     * 1 required - property name,
     * 2 required - default value,
     * 3 optional - name of component attribute to add value into
     * 4 optional - canonical name of class for handling multivalues, String or Tag
     */
    public static final Object[][] DEFAULT_FIELDS_METADATA = { //NOSONAR used by classes
            {"metadataContentType", StringUtils.EMPTY},
            {NameConstants.PN_PAGE_LAST_MOD, StringUtils.EMPTY},
            {JcrConstants.JCR_LASTMODIFIED, StringUtils.EMPTY},
            {JcrConstants.JCR_CREATED, StringUtils.EMPTY},
            {NameConstants.PN_PAGE_LAST_REPLICATED, StringUtils.EMPTY},
    };

    /**
     * Badge Metadata used by Details Components
     *
     * Structure:
     * 1 required - property name,
     * 2 required - default value,
     * 3 optional - name of component attribute to add value into
     * 4 optional - canonical name of class for handling multivalues, String or Tag
     */
    public static final Object[][] DEFAULT_FIELDS_DETAILS_OPTIONS = { //NOSONAR used by classes
            {DETAILS_MENU_COLOR, StringUtils.EMPTY},
            {DETAILS_MENU_ICONSHOW, false},
            {DETAILS_MENU_ICON, new String[]{}, StringUtils.EMPTY, Tag.class.getCanonicalName()},
            {DETAILS_MENU_ACCESS_KEY, StringUtils.EMPTY},
            {DETAILS_CARD_STYLE, new String[]{}, StringUtils.EMPTY, Tag.class.getCanonicalName()},
            {DETAILS_CARD_ICONSHOW, false},
            {DETAILS_CARD_ICON, new String[]{}, StringUtils.EMPTY, Tag.class.getCanonicalName()},
            {DETAILS_LINK_TARGET, "_blank"},
            {DETAILS_LINK_TEXT, "${value ? value : (" + FIELD_PAGE_TITLE_NAV + " ? " + FIELD_PAGE_TITLE_NAV + " : " + FIELD_PAGE_TITLE + ")}"},
            {DETAILS_LINK_TITLE, "${value ? value : (" + FIELD_PAGE_TITLE + " ? " + FIELD_PAGE_TITLE + " : "+ FIELD_RESOURCE_NAME + ")}"},
            {DETAILS_LINK_STYLE, new String[]{}, StringUtils.EMPTY, Tag.class.getCanonicalName()},
            {DETAILS_LINK_FORMATTED, "${value ? value : pageUrl}", StringUtils.EMPTY, Tag.class.getCanonicalName()},
            {DETAILS_TITLE_TRIM, false},
            {DETAILS_TITLE_TRIM_LENGTH_MAX, ConstantsUtil.DEFAULT_SUMMARY_TRIM_LENGTH},
            {DETAILS_TITLE_TRIM_LENGTH_MAX_SUFFIX, ConstantsUtil.DEFAULT_SUMMARY_TRIM_SUFFIX},
            {DETAILS_SUMMARY_TRIM, false},
            {DETAILS_SUMMARY_TRIM_LENGTH_MAX, ConstantsUtil.DEFAULT_SUMMARY_TRIM_LENGTH},
            {DETAILS_SUMMARY_TRIM_LENGTH_MAX_SUFFIX, ConstantsUtil.DEFAULT_SUMMARY_TRIM_SUFFIX},
            {DETAILS_TAB_ICONSHOW, false},
            {DETAILS_TAB_ICON, new String[]{}, StringUtils.EMPTY, Tag.class.getCanonicalName()},
            {DETAILS_TITLE_ICONSHOW, false},
            {DETAILS_TITLE_ICON, new String[]{}, StringUtils.EMPTY, Tag.class.getCanonicalName()},
            {DETAILS_OVERLAY_ICONSHOW, false},
            {DETAILS_OVERLAY_ICON, new String[]{}, StringUtils.EMPTY, Tag.class.getCanonicalName()},
            {DETAILS_THUMBNAIL_WIDTH, ConstantsUtil.DEFAULT_THUMB_WIDTH_SM},
            {DETAILS_THUMBNAIL_HEIGHT, StringUtils.EMPTY},
            {DETAILS_THUMBNAIL_TYPE, ConstantsUtil.IMAGE_OPTION_RENDITION},
            {DETAILS_TITLE_TAG_TYPE, ConstantsUtil.DEFAULT_TITLE_TAG_TYPE_BADGE},
            {DETAILS_THUMBNAIL_ID, StringUtils.EMPTY},
            {DETAILS_THUMBNAIL_LICENSE_INFO, StringUtils.EMPTY},
            {DETAILS_THUMBNAIL, StringUtils.EMPTY},
            {DETAILS_BADGE_ANALYTICS_TRACK, StringUtils.EMPTY},
            {DETAILS_BADGE_ANALYTICS_LOCATION, StringUtils.EMPTY},
            {DETAILS_BADGE_ANALYTICS_LABEL, "${value ?  value : " + DETAILS_LINK_TEXT + "}"},
            {DETAILS_PAGE_METADATA_PROPERTY, new String[]{}},
            {DETAILS_PAGE_METADATA_PROPERTY_CONTENT, new String[]{}},
            {DETAILS_BADGE_CUSTOM, false},
            {DETAILS_BADGE_FIELDS_TEMPLATE, new String[]{}},
            {DETAILS_BADGE_FIELDS, new String[]{}},
            {DETAILS_BADGE_TEMPLATE, StringUtils.EMPTY}, //after DETAILS_BADGE_FIELDS_TEMPLATE and DETAILS_BADGE_FIELDS as it will override them
    };

    /**
     * Badge Metadata used in Lists
     *
     * Structure:
     * 1 required - property name,
     * 2 required - default value,
     * 3 optional - name of component attribute to add value into
     * 4 optional - canonical name of class for handling multivalues, String or Tag
     */
    public static final Object[][] DEFAULT_FIELDS_DETAILS_OPTIONS_OVERRIDE = { //NOSONAR used by classes
            {DETAILS_MENU_COLOR, StringUtils.EMPTY},
            {DETAILS_MENU_ICONSHOW, StringUtils.EMPTY},
            {DETAILS_MENU_ICON, new String[]{}, StringUtils.EMPTY, Tag.class.getCanonicalName()},
            {DETAILS_MENU_ACCESS_KEY, StringUtils.EMPTY},
            {DETAILS_CARD_STYLE, new String[]{}, StringUtils.EMPTY, Tag.class.getCanonicalName()},
            {DETAILS_CARD_ICONSHOW, StringUtils.EMPTY},
            {DETAILS_CARD_ICON, new String[]{}, StringUtils.EMPTY, Tag.class.getCanonicalName()},
            {DETAILS_LINK_TARGET, StringUtils.EMPTY},
            {DETAILS_LINK_TEXT, StringUtils.EMPTY},
            {DETAILS_LINK_TITLE, StringUtils.EMPTY},
            {DETAILS_LINK_STYLE, new String[]{}, StringUtils.EMPTY, Tag.class.getCanonicalName()},
            {DETAILS_LINK_FORMATTED, new String[]{}, StringUtils.EMPTY, Tag.class.getCanonicalName()},
            {DETAILS_TITLE_TRIM, StringUtils.EMPTY},
            {DETAILS_TITLE_TRIM_LENGTH_MAX, DETAILS_TITLE_TRIM_LENGTH_MAX_DEFAULT},
            {DETAILS_TITLE_TRIM_LENGTH_MAX_SUFFIX, DETAILS_TITLE_TRIM_LENGTH_MAX_SUFFIX_DEFAULT},
            {DETAILS_SUMMARY_TRIM, StringUtils.EMPTY},
            {DETAILS_SUMMARY_TRIM_LENGTH_MAX, DETAILS_SUMMARY_TRIM_LENGTH_MAX_DEFAULT},
            {DETAILS_SUMMARY_TRIM_LENGTH_MAX_SUFFIX, DETAILS_SUMMARY_TRIM_LENGTH_MAX_SUFFIX_DEFAULT},
            {DETAILS_TAB_ICONSHOW, StringUtils.EMPTY},
            {DETAILS_TAB_ICON, new String[]{}, StringUtils.EMPTY, Tag.class.getCanonicalName()},
            {DETAILS_TITLE_ICONSHOW, StringUtils.EMPTY},
            {DETAILS_TITLE_ICON, new String[]{}, StringUtils.EMPTY, Tag.class.getCanonicalName()},
            {DETAILS_OVERLAY_ICONSHOW, StringUtils.EMPTY},
            {DETAILS_OVERLAY_ICON, new String[]{}, StringUtils.EMPTY, Tag.class.getCanonicalName()},
            {DETAILS_THUMBNAIL_WIDTH, StringUtils.EMPTY},
            {DETAILS_THUMBNAIL_HEIGHT, StringUtils.EMPTY},
            {DETAILS_THUMBNAIL_TYPE, StringUtils.EMPTY},
            {DETAILS_TITLE_TAG_TYPE, StringUtils.EMPTY},
            {DETAILS_THUMBNAIL_ID, StringUtils.EMPTY},
            {DETAILS_THUMBNAIL_LICENSE_INFO, StringUtils.EMPTY},
            {DETAILS_THUMBNAIL, StringUtils.EMPTY},
            {DETAILS_BADGE_ANALYTICS_TRACK, StringUtils.EMPTY}, //basic
            {DETAILS_BADGE_ANALYTICS_LOCATION, StringUtils.EMPTY}, //basic
            {DETAILS_BADGE_ANALYTICS_LABEL, StringUtils.EMPTY}, //basic
            {DETAILS_BADGE_CUSTOM, false},
            {DETAILS_BADGE_FIELDS_TEMPLATE, new String[]{}},
            {DETAILS_BADGE_FIELDS, new String[]{}},
            {DETAILS_BADGE_TEMPLATE, StringUtils.EMPTY}, //after DETAILS_BADGE_FIELDS_TEMPLATE and DETAILS_BADGE_FIELDS as it will override them

    };

    /**
     * Component Analytics
     *
     * Structure:
     * 1 required - property name,
     * 2 required - default value,
     * 3 optional - name of component attribute to add value into
     * 4 optional - canonical name of class for handling multivalues, String or Tag
     */
    public static final Object[][] DEFAULT_FIELDS_ANALYTICS = { //NOSONAR used by classes
            {DETAILS_ANALYTICS_TRACK, false, DETAILS_DATA_ANALYTICS_TRACK}, //basic
            {DETAILS_ANALYTICS_LOCATION, StringUtils.EMPTY, DETAILS_DATA_ANALYTICS_LOCATION}, //basic
            {DETAILS_ANALYTICS_LABEL, "${ value ? value : " + FIELD_PAGE_TITLE + " }", DETAILS_DATA_ANALYTICS_LABEL}, //basic
            {"analyticsEventType", StringUtils.EMPTY, "data-analytics-event"}, //advanced
            {"analyticsHitType", StringUtils.EMPTY, "data-analytics-hit-type"}, //advanced
            {"analyticsEventCategory", StringUtils.EMPTY, "data-analytics-event-category"}, //advanced
            {"analyticsEventAction", StringUtils.EMPTY, "data-analytics-event-action"}, //advanced
            {"analyticsEventLabel", StringUtils.EMPTY, "data-analytics-event-label"}, //advanced
            {"analyticsTransport", StringUtils.EMPTY, "data-analytics-transport"}, //advanced
            {"analyticsNonInteraction", StringUtils.EMPTY, "data-analytics-noninteraction"}, //advanced
    };

    /**
     * Link Attributes
     *
     * Structure:
     * 1 required - property name,
     * 2 required - default value,
     * 3 optional - name of component attribute to add value into
     * 4 optional - canonical name of class for handling multivalues, String or Tag
     */
    public static final Object[][] DEFAULT_FIELDS_ATTRIBUTES = { //NOSONAR used by classes
            {"dataType", StringUtils.EMPTY, "type"},
            {"dataTarget", StringUtils.EMPTY, "data-target"},
            {"dataToggle", StringUtils.EMPTY, "data-toggle"},
    };

    /**
     * Asset Metadata
     *
     * Structure:
     * 1 required - property name,
     * 2 required - default value,
     * 3 optional - name of component attribute to add value into
     * 4 optional - canonical name of class for handling multivalues, String or Tag
     */
    public static final Object[][] DEFAULT_FIELDS_ASSET = { //NOSONAR used by classes
            {CommonUtil.DAM_TITLE, "${ value ? value : name }", "data-title"},
            {CommonUtil.DAM_DESCRIPTION, StringUtils.EMPTY, "data-description"},
            {CommonUtil.DAM_CREDIT, StringUtils.EMPTY, "data-credit"},
            {CommonUtil.DAM_HEADLINE, StringUtils.EMPTY, "data-headline"},
            {CommonUtil.DAM_SOURCE, StringUtils.EMPTY, "data-source"},
            {CommonUtil.DAM_SOURCE_URL, StringUtils.EMPTY, "data-sourceurl"},
            {CommonUtil.DAM_VIDEO_URL, StringUtils.EMPTY, "data-videourl"},
            {DamConstants.TIFF_IMAGEWIDTH, StringUtils.EMPTY, "data-width"},
            {DamConstants.TIFF_IMAGELENGTH, StringUtils.EMPTY, "data-height"},
            {DamConstants.DC_RIGHTS, StringUtils.EMPTY, "data-rights"},
            {DamConstants.DC_CREATOR, StringUtils.EMPTY, "data-creator"},
            {DamConstants.DC_FORMAT, StringUtils.EMPTY},
    };


    /**
     * Asset Image Metadata
     *
     * Structure:
     * 1 required - property name,
     * 2 required - default value,
     * 3 optional - name of component attribute to add value into
     * 4 optional - canonical name of class for handling multivalues, String or Tag
     */
    public static final Object[][] DEFAULT_FIELDS_ASSET_IMAGE = { //NOSONAR used by classes
            {CommonUtil.DAM_TITLE, StringUtils.EMPTY},
            {CommonUtil.DAM_DESCRIPTION, StringUtils.EMPTY},
            {CommonUtil.DAM_CREDIT, StringUtils.EMPTY},
            {CommonUtil.DAM_HEADLINE, StringUtils.EMPTY},
            {CommonUtil.DAM_SOURCE, StringUtils.EMPTY},
            {CommonUtil.DAM_SOURCE_URL, StringUtils.EMPTY},
    };

    /**
     * Asset Video Metadata
     *
     * Structure:
     * 1 required - property name,
     * 2 required - default value,
     * 3 optional - name of component attribute to add value into
     * 4 optional - canonical name of class for handling multivalues, String or Tag
     */
    public static final Object[][] DEFAULT_FIELDS_ASSET_VIDEO = { //NOSONAR used by classes
            {CommonUtil.DAM_TITLE, StringUtils.EMPTY},
            {CommonUtil.DAM_DESCRIPTION, StringUtils.EMPTY},
            {CommonUtil.DAM_HEADLINE, StringUtils.EMPTY},
            {CommonUtil.DAM_CREDIT, StringUtils.EMPTY},
            {CommonUtil.DAM_SOURCE, StringUtils.EMPTY},
            {CommonUtil.DAM_SOURCE_URL, StringUtils.EMPTY},
            {CommonUtil.DAM_VIDEO_URL, StringUtils.EMPTY},
    };


    /**
     * Page Theme Metadata
     *
     * Structure:
     * 1 required - property name,
     * 2 required - default value,
     * 3 optional - name of component attribute to add value into
     * 4 optional - canonical name of class for handling multivalues, String or Tag
     */
    public static final Object[][] DEFAULT_FIELDS_PAGE_THEME = { //NOSONAR used by classes
            {"themeStyle", StringUtils.EMPTY},
            {"faviconsPath", StringUtils.EMPTY},
            {"favicon", StringUtils.EMPTY},
            {"siteThemeColor", StringUtils.EMPTY},
            {"siteTileColor", StringUtils.EMPTY},
    };

    /**
     * Common Component Layout Fields
     *
     * Structure:
     * 1 required - property name,
     * 2 required - default value,
     * 3 optional - name of component attribute to add value into
     * 4 optional - canonical name of class for handling multivalues, String or Tag
     */
    public static final Object[][] DEFAULT_COMMON_COMPONENT_LAYOUT_FIELDS = { //NOSONAR used by classes
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {FIELD_TITLE_TAG_TYPE, StringUtils.EMPTY},
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
     * @param paths paths to check
     * @param separator separator to use
     * @return a string with the file contents
     */
    public static String getResourceContent(ResourceResolver resourceResolver, String[] paths, String separator) {
        String returnValue = StringUtils.EMPTY;

        for (String path : paths) {
            Resource resource = resourceResolver.getResource(path);

            returnValue = returnValue.concat(getResourceContent(resource));

            if (StringUtils.isNotEmpty(separator) && separator != null) {
                returnValue = returnValue.concat(separator);
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
    @SuppressWarnings("squid:S3776")
    public static String getResourceContent(Resource resource) {
        String returnValue = StringUtils.EMPTY;

        if (resource != null) {

            try {
                Node resourceNode = resource.adaptTo(Node.class);

                if (resourceNode != null) {
                    Node contentNode = null;

                    if (resourceNode.getPrimaryNodeType().getName().equals(NameConstants.NT_PAGE) && resourceNode.hasNode(JcrConstants.JCR_CONTENT)) {
                        contentNode = resourceNode.getNode(JcrConstants.JCR_CONTENT);
                    }

                    String originalPath = JcrConstants.JCR_CONTENT.concat("/").concat(DamConstants.RENDITIONS_FOLDER).concat("/original/").concat(JcrConstants.JCR_CONTENT);
                    if (resourceNode.getPrimaryNodeType().getName().equals(DamConstants.NT_DAM_ASSET) && resourceNode.hasNode(originalPath))  {
                        contentNode = resourceNode.getNode(originalPath);
                    }

                    if (contentNode != null) {
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
                LOGGER.error("Could not load file to be included {}", resource.getPath());
            }
        }
        return returnValue;
    }

    /**
     * Read properties for the Component, use component style to override properties if they are not set.
     * @param componentProperties component properties
     * @param contentPolicy       value map of content policy
     * @param name                name of the property
     * @param defaultValue        default value for the property
     * @param useStyle            use styles properties if property is missing
     * @return component property
     */
    @SuppressWarnings("Duplicates")
    public static Object getComponentProperty(ValueMap componentProperties, ValueMap contentPolicy, String name, Object defaultValue, Boolean useStyle) {
        //quick fail
        if (componentProperties == null) {
            LOGGER.warn("getComponentProperty, componentProperties is ({0})", componentProperties);
            return StringUtils.EMPTY;
        }
        if (useStyle && (contentPolicy == null || contentPolicy.isEmpty())) {

            useStyle = false;
        }

        if (useStyle) {
            return componentProperties.get(name, contentPolicy.get(name, defaultValue));
        } else {
            return componentProperties.get(name, defaultValue);
        }
    }

    /**
     * Read properties for the Component, use component style to override properties if they are not set.
     * @param componentProperties component properties
     * @param pageStyle           page style to use as default
     * @param name                name of the property
     * @param defaultValue        default value for the property
     * @param useStyle            use styles properties if property is missing
     * @return component property
     */
    @SuppressWarnings("Duplicates")
    public static Object getComponentProperty(ValueMap componentProperties, Style pageStyle, String name, Object defaultValue, Boolean useStyle) {
        //quick fail
        if (componentProperties == null) {
            LOGGER.warn("getComponentProperty, componentProperties is ({0})", componentProperties);
            return StringUtils.EMPTY;
        }
        if (useStyle && pageStyle == null) {
            LOGGER.warn("getComponentProperty, useStyle is ({0}) but pageStyle is {1}", useStyle, pageStyle);
            return StringUtils.EMPTY;
        }

        if (useStyle) {
            return componentProperties.get(name, pageStyle.get(name, defaultValue));
        } else {
            return componentProperties.get(name, defaultValue);
        }
    }

    /**
     * returns component values with defaults from target component on a page.
     * @param pageContext current page context
     * @param componentPage target page
     * @param componentPath target component path
     * @param fieldLists list of fields definition Object{{name, defaultValue, attributeName, valueTypeClass},...}
     * @return map of component attributes
     */
    public static ComponentProperties getComponentProperties(PageContext pageContext, Page componentPage, String componentPath, Object[][]... fieldLists) {
        try {
            Resource componentResource = componentPage.getContentResource(componentPath);

            return getComponentProperties(pageContext, componentResource, fieldLists);

        } catch (Exception ex) {
            LOGGER.error("getComponentProperties: {}, error: {}", componentPath, ex);
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

    /***
     * helper to create new Component Properties.
     * @param wcmUsePojoModel component model pojo
     * @return map of attributes
     */
    public static ComponentProperties getNewComponentProperties(WCMUsePojo wcmUsePojoModel) {

        Map<String, Object> pageContextMap = new HashMap<>();

        SlingHttpServletRequest slingRequest = wcmUsePojoModel.getRequest();
        pageContextMap.put(PAGECONTEXTMAP_OBJECT_SLINGREQUEST, slingRequest);

        SlingScriptHelper sling = wcmUsePojoModel.getSlingScriptHelper();
        pageContextMap.put(PAGECONTEXTMAP_OBJECT_SLING , sling);

        XSSAPI xssAPI = wcmUsePojoModel.getSlingScriptHelper().getService(XSSAPI.class);
        pageContextMap.put(PAGECONTEXTMAP_OBJECT_XSSAPI, xssAPI);

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
        try {
            XSSAPI xss = null;
            if (pageContext.containsKey(PAGECONTEXTMAP_OBJECT_XSSAPI)) {
                xss = (XSSAPI)pageContext.get(PAGECONTEXTMAP_OBJECT_XSSAPI);
            } else {
                SlingScriptHelper sling = (SlingScriptHelper) pageContext.get(PAGECONTEXTMAP_OBJECT_SLING);
                xss = Objects.requireNonNull(sling.getService(XSSAPI.class));
            }

            if (xss != null) {
                componentProperties.attr = new AttrBuilder(xss);
            } else {
                LOGGER.error("getNewComponentProperties: could not configure componentProperties with attributeBuilder as xss is missing");
            }

        } catch (Exception ex) {
            LOGGER.error("getNewComponentProperties: could not configure componentProperties with attributeBuilder");
        }
        return componentProperties;
    }


    /**
     * returns component values with defaults from pageContent Properties.
     * @param wcmUsePojoModel component model pojo
     * @param includeComponentAttributes include attributes specific to this component instance
     * @param fieldLists      list of fields definition Object{{name, defaultValue, attributeName, valueTypeClass},...}
     * @return map of attributes
     */
    public static ComponentProperties getComponentProperties(WCMUsePojo wcmUsePojoModel, Boolean includeComponentAttributes, Object[][]... fieldLists) {
        return getComponentProperties(wcmUsePojoModel, null, includeComponentAttributes, fieldLists);
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
            LOGGER.error("getComponentProperties(wcmUsePojoModel,targetResource,fieldLists) could not read required objects: {}, error: {}", wcmUsePojoModel, ex);
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
    public static ComponentProperties getComponentProperties(GenericModel genericModel, Resource targetResource, Object[][]... fieldLists) {
        try {

            return getComponentProperties(genericModel.getPageContextMap(), targetResource, true, fieldLists);

        } catch (Exception ex) {
            LOGGER.error("getComponentProperties(genericModel,targetResource,fieldLists) could not read required objects: {}, error: {}", genericModel, ex);
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
            LOGGER.error("getComponentProperties(genericModel,targetResource,includeComponentAttributes,fieldLists) could not read required objects: {}, error: {}", genericModel, ex);
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
            LOGGER.error("getComponentProperties(wcmUsePojoModel,targetResource,includeComponentAttributes,fieldLists) could not read required objects: {}, error: {}", wcmUsePojoModel, ex);
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
            LOGGER.error("getComponentProperties(PageContext) could not read required objects", ex);
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
    @SuppressWarnings({"unchecked","Depreciated","Duplicates","squid:S3776"})
    public static ComponentProperties getComponentProperties(Map<String, Object> pageContext, Object targetResource, Boolean includeComponentAttributes, Object[][]... fieldLists) {
        ComponentProperties componentProperties = new ComponentProperties();

        boolean addMoreAttributes = includeComponentAttributes;

        SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) pageContext.get("slingRequest");
        //ResourceResolver resourceResolver = (ResourceResolver) pageContext.get("resourceResolver");

        SlingScriptHelper sling = (SlingScriptHelper) pageContext.get("sling");

        com.adobe.granite.xss.XSSAPI oldXssAPI = slingRequest.adaptTo(com.adobe.granite.xss.XSSAPI.class);

        ComponentContext componentContext = (ComponentContext) pageContext.get("componentContext");
        Component component = componentContext.getComponent();
        XSSAPI xssAPI = Objects.requireNonNull(sling.getService(XSSAPI.class));

        componentProperties.attr = new AttrBuilder(xssAPI);
        if (addMoreAttributes) {
            //all component have a boolean attribute component, sling does not print attributes with blank values
            componentProperties.attr.add("component", "component");
        }

        componentProperties.expressionFields = new ArrayList();

        Resource contentResource = null;

        final String CLASS_TYPE_RESOURCE = Resource.class.getCanonicalName();
        final String CLASS_TYPE_JCRNODERESOURCE = "org.apache.sling.jcr.resource.internal.helper.jcr.JcrNodeResource";
        final String CLASS_TYPE_ASSET = "com.adobe.granite.asset.core.impl.AssetImpl";

        ContentAccess contentAccess = sling.getService(ContentAccess.class);
        if (contentAccess != null) {
            try (ResourceResolver adminResourceResolver = contentAccess.getAdminResourceResolver()) {

                Node currentNode = (javax.jcr.Node) pageContext.get("currentNode");

                TagManager tagManager = adminResourceResolver.adaptTo(TagManager.class);

                // if targetResource == null get defaults
                ValueMap properties = (ValueMap) pageContext.get("properties"); //NOSONAR getting properties from pagecontext

                ValueMap currentPolicy = getContentPolicyProperties(componentContext.getResource(), adminResourceResolver);

                //if targetResource != null get the appropriate objects
                if (targetResource != null && targetResource.getClass().getCanonicalName().equals(CLASS_TYPE_ASSET)) {

                    try {
                        //
                        com.adobe.granite.asset.api.Asset asset = (com.adobe.granite.asset.api.Asset) targetResource;

                        contentResource = asset.getResourceResolver().getResource(asset, JcrConstants.JCR_CONTENT);

                        if (contentResource != null) {
                            contentResource = contentResource.getChild(DamConstants.METADATA_FOLDER);
                        }
                        properties = contentResource.adaptTo(ValueMap.class);
                        addMoreAttributes = false;

                        componentProperties.put(COMPONENT_TARGET_RESOURCE, contentResource.getPath());

                    } catch (Exception ex) {
                        LOGGER.error("getComponentProperties: could not evaluate target asset", ex);
                        return componentProperties;
                    }
                } else if (targetResource != null && (targetResource.getClass().getCanonicalName().equals(CLASS_TYPE_RESOURCE) || targetResource.getClass().getCanonicalName().equals(CLASS_TYPE_JCRNODERESOURCE))) {

                    try {
                        contentResource = (Resource) targetResource;

                        properties = contentResource.adaptTo(ValueMap.class);

                        currentPolicy = getContentPolicyProperties(contentResource, adminResourceResolver);

                        ComponentManager componentManager = contentResource.getResourceResolver().adaptTo(ComponentManager.class);
                        Component resourceComponent = componentManager.getComponentOfResource(contentResource);
                        //set component to match target resource
                        if (resourceComponent != null) {
                            component = resourceComponent;
                        }

                        //set currentnode to match target resource
                        Node resourceNode = contentResource.adaptTo(Node.class);
                        if (resourceNode != null) {
                            currentNode = resourceNode;
                        }

                        componentProperties.put(COMPONENT_TARGET_RESOURCE, contentResource.getPath());

                    } catch (Exception ex) {
                        LOGGER.error("getComponentProperties: could not evaluate target resource", ex);
                        return componentProperties;
                    }
                } else if (targetResource != null) {
                    LOGGER.warn("getComponentProperties: processing is unsupported of target resource of type: {}", targetResource.getClass().getCanonicalName());
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

                    for (Object[][] fieldDefaults : fieldLists) {
                        for (Object[] field : fieldDefaults) {
                            if (field.length < 1) {
                                throw new IllegalArgumentException(format("Key, Value, ..., Value-n expected, instead got {0} fields.", field.length));
                            }
                            //read first field - get field name
                            String fieldName = field[0].toString();

                            //read second field - get default value or default expression
                            Object fieldDefaultValue = field[1];
                            //set default value to empty string if default value is null
                            if (isNull(fieldDefaultValue)) {
                                fieldDefaultValue = StringUtils.EMPTY;
                            }

                            //read third field - get data attribute name
                            String fieldDataName= StringUtils.EMPTY;
                            if (field.length > 2) {
                                fieldDataName = field[2].toString();
                            }
                            //read forth field - get current field value type
                            String fieldValueType;
                            if (field.length > 3) {
                                fieldValueType = (String) field[3];
                            } else {
                                fieldValueType = String.class.getCanonicalName();
                            }

                            if (componentProperties.containsKey(fieldName)) {
                                //skip entries that already exist
                                //first Object in fieldLists will set a field value
                                //we expect the additional Objects to not override
                                LOGGER.warn("getComponentProperties: skipping property [{}] its already defined, {}", fieldName, componentContext.getResource().getPath());
                                continue;
                            }


                            Object fieldValue = null;

                            //handle Tag[] value expectation.
                            if (fieldDefaultValue.getClass() == Tag[].class) {
                                Resource currentResource = (Resource)pageContext.get("resource");
                                //get value from policy
                                fieldValue = currentPolicy.get(fieldName, Tag[].class);
                                //try resourcee
                                if (fieldValue == null) {
                                    fieldValue = tagManager.getTags(currentResource);
                                }
                                //use default
                                if (fieldValue == null) {
                                    fieldValue = fieldDefaultValue;
                                }
                            } else {
                                //read component value or return default value, result could be a expression
                                // that need to be processed using @ComponentProperties.evaluateExpressionFields()
                                fieldValue = getComponentProperty(properties, currentPolicy, fieldName, fieldDefaultValue, true);
                            }

                            boolean fieldValueHasExpressions = isStringRegex(fieldDefaultValue.toString());

                            //check if field value has expressions
                            if (fieldValue instanceof String && StringUtils.isNotEmpty(fieldValue.toString()) && isStringRegex(fieldValue.toString())) {
                                fieldValueHasExpressions = true;
                            }

                            //Empty array with empty string will set the default value
                            if (fieldValue instanceof String && StringUtils.isEmpty(fieldValue.toString())) {
                                fieldValue = fieldDefaultValue;
                            } else if (fieldValue instanceof String[] && fieldValue != null && (StringUtils.isEmpty(StringUtils.join((String[]) fieldValue, StringUtils.EMPTY)))) {
                                fieldValue = fieldDefaultValue;
                            }

                            //fix values that should be arrays but are not
                            if (fieldValueType.equals(Tag.class.getCanonicalName()) && !fieldValue.getClass().isArray()) {
                                fieldValue = new String[]{(String)fieldValue};
                            } else if ((fieldValueType.getClass().isArray() || fieldDefaultValue.getClass().isArray()) && !fieldValue.getClass().isArray()) {
                                Class<?> arrayType = fieldValue.getClass().getComponentType();
                                Object newArray = Array.newInstance(arrayType,1);
                                Array.set(newArray,1, fieldValue);
                                fieldValue = newArray;
                            }

                            //field has more field than name and default value
                            if (field.length > 2) {
                                String fieldValueString = StringUtils.EMPTY;

                                //test field value type
                                if (fieldValue.getClass().isArray()) {
                                    if (ArrayUtils.isNotEmpty((String[]) fieldValue)) {
                                        //handle tags as values
                                        if (fieldValueType.equals(Tag.class.getCanonicalName())) {
                                            //if data-attribute not specified return values as map entry
                                            if (isEmpty(fieldDataName)) {
                                                fieldValue = TagUtil.getTagsValues(tagManager, adminResourceResolver, " ", (String[]) fieldValue);
                                                //find the first item that has expressions
                                                for (String tagValue: (String[])fieldValue) {
                                                    if (isStringRegex(tagValue)) {
                                                        fieldValueHasExpressions = true;
                                                        break;
                                                    }
                                                }
                                            } else {
                                                fieldValueString = TagUtil.getTagsAsValues(tagManager, adminResourceResolver, FIELD_DATA_TAG_SEPARATOR, (String[]) fieldValue);
                                                //check if result string has regex
                                                if (isStringRegex(fieldValueString)) {
                                                    fieldValueHasExpressions = true;
                                                }
                                            }
                                        } else { //handle plain string
                                            fieldValueString = StringUtils.join((String[]) fieldValue, FIELD_DATA_ARRAY_SEPARATOR);
                                        }
                                    } else {
                                        //if data-attribute not specified return empty if values array is empty
                                        if (isEmpty(fieldDataName)) {
                                            fieldValue = StringUtils.EMPTY;
                                        }
                                    }
                                } else {
                                    fieldValueString = fieldValue.toString();
                                }

                                //set data attribute if data attribute has been specified and value is not an expression
                                if (isNotEmpty(fieldValueString)
                                    && isNotEmpty(fieldDataName)
                                    && !fieldDataName.equals(FIELD_VALUES_ARE_ATTRIBUTES)
                                    && !fieldValueHasExpressions
                                ) {
                                    componentProperties.attr.add(fieldDataName, fieldValueString);
                                } else if (isNotEmpty(fieldValueString) && fieldDataName.equals(FIELD_VALUES_ARE_ATTRIBUTES)) {
                                    //process possible Key-Value pairs that are in the values output

                                    if (fieldValueString.contains(FIELD_DATA_TAG_SEPARATOR)) {
                                        //multiple boolean attributes being added
                                        for (String item : fieldValueString.split(FIELD_VALUES_ARE_ATTRIBUTES)) {
                                            if (!item.contains("=")) {
                                                componentProperties.attr.add(item, "true");
                                            } else {
                                                String[] items = item.split("=");
                                                componentProperties.attr.add(items[0], StringUtils.substringBetween(items[1], "\"", "\""));
                                            }
                                        }
                                    } else {
                                        if (!fieldValueString.contains("=")) {
                                            componentProperties.attr.add(fieldValueString, "true");
                                        } else {
                                            String[] items = fieldValueString.split("=");
                                            componentProperties.attr.add(items[0], StringUtils.substringBetween(items[1], "\"", "\""));
                                        }
                                    }
                                }

                            }

                            if (fieldValueHasExpressions) {
                                //add current field to expression fields list to allow re-evaluation
                                ComponentField expField = new ComponentField(field);
                                expField.setValue(fieldValue);
                                componentProperties.expressionFields.add(expField);
                            }

                            try {
                                componentProperties.put(fieldName, fieldValue);
                            } catch (Exception ex) {
                                LOGGER.error("error adding value. {}", ex);
                            }
                        }
                    }

                    //get current variant value
                    String variant = componentProperties.get(FIELD_VARIANT, StringUtils.EMPTY);
                    //get current badge template value
                    String badge = componentProperties.get(DETAILS_BADGE_TEMPLATE, StringUtils.EMPTY);

                    if (addMoreAttributes) {

                        //use default variant if variant value not set
                        if (isEmpty(variant)) {
                            variant = DEFAULT_VARIANT;
                            //mark current variant selection as legacy
                            componentProperties.put(FIELD_VARIANT_LEGACY, true);
                        } else {
                            //get variant config
                            ComponentProperties variantConfig = getTemplateConfig(pageContext, variant, adminResourceResolver, tagManager, FIELD_VARIANT_FIELDS_TEMPLATE, FIELD_VARIANT_FIELDS, FIELD_VARIANT);
                            componentProperties.putAll(variantConfig);
                            //get updated version of variant
                            variant = componentProperties.get(FIELD_VARIANT, StringUtils.EMPTY);
                        }
                        //get badge config
                        if (isNotEmpty(badge)) {
                            ComponentProperties badgeConfig = getTemplateConfig(pageContext, badge, adminResourceResolver, tagManager, DETAILS_BADGE_FIELDS_TEMPLATE, DETAILS_BADGE_FIELDS, "detailsBadge");
                            componentProperties.putAll(badgeConfig);
                        }

                        //compile variantTemplate param
                        String variantTemplate = getComponentVariantTemplate(component, format(COMPONENT_VARIANT_TEMPLATE_FORMAT, variant), sling);
                        componentProperties.put(COMPONENT_VARIANT_TEMPLATE, variantTemplate);
                    }

                    //add variant to the end of class string
                    if (!componentProperties.attr.isEmpty() && addMoreAttributes) {
                        //check if component variant exist and add to class chain if not default
                        if (isNotEmpty(variant) && !variant.equals(DEFAULT_VARIANT)) {
                            componentProperties.attr.add(COMPONENT_ATTRIBUTE_CLASS, variant);
                        }
                        componentProperties.put(COMPONENT_ATTRIBUTES, buildAttributesString(componentProperties.attr.getData(), xssAPI));
                    }

                }

            } catch (Exception ex) {
                LOGGER.error("getComponentProperties: error processing properties: component={}, ex.message={}, ex={}", component.getPath(), ex.getMessage(), ex);
            }
        } else {
            LOGGER.error("getComponentProperties: could not get ContentAccess service.");
        }

        return componentProperties;
    }

    /**
     * get template structure config from tags for badge or variant
     * @param pageContext current page context
     * @param configTag tag to use for lookup
     * @param resourceResolver instance of resource resolver
     * @param tagManager instance of tag manager
     * @param fieldNameTemplates return field name for templates
     * @param fieldNameFields return field name for fields
     * @param fieldNameFirstTemplateName return field name for template name
     * @return return map of fields with values
     */
    @SuppressWarnings("squid:S3776")
    public static ComponentProperties getTemplateConfig(Map<String, Object> pageContext, String configTag, ResourceResolver resourceResolver, TagManager tagManager, String fieldNameTemplates, String fieldNameFields, String fieldNameFirstTemplateName) {
        ComponentProperties componentProperties = getNewComponentProperties(pageContext);
        if (isNotEmpty(configTag)) {
            Tag tagConfig = getTag(configTag, resourceResolver, tagManager);
            if (tagConfig != null) {
                //mark current variant selection as not legacy
                componentProperties.put(FIELD_VARIANT_LEGACY, false);

                //get value map of tag
                Resource tagConfigRS = resourceResolver.getResource(tagConfig.getPath());
                if (tagConfigRS != null) {
                    ValueMap tagConfigVM = tagConfigRS.adaptTo(ValueMap.class);
                    if (tagConfigVM != null) {
                        //see if it has templates set and get it
                        if (tagConfigVM.containsKey(FIELD_TAG_TEMPLATE_CONFIG_TEMPLATES)) {
                            String[] template = tagConfigVM.get(FIELD_TAG_TEMPLATE_CONFIG_TEMPLATES, new String[]{});
                            componentProperties.put(fieldNameTemplates, template);
                            if (template.length > 0) {
                                //update the return template name to first entry
                                componentProperties.put(fieldNameFirstTemplateName, template[0]);
                            }
                        } else {
                            //get tag value as variant name
                            if (tagConfigVM.containsKey(FIELD_TAG_TEMPLATE_CONFIG_VALUE)) {
                                String value = tagConfigVM.get(FIELD_TAG_TEMPLATE_CONFIG_VALUE, DEFAULT_VARIANT);
                                componentProperties.put(fieldNameFirstTemplateName, value);
                            }

                        }
                        //get fields list
                        if (tagConfigVM.containsKey(FIELD_TAG_TEMPLATE_CONFIG_FIELDS)) {
                            String[] fields = tagConfigVM.get(FIELD_TAG_TEMPLATE_CONFIG_FIELDS, new String[]{});
                            componentProperties.put(fieldNameFields, fields);
                        }
                    }
                }
            } else {
                //mark current variant selection as legacy
                componentProperties.put(FIELD_VARIANT_LEGACY, true);
            }
        }
        return componentProperties;
    }
    /**
     * return variant template name from component or return default
     * @param component component to check for variant template
     * @param variantTemplate variant template
     * @param sling sling instance
     * @return variant template path
     */
    public static String getComponentVariantTemplate(Component component, String variantTemplate, SlingScriptHelper sling) {
        String variantTemplateDefault = format(COMPONENT_VARIANT_TEMPLATE_FORMAT, DEFAULT_VARIANT);

        //ensure that variant exist
        if (component != null && isNotEmpty(variantTemplate)) {

            String variantTemplatePath = findLocalResourceInSuperComponent(component, variantTemplate, sling);

            if (isNotBlank(variantTemplatePath)) {

                return variantTemplatePath;
            }

        }

        return variantTemplateDefault;
    }


    /***
     * build attributes from attributes data without encoding.
     * @param data map of data attributes and values
     * @param xssAPI old xssi api
     * @return attributes string
     */
    @SuppressWarnings("Depreciated")
    public static String buildAttributesString(Map<String, String> data, XSSAPI xssAPI) {
        return buildAttributesString(data, xssAPI, new HashMap<>());
    }

    /***
     * build attributes from attributes data.
     * @param attributes map of data attributes and values
     * @param xssAPI xss api
     * @param encodings map of encoding per data attribute
     * @return attributes string
     */
    @SuppressWarnings({"Depreciated", "squid:S3776"})
    public static String buildAttributesString(
        Map<String, String> attributes,
        XSSAPI xssAPI,
        @Nonnull Map<String, AttrBuilder.EncodingType> encodings
    ) {
        AttrBuilder attr = new AttrBuilder(xssAPI);

        attributes.forEach((attribute, value) -> {
            if (value == null) {
                return;
            }

            attr.add(attribute, value, encodings.get(attribute));
        });

        return attr.build().replace("&#x20;", " ");
    }

    /***
     *
     * @deprecated please use responsive {@link design.aem.utils.components.ImagesUtil#getBackgroundImageRenditions} or ComponentProperties.attr which is an AttributeBuilder.
     * add style tag to component attributes collection
     * @param componentProperties component attributes collection
     * @param resource resource to search for image
     * @param imageResourceName image node name
     * @return style with background image
     *
     */
    @Deprecated
    public static String addComponentBackgroundToAttributes(ComponentProperties componentProperties, Resource resource, String imageResourceName) {
        String componentAttributes = componentProperties.get(COMPONENT_ATTRIBUTES, StringUtils.EMPTY);
        Resource imageResource = resource.getChild(imageResourceName);
        if (imageResource != null) {
            Resource fileReference = imageResource.getChild(ConstantsUtil.IMAGE_FILEREFERENCE);
            if (fileReference != null) {
                String imageSrc = StringUtils.EMPTY;
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

        String componentAttributes = componentProperties.get(COMPONENT_ATTRIBUTES, StringUtils.EMPTY);

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

                Resource detailsResource = resourceResolver.resolve(detailsPath);
                if (!ResourceUtil.isNonExistingResource(detailsResource)) {
                    ValueMap vm = detailsResource.getValueMap();
                    String detailsDescription = vm.get(DETAILS_DESCRIPTION, "");
                    if (StringUtils.isNotEmpty(detailsDescription)) {
                        return detailsDescription;
                    }
                }


            }

        } catch (Exception ex) {
            LOGGER.error("getPageDescription: {}", ex);
        }
        return pageDescription;
    }

    /***
     * compile a message from component properties using one of the component format tag fields.
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

        return CommonUtil.compileMapMessage(fieldFormatValue, componentProperties);

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
        String path = StringUtils.EMPTY;
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
            LOGGER.error("Could not get id for component path={},id={},error {}", path, componentId, ex);
        }

        return componentId;
    }

    /***
     * get cloud config setting from inherited value map
     * @param pageProperties inherited value map with values
     * @param configurationName config name
     * @param propertyName property to get
     * @param sling sling instance
     * @return return cloud config property
     */
    public static String getCloudConfigProperty(InheritanceValueMap pageProperties, String configurationName, String propertyName, SlingScriptHelper sling) {
        return getCloudConfigProperty(pageProperties,configurationName,propertyName, sling);
    }

    /***
     * get cloud config setting from inherited value map
     * @param pageProperties value map with values
     * @param configurationName config name
     * @param propertyName property to get
     * @param sling sling instance
     * @return return cloud config property
     */
    public static String getCloudConfigProperty(ValueMap pageProperties, String configurationName, String propertyName, SlingScriptHelper sling) {
        String returnValue = StringUtils.EMPTY;

        ContentAccess contentAccess = sling.getService(ContentAccess.class);
        if (contentAccess != null) {
            try (ResourceResolver adminResourceResolver = contentAccess.getAdminResourceResolver()) {

                // Getting attached facebook cloud service config in order to fetch appID
                ConfigurationManager cfgMgr = adminResourceResolver.adaptTo(ConfigurationManager.class);
                Configuration addthisConfiguration = null;
                String[] services = pageProperties.get(ConfigurationConstants.PN_CONFIGURATIONS, new String[]{});
                if (cfgMgr != null) {
                    addthisConfiguration = cfgMgr.getConfiguration(configurationName, services);
                    if (addthisConfiguration != null) {
                        returnValue = addthisConfiguration.get(propertyName, StringUtils.EMPTY);
                    }
                }


            } catch (Exception ex) {
                LOGGER.error(Throwables.getStackTraceAsString(ex));
            }
        } else {
            LOGGER.error("getCloudConfigProperty: could not get ContentAccess service.");
        }

        return returnValue;
    }


    /***
     * find an ancestor resource matching current resource.
     * @param page page to use
     * @param componentContext component context
     * @return found resource
     */
    @SuppressWarnings("squid:S3776")
    public static Resource findInheritedResource(Page page, ComponentContext componentContext) {
        final String pageResourcePath = page.getContentResource().getPath(); // assume that page have resource
        final Resource thisResource = componentContext.getResource();
        final String nodeResourceType = thisResource.getResourceType();
        final String relativePath = thisResource.getPath().replaceFirst(pageResourcePath.concat(FileSystem.SEPARATOR), StringUtils.EMPTY);

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
                    LOGGER.info("Failed to get {} from {}", relativePath, curPage.getContentResource().getPath());
                }

                if (null != curResource) {
                    //check for inherit flag + sling resource type

                    curProperties = curResource.adaptTo(ValueMap.class);
                    if (curProperties != null) {
                        curResourceTypeMatch = curResource.isResourceType(nodeResourceType);
                        curCancelInheritParent = curProperties.get(COMPONENT_CANCEL_INHERIT_PARENT, StringUtils.EMPTY).contentEquals("true");

                        if (curResourceTypeMatch && curCancelInheritParent) {
                            String found = format("findInheritedResource: FOUND looking for inherited resource for path=\"{0}\" by relative path=\"{1}\" in parent=\"{2}\"", pageResourcePath, relativePath, curPage.getPath());
                            LOGGER.info(found);

                            break;
                        } else {
                            String notfound = format("findInheritedResource: NOT FOUND looking for inherited resource for path=\"{0}\" by relative path=\"{1}\" in parent=\"{2}\"", pageResourcePath, relativePath, curPage.getPath());
                            LOGGER.info(notfound);

                        }
                    } else {
                        LOGGER.error("findInheritedResource: could not convert resource to value map, curResource={}", curResource);
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
     * @param attributeName attribute name
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
        return StringUtils.EMPTY;
    }

    /***
     * get component path relative to {@link JcrConstants#JCR_CONTENT}
     * @param componentNode component node to use for path evaluation
     * @return component path relative to {@link JcrConstants#JCR_CONTENT}
     */
    public static String getComponentInPagePath(Node componentNode) {
        String componentInPagePath = StringUtils.EMPTY;
        if (componentNode == null) {
            return StringUtils.EMPTY;
        }

        try {
            componentInPagePath = componentNode.getPath();
            if (isNotEmpty(componentInPagePath) && componentInPagePath.contains(JcrConstants.JCR_CONTENT)) {
                String[] parts = componentInPagePath.split(JcrConstants.JCR_CONTENT);
                if (parts.length > 0) {
                    componentInPagePath = parts[1];
                    if (isNotEmpty(componentInPagePath) && componentInPagePath.startsWith(FileSystem.SEPARATOR)) {
                        componentInPagePath = componentInPagePath.replaceFirst(FileSystem.SEPARATOR, StringUtils.EMPTY);
                    }
                }
            }
        } catch (Exception ex) {
            LOGGER.warn("getComponentInPagePath: could not get component in path {}, ex: {}", componentNode, ex);
        }
        return componentInPagePath;
    }

    /**
     * return list of available sub-resources in current and all super components.
     * @param component component to start with
     * @param resourceName sub-resource container to find
     * @param sling sling instance
     * @return returns list of resources
     */
    @SuppressWarnings({"squid:S3776"})
    public static Map<String, Resource> getLocalSubResourcesInSuperComponent(Component component,String resourceName, SlingScriptHelper sling) {
        HashMap<String, Resource> subResources = new HashMap<>();
        Component superComponent = null;

        if (component != null && isNotEmpty(resourceName)) {

            ContentAccess contentAccess = sling.getService(ContentAccess.class);
            if (contentAccess != null) {
                try (ResourceResolver adminResourceResolver = contentAccess.getAdminResourceResolver()) {

                    //get component with admin resource resolver
                    String componentPath = component.getPath();
                    Resource componentAdminResource = adminResourceResolver.resolve(componentPath);
                    if (!ResourceUtil.isNonExistingResource(componentAdminResource)) {
                        superComponent = componentAdminResource.adaptTo(Component.class);

                        if (superComponent != null) {
                            Resource localresource = superComponent.getLocalResource(resourceName);

                            //check for local resources
                            if (localresource != null && !ResourceUtil.isNonExistingResource(localresource)) {
                                for (Resource resource : localresource.getChildren()) {
                                    String name = resource.getName().replace(DEFAULT_EXTENTION, EMPTY);
                                    subResources.put(name, resource);
                                }
                            }

                            //collect sub resources from super types
                            while (superComponent.getSuperComponent() != null) {

                                superComponent = superComponent.getSuperComponent();

                                if (superComponent == null) {
                                    break;
                                }

                                localresource = superComponent.getLocalResource(resourceName);

                                if (localresource != null && !ResourceUtil.isNonExistingResource(localresource)) {
                                    for (Resource resource : localresource.getChildren()) {
                                        String name = resource.getName().replace(DEFAULT_EXTENTION, EMPTY);

                                        //add only newly found resources
                                        if (!subResources.containsKey(name)) {
                                            subResources.put(name, resource);
                                        }
                                    }
                                }
                            }
                        } else {
                            LOGGER.error("getComponentSubResources: could not convert resource to component, componentAdminResource={}", componentAdminResource);
                        }
                    } else {
                        LOGGER.error("getComponentSubResources: could not resolve component path to resource, componentPath={}", componentPath);
                    }

                } catch (Exception ex) {
                    LOGGER.error(Throwables.getStackTraceAsString(ex));
                }

            } else {
                LOGGER.error("getComponentSubResources: could not get ContentAccess service.");
            }


        } else {
            LOGGER.error("getComponentSubResources: please specify component and sub resource: component={},resourceName={}",component, resourceName);
        }

        return subResources;
    }

    /**
     * find local resource in component and its super components
     * @param component component to check
     * @param resourceName local resource name
     * @param sling sing instance
     * @return local resource path found
     */
    @SuppressWarnings("squid:S3776")
    public static String findLocalResourceInSuperComponent(Component component, String resourceName, SlingScriptHelper sling) {

        Component superComponent = null;
        if (component != null) {

            ContentAccess contentAccess = sling.getService(ContentAccess.class);
            if (contentAccess != null) {
                try (ResourceResolver adminResourceResolver = contentAccess.getAdminResourceResolver()) {

                    //get component with admin resource resolver
                    String componentPath = component.getPath();
                    Resource componentAdminResource = adminResourceResolver.resolve(componentPath);
                    if (!ResourceUtil.isNonExistingResource(componentAdminResource)) {
                        superComponent = componentAdminResource.adaptTo(Component.class);

                        if (superComponent != null) {
                            Resource localresource = superComponent.getLocalResource(resourceName);

                            if (localresource != null && !ResourceUtil.isNonExistingResource(localresource)) {
                                return localresource.getPath();
                            }

                            while (superComponent.getSuperComponent() != null) {

                                superComponent = superComponent.getSuperComponent();

                                if (superComponent == null) {
                                    return StringUtils.EMPTY;
                                }

                                localresource = superComponent.getLocalResource(resourceName);

                                if (localresource != null && !ResourceUtil.isNonExistingResource(localresource)) {

                                    return localresource.getPath();
                                }

                            }
                        } else {
                            LOGGER.error("findLocalResourceInSuperComponent: could not convert resource to component, componentAdminResource={}", componentAdminResource);
                        }
                    } else {
                        LOGGER.error("findLocalResourceInSuperComponent: could not resolve component path to resource, componentPath={}", componentPath);
                    }

                } catch (Exception ex) {
                    LOGGER.error(Throwables.getStackTraceAsString(ex));
                }

            } else {
                LOGGER.error("findLocalResourceInSuperComponent: could not get ContentAccess service.");
            }


        }
        return StringUtils.EMPTY;
    }



    /**
     * create a map of component fields matched to Dialog Title and Description
     * @param componentResource component resource
     * @param adminResourceResolver admin resolver
     * @param slingScriptHelper sling script helper
     * @return map of component dialog fields and their attributes
     */
    @SuppressWarnings("squid:S3776")
    public static Map<String, Object> getComponentFieldsAndDialogMap(Resource componentResource , ResourceResolver adminResourceResolver, SlingScriptHelper slingScriptHelper) {
        Map<String, Object> firstComponentConfig = new HashMap<>();

        if (!ResourceUtil.isNonExistingResource(componentResource)) {

            ValueMap componentResourceMap = componentResource.adaptTo(ValueMap.class);
            if (componentResourceMap != null) {

                try {
                    ComponentManager componentManager = adminResourceResolver.adaptTo(ComponentManager.class);
                    Component componentOfResource = componentManager.getComponentOfResource(componentResource);

                    if (componentOfResource != null) {
                        ValueMap componentOfResourceValueMap = componentOfResource.getProperties();
                        String componentPath = componentOfResource.getPath();
                        Resource componentOfResourceRS = adminResourceResolver.resolve(componentPath);

                        //walk up the tree of resourceSuperType and get base component
                        String componentDialogPath = findLocalResourceInSuperComponent(componentOfResource,"cq:dialog", slingScriptHelper);

                        String dialogPath = StringUtils.EMPTY;
                        Document dialogContent = null;

                        if (isNotEmpty(componentDialogPath)) {

                            //get dialog with value from resource: /<app component path>/cq:dialog/content.html/<resource path to pull values>
                            dialogPath = componentDialogPath.concat(DEFAULT_EXTENTION).concat(componentResource.getPath());

                            String dialogHTML = resourceRenderAsHtml(
                                    dialogPath,
                                    adminResourceResolver,
                                    slingScriptHelper,
                                    WCMMode.DISABLED,
                                    null,
                                    null,
                                    false);

                            dialogContent = Jsoup.parse(dialogHTML);

                        }
                        for (Map.Entry<String, Object> field : componentResourceMap.entrySet()) {

                            String name = field.getKey();
                            Object value = field.getValue();

                            Map<String, Object> row = new HashMap<>();
                            row.put("value", value);
                            row.put("fieldDescription", StringUtils.EMPTY);
                            row.put("fieldLabel", StringUtils.EMPTY);
                            row.put("type", StringUtils.EMPTY);

                            if (isNotEmpty(dialogPath) && dialogContent != null) {
                                String fieldSelection = "[name='./" + name + "']";


                                Element htmlSection = dialogContent.selectFirst(".coral-Form-fieldwrapper:has(" + fieldSelection + ")");

                                Element fieldElement = dialogContent.selectFirst(fieldSelection);

                                if (fieldElement != null) {
                                    Object[] classNames = fieldElement.classNames().toArray();
                                    if (classNames.length > 0) {
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

                                    String fieldDescriptionString = StringUtils.EMPTY;

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

                    } else {
                        LOGGER.error("getComponentFieldsAndDialogMap: could not get component from resource - path={}",componentResource.getPath());
                    }
                } catch (Exception ex) {
                    LOGGER.error("getComponentFieldsAndDialogMap: ex={}",ex);
                }
            } else {
                LOGGER.error("getComponentFieldsAndDialogMap: could not get component values - path={}",componentResource.getPath());
            }
        } else {
            LOGGER.error("getComponentFieldsAndDialogMap: could not find component for resource - path={}",componentResource.getPath());
        }

        return firstComponentConfig;

    }

    /**
     * return current content policy settings for a component
     * @param componentResource component resource to use
     * @param resourceResolver resource resolver to use
     * @return ValueMap of policy settings
     */
    public static ValueMap getContentPolicyProperties(Resource componentResource, ResourceResolver resourceResolver) {
        ValueMap contentPolicyProperties = new ValueMapDecorator(new HashMap<>());
        ContentPolicyManager contentPolicyManager = resourceResolver.adaptTo(ContentPolicyManager.class);
        if (contentPolicyManager != null) {
            ContentPolicy policy = contentPolicyManager.getPolicy(componentResource);
            if (policy != null) {
                contentPolicyProperties = policy.getProperties();
            }
        }
        return contentPolicyProperties;
    }

    /**
     * check if string value a regex
     * @param value string to check if its a regex
     * @return does string match regex check
     */
    public static boolean isStringRegex(Object value) {
        if (value == null || !(value instanceof String)) {
            return false;
        }
        return isStringRegex(value.toString());
    }

    /**
     * check if string value a regex
     * @param value string to check if its a regex
     * @return does string match regex check
     */
    public static boolean isStringRegex(String value) {
        return isStringRegex(value, STRING_EXPRESSION_CHECK);
    }

    /**
     * check if string value a regex
     * @param value string to check if its a regex
     * @param patternToUse regex to use to check string
     * @return does string match regex check
     */
    public static boolean isStringRegex(String value, String patternToUse) {
        try {
            Pattern valueIsRegexPattern = Pattern.compile(patternToUse);
            return valueIsRegexPattern.matcher(value).matches();
        } catch (PatternSyntaxException ex) {
            LOGGER.error("isStringRegex: could not check if string is a regex, ex={}", ex);
        }
        return false;
    }

    /**
     * evaluate expression in a context with value
     * reference https://commons.apache.org/proper/commons-jexl/reference/syntax.html
     * @param jxlt JXTL Engine
     * @param jc JXTL Context
     * @param expression regex expression
     * @param value value to add into JXTL context
     * @return returns evaluated value
     * @throws JexlException throes Jexl errors with regex expression
     *
     */
    public static Object evaluateExpressionWithValue(JxltEngine jxlt, JexlContext jc, Object expression, Object value) {
       return evaluateExpressionWithValue(jxlt,jc, expression != null ? expression.toString() : "", value);
    }

    /**
     * evaluate expression in a context with value
     * reference https://commons.apache.org/proper/commons-jexl/reference/syntax.html
     * @param jxlt JXTL Engine
     * @param jc JXTL Context
     * @param expression regex expression
     * @param value value to add into JXTL context
     * @return returns evaluated value
     * @throws JexlException throes Jexl errors with regex expression
     *
     */
    public static Object evaluateExpressionWithValue(JxltEngine jxlt, JexlContext jc, String expression, Object value) {
        JxltEngine.Expression expr = jxlt.createExpression(expression);

        //add current value to the map
        jc.set("value", value);

        Object returnValue = "";

        try {
            returnValue = expr.evaluate(jc);
        } catch (Exception ex) {
            returnValue = "";
        }

        //clean up
        jc.set("value", "");

        return returnValue;
    }

    /**
     * replace regex chars in a string
     * @param value regex expression
     * @return updated string or original string
     */
    @SuppressWarnings({"squid:S4784"})
    public static String removeRegexFromString(String value) {
        try {
            Pattern valueIsRegexPattern = Pattern.compile("(\\$\\{.*?\\})");
            return value.replaceAll(valueIsRegexPattern.pattern(), StringUtils.EMPTY);
        } catch (PatternSyntaxException ex) {
            LOGGER.error("removeRegexFromString: could not remove patterns from string, ex={}", ex);
        }
        return value;
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

        FastDateFormat dateFormat = FastDateFormat.getInstance(DEFAULT_RSS_DATE_FORMAT);
        return dateFormat.format(cal);
    }


    /**
     * Transform calendar into a publication date.
     *
     * @param cal is the calendar to transform
     * @param format format to use
     * @return is the formatted RSS date.
     */
    public static String formatDate(Calendar cal, String format) {
        if (cal == null || format == null) {
            return null;
        }

        FastDateFormat dateFormat = FastDateFormat.getInstance(format);
        return dateFormat.format(cal);
    }


    /**
     * Get the unique identifier for this paqe
     *
     * @param listPage is the page to uniqify
     * @return the md5 hash of the page's content path
     */
    public static String getUniquePageIdentifier(Page listPage) {
        if (listPage!= null) {
            String uniqueBase = listPage.getPath().substring(1).replace(FileSystem.SEPARATOR, "-");

            return hashMd5(uniqueBase);
        }
        return StringUtils.EMPTY;
    }
}
