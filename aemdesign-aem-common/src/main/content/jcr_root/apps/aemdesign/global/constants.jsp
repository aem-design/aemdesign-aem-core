<%@ page import="com.day.cq.commons.jcr.JcrUtil" %>
<%@ page import="com.day.cq.search.eval.JcrBoolPropertyPredicateEvaluator" %>
<%@ page import="org.apache.sling.api.SlingHttpServletRequest" %>
<%@page session="false"%>
<%!
    /**
     * OOTB Paragraph Helpers Constants
     *
     * WARNING: DO NOT ADD CONSTANT THAT ARE ALREADY DEFINED HERE
     *
     * http://dev.day.com/docs/en/cq/current/javadoc/constant-values.html
     * https://docs.adobe.com/docs/en/aem/6-0/develop/ref/javadoc/constant-values.html
     * https://docs.adobe.com/docs/en/aem/6-3/develop/ref/javadoc/constant-values.html
     * https://helpx.adobe.com/aem-forms/6-3/javadocs/constant-values.html
     *
     *
     */

    public static final String DEFAULT_EXTENTION = ".html";

    public static final String EXTENSION_JSP = "jsp";

    //Do not update unless you have verified all components work
    public static Boolean REMOVEDECORATION = true; //change this if you want component decoration removed

    //Decide to print Component Badges
    public static Boolean PRINT_COMPONENT_BADGE = true;

    /**
     * name of the layout property
     */
    public static final String COL_CTL_LAYOUT = "layout";

    /**
     * name of the column control type property
     */
    public static final String COL_CTL_TYPE = "controlType";

    /**
     * Request parameter. If set, the context is forced to be printed.
     */
    private static final String COMPONENT_FORCE_CONTEXT_PARAMETER = "forceeditcontext";

    /**
     * Name of the cookie containing the authoring UI mode
     */
    private static final String WCM_AUTHORING_MODE_COOKIE = "cq-authoring-mode";
    private static final String WCM_AUTHORING_MODE_COOKIE_VALUE_CLASSIC = "CLASSIC";
    private static final String WCM_AUTHORING_MODE_COOKIE_VALUE_TOUCH = "TOUCH";

    /**
     * the css prefix for the column classes
     */
    private static String defaultLayout = "1;col-md-,12";


    /**
     * component field defaults
     */


    /**
     * Default component placeholder to be used in places of empty text components
     */

    public final static String DEFAULT_CLASSIC_PLACEHOLDER =
            "<span class=\"cq-text-placeholder-ipe\">&para;</span>";

    public final static String DEFAULT_CLASSIC_PLACEHOLDER_VIDEO =
            "<div class=\"cq-dd-video cq-video-placeholder\"></div>";

    public final static String DEFAULT_CLASSIC_PLACEHOLDER_CHART =
            "<img src=\"/apps/settings/wcm/design/aemdesign/blank.png\" class=\"cq-chart-placeholder\">";

    public final static String DEFAULT_CLASSIC_PLACEHOLDER_TAGCLOUD =
            "<img src=\"/apps/settings/wcm/design/aemdesign/blank.png\" class=\"cq-tagcloud-placeholder\" alt=\"\">";

    public final static String DEFAULT_CLASSIC_PLACEHOLDER_LINK =
            "<img src=\"/apps/settings/wcm/design/aemdesign/blank.png\" class=\"cq-reference-placeholder cq-dd-paragraph\" alt=\"\">";

    public final static String DEFAULT_CLASSIC_PLACEHOLDER_TABLE =
            "<img src=\"/apps/settings/wcm/design/aemdesign/blank.png\" class=\"cq-table-placeholder\" alt=\"\">";

    public final static String DEFAULT_CLASSIC_PLACEHOLDER_LIST =
            "<img src=\"/apps/settings/wcm/design/aemdesign/blank.png\" class=\"cq-list-placeholder\" alt=\"\">";

    public final static String DEFAULT_CLASSIC_PLACEHOLDER_IMAGE =
            "<img title=\"\" alt=\"\" class=\"cq-dd-image cq-image-placeholder\" src=\"/apps/settings/wcm/design/aemdesign/blank.png\">";

    public final static String DEFAULT_TOUCH_PLACEHOLDER_IMAGE =
            "<div class=\"cq-placeholder cq-dd-image\" data-emptytext=\"Image\"></div>";

    public final static String DEFAULT_CLASSIC_PLACEHOLDER_CAROUSEL =
            "<img src=\"/apps/settings/wcm/design/aemdesign/blank.png\" class=\"cq-carousel-placeholder\" alt=\"\">";

    public final static String DEFAULT_CLASSIC_PLACEHOLDER_TEXT =
            "<span class=\"cq-text-placeholder-ipe\">&para;</span>";

    private static final String DEFAULT_IMAGE_BLANK = "/apps/settings/wcm/design/aemdesign/blank.png";

    private static final int DEFAULT_THUMB_WIDTH_XSM = 140;
    private static final int DEFAULT_THUMB_WIDTH_SM = 319;
    private static final int DEFAULT_THUMB_WIDTH_MD = 800;
    private static final int DEFAULT_THUMB_WIDTH_LG = 1280;
    private static final int DEFAULT_THUMB_WIDTH_XLG = 1600;
    private static final int DEFAULT_THUMB_WIDTH_XXLG = 1900;

    private static final String DEFAULT_TITLE_TAG_TYPE_BADGE = "h3";
    private static final String DEFAULT_SUMMARY_TRIM_SUFFIX = "...";
    private static final int DEFAULT_SUMMARY_TRIM_LENGTH = 50;


    public static final String SITE_INCLUDE = "siteinclude";
    public static final String SITE_INCLUDE_PATHS = "includePaths";

    public static final String FIELD_SOURCE_ATTRIBUTE = "src";
    public static final String FIELD_PROVIDER_URL = "mediaProviderUrl";

    public static final String IMAGE_FILEREFERENCE = "fileReference";
    public static final String COMPONENT_LIST_PROPERTIES = "component.list.properties";

    public static final String FIELD_THUMBNAIL_WIDTH = "thumbnailWidth";
    public static final String FIELD_THUMBNAIL_HEIGHT = "thumbnailHeight";
    public static final String FIELD_THUMBNAIL_TYPE = "thumbnailType";

    public static final String FIELD_PAGE_IMAGE = "pageImage";
    public static final String FIELD_PAGE_IMAGE_THUMBNAIL = "pageImageThumbnail";
    public static final String FIELD_PAGE_IMAGE_ID = "pageImageId";
    public static final String FIELD_PAGE_IMAGE_LICENSE_INFO = "pageImageLicenseInfo";
    public static final String FIELD_PAGE_SECONDARY_IMAGE = "pageSecondaryImage";
    public static final String FIELD_PAGE_SECONDARY_IMAGE_THUMBNAIL = "pageSecondaryImageThumbnail";
    public static final String FIELD_PAGE_BACKGROUND_IMAGE = "pageBackgroundImage";
    public static final String FIELD_PAGE_THUMBNAIL_IMAGE = "pageThumbnailImage";
    public static final String FIELD_PAGE_THUMBNAIL_IMAGE_THUMBNAIL = "pageThumbnailImageThumbnail";
    public static final String FIELD_PAGE_THUMBNAIL = "pageThumbnail";
    public static final String FIELD_PAGE_URL = "pageUrl";
    public static final String FIELD_PAGE_TITLE_NAV = "pageNavTitle";
    public static final String FIELD_PAGE_TITLE_SUBTITLE = "subtitle";
    public static final String FIELD_VIDEO_BACKGROUND = "backgroundVideo";

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

    public static final String DEFAULT_VALUE_STRING_NOT_FOUND = "";

    public static final String ATTRIBUTE_ID = "attrId";

    private final static String IMAGE_OPTION_GENERATED = "generated";
    private final static String IMAGE_OPTION_RENDITION = "rendition";
    private final static String IMAGE_OPTION_RESPONSIVE = "responsive";
    private final static String IMAGE_OPTION_ADAPTIVE = "adaptive";


    private final static String BADGE_REQUEST_ATTRIBUTES = "badgeRequestAttributes";
    private final static String COMPONENT_PROPERTIES = "componentProperties";

    /**
     * CQ
     */

    /**
     * return if the current request has a Force Context Query String parameter specified
     * @param slingRequest
     * @return
     */
    public static Boolean isForceEditContext(SlingHttpServletRequest slingRequest) {
        if (slingRequest.getRequestParameter(COMPONENT_FORCE_CONTEXT_PARAMETER) != null) {
            return Boolean.parseBoolean(slingRequest.getRequestParameter(COMPONENT_FORCE_CONTEXT_PARAMETER).getString());
        }
        return false;
    }

    /**
     * return current UI Mode, Classic or Touch
     * @param slingRequest
     * @return
     */
    public static String UIMode(SlingHttpServletRequest slingRequest) {
        if (slingRequest.getCookie(WCM_AUTHORING_MODE_COOKIE) != null) {
            return slingRequest.getCookie(WCM_AUTHORING_MODE_COOKIE).getValue();
        }
        return "";

    }
%>

<c:set var="DEFAULT_CLASSIC_PLACEHOLDER_LINK" value="<%= DEFAULT_CLASSIC_PLACEHOLDER_LINK %>"/>
<c:set var="DEFAULT_CLASSIC_PLACEHOLDER_LIST" value="<%= DEFAULT_CLASSIC_PLACEHOLDER_LIST %>"/>
<c:set var="DEFAULT_CLASSIC_PLACEHOLDER_TEXT" value="<%= DEFAULT_CLASSIC_PLACEHOLDER_TEXT %>"/>
<c:set var="DEFAULT_CLASSIC_PLACEHOLDER_IMAGE" value="<%= DEFAULT_CLASSIC_PLACEHOLDER_IMAGE %>"/>
<c:set var="DEFAULT_TOUCH_PLACEHOLDER_IMAGE" value="<%= DEFAULT_TOUCH_PLACEHOLDER_IMAGE %>"/>
