package design.aem.models.v3.content;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.granite.asset.api.AssetManager;
import org.apache.jackrabbit.vault.util.JcrConstants;
import com.day.cq.dam.api.Asset;
import com.day.cq.wcm.api.Page;
import design.aem.components.ComponentProperties;
import design.aem.models.GenericComponent;
import design.aem.models.GenericModel;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static design.aem.utils.components.CommonUtil.*;
import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.ConstantsUtil.DATA_ATTRIBUTE_PREFIX;
import static design.aem.utils.components.ConstantsUtil.IMAGE_FILEREFERENCE;
import static design.aem.utils.components.ImagesUtil.*;
import static java.text.MessageFormat.format;
import static org.apache.commons.lang3.StringUtils.*;


@Model(adaptables = SlingHttpServletRequest.class,
        adapters = {GenericComponent.class, ComponentExporter.class},
        resourceType = {ImageImpl.RESOURCE_TYPE_V3})
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ImageImpl extends GenericModel implements GenericComponent {
    protected static final String RESOURCE_TYPE_V3 = "aemdesign/components/media/image/v3/image";

    protected static final Logger LOGGER = LoggerFactory.getLogger(ImageImpl.class);

    /**
     * Component property name that indicates which Image Component will perform the image rendering for composed components. When
     * rendering images, the composed components that provide this property will be able to retrieve the content policy defined for the
     * Image Component's resource type.
     */
    public static final String IMAGE_DELEGATE = "imageDelegate";

    private List<String> hiddenImageResourceProperties = new ArrayList<>();

    public ImageImpl() {
        hiddenImageResourceProperties.add(JcrConstants.JCR_TITLE);
        hiddenImageResourceProperties.add(JcrConstants.JCR_DESCRIPTION);
    }

    @PostConstruct
	@SuppressWarnings({"Duplicates","squid:S3776"})
    protected void initModel() {


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
        Object[][] componentFields = {
                {FIELD_VARIANT, DEFAULT_VARIANT},
                {FIELD_LINKURL, StringUtils.EMPTY},
                {FIELD_ARIA_ROLE, DEFAULT_ARIA_ROLE, FIELD_ARIA_DATA_ATTRIBUTE_ROLE},
                {FIELD_TITLE_TAG_TYPE, DEFAULT_TITLE_TAG_TYPE},
        };


        componentProperties = ComponentsUtil.getComponentProperties(
                this,
                componentFields,
                DEFAULT_FIELDS_STYLE,
                DEFAULT_FIELDS_ACCESSIBILITY,
                DEFAULT_FIELDS_ANALYTICS,
                DEFAULT_FIELDS_IMAGE_OPTIONS);

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
						String licenseInfo = getAssetCopyrightInfo(assetBasic, getI18n().get(DEFAULT_I18N_LABEL_LICENSEINFO, DEFAULT_I18N_CATEGORY));
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
						if (isEmpty(title)) {
							componentProperties.put(DAM_TITLE, assetBasic.getName());
						}

						componentProperties.attr.add(DATA_ATTRIBUTE_PREFIX + FIELD_ASSETID, assetUID);
						componentProperties.attr.add(DATA_ATTRIBUTE_PREFIX + FIELD_ASSET_TRACKABLE, true);
						componentProperties.attr.add(DATA_ATTRIBUTE_PREFIX + FIELD_ASSET_LICENSED, isNotBlank(licenseInfo));
						componentProperties.attr.add(FIELD_DATA_ANALYTICS_EVENT_LABEL, componentProperties.get(DAM_TITLE, ""));
						componentProperties.attr.add(FIELD_DATA_ANALYTICS_METATYPE, assetBasic.getMimeType());
						componentProperties.attr.add(FIELD_DATA_ANALYTICS_FILENAME, assetBasic.getPath());
                    } else {
						LOGGER.error("ImageImpl: null check asset={} and assetBasic={}", asset, assetBasic);
					}
                } else {
                    LOGGER.error("ImageImpl: could not get AssetManager object");
                }

                componentProperties.put(COMPONENT_ATTRIBUTES, buildAttributesString(componentProperties.attr.getData(), null));

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
                ComponentProperties imageProps = getResourceImageRenditions(getPageContextMap(), getResource(), FIELD_RENDITIONS, FIELD_IMAGEURL);
                Map<String, String> renditions = imageProps.get(FIELD_RENDITIONS, new LinkedHashMap<>());

                if (imageProps.containsKey(FIELD_IMAGEURL)) {
                    componentProperties.put(FIELD_IMAGEURL, imageProps.get(FIELD_IMAGEURL, ""));
                }

                componentProperties.put(FIELD_RENDITIONS, renditions);

            }


        }

        componentProperties.put("fileReferenceMissing", fileReferenceMissing);

        String variant = componentProperties.get(FIELD_VARIANT, "");

        //imageOnly is default
        if (variant.equals("imageOnly")) {
            variant = DEFAULT_VARIANT;
        }

        componentProperties.put(FIELD_VARIANT, variant);

        //compile variantTemplate param
        componentProperties.put(COMPONENT_VARIANT_TEMPLATE, format(COMPONENT_VARIANT_TEMPLATE_FORMAT, variant));

    }


}
