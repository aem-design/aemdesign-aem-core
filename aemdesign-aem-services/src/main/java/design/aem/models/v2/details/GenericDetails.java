package design.aem.models.v2.details;

import com.day.cq.tagging.TagConstants;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.google.common.base.Throwables;
import design.aem.components.ComponentProperties;
import design.aem.models.BaseComponent;
import design.aem.models.v2.content.ContentTemplate;
import design.aem.services.ContentAccess;
import design.aem.utils.components.ComponentsUtil;
import design.aem.utils.components.ContentFragmentUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static design.aem.utils.components.CommonUtil.*;
import static design.aem.utils.components.ComponentDetailsUtil.isComponentRenderedByList;
import static design.aem.utils.components.ComponentDetailsUtil.processBadgeRequestConfig;
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

public class GenericDetails extends BaseComponent {
    protected static final Logger LOGGER = LoggerFactory.getLogger(GenericDetails.class);

    protected static final String COMPONENT_DETAILS_NAME = "generic-details";

    protected static final String PAGE_META_PROPERTY_FIELDS = "metaPropertyFields";

    protected static final String FIELD_DESCRIPTION = "description";
    protected static final String FIELD_FORMAT_DESCRIPTION = "descriptionFormat";
    protected static final String FIELD_FORMATTED_DESCRIPTION = "descriptionFormatted";
    protected static final String FIELD_TITLE = "title";
    protected static final String FIELD_FORMAT_TITLE = "titleFormat";
    protected static final String FIELD_FORMATTED_TITLE = "titleFormatted";
    protected static final String FIELD_FORMATTED_TITLE_TEXT = "titleFormattedText";
    protected static final String FIELD_SUBCATEGORY = "subCategory";
    protected static final String FIELD_CATEGORY = "category";
    protected static final String FIELD_LEGACY_BADGE_CONFIG = "legacyBadgeConfig";
    protected static final String FIELD_LEGACY_BADGE_SELECTED = "legacyBadgeSelected";
    protected static final String FIELD_LEGACY_BADGE_CONFIG_TAGS = "legacyBadgeConfigTags";

    protected static final String FIELD_CONTENTFRAGMENT_VARIATION = "variationName";
    protected static final String FIELD_CONTENTFRAGMENT_FRAGMENTPATH = "fragmentPath";

    protected static final String FIELD_VARIANT_HIDDEN_LABEL = "variantHiddenLabel";

    protected static final String DEFAULT_ARIA_ROLE = "banner";
    protected static final String DEFAULT_FORMAT_TITLE = "${title}";
    protected static final String DEFAULT_TAG_LEGACY_BADGE_CONFIG = ":component-dialog/components/details/generic-details/legacy";
    protected static final String DEFAULT_TITLE_TAG_TYPE = "h1";
    protected static final String DEFAULT_I18N_LABEL = "variantHiddenLabel";

