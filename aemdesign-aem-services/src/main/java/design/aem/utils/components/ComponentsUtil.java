package design.aem.utils.components;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagConstants;
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

    public static final String DEFAULT_TENANT = "aemdesign";

    public static final String DEFAULT_PATH_TAGS = "/content/" + TagConstants.PN_TAGS;

    public static final String FIELD_VARIANT = "variant";
    public static final String FIELD_VARIANT_LABEL = "variantHiddenLabel";
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

    public static final String FIELD_HIDEINMENU = "hideInMenu";

    public static final Pattern DEFAULT_RENDTION_PATTERN_OOTB = Pattern.compile("cq5dam\\.(.*)?\\.(\\d+)\\.(\\d+)\\.(.*)"); // NOSONAR its safe
    public static final String DEFAULT_ASSET_RENDITION_PREFIX1 = "cq5dam.thumbnail.";
    public static final String DEFAULT_ASSET_RENDITION_PREFIX2 = "cq5dam.web.";

    public static final String DEFAULT_IMAGE_GENERATED_FORMAT = "{0}.img.jpeg/{1}.jpeg";

    public static final String DEFAULT_IMAGE_RESOURCETYPE = "aemdesign/components/media/image";
    public static final String DEFAULT_IMAGE_RESOURCETYPE_SUFFIX = "/components/media/image";

    public static final String COMPONENT_ATTRIBUTES = "componentAttributes";
    public static final String COMPONENT_INSTANCE_NAME = "instanceName";
    public static final String COMPONENT_TARGET_RESOURCE = "targetResource";
    public static final String COMPONENT_ATTRIBUTE_CLASS = "class";
    public static final String COMPONENT_ATTRIBUTE_ID = "id";
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

    public static final String FIELD_DATA_ANALYTICS_METATYPE = "data-analytics-metatype";
    public static final String FIELD_DATA_ANALYTICS_FILENAME = "data-analytics-filename";
    public static final String FIELD_DATA_ANALYTICS_EVENT_LABEL = "data-analytics-event-label";

    public static final String FIELD_DATA_ARRAY_SEPARATOR = ",";
    public static final String FIELD_DATA_TAG_SEPARATOR = " ";

    public static final String FIELD_TITLE = "title";
    public static final String FIELD_TITLE_FORMAT = "titleFormat";
    public static final String FIELD_TITLE_FORMATTED = "titleFormatted";
    public static final String FIELD_TITLE_FORMATTED_TEXT = "titleFormattedText";
    public static final String FIELD_TITLE_TAG_TYPE = "titleType";
    public static final String FIELD_HIDE_TITLE = "hideTitle";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_DESCRIPTION_FORMAT = "descriptionFormat";
    public static final String FIELD_DESCRIPTION_FORMATTED = "descriptionFormatted";
    public static final String FIELD_HIDE_DESCRIPTION = "hideDescription";
    public static final String FIELD_SHOW_BREADCRUMB = "showBreadcrumb";
    public static final String FIELD_SHOW_TOOLBAR = "showToolbar";
    public static final String FIELD_SHOW_PAGEDATE = "showPageDate";
    public static final String FIELD_SHOW_PARSYS = "showParsys";
    public static final String FIELD_USE_CONTAINER = "useContainer";

    public static final String FIELD_CATEGORY = "category";
    public static final String FIELD_SUBCATEGORY = "subCategory";
    public static final String FIELD_TAGS = TagConstants.PN_TAGS;

    public static final String FIELD_LICENSE_INFO = "licenseInfo";
    public static final String FIELD_ASSETID = "asset-id";
    public static final String FIELD_ASSET_LICENSED = "asset-licensed";
    public static final String FIELD_ASSET_TRACKABLE = "asset-trackable";
    public static final String FIELD_DATA_ASSET_PRIMARY_ID = "data-asset-primary-id";
    public static final String FIELD_DATA_ASSET_PRIMARY_LICENSE = "data-asset-primary-license";
    public static final String FIELD_DATA_ASSET_SECONDARY_ID = "data-asset-secondary-id";
    public static final String FIELD_DATA_ASSET_SECONDARY_LICENSE = "data-asset-secondary-license";

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

    public static final String DETAILS_SELECTOR_BADGE = "badge";

    private static final String STRING_EXPRESSION_CHECK = ".*(\\$\\{.*?\\}).*";

    /**
     * Component Style
     * <p>
     * Structure:
     * 1 required - property name,
     * 2 required - default value,
     * 3 optional - name of component attribute to add value into
     * 4 optional - canonical name of class for handling multivalues, String or Tag
     */
    public static final Object[][] DEFAULT_FIELDS_STYLE = { // NOSONAR used by classes
        {FIELD_STYLE_COMPONENT_ID, StringUtils.EMPTY, COMPONENT_ATTRIBUTE_ID},
        {FIELD_STYLE_COMPONENT_THEME, new String[]{}, COMPONENT_ATTRIBUTE_CLASS, Tag.class.getCanonicalName()},
        {FIELD_STYLE_COMPONENT_MODIFIERS, new String[]{}, COMPONENT_ATTRIBUTE_CLASS, Tag.class.getCanonicalName()},
        {FIELD_STYLE_COMPONENT_MODULES, new String[]{}, "data-modules", Tag.class.getCanonicalName()},
        {FIELD_STYLE_COMPONENT_CHEVRON, new String[]{}, COMPONENT_ATTRIBUTE_CLASS, Tag.class.getCanonicalName()},
        {FIELD_STYLE_COMPONENT_ICON, new String[]{}, COMPONENT_ATTRIBUTE_CLASS, Tag.class.getCanonicalName()},
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
     * <p>
     * Structure:
     * 1 required - property name,
     * 2 required - default value,
     * 3 optional - name of component attribute to add value into
     * 4 optional - canonical name of class for handling multivalues, String or Tag
     */
    public static final Object[][] DEFAULT_FIELDS_ACCESSIBILITY = { // NOSONAR used by classes
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
     * Badge Metadata used by Details Components
     * <p>
     * Structure:
     * 1 required - property name,
     * 2 required - default value,
     * 3 optional - name of component attribute to add value into
     * 4 optional - canonical name of class for handling multivalues, String or Tag
     */
    public static final Object[][] DEFAULT_FIELDS_DETAILS_OPTIONS = { // NOSONAR used by classes
        {DETAILS_MENU_COLOR, StringUtils.EMPTY},
        {DETAILS_MENU_ICONSHOW, false},
        {DETAILS_MENU_ICON, new String[]{}, StringUtils.EMPTY, Tag.class.getCanonicalName()},
        {DETAILS_MENU_ACCESS_KEY, StringUtils.EMPTY},
        {DETAILS_CARD_STYLE, new String[]{}, StringUtils.EMPTY, Tag.class.getCanonicalName()},
        {DETAILS_CARD_ICONSHOW, false},
        {DETAILS_CARD_ICON, new String[]{}, StringUtils.EMPTY, Tag.class.getCanonicalName()},
        {DETAILS_LINK_TARGET, "_blank"},
        {DETAILS_LINK_TEXT, "${value ? value : (" + FIELD_PAGE_TITLE_NAV + " ? " + FIELD_PAGE_TITLE_NAV + " : '')}"},
        {DETAILS_LINK_TITLE, "${value ? value : (" + FIELD_PAGE_TITLE + " ? " + FIELD_PAGE_TITLE + " : '')}"},
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
        {DETAILS_BADGE_ANALYTICS_TRACK, StringUtils.EMPTY, DETAILS_DATA_ANALYTICS_TRACK},
        {DETAILS_BADGE_ANALYTICS_LOCATION, StringUtils.EMPTY, DETAILS_DATA_ANALYTICS_LOCATION},
        {DETAILS_BADGE_ANALYTICS_LABEL, "${value ?  value : " + DETAILS_LINK_TEXT + "}", DETAILS_DATA_ANALYTICS_LABEL},
        {DETAILS_PAGE_METADATA_PROPERTY, new String[]{}},
        {DETAILS_PAGE_METADATA_PROPERTY_CONTENT, new String[]{}},
        {DETAILS_BADGE_CUSTOM, false},
        {DETAILS_BADGE_FIELDS_TEMPLATE, new String[]{}},
        {DETAILS_BADGE_FIELDS, new String[]{}},
        {DETAILS_BADGE_TEMPLATE, StringUtils.EMPTY}, //after DETAILS_BADGE_FIELDS_TEMPLATE and DETAILS_BADGE_FIELDS as it will override them
    };

    /**
     * Badge Metadata used in Lists
     * <p>
     * Structure:
     * 1 required - property name,
     * 2 required - default value,
     * 3 optional - name of component attribute to add value into
     * 4 optional - canonical name of class for handling multivalues, String or Tag
     */
    public static final Object[][] DEFAULT_FIELDS_DETAILS_OPTIONS_OVERRIDE = { // NOSONAR used by classes
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
        {DETAILS_BADGE_ANALYTICS_TRACK, StringUtils.EMPTY, DETAILS_DATA_ANALYTICS_TRACK}, //basic
        {DETAILS_BADGE_ANALYTICS_LOCATION, StringUtils.EMPTY, DETAILS_DATA_ANALYTICS_LOCATION}, //basic
        {DETAILS_BADGE_ANALYTICS_LABEL, StringUtils.EMPTY, DETAILS_DATA_ANALYTICS_LABEL}, //basic
        {DETAILS_BADGE_CUSTOM, false},
        {DETAILS_BADGE_FIELDS_TEMPLATE, new String[]{}},
        {DETAILS_BADGE_FIELDS, new String[]{}},
        {DETAILS_BADGE_TEMPLATE, StringUtils.EMPTY}, //after DETAILS_BADGE_FIELDS_TEMPLATE and DETAILS_BADGE_FIELDS as it will override them

    };

    /**
     * Component Analytics
     * <p>
     * Structure:
     * 1 required - property name,
     * 2 required - default value,
     * 3 optional - name of component attribute to add value into
     * 4 optional - canonical name of class for handling multivalues, String or Tag
     */
    public static final Object[][] DEFAULT_FIELDS_ANALYTICS = { // NOSONAR used by classes
        {DETAILS_ANALYTICS_TRACK, false, DETAILS_DATA_ANALYTICS_TRACK}, //basic
        {DETAILS_ANALYTICS_LOCATION, StringUtils.EMPTY, DETAILS_DATA_ANALYTICS_LOCATION}, //basic
        {DETAILS_ANALYTICS_LABEL, "${ value ? value : label }", DETAILS_DATA_ANALYTICS_LABEL}, //basic
        {"analyticsEventType", StringUtils.EMPTY, "data-analytics-event"}, //advanced
        {"analyticsHitType", StringUtils.EMPTY, "data-analytics-hit-type"}, //advanced
        {"analyticsEventCategory", StringUtils.EMPTY, "data-analytics-event-category"}, //advanced
        {"analyticsEventAction", StringUtils.EMPTY, "data-analytics-event-action"}, //advanced
        {"analyticsEventLabel", StringUtils.EMPTY, FIELD_DATA_ANALYTICS_EVENT_LABEL}, //advanced
        {"analyticsTransport", StringUtils.EMPTY, "data-analytics-transport"}, //advanced
        {"analyticsNonInteraction", StringUtils.EMPTY, "data-analytics-noninteraction"}, //advanced
    };

    /**
     * Link Attributes
     * <p>
     * Structure:
     * 1 required - property name,
     * 2 required - default value,
     * 3 optional - name of component attribute to add value into
     * 4 optional - canonical name of class for handling multivalues, String or Tag
     */
    public static final Object[][] DEFAULT_FIELDS_ATTRIBUTES = { // NOSONAR used by classes
        {"dataType", StringUtils.EMPTY, "type"},
        {"dataTarget", StringUtils.EMPTY, "data-target"},
        {"dataToggle", StringUtils.EMPTY, "data-toggle"},
    };

    /**
     * Asset Metadata
     * <p>
     * Structure:
     * 1 required - property name,
     * 2 required - default value,
     * 3 optional - name of component attribute to add value into
     * 4 optional - canonical name of class for handling multivalues, String or Tag
     */
    public static final Object[][] DEFAULT_FIELDS_ASSET = { // NOSONAR used by classes
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
     * <p>
     * Structure:
     * 1 required - property name,
     * 2 required - default value,
     * 3 optional - name of component attribute to add value into
     * 4 optional - canonical name of class for handling multivalues, String or Tag
     */
    public static final Object[][] DEFAULT_FIELDS_ASSET_IMAGE = { // NOSONAR used by classes
        {CommonUtil.DAM_TITLE, StringUtils.EMPTY},
        {CommonUtil.DAM_DESCRIPTION, StringUtils.EMPTY},
        {CommonUtil.DAM_CREDIT, StringUtils.EMPTY},
        {CommonUtil.DAM_HEADLINE, StringUtils.EMPTY},
        {CommonUtil.DAM_SOURCE, StringUtils.EMPTY},
        {CommonUtil.DAM_SOURCE_URL, StringUtils.EMPTY},
    };


    private ComponentsUtil() {
    }

    /**
     * Get a include file contents
     *
     * @param resourceResolver is the resource
     * @param paths            paths to check
     * @param separator        separator to use
     * @return a string with the file contents
     */
    public static String getResourceContent(ResourceResolver resourceResolver, String[] paths, String separator) {
        String returnValue = StringUtils.EMPTY;

        for (String path : paths) {
            Resource resource = resourceResolver.getResource(path);

            returnValue = returnValue.concat(getResourceContent(resource));

            if (StringUtils.isNotEmpty(separator)) {
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

                    if (resourceNode.getPrimaryNodeType().getName().equals(NameConstants.NT_PAGE)
                        && resourceNode.hasNode(JcrConstants.JCR_CONTENT)) {
                        contentNode = resourceNode.getNode(JcrConstants.JCR_CONTENT);
                    }

                    String originalPath = JcrConstants.JCR_CONTENT.concat("/")
                        .concat(DamConstants.RENDITIONS_FOLDER)
                        .concat("/original/")
                        .concat(JcrConstants.JCR_CONTENT);

                    if (resourceNode.getPrimaryNodeType().getName().equals(DamConstants.NT_DAM_ASSET) && resourceNode.hasNode(originalPath)) {
                        contentNode = resourceNode.getNode(originalPath);
                    }

                    if (contentNode != null && contentNode.hasProperty(JcrConstants.JCR_DATA)) {
                        InputStream contentsStream = contentNode.getProperty(JcrConstants.JCR_DATA).getBinary().getStream();
                        byte[] result;

                        BufferedInputStream bin = new BufferedInputStream(contentsStream);

                        result = IOUtils.toByteArray(bin);

                        bin.close();
                        contentsStream.close();

                        if (result != null) {
                            returnValue = new String(result);
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
     *
     * @param componentProperties component properties
     * @param contentPolicy       value map of content policy
     * @param name                name of the property
     * @param defaultValue        default value for the property
     * @param useStyle            use styles properties if property is missing
     * @return component property
     */
    @SuppressWarnings("Duplicates")
    public static Object getComponentProperty(
        ValueMap componentProperties,
        ValueMap contentPolicy,
        String name,
        Object defaultValue,
        boolean useStyle
    ) {
        if (componentProperties == null) {
            LOGGER.warn("Cannot retrieve component property as 'componentProperties' is 'null'.");

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
     * Returns component values with defaults from the page content properties.
     *
     * @param pageContext   current page context
     * @param componentPage target page
     * @param componentPath target component path
     * @param fieldLists    list of field definitions
     * @return map of component attributes
     */
    public static ComponentProperties getComponentProperties(
        PageContext pageContext,
        Page componentPage,
        String componentPath,
        Object[][]... fieldLists
    ) {
        try {
            Resource componentResource = componentPage.getContentResource(componentPath);

            return getComponentProperties(pageContext, componentResource, fieldLists);
        } catch (Exception ex) {
            LOGGER.error("Unable to get component properties for the component path: {}, error: {}",
                componentPath,
                ex.getMessage());
        }

        return getNewComponentProperties(pageContext);
    }

    /**
     * Helper to create a new {@link ComponentProperties} instance.
     *
     * @param pageContext page context
     * @return {@link ComponentProperties} instance
     */
    public static ComponentProperties getNewComponentProperties(PageContext pageContext) {
        SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) pageContext
            .getAttribute(PAGECONTEXTMAP_OBJECT_SLINGREQUEST);

        Map<String, Object> pageContextMap = new HashMap<>();
        pageContextMap.put(PAGECONTEXTMAP_OBJECT_SLINGREQUEST, slingRequest);

        return getNewComponentProperties(pageContextMap);
    }

    /**
     * Helper to create a new {@link ComponentProperties} instance.
     *
     * @param model component model pojo
     * @return {@link ComponentProperties} instance
     */
    public static ComponentProperties getNewComponentProperties(WCMUsePojo model) {
        SlingHttpServletRequest slingRequest = model.getRequest();

        Map<String, Object> pageContextMap = new HashMap<>();
        pageContextMap.put(PAGECONTEXTMAP_OBJECT_SLINGREQUEST, slingRequest);

        return getNewComponentProperties(pageContextMap);
    }

    /**
     * Helper to create a new {@link ComponentProperties} instance.
     *
     * @param pageContext page context
     * @return {@link ComponentProperties} instance
     */
    public static ComponentProperties getNewComponentProperties(Map<String, Object> pageContext) {
        ComponentProperties componentProperties = new ComponentProperties();

        try {
            SlingScriptHelper sling = (SlingScriptHelper) pageContext.get(PAGECONTEXTMAP_OBJECT_SLING);

            XSSAPI xss = Objects.requireNonNull(sling.getService(XSSAPI.class));

            componentProperties.attr = new AttrBuilder(xss);
        } catch (Exception ex) {
            LOGGER.error("getNewComponentProperties: could not configure componentProperties with attributeBuilder");
        }

        return componentProperties;
    }

    /**
     * Returns component values with defaults from the page content properties.
     *
     * @param model             component model pojo
     * @param includeAttributes include attributes specific to this component instance
     * @param fieldLists        list of field definitions
     * @return {@link ComponentProperties} instance
     */
    public static ComponentProperties getComponentProperties(
        WCMUsePojo model,
        Boolean includeAttributes,
        Object[][]... fieldLists
    ) {
        return getComponentProperties(model, null, includeAttributes, fieldLists);
    }

    /**
     * Returns component values with defaults from the page content properties.
     *
     * @param model      component model pojo
     * @param fieldLists list of field definitions
     * @return {@link ComponentProperties} instance
     */
    public static ComponentProperties getComponentProperties(WCMUsePojo model, Object[][]... fieldLists) {
        return getComponentProperties(model, null, true, fieldLists);
    }


    /**
     * Returns component values with defaults from the page content properties.
     *
     * @param model      generic model
     * @param fieldLists list of field definitions
     * @return {@link ComponentProperties} instance
     */
    public static ComponentProperties getComponentProperties(GenericModel model, Object[][]... fieldLists) {
        return getComponentProperties(model, null, true, fieldLists);
    }


    /**
     * Returns component values with defaults from the page content properties.
     *
     * @param model          component model
     * @param targetResource resource to use as source
     * @param fieldLists     list of field definitions
     * @return {@link ComponentProperties} instance
     */
    public static ComponentProperties getComponentProperties(
        WCMUsePojo model,
        Object targetResource,
        Object[][]... fieldLists
    ) {
        try {
            return getComponentProperties(getContextObjects(model),
                targetResource,
                true,
                fieldLists);
        } catch (Exception ex) {
            LOGGER.error("getComponentProperties(model, targetResource, fieldLists) Could not read required objects: {}, error: {}",
                model,
                ex.getMessage());
        }

        return getNewComponentProperties(model);
    }

    /**
     * Returns component values with defaults from the page content properties.
     *
     * @param model          component model
     * @param targetResource resource to use as source
     * @param fieldLists     list of field definitions
     * @return {@link ComponentProperties} instance
     */
    public static ComponentProperties getComponentProperties(
        GenericModel model,
        Resource targetResource,
        Object[][]... fieldLists
    ) {
        try {
            return getComponentProperties(model.getPageContextMap(),
                targetResource,
                true,
                fieldLists);
        } catch (Exception ex) {
            LOGGER.error("getComponentProperties(model, targetResource, fieldLists) Could not read required objects: {}, error: {}",
                model,
                ex.getMessage());
        }

        return getNewComponentProperties(model.getPageContextMap());
    }

    /**
     * returns component values with defaults from a targetResource, default to pageContext properties.
     *
     * @param pageContext    current page context
     * @param targetResource resource to use as source
     * @param fieldLists     list of field definitions
     * @return {@link ComponentProperties} instance
     */
    public static ComponentProperties getComponentProperties(
        PageContext pageContext,
        Object targetResource,
        Object[][]... fieldLists
    ) {
        return getComponentProperties(pageContext, targetResource, true, fieldLists);
    }

    /**
     * Returns component values with defaults from the page content properties.
     *
     * @param model             component model
     * @param targetResource    resource to use as source
     * @param includeAttributes include additional attributes associated with component
     * @param fieldLists        list of field definitions
     * @return {@link ComponentProperties} instance
     */
    public static ComponentProperties getComponentProperties(
        GenericModel model,
        Object targetResource,
        Boolean includeAttributes,
        Object[][]... fieldLists
    ) {
        try {
            return getComponentProperties(model.getPageContextMap(), targetResource, includeAttributes, fieldLists);
        } catch (Exception ex) {
            LOGGER.error("getComponentProperties(model, targetResource, includeAttributes, fieldLists) Could not read required objects: {}, error: {}",
                model,
                ex.getMessage());
        }

        return getNewComponentProperties(model.getPageContextMap());
    }

    /**
     * Returns component values with defaults from the page content properties.
     *
     * @param model             component model
     * @param targetResource    resource to use as source
     * @param includeAttributes include additional attributes associated with component
     * @param fieldLists        list of field definitions
     * @return {@link ComponentProperties} instance
     */
    public static ComponentProperties getComponentProperties(
        WCMUsePojo model, Object targetResource, Boolean includeAttributes, Object[][]... fieldLists) {
        try {
            return getComponentProperties(getContextObjects(model), targetResource, includeAttributes, fieldLists);
        } catch (Exception ex) {
            LOGGER.error("getComponentProperties(model, targetResource, includeAttributes, fieldLists) Could not read required objects: {}, error: {}",
                model,
                ex.getMessage());
        }


        return getNewComponentProperties(model);
    }

    /**
     * Returns component values with defaults from a {@code targetResource}, default to {@code pageContext} properties.
     *
     * @param pageContext       current page context
     * @param targetResource    resource to use as source
     * @param includeAttributes include additional attributes associated with component
     * @param fieldLists        list of field definitions
     * @return {@link ComponentProperties} instance
     */
    public static ComponentProperties getComponentProperties(
        PageContext pageContext,
        Object targetResource,
        Boolean includeAttributes,
        Object[][]... fieldLists
    ) {
        try {
            return getComponentProperties(getContextObjects(pageContext), targetResource, includeAttributes, fieldLists);
        } catch (Exception ex) {
            LOGGER.error("getComponentProperties(pageContext, targetResource, includeAttributes, fieldLists) Could not read required objects: {}",
                ex.getMessage());
        }

        return getNewComponentProperties(pageContext);
    }

    /**
     * Returns component values with defaults from a {@code targetResource}, default to {@code pageContext} properties.
     *
     * @param pageContext       current page context
     * @param targetResource    resource to use as source
     * @param includeAttributes include additional attributes associated with component
     * @param fieldLists        list of field definitions
     * @return {@link ComponentProperties} instance
     */
    @SuppressWarnings({"Depreciated", "Duplicates", "squid:S3776"})
    public static ComponentProperties getComponentProperties(
        Map<String, Object> pageContext,
        Object targetResource,
        Boolean includeAttributes,
        Object[][]... fieldLists
    ) {
        ComponentProperties componentProperties = new ComponentProperties();

        boolean addMoreAttributes = includeAttributes;

        SlingScriptHelper sling = (SlingScriptHelper) pageContext.get(PAGECONTEXTMAP_OBJECT_SLING);
        ComponentContext componentContext = (ComponentContext) pageContext.get(PAGECONTEXTMAP_OBJECT_COMPONENTCONTEXT);

        Component component = componentContext.getComponent();
        XSSAPI xss = Objects.requireNonNull(sling.getService(XSSAPI.class));

        componentProperties.attr = new AttrBuilder(xss);

        if (addMoreAttributes) {
            componentProperties.attr.add("component", "true");
        }

        componentProperties.expressionFields = new ArrayList<>();

        Resource contentResource = null;

        final String CLASS_TYPE_RESOURCE = Resource.class.getCanonicalName();
        final String CLASS_TYPE_JCRNODERESOURCE = "org.apache.sling.jcr.resource.internal.helper.jcr.JcrNodeResource";
        final String CLASS_TYPE_ASSET = "com.adobe.granite.asset.core.impl.AssetImpl";

        ContentAccess contentAccess = sling.getService(ContentAccess.class);

        if (contentAccess == null) {
            throw new NullPointerException("Could not get ContentAccess service");
        }

        try (ResourceResolver adminResourceResolver = contentAccess.getAdminResourceResolver()) {
            Node currentNode = (javax.jcr.Node) pageContext.get(PAGECONTEXTMAP_OBJECT_CURRENTNODE);
            TagManager tagManager = adminResourceResolver.adaptTo(TagManager.class);

            // if targetResource == null get defaults
            ValueMap properties = (ValueMap) pageContext.get("properties"); // NOSONAR getting properties from pageContext
            ValueMap currentPolicy = getContentPolicyProperties(componentContext.getResource(), adminResourceResolver);

            //if targetResource != null get the appropriate objects
            if (targetResource != null && targetResource.getClass().getCanonicalName().equals(CLASS_TYPE_ASSET)) {
                try { // NOSONAR
                    com.adobe.granite.asset.api.Asset asset = (com.adobe.granite.asset.api.Asset) targetResource;

                    contentResource = asset.getResourceResolver().getResource(asset, JcrConstants.JCR_CONTENT);

                    if (contentResource != null) {
                        contentResource = contentResource.getChild(DamConstants.METADATA_FOLDER);
                    }

                    addMoreAttributes = false;
                    properties = contentResource.adaptTo(ValueMap.class);

                    componentProperties.put(COMPONENT_TARGET_RESOURCE, contentResource.getPath());
                } catch (Exception ex) {
                    LOGGER.error("getComponentProperties: Could not evaluate target asset", ex);

                    return componentProperties;
                }
            } else if (targetResource != null &&
                (
                    targetResource.getClass().getCanonicalName().equals(CLASS_TYPE_RESOURCE) ||
                        targetResource.getClass().getCanonicalName().equals(CLASS_TYPE_JCRNODERESOURCE)
                )
            ) {
                try { // NOSONAR
                    contentResource = (Resource) targetResource;
                    currentPolicy = getContentPolicyProperties(contentResource, adminResourceResolver);
                    properties = contentResource.adaptTo(ValueMap.class);

                    ComponentManager componentManager = contentResource.getResourceResolver().adaptTo(ComponentManager.class);
                    Component resourceComponent = componentManager.getComponentOfResource(contentResource);

                    // Set the component to match target resource component
                    if (resourceComponent != null) {
                        component = resourceComponent;
                    }

                    // Set the current node to match target resource node
                    Node resourceNode = contentResource.adaptTo(Node.class);

                    if (resourceNode != null) {
                        currentNode = resourceNode;
                    }

                    componentProperties.put(COMPONENT_TARGET_RESOURCE, contentResource.getPath());
                } catch (Exception ex) {
                    LOGGER.error("getComponentProperties: Could not evaluate target resource", ex);

                    return componentProperties;
                }
            } else if (targetResource != null) {
                LOGGER.warn("getComponentProperties: Processing is unsupported of target resource of type: {}",
                    targetResource.getClass().getCanonicalName());
            } else {
                LOGGER.warn("getComponentProperties: Processing of NULL target resource of type return design defaults");
            }

            if (currentNode != null && addMoreAttributes) {
                componentProperties.put(COMPONENT_INSTANCE_NAME, currentNode.getName());

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
                            throw new IllegalArgumentException(format("Name, Default Value, ..., Value-n expected, instead only {0} fields were supplied.", field.length));
                        }

                        // Read the first field, this is the field name
                        String fieldName = field[0].toString();

                        // Read the second field, this is the default value/expression value
                        Object fieldDefaultValue = field[1];

                        // Set default value to an empty string if the default value is null
                        if (isNull(fieldDefaultValue)) {
                            fieldDefaultValue = StringUtils.EMPTY;
                        }

                        // Read the third field, this is an attribute name
                        String fieldAttribute = StringUtils.EMPTY;

                        if (field.length > 2) {
                            fieldAttribute = field[2].toString();
                        }

                        // Read the forth field, this define the field value type (i.e. String, String[])
                        String fieldValueType = field.length > 3 ? (String) field[3] : String.class.getCanonicalName();

                        // Skip fields that already exist, this most-commonly occurs when a project overrides AEM.Design
                        if (componentProperties.containsKey(fieldName)) {
                            LOGGER.info("getComponentProperties: Skipping property '{}' as it is already defined, {}",
                                fieldName,
                                componentContext.getResource().getPath());

                            continue;
                        }

                        Object fieldValue;
                        boolean fieldValueHasExpressions = false;

                        // Complete the next step only when 'fieldValueType' is an instance of String.
                        // If the default value has expressions we try to evaluate these if they match something else.
                        if (fieldDefaultValue instanceof String &&
                            StringUtils.isNotEmpty(fieldDefaultValue.toString()) &&
                            fieldValueType.equals(String.class.getCanonicalName()) &&
                            isStringRegex(fieldDefaultValue.toString())
                        ) {
                            fieldValue = getComponentProperty(properties, currentPolicy, fieldName, null, true);

                            boolean expressionValid = false;

                            try { // NOSONAR
                                Object expressionResult = evaluateExpressionWithValue(jxlt, jc, fieldDefaultValue.toString(), fieldValue);

                                if (expressionResult != null) {
                                    expressionValid = true;
                                    fieldDefaultValue = expressionResult;
                                }
                            } catch (JexlException jex) {
                                LOGGER.warn("Could not evaluate default value expression component={}, contentResource={}, currentNode={}, field={}, value={}, default value={}, componentProperties.keys={}, jex.info={}",
                                    (component == null ? component : component.getPath()),
                                    (contentResource == null ? contentResource : contentResource.getPath()),
                                    (currentNode == null ? currentNode : currentNode.getPath()),
                                    fieldName, fieldValue, fieldDefaultValue,
                                    componentProperties.keySet(), jex.getInfo());
                            } catch (Exception ex) {
                                LOGGER.error("Could not evaluate default value expression component={}, contentResource={}, currentNode={}, field={}, value={}, default value={}, componentProperties.keys={}, ex.cause={}, ex.message={}, ex={}",
                                    (component == null ? component : component.getPath()),
                                    (contentResource == null ? contentResource : contentResource.getPath()),
                                    (currentNode == null ? currentNode : currentNode.getPath()),
                                    fieldName, fieldValue, fieldDefaultValue,
                                    componentProperties.keySet(), ex.getCause(), ex.getMessage(), ex);
                            }

                            if (!expressionValid) {
                                fieldDefaultValue = removeRegexFromString((String) fieldDefaultValue);
                            }

                            fieldValue = fieldDefaultValue;
                        } else {
                            fieldValue = getComponentProperty(properties, currentPolicy, fieldName, fieldDefaultValue, true);
                        }

                        // Secondary expression check, this only work when the field canonical type is not String
                        if (fieldValue instanceof String &&
                            StringUtils.isNotEmpty(fieldValue.toString()) &&
                            isStringRegex(fieldValue.toString())) {
                            fieldValueHasExpressions = true;
                        }

                        // Empty array with empty string will set the default value
                        if ((fieldValue instanceof String && StringUtils.isEmpty(fieldValue.toString())) ||
                            (
                                fieldValue instanceof String[] &&
                                    StringUtils.isEmpty(StringUtils.join((String[]) fieldValue, StringUtils.EMPTY))
                            )
                        ) {
                            fieldValue = fieldDefaultValue;
                        }

                        // Fix values that we expect to be an array but aren't
                        if (fieldValueType.equals(Tag.class.getCanonicalName()) && !fieldValue.getClass().isArray()) {
                            fieldValue = new String[]{(String) fieldValue};
                        } else if ((fieldValueType.getClass().isArray() || fieldDefaultValue.getClass().isArray()) &&
                            !fieldValue.getClass().isArray()
                        ) {
                            Class<?> arrayType = fieldValue.getClass().getComponentType();
                            Object newArray = Array.newInstance(arrayType, 1);
                            Array.set(newArray, 1, fieldValue);

                            fieldValue = newArray;
                        }

                        if (field.length > 2) {
                            String fieldValueString = StringUtils.EMPTY;

                            if (fieldValue.getClass().isArray()) {
                                if (ArrayUtils.isNotEmpty((String[]) fieldValue)) {
                                    // Handle tags as values
                                    if (fieldValueType.equals(Tag.class.getCanonicalName())) {
                                        // If an attribute was not specified, return values as map entry
                                        if (isEmpty(fieldAttribute)) {
                                            fieldValue = TagUtil.getTagsValues(tagManager,
                                                adminResourceResolver, " ",
                                                (String[]) fieldValue);

                                            if (Arrays.stream((String[]) fieldValue).anyMatch(ComponentsUtil::isStringRegex)) {
                                                fieldValueHasExpressions = true;
                                            }
                                        } else {
                                            fieldValueString = TagUtil.getTagsAsValues(tagManager,
                                                adminResourceResolver,
                                                FIELD_DATA_TAG_SEPARATOR,
                                                (String[]) fieldValue);

                                            if (isStringRegex(fieldValueString)) {
                                                fieldValueHasExpressions = true;
                                            }
                                        }

                                        // Handle normal strings
                                    } else {
                                        fieldValueString = StringUtils.join((String[]) fieldValue, FIELD_DATA_ARRAY_SEPARATOR);
                                    }
                                } else {
                                    if (isEmpty(fieldAttribute)) {
                                        fieldValue = StringUtils.EMPTY;
                                    }
                                }
                            } else {
                                fieldValueString = fieldValue.toString();
                            }

                            if (isNotEmpty(fieldValueString) &&
                                isNotEmpty(fieldAttribute) &&
                                !fieldAttribute.equals(FIELD_VALUES_ARE_ATTRIBUTES)
                            ) {
                                componentProperties.attr.add(fieldAttribute, fieldValueString);

                                // Handle possible <key, value> pairs that may be in the values
                            } else if (isNotEmpty(fieldValueString) && fieldAttribute.equals(FIELD_VALUES_ARE_ATTRIBUTES)) {
                                if (fieldValueString.contains(FIELD_DATA_TAG_SEPARATOR)) {
                                    for (String item : fieldValueString.split(FIELD_VALUES_ARE_ATTRIBUTES)) {
                                        if (!item.contains("=")) {
                                            componentProperties.attr.add(item, "true");
                                        } else {
                                            String[] items = item.split("=");

                                            componentProperties.attr.add(items[0],
                                                StringUtils.substringBetween(items[1], "\"", "\""));
                                        }
                                    }
                                } else {
                                    if (!fieldValueString.contains("=")) {
                                        componentProperties.attr.add(fieldValueString, "true");
                                    } else {
                                        String[] items = fieldValueString.split("=");

                                        componentProperties.attr.add(items[0],
                                            StringUtils.substringBetween(items[1], "\"", "\""));
                                    }
                                }
                            }
                        }

                        // When expressions are detections, store them in 'evaluateExpressionFields' so they can be used
                        // by other fields later on.
                        if (fieldValueHasExpressions) {
                            ComponentField expField = new ComponentField(field);

                            expField.setValue(fieldValue);
                            componentProperties.expressionFields.add(expField);
                        }

                        try { // NOSONAR
                            componentProperties.put(fieldName, fieldValue);
                        } catch (Exception ex) {
                            LOGGER.error("Error adding field. Field: {}, Error: {}", fieldName, ex.getMessage());
                        }
                    }
                }

                // The final piece of the puzzle is handling badges and variants
                String badge = componentProperties.get(DETAILS_BADGE_TEMPLATE, StringUtils.EMPTY);
                String variant = componentProperties.get(FIELD_VARIANT, StringUtils.EMPTY);

                if (addMoreAttributes) {
                    if (isEmpty(variant)) {
                        variant = DEFAULT_VARIANT;

                        componentProperties.put(FIELD_VARIANT_LEGACY, true);
                    } else {
                        ComponentProperties variantConfig = getTemplateConfig(pageContext,
                            variant,
                            adminResourceResolver,
                            tagManager,
                            FIELD_VARIANT_FIELDS_TEMPLATE,
                            FIELD_VARIANT_FIELDS,
                            FIELD_VARIANT);

                        componentProperties.putAll(variantConfig);

                        variant = componentProperties.get(FIELD_VARIANT, StringUtils.EMPTY);
                    }

                    // Get the badge configuration, this is how lists interact with the details badges
                    if (isNotEmpty(badge)) {
                        ComponentProperties badgeConfig = getTemplateConfig(pageContext,
                            badge,
                            adminResourceResolver,
                            tagManager,
                            DETAILS_BADGE_FIELDS_TEMPLATE,
                            DETAILS_BADGE_FIELDS,
                            "detailsBadge");

                        componentProperties.putAll(badgeConfig);
                    }

                    String variantTemplate = getComponentVariantTemplate(component,
                        format(COMPONENT_VARIANT_TEMPLATE_FORMAT, variant),
                        sling);

                    componentProperties.put(COMPONENT_VARIANT_TEMPLATE, variantTemplate);
                }

                // Add the variant name to the component attributes
                if (!componentProperties.attr.isEmpty() && addMoreAttributes) {
                    if (isNotEmpty(variant) && !variant.equals(DEFAULT_VARIANT)) {
                        componentProperties.attr.add(COMPONENT_ATTRIBUTE_CLASS, variant);
                    }

                    componentProperties.put(COMPONENT_ATTRIBUTES,
                        buildAttributesString(componentProperties.attr.getAttributes(), xss));
                }
            }
        } catch (Exception ex) {
            LOGGER.error("getComponentProperties: Error processing properties: Component Path: {}, Error: {}",
                component != null ? component.getPath() : "Unknown",
                ex.getMessage());
        }

        return componentProperties;
    }

    /**
     * Get template structure config from tags for badge or variant.
     *
     * @param pageContext                current page context
     * @param configTag                  tag to use for lookup
     * @param resourceResolver           instance of resource resolver
     * @param tagManager                 instance of tag manager
     * @param fieldNameTemplates         return field name for templates
     * @param fieldNameFields            return field name for fields
     * @param fieldNameFirstTemplateName return field name for template name
     * @return return map of fields with values
     */
    @SuppressWarnings("squid:S3776")
    public static ComponentProperties getTemplateConfig(
        Map<String, Object> pageContext,
        String configTag,
        ResourceResolver resourceResolver,
        TagManager tagManager,
        String fieldNameTemplates,
        String fieldNameFields,
        String fieldNameFirstTemplateName
    ) {
        ComponentProperties componentProperties = getNewComponentProperties(pageContext);

        if (isEmpty(configTag)) {
            return componentProperties;
        }

        Tag tagConfig = getTag(configTag, resourceResolver, tagManager);

        componentProperties.put(FIELD_VARIANT_LEGACY, tagConfig == null);

        if (tagConfig != null) {
            Resource tagConfigResource = resourceResolver.getResource(tagConfig.getPath());

            if (tagConfigResource != null) {
                ValueMap tagConfigMap = tagConfigResource.adaptTo(ValueMap.class);

                if (tagConfigMap != null) {
                    if (tagConfigMap.containsKey(FIELD_TAG_TEMPLATE_CONFIG_TEMPLATES)) {
                        String[] template = tagConfigMap.get(FIELD_TAG_TEMPLATE_CONFIG_TEMPLATES, new String[]{});

                        componentProperties.put(fieldNameTemplates, template);

                        if (template.length > 0) {
                            componentProperties.put(fieldNameFirstTemplateName, template[0]);
                        }
                    } else {
                        if (tagConfigMap.containsKey(FIELD_TAG_TEMPLATE_CONFIG_VALUE)) {
                            String value = tagConfigMap.get(FIELD_TAG_TEMPLATE_CONFIG_VALUE, DEFAULT_VARIANT);

                            componentProperties.put(fieldNameFirstTemplateName, value);
                        }
                    }

                    if (tagConfigMap.containsKey(FIELD_TAG_TEMPLATE_CONFIG_FIELDS)) {
                        String[] fields = tagConfigMap.get(FIELD_TAG_TEMPLATE_CONFIG_FIELDS, new String[]{});

                        componentProperties.put(fieldNameFields, fields);
                    }
                }
            }
        }

        return componentProperties;
    }

    /**
     * Return the variant template name from component.
     *
     * @param component {@link Component} object
     * @param template  variant template
     * @param sling     {@link SlingScriptHelper} instance
     * @return variant template path
     */
    public static String getComponentVariantTemplate(Component component, String template, SlingScriptHelper sling) {
        if (component != null && isNotEmpty(template)) {
            String variantTemplatePath = findLocalResourceInSuperComponent(component, template, sling);

            if (isNotBlank(variantTemplatePath)) {
                return variantTemplatePath;
            }
        }

        return format(COMPONENT_VARIANT_TEMPLATE_FORMAT, DEFAULT_VARIANT);
    }


    /**
     * Build the attributes string from the provided {@link Map} without encoding.
     *
     * @param data {@link Map} of attributes and values
     * @param xss  {@link XSSAPI} instance
     * @return attributes string
     */
    public static String buildAttributesString(Map<String, String> data, XSSAPI xss) {
        return buildAttributesString(data, xss, new HashMap<>());
    }

    /**
     * Build the attributes string from the provided {@link Map} using the provided encoding types.
     *
     * @param attributes {@link Map} of attributes and values
     * @param xss        {@link XSSAPI} instance
     * @param encodings  {@link Map} of field {@link AttrBuilder.EncodingType}'s
     * @return attributes string
     */
    public static String buildAttributesString(
        Map<String, String> attributes,
        XSSAPI xss,
        @Nonnull Map<String, AttrBuilder.EncodingType> encodings
    ) {
        AttrBuilder attr = new AttrBuilder(xss);

        attributes.forEach((attribute, value) -> {
            if (value == null) {
                return;
            }

            attr.add(attribute, value, encodings.get(attribute));
        });

        return attr.build().replace("&#x20;", " ");
    }

    /**
     * Find the summary field in a 'detail' component or just return the page description.
     *
     * @param page {@link Page} instance
     * @return page description
     */
    public static String getPageDescription(Page page) {
        String pageDescription = page.getDescription();

        try {
            Resource pageResource = page.getContentResource();

            if (pageResource != null) {
                String detailsPath = CommonUtil.findComponentInPage(page, CommonUtil.DEFAULT_LIST_DETAILS_SUFFIX);

                ResourceResolver resourceResolver = pageResource.getResourceResolver();
                Resource detailsResource = resourceResolver.resolve(detailsPath);

                if (!ResourceUtil.isNonExistingResource(detailsResource)) {
                    Node detailsNode = detailsResource.adaptTo(Node.class);

                    if (detailsNode != null && detailsNode.hasProperty(DETAILS_DESCRIPTION)) {
                        return detailsNode.getProperty(DETAILS_DESCRIPTION).getString();
                    }
                }
            }
        } catch (Exception ex) {
            LOGGER.warn("getPageDescription: Unable to description for page. Error: {}", ex.getMessage());
        }

        return pageDescription;
    }

    /**
     * Compile a message from component properties using one of the component format tag fields.
     *
     * @param formatFieldName     field with format path
     * @param defaultFormat       default format template
     * @param componentProperties component properties
     * @param sling               sling helper
     * @return formatted string
     */
    public static String compileComponentMessage(
        String formatFieldName,
        String defaultFormat,
        ComponentProperties componentProperties,
        SlingScriptHelper sling
    ) {
        if (componentProperties == null || sling == null) {
            return StringUtils.EMPTY;
        }

        String formatFieldTagPath = componentProperties.get(formatFieldName, StringUtils.EMPTY);
        String fieldFormatValue = defaultFormat;

        if (isNotEmpty(formatFieldTagPath)) {
            fieldFormatValue = TagUtil.getTagValueAsAdmin(formatFieldTagPath, sling);
        }

        return CommonUtil.compileMapMessage(fieldFormatValue, componentProperties);
    }

    /**
     * Get or generate a component id.
     *
     * @param componentNode component node
     * @return component id
     */
    public static String getComponentId(Node componentNode) {
        String componentId = UUID.randomUUID().toString();

        if (componentNode == null) {
            return componentId;
        }

        try {
            String path = componentNode.getPath();

            if (!componentNode.hasProperty(FIELD_STYLE_COMPONENT_ID)) {
                String prefix = componentNode.getName();

                // Clean up the prefix
                prefix = prefix.replaceAll("[^a-zA-Z0-9-_]", "_");

                componentNode.setProperty(FIELD_STYLE_COMPONENT_ID, format(
                    "{0}_{1}",
                    prefix,
                    RandomStringUtils.randomAlphanumeric(9).toUpperCase()));

                componentNode.getSession().save();
            }

            componentId = CommonUtil.getProperty(componentNode, FIELD_STYLE_COMPONENT_ID);
        } catch (Exception ex) {
            LOGGER.error("Could not get component id '{}'. Error: {}", componentId, ex.getMessage());
        }

        return componentId;
    }

    /**
     * Retrieve a configuration value from Cloud Config.
     *
     * @param pageProperties    {@link InheritanceValueMap} of page properties
     * @param configurationName Cloud configuration name
     * @param propertyName      Name of the property to search for
     * @param sling             {@link SlingScriptHelper} instance
     * @return found configuration value or a blank {@link String}
     */
    public static String getCloudConfigProperty(
        InheritanceValueMap pageProperties,
        String configurationName,
        String propertyName,
        SlingScriptHelper sling
    ) {
        String returnValue = StringUtils.EMPTY;

        ContentAccess contentAccess = sling.getService(ContentAccess.class);

        if (contentAccess != null) {
            try (ResourceResolver adminResourceResolver = contentAccess.getAdminResourceResolver()) {
                ConfigurationManager configManager = adminResourceResolver.adaptTo(ConfigurationManager.class);
                Configuration configuration;

                String[] services = pageProperties.getInherited(ConfigurationConstants.PN_CONFIGURATIONS, new String[]{});

                if (configManager != null) {
                    configuration = configManager.getConfiguration(configurationName, services);

                    if (configuration != null) {
                        returnValue = configuration.get(propertyName, StringUtils.EMPTY);
                    }
                }
            } catch (Exception ex) {
                LOGGER.error(Throwables.getStackTraceAsString(ex));
            }
        } else {
            LOGGER.warn("getCloudConfigProperty: Could not get 'ContentAccess' service.");
        }

        return returnValue;
    }

    /**
     * Find an ancestor resource matching current resource.
     *
     * @param page             {@link Page} to use
     * @param componentContext component context
     * @return found resource
     */
    @SuppressWarnings("squid:S3776")
    public static Resource findInheritedResource(Page page, ComponentContext componentContext) {
        final String pageResourcePath = page.getContentResource().getPath();
        final Resource thisResource = componentContext.getResource();
        final String nodeResourceType = thisResource.getResourceType();

        final String relativePath = thisResource.getPath().replaceFirst(
            pageResourcePath.concat(FileSystem.SEPARATOR), StringUtils.EMPTY);

        // definition of a parent node
        // 1. is from parent page
        // 2. same sling resource type
        // 3. same relative path

        Page curPage = page.getParent();
        Resource curResource = null;
        boolean curResourceTypeMatch;
        boolean curCancelInheritParent;
        ValueMap curProperties;

        try {
            while (null != curPage) {
                String error = format("findInheritedResource: Looking for inherited resource for path='%s' by relative path='%s' in parent='%s'", pageResourcePath, relativePath, curPage.getPath());

                LOGGER.info(error);

                try { // NOSONAR
                    curResource = curPage.getContentResource(relativePath);
                } catch (Exception e) {
                    LOGGER.info("Failed to get {} from {}", relativePath, curPage.getContentResource().getPath());
                }

                if (null != curResource) {
                    curProperties = curResource.adaptTo(ValueMap.class);

                    if (curProperties != null) {
                        curResourceTypeMatch = curResource.isResourceType(nodeResourceType);

                        curCancelInheritParent = curProperties.get(COMPONENT_CANCEL_INHERIT_PARENT,
                            StringUtils.EMPTY).contentEquals("true");

                        if (curResourceTypeMatch && curCancelInheritParent) {
                            String found = format("findInheritedResource: FOUND looking for inherited resource for path=\"{0}\" by relative path=\"{1}\" in parent=\"{2}\"", pageResourcePath, relativePath, curPage.getPath());

                            LOGGER.info(found);

                            break;
                        } else {
                            String notfound = format("findInheritedResource: NOT FOUND looking for inherited resource for path=\"{0}\" by relative path=\"{1}\" in parent=\"{2}\"", pageResourcePath, relativePath, curPage.getPath());

                            LOGGER.info(notfound);
                        }
                    } else {
                        LOGGER.warn("findInheritedResource: Could not convert resource to value map, curResource={}", curResource);
                    }
                }

                curPage = curPage.getParent();
            }
        } catch (Exception ex) {
            LOGGER.warn("Failed to find inherited resource. Error: {}", ex.getMessage());
        }

        return curResource;
    }

    /**
     * Get component path relative to {@link JcrConstants#JCR_CONTENT}
     *
     * @param componentNode component {@link Node}
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
            LOGGER.warn("getComponentInPagePath: Could not get component. Path: {}, Error: {}",
                componentNode,
                ex.getMessage());
        }

        return componentInPagePath;
    }

    /**
     * Return list of available sub-resources in current and all super components.
     *
     * @param component    component to start with
     * @param resourceName sub-resource container to find
     * @param sling        {@link SlingScriptHelper} instance
     * @return returns list of resources
     */
    @SuppressWarnings({"squid:S3776"})
    public static Map<String, Resource> getLocalSubResourcesInSuperComponent(
        Component component,
        String resourceName,
        SlingScriptHelper sling
    ) {
        HashMap<String, Resource> subResources = new HashMap<>();
        Component superComponent;

        if (component != null && isNotEmpty(resourceName)) {
            ContentAccess contentAccess = sling.getService(ContentAccess.class);

            if (contentAccess != null) {
                try (ResourceResolver adminResourceResolver = contentAccess.getAdminResourceResolver()) {
                    String componentPath = component.getPath();
                    Resource componentAdminResource = adminResourceResolver.resolve(componentPath);

                    if (!ResourceUtil.isNonExistingResource(componentAdminResource)) {
                        superComponent = componentAdminResource.adaptTo(Component.class);

                        if (superComponent != null) {
                            Resource localresource = superComponent.getLocalResource(resourceName);

                            if (localresource != null && !ResourceUtil.isNonExistingResource(localresource)) {
                                for (Resource resource : localresource.getChildren()) {
                                    String name = resource.getName().replace(DEFAULT_EXTENTION, EMPTY);

                                    subResources.put(name, resource);
                                }
                            }

                            while (superComponent.getSuperComponent() != null) {
                                superComponent = superComponent.getSuperComponent();

                                if (superComponent == null) {
                                    break;
                                }

                                localresource = superComponent.getLocalResource(resourceName);

                                if (localresource != null && !ResourceUtil.isNonExistingResource(localresource)) {
                                    for (Resource resource : localresource.getChildren()) {
                                        if (!subResources.containsValue(resource.getName())) { // NOSONAR
                                            String name = resource.getName().replace(DEFAULT_EXTENTION, EMPTY);

                                            subResources.put(name, resource);
                                        }
                                    }
                                }
                            }
                        } else {
                            LOGGER.warn("getComponentSubResources: Could not convert resource to component.\ncomponentAdminResource={}",
                                componentAdminResource);
                        }
                    } else {
                        LOGGER.warn("getComponentSubResources: Could not resolve component path to resource.\nComponent Path: {}",
                            componentPath);
                    }
                } catch (Exception ex) {
                    LOGGER.error(Throwables.getStackTraceAsString(ex));
                }
            } else {
                LOGGER.warn("getComponentSubResources: Could not get ContentAccess service.");
            }
        } else {
            LOGGER.error("getComponentSubResources: Please specify component and sub resource.\nComponent [{}]\n Resource Name: {}",
                component,
                resourceName);
        }

        return subResources;
    }

    /**
     * Find local resource in component and its super components.
     *
     * @param component    component to check
     * @param resourceName local resource name
     * @param sling        {@link SlingScriptHelper} instance
     * @return local resource path found
     */
    @SuppressWarnings("squid:S3776")
    public static String findLocalResourceInSuperComponent(
        Component component,
        String resourceName,
        SlingScriptHelper sling
    ) {
        Component superComponent;

        if (component != null) {
            ContentAccess contentAccess = sling.getService(ContentAccess.class);

            if (contentAccess != null) {
                try (ResourceResolver adminResourceResolver = contentAccess.getAdminResourceResolver()) {
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
                            LOGGER.warn("findLocalResourceInSuperComponent: Could not convert resource to component, componentAdminResource={}",
                                componentAdminResource);
                        }
                    } else {
                        LOGGER.warn("findLocalResourceInSuperComponent: Could not resolve component path to resource, componentPath={}",
                            componentPath);
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
     * Create a map of component fields matched to the component dialog.
     *
     * @param componentResource     component resource
     * @param adminResourceResolver admin {@link ResourceResolver}
     * @param slingScriptHelper     {@link SlingScriptHelper} instance
     * @return map of component dialog fields and their attributes
     */
    @SuppressWarnings("squid:S3776")
    public static Map<String, Object> getComponentFieldsAndDialogMap(
        Resource componentResource,
        ResourceResolver adminResourceResolver,
        SlingScriptHelper slingScriptHelper
    ) {
        Map<String, Object> firstComponentConfig = new HashMap<>();

        if (!ResourceUtil.isNonExistingResource(componentResource)) {
            ValueMap componentResourceMap = componentResource.adaptTo(ValueMap.class);

            if (componentResourceMap != null) {
                try {
                    ComponentManager componentManager = adminResourceResolver.adaptTo(ComponentManager.class);
                    Component componentOfResource = componentManager.getComponentOfResource(componentResource);

                    if (componentOfResource != null) {
                        // Walk up the tree of resourceSuperType and get base component
                        String componentDialogPath = findLocalResourceInSuperComponent(
                            componentOfResource,
                            "cq:dialog",
                            slingScriptHelper);

                        String dialogPath = StringUtils.EMPTY;
                        Document dialogContent = null;

                        if (isNotEmpty(componentDialogPath)) {
                            // Get dialog with value from resource: /<app component path>/cq:dialog/content.html/<resource path to pull values>
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

                                    row.put("type", classNames.length > 0
                                        ? classNames[classNames.length - 1]
                                        : fieldElement.tagName());
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
                        LOGGER.warn("getComponentFieldsAndDialogMap: Could not get component from resource. Path: {}",
                            componentResource.getPath());
                    }
                } catch (Exception ex) {
                    LOGGER.error("getComponentFieldsAndDialogMap: Error: {}", ex.getMessage());
                }
            } else {
                LOGGER.warn("getComponentFieldsAndDialogMap: Could not get component values. Path: {}",
                    componentResource.getPath());
            }
        } else {
            LOGGER.warn("getComponentFieldsAndDialogMap: Could not find component for resource. Path: {}",
                componentResource.getPath());
        }

        return firstComponentConfig;

    }

    /**
     * Retrieve the current content policy settings for a component.
     *
     * @param componentResource component resource to use
     * @param resourceResolver  {@link ResourceResolver} instance
     * @return {@link ValueMap} for policy
     */
    public static ValueMap getContentPolicyProperties(Resource componentResource, ResourceResolver resourceResolver) {
        ContentPolicyManager contentPolicyManager = resourceResolver.adaptTo(ContentPolicyManager.class);

        if (contentPolicyManager != null) {
            ContentPolicy policy = contentPolicyManager.getPolicy(componentResource);

            if (policy != null) {
                return policy.getProperties();
            }
        }

        return new ValueMapDecorator(new HashMap<>());
    }

    /**
     * Check if the given {@code value} is a regular expression.
     *
     * @param value string to check
     * @return does string match regex check
     */
    public static boolean isStringRegex(String value) {
        return isStringRegex(value, STRING_EXPRESSION_CHECK);
    }

    /**
     * Check if the given {@code value} is a regular expression.
     *
     * @param value        string to check
     * @param patternToUse regex to use to check string
     * @return does string match regex check
     */
    public static boolean isStringRegex(String value, String patternToUse) {
        try {
            Pattern valueIsRegexPattern = Pattern.compile(patternToUse);

            return valueIsRegexPattern.matcher(value).matches();
        } catch (PatternSyntaxException ex) {
            LOGGER.warn("isStringRegex: could not check if string is a regular expression. Error: {}", ex.getMessage());
        }

        return false;
    }

    /**
     * Evaluate expression in a context with value.
     * https://commons.apache.org/proper/commons-jexl/reference/syntax.html
     *
     * @param jxlt       {@link JxltEngine} instance
     * @param jc         {@link JexlContext} instance
     * @param expression regular expression
     * @param value      value to add into {@link JexlContext}
     * @return returns evaluated value
     * @throws JexlException throes Jexl errors with regex expression
     */
    public static Object evaluateExpressionWithValue(JxltEngine jxlt, JexlContext jc, String expression, Object value) {
        JxltEngine.Expression expr = jxlt.createExpression(expression);

        jc.set("value", value);

        return expr.evaluate(jc);
    }

    /**
     * Replace regex characters in the given {@code value}.
     *
     * @param value input string
     * @return updated string or original string
     */
    @SuppressWarnings({"squid:S4784"})
    public static String removeRegexFromString(String value) {
        try {
            Pattern valueIsRegexPattern = Pattern.compile("(\\$\\{.*?\\})");

            return value.replaceAll(valueIsRegexPattern.pattern(), StringUtils.EMPTY);
        } catch (PatternSyntaxException ex) {
            LOGGER.error("removeRegexFromString: Could not remove patterns from string. Error: {}", ex.getMessage());
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
     * @param cal    {@link Calendar} to transform
     * @param format format to use
     * @return formatted date string
     */
    public static String formatDate(Calendar cal, String format) {
        if (cal == null || format == null) {
            return null;
        }

        FastDateFormat dateFormat = FastDateFormat.getInstance(format);

        return dateFormat.format(cal);
    }

    /**
     * Get a unique identifier for the given {@code page}.
     *
     * @param page {@link Page} instance
     * @return the md5 hash of the pages content path
     */
    public static String getUniquePageIdentifier(Page page) {
        if (page != null) {
            String uniqueBase = page.getPath().substring(1).replace(FileSystem.SEPARATOR, "-");

            return hashMd5(uniqueBase);
        }

        return StringUtils.EMPTY;
    }

    /**
     * Helper that generates an expression used for field defaults.
     *
     * @param fields list of field names
     * @return {@code fields} as an expression
     */
    public static String getFormatExpression(String... fields) {
        List<String> expressions = Arrays.asList(fields);

        expressions.replaceAll(field -> String.format("${%s}", field));

        return String.join(" ", expressions);
    }

    /**
     * Get context objects for the given {@link WCMUsePojo} model.
     *
     * @param model component model
     * @return {@link Map} of objects
     */
    public static Map<String, Object> getContextObjects(WCMUsePojo model) {
        SlingHttpServletRequest slingRequest = model.getRequest();
        ResourceResolver resourceResolver = model.getResourceResolver();
        SlingScriptHelper sling = model.getSlingScriptHelper();
        ComponentContext componentContext = model.getComponentContext();
        Resource resource = model.getResource();
        Node currentNode = resource.adaptTo(Node.class);
        ValueMap properties = model.getProperties();
        Style currentStyle = model.getCurrentStyle();
        Page currentPage = model.getCurrentPage();
        Page resourcePage = model.getResourcePage();
        Design resourceDesign = model.getResourceDesign();

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
        pageContextMap.put(PAGECONTEXTMAP_SOURCE, model);
        pageContextMap.put(PAGECONTEXTMAP_SOURCE_TYPE, PAGECONTEXTMAP_SOURCE_TYPE_WCMUSEPOJO);

        return pageContextMap;
    }

    /**
     * Get context objects for the given {@link PageContext} object.
     *
     * @param pageContext page context to use
     * @return {@link Map} of objects
     */
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
}
