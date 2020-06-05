package design.aem.models.v2.content;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.dam.api.Rendition;
import com.day.cq.i18n.I18n;
import com.day.cq.tagging.TagConstants;
import design.aem.models.BaseComponent;
import design.aem.utils.components.ComponentsUtil;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import java.text.MessageFormat;

import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.ConstantsUtil.DEFAULT_IMAGE_BLANK;
import static design.aem.utils.components.ConstantsUtil.DEFAULT_THUMB_WIDTH_SM;
import static design.aem.utils.components.I18nUtil.getDefaultLabelIfEmpty;
import static design.aem.utils.components.ImagesUtil.*;
import static design.aem.utils.components.ResolverUtil.mappedUrl;
import static java.text.MessageFormat.format;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class Download extends BaseComponent {
    protected static final Logger LOGGER = LoggerFactory.getLogger(Download.class);

    private static final String DEFAULT_I18N_CATEGORY = "download";
    private static final String DEFAULT_I18N_LABEL = "downloadlabel";
    private static final String DEFAULT_TITLE_TAG_TYPE = "h4";

    private static final String EMPTY_FILE = "empty file";
    private static final String FIELD_TITLE = "title";
    private static final String FIELD_DESCRIPTION = "description";
    private static final String FIELD_THUMBNAIL = DEFAULT_THUMBNAIL_IMAGE_NODE_NAME;

    @SuppressWarnings({"uncheked", "squid:S3776"})
    public void ready() {
        com.day.cq.i18n.I18n i18n = new I18n(getRequest());

        setComponentFields(new Object[][]{
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {"thumbnailType", "icon"},
            {"label", getDefaultLabelIfEmpty("", DEFAULT_I18N_CATEGORY, DEFAULT_I18N_LABEL, DEFAULT_I18N_CATEGORY, i18n)},
            {"thumbnailWidth", "", "thumbnailWidth"},
            {"thumbnailHeight", "", "thumbnailHeight"},
            {FIELD_TITLE, ""},
            {FIELD_TITLE_TAG_TYPE, DEFAULT_TITLE_TAG_TYPE},
            {FIELD_DESCRIPTION, ""},
            {"fileReference", ""},
            {FIELD_THUMBNAIL, DEFAULT_IMAGE_BLANK},
        });

        setAnalyticsFields(new Object[][]{
            {DETAILS_BADGE_ANALYTICS_LABEL, "${(fileReference ? fileReference + '|' : '') + value }"}, //basic
        });

        componentProperties = ComponentsUtil.getComponentProperties(
            this,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY,
            DEFAULT_FIELDS_ANALYTICS,
            analyticsFields);

        String variant = componentProperties.get(FIELD_VARIANT, DEFAULT_VARIANT);

        com.day.cq.wcm.foundation.Download dld = new com.day.cq.wcm.foundation.Download(this.getResource());
        componentProperties.put("hasContent", dld.hasContent());
        if (dld.hasContent()) {

            String mimeType = getDownloadMimeType(this.getResourceResolver(), dld);
            String mimeTypeLabel = i18n.get(mimeType, DEFAULT_I18N_CATEGORY);

            Resource assetRes = dld.getResourceResolver().resolve(dld.getHref());

            if (!ResourceUtil.isNonExistingResource(assetRes)) {

                Asset asset = assetRes.adaptTo(Asset.class);
                Node assetN = asset.adaptTo(Node.class);

                String href = mappedUrl(getResourceResolver(), dld.getHref());
                String assetDescription = asset.getMetadataValue(DamConstants.DC_DESCRIPTION);
                String assetTitle = asset.getMetadataValue(DamConstants.DC_TITLE);
                String assetTags = getMetadataStringForKey(assetN, TagConstants.PN_TAGS, "");

                String licenseInfo = getAssetCopyrightInfo(asset, i18n.get("licenseinfo", DEFAULT_I18N_CATEGORY));

                //override title and description if image has rights
                String title = componentProperties.get(FIELD_TITLE, "");
                if (isNotEmpty(licenseInfo)) {
                    componentProperties.put("isLicensed", true);
                    if (isNotEmpty(assetTitle)) {
                        componentProperties.put("hasAssetTitle", true);
                        title = assetTitle;
                    } else {
                        componentProperties.put("hasAssetTitle", false);
                        title = assetRes.getName();
                    }
                } else {
                    componentProperties.put("isLicensed", false);
                    if (isEmpty(title) && isNotEmpty(assetTitle)) {
                        componentProperties.put("hasAssetTitleOveride", false);
                        title = assetTitle;
                    } else {
                        componentProperties.put("hasAssetTitleOveride", true);
                    }
                }
                componentProperties.put(FIELD_TITLE, title);

                String description = componentProperties.get(FIELD_DESCRIPTION, "");

                if (isNotEmpty(licenseInfo) || (isEmpty(description) && isNotEmpty(assetDescription))) {
                    componentProperties.put(FIELD_DESCRIPTION, assetDescription);
                }

                componentProperties.put("licenseInfo", licenseInfo);


                componentProperties.put("mimeTypeLabel", mimeTypeLabel);
                componentProperties.put("assetTitle", assetTitle);
                componentProperties.put("assetDescription", assetDescription);
                componentProperties.put("href", href);

                String thumbnailType = componentProperties.get("thumbnailType", "");

                String assetAsThumbnail = assetRes.getPath();
                String thumbnailImagePath = getResourceImagePath(getResource(), FIELD_THUMBNAIL);
                if (isNotEmpty(thumbnailImagePath)) {
                    componentProperties.put(FIELD_THUMBNAIL, thumbnailImagePath);
                } else {
                    componentProperties.put(FIELD_THUMBNAIL, assetAsThumbnail);
                }

                if (thumbnailType.equals("dam")) {

                    Rendition assetRendition = getThumbnail(asset, DEFAULT_THUMB_WIDTH_SM);

                    if (assetRendition == null) {
                        componentProperties.put(FIELD_THUMBNAIL, DEFAULT_DOWNLOAD_THUMB_ICON);
                    } else {
                        componentProperties.put(FIELD_THUMBNAIL, assetRendition.getPath());
                    }
                } else if (thumbnailType.equals("customdam")) {

                    Resource thumbnailImage = getResourceResolver().resolve(thumbnailImagePath);

                    if (ResourceUtil.isNonExistingResource(thumbnailImage)) {
                        thumbnailImagePath = DEFAULT_DOWNLOAD_THUMB_ICON;
                    } else {
                        Rendition assetRendition = getThumbnail(thumbnailImage.adaptTo(Asset.class), DEFAULT_THUMB_WIDTH_SM);

                        thumbnailImagePath = assetRendition.getPath();
                    }

                    componentProperties.put(FIELD_THUMBNAIL, thumbnailImagePath);

                } else if (thumbnailType.equals("custom")) {

                    String thumbnailImage = getResourceImageCustomHref(getResource(), FIELD_THUMBNAIL);

                    if (isEmpty(thumbnailImage)) {
                        componentProperties.put(FIELD_THUMBNAIL, DEFAULT_DOWNLOAD_THUMB_ICON);
                    } else {
                        componentProperties.put(FIELD_THUMBNAIL, thumbnailImage);
                    }

                } else if (thumbnailType.equals("icon") || isEmpty(thumbnailType)) {
                    componentProperties.put("iconType", dld.getIconType());
                }

                componentProperties.attr.add("href", href);
                componentProperties.attr.add("data-tags", assetTags);

                componentProperties.put(COMPONENT_ATTRIBUTES, buildAttributesString(componentProperties.attr.getData(), null));

                componentProperties.put("info", MessageFormat.format("({0}, {1})", getFormattedDownloadSize(dld), mimeTypeLabel));
            } else {
                variant = "empty";
            }
        } else {
            variant = "empty";
        }

        //compile variantTemplate param
        if (isNotEmpty(variant)) {
            componentProperties.put(FIELD_VARIANT, variant);
            componentProperties.put(COMPONENT_VARIANT_TEMPLATE, format(COMPONENT_VARIANT_TEMPLATE_FORMAT, variant));
        }
    }

    /**
     * Determine whether file has an associated data blob, if so then get the formatted
     * date.
     *
     * @param dld is the download to check in
     * @return is the formatted size to return (or null)
     */
    @SuppressWarnings("unchecked")
    private String getFormattedDownloadSize(com.day.cq.wcm.foundation.Download dld) {
        String size = "";
        try {
            if (dld.getData() != null) {
                size = getFileSize(dld.getData().getLength());
            }
        } catch (Exception ex) {
            LOGGER.error("getFormattedDownloadSize {}", ex);
        }
        return size;
    }

    /**
     * Determine the filetype by extracting and uppercasing the last element of the mimetype
     *
     * @param resolver resource resolver instance
     * @param download is the download to do this for
     * @return the filetype for the download
     */
    private String getDownloadMimeType(ResourceResolver resolver, com.day.cq.wcm.foundation.Download download) {
        String mimeTypeReturn = "";

        try {

            String filePath = download.getFileReference();
            Resource resource = resolver.resolve(filePath);
            if (!ResourceUtil.isNonExistingResource(resource)) {
                Asset asset = resource.adaptTo(Asset.class);
                if (asset != null) {
                    String mimeType = asset.getMimeType();
                    mimeTypeReturn = mimeType.split("/")[1].toUpperCase();
                }
            }
        } catch (Exception ex) {
            LOGGER.error("getDownloadMimeType: {}", ex);
        }
        return mimeTypeReturn;
    }

    /**
     * To determine the file size in humanly readable measurements we take the
     * following formula: measurementType = floor(log10(size) / 3)
     *
     * @param fileSize is the filesize in bytes
     * @return a formatted file size using humanly readable measurements
     */
    private String getFileSize(Long fileSize) {
        if (fileSize == null) {
            return EMPTY_FILE;
        }
        String fileSizeReturn = EMPTY_FILE;

        try {

            String[] measures = {"bytes", "kB", "MB", "GB"};
            Double measurementIndex = Math.floor(Math.log10(fileSize) / 3.0f);
            Double decSize = fileSize / Math.pow(1024, measurementIndex);
            fileSizeReturn = String.format("%.0f %S", decSize, measures[measurementIndex.intValue()]);

        } catch (Exception ex) {
            LOGGER.error("getFileSize: {}", ex);
        }
        return fileSizeReturn;

    }
}
