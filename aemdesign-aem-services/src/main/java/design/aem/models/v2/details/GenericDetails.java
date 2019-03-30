package design.aem.models.v2.details;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.i18n.I18n;
import com.day.cq.wcm.api.Page;
import design.aem.components.ComponentProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static design.aem.utils.components.CommonUtil.*;
import static design.aem.utils.components.ComponentDetailsUtil.processBadgeRequestConfig;
import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.ConstantsUtil.*;
import static design.aem.utils.components.ImagesUtil.*;
import static design.aem.utils.components.ResolverUtil.mappedUrl;
import static java.text.MessageFormat.format;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class GenericDetails extends WCMUsePojo {


    private static final Logger LOGGER = LoggerFactory.getLogger(GenericDetails.class);

    public static String PAGE_META_PROPERTY_FIELDS = "metaPropertyFields";

    protected ComponentProperties componentProperties = null;

    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }

    @Override
    @SuppressWarnings("Duplicates")
    public void activate() throws Exception {

        I18n _i18n = new I18n(getRequest());


        processCommonFields();


    }

    @SuppressWarnings("Duplicates")
    protected void processCommonFields() {

        try {

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
            if (isEmpty(componentBadge)) {
                componentProperties.put(COMPONENT_BADGE_SELECTED, false);
                componentBadge = DEFAULT_BADGE;
            } else {
                componentProperties.put(COMPONENT_BADGE_SELECTED, true);
            }

            String requestedBadgeTemplate = format(COMPONENT_BADGE_TEMPLATE_FORMAT, componentBadge);
            String defaultBadgeTemplate = format(COMPONENT_BADGE_DEFAULT_TEMPLATE_FORMAT, DEFAULT_BADGE);


            //check if component has the badge and reset if it does not
            if (getComponent().getLocalResource(requestedBadgeTemplate) == null) {
                componentBadge = DEFAULT_BADGE;
                requestedBadgeTemplate = defaultBadgeTemplate;
            }

            componentProperties.put(COMPONENT_BADGE, componentBadge);

            //compile componentBadgeTemplate param
            componentProperties.put(COMPONENT_BADGE_TEMPLATE, requestedBadgeTemplate);

            //get background image
            componentProperties.put(DEFAULT_BACKGROUND_IMAGE_NODE_NAME, getBackgroundImageRenditions(this));

            //update component properties overrides possibly from list component
            componentProperties.putAll(processBadgeRequestConfig(componentProperties, getResourceResolver(), getRequest()), true);

            //evaluate fields after badge overrides have been applied
            componentProperties.evaluateExpressionFields();

            //process badge config
            componentProperties.putAll(processBadgeConfig(getResourcePage(), componentProperties));


            String variant = componentProperties.get(FIELD_VARIANT, DEFAULT_VARIANT);
            //process variant selection
            if (isEmpty(variant)) {
                variant = DEFAULT_VARIANT;
            }

            //compile variantTemplate param
            componentProperties.put(COMPONENT_VARIANT_TEMPLATE, format(COMPONENT_VARIANT_TEMPLATE_FORMAT, variant));

            //get page metadata fields
            componentProperties.put(PAGE_META_PROPERTY_FIELDS, processPageMetaProperties(getResourcePage(), getResourceResolver(), getRequest(), componentProperties));

            //set canonical url
            componentProperties.put(FIELD_CANONICAL_URL, mappedUrl(getResourceResolver(), getRequest(), getResourcePage().getPath()).concat(DEFAULT_EXTENTION));

        } catch (Exception ex) {
            LOGGER.error("processCommonFields: could not process fields componentProperties={}, ex={}", componentProperties, ex);
        }

    }


    /***
     * get and format badge config
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
            badgeImageAttr.put(FIELD_CLASS, "rollover");
            badgeImageAttr.put(FIELD_DATA_ASSET_ROLLOVER_SRC, pageSecondaryImageThumbnail);
        }

        badgeConfig.put(DETAILS_BADGE_IMAGE_ATTR, badgeImageAttr);

        //get badge class attributes
        String badgeClassAttr = "";
        badgeClassAttr += StringUtils.join(componentProperties.get(DETAILS_CARD_STYLE, new String[0]), " ");
        badgeClassAttr += StringUtils.join(componentProperties.get(DETAILS_TITLE_ICON, new String[0]), " ");

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

}
