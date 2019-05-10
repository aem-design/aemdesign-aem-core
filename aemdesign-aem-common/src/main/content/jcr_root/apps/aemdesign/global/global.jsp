<%--
  Global WCM script.

  This script can be used by any other script in order to get the default
  tag libs, sling objects and CQ objects defined.

  the following page context attributes are initialized via the <cq:defineObjects/>
  tag:

    @param slingRequest SlingHttpServletRequest
    @param slingResponse SlingHttpServletResponse
    @param resource the current resource
    @param currentNode the current node
    @param log default logger
    @param sling sling script helper

    @param componentContext component context of this request
    @param editContext edit context of this request
    @param properties properties of the addressed resource (aka "localstruct")
    @param pageManager page manager
    @param currentPage containing page addressed by the request (aka "actpage")
    @param resourcePage containing page of the addressed resource (aka "myPage")
    @param pageProperties properties of the containing page
    @param component current CQ5 component
    @param designer designer
    @param currentDesign design of the addressed resource  (aka "actdesign")
    @param resourceDesign design of the addressed resource (aka "myDesign")
    @param currentStyle style of the addressed resource (aka "actstyle")

  ==============================================================================

--%>
<%@page session="false" trimDirectiveWhitespaces="true" %>
<%@page import="design.aem.components.ComponentProperties" %>
<%@page import="javax.jcr.*,
                                org.apache.sling.api.resource.Resource,
                                org.apache.sling.api.resource.ValueMap,
                                com.day.cq.commons.inherit.InheritanceValueMap,
                                com.day.cq.wcm.commons.WCMUtils,
                                com.day.cq.wcm.api.Page,
                                com.day.cq.wcm.api.NameConstants,
                                com.day.cq.wcm.api.PageManager,
                                com.day.cq.wcm.api.designer.Designer,
                                com.day.cq.wcm.api.designer.Design,
                                com.day.cq.wcm.api.designer.Style,
                                java.util.Arrays,
                                java.util.List,
                                com.day.cq.wcm.api.components.IncludeOptions,
                                com.adobe.granite.ui.components.ComponentHelper" %>
<%@ page import="static design.aem.utils.components.CommonUtil.getBadgeFromSelectors" %>
<%@ page import="com.day.cq.i18n.I18n" %>
<%@ page import="org.apache.sling.xss.XSSAPI" %>
<%@ page import="com.day.cq.wcm.api.WCMMode" %>
<%@ page import="com.day.cq.wcm.foundation.Placeholder" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %>
<%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<cq:defineObjects/>
<%

    com.day.cq.wcm.api.components.ComponentContext _componentContext = (com.day.cq.wcm.api.components.ComponentContext) pageContext.getAttribute("componentContext");
    com.day.cq.wcm.api.designer.Design _currentDesign = (com.day.cq.wcm.api.designer.Design) pageContext.getAttribute("currentDesign");
    javax.jcr.Node _currentNode = (javax.jcr.Node) pageContext.getAttribute("currentNode");
    com.day.cq.wcm.api.Page _currentPage = (com.day.cq.wcm.api.Page) pageContext.getAttribute("currentPage");
    com.day.cq.wcm.api.designer.Style _currentStyle = (com.day.cq.wcm.api.designer.Style) pageContext.getAttribute("currentStyle");
    com.day.cq.wcm.api.designer.Designer _designer = (com.day.cq.wcm.api.designer.Designer) pageContext.getAttribute("designer");
    com.day.cq.wcm.api.components.EditContext _editContext = (com.day.cq.wcm.api.components.EditContext) pageContext.getAttribute("editContext");
    org.slf4j.Logger _log = (org.slf4j.Logger) pageContext.getAttribute("log");
    com.day.cq.wcm.api.PageManager _pageManager = (com.day.cq.wcm.api.PageManager) pageContext.getAttribute("pageManager");
    com.day.cq.commons.inherit.InheritanceValueMap _pageProperties = (com.day.cq.commons.inherit.InheritanceValueMap) pageContext.getAttribute("pageProperties");
    org.apache.sling.api.resource.ValueMap _properties = (org.apache.sling.api.resource.ValueMap) pageContext.getAttribute("properties");
    org.apache.sling.api.resource.Resource _resource = (org.apache.sling.api.resource.Resource) pageContext.getAttribute("resource");
    com.day.cq.wcm.api.designer.Design _resourceDesign = (com.day.cq.wcm.api.designer.Design) pageContext.getAttribute("resourceDesign");
    com.day.cq.wcm.api.Page _resourcePage = (com.day.cq.wcm.api.Page) pageContext.getAttribute("resourcePage");
    org.apache.sling.api.resource.ResourceResolver _resourceResolver = (org.apache.sling.api.resource.ResourceResolver) pageContext.getAttribute("resourceResolver");
    org.apache.sling.api.scripting.SlingScriptHelper _sling = (org.apache.sling.api.scripting.SlingScriptHelper) pageContext.getAttribute("sling");
    org.apache.sling.api.SlingHttpServletRequest _slingRequest = (org.apache.sling.api.SlingHttpServletRequest) pageContext.getAttribute("slingRequest");
    org.apache.sling.api.SlingHttpServletResponse _slingResponse = (org.apache.sling.api.SlingHttpServletResponse) pageContext.getAttribute("slingResponse");
    org.apache.sling.xss.XSSAPI _xssAPI = _sling.getService(XSSAPI.class).getRequestSpecificAPI(_slingRequest);
    com.day.cq.tagging.TagManager _tagManager = _resourceResolver.adaptTo(com.day.cq.tagging.TagManager.class);
    com.day.cq.commons.Externalizer _externalizer = _sling.getService(com.day.cq.commons.Externalizer.class);
    com.day.cq.i18n.I18n _i18n = new I18n(_slingRequest);
    com.day.cq.wcm.api.LanguageManager _languageManager = _sling.getService(com.day.cq.wcm.api.LanguageManager.class);

    final ComponentHelper cmp = new ComponentHelper(pageContext);


    // add initialization code here
    // can put language, wcmmode here
    final WCMMode CURRENT_WCMMODE = WCMMode.fromRequest(request);
    final String DESIGN_PATH = "";//_currentDesign.getPath();
    final boolean INCLUDE_PAGE_TIMING = false; //does not work on aem 6.0
    final boolean INCLUDE_PAGE_CLOUDSERVICES = true;
    final boolean INCLUDE_PAGE_COMPONENTINIT = true;
    final boolean INCLUDE_PAGE_CONTEXTHUB = true;       //used in Touch UI
    final boolean INCLUDE_PAGE_CLIENTCONTEXT = false;   //used in Classic UI
    final boolean INCLUDE_BADGE_VARIANT_CODE = false; //show component variant template in component BADGE
    final boolean INCLUDE_USE_GRID = true; //for a parsys use aemdesign/components/layout/container
    //Do not update unless you have verified all components work
    final Boolean REMOVEDECORATION = true; //change this if you want component decoration removed
    //Decide to print Component Badges
    final Boolean PRINT_COMPONENT_BADGE = true;


    //remove decoration for all components
    if (CURRENT_WCMMODE != WCMMode.EDIT && CURRENT_WCMMODE != WCMMode.DESIGN) {
        if (REMOVEDECORATION)   {

            _componentContext.setDecorate(false);
            _componentContext.setDecorationTagName("");
            _componentContext.setDefaultDecorationTagName("");

            IncludeOptions.getOptions(request, true).forceSameContext(Boolean.FALSE).setDecorationTagName("");

        }
    }
    List<String> selectors = Arrays.asList(_slingRequest.getRequestPathInfo().getSelectors());
    boolean MODE_TOUCHUI = Placeholder.isAuthoringUIModeTouch(_slingRequest);
//    if (selectors.contains("touchedit")) {
//        MODE_TOUCHUI = true;
//    }

    String componentBadge = getBadgeFromSelectors(_slingRequest.getRequestPathInfo().getSelectorString());


%>

<c:set var="CURRENT_WCMMODE" value="<%= CURRENT_WCMMODE %>"/>
<c:set var="PRINT_COMPONENT_BADGE" value="<%= PRINT_COMPONENT_BADGE %>"/>
<c:set var="WCMMODE_DISABLED" value="<%= WCMMode.DISABLED %>"/>
<c:set var="WCMMODE_EDIT" value="<%= WCMMode.EDIT %>"/>
<c:set var="WCMMODE_DESIGN" value="<%= WCMMode.DESIGN %>"/>
<c:set var="WCMMODE_PREVIEW" value="<%= WCMMode.PREVIEW %>"/>
<c:set var="DESIGN_PATH" value="<%= DESIGN_PATH %>"/>
<c:set var="LOCALE" value="<%= request.getLocale() %>"/>
<c:set var="REMOVEDECORATION" value="<%= REMOVEDECORATION %>"/>
<c:set var="INCLUDE_PAGE_TIMING" value="<%= INCLUDE_PAGE_TIMING %>"/>
<c:set var="INCLUDE_PAGE_CONTEXTHUB" value="<%= INCLUDE_PAGE_CONTEXTHUB %>"/>
<c:set var="INCLUDE_PAGE_CLIENTCONTEXT" value="<%= INCLUDE_PAGE_CLIENTCONTEXT%>"/>
<c:set var="INCLUDE_PAGE_CLOUDSERVICES" value="<%= INCLUDE_PAGE_CLOUDSERVICES %>"/>
<c:set var="INCLUDE_PAGE_COMPONENTINIT" value="<%= INCLUDE_PAGE_COMPONENTINIT %>"/>
<c:set var="MODE_TOUCHUI" value="<%= MODE_TOUCHUI %>"/>
<c:set var="INCLUDE_BADGE_VARIANT_CODE" value="<%= INCLUDE_BADGE_VARIANT_CODE %>"/>
<c:set var="COMPONENT_BADGE" value="<%= componentBadge %>"/>
<c:set var="INCLUDE_USE_GRID" value="<%= INCLUDE_USE_GRID %>"/>
