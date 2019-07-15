package design.aem.utils.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConstantsUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConstantsUtil.class);

    /**
     * OOTB Paragraph Helpers Constants
     * <p>
     * WARNING: DO NOT ADD CONSTANT THAT ARE ALREADY DEFINED HERE
     * <p>
     * http://dev.day.com/docs/en/cq/current/javadoc/constant-values.html
     * https://docs.adobe.com/docs/en/aem/6-0/develop/ref/javadoc/constant-values.html
     * https://docs.adobe.com/docs/en/aem/6-3/develop/ref/javadoc/constant-values.html
     * https://helpx.adobe.com/aem-forms/6-3/javadocs/constant-values.html
     */

    public static final String DEFAULT_EXTENTION = ".html";
    public static final String DEFAULT_MARK_HASHBANG = "#";
    public static final String DEFAULT_MARK_QUERYSTRING = "?";

    public static final String EXTENSION_JSP = "jsp";

    /**
     * name of the layout property
     */
    public static final String COL_CTL_LAYOUT = "layout";

    /**
     * name of the column control type property
     */
    public static final String COL_CTL_TYPE = "controlType";

    /**
     * Name of the cookie containing the authoring UI mode
     */
    public static final String WCM_AUTHORING_MODE_COOKIE = "cq-authoring-mode";
    public static final String WCM_AUTHORING_MODE_COOKIE_VALUE_TOUCH = "TOUCH";


    /**
     * component field defaults
     */


    /**
     * Default component placeholder to be used in places of empty text components
     */

    public static final String DEFAULT_IMAGE_BLANK = "/etc.clientlibs/settings/wcm/designs/aemdesign/clientlibs-theme/resources/blank.png";

    public static final int DEFAULT_THUMB_WIDTH_XSM = 140;
    public static final int DEFAULT_THUMB_WIDTH_SM = 319;
    public static final int DEFAULT_THUMB_WIDTH_MD = 800;
    public static final int DEFAULT_THUMB_WIDTH_LG = 1280;
    public static final int DEFAULT_THUMB_WIDTH_XLG = 1600;
    public static final int DEFAULT_THUMB_WIDTH_XXLG = 1900;

    public static final String DEFAULT_TITLE_TAG_TYPE_BADGE = "h3";
    public static final String DEFAULT_SUMMARY_TRIM_SUFFIX = "...";
    public static final int DEFAULT_SUMMARY_TRIM_LENGTH = 50;

    public static final String PATH_SEPARATOR = "/";
    public static final String PATH_UNDERSCORE = "_";


    public static final String SITE_INCLUDE = "siteinclude";
    public static final String SITE_INCLUDE_PATHS = "includePaths";

    public static final String FIELD_SOURCE_ATTRIBUTE = "src";
    public static final String FIELD_MEDIA_PROVIDER_URL = "mediaProviderUrl";

    public static final String IMAGE_FILEREFERENCE = "fileReference"; //ASSET_FILEREFERENCE
    public static final String COMPONENT_LIST_PROPERTIES = "component.list.properties";

    public static final String FIELD_THUMBNAIL_WIDTH = "thumbnailWidth";
    public static final String FIELD_THUMBNAIL_HEIGHT = "thumbnailHeight";
    public static final String FIELD_THUMBNAIL_TYPE = "thumbnailType";

    public static final String FIELD_PAGE_IMAGE = "pageImage";
    public static final String FIELD_PAGE_IMAGE_THUMBNAIL = "pageImageThumbnail";
    public static final String FIELD_PAGE_IMAGE_ID = "pageImageId";
    public static final String FIELD_PAGE_IMAGE_LICENSE_INFO = "pageImageLicenseInfo";
    public static final String FIELD_PAGE_IMAGE_SECONDARY_ID = "pageSecondaryImageId";
    public static final String FIELD_PAGE_IMAGE_SECONDARY_LICENSE_INFO = "pageSecondaryImageLicenseInfo";
    public static final String FIELD_PAGE_SECONDARY_IMAGE = "pageSecondaryImage";
    public static final String FIELD_PAGE_SECONDARY_IMAGE_THUMBNAIL = "pageSecondaryImageThumbnail";
    public static final String FIELD_PAGE_BACKGROUND_IMAGE = "pageBackgroundImage";
    public static final String FIELD_PAGE_THUMBNAIL_IMAGE = "pageThumbnailImage";
    public static final String FIELD_PAGE_THUMBNAIL_IMAGE_THUMBNAIL = "pageThumbnailImageThumbnail";
    public static final String FIELD_PAGE_THUMBNAIL = "pageThumbnail";
    public static final String FIELD_PAGE_URL = "pageUrl";
    public static final String FIELD_PAGE_TITLE = "pageTitle";
    public static final String FIELD_PAGE_TITLE_NAV = "pageNavTitle";
    public static final String FIELD_PAGE_TITLE_SUBTITLE = "subtitle";
    public static final String FIELD_VIDEO_BACKGROUND = "backgroundVideo";
    public static final String FIELD_IMAGE_BACKGROUND = "backgroundImage";

    public static final String FIELD_MEDIA_PROVIDER = "mediaProvider";
    public static final String FIELD_MEDIA_TITLE = "mediaTitle";
    public static final String FIELD_MEDIA_ID = "mediaId";
    public static final String FIELD_MEDIA_PARTNER_ID = "mediaPartnerId";
    public static final String FIELD_MEDIA_PLAYER_ID = "mediaPlayerId";

    public static final String FIELD_DATA_MEDIA_PROVIDER = "data-mediaprovider";
    public static final String FIELD_DATA_MEDIA_TITLE = "data-mediatitle";
    public static final String FIELD_DATA_MEDIA_ID = "data-mediaid";
    public static final String FIELD_DATA_MEDIA_PARTNER_ID = "data-mediapartnerid";
    public static final String FIELD_DATA_MEDIA_PLAYER_ID = "data-mediaplayerid";

    public static final String INHERITED_RESOURCE = "inheritedResource";

    public final static String IMAGE_OPTION_GENERATED = "generated";
    public final static String IMAGE_OPTION_RENDITION = "rendition";
    public final static String IMAGE_OPTION_RESPONSIVE = "responsive";
    public final static String IMAGE_OPTION_ADAPTIVE = "adaptive";
    public final static String IMAGE_OPTION_MEDIAQUERYRENDITION = "mediaqueryrendition";

    public final static String FIELD_ASSET_RENDITION_PATH_SUFFIX = "/jcr:content/renditions/";

    public final static String BADGE_REQUEST_ATTRIBUTES = "badgeRequestAttributes";
    public final static String COMPONENT_PROPERTIES = "componentProperties";

}
