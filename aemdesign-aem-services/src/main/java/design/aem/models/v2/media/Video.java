package design.aem.models.v2.media;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.dam.api.Rendition;
import design.aem.components.ComponentProperties;
import design.aem.models.ModelProxy;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;

import javax.jcr.Node;

import static design.aem.utils.components.CommonUtil.getFirstMediaNode;
import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.ConstantsUtil.IMAGE_FILEREFERENCE;
import static design.aem.utils.components.ImagesUtil.DEFAULT_IMAGE_PATH_SELECTOR;
import static design.aem.utils.components.ImagesUtil.DEFAULT_THUMBNAIL_IMAGE_NODE_NAME;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class Video extends ModelProxy {
    private static final String POPUP_HEIGHT = "lightboxHeight";
    private static final String POPUP_WIDTH = "lightboxWidth";

    @SuppressWarnings({"Duplicates", "squid:S3776"})
    protected void ready() throws Exception {
        setComponentFields(new Object[][]{
            {POPUP_HEIGHT, "70"},
            {POPUP_WIDTH, "70"},
            {"thumbnailHeight", "auto"},
            {"thumbnailWidth", "auto"},
            {"assetTitlePrefix", StringUtils.EMPTY},
            {"fileReference", StringUtils.EMPTY},
            {FIELD_VARIANT, DEFAULT_VARIANT}
        });

        String msgStart = "";
        String thumbnail = "";
        String metaTitle = "";
        String metaDesc = "";
        String metaCreator = "";
        String metaCopyRight = "";

        componentProperties = ComponentsUtil.getComponentProperties(
            this,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);

        String fileReference = componentProperties.get(IMAGE_FILEREFERENCE, "");

        Boolean fileReferenceMissing = true;

        componentProperties.put("href", fileReference);

        msgStart = (String) componentProperties.get("assetTitlePrefix");

        if (isNotEmpty(fileReference)) {

            //get asset
            Resource assetR = getResourceResolver().resolve(fileReference);
            if (!ResourceUtil.isNonExistingResource(assetR)) {

                fileReferenceMissing = false;

                Resource fileResource = getResourceResolver().getResource(fileReference);
                if (fileResource != null && !ResourceUtil.isNonExistingResource(fileResource)) {
                    Asset asset = fileResource.adaptTo(Asset.class);
                    if (asset != null) {
                        String videoWidth = asset.getMetadataValue("tiff:ImageWidth");
                        String videoHeight = asset.getMetadataValue("tiff:ImageLength");
                        Rendition rd = asset.getRendition(DEFAULT_IMAGE_PATH_SELECTOR);
                        thumbnail = (rd == null) ? "" : rd.getPath();
                        componentProperties.put(DEFAULT_THUMBNAIL_IMAGE_NODE_NAME, thumbnail);

                        componentProperties.put("videoWidth", videoWidth);
                        componentProperties.put("videoHeight", videoHeight);

                        metaTitle = StringUtils.isBlank(asset.getMetadataValue(DamConstants.DC_TITLE)) ? "" : asset.getMetadataValue(DamConstants.DC_TITLE);
                        metaDesc = StringUtils.isBlank(asset.getMetadataValue(DamConstants.DC_DESCRIPTION)) ? "" : asset.getMetadataValue(DamConstants.DC_DESCRIPTION);
                        metaCreator = StringUtils.isBlank(asset.getMetadataValue(DamConstants.DC_CREATOR)) ? "" : asset.getMetadataValue(DamConstants.DC_CREATOR);
                        metaCopyRight = StringUtils.isBlank(asset.getMetadataValue(DamConstants.DC_RIGHTS)) ? "" : "&amp;copy;" + asset.getMetadataValue(DamConstants.DC_RIGHTS);

                        componentProperties.put("msg", msgStart + metaTitle);
                        componentProperties.put("metaTitle", metaTitle);
                        componentProperties.put("metaDesc", metaDesc);
                        componentProperties.put("metaCreator", metaCreator);
                        componentProperties.put("metaCopyRight", metaCopyRight);

                        Node media = getFirstMediaNode(getResourcePage());
                        //set display area size to first media node
                        if (media != null && !media.getPath().equals(getResource().getPath())) {
                            if (media.hasProperty(POPUP_HEIGHT)) {
                                componentProperties.put(POPUP_HEIGHT, media.getProperty(POPUP_HEIGHT).getValue().toString());
                            } else {
                                componentProperties.put(POPUP_HEIGHT, "");
                            }

                            if (media.hasProperty(POPUP_WIDTH)) {
                                componentProperties.put(POPUP_WIDTH, media.getProperty(POPUP_WIDTH).getValue().toString());
                            } else {
                                componentProperties.put(POPUP_WIDTH, "");
                            }
                        }

                        String lightboxWidth = componentProperties.get(POPUP_WIDTH, "");
                        String lightboxHeight = componentProperties.get(POPUP_HEIGHT, "");

                        componentProperties.put("width", videoWidth);
                        componentProperties.put("height", videoHeight);

                        if (isNotEmpty(lightboxWidth)) {
                            componentProperties.put("width", lightboxWidth);
                        }
                        if (isNotEmpty(lightboxWidth)) {
                            componentProperties.put("height", lightboxHeight);
                        }
                    }
                }
            }
        }

        componentProperties.put("fileReferenceMissing", fileReferenceMissing);
    }
}
