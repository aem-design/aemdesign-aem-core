package design.aem.models.v2.lists;

import com.adobe.granite.asset.api.AssetManager;
import com.day.cq.commons.inherit.InheritanceValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.ConstantsUtil.DEFAULT_CLOUDCONFIG_GOOGLEMAP;
import static design.aem.utils.components.ConstantsUtil.DEFAULT_CLOUDCONFIG_GOOGLEMAP_API_KEY;
import static design.aem.utils.components.ImagesUtil.getMetadataStringForKey;
import static design.aem.utils.components.ImagesUtil.getResourceImagePath;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class LocationList extends List {
    protected static final Logger LOGGER = LoggerFactory.getLogger(LocationList.class);

    @Override
    @SuppressWarnings("Duplicates")
    protected void ready() {
        detailsNameSuffix = new String[]{
            "location-details",
            "generic-details",
        };

        super.ready();

        String mapImagePath = getResourceImagePath(getResource(), "map");

        if (isNotEmpty(mapImagePath)) {
            componentProperties.put("mapPath", mapImagePath);
            componentProperties.attr.add("data-map-path", mapImagePath);

            AssetManager assetManager = getResourceResolver().adaptTo(AssetManager.class);

            if (assetManager != null) {
                com.adobe.granite.asset.api.Asset asset = assetManager.getAsset(mapImagePath);
                String imageLength = getMetadataStringForKey(asset, "tiff:ImageLength");
                String imageWidth = getMetadataStringForKey(asset, "tiff:ImageWidth");

                componentProperties.attr.add("data-map-image-y", imageLength);
                componentProperties.attr.add("data-map-image-x", imageWidth);

                componentProperties.attr.add("style", "height:" + imageLength + "px;");
                componentProperties.attr.add("style", "width:" + imageWidth + "px;");
            } else {
                LOGGER.error("ImageImpl: could not get AssetManager object");
            }
        }

        componentProperties.attr.add("wcmmode", getWcmMode().toString().toLowerCase());

        String googleApiKey = getCloudConfigProperty(
            (InheritanceValueMap) getPageProperties(),
            DEFAULT_CLOUDCONFIG_GOOGLEMAP,
            DEFAULT_CLOUDCONFIG_GOOGLEMAP_API_KEY,
            getSlingScriptHelper());

        componentProperties.attr.add("data-map-apikey", googleApiKey);

        componentProperties.put(COMPONENT_ATTRIBUTES,
            buildAttributesString(componentProperties.attr.getData(), null));
    }

    @Override
    protected String getComponentCategory() {
        return "locationlist";
    }
}
