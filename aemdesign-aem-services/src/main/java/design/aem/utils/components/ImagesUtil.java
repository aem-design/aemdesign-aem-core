package design.aem.utils.components;


import com.adobe.cq.sightly.WCMUsePojo;
import com.adobe.xmp.XMPDateTime;
import com.adobe.xmp.XMPDateTimeFactory;
import com.day.cq.commons.ImageResource;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.dam.api.Rendition;
import com.day.cq.dam.commons.util.DamUtil;
import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.foundation.Image;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import design.aem.components.ComponentProperties;
import design.aem.services.ContentAccess;
import design.aem.utils.WidthBasedRenditionComparator;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.jackrabbit.vault.util.JcrConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.jsp.PageContext;
import java.net.URI;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;

import static com.day.cq.dam.api.DamConstants.METADATA_FOLDER;
import static design.aem.utils.components.CommonUtil.*;
import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.ConstantsUtil.*;
import static design.aem.utils.components.ResolverUtil.mappedUrl;
import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.jackrabbit.JcrConstants.JCR_CONTENT;


public class ImagesUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImagesUtil.class);

    public static final String DEFAULT_IMAGE_THUMB_SELECTOR = ".thumb.319.319.png";
    public static final String SMALL_IMAGE_THUMB_SELECTOR = ".thumb.140.100.png";
    public static final String DEFAULT_THUMB_SELECTOR_XSM = ".thumb.140.140.png";
    public static final String DEFAULT_THUMB_SELECTOR_SM = ".thumb.319.319.png";
    public static final String DEFAULT_THUMB_SELECTOR_MD = ".thumb.800.800.png";
    public static final String DEFAULT_THUMB_SELECTOR_LG = ".thumb.1280.1280.png";

    public static final String FORM_CHOOSER_SELECTOR_SERVLET = ".form";

    public static final String DEFAULT_IMAGE_PATH = "/content/dam/aemdesign/common/placeholder.png";
    public static final String DEFAULT_IMAGE_PATH_RENDITION = "/content/dam/aemdesign/common/placeholder".concat(DEFAULT_IMAGE_THUMB_SELECTOR);

    public static final String SMALL_IMAGE_PATH_SELECTOR = "cq5dam" + SMALL_IMAGE_THUMB_SELECTOR;
    public static final String DEFAULT_IMAGE_PATH_SELECTOR = "cq5dam" + DEFAULT_IMAGE_THUMB_SELECTOR;
    public static final String DEFAULT_DOWNLOAD_THUMB_ICON = "/content/dam/aemdesign/common/download.png";

    public static final String MEDIUM_THUMBNAIL_SIZE = "320";
    public static final String LARGE_THUMBNAIL_SIZE = "480";

    public static final String DEFAULT_BACKGROUND_IMAGE_NODE_NAME = "bgimage";
    public static final String DEFAULT_BACKGROUND_VIDEO_NODE_NAME = "bgvideo";
    public static final String DEFAULT_SECONDARY_IMAGE_NODE_NAME = "secondaryImage";
    public static final String DEFAULT_THUMBNAIL_IMAGE_NODE_NAME = "thumbnail";
    public static final String DEFAULT_BADGETHUMBNAIL_IMAGE_NODE_NAME = "badgeThumbnail";
    public static final String DEFAULT_IMAGE_NODE_NAME = "image";

    public static final String FIELD_RENDITIONS_VIDEO = "renditionsVideo";


    public static final String RENDITION_PROFILE_CUSTOM = "cq5dam.custom.";

    public static final String RENDITION_REGEX_PATTERN = "^(\\w*)\\.(\\w*)\\.(\\d*)\\.(\\d*)\\.(\\S{3,4})$";

    //this is used to store admin config for image component
    public static final String OSGI_CONFIG_MEDIA_IMAGE = "aemdesign.components.media.image";


    public static final String FIELD_RENDITION_PREFIX = "renditionPrefix";
    public static final String FIELD_RESPONSIVE_MAP = "renditionImageMapping";
    public static final String FIELD_ADAPTIVE_MAP = "adaptiveImageMapping";
    public static final String FIELD_MEDIAQUERYRENDITION_KEY = "assetMediaQuery";
    public static final String FIELD_MEDIAQUERYRENDITION_VALUE = "assetMediaQueryRendition";
    public static final String FIELD_IMAGE_OPTION = "imageOption";

    public static final String ASSET_METADATA_FOLDER = JCR_CONTENT.concat("/").concat(METADATA_FOLDER);

    public static final String[] DEFAULT_RENDITION_IMAGE_MAP = new String[]{ //NOSONAR used by classes
            "48=(min-width: 1px) and (max-width: 72px)",
            "140=(min-width: 73px) and (max-width: 210px)",
            "319=(min-width: 211px) and (max-width: 478px)",
            "1280=(min-width: 478px)"
    };

    //core: 128, 256, 512, 1024, 1280, 1440, 1920, 2048
    //normal: 480, 640, 720, 800, 960, 1024, 1280, 1440, 1920, 2048
    public static final String[] DEFAULT_ADAPTIVE_IMAGE_MAP = new String[]{ //NOSONAR used by classes
            "480=(min-width: 1px) and (max-width: 533px)",
            "640=(min-width: 534px) and (max-width: 691px)",
            "720=(min-width: 692px) and (max-width: 770px)",
            "800=(min-width: 771px) and (max-width: 848px)",
            "960=(min-width: 849px) and (max-width: 1008px)",
            "1024=(min-width: 1009px) and (max-width: 1075px)",
            "1280=(min-width: 1076px) and (max-width: 1331px)",
            "1440=(min-width: 1332px) and (max-width: 1572px)",
            "1920=(min-width: 1573px) and (max-width: 1971px)",
            "2048=(min-width: 1971px)"
    };

    /**
     * Image Options
     *
     * Structure:
     * 1 required - property name,
     * 2 required - default value,
     * 3 optional - name of component attribute to add value into
     * 4 optional - canonical name of class for handling multivalues, String or Tag
     */
    public static final Object[][] DEFAULT_FIELDS_IMAGE_OPTIONS = { //NOSONAR used by classes
            {FIELD_IMAGE_OPTION, IMAGE_OPTION_RESPONSIVE},
            {ImageResource.PN_HTML_WIDTH, ""},
            {ImageResource.PN_HTML_HEIGHT, ""},
            {ImageResource.PN_WIDTH, 0},
            {ImageResource.PN_HEIGHT, 0},
            {IMAGE_FILEREFERENCE, ""},
            {FIELD_ADAPTIVE_MAP, DEFAULT_ADAPTIVE_IMAGE_MAP},
            {FIELD_RESPONSIVE_MAP, DEFAULT_RENDITION_IMAGE_MAP},
            {FIELD_RENDITION_PREFIX, StringUtils.EMPTY},
            {FIELD_MEDIAQUERYRENDITION_KEY, new String[]{}, "", Tag.class.getCanonicalName()},
            {FIELD_MEDIAQUERYRENDITION_VALUE, new String[0]},
    };


    /**
     * Background Video Options
     *
     * Structure:
     * 1 required - property name,
     * 2 required - default value,
     * 3 optional - name of component attribute to add value into
     * 4 optional - canonical name of class for handling multivalues, String or Tag
     */
    public static final Object[][] DEFAULT_FIELDS_BACKGROUNDVIDEO_OPTIONS = { //NOSONAR used by classes
            {IMAGE_FILEREFERENCE, ""},
    };

    /**
     * Necessary to make sure some of the weird behavior CQ exhibits gets worked around. In
     * some cases (don't know when exactly), the asset dc:title and dc:description keys are
     * returned as Object[] with one element instead of a String. This method tests that
     * and returns the first element from the list or just the element itself
     *
     * @param assetNode is the asset to interogate
     * @param key       is the key to get the metadata for
     * @param defaultValue default value to return on null
     * @return the value or null when nothing is found
     */
    public static String getMetadataStringForKey(Node assetNode, String key, String defaultValue) {
        if (assetNode == null) {
            return null;
        }
        if (isBlank(key)) {
            return null;
        }

        String returnVal = "";

        try {

            if (assetNode.hasNode(ASSET_METADATA_FOLDER)) {
                Node metadataNode = assetNode.getNode(ASSET_METADATA_FOLDER);
                returnVal = DamUtil.getValue(metadataNode, key, defaultValue);
            }
        } catch (RepositoryException rex) {
            // If this fails it's ok, we return 0 as fallback
        }
        return returnVal;

    }

    /**
     * The same workaround as below getMetadataStringForKey(com.day.cq.dam.api.Asset asset, String key)
     * Just with different signature
     *
     * @param asset is the asset to interogate
     * @param key   is the key to get the metadata for
     * @return the value or null when nothing is found
     */
    public static String getMetadataStringForKey(com.adobe.granite.asset.api.Asset asset, String key) {
        if (asset == null) {
            return null;
        }
        if (isBlank(key)) {
            return null;
        }

        Object metadataObj = null;
        Node node = asset.adaptTo(Node.class);
        try {

            if (node != null) {

                Resource metadataRes = asset.getResourceResolver().getResource(node.getPath().concat("/").concat(ASSET_METADATA_FOLDER));

                if (metadataRes != null) {
                    ValueMap map = metadataRes.adaptTo(ValueMap.class);
                    if (map != null && map.containsKey(key)) {
                        metadataObj = map.get(key).toString();
                    }
                }
            }

        } catch (Exception ex) {
            LOGGER.error("getMetadataStringForKey: {}", ex);
        }


        if (metadataObj == null) {
            return null;
        }
        if (metadataObj.getClass().isArray()) {
            return ((Object[]) metadataObj)[0].toString();
        }
        return metadataObj.toString();
    }

    /**
     * Necessary to make sure some of the weird behavior CQ exhibits gets worked around. In
     * some cases (don't know when exactly), the asset dc:title and dc:description keys are
     * returned as Object[] with one element instead of a String. This method tests that
     * and returns the first element from the list or just the element itself
     *
     * @param asset is the asset to interogate
     * @param key   is the key to get the metadata for
     * @return the value or null when nothing is found
     */
    public static String getMetadataStringForKey(com.day.cq.dam.api.Asset asset, String key) {
        if (asset == null) {
            return null;
        }
        if (isBlank(key)) {
            return null;
        }

        Object metadataObj = asset.getMetadata(key);
        if (metadataObj == null) {
            return null;
        }
        if (metadataObj.getClass().isArray()) {
            return ((Object[]) metadataObj)[0].toString();
        }
        return metadataObj.toString();
    }

    /***
     * get rendition matching selected width.
     * @param asset asset to use
     * @param minWidth width to find
     * @return found rendition
     */
    public static Rendition getThumbnail(Asset asset, int minWidth) {
        if (asset == null) {
            return null;
        }

        try {

            return DamUtil.getBestFitRendition(minWidth, asset.getRenditions());

        } catch (Exception ex) {
            LOGGER.error("getThumbnail: {}", ex);
        }
        return null;
    }

    /**
     * get thumbnail url for a page.
     * @param page page object
     * @param resourceResolver resource resolver instance
     * @return page thumbnail url
     */
    public static String getThumbnailUrl(Page page, ResourceResolver resourceResolver) {
        return resourceResolver.map(page.getPath().concat(DEFAULT_THUMB_SELECTOR_MD));
    }


    /**
     * Get width of the asset
     *
     * @param assetNode asset node to check metadata in
     * @return width of asset
     * @throws javax.jcr.RepositoryException when can't access asset
     */
    public static int getWidth(Node assetNode) throws RepositoryException {
        int width = 0;
        if (assetNode.hasNode(ASSET_METADATA_FOLDER)) {
            Node metadataNode = assetNode.getNode(ASSET_METADATA_FOLDER);
            try {
                width = Integer.valueOf(
                        DamUtil.getValue(metadataNode, "tiff:ImageWidth",
                                DamUtil.getValue(metadataNode, "exif:PixelXDimension", "")));
            } catch (Exception e) {
                // If this fails it's ok, we return 0 as fallback
            }
        }
        return width;
    }

    /**
     * Get the processed version of an image; it will have cropping etc applied to it
     *
     * @param resource     is the resource to read from
     * @param relativePath is the path under which the image was stored
     * @return the image instance
     */
    public static Image getProcessedImage(Resource resource, String relativePath) {
        Image image = null;
        if (relativePath != null) {
            image = new Image(resource, relativePath);
        } else {
            image = new Image(resource);
        }

        // make it a servlet call so it processes the cropping etc.
        image.setSelector(".img");

        return image;
    }


    /**
     * Get a scaled down, cropped out version of the image at `relativePath` with a
     * maximum width of `width`.
     *
     * @param resource     is the resource the image is a part of
     * @param relativePath is the relative path
     * @param maxWidth     is the maximum width
     * @return is the image object that we will render.
     * @deprecated use Image Server to do this
     */
    @Deprecated
    public static Image getScaledProcessedImage(Resource resource, String relativePath, int maxWidth) {
        Image image = getProcessedImage(resource, relativePath);

        // set the selector to be the thumbnail selector
        image.setSelector(".scale.thumbnail." + maxWidth);

        return image;
    }

    /***
     * get image url for a resource if defined.
     * @param resource parent resource to use
     * @param imageResourceName name of child node
     * @return path to image
     */
    public static String getResourceImageCustomHref(Resource resource, String imageResourceName) {
        String imageSrc = "";
        if (resource == null || isEmpty(imageResourceName)) {
            return imageSrc;
        }
        Resource imageResource = resource.getChild(imageResourceName);
        if (imageResource != null) {
            Resource fileReference = imageResource.getChild(IMAGE_FILEREFERENCE);
            if (fileReference != null) {
                if (imageResource.getResourceType().equals(DEFAULT_IMAGE_RESOURCETYPE) || imageResource.getResourceType().endsWith(DEFAULT_IMAGE_RESOURCETYPE_SUFFIX)) {
                    Long lastModified = getLastModified(imageResource);
                    imageSrc = MessageFormat.format(DEFAULT_IMAGE_GENERATED_FORMAT, imageResource.getPath(), lastModified.toString());
                    imageSrc = mappedUrl(resource.getResourceResolver(), imageSrc);
                }
            }
        }
        return imageSrc;
    }

    /***
     * get asset reference for image node from a page
     * @param page to use as source
     * @return path to image or return default reference to page thumbnail selector
     */
    public static String getPageImgReferencePath(Page page) {
        String imagePath = getResourceImagePath(page.getContentResource(), DEFAULT_IMAGE_NODE_NAME);

        if (isEmpty(imagePath)) {
            imagePath = page.getPath().concat(DEFAULT_IMAGE_THUMB_SELECTOR);
        }

        return imagePath;
    }

    /***
     * get fileReference from image node of a resource.
     * @param resource resource to use
     * @param imageResourceName image node name
     * @return image path
     */
    public static String getResourceImagePath(Resource resource, String imageResourceName) {
        String fileReferencPath = "";
        if (resource == null || isEmpty(imageResourceName)) {
            return fileReferencPath;
        }
        try {
            Resource fileReference = resource.getChild(imageResourceName);
            if (fileReference != null) {
                Node fileReferenceNode = fileReference.adaptTo(Node.class);
                if (fileReferenceNode != null) {
                    if (fileReferenceNode.hasProperty(IMAGE_FILEREFERENCE)) {
                        fileReferencPath = fileReferenceNode.getProperty(IMAGE_FILEREFERENCE).getString();
                    }
                }
            }
        } catch (Exception ex) {
            LOGGER.error("getResourceImagePath: Exception occurred: {}", ex);
        }
        return fileReferencPath;
    }

    /***
     * get asset metadata value and return default value if its empty.
     * @param asset asset to use
     * @param name name of metadata field
     * @param defaultValue default value to return
     * @return asset property value
     */
    public static String getAssetPropertyValueWithDefault(Asset asset, String name, String defaultValue) {
        if (asset == null) {
            return defaultValue;
        }

        try {

            String assetValue = asset.getMetadataValue(name);

            if (isNotEmpty(assetValue)) {
                return assetValue;
            }

        } catch (Exception ex) {
            LOGGER.error("getAssetPropertyValueWithDefault: Exception occurred: {}", ex);
        }

        return defaultValue;
    }


    /***
     * get formatted copyright info text for an asset.
     * @param asset asset to use
     * @param format format to use with fields merge
     * @return copyright text
     */
    public static String getAssetCopyrightInfo(Asset asset, String format) {
        String copyrightInfo = "";
        if (asset == null) {
            return copyrightInfo;
        }
        if (isEmpty(format)) {
            format = DAM_LICENSE_FORMAT;
        }

        try {
            String assetCreator = getAssetPropertyValueWithDefault(asset, DamConstants.DC_CREATOR, "");
            String assetContributor = getAssetPropertyValueWithDefault(asset, DamConstants.DC_CONTRIBUTOR, "");
            String assetLicense = getAssetPropertyValueWithDefault(asset, DamConstants.DC_RIGHTS, "");
            String assetCopyrightOwner = getAssetPropertyValueWithDefault(asset, DAM_FIELD_LICENSE_COPYRIGHT_OWNER, "");
            String assetUsageTerms = asset.getMetadataValue(DAM_FIELD_LICENSE_USAGETERMS);

            String assetExpiresYear = "";
            String assetExpires = asset.getMetadataValue(DAM_FIELD_LICENSE_EXPIRY);

            if (assetExpires != null) {
                XMPDateTime assetExpiresDate = XMPDateTimeFactory.createFromISO8601(assetExpires);
                assetExpiresYear = Integer.toString(assetExpiresDate.getCalendar().get(Calendar.YEAR));
            }

            String values = StringUtils.join(assetCreator, assetContributor, assetLicense, assetCopyrightOwner, assetExpiresYear).trim();
            if (isNotEmpty(values)) {
                copyrightInfo = MessageFormat.format(format, assetCreator, assetContributor, assetLicense, assetCopyrightOwner, assetExpiresYear);
            }
        } catch (Exception ex) {
            LOGGER.error("getAssetCopyrightInfo: Exception occurred: {}", ex);
        }

        return copyrightInfo;

    }


    public static int getWidth(com.adobe.granite.asset.api.Rendition r) {
        return getDimension(r, "tiff:ImageWidth");
    }

    public static int getDimension(com.adobe.granite.asset.api.Rendition r, String dimensionProperty) {
        if (r == null) {
            LOGGER.debug("Null rendition at", new Exception("Null rendition"));
            return 0;
        }
        if ((dimensionProperty == null) || ((!dimensionProperty.equals("tiff:ImageLength")) && (!dimensionProperty.equals("tiff:ImageWidth")))) {
            LOGGER.warn("Incorrect dimension property for {}, Invalid property name {}", r.getPath(), dimensionProperty);
            return 0;
        }
        String name = r.getName();
        if (isEmpty(name)) {
            LOGGER.warn("Empty name returned at {}", r.getPath());
            return 0;
        }
        try {
            if (name.equals("original")) {
                com.adobe.granite.asset.api.Asset asset = r.adaptTo(com.adobe.granite.asset.api.Asset.class);
                if (asset == null) {
                    LOGGER.debug("Rendition at {} is not adaptable to an asset.", r.getPath());
                    return 0;
                }

                String val = null;
                Node assetNode = asset.adaptTo(Node.class);
                if (assetNode != null && !assetNode.hasNode(JCR_CONTENT.concat("/").concat(METADATA_FOLDER))) {
                    Node assetMetadata = assetNode.getNode(JCR_CONTENT.concat("/").concat(METADATA_FOLDER));
                    if (assetMetadata.hasProperty(dimensionProperty)) {
                        val = assetMetadata.getProperty(dimensionProperty).getString();
                    }
                }

                if ((val == null) || (val.length() == 0)) {
                    LOGGER.debug("Unable to find metadata property {} for {}", dimensionProperty, asset.getPath());
                    return 0;
                }
                try {
                    return Integer.parseInt(val);
                } catch (NumberFormatException nfe) {
                    LOGGER.warn("Metadata property {} was {} and not a number at {}", dimensionProperty, val, asset.getPath());
                    return 0;
                }
            }
            Matcher matcher = DEFAULT_RENDTION_PATTERN_OOTB.matcher(name);
            if (matcher.matches()) {
                int matcherIndex;
                if ("tiff:ImageLength".equals(dimensionProperty)) {
                    matcherIndex = 3;
                } else {
                    matcherIndex = 2;
                }
                return Integer.parseInt(matcher.group(matcherIndex));
            }
            LOGGER.debug("Unknown naming format for name {} at {}", name, r.getPath());
            return 0;
        } catch (Exception e) {
            LOGGER.warn("Unexpected exception finding dimension for asset at {}, {}", r.getPath(), e);
        }
        return 0;
    }

    /***
     * get a best fitting rendition for an asset resolved from asset path.
     * @param assetPath asset to use
     * @param renditionWidth rendition name
     * @param resourceResolver resource resolver
     * @return path to rendition
     */
    public static String getBestFitRendition(String assetPath, int renditionWidth, ResourceResolver resourceResolver) {
        String renditionPath = assetPath;

        if (isEmpty(assetPath)) {
            return "";
        }

        try {

            com.adobe.granite.asset.api.AssetManager assetManager = resourceResolver.adaptTo(com.adobe.granite.asset.api.AssetManager.class);
            if (assetManager != null) {
                com.adobe.granite.asset.api.Asset asset = assetManager.getAsset(assetPath);

                if (asset != null) {

                    com.adobe.granite.asset.api.Rendition bestRendition = getBestFitRendition(renditionWidth, asset);
                    if (bestRendition != null) {
                        renditionPath = bestRendition.getPath();
                    }

                }
            } else {
                LOGGER.error("getBestFitRendition: could not get AssetManager object");
            }

        } catch (Exception ex) {
            LOGGER.error("getBestFitRendition error {} ", ex);
        }
        return renditionPath;
    }

    /***
     * allow picking of best rendition by width based on default prefixes.
     * @param width min width
     * @param asset asset with renditions
     * @return matching rendition
     */
    public static com.adobe.granite.asset.api.Rendition getBestFitRendition(int width, com.adobe.granite.asset.api.Asset asset) {
        List<com.adobe.granite.asset.api.Rendition> renditions = Lists.newArrayList(asset.listRenditions());
        return getBestFitRendition(width, renditions, null);
    }

    /***
     * allow picking of best rendition by width with optional prefix.
     * @param width min width
     * @param asset asset with renditions
     * @param renditionPrefix rendition prefix
     * @return matching rendition
     */
    public static com.adobe.granite.asset.api.Rendition getBestFitRendition(int width, com.adobe.granite.asset.api.Asset asset, String renditionPrefix) {
        List<com.adobe.granite.asset.api.Rendition> renditions = Lists.newArrayList(asset.listRenditions());
        return getBestFitRendition(width, renditions, renditionPrefix);

    }

    /***
     * allow picking of best rendition by width with optional prefix.
     * @param width min width
     * @param renditions list of renditions
     * @param renditionPrefix rendition prefix
     * @return matching rendition
     */
    public static com.adobe.granite.asset.api.Rendition getBestFitRendition(int width, List<com.adobe.granite.asset.api.Rendition> renditions, String renditionPrefix) {
        com.adobe.granite.asset.api.Rendition bestFitRendition = null;
        //try custom prefix directly
        if (renditionPrefix != null) {
            bestFitRendition = getRenditionByPrefix(renditions.iterator(), renditionPrefix + width);
        }
        //try default prefixes directly
        if (bestFitRendition == null) {

            bestFitRendition = getRenditionByPrefix(renditions.iterator(), DEFAULT_ASSET_RENDITION_PREFIX1 + width);
            if (bestFitRendition == null) {
                bestFitRendition = getRenditionByPrefix(renditions.iterator(), DEFAULT_ASSET_RENDITION_PREFIX2 + width);
            }
        }
        if (bestFitRendition != null) {
            return bestFitRendition;
        }
        //if not found directly, find first rendition bigger that what we need
        WidthBasedRenditionComparator comp = new WidthBasedRenditionComparator();
        Collections.sort(renditions, comp);
//        Collections.reverse(renditions);
        Iterator<com.adobe.granite.asset.api.Rendition> itr = renditions.iterator();
        com.adobe.granite.asset.api.Rendition bestFit = null;
        com.adobe.granite.asset.api.Rendition original = null;
        while (itr.hasNext()) {
            com.adobe.granite.asset.api.Rendition rend = itr.next();
            if (canRenderOnWeb(rend.getMimeType())) {
                int w = getWidth(rend);

                if (w >= width) {
                    bestFit = rend;
                    if (renditionPrefix != null) {
                        //if matches width and type continue
                        if (rend.getName().startsWith(renditionPrefix)) {

                            break;
                        }
                    } else {
                        if (rend.getName().startsWith(DEFAULT_ASSET_RENDITION_PREFIX1) || rend.getName().startsWith(DEFAULT_ASSET_RENDITION_PREFIX2)) {

                            break;
                        }
                    }
                }
            }
        }

        //find first rendition that can be rendered
        if (bestFit == null) {
            itr = renditions.iterator();
            while (itr.hasNext()) {
                com.adobe.granite.asset.api.Rendition rend = itr.next();
                if (canRenderOnWeb(rend.getMimeType())) {
                    bestFit = rend;
                    break;
                }
            }
        }

        return bestFit;
    }

    public static boolean canRenderOnWeb(String mimeType) {
        return (mimeType != null) && ((mimeType.toLowerCase().contains("jpeg")) || (mimeType.toLowerCase().contains("jpg")) || (mimeType.toLowerCase().contains("gif")) || (mimeType.toLowerCase().contains("png")));
    }

    public static com.adobe.granite.asset.api.Rendition getRenditionByPrefix(Iterator<com.adobe.granite.asset.api.Rendition> renditions, String prefix) {
        return getRenditionByPrefix(renditions, prefix, false);
    }

    public static com.adobe.granite.asset.api.Rendition getRenditionByPrefix(Iterator<com.adobe.granite.asset.api.Rendition> renditions, String prefix, boolean returnOriginal) {
        com.adobe.granite.asset.api.Rendition original = null;
        while (renditions.hasNext()) {
            com.adobe.granite.asset.api.Rendition rendition = renditions.next();
            if ("original".equals(rendition.getName())) {
                original = rendition;
            }
            if (rendition.getName().startsWith(prefix)) {
                return rendition;
            }
        }
        if (returnOriginal) {
            return original;
        }
        return null;
    }

    /***
     * get basic asset info and return default if asset not found.
     * @param resourceResolver resource resolver
     * @param assetPath asset path to use
     * @param infoPrefix attribute prefix to use
     * @param defaultPath default path to use
     * @return map of attributes
     */
    public static Map<String, String> getAssetInfo(ResourceResolver resourceResolver, String assetPath, String infoPrefix, String defaultPath) {

        Map<String, String> assetInfo = getAssetInfo(resourceResolver, assetPath, infoPrefix);

        if (assetInfo.size() == 0) {
            assetInfo.put(infoPrefix, defaultPath);
        }

        return assetInfo;
    }

    /***
     * get basic asset info.
     * @param resourceResolver resource resolver
     * @param assetPath asset path to use
     * @param infoPrefix attribute prefix to use
     * @return map of attributes
     */
    public static Map<String, String> getAssetInfo(ResourceResolver resourceResolver, String assetPath, String infoPrefix) {
        if (isEmpty(infoPrefix)) {
            infoPrefix = "image";
        }
        Map<String, String> assetInfo = new HashMap<>();

        if (isNotEmpty(assetPath)) {

            assetInfo.put(infoPrefix, assetPath);

            Resource pageImageResource = resourceResolver.resolve(assetPath);
            if (!ResourceUtil.isNonExistingResource(pageImageResource)) {
                Asset pageImageAsset = pageImageResource.adaptTo(Asset.class);
                if (pageImageAsset != null) {
                    assetInfo.put(infoPrefix + "LicenseInfo", getAssetCopyrightInfo(pageImageAsset, DAM_LICENSE_FORMAT));

                    assetInfo.put(infoPrefix + "Title", getAssetPropertyValueWithDefault(pageImageAsset, DamConstants.DC_TITLE, ""));
                    assetInfo.put(infoPrefix + "Description", getAssetPropertyValueWithDefault(pageImageAsset, DamConstants.DC_DESCRIPTION, ""));

                    try {
                        Node pageImageResourceNode = pageImageResource.adaptTo(Node.class);
                        if (pageImageResourceNode != null && pageImageResourceNode.hasProperty(JcrConstants.JCR_UUID)) {
                            assetInfo.put(infoPrefix + "Id", pageImageResourceNode.getProperty(JcrConstants.JCR_UUID).getString());
                        }
                    } catch (Exception ex) {
                        LOGGER.error("getAssetInfo: could not get assetID {}", ex);
                    }

                }
            }
        }
        return assetInfo;
    }

    /***
     * get asset video renditions if any.
     * @param asset to use
     * @return map of renditions
     */
    public static TreeMap<String, String> getAssetRenditionsVideo(com.adobe.granite.asset.api.Asset asset) {
        TreeMap<String, String> renditionsSet = new TreeMap<>();

        if (asset != null) {
            Iterator renditions = asset.listRenditions();

            while (renditions.hasNext()) {
                com.adobe.granite.asset.api.Rendition rendition = (com.adobe.granite.asset.api.Rendition) renditions.next();
                if (rendition != null && (rendition.getName().contains(".video.") || rendition.getName().equals("original"))) {
                    renditionsSet.put(rendition.getPath(), rendition.getMimeType());
                }
            }

        }

        return renditionsSet;
    }

    /***
     * get background video settings from shared background video tab.
     * @param wcmUsePojoModel component model model
     * @return returns map of attributes
     */
    public static ComponentProperties getBackgroundVideoRenditions(WCMUsePojo wcmUsePojoModel) {

        try {

            return getBackgroundVideoRenditions(getContextObjects(wcmUsePojoModel));

        } catch (Exception ex) {
            LOGGER.error("getBackgroundImageRenditions(WCMUsePojo) could not read required objects={}, error={}",wcmUsePojoModel,ex);
        }

        return getNewComponentProperties(wcmUsePojoModel);
    }

    /***
     * get background video settings from shared background video tab.
     * @param pageContext page context
     * @return returns map of attributes
     */

    public static ComponentProperties getBackgroundVideoRenditions(PageContext pageContext) {
        try {

            return getBackgroundVideoRenditions(getContextObjects(pageContext));

        } catch (Exception ex) {
            LOGGER.error("getBackgroundImageRenditions(PageContext) could not read required objects", ex);
        }

        return getNewComponentProperties(pageContext);
    }

    /***
     * get background video settings from shared background video tab.
     * @param pageContext page context
     * @return map of attributes
     */
    public static ComponentProperties getBackgroundVideoRenditions(Map<String, Object> pageContext) {
        Resource resource = (org.apache.sling.api.resource.Resource) pageContext.get("resource");

        SlingScriptHelper sling = (SlingScriptHelper) pageContext.get("sling");
        ContentAccess contentAccess = sling.getService(ContentAccess.class);
        if (contentAccess != null) {
            try (ResourceResolver resourceResolver = contentAccess.getAdminResourceResolver()) {

                Resource backgroundResource = resourceResolver.getResource(resource, DEFAULT_BACKGROUND_VIDEO_NODE_NAME);

                if (backgroundResource != null) {

                    ComponentProperties imageProperties = getComponentProperties(pageContext, backgroundResource, false, DEFAULT_FIELDS_BACKGROUNDVIDEO_OPTIONS);

                    String fileReference = imageProperties.get(IMAGE_FILEREFERENCE, "");

                    Resource assetR = resourceResolver.resolve(fileReference);
                    if (!ResourceUtil.isNonExistingResource(assetR)) {

                        com.adobe.granite.asset.api.AssetManager assetManager = resourceResolver.adaptTo(com.adobe.granite.asset.api.AssetManager.class);
                        if (assetManager != null) {
                            com.adobe.granite.asset.api.Asset videoAsset = assetManager.getAsset(fileReference);

                            if (videoAsset != null) {

                                imageProperties.putAll(getAssetInfo(resourceResolver, fileReference, FIELD_VIDEO_BACKGROUND));

                                imageProperties.put(FIELD_RENDITIONS_VIDEO, getAssetRenditionsVideo(videoAsset));
                            }
                        } else {
                            LOGGER.error("getBackgroundVideoRenditions: could not get AssetManager object");
                        }
                    }

                    return imageProperties;
                }
            } catch (Exception ex) {
                LOGGER.error(Throwables.getStackTraceAsString(ex));
            }
        } else {
            LOGGER.error("getBackgroundVideoRenditions: could not get ContentAccess service.");
        }

        return null;
    }


    /***
     * get background image settings from shared background image tab.
     * @param wcmUsePojoModel component model model
     * @return returns map of attributes
     */
    public static ComponentProperties getBackgroundImageRenditions(WCMUsePojo wcmUsePojoModel) {

        try {

            return getBackgroundImageRenditions(getContextObjects(wcmUsePojoModel));

        } catch (Exception ex) {
            LOGGER.error("getBackgroundImageRenditions(WCMUsePojo) could not read required objects={}, error={}",wcmUsePojoModel,ex);
        }

        return getNewComponentProperties(wcmUsePojoModel);
    }

    /***
     * get background image settings from shared background image tab.
     * @param pageContext page context
     * @return returns map of attributes
     */

    public static ComponentProperties getBackgroundImageRenditions(PageContext pageContext) {
        try {

            return getBackgroundImageRenditions(getContextObjects(pageContext));

        } catch (Exception ex) {
            LOGGER.error("getBackgroundImageRenditions(PageContext) could not read required objects", ex);
        }

        return getNewComponentProperties(pageContext);
    }

    /***
     * get background image settings from shared background image tab.
     * @param pageContext page context
     * @return returns map of attributes
     */
    public static ComponentProperties getBackgroundImageRenditions(Map<String, Object> pageContext) {
        Resource resource = (org.apache.sling.api.resource.Resource) pageContext.get("resource");

        Resource backgroundResource = resource.getChild(DEFAULT_BACKGROUND_IMAGE_NODE_NAME);

        return getResourceImageRenditions(pageContext, backgroundResource, COMPONENT_BACKGROUND_ASSETS, FIELD_IMAGE_BACKGROUND);
    }



    /***
     * get resource image settings.
     * @param wcmUsePojoModel component model model
     * @param resource resource to use
     * @param attributeName attribute name to use
     * @param returnLastRenditionName  rendition to stop search on
     * @return returns map of attributes
     */
    public static ComponentProperties getResourceImageRenditions(WCMUsePojo wcmUsePojoModel, Resource resource, String attributeName, String returnLastRenditionName) {

        try {

            return getResourceImageRenditions(getContextObjects(wcmUsePojoModel), resource, attributeName, returnLastRenditionName);

        } catch (Exception ex) {
            LOGGER.error("getBackgroundImageRenditions(WCMUsePojo) could not read required objects={}, error={}",wcmUsePojoModel,ex);
        }

        return getNewComponentProperties(wcmUsePojoModel);
    }


    /***
     * get resource image settings.
     * @param pageContext page context
     * @param resource resource to use
     * @param attributeName attribute name to use
     * @param returnLastRenditionName last rendition to return if not found
     * @return returns map of attributes
     */
    public static ComponentProperties getResourceImageRenditions(PageContext pageContext, Resource resource, String attributeName, String returnLastRenditionName) {

        try {

            return getResourceImageRenditions(getContextObjects(pageContext), resource, attributeName, returnLastRenditionName);

        } catch (Exception ex) {
            LOGGER.error("getBackgroundImageRenditions(pageContext) could not read required objects", ex);
        }

        return getNewComponentProperties(pageContext);
    }

    /***
     * get resource image settings.
     * @param pageContext page context map
     * @param imageResource resource to use
     * @param returnRenditionsListName  name of the attribute to create for rendition list
     * @param returnLastRenditionName last rendition to return if not found
     * @return returns map of attributes
     */
    @SuppressWarnings("Duplicates")
    public static ComponentProperties getResourceImageRenditions(Map<String, Object> pageContext, Resource imageResource, String returnRenditionsListName, String returnLastRenditionName) {

        SlingScriptHelper sling = (SlingScriptHelper) pageContext.get("sling");
        ContentAccess contentAccess = sling.getService(ContentAccess.class);
        if (contentAccess != null) {
            try (ResourceResolver resourceResolver = contentAccess.getAdminResourceResolver()) {

                if (isEmpty(returnRenditionsListName)) {
                    returnRenditionsListName = "images";
                }

                if (imageResource != null) {

                    ComponentProperties imageProperties = getComponentProperties(pageContext, imageResource, false, DEFAULT_FIELDS_IMAGE_OPTIONS);

                    String fileReference = imageProperties.get(IMAGE_FILEREFERENCE, "");

                    Resource assetR = resourceResolver.resolve(fileReference);
                    if (!ResourceUtil.isNonExistingResource(assetR)) {

                        com.adobe.granite.asset.api.AssetManager assetManager = resourceResolver.adaptTo(com.adobe.granite.asset.api.AssetManager.class);
                        if (assetManager != null) {
                            com.adobe.granite.asset.api.Asset asset = assetManager.getAsset(fileReference);

                            if (asset != null) {

                                try {

                                    String imageOption = imageProperties.get(FIELD_IMAGE_OPTION, "");

                                    Map<String, String> responsiveImageSet = new LinkedHashMap<>();
									if (fileReference.endsWith(".svg") || fileReference.endsWith(".gif")) {
										imageProperties.put(returnLastRenditionName, fileReference);
										imageProperties.put(FIELD_IMAGE_OPTION, "simple");
									} else {
										responsiveImageSet = getImageSetForImageOptions(imageOption,asset, imageProperties, imageResource, resourceResolver,sling);
									}

                                    imageProperties.put(returnRenditionsListName, responsiveImageSet);

                                    //pick last one from collection
                                    if (responsiveImageSet.values().size() > 0) {
                                        imageProperties.put(returnLastRenditionName, responsiveImageSet.values().toArray()[responsiveImageSet.values().size() - 1]);
                                    }

                                } catch (Exception ex) {
                                    LOGGER.error("failed to create Width and Image Mapping: {}", ex);
                                }

                            }
                        } else {
                            LOGGER.error("getResourceImageRenditions: could not get AssetManager object");
                        }
                    }

                    return imageProperties;
                }

            } catch (Exception ex) {
                LOGGER.error(Throwables.getStackTraceAsString(ex));
            }
        } else {
            LOGGER.error("getResourceImageRenditions: could not get ContentAccess service.");
        }
        return null;
    }

    /**
     * function to filter out the design dialog values which are not matching rendition profile.
     *
     * @param asset                 asset to use
     * @param renditionImageMapping array of minWidth=mediaQuery
     * @param renditionPrefix       prefix to use
     * @return profile with substituted paths
     */
    public static Map<String, String> getBestFitMediaQueryRenditionSet(com.adobe.granite.asset.api.Asset asset, String[] renditionImageMapping, String renditionPrefix) {

        Map<String, String> profileRendtiions = new LinkedHashMap<>();

        if (isEmpty(renditionPrefix))

            if (asset != null && renditionImageMapping != null) {

                for (String entry : renditionImageMapping) {
                    String[] entryArray = StringUtils.split(entry, "=");
                    if (entryArray == null || entryArray.length != 2) {
                        LOGGER.error("getBestFitMediaQueryRenditionSet [{}] is invalid", entry);
                        continue;
                    }
                    String minWidth = entryArray[0];
                    if (isEmpty(minWidth) || (!NumberUtils.isDigits(minWidth))) {
                        LOGGER.error("getBestFitMediaQueryRenditionSet [{}] is invalid, incorrect width [{}]", entry, minWidth);
                        continue;
                    }

                    String mediaQuery = entryArray[1];

                    com.adobe.granite.asset.api.Rendition rendition = getBestFitRendition(tryParseInt(minWidth, 0), asset, defaultIfEmpty(renditionPrefix, null));

                    String renditionPath = rendition.getPath();

                    //don't return paths to original rendition return path to asset instead
                    if (renditionPath.endsWith("/original")) {
                        String assetPath = renditionPath.substring(0, renditionPath.indexOf(JCR_CONTENT) - 1);
                        ResourceResolver resourceResolver = asset.getResourceResolver();
                        if (resourceResolver != null) {
                            Resource assetPathResource = resourceResolver.resolve(assetPath);
                            if (!ResourceUtil.isNonExistingResource(assetPathResource)) {
                                renditionPath = assetPath;
                            }
                        }
                    }

                    profileRendtiions.put(mediaQuery, renditionPath);
                }

            }
        return profileRendtiions;

    }

    /**
     * function to filter out the design dialog values which are not matching adaptive profile
     *
     * @param adaptiveImageMapping image mapping config
     * @param resolver resolver instance
     * @param componentPath                path to component doing the render
     * @param fileReference                path to asset to use for render
     * @param outputFormat                 specify which output format to use
     * @param useFileReferencePathAsRender create paths using fileReference instead of using Component Path
     * @param sling sling instance
     * @return map of urls
     */
    public static Map<String, String> getAdaptiveImageSet(String[] adaptiveImageMapping, ResourceResolver resolver, String componentPath, String fileReference, String outputFormat, Boolean useFileReferencePathAsRender, org.apache.sling.api.scripting.SlingScriptHelper sling) {

        Map<String, String> responsiveImageSet = new LinkedHashMap<>();

        URI fileReferenceURI = URI.create(fileReference);
        if (isBlank(outputFormat)) {
            String extension = fileReferenceURI.getPath();
            outputFormat = extension.substring(extension.lastIndexOf("."));
        }
        String suffix = defaultString(fileReferenceURI.getQuery(), "");

        String renderPath = componentPath;

        if (useFileReferencePathAsRender) {
            renderPath = fileReference;
        }

        int[] allowedSizes = getAdaptiveImageSupportedWidths(sling);

        for (String entry : adaptiveImageMapping) {

            String[] entryArray = StringUtils.split(entry, "="); //320.medium=(min-width: 1px) and (max-width: 425px)
            if (entryArray == null || entryArray.length != 2) {
                LOGGER.error("getAdaptiveImageSet [{}] is invalid",entry);
                continue;
            }
            String adaptiveProfile = entryArray[0];
            if (isEmpty(adaptiveProfile) && (!adaptiveProfile.contains("."))) {
                LOGGER.error("getAdaptiveImageSet [{}] is invalid, incorrect profile format [{}] expecting {width}.{quality}.{format}", entry, adaptiveProfile);
                continue;
            }

            String mediaQuery = entryArray[1];

            String[] adaptiveProfileArray = StringUtils.split(adaptiveProfile, ".");

            Integer profileWidth = tryParseInt(adaptiveProfileArray[0], 0);

            String profileOutputFormat = outputFormat;
            if (adaptiveProfileArray.length == 3) {
                profileOutputFormat = "";
            }

            if (adaptiveProfile.equals("full") || ArrayUtils.contains(allowedSizes, profileWidth)) {
                responsiveImageSet.put(mediaQuery,
                        MessageFormat.format("{0}.img.{1}{2}{3}",
                                renderPath,
                                adaptiveProfile,
                                profileOutputFormat,
                                suffix
                        )
                );
            } else {
                LOGGER.error("getAdaptiveImageSet rendition selected size is not allowed [{}], [{}]", profileWidth, entry);
                continue;
            }

        }

        return responsiveImageSet;
    }

    /**
     * return list of configured widths in com.day.cq.wcm.foundation.impl.AdaptiveImageComponentServlet
     *
     * @param sling sling instance
     * @return list of ints allowed
     */
    @SuppressWarnings("unchecked")
    public static int[] getAdaptiveImageSupportedWidths(org.apache.sling.api.scripting.SlingScriptHelper sling) {

        int[] defaultWidths = {480, 640, 720, 800, 960, 1024, 1280, 1440, 1920, 2048};
        int[] supportedWidths = new int[0];

        try {
            org.osgi.service.cm.ConfigurationAdmin configAdmin = sling.getService(org.osgi.service.cm.ConfigurationAdmin.class);

            if (configAdmin != null) {

                org.osgi.service.cm.Configuration config = configAdmin.getConfiguration(OSGI_CONFIG_MEDIA_IMAGE);

                Object obj = org.apache.sling.commons.osgi.PropertiesUtil.toStringArray(config.getProperties().get("adapt.supported.widths"));

                if (obj instanceof String[]) {

                    String[] strings = (String[]) obj;
                    supportedWidths = new int[strings.length];
                    for (int i = 0; i < strings.length; i++) {
                        supportedWidths[i] = Integer.parseInt(strings[i]);
                    }
                }

                if (obj instanceof long[]) {

                    long[] longs = (long[]) obj;
                    supportedWidths = new int[longs.length];
                    for (int i = 0; i < longs.length; i++) {
                        supportedWidths[i] = (int) longs[i];
                    }
                }
            } else {
                LOGGER.error("getAdaptiveImageSupportedWidths: could not get ConfigurationAdmin service");
            }

        } catch (Exception ex) {
            LOGGER.warn("using default adapt.supported.widths=[{}] as config is missing OSGI configuration: {}", defaultWidths, OSGI_CONFIG_MEDIA_IMAGE);
            return defaultWidths;
        }
        return supportedWidths;

    }

	/**
	 * get asset image options
	 * @param imageOption image option
	 * @param asset asset object
	 * @param componentProperties component properties for config
	 * @param assetResource asset resource
	 * @param resourceResolver resolver instance
	 * @param sling sling instance
	 * @return list of images
	 */
	public static Map<String, String> getImageSetForImageOptions(String imageOption, com.adobe.granite.asset.api.Asset asset, ComponentProperties componentProperties,  Resource assetResource, ResourceResolver resourceResolver, SlingScriptHelper sling) {
		Map<String, String> responsiveImageSet = new LinkedHashMap<>();

		if (isNotEmpty(imageOption) && asset != null && !ResourceUtil.isNonExistingResource(assetResource)) {
			try {

				String assetPath = assetResource.getPath();
				switch (imageOption) {
					case IMAGE_OPTION_GENERATED:
						String imageHref = "";
						Long lastModified = getLastModified(assetResource);
						imageHref = MessageFormat.format(DEFAULT_IMAGE_GENERATED_FORMAT, assetPath, lastModified.toString());

						responsiveImageSet.put("", imageHref);
						break;
					case IMAGE_OPTION_RENDITION:
						int targetWidth = componentProperties.get(ImageResource.PN_WIDTH, 0);
						com.adobe.granite.asset.api.Rendition bestRendition = getBestFitRendition(targetWidth, asset);
						if (bestRendition != null) {

							responsiveImageSet.put("", bestRendition.getPath());
						}
						break;
					case IMAGE_OPTION_ADAPTIVE:
						String[] adaptiveImageMapping = componentProperties.get(FIELD_ADAPTIVE_MAP, new String[]{});

						responsiveImageSet = getAdaptiveImageSet(adaptiveImageMapping, resourceResolver, assetPath, assetPath, null, false, sling);

						break;
					case IMAGE_OPTION_MEDIAQUERYRENDITION:
						//map of media queries to renditions
						String[] mediaQueryList = componentProperties.get(FIELD_MEDIAQUERYRENDITION_KEY, new String[]{});
						String[] renditionList = componentProperties.get(FIELD_MEDIAQUERYRENDITION_VALUE, new String[]{});

						if (mediaQueryList.length != renditionList.length) {
							LOGGER.error(MessageFormat.format("fields {0} and {1} need to be equal length", FIELD_MEDIAQUERYRENDITION_KEY, FIELD_MEDIAQUERYRENDITION_VALUE));
							break;
						}

						for (int i = 0; i < mediaQueryList.length; i++) {
							responsiveImageSet.put(mediaQueryList[i], assetPath + FIELD_ASSET_RENDITION_PATH_SUFFIX + renditionList[i]);
						}

						break;
					default: //IMAGE_OPTION_RESPONSIVE
						String[] renditionImageMapping = componentProperties.get(FIELD_RESPONSIVE_MAP, new String[]{});

						//get rendition profile prefix selected
						String renditionPrefix = componentProperties.get(FIELD_RENDITION_PREFIX, "");

						//get best fit renditions set
						responsiveImageSet = getBestFitMediaQueryRenditionSet(asset, renditionImageMapping, renditionPrefix);
				}

			} catch (Exception ex) {
				LOGGER.error("getAssetInfo: could not collect renditions asset={}", asset);
			}

		}

		return responsiveImageSet;
	}

	/**
	 * get asset duration from metadata/xmpDM:duration value map
	 * @param assetMetadataDurationValueMap xmpDM:duration value map
	 * @return duration
	 */
	public static Duration getAssetDuration(ValueMap assetMetadataDurationValueMap)  {
		if (assetMetadataDurationValueMap != null) {
			try {

				String durationScale = assetMetadataDurationValueMap.get("xmpDM:scale", "");
				String durationValue = assetMetadataDurationValueMap.get("xmpDM:value", "");

				Double scale = Double.parseDouble("1");
				Double value = Double.parseDouble(durationValue);
				if (durationScale.contains("/")) {
					String[] scaleList = durationScale.split("/");
					String stringOne = scaleList[0];
					String stringDivided = scaleList[1];

					Double doubleOne = Double.parseDouble(stringOne);
					Double doubleDivided = Double.parseDouble(stringDivided);

					scale = doubleOne / doubleDivided;
				}

				Double duration = scale * value;

				return Duration.ofSeconds(Math.round(duration));

			} catch (Exception ex) {
				LOGGER.error("getAssetDuration: could not extract duration asset metadata={}",assetMetadataDurationValueMap);
			}

		}

		return Duration.ZERO;
	}



