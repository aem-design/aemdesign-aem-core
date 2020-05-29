package design.aem.models.v2.details;

import com.day.cq.i18n.I18n;
import com.day.cq.tagging.TagConstants;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.google.common.base.Throwables;
import design.aem.components.ComponentProperties;
import design.aem.models.ModelProxy;
import design.aem.models.v2.content.ContentTemplate;
import design.aem.services.ContentAccess;
import design.aem.utils.components.ComponentsUtil;
import design.aem.utils.components.ContentFragmentUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static design.aem.utils.components.CommonUtil.*;
import static design.aem.utils.components.ComponentDetailsUtil.isComponentRenderedByList;
import static design.aem.utils.components.ComponentDetailsUtil.processBadgeRequestConfig;
import static design.aem.utils.components.ComponentsUtil.getPageDescription;
import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.ConstantsUtil.*;
import static design.aem.utils.components.ContentFragmentUtil.DEFAULT_CONTENTFRAGMENT_VARIATION;
import static design.aem.utils.components.I18nUtil.getDefaultLabelIfEmpty;
import static design.aem.utils.components.ImagesUtil.*;
import static design.aem.utils.components.ResolverUtil.mappedUrl;
import static design.aem.utils.components.TagUtil.getTagsAsAdmin;
import static design.aem.utils.components.TenantUtil.resolveTenantIdFromPath;
import static java.text.MessageFormat.format;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class GenericDetails extends ModelProxy {
    protected static final Logger LOGGER = LoggerFactory.getLogger(GenericDetails.class);

    protected final String PAR_NAME = DEFAULT_PAR_NAME;

    protected final String PAGE_META_PROPERTY_FIELDS = "metaPropertyFields";
    protected final String DEFAULT_FORMAT_TITLE = "${title}";
    protected final String FIELD_FORMATTED_TITLE = "titleFormatted";
    protected final String FIELD_FORMATTED_TITLE_TEXT = "titleFormattedText";
    protected final String FIELD_SUBCATEGORY = "subCategory";
    protected final String FIELD_CATEGORY = "category";
    protected final String FIELD_LEGACY_BADGE_CONFIG = "legacyBadgeConfig";
    protected final String FIELD_LEGACY_BADGE_SELECTED = "legacyBadgeSelected";
    protected final String FIELD_LEGACY_BADGE = "legacyBadge";
    protected final String FIELD_LEGACY_BADGE_LIST = "legacyBadgeList";
    protected final String FIELD_LEGACY_BADGE_LIST_MAPPING = "legacyBadgeListMapping";
    protected final String FIELD_LEGACY_BADGE_CNNFIG_TAGS = "legacyBadgeConfigTags";

    protected String DEFAULT_TITLE = null;
    protected String DEFAULT_DESCRIPTION = null;
    protected String DEFAULT_SUBTITLE = null;

    protected final String DEFAULT_ARIA_ROLE = "banner";
    protected final String DEFAULT_TITLE_TAG_TYPE = "h1";
    protected final String DEFAULT_I18N_CATEGORY = "page-detail";
    protected final String DEFAULT_I18N_LABEL = "variantHiddenLabel";
    protected final Boolean DEFAULT_HIDE_TITLE = false;
    protected final Boolean DEFAULT_HIDE_DESCRIPTION = false;
    protected final Boolean DEFAULT_SHOW_BREADCRUMB = true;
    protected final Boolean DEFAULT_SHOW_TOOLBAR = true;
    protected final Boolean DEFAULT_SHOW_PAGE_DATE = true;
    protected final Boolean DEFAULT_SHOW_PARSYS = true;
    protected final Boolean DEFAULT_USE_CONTAINER = true;
    protected final String DEFAULT_TAG_LEGACY_BADGE_CONFIG = ":component-dialog/components/details/generic-details/legacy";

    protected final String FIELD_CONTENTFRAGMENT_VARIATION = "variationName";
    protected final String FIELD_CONTENTFRAGMENT_FRAGMENTPATH = "fragmentPath";

    private static final String COMPONENT_DETAILS_NAME = "generic-details";

    // Used for backwards compatibility of details components
    private static final String[] legacyBadgeList = new String[]{
        "badge.cardActionIconDescription",
        "badge.cardActionIconTitleCategoryDescription",
        "badge.cardActionIconTitleDescription",
        "badge.cardActionImageTitle",
        "badge.cardActionImageTitleCategoryDescription",
        "badge.cardActionImageTitleDescription",
        "badge.cardHorizontalIconTitleCategoryDescriptionAction",
        "badge.cardIcon",
        "badge.cardIconDescription",
        "badge.cardIconTitle",
        "badge.cardIconTitleAction",
        "badge.cardIconTitleCategoryDescription",
        "badge.cardIconTitleCategoryDescriptionAction",
        "badge.cardIconTitleDateAction",
        "badge.cardIconTitleDateDescriptionAction",
        "badge.cardIconTitleDescription",
        "badge.cardIconTitleDescriptionAction",
        "badge.cardIconTitleSubtitleDate",
        "badge.cardIconTitleSubtitleDateDescriptionAction",
        "badge.cardIconTitleSubtitleDescriptionAction",
        "badge.cardImageSubtitleTitleCategoryDescriptionAction",
        "badge.cardImageSubtitleTitleDescriptionAction",
        "badge.cardImageTagTitleAction",
        "badge.cardImageTitleAction",
        "badge.cardImageTitleCategoryActionDate",
        "badge.cardImageTitleCategoryDescription",
        "badge.cardImageTitleCategoryDescriptionAction",
        "badge.cardImageTitleDescription",
        "badge.cardImageTitleDescriptionAction",
        "badge.cardImageTitleSubtitleDescriptionAction",
        "badge.cardTitleDescriptionAction",
        "badge.data",
        "badge.default",
        "badge.icon",
        "badge.image",
        "badge.metadata"
    };

    @SuppressWarnings("squid:S1192")
    // Used for backwards compatibility of details components
    private static final String[][] legacyBadgeListMapping = new String[][]{
        new String[]{"action-icon", "description"},
        new String[]{"action-icon", "title", "taglist", "description"},
        new String[]{"action-icon", "title", "description"},
        new String[]{"action-image", "title"},
        new String[]{"action-image", "title", "taglist", "description"},
        new String[]{"action-image", "title", "description"},
        new String[]{"horizontal-icon", "title", "taglist", "description", "action"},
        new String[]{"card-icon"},
        new String[]{"card-icon", "description"},
        new String[]{"card-icon", "title"},
        new String[]{"card-icon", "title", "action"},
        new String[]{"card-icon", "title", "taglist", "description"},
        new String[]{"card-icon", "title", "taglist", "description", "action"},
        new String[]{"card-icon", "title", "startdate", "action"},
        new String[]{"card-icon", "title", "startdate", "description"},
        new String[]{"card-icon", "title", "description"},
        new String[]{"card-icon", "title", "description", "action"},
        new String[]{"card-icon", "title", "subtitle", "startdate"},
        new String[]{"card-icon", "title", "subtitle", "startdate", "description", "action"},
        new String[]{"card-icon", "title", "subtitle", "description", "action"},
        new String[]{"card-image", "subtitle", "title", "taglist", "description", "action"},
        new String[]{"card-image", "subtitle", "title", "description", "action"},
        new String[]{"card-image", "taglist", "title", "action"},
        new String[]{"card-image", "title", "action"},
        new String[]{"card-image", "title", "taglist", "action", "startdate"},
        new String[]{"card-image", "title", "taglist", "description"},
        new String[]{"card-image", "title", "taglist", "description", "action"},
        new String[]{"card-image", "title", "description"},
        new String[]{"card-image", "title", "description", "action"},
        new String[]{"card-image", "title", "subtitle", "description", "action"},
        new String[]{"card", "title", "description", "action"},
        new String[]{"simple-data"},
        new String[]{"default"},
        new String[]{"simple-icon"},
        new String[]{"simple-image"},
        new String[]{"simple-metadata"},
    };

    protected void ready() {
        setComponentFields(new Object[][]{
            {FIELD_VARIANT_FIELDS, new String[]{}},
            {FIELD_VARIANT_FIELDS_TEMPLATE, new String[]{}},

            // Legacy variants
            {FIELD_LEGACY_BADGE_LIST, legacyBadgeList},
            {FIELD_LEGACY_BADGE_LIST_MAPPING, legacyBadgeListMapping},
            {FIELD_LEGACY_BADGE_CNNFIG_TAGS,
                resolveTenantIdFromPath(getCurrentPage().getPath()).concat(DEFAULT_TAG_LEGACY_BADGE_CONFIG)},

            // Content Fragment
            {FIELD_CONTENTFRAGMENT_VARIATION, DEFAULT_CONTENTFRAGMENT_VARIATION},
            {FIELD_CONTENTFRAGMENT_FRAGMENTPATH, StringUtils.EMPTY},
        });

        generateComponentFields();

        // Process content fragment content if its used
        String fragmentPath = componentProperties.get(FIELD_CONTENTFRAGMENT_FRAGMENTPATH, "");

        if (isNotEmpty(fragmentPath)) {
            String variationName = componentProperties.get(
                FIELD_CONTENTFRAGMENT_VARIATION,
                DEFAULT_CONTENTFRAGMENT_VARIATION);

            componentProperties.putAll(ContentFragmentUtil.getComponentFragmentMap(
                fragmentPath,
                variationName,
                getResourceResolver()));
        }

        // Set properties into request to allow ContentTemplate to use it for presentation
        getRequest().setAttribute(ContentTemplate.REQUEST_COMPONENT_PROPERTIES, componentProperties);
    }

    /**
     * Process the component fields and generate the {@link componentProperties} instance.
     */
    protected void generateComponentFields() {
        com.day.cq.i18n.I18n i18n = new I18n(getRequest());

        setComponentFieldsDefaults();

        setComponentFields(new Object[][]{
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {FIELD_VARIANT_LABEL, getDefaultLabelIfEmpty(
                StringUtils.EMPTY,
                DEFAULT_I18N_CATEGORY,
                DEFAULT_I18N_LABEL,
                DEFAULT_I18N_CATEGORY,
                i18n)},

            // Title
            {FIELD_TITLE, DEFAULT_TITLE},
            {FIELD_TITLE_FORMAT, StringUtils.EMPTY}, // Tag path, will be resolved to value in processComponentFields
            {FIELD_TITLE_TAG_TYPE, DEFAULT_TITLE_TAG_TYPE},
            {FIELD_HIDE_TITLE, DEFAULT_HIDE_TITLE},

            // Page/Navigation Title & Subtitle
            {FIELD_PAGE_TITLE, DEFAULT_TITLE},
            {FIELD_PAGE_TITLE_NAV, getPageNavTitle(getResourcePage())},
            {FIELD_PAGE_TITLE_SUBTITLE, DEFAULT_SUBTITLE},

            // Description
            {FIELD_DESCRIPTION, DEFAULT_DESCRIPTION},
            {FIELD_HIDE_DESCRIPTION, DEFAULT_HIDE_DESCRIPTION},

            // Layout toggles
            {FIELD_USE_CONTAINER, DEFAULT_USE_CONTAINER},
            {FIELD_SHOW_BREADCRUMB, DEFAULT_SHOW_BREADCRUMB},
            {FIELD_SHOW_TOOLBAR, DEFAULT_SHOW_TOOLBAR},
            {FIELD_SHOW_PAGEDATE, DEFAULT_SHOW_PAGE_DATE},
            {FIELD_SHOW_PARSYS, DEFAULT_SHOW_PARSYS},

            // Aria
            {FIELD_ARIA_ROLE, DEFAULT_ARIA_ROLE, FIELD_ARIA_DATA_ATTRIBUTE_ROLE},

            // Tagging
            {FIELD_TAGS, new String[]{}},
            {FIELD_SUBCATEGORY, new String[]{}},

            // Miscellaneous..
            {FIELD_LINK_TARGET, StringUtils.EMPTY, FIELD_TARGET},
            {FIELD_PAGE_URL, getPageUrl(getResourcePage())},
        });

        componentProperties = ComponentsUtil.getComponentProperties(
            this,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY,
            DEFAULT_FIELDS_ANALYTICS,
            DEFAULT_FIELDS_DETAILS_OPTIONS);

        processCommonFields();

        String[] tags = componentProperties.get(TagConstants.PN_TAGS, new String[]{});

        componentProperties.put(FIELD_CATEGORY, getTagsAsAdmin(
            getSlingScriptHelper(),
            tags,
            getRequest().getLocale()));

        String[] subCategory = componentProperties.get(FIELD_SUBCATEGORY, new String[]{});

        componentProperties.put(FIELD_SUBCATEGORY, getTagsAsAdmin(
            getSlingScriptHelper(),
            subCategory,
            getRequest().getLocale()));

        // Format fields
        componentProperties.putAll(
            processComponentFields(componentProperties, i18n, getSlingScriptHelper()),
            false);

        generateHasAuthoredContentComponentProps();
    }

    /**
     * Set the default component field values that are dynamic.
     */
    protected void setComponentFieldsDefaults() {
        DEFAULT_TITLE = getPageTitle(getResourcePage(), getResource());
        DEFAULT_DESCRIPTION = getResourcePage().getDescription();
        DEFAULT_SUBTITLE = getResourcePage().getProperties().get(FIELD_PAGE_TITLE_SUBTITLE, "");
    }

    /***
     * process common fields that details supports.
     */
    @SuppressWarnings({"Duplicates", "squid:S3776", "squid:S1141"})
    protected void processCommonFields() {
        try {
            if (componentProperties == null) {
                componentProperties = getNewComponentProperties(this);
            }
            //read the image node
            componentProperties.putAll(getAssetInfo(getResourceResolver(),
                getPageImgReferencePath(getResourcePage()),
                FIELD_PAGE_IMAGE));

            //read the secondary image node
            componentProperties.putAll(getAssetInfo(getResourceResolver(),
                getResourceImagePath(getResource(), DEFAULT_SECONDARY_IMAGE_NODE_NAME),
                FIELD_PAGE_SECONDARY_IMAGE));

            //read the background image node
            componentProperties.putAll(getAssetInfo(getResourceResolver(),
                getResourceImagePath(getResource(), DEFAULT_BACKGROUND_IMAGE_NODE_NAME),
                FIELD_PAGE_BACKGROUND_IMAGE));

            //read the thumbnail image node
            componentProperties.putAll(getAssetInfo(getResourceResolver(),
                getResourceImagePath(getResource(), DEFAULT_THUMBNAIL_IMAGE_NODE_NAME),
                FIELD_PAGE_THUMBNAIL_IMAGE));

            componentProperties.put(FIELD_REDIRECT_TARGET, getResourcePage().getProperties().get(FIELD_REDIRECT_TARGET, ""));

            //set thumbnail path for image node
            componentProperties.put(FIELD_PAGE_IMAGE_THUMBNAIL,
                getBestFitRendition(
                    componentProperties.get(FIELD_PAGE_IMAGE, ""),
                    componentProperties.get(DETAILS_THUMBNAIL_WIDTH, DEFAULT_THUMB_WIDTH_SM),
                    getResourceResolver()
                )
            );

            //set thumbnail path for secondary image node
            componentProperties.put(FIELD_PAGE_SECONDARY_IMAGE_THUMBNAIL,
                getBestFitRendition(
                    componentProperties.get(FIELD_PAGE_SECONDARY_IMAGE, ""),
                    componentProperties.get(DETAILS_THUMBNAIL_WIDTH, DEFAULT_THUMB_WIDTH_SM),
                    getResourceResolver()
                )
            );

            //set thumbnail path for thumbnail image node
            componentProperties.put(FIELD_PAGE_THUMBNAIL_IMAGE_THUMBNAIL,
                getBestFitRendition(
                    componentProperties.get(FIELD_PAGE_THUMBNAIL_IMAGE, ""),
                    componentProperties.get(DETAILS_THUMBNAIL_WIDTH, DEFAULT_THUMB_WIDTH_SM),
                    getResourceResolver()
                )
            );

            //process badge selection
            String componentBadge = getBadgeFromSelectors(getRequest().getRequestPathInfo().getSelectorString());
            Boolean badgeWasRequested = isNotEmpty(componentBadge);

            String requestedBadgeTemplate = format(COMPONENT_BADGE_TEMPLATE_FORMAT, componentBadge);
            if (isEmpty(componentBadge)) {
                componentProperties.put(COMPONENT_BADGE_SELECTED, false);
                componentBadge = DEFAULT_BADGE;
                requestedBadgeTemplate = format(COMPONENT_BADGE_DEFAULT_TEMPLATE_FORMAT, componentBadge);
            } else {
                componentProperties.put(COMPONENT_BADGE_SELECTED, true);
            }


            String defaultBadgeTemplate = format(COMPONENT_BADGE_DEFAULT_TEMPLATE_FORMAT, DEFAULT_BADGE);

            //get super component if exists
            String badgePath = findLocalResourceInSuperComponent(getComponent(), requestedBadgeTemplate, getSlingScriptHelper());

            //check if component has the badge and reset if it does not
            if (isEmpty(badgePath)) {
                if (Boolean.TRUE.equals(badgeWasRequested) && !ArrayUtils.contains(legacyBadgeList, componentBadge)) {
                    LOGGER.error("LEGACY BADGE WAS REQUESTED BUT NOT FOUND IN COMPONENT AND LEGACY MAPPING NOT FOUND requestedBadgeTemplate={}", requestedBadgeTemplate);
                }
                componentBadge = DEFAULT_BADGE;
                requestedBadgeTemplate = defaultBadgeTemplate;
            }

            componentProperties.put(COMPONENT_BADGE, componentBadge);

            //compile componentBadgeTemplate param
            componentProperties.put(COMPONENT_BADGE_TEMPLATE, requestedBadgeTemplate);

            //get background image
            componentProperties.put(DEFAULT_BACKGROUND_IMAGE_NODE_NAME, getBackgroundImageRenditions(this));

            if (isComponentRenderedByList(getRequest())) {
                //update component properties overrides possibly from list component
                ComponentProperties badgeOverrides = processBadgeRequestConfig(componentProperties, getResourceResolver(), getRequest());
                //if override badgeCustom is set to false remove related fields from overrides
                if (Boolean.FALSE.equals(badgeOverrides.get(DETAILS_BADGE_CUSTOM, false))) {
                    badgeOverrides.remove(DETAILS_BADGE_CUSTOM);
                    badgeOverrides.remove(DETAILS_BADGE_FIELDS);
                    badgeOverrides.remove(DETAILS_BADGE_FIELDS_TEMPLATE);
                    badgeOverrides.remove(DETAILS_BADGE_TEMPLATE);
                }
                //add updated overrides
                componentProperties.putAll(badgeOverrides, true);
            }

            //evaluate fields after badge overrides have been applied
            componentProperties.evaluateExpressionFields();

            //process badge config
            componentProperties.putAll(processBadgeConfig(getResourcePage(), componentProperties));

            //if custom badge being used process its config
            if (Boolean.TRUE.equals(componentProperties.get(DETAILS_BADGE_CUSTOM, false))) {
                String badge = componentProperties.get(DETAILS_BADGE_TEMPLATE, StringUtils.EMPTY);

                //get badge config
                if (isNotEmpty(badge)) {
                    ContentAccess contentAccess = getSlingScriptHelper().getService(ContentAccess.class);
                    if (contentAccess != null) {
                        try (ResourceResolver adminResourceResolver = contentAccess.getAdminResourceResolver()) {

                            ComponentProperties badgeConfig = getTemplateConfig(getContextObjects(this), badge, adminResourceResolver, adminResourceResolver.adaptTo(TagManager.class), DETAILS_BADGE_FIELDS_TEMPLATE, DETAILS_BADGE_FIELDS, "detailsBadge");
                            componentProperties.putAll(badgeConfig);

                        } catch (Exception ex) {
                            LOGGER.error(Throwables.getStackTraceAsString(ex));
                        }
                    }
                }
            }

            //if COMPONENT_VARIANT_TEMPLATE has not been set do it now
            if (isEmpty(componentProperties.get(COMPONENT_VARIANT_TEMPLATE, ""))) {

                //only process variantTemplate if its not been set already
                String variant = componentProperties.get(FIELD_VARIANT, DEFAULT_VARIANT);

                if (isEmpty(variant)) {
                    variant = DEFAULT_VARIANT;
                }

                //compile variantTemplate param
                String variantTemplate = getComponentVariantTemplate(getComponent(), format(COMPONENT_VARIANT_TEMPLATE_FORMAT, variant), getSlingScriptHelper());
                componentProperties.put(COMPONENT_VARIANT_TEMPLATE, variantTemplate);
            }

            //get page metadata fields
            componentProperties.put(PAGE_META_PROPERTY_FIELDS, processPageMetaProperties(getResourcePage(), getResourceResolver(), getRequest(), componentProperties));

            //set canonical url
            componentProperties.put(FIELD_CANONICAL_URL, mappedUrl(getResourceResolver(), getRequest(), getResourcePage().getPath()).concat(DEFAULT_EXTENTION));

            //get legacy badge configs from tags
            String legacyBadgeConfigTags = componentProperties.get(FIELD_LEGACY_BADGE_CNNFIG_TAGS, "");
            if (isNotEmpty(legacyBadgeConfigTags)) {
                Map<String, Map> legacyBadgeConfig = getTagsAsAdmin(
                    getSlingScriptHelper(),
                    new String[]{legacyBadgeConfigTags},
                    getRequest().getLocale(),
                    new String[]{FIELD_TAG_TEMPLATE_CONFIG_TEMPLATES, FIELD_TAG_TEMPLATE_CONFIG_FIELDS},
                    true);
                componentProperties.put(FIELD_LEGACY_BADGE_CONFIG, legacyBadgeConfig);
            }


        } catch (Exception ex) {
            LOGGER.error("processCommonFields: could not process fields componentProperties={}, ex={}", componentProperties, ex);
        }
    }

    /**
     * get field template in component.
     *
     * @return list of field resources
     */
    public Map<String, Resource> getFields() {
        return getLocalSubResourcesInSuperComponent(getComponent(), "field", getSlingScriptHelper());
    }

    /**
     * get template in component.
     *
     * @return list of template resources
     */
    public Map<String, Resource> getTemplates() {
        return getLocalSubResourcesInSuperComponent(getComponent(), "template", getSlingScriptHelper());
    }

    /**
     * return list of selectors if not using legacy badge convention.
     *
     * @return list of template resources
     */
    public String[] getRequestedFields() {
        String legacyComponentBadge = getBadgeFromSelectors(getRequest().getRequestPathInfo().getSelectorString());

        if (isNotEmpty(legacyComponentBadge) && this.componentProperties != null) {
            //check if config from tags being used
            LinkedHashMap<String, Map> legacyBadgeConfig = componentProperties.get(FIELD_LEGACY_BADGE_CONFIG, LinkedHashMap.class);
            if (legacyBadgeConfig != null) {
                //check if config exist
                for (Map.Entry<String, Map> entry : legacyBadgeConfig.entrySet()) {
                    Map config = entry.getValue();
                    if (config.containsKey("value") && config.get("value").equals(legacyComponentBadge)) {

                        String[] fields = new String[0];
                        String[] templates = new String[0];
                        //get config fields from tag
                        if (config.containsKey(FIELD_TAG_TEMPLATE_CONFIG_FIELDS)) {
                            Object value = config.get(FIELD_TAG_TEMPLATE_CONFIG_FIELDS);
                            if (value != null && value.getClass().isArray()) {
                                fields = (String[]) config.get(FIELD_TAG_TEMPLATE_CONFIG_FIELDS);
                            }
                        }
                        if (config.containsKey(FIELD_TAG_TEMPLATE_CONFIG_TEMPLATES)) {
                            Object value = config.get(FIELD_TAG_TEMPLATE_CONFIG_TEMPLATES);
                            if (value != null && value.getClass().isArray()) {
                                templates = (String[]) config.get(FIELD_TAG_TEMPLATE_CONFIG_TEMPLATES);
                            }
                        }
                        this.componentProperties.put(FIELD_LEGACY_BADGE_SELECTED, true);
                        return ArrayUtils.addAll(templates, fields);

                    }
                }


            } else {
                String[][] legacyBadgeMapping = this.componentProperties.get(FIELD_LEGACY_BADGE_LIST_MAPPING, legacyBadgeListMapping);
                String[] legacyBadges = this.componentProperties.get(FIELD_LEGACY_BADGE, legacyBadgeList);
                int badgeMapIndex = ArrayUtils.indexOf(legacyBadges, legacyComponentBadge);
                if (legacyBadgeMapping.length > badgeMapIndex) {
                    this.componentProperties.put(FIELD_LEGACY_BADGE_SELECTED, true);
                    return legacyBadgeMapping[badgeMapIndex];
                }
            }

        }

        //selectors selection overrides config
        String[] fields = this.getSlingScriptHelper().getRequest().getRequestPathInfo().getSelectors();

        if (fields.length > 0) {
            return fields;
        }

        //use configured fields
        fields = componentProperties.get(FIELD_VARIANT_FIELDS, new String[]{});
        if (fields.length > 0) {
            return fields;
        }

        return new String[]{};
    }

    /***
     * get and format badge config.
     * @param page resource page
     * @param componentProperties current componentProperties
     * @return badge config map to be added to componentProperties
     */
    @SuppressWarnings("Duplicates")
    public static Map<String, Object> processBadgeConfig(Page page, ComponentProperties componentProperties) {
        Map<String, Object> badgeConfig = new HashMap<>();

        //get badge action attributes
        Map<String, String> badgeLinkAttr = new HashMap<>();
        badgeLinkAttr.put(FIELD_TARGET, componentProperties.get(FIELD_LINK_TARGET, ""));
        if (isNotEmpty(getPageRedirect(page))) {
            badgeLinkAttr.put(FIELD_EXTERNAL, "true");
        }
        badgeLinkAttr.put(DETAILS_DATA_ANALYTICS_TRACK, componentProperties.get(DETAILS_BADGE_ANALYTICS_TRACK, ""));
        badgeLinkAttr.put(DETAILS_DATA_ANALYTICS_LOCATION, componentProperties.get(DETAILS_BADGE_ANALYTICS_LOCATION, ""));
        badgeLinkAttr.put(DETAILS_DATA_ANALYTICS_LABEL, componentProperties.get(DETAILS_BADGE_ANALYTICS_LABEL, ""));
        badgeLinkAttr.put(COMPONENT_ATTRIBUTE_INPAGEPATH, componentProperties.get(COMPONENT_INPAGEPATH, ""));

        badgeConfig.put(DETAILS_BADGE_LINK_ATTR, badgeLinkAttr);

        //get badge image attributes
        Map<String, String> badgeImageAttr = new HashMap<>();
        badgeImageAttr.put(FIELD_DATA_ASSET_PRIMARY_ID, componentProperties.get(FIELD_PAGE_IMAGE_ID, ""));
        badgeImageAttr.put(FIELD_DATA_ASSET_PRIMARY_LICENSE, componentProperties.get(FIELD_PAGE_IMAGE_LICENSE_INFO, ""));
        badgeImageAttr.put(FIELD_DATA_ASSET_SECONDARY_ID, componentProperties.get(FIELD_PAGE_IMAGE_SECONDARY_ID, ""));
        badgeImageAttr.put(FIELD_DATA_ASSET_SECONDARY_LICENSE, componentProperties.get(FIELD_PAGE_IMAGE_SECONDARY_LICENSE_INFO, ""));
        badgeImageAttr.put(FIELD_WIDTH, componentProperties.get(FIELD_THUMBNAIL_WIDTH, ""));

        badgeImageAttr.put(FIELD_HEIGHT, componentProperties.get(FIELD_THUMBNAIL_HEIGHT, ""));

        String pageSecondaryImageThumbnail = componentProperties.get(FIELD_PAGE_SECONDARY_IMAGE_THUMBNAIL, "");
        if (isNotEmpty(pageSecondaryImageThumbnail)) {
            badgeImageAttr.put(FIELD_CLASS, FIELD_DATA_ASSET_SECONDARY_CLASS);
            badgeImageAttr.put(FIELD_DATA_ASSET_SECONDARY_SRC, pageSecondaryImageThumbnail);
        }

        badgeConfig.put(DETAILS_BADGE_IMAGE_ATTR, badgeImageAttr);

        //get badge class attributes
        String badgeClassAttr = "";
        badgeClassAttr += StringUtils.join(componentProperties.get(DETAILS_CARD_STYLE, new String[0]), " ");
        badgeClassAttr += StringUtils.join(componentProperties.get(DETAILS_TITLE_ICON, new String[0]), " ");
        badgeClassAttr += StringUtils.join(componentProperties.get(DETAILS_CARD_ADDITIONAL, new String[0]), " ");

        badgeConfig.put(DETAILS_BADGE_CLASS, badgeClassAttr);

        String badgeClassIconAttr = "";
        badgeClassIconAttr += StringUtils.join(componentProperties.get(DETAILS_CARD_ICON, new String[0]), " ");

        badgeConfig.put(DETAILS_BADGE_CLASS_ICON, badgeClassIconAttr);


        //check badge title
        String pageTitle = componentProperties.get(FIELD_PAGE_TITLE, "");
        badgeConfig.put(DETAILS_BADGE_TITLE, pageTitle);
        //trim pageNavTitle if needed
        if (Boolean.parseBoolean(componentProperties.get(DETAILS_TITLE_TRIM, ""))) {
            int badgeTitleTrimLengthMax = componentProperties.get(DETAILS_TITLE_TRIM_LENGTH_MAX, 20);
            if (StringUtils.isNotEmpty(pageTitle)) {
                if (pageTitle.length() > badgeTitleTrimLengthMax) {
                    pageTitle = pageTitle.substring(0, badgeTitleTrimLengthMax)
                        .concat(
                            componentProperties.get(
                                DETAILS_TITLE_TRIM_LENGTH_MAX_SUFFIX,
                                DETAILS_TITLE_TRIM_LENGTH_MAX_SUFFIX_DEFAULT
                            )
                        );
                }

                badgeConfig.put(DETAILS_BADGE_TITLE, pageTitle);
            }
        }

        //check badge description
        String badgeDescription = componentProperties.get(DETAILS_DESCRIPTION, "");
        badgeConfig.put(DETAILS_BADGE_DESCRIPTION, badgeDescription);
        //trim page description if needed
        if (Boolean.parseBoolean(componentProperties.get(DETAILS_SUMMARY_TRIM, ""))) {
            int badgeSummaryLengthMax = componentProperties.get(DETAILS_SUMMARY_TRIM_LENGTH_MAX, 20);
            if (StringUtils.isNotEmpty(badgeDescription)) {
                if (badgeDescription.length() > badgeSummaryLengthMax) {
                    badgeDescription = badgeDescription.substring(0, badgeSummaryLengthMax)
                        .concat(
                            componentProperties.get(
                                DETAILS_SUMMARY_TRIM_LENGTH_MAX_SUFFIX,
                                DETAILS_SUMMARY_TRIM_LENGTH_MAX_SUFFIX
                            )
                        );
                }

                badgeConfig.put(DETAILS_BADGE_DESCRIPTION, badgeDescription);
            }
        }

        return badgeConfig;
    }


    /***
     * process page metadata that has been configures in the page properties.
     * @param page page to check
     * @param resourceResolver resolver instance
     * @param request request instance
     * @param componentProperties components properties to use for content
     * @return returns map with page metadata
     */
    public static Map<String, String> processPageMetaProperties(Page page, ResourceResolver resourceResolver, SlingHttpServletRequest request, ComponentProperties componentProperties) {
        Map<String, String> newFields = new HashMap<>();


        String[] pageMetaProperty = componentProperties.get(DETAILS_PAGE_METADATA_PROPERTY, new String[0]);
        String[] pageMetaPropertyContent = componentProperties.get(DETAILS_PAGE_METADATA_PROPERTY_CONTENT, new String[0]);

        if (pageMetaProperty.length == pageMetaPropertyContent.length) {
            for (int i = 0; i < pageMetaProperty.length; i++) {
                String key = pageMetaProperty[i];
                String value = pageMetaPropertyContent[i];
                if (isNotEmpty(key) || isNotEmpty(value)) {
                    newFields.put(key, value);
                }
            }
        }

        //provide defaults for metadata
        if (!newFields.containsKey(FIELD_OG_URL)) {
            newFields.put(FIELD_OG_URL, mappedUrl(resourceResolver, request, page.getPath()).concat(DEFAULT_EXTENTION));
        }
        if (!newFields.containsKey(FIELD_OG_IMAGE)) {
            newFields.put(FIELD_OG_IMAGE, mappedUrl(resourceResolver, request, getThumbnailUrl(page, resourceResolver)));
        }
        if (!newFields.containsKey(FIELD_OG_TITLE)) {
            newFields.put(FIELD_OG_TITLE, componentProperties.get(FIELD_PAGE_TITLE, StringUtils.EMPTY));
        }
        if (!newFields.containsKey(FIELD_OG_DESCRIPTION)) {
            newFields.put(FIELD_OG_DESCRIPTION, getPageDescription(page));
        }


        return newFields;
    }


    /***
     * substitute formatted field template with fields from component.
     * @param componentProperties source map with fields
     * @param i18n i18n
     * @param sling sling helper
     * @return returns map with new values
     */
    @SuppressWarnings("Duplicates")
    public Map<String, Object> processComponentFields(
        ComponentProperties componentProperties,
        com.day.cq.i18n.I18n i18n,
        SlingScriptHelper sling
    ) {
        Map<String, Object> newFields = new HashMap<>();

        try {
            String formattedTitle = compileComponentMessage(FIELD_TITLE_FORMAT, DEFAULT_FORMAT_TITLE, componentProperties, sling);
            Document fragment = Jsoup.parse(formattedTitle);
            String formattedTitleText = fragment.text();

            newFields.put(FIELD_FORMATTED_TITLE,
                formattedTitle.trim()
            );

            newFields.put(FIELD_FORMATTED_TITLE_TEXT,
                formattedTitleText.trim()
            );
        } catch (Exception ex) {
            LOGGER.error("Could not process component fields in {}", getComponentDetailsName());
        }

        return newFields;
    }

    /**
     * Detects if the child parsys has any authored content which enables us to hide certain elements
     * within the details component when no children exist.
     */
    protected void generateHasAuthoredContentComponentProps() {
        Resource componentResource = getResource().getChild(PAR_NAME);

        componentProperties.put("hasAuthoredContent",
            componentResource != null && componentResource.hasChildren());
    }

    /**
     * Retrieve the name of the current details component.
     *
     * @return Name of the component
     */
    protected String getComponentDetailsName() {
        return COMPONENT_DETAILS_NAME;
    }
}