    protected void ready() {
        componentProperties = ComponentsUtil.getComponentProperties(
            this,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY,
            DEFAULT_FIELDS_ANALYTICS,
            DEFAULT_FIELDS_DETAILS_OPTIONS);

        //process all common fields across details components
        processCommonFields();

        // load details specific data, this is overridden by each component model
        componentProperties.putAll(processComponentFields(), false);

        //process badge config set by list component
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

            //update badge action attributes
            Map<String, String> badgeLinkAttr = new HashMap<>();
            badgeLinkAttr.put(FIELD_TARGET, componentProperties.get(FIELD_LINK_TARGET, StringUtils.EMPTY));
            badgeLinkAttr.put(FIELD_EXTERNAL, componentProperties.get(FIELD_EXTERNAL, StringUtils.EMPTY));
            badgeLinkAttr.put(DETAILS_DATA_ANALYTICS_TRACK, componentProperties.get(DETAILS_BADGE_ANALYTICS_TRACK, StringUtils.EMPTY));
            badgeLinkAttr.put(DETAILS_DATA_ANALYTICS_LOCATION, componentProperties.get(DETAILS_BADGE_ANALYTICS_LOCATION, StringUtils.EMPTY));
            badgeLinkAttr.put(DETAILS_DATA_ANALYTICS_LABEL, componentProperties.get(DETAILS_BADGE_ANALYTICS_LABEL, StringUtils.EMPTY));
            badgeLinkAttr.put(COMPONENT_ATTRIBUTE_INPAGEPATH, componentProperties.get(COMPONENT_INPAGEPATH, StringUtils.EMPTY));

            if (DETAILS_DATA_SCHEMA_ITEMSCOPE.equals(componentProperties.get(DETAILS_DATA_SCHEMA_ITEMSCOPE, ""))) {
                badgeLinkAttr.put(DETAILS_DATA_SCHEMA_ITEMSCOPE,DETAILS_DATA_SCHEMA_ITEMSCOPE);
                badgeLinkAttr.put(DETAILS_DATA_SCHEMA_ITEMTYPE,componentProperties.get(DETAILS_DATA_SCHEMA_ITEMTYPE, DETAILS_DATA_SCHEMA_ITEMTYPE_DEFAULT));
            }

            componentProperties.put(DETAILS_BADGE_LINK_ATTR, badgeLinkAttr);

        }

