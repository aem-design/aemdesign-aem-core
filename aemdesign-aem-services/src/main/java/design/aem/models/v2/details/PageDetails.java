package design.aem.models.v2.details;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.i18n.I18n;
import com.day.cq.tagging.TagConstants;
import com.day.cq.wcm.api.Page;
import design.aem.components.ComponentProperties;
import design.aem.utils.components.ComponentsUtil;
import design.aem.utils.components.TagUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static design.aem.utils.components.CommonUtil.*;
import static design.aem.utils.components.ComponentDetailsUtil.processBadgeRequestConfig;
import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.ConstantsUtil.*;
import static design.aem.utils.components.I18nUtil.getDefaultLabelIfEmpty;
import static design.aem.utils.components.ImagesUtil.*;
import static design.aem.utils.components.ResolverUtil.mappedUrl;
import static design.aem.utils.components.TagUtil.getTagsAsAdmin;
import static java.text.MessageFormat.format;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class PageDetails extends WCMUsePojo {

    private static final Logger LOGGER = LoggerFactory.getLogger(PageDetails.class);

    final String COMPONENT_DETAILS_NAME = "page-details";
    final String componentPath = "./" + PATH_DEFAULT_CONTENT + "/" + COMPONENT_DETAILS_NAME;

    final String DEFAULT_FORMAT_TITLE = "${title}";
    final String FIELD_FORMAT_TITLE = "titleFormat";
    final String FIELD_FORMATTED_TITLE = "titleFormatted";
    final String FIELD_FORMATTED_TITLE_TEXT = "titleFormattedText";
    final String I18N_CATEGORY = "page-detail";
    final String I18N_READMORE = "readMoreAboutText";
    final String I18N_FILTERBYTEXT = "filterByText";
    final String PAGE_CONTENT_SECONDARY_IMAGE_PATH = "article/par/page-details/secondaryImage";


    private ComponentProperties componentProperties = null;
    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }


    @Override
    public void activate() throws Exception {

        com.day.cq.i18n.I18n _i18n = new I18n(getRequest());
        
        final String DEFAULT_ARIA_ROLE = "banner";
        final String DEFAULT_TITLE_TAG_TYPE = "h1";
        final String DEFAULT_I18N_CATEGORY = "page-detail";
        final String DEFAULT_I18N_LABEL = "variantHiddenLabel";

        // default values for the component
        final String DEFAULT_TITLE = getPageTitle(getCurrentPage());
        final String DEFAULT_DESCRIPTION = getCurrentPage().getDescription();
        final String DEFAULT_SUBTITLE = getCurrentPage().getProperties().get(FIELD_PAGE_TITLE_SUBTITLE,"");
        final Boolean DEFAULT_HIDE_TITLE = false;
        final Boolean DEFAULT_HIDE_DESCRIPTION = false;
        final Boolean DEFAULT_SHOW_BREADCRUMB = true;
        final Boolean DEFAULT_SHOW_TOOLBAR = true;
        final Boolean DEFAULT_SHOW_PAGE_DATE = true;
        final Boolean DEFAULT_SHOW_PARSYS = true;


        //not using lamda is available so this is the best that can be done
        Object[][] componentFields = {
                {FIELD_VARIANT, DEFAULT_VARIANT},
                {"title", DEFAULT_TITLE},
                {"titleFormat",""}, //tag path, will be resolved to value in processComponentFields
                {"description", DEFAULT_DESCRIPTION},
                {"hideDescription", DEFAULT_HIDE_DESCRIPTION},
                {"hideTitle", DEFAULT_HIDE_TITLE},
                {"showBreadcrumb", DEFAULT_SHOW_BREADCRUMB},
                {"showToolbar", DEFAULT_SHOW_TOOLBAR},
                {"showPageDate", DEFAULT_SHOW_PAGE_DATE},
                {"showParsys", DEFAULT_SHOW_PARSYS},
                {"linkTarget", StringUtils.EMPTY, "target"},
                {FIELD_PAGE_URL, getPageUrl(getCurrentPage())},
                {FIELD_PAGE_TITLE_NAV, getPageNavTitle(getCurrentPage())},
                {FIELD_PAGE_TITLE_SUBTITLE, DEFAULT_SUBTITLE},
                {TagConstants.PN_TAGS, new String[]{}},
                {FIELD_ARIA_ROLE,DEFAULT_ARIA_ROLE, FIELD_ARIA_DATA_ATTRIBUTE_ROLE},
                {FIELD_TITLE_TAG_TYPE, DEFAULT_TITLE_TAG_TYPE},
                {"variantHiddenLabel", getDefaultLabelIfEmpty("",DEFAULT_I18N_CATEGORY,DEFAULT_I18N_LABEL,DEFAULT_I18N_CATEGORY,_i18n)},
                {DETAILS_LINK_TEXT, getPageNavTitle(getCurrentPage())},
                {DETAILS_LINK_TITLE, getPageTitle(getCurrentPage())},
        };

        componentProperties = ComponentsUtil.getComponentProperties(
                this,
                componentFields,
                DEFAULT_FIELDS_STYLE,
                DEFAULT_FIELDS_ACCESSIBILITY,
                DEFAULT_FIELDS_DETAILS_OPTIONS);

        String variant = componentProperties.get(FIELD_VARIANT,DEFAULT_VARIANT);

        String[] tags = componentProperties.get(TagConstants.PN_TAGS, new String[]{});
        componentProperties.put("category",getTagsAsAdmin(getSlingScriptHelper(), tags, getRequest().getLocale()));

        //read the image node
        componentProperties.putAll(getAssetInfo(getResourceResolver(),
                getPageImgReferencePath(getCurrentPage()),
                FIELD_PAGE_IMAGE));

        //read the secondary image node
        componentProperties.putAll(getAssetInfo(getResourceResolver(),
                getResourceImagePath(getResource(),DEFAULT_SECONDARY_IMAGE_NODE_NAME),
                FIELD_PAGE_SECONDARY_IMAGE));

        //read the background image node
        componentProperties.putAll(getAssetInfo(getResourceResolver(),
                getResourceImagePath(getResource(),DEFAULT_BACKGROUND_IMAGE_NODE_NAME),
                FIELD_PAGE_BACKGROUND_IMAGE));

        //read the thumbnail image node
        componentProperties.putAll(getAssetInfo(getResourceResolver(),
                getResourceImagePath(getResource(),DEFAULT_THUMBNAIL_IMAGE_NODE_NAME),
                FIELD_PAGE_THUMBNAIL_IMAGE));

        componentProperties.put(FIELD_REDIRECT_TARGET,getPageProperties().get(FIELD_REDIRECT_TARGET,""));

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

        componentProperties.putAll(processComponentFields(componentProperties,_i18n,getSlingScriptHelper()), false);

        componentProperties.putAll(processBadgeRequestConfig(componentProperties,getResourceResolver(), getRequest()), true);

        componentProperties.putAll(getBackgroundImageRenditions(this));

        //get badge action attributes
        Map<String, String> badgeLinkAttr = new HashMap<>();
        badgeLinkAttr.put("target",componentProperties.get("linkTarget",""));
        badgeLinkAttr.put("external","true");
        badgeLinkAttr.put("data-layer-event",componentProperties.get("badgeAnalyticsEventType",""));
        badgeLinkAttr.put("data-layer-linktype",componentProperties.get("badgeAnalyticsLinkType",""));
        badgeLinkAttr.put("data-layer-linklocation",componentProperties.get("badgeAnalyticsLinkLocation",""));
        badgeLinkAttr.put("data-layer-linkdescription",componentProperties.get("badgeAnalyticsLinkDescription",""));
        badgeLinkAttr.put(COMPONENT_ATTRIBUTE_INPAGEPATH,componentProperties.get("componentInPagePath",""));

        componentProperties.put("badgeLinkAttr",badgeLinkAttr);

        //get badge image attributes
        Map<String, String> badgeImageAttr = new HashMap<>();
        badgeImageAttr.put("data-asset-id-primary",componentProperties.get("pageImageId",""));
        badgeImageAttr.put("data-asset-license-primary",componentProperties.get("pageImageLicenseInfo",""));
        badgeImageAttr.put("data-asset-id-secondary",componentProperties.get("pageSecondaryImageId",""));
        badgeImageAttr.put("data-asset-license-secondary",componentProperties.get("pageSecondaryImageLicenseInfo",""));
        badgeImageAttr.put("width",componentProperties.get("thumbnailWidth",""));

        badgeImageAttr.put("height",componentProperties.get("thumbnailHeight",""));

        String pageSecondaryImageThumbnail = componentProperties.get(FIELD_PAGE_SECONDARY_IMAGE_THUMBNAIL,"");
        if (isNotEmpty(pageSecondaryImageThumbnail)) {
            badgeImageAttr.put("class", "rollover");
            badgeImageAttr.put("data-rollover-src", pageSecondaryImageThumbnail);
        }
        componentProperties.put("badgeImageAttr",badgeImageAttr);

        //get badge class attributes
        String badgeClassAttr = "";
        badgeClassAttr += StringUtils.join(componentProperties.get("cardStyle",new String[0])," ");
        badgeClassAttr += StringUtils.join(componentProperties.get("titleIcon",new String[0])," ");

        componentProperties.put("badgeClassAttr",badgeClassAttr);

        String badgeClassIconAttr = "";
        badgeClassIconAttr += StringUtils.join(componentProperties.get("cardIcon",new String[0])," ");

        componentProperties.put("badgeClassIconAttr",badgeClassIconAttr);


        //check badge title
        String pageNavTitle = componentProperties.get("pageNavTitle", "");
        componentProperties.put("badgeTitle",pageNavTitle);
        //trim pageNavTitle if needed
        if (Boolean.parseBoolean(componentProperties.get("badgeTitleTrim",""))) {
            int badgeTitleTrimLengthMax = componentProperties.get("badgeTitleTrimLengthMax",20);
            if (StringUtils.isNoneEmpty(pageNavTitle)) {
                componentProperties.put("badgeTitle", pageNavTitle.substring(0, badgeTitleTrimLengthMax));
            }
        }

        //check badge description
        String badgeDescription = componentProperties.get("description", "");
        componentProperties.put("badgeDescription",badgeDescription);
        //trim page description if needed
        if (Boolean.parseBoolean(componentProperties.get("badgeSummaryTrim",""))) {
            int badgeSummaryLengthMaxSuffix = componentProperties.get("badgeSummaryLengthMaxSuffix",20);
            if (StringUtils.isNoneEmpty(badgeDescription)) {
                componentProperties.put("badgeDescription", badgeDescription.substring(0, badgeSummaryLengthMaxSuffix));
            }
        }


        //process badge selection
        String componentBadge = getBadgeFromSelectors(getRequest().getRequestPathInfo().getSelectorString());
        if (isEmpty(componentBadge)) {
            componentBadge = DEFAULT_BADGE;
        }

        componentProperties.put(COMPONENT_BADGE,componentBadge);
        //compile componentBadgeTemplate param
        componentProperties.put(COMPONENT_BADGE_TEMPLATE,format(COMPONENT_BADGE_TEMPLATE_FORMAT,componentBadge));


        //process variant selection
        if (isEmpty(variant)) {
            variant = DEFAULT_VARIANT;
        }

        //compile variantTemplate param
        componentProperties.put(COMPONENT_VARIANT_TEMPLATE, format(COMPONENT_VARIANT_TEMPLATE_FORMAT,variant));


        //get page metadata
        Page componentPage = getPageManager().getContainingPage(getResource().getPath());

        String[] pageMetaProperty = componentProperties.get(DETAILS_PAGE_METADATA_PROPERTY, new String[0]);
        String[] pageMetaPropertyContent = componentProperties.get(DETAILS_PAGE_METADATA_PROPERTY_CONTENT, new String[0]);
        Map<String, String> metaPropertyFields = new HashMap<String, String>();

        if (pageMetaProperty.length == pageMetaPropertyContent.length) {
            for (int i = 0; i < pageMetaProperty.length; i ++) {
                String key = pageMetaProperty[i];
                String value = pageMetaPropertyContent[i];
                if (isNotEmpty(key) || isNotEmpty(value)) {
                    metaPropertyFields.put(key, value);
                }
            }
        }

        //provide defaults for metadata
        if (!metaPropertyFields.containsKey("og:url")) {
            metaPropertyFields.put("og:url", mappedUrl(getResourceResolver(), getRequest(), componentPage.getPath()).concat(DEFAULT_EXTENTION));
        }
        if (!metaPropertyFields.containsKey("og:image")) {
            metaPropertyFields.put("og:image", mappedUrl(getResourceResolver(), getRequest(), getThumbnailUrl(componentPage,getResourceResolver())));
        }
        if (!metaPropertyFields.containsKey("og:title")) {
            metaPropertyFields.put("og:title", getPageTitle(componentPage));
        }
        if (!metaPropertyFields.containsKey("og:description")) {
            metaPropertyFields.put("og:description", getPageDescription(componentPage));
        }

        componentProperties.put("metaPropertyFields",metaPropertyFields);

        //set canonical url
        componentProperties.put("canonicalUrl",mappedUrl(getResourceResolver(), getRequest(), componentPage.getPath()).concat(DEFAULT_EXTENTION));

    }


    /***
     * substitute formatted field template with fields from component.
     * @param componentProperties source map with fields
     * @param i18n i18n
     * @param sling sling helper
     * @return returns map with new values
     */
    public Map processComponentFields(ComponentProperties componentProperties, com.day.cq.i18n.I18n i18n, SlingScriptHelper sling){
        Map newFields = new HashMap();

        try {

            String formattedTitle = compileComponentMessage(FIELD_FORMAT_TITLE, DEFAULT_FORMAT_TITLE, componentProperties, sling);
            Document fragment = Jsoup.parse(formattedTitle);
            String formattedTitleText = fragment.text();

            newFields.put(FIELD_FORMATTED_TITLE,
                    formattedTitle.trim()
            );
            newFields.put(FIELD_FORMATTED_TITLE_TEXT,
                    formattedTitleText.trim()
            );

        } catch (Exception ex) {
            LOGGER.error("Could not process component fields in " + COMPONENT_DETAILS_NAME);
        }
        return newFields;
    }

}
