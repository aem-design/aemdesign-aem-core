<%@page session="false" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="contentblockdata.jsp" %>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%

    Object[][] componentFields = {
            {"variant", ""},
            {"hideTitle", true},
            {"hideTitleSeparator", true},
            {"hideTopLink", true},
            {"topLinkLabel", true},
            {"topLinkTitle", true},
            {"styleTitleContainerClass", "heading"},
            {"linksRightTitle", ""},
            {"dataTitle", ""},
            {"dataScroll", ""},
            {"showdataTitle", false},
            {"showdataScroll", false},
            {"linkPagesRight", new String[]{}},
            {"linkPagesLeft", new String[]{}},
            {"title", ""}
    };

    ComponentProperties componentProperties = getComponentProperties(pageContext, componentFields);
    componentProperties.putAll(getComponentStyleProperties(pageContext));

    componentProperties.put("instanceName",_currentNode.getName());

    componentProperties.put("listPagesRight",getContentPageList(_pageManager, componentProperties.get("linkPagesRight", new String[]{})));
    componentProperties.put("listPagesLeft",getContentPageList(_pageManager, componentProperties.get("linkPagesLeft", new String[]{})));

    // init
    Map<String, Object> info = new HashMap<String, Object>();

    String attributes = compileComponentAttributesAsAdmin(componentProperties,_component,_sling);

    String bgImageFile = _properties.get("bgimage/fileReference", "");

    if (isNotEmpty(bgImageFile)) {
        attributes += MessageFormat.format("style=\"background-image: url({0})\"", mappedUrl(bgImageFile));
    }

    if (componentProperties.get("showdataTitle",false)) {
        attributes += MessageFormat.format("data-fixed-menu-title=\"{0}\"", componentProperties.get("linkPagesLeft",""));
    }
    if (componentProperties.get("showdataScroll",false)) {
        attributes += MessageFormat.format("data-scroll-class=\"{0}\"", "animate-in");
    }

    componentProperties.put("componentAttributes",attributes);
%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:choose>
    <c:when test="${componentProperties.variant == 'descriptionlist'}">
        <%@ include file="variant.descriptionlist.jsp" %>
    </c:when>
    <c:when test="${componentProperties.variant == 'fieldset'}">
        <%@ include file="variant.fieldset.jsp" %>
    </c:when>
    <c:when test="${componentProperties.variant == 'advsection'}">
        <%@ include file="variant.advanced.jsp" %>
    </c:when>
    <c:when test="${componentProperties.variant == 'floating'}">
        <%@ include file="variant.floating.jsp" %>
    </c:when>
    <c:when test="${componentProperties.variant == 'container'}">
        <%@ include file="variant.container.jsp" %>
    </c:when>
    <c:when test="${componentProperties.variant == 'section'}">
        <%@ include file="variant.section.jsp" %>
    </c:when>
    <c:otherwise>
        <%@ include file="variant.default.jsp" %>
    </c:otherwise>
</c:choose>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