        //process badge config set by component
        componentProperties.putAll(processBadgeConfig(getResourcePage(), componentProperties));



    }

    @Override
    protected void done() throws Exception {

        super.done();

        // Set properties into request to allow ContentTemplate to use it for presentation
        getRequest().setAttribute(ContentTemplate.REQUEST_COMPONENT_PROPERTIES, componentProperties);

    }

    @Override
    protected void setFields() {
        setComponentFields(new Object[][]{
            {FIELD_DESCRIPTION, "${value ? value : " + FIELD_FORMATTED_TITLE_TEXT + " }"},
            {FIELD_FORMATTED_TITLE, "${value ? value : " + FIELD_TITLE +"}"},
            {FIELD_FORMATTED_TITLE_TEXT, "${value ? value : " + FIELD_TITLE +"}"},
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {FIELD_FORMAT_DESCRIPTION, StringUtils.EMPTY},
            {FIELD_TITLE, componentDefaults.get(FIELD_TITLE)},
            {FIELD_FORMAT_TITLE, StringUtils.EMPTY},
            {FIELD_LINK_TARGET, StringUtils.EMPTY, FIELD_TARGET},
            {FIELD_PAGE_URL, componentDefaults.get(FIELD_PAGE_URL)},
            {FIELD_PAGE_TITLE, componentDefaults.get(FIELD_PAGE_TITLE)},
            {FIELD_PAGE_TITLE_NAV, componentDefaults.get(FIELD_PAGE_TITLE_NAV)},
            {FIELD_PAGE_TITLE_SUBTITLE, componentDefaults.get(FIELD_PAGE_TITLE_SUBTITLE)},
            {TagConstants.PN_TAGS, new String[]{}},
            {FIELD_SUBCATEGORY, new String[]{}},
            {FIELD_ARIA_ROLE, DEFAULT_ARIA_ROLE, FIELD_ARIA_DATA_ATTRIBUTE_ROLE},
            {FIELD_TITLE_TAG_TYPE, DEFAULT_TITLE_TAG_TYPE},
            {FIELD_VARIANT_HIDDEN_LABEL, componentDefaults.get(FIELD_VARIANT_HIDDEN_LABEL)},
            {FIELD_LEGACY_BADGE_CONFIG_TAGS, componentDefaults.get(FIELD_LEGACY_BADGE_CONFIG_TAGS)},
            {FIELD_VARIANT_FIELDS, new String[]{}},
            {FIELD_VARIANT_FIELDS_TEMPLATE, new String[]{}},
            {"hideDescription", false},
            {"hideTitle", false},
            {"showBreadcrumb", true},
            {"showToolbar", false},
            {"showPageDate", false},
            {"showParsys", true},
            {FIELD_CONTENTFRAGMENT_VARIATION, DEFAULT_CONTENTFRAGMENT_VARIATION},
            {FIELD_CONTENTFRAGMENT_FRAGMENTPATH, StringUtils.EMPTY},
        });
    }

    @Override
    protected void setFieldDefaults() {
        componentDefaults.put(FIELD_DESCRIPTION, getResourcePage().getDescription());
        componentDefaults.put(FIELD_TITLE, getPageTitle(getResourcePage(), getResource()));

        componentDefaults.put(FIELD_PAGE_TITLE_SUBTITLE,
            getResourcePage().getProperties().get(FIELD_PAGE_TITLE_SUBTITLE, StringUtils.EMPTY));

        componentDefaults.put(FIELD_PAGE_URL, getPageUrl(getResourcePage()));
        componentDefaults.put(FIELD_PAGE_TITLE, getPageTitle(getResourcePage(), getResource()));
        componentDefaults.put(FIELD_PAGE_TITLE_NAV, getPageNavTitle(getResourcePage()));

        componentDefaults.put(FIELD_VARIANT_HIDDEN_LABEL, getDefaultLabelIfEmpty(
            StringUtils.EMPTY,
            getComponentCategory(),
            DEFAULT_I18N_LABEL,
            getComponentCategory(),
            i18n));

        componentDefaults.put(FIELD_LEGACY_BADGE_CONFIG_TAGS, resolveTenantIdFromPath(
            getCurrentPage().getPath()).concat(DEFAULT_TAG_LEGACY_BADGE_CONFIG));
    }

    protected String getComponentCategory() {
        return "generic-detail";
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

            // Process content fragment content if its used
            String fragmentPath = componentProperties.get(FIELD_CONTENTFRAGMENT_FRAGMENTPATH, StringUtils.EMPTY);

            if (isNotEmpty(fragmentPath)) {
                String variationName = componentProperties.get(FIELD_CONTENTFRAGMENT_VARIATION, DEFAULT_CONTENTFRAGMENT_VARIATION);
                componentProperties.putAll(ContentFragmentUtil.getComponentFragmentMap(fragmentPath, variationName, getResourceResolver()));
            }

            String[] tags = componentProperties.get(TagConstants.PN_TAGS, new String[]{});
            componentProperties.put(FIELD_CATEGORY, getTagsAsAdmin(getSlingScriptHelper(), tags, getRequest().getLocale()));

            String[] subCategory = componentProperties.get(FIELD_SUBCATEGORY, new String[]{});
            componentProperties.put(FIELD_SUBCATEGORY, getTagsAsAdmin(getSlingScriptHelper(), subCategory, getRequest().getLocale()));

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

            componentProperties.put(FIELD_REDIRECT_TARGET, getResourcePage().getProperties().get(FIELD_REDIRECT_TARGET, StringUtils.EMPTY));

            //set thumbnail path for image node
            componentProperties.put(FIELD_PAGE_IMAGE_THUMBNAIL,
                getBestFitRendition(
                    componentProperties.get(FIELD_PAGE_IMAGE, StringUtils.EMPTY),
                    componentProperties.get(DETAILS_THUMBNAIL_WIDTH, DEFAULT_THUMB_WIDTH_SM),
                    getResourceResolver()
                )
            );

            //set thumbnail path for secondary image node
            componentProperties.put(FIELD_PAGE_SECONDARY_IMAGE_THUMBNAIL,
                getBestFitRendition(
                    componentProperties.get(FIELD_PAGE_SECONDARY_IMAGE, StringUtils.EMPTY),
                    componentProperties.get(DETAILS_THUMBNAIL_WIDTH, DEFAULT_THUMB_WIDTH_SM),
                    getResourceResolver()
                )
            );

            //set thumbnail path for thumbnail image node
            componentProperties.put(FIELD_PAGE_THUMBNAIL_IMAGE_THUMBNAIL,
                getBestFitRendition(
                    componentProperties.get(FIELD_PAGE_THUMBNAIL_IMAGE, StringUtils.EMPTY),
                    componentProperties.get(DETAILS_THUMBNAIL_WIDTH, DEFAULT_THUMB_WIDTH_SM),
                    getResourceResolver()
                )
            );

            //get legacy badge configs from tags
            String legacyBadgeConfigTags = componentProperties.get(FIELD_LEGACY_BADGE_CONFIG_TAGS, StringUtils.EMPTY);
            if (isNotEmpty(legacyBadgeConfigTags)) {
                Map<String, Map> legacyBadgeConfig = getTagsAsAdmin(
                    getSlingScriptHelper(),
                    new String[]{legacyBadgeConfigTags},
                    getRequest().getLocale(),
                    new String[]{FIELD_TAG_TEMPLATE_CONFIG_TEMPLATES, FIELD_TAG_TEMPLATE_CONFIG_FIELDS},
                    true);
                componentProperties.put(FIELD_LEGACY_BADGE_CONFIG, legacyBadgeConfig);
            }

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
                LinkedHashMap<String, Map> legacyBadgeConfig = componentProperties.get(FIELD_LEGACY_BADGE_CONFIG, LinkedHashMap.class);
                if (isNotNull(legacyBadgeConfig) && Boolean.TRUE.equals(badgeWasRequested) && !ArrayUtils.contains(legacyBadgeConfig.keySet().toArray(), componentBadge)) {
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

            //if custom badge being used process its config
            if (Boolean.TRUE.equals(componentProperties.get(DETAILS_BADGE_CUSTOM, false))) {
                //get the preconfigured badge config from tags
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
            if (isEmpty(componentProperties.get(COMPONENT_VARIANT_TEMPLATE, StringUtils.EMPTY))) {

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
                this.componentProperties.put(FIELD_LEGACY_BADGE_SELECTED, true);
                Object fields = legacyBadgeConfig.get(legacyComponentBadge);
                return (String[])fields;
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
        badgeLinkAttr.put(FIELD_TARGET, componentProperties.get(FIELD_LINK_TARGET, StringUtils.EMPTY));
        if (isNotEmpty(getPageRedirect(page))) {
            badgeLinkAttr.put(FIELD_EXTERNAL, "true");
        }
        badgeLinkAttr.put(DETAILS_DATA_ANALYTICS_TRACK, componentProperties.get(DETAILS_BADGE_ANALYTICS_TRACK, StringUtils.EMPTY));
        badgeLinkAttr.put(DETAILS_DATA_ANALYTICS_LOCATION, componentProperties.get(DETAILS_BADGE_ANALYTICS_LOCATION, StringUtils.EMPTY));
        badgeLinkAttr.put(DETAILS_DATA_ANALYTICS_LABEL, componentProperties.get(DETAILS_BADGE_ANALYTICS_LABEL, StringUtils.EMPTY));
        badgeLinkAttr.put(COMPONENT_ATTRIBUTE_INPAGEPATH, componentProperties.get(COMPONENT_INPAGEPATH, StringUtils.EMPTY));

        badgeConfig.put(DETAILS_BADGE_LINK_ATTR, badgeLinkAttr);

        //get badge image attributes
        Map<String, String> badgeImageAttr = new HashMap<>();
        badgeImageAttr.put(FIELD_DATA_ASSET_PRIMARY_ID, componentProperties.get(FIELD_PAGE_IMAGE_ID, StringUtils.EMPTY));
        badgeImageAttr.put(FIELD_DATA_ASSET_PRIMARY_LICENSE, componentProperties.get(FIELD_PAGE_IMAGE_LICENSE_INFO, StringUtils.EMPTY));
        badgeImageAttr.put(FIELD_DATA_ASSET_SECONDARY_ID, componentProperties.get(FIELD_PAGE_IMAGE_SECONDARY_ID, StringUtils.EMPTY));
        badgeImageAttr.put(FIELD_DATA_ASSET_SECONDARY_LICENSE, componentProperties.get(FIELD_PAGE_IMAGE_SECONDARY_LICENSE_INFO, StringUtils.EMPTY));
        badgeImageAttr.put(FIELD_WIDTH, componentProperties.get(FIELD_THUMBNAIL_WIDTH, StringUtils.EMPTY));

        badgeImageAttr.put(FIELD_HEIGHT, componentProperties.get(FIELD_THUMBNAIL_HEIGHT, StringUtils.EMPTY));

        String pageSecondaryImageThumbnail = componentProperties.get(FIELD_PAGE_SECONDARY_IMAGE_THUMBNAIL, StringUtils.EMPTY);
        if (isNotEmpty(pageSecondaryImageThumbnail)) {
            badgeImageAttr.put(FIELD_CLASS, FIELD_DATA_ASSET_SECONDARY_CLASS);
            badgeImageAttr.put(FIELD_DATA_ASSET_SECONDARY_SRC, pageSecondaryImageThumbnail);
        }

        badgeConfig.put(DETAILS_BADGE_IMAGE_ATTR, badgeImageAttr);

        //get badge class attributes
        String badgeClassAttr = StringUtils.EMPTY;
        badgeClassAttr += StringUtils.join(componentProperties.get(DETAILS_CARD_STYLE, new String[0]), " ");
        badgeClassAttr += StringUtils.join(componentProperties.get(DETAILS_TITLE_ICON, new String[0]), " ");
        badgeClassAttr += StringUtils.join(componentProperties.get(DETAILS_CARD_ADDITIONAL, new String[0]), " ");

        badgeConfig.put(DETAILS_BADGE_CLASS, badgeClassAttr);

        String badgeClassIconAttr = StringUtils.EMPTY;
        badgeClassIconAttr += StringUtils.join(componentProperties.get(DETAILS_CARD_ICON, new String[0]), " ");

        badgeConfig.put(DETAILS_BADGE_CLASS_ICON, badgeClassIconAttr);


        //check badge title
        String pageTitle = componentProperties.get(FIELD_PAGE_TITLE, StringUtils.EMPTY);
        badgeConfig.put(DETAILS_BADGE_TITLE, pageTitle);
        //trim pageNavTitle if needed
        if (Boolean.parseBoolean(componentProperties.get(DETAILS_TITLE_TRIM, StringUtils.EMPTY))) {
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
        String badgeDescription = componentProperties.get(DETAILS_DESCRIPTION, StringUtils.EMPTY);
        badgeConfig.put(DETAILS_BADGE_DESCRIPTION, badgeDescription);
        //trim page description if needed
        if (Boolean.parseBoolean(componentProperties.get(DETAILS_SUMMARY_TRIM, StringUtils.EMPTY))) {
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
     * @return returns map with new values
     */
    protected Map<String, Object> processComponentFields() {
        Map<String, Object> newFields = new HashMap<>();

        try {
            String formattedTitle = compileComponentMessage(
                FIELD_FORMAT_TITLE,
                DEFAULT_FORMAT_TITLE,
                componentProperties,
                slingScriptHelper);

            Document fragment = Jsoup.parse(formattedTitle);
            String formattedTitleText = fragment.text();

            newFields.put(FIELD_FORMATTED_TITLE,
                formattedTitle.trim()
            );

            newFields.put(FIELD_FORMATTED_TITLE_TEXT,
                formattedTitleText.trim()
            );
        } catch (Exception ex) {
            LOGGER.error("Could not process component fields in {}", COMPONENT_DETAILS_NAME);
        }

        return newFields;
    }

}
