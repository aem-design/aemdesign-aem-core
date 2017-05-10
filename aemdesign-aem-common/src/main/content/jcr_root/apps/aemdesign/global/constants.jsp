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
     *
     *
     */

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
    private String defaultLayout = "1;col-md-,12";


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
            "<img src=\"/etc/designs/admin/0.gif\" class=\"cq-chart-placeholder\">";

    public final static String DEFAULT_CLASSIC_PLACEHOLDER_TAGCLOUD =
            "<img src=\"/libs/cq/ui/resources/0.gif\" class=\"cq-tagcloud-placeholder\" alt=\"\">";

    public final static String DEFAULT_CLASSIC_PLACEHOLDER_LINK =
            "<img src=\"/libs/cq/ui/resources/0.gif\" class=\"cq-reference-placeholder cq-dd-paragraph\" alt=\"\">";

    public final static String DEFAULT_CLASSIC_PLACEHOLDER_TABLE =
            "<img src=\"/libs/cq/ui/resources/0.gif\" class=\"cq-table-placeholder\" alt=\"\">";

    public final static String DEFAULT_CLASSIC_PLACEHOLDER_LIST =
            "<img src=\"/libs/cq/ui/resources/0.gif\" class=\"cq-list-placeholder\" alt=\"\">";

    public final static String DEFAULT_CLASSIC_PLACEHOLDER_IMAGE =
            "<img title=\"\" alt=\"\" class=\"cq-dd-image cq-image-placeholder\" src=\"/etc/designs/admin/0.gif\">";

    public final static String DEFAULT_CLASSIC_PLACEHOLDER_CAROUSEL =
            "<img src=\"/libs/cq/ui/resources/0.gif\" class=\"cq-carousel-placeholder\" alt=\"\">";

    public final static String DEFAULT_CLASSIC_PLACEHOLDER_TEXT =
            "<span class=\"cq-text-placeholder-ipe\">&para;</span>";



    public static final String SITE_INCLUDE = "siteinclude";
    public static final String SITE_INCLUDE_PATHS = "includePaths";

    public static final String IMAGE_FILEREFERENCE = "fileReference";
    public static final String COMPONENT_LIST_PROPERTIES = "component.list.properties";

    public static final String FIELD_THUMBNAIL_WIDTH = "thumbnailWidth";
    public static final String FIELD_THUMBNAIL_HEIGHT = "thumbnailHeight";


    public static final String INHERITED_RESOURCE = "inheritedResource";

    public static final String DEFAULT_VALUE_STRING_NOT_FOUND = "";

    public static final String ATTRIBUTE_CSSCLASS = "cssClass";
    public static final String ATTRIBUTE_ID = "attrId";
    public static final String ATTRIBUTE_NUMCOL = "numCols";
    public static final String ATTRIBUTE_CSSCLASS_JUMBOTRON = "cssClassJumbotron";
    public static final String ATTRIBUTE_CSSCLASS_LAYOUT = "cssClassLayout";
    public static final String ATTRIBUTE_CSSCLASS_BACKGROUND = "cssClassBackground";
    public static final String ATTRIBUTE_CSSCLASS_ROW = "cssClassRow";
    public static final String ATTRIBUTE_CSSCLASS_COLUMN = "cssClassColumn";


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
<c:set var="DEFAULT_CLASSIC_PLACEHOLDER_LIST" value="<%= DEFAULT_CLASSIC_PLACEHOLDER_LIST %>"/>