/* TESTS
    out.println("responsiveRenditionOverride : "+widthRenditionProfileMapping);
    out.println("responsiveRenditionOverride1 : "+responsiveImageSet);
    out.println("filterRenditionImageSet(asset, widthRenditionProfileMapping, RENDITION_PROFILE_CUSTOM) : \n"+filterRenditionImageSet(asset, widthRenditionProfileMapping, RENDITION_PROFILE_CUSTOM));
    out.println("filterRenditionImageSet(asset, widthRenditionProfileMapping, \"cq5dam.custom.\") : \n"+filterRenditionImageSet(asset, widthRenditionProfileMapping, "cq5dam.custom."));
    out.println("getBestFitRendition(48, asset) : \n" + getBestFitRendition(48, asset) );
    out.println("getBestFitRendition(48, asset, RENDITION_PROFILE_CUSTOM) : \n" + getBestFitRendition(48, asset, RENDITION_PROFILE_CUSTOM) );
    out.println("getBestFitRendition(48, asset, \"cq5dam.custom.\") : \n" + getBestFitRendition(48, asset, "cq5dam.custom.") );
    out.println("getBestFitRendition(319, asset, \"cq5dam.custom.\") : \n" + getBestFitRendition(319, asset, "cq5dam.custom.") );
    out.println("getBestFitRendition(1900, asset, \"cq5dam.custom.\") : \n" + getBestFitRendition(1900, asset, "cq5dam.custom.") );
    out.println("getBestFitRendition(1280, asset, RENDITION_PROFILE_CUSTOM) : \n" + getBestFitRendition(1280, asset, RENDITION_PROFILE_CUSTOM) );
    out.println("getBestFitRendition(47, asset, RENDITION_PROFILE_CUSTOM) : \n" + getBestFitRendition(47, asset, RENDITION_PROFILE_CUSTOM) );
    out.println("getBestFitRendition(49, asset, \"cq5dam.custom.\") : \n" + getBestFitRendition(49, asset, "cq5dam.custom.") );
    out.println("getBestFitRendition(47, asset) : \n" + getBestFitRendition(47, asset) );
    out.println("getBestFitRendition(48, asset) : \n" + getBestFitRendition(48, asset) );
    out.println("getBestFitRendition(49, asset) : \n" + getBestFitRendition(49, asset) );
    out.println("getBestFitRendition(139, asset) : \n" + getBestFitRendition(139, asset) );
    out.println("getBestFitRendition(140, asset) : \n" + getBestFitRendition(140, asset) );
    out.println("getBestFitRendition(141, asset) : \n" + getBestFitRendition(141, asset) );
    out.println("getBestFitRendition(318, asset) : \n" + getBestFitRendition(318, asset) );
    out.println("getBestFitRendition(319, asset) : \n" + getBestFitRendition(319, asset) );
    out.println("getBestFitRendition(320, asset) : \n" + getBestFitRendition(320, asset) );
    out.println("getBestFitRendition(1279, asset) : \n" + getBestFitRendition(1279, asset) );
    out.println("getBestFitRendition(1280, asset) : \n" + getBestFitRendition(1280, asset) );
    out.println("getBestFitRendition(1281, asset) : \n" + getBestFitRendition(1281, asset) );
*/

}
