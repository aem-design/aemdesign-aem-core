<%@page session="false" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.adobe.granite.asset.api.AssetManager" %>
<%@ page import="com.day.cq.commons.*" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="/apps/aemdesign/global/i18n.jsp" %>
<%@ include file="/apps/aemdesign/global/component-details.jsp" %>
<%

    final String DEFAULT_I18N_CATEGORY = "contentblock";
    final String DEFAULT_I18N_BACKTOTOP_LABEL = "backtotoplabel";
    final String DEFAULT_I18N_BACKTOTOP_TITLE = "backtotoptitle";
    final String DEFAULT_TITLE_TAG_TYPE = "h2";
    final String[] DEFAULT_VIDEO_ATTRIBUTES = new String[]{
                "aemdesign:component-style-modifier/video/playsinline",
                "aemdesign:component-style-modifier/video/loop",
                "aemdesign:component-style-modifier/video/muted",
                };

    Object[][] componentFields = {
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {"hideTitle", false},
            {"hideTopLink", false},
            {"linksLeftTitle", ""},
            {"linksRightTitle", ""},
            {"dataTitle", ""},
            {"dataScroll", ""},
            {"linksRight", new String[]{}},
            {"linksLeft", new String[]{}},
            {"titleType", DEFAULT_TITLE_TAG_TYPE},
            {"title", ""},
            {"dataParent", ""},
            {"dataToggle", ""},
            {FIELD_STYLE_COMPONENT_BOOLEANATTR, DEFAULT_VIDEO_ATTRIBUTES,"", Tag.class.getCanonicalName()},
    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ANALYTICS,
            DEFAULT_FIELDS_ACCESSIBILITY);

    componentProperties.put("linksRightList",getPageListInfo(pageContext,_pageManager, _resourceResolver, componentProperties.get("linksRight", new String[]{})));
    componentProperties.put("linksLeftList",getPageListInfo(pageContext,_pageManager, _resourceResolver, componentProperties.get("linksLeft", new String[]{})));

    componentProperties.put("topLinkLabel",getDefaultLabelIfEmpty("",DEFAULT_I18N_CATEGORY,DEFAULT_I18N_BACKTOTOP_LABEL,DEFAULT_I18N_CATEGORY,_i18n));
    componentProperties.put("topLinkTitle",getDefaultLabelIfEmpty("",DEFAULT_I18N_CATEGORY,DEFAULT_I18N_BACKTOTOP_TITLE,DEFAULT_I18N_CATEGORY,_i18n));

    if (componentProperties.get(FIELD_VARIANT, DEFAULT_VARIANT).equals("advsection")) {
        String ariaLabelledBy = componentProperties.get(FIELD_ARIA_LABELLEDBY, "");
        if (isEmpty(ariaLabelledBy)) {
            String labelId = "heading-".concat(_currentNode.getName());
            componentProperties.put(FIELD_ARIA_LABELLEDBY, labelId);
            componentProperties.put(COMPONENT_ATTRIBUTES, addComponentAttributes(componentProperties,"aria-labelledby",labelId));
        }

    }

    componentProperties.put(DEFAULT_BACKGROUND_VIDEO_NODE_NAME,getBackgroundVideoRenditions(pageContext));

    componentProperties.put(DEFAULT_BACKGROUND_IMAGE_NODE_NAME,getBackgroundImageRenditions(pageContext));




%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<%@ include file="/apps/aemdesign/global/component-background.jsp" %>
<c:choose>
    <c:when test="${componentProperties.variant eq 'descriptionlist'}">
        <%@ include file="variant.descriptionlist.jsp" %>
    </c:when>
    <c:when test="${componentProperties.variant eq 'fieldset'}">
        <%@ include file="variant.fieldset.jsp" %>
    </c:when>
    <c:when test="${componentProperties.variant eq 'advsection'}">
        <%@ include file="variant.advanced.jsp" %>
    </c:when>
    <c:when test="${componentProperties.variant eq 'floating'}">
        <%@ include file="variant.floating.jsp" %>
    </c:when>
    <c:when test="${componentProperties.variant eq 'container'}">
        <%@ include file="variant.container.jsp" %>
    </c:when>
    <c:when test="${componentProperties.variant eq 'containerVideo'}">
        <%@ include file="variant.containerVideo.jsp" %>
    </c:when>
    <c:when test="${componentProperties.variant eq 'childsection'}">
        <%@ include file="variant.childsection.jsp" %>
    </c:when>
    <c:when test="${componentProperties.variant eq 'parsys'}">
        <%@ include file="variant.parsys.jsp" %>
    </c:when>
    <c:otherwise>
        <%@ include file="variant.default.jsp" %>
    </c:otherwise>
</c:choose>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
