<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="/apps/aemdesign/global/i18n.jsp" %>
<%

    final String DEFAULT_ARIA_ROLE = "banner";

    // {
    //   1 required - property name,
    //   2 required - default value,
    //   3 optional - compile into a data-{name} attribute
    // }
    Object[][] componentFields = {
        {FIELD_ARIA_ROLE,DEFAULT_ARIA_ROLE, DEFAULT_ARIA_ROLE_ATTRIBUTE},
        {FIELD_VARIANT, DEFAULT_VARIANT},
        {COMPONENT_CANCEL_INHERIT_PARENT, false},
    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);

    componentProperties.put(INHERITED_RESOURCE,findInheritedResource(_currentPage,_componentContext));
    componentProperties.put(DEFAULT_I18N_INHERIT_LABEL_PARENTNOTFOUND,getDefaultLabelIfEmpty("",DEFAULT_I18N_INHERIT_CATEGORY,DEFAULT_I18N_INHERIT_LABEL_PARENTNOTFOUND,DEFAULT_I18N_INHERIT_CATEGORY,_i18n));

    componentProperties.put(DEFAULT_BACKGROUND_IMAGE_NODE_NAME,getBackgroundImageRenditions(pageContext));

%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:set var="inheritedResource" value="${componentProperties.inheritedResource}"/>
<%@ include file="/apps/aemdesign/global/component-background.jsp" %>

<c:choose>
    <c:when test="${!componentProperties.cancelInheritParent and empty componentProperties.inheritedResource}">
        <%@ include file="parent.notfound.jsp" %>
    </c:when>
    <c:when test="${!componentProperties.cancelInheritParent}">
        <%@ include file="parent.render.jsp" %>
    </c:when>
    <c:otherwise>
        <%@ include file="variant.default.jsp" %>
    </c:otherwise>
</c:choose>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>