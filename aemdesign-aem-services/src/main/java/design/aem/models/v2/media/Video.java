package design.aem.models.v2.media;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.dam.api.Rendition;
import com.day.cq.i18n.I18n;
import design.aem.components.ComponentProperties;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;

import static design.aem.utils.components.CommonUtil.getFirstMediaNode;
import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.ConstantsUtil.IMAGE_FILEREFERENCE;
import static design.aem.utils.components.ImagesUtil.DEFAULT_IMAGE_PATH_SELECTOR;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class Video extends WCMUsePojo {

    private static final Logger LOGGER = LoggerFactory.getLogger(Video.class);

    private ComponentProperties componentProperties = null;

    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }

    @Override
    public void activate() throws Exception {

        I18n _i18n = new I18n(getRequest());


        Object[][] componentFields = {
                {"lightboxHeight", "70"},
                {"lightboxWidth", "70"},
                {"thumbnailHeight", "auto"},
                {"thumbnailWidth", "auto"},
                {"assetTitlePrefix", StringUtils.EMPTY},
                {"fileReference", StringUtils.EMPTY},
                {FIELD_VARIANT, DEFAULT_VARIANT}

        };
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

                Asset asset = getResourceResolver().getResource(fileReference).adaptTo(Asset.class);
                String videoWidth = asset.getMetadataValue("tiff:ImageWidth");
                String videoHeight = asset.getMetadataValue("tiff:ImageLength");
                Rendition rd = asset.getRendition(DEFAULT_IMAGE_PATH_SELECTOR);
                thumbnail = (rd == null) ? "" : rd.getPath();
                componentProperties.put("thumbnail", thumbnail);

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
                if (media != null && !media.getPath().equals(getResource().adaptTo(Node.class).getPath())) {
                    if (media.hasProperty("lightboxHeight")) {
                        componentProperties.put("lightboxHeight", media.getProperty("lightboxHeight").getValue().toString());
                    } else {
                        componentProperties.put("lightboxHeight", "");
                    }

                    if (media.hasProperty("lightboxWidth")) {
                        componentProperties.put("lightboxWidth", media.getProperty("lightboxWidth").getValue().toString());
                    } else {
                        componentProperties.put("lightboxWidth", "");
                    }
                }

                String lightboxWidth = componentProperties.get("lightboxWidth", "");
                String lightboxHeight = componentProperties.get("lightboxHeight", "");

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

        componentProperties.put("fileReferenceMissing", fileReferenceMissing);


    }


}