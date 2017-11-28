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
                                com.day.cq.wcm.api.components.IncludeOptions" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %>
<%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<cq:defineObjects/>

<%@include file="/apps/aemdesign/global/context-objects.jsp"%>
<%@include file="/apps/aemdesign/global/constants.jsp"%>
<%@include file="/apps/aemdesign/global/errors.jsp"%>
<%@include file="/apps/aemdesign/global/common.jsp"%>
<%@include file="/apps/aemdesign/global/logging.jsp"%>
<%@include file="/apps/aemdesign/global/resolver.jsp"%>
<%

    // add initialization code here
    // can put language, wcmmode here
    final WCMMode CURRENT_WCMMODE = WCMMode.fromRequest(request);
    final String DESIGN_PATH = _currentDesign.getPath();
    final boolean INCLUDE_PAGE_TIMING = false; //does not work on aem 6.0
    final boolean INCLUDE_PAGE_CLOUDSERVICES = true;
    final boolean INCLUDE_PAGE_COMPONENTINIT = true;
    final boolean INCLUDE_PAGE_CONTEXTHUB = true;       //used in Touch UI
    final boolean INCLUDE_PAGE_CLIENTCONTEXT = false;   //used in Classic UI
    final boolean INCLUDE_BADGE_VARIANT_CODE = false; //show component variant template in component BADGE
    final boolean INCLUDE_USE_GRID = true; //for a parsys use wcm/foundation/components/responsivegrid

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
    boolean MODE_TOUCHUI = Placeholder.isAuthoringUIModeTouch(slingRequest);
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
<c:set var="LOCALE" value="<%= _currentPage.getLanguage(true) %>"/>
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
