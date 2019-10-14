package design.aem.models.v2.media;

import com.adobe.granite.asset.api.AssetManager;
import com.day.cq.dam.api.Asset;
import com.day.cq.i18n.I18n;
import com.day.cq.wcm.api.Page;
import design.aem.components.ComponentProperties;
import design.aem.models.ModelProxy;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

import static design.aem.utils.components.CommonUtil.*;
import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.ConstantsUtil.DATA_ATTRIBUTE_PREFIX;
import static design.aem.utils.components.ConstantsUtil.IMAGE_FILEREFERENCE;
import static design.aem.utils.components.ImagesUtil.*;
import static java.text.MessageFormat.format;
import static org.apache.commons.lang3.StringUtils.*;

public class Image extends ModelProxy {
    protected static final Logger LOGGER = LoggerFactory.getLogger(Image.class);

    protected ComponentProperties componentProperties = null;
    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }

    @SuppressWarnings({"Duplicates","squid:S3776"})
    protected void ready() {
        I18n i18n = new I18n(getRequest());

        final String DEFAULT_I18N_CATEGORY = "image";
        final String DEFAULT_I18N_LABEL_LICENSEINFO = "licenseinfo";
        final String DEFAULT_ARIA_ROLE = "banner";
        final String FIELD_LINKURL = "linkURL";
        final String FIELD_IMAGEURL = "imageURL";
        final String FIELD_RENDITIONS = "renditions";
        final String DEFAULT_TITLE_TAG_TYPE = "h4";

        /*
          Component Fields Helper

          Structure:
          1 required - property name,
          2 required - default value,
          3 optional - name of component attribute to add value into
          4 optional - canonical name of class for handling multivalues, String or Tag
         */
        setComponentFields(new Object[][]{
                {FIELD_VARIANT, DEFAULT_VARIANT},
                {FIELD_LINKURL,StringUtils.EMPTY},
                {FIELD_ARIA_ROLE,DEFAULT_ARIA_ROLE, FIELD_ARIA_DATA_ATTRIBUTE_ROLE},
                {FIELD_TITLE_TAG_TYPE, DEFAULT_TITLE_TAG_TYPE},
        });

        componentProperties = ComponentsUtil.getComponentProperties(
                this,
                componentFields,
                DEFAULT_FIELDS_IMAGE_OPTIONS,
                DEFAULT_FIELDS_STYLE,
                DEFAULT_FIELDS_ANALYTICS,
                DEFAULT_FIELDS_ACCESSIBILITY);

        String fileReference = componentProperties.get(IMAGE_FILEREFERENCE, "");

        Boolean fileReferenceMissing = true;

        if (isNotEmpty(fileReference)) {

            //get asset
            Resource assetR = getResourceResolver().resolve(fileReference);
            if (!ResourceUtil.isNonExistingResource(assetR)) {

                fileReferenceMissing = false;

                AssetManager assetManager = getResourceResolver().adaptTo(AssetManager.class);
                if (assetManager != null) {
                    com.adobe.granite.asset.api.Asset asset = assetManager.getAsset(fileReference);

                    Asset assetBasic = assetR.adaptTo(Asset.class);

                    if (asset != null && assetBasic != null) {
						//get asset metadata
						String assetUID = asset.getIdentifier();
						String licenseInfo = getAssetCopyrightInfo(assetBasic, i18n.get(DEFAULT_I18N_LABEL_LICENSEINFO, DEFAULT_I18N_CATEGORY));
						componentProperties.put(FIELD_LICENSE_INFO, licenseInfo);
						componentProperties.put(FIELD_ASSETID, assetUID);


						//get asset properties
						ComponentProperties assetProperties = ComponentsUtil.getComponentProperties(this, asset, DEFAULT_FIELDS_ASSET_IMAGE);
						//add asset properties to component properties and ensure licensed image meta does not get overwritten
						componentProperties.putAll(assetProperties, isEmpty(licenseInfo));

						//if asset is not licensed
						if (isEmpty(licenseInfo)) {
							//get asset properties override from component
							ComponentProperties assetPropertiesOverride = ComponentsUtil.getComponentProperties(this, null, false, DEFAULT_FIELDS_ASSET_IMAGE);

							//add asset properties override to component properties and ensure licensed image meta does not get overwritten
							componentProperties.putAll(assetPropertiesOverride, isEmpty(licenseInfo));

						}

						//ensure something is added as title
						String title = componentProperties.get(DAM_TITLE, "");
						if (isEmpty(title) && isNotNull(assetBasic)) {
							componentProperties.put(DAM_TITLE, defaultString(assetBasic.getName(), asset.getName()) );
						}

						componentProperties.attr.add(DATA_ATTRIBUTE_PREFIX + FIELD_ASSETID, assetUID);
						componentProperties.attr.add(DATA_ATTRIBUTE_PREFIX + FIELD_ASSET_TRACKABLE, true);
						componentProperties.attr.add(DATA_ATTRIBUTE_PREFIX + FIELD_ASSET_LICENSED, isNotBlank(licenseInfo));
						componentProperties.attr.add(FIELD_DATA_ANALYTICS_EVENT_LABEL, componentProperties.get(DAM_TITLE, ""));
						componentProperties.attr.add(FIELD_DATA_ANALYTICS_METATYPE, defaultString(assetBasic.getMimeType(),""));
						componentProperties.attr.add(FIELD_DATA_ANALYTICS_FILENAME, assetBasic.getPath());
                    } else {
						LOGGER.error("ImageImpl: null check asset={} and assetBasic={}", asset, assetBasic);
					}
                } else {
                    LOGGER.error("ImageImpl: could not get AssetManager object");
                }

                //get page link
                String linkURL = componentProperties.get(FIELD_LINKURL, StringUtils.EMPTY);
                if (isNotEmpty(linkURL)) {
                    Page imageTargetPage = getPageManager().getPage(linkURL);
                    if (imageTargetPage != null) {
                        linkURL = getPageUrl(imageTargetPage);
                    }
                } else {
                    //use dam link source from Asset if not assigned
                    linkURL = componentProperties.get(DAM_SOURCE_URL, StringUtils.EMPTY);
                }
                componentProperties.put(FIELD_LINKURL, linkURL);

                //get renditions for current resource
                ComponentProperties imageProps = getResourceImageRenditions(this, getResource(),FIELD_RENDITIONS, FIELD_IMAGEURL);
                Map<String, String> renditions = imageProps.get(FIELD_RENDITIONS, new LinkedHashMap<>());

                if (imageProps.containsKey(FIELD_IMAGEURL)) {
                    componentProperties.put(FIELD_IMAGEURL,imageProps.get(FIELD_IMAGEURL,""));
                }

                componentProperties.put(FIELD_RENDITIONS, renditions);

            }
        }

        componentProperties.put("fileReferenceMissing",fileReferenceMissing);

        String variant = componentProperties.get(FIELD_VARIANT, "");

        //imageOnly is default
        if (variant.equals("imageOnly")) {
            variant = DEFAULT_VARIANT;
        }

        if (fileReferenceMissing) {
            variant = "empty";
        }

        componentProperties.put(FIELD_VARIANT,variant);

        //compile variantTemplate param
        componentProperties.put(COMPONENT_VARIANT_TEMPLATE, format(COMPONENT_VARIANT_TEMPLATE_FORMAT,variant));

        if (getWcmMode().isEdit()) {
            //add class to allow image dropping
            componentProperties.attr.add("class", "cq-dd-image");
        }

        componentProperties.put(COMPONENT_ATTRIBUTES, buildAttributesString(componentProperties.attr.getData(), null));
    }
}
