<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="/apps/aemdesign/global/i18n.jsp" %>
<%
    final String DEFAULT_ARIA_ROLE = "navigation";

    final String DEFAULT_I18N_INHERIT_CATEGORY = "inherit";
    final String DEFAULT_I18N_INHERIT_LABEL_PARENTNOTFOUND = "parentnotfound";

    // {
    //   1 required - property name,
    //   2 required - default value,
    //   3 optional - compile into a data-{name} attribute
    // }
    Object[][] componentFields = {
        {FIELD_ARIA_ROLE,DEFAULT_ARIA_ROLE, DEFAULT_ARIA_ROLE_ATTRIBUTE},
        {FIELD_VARIANT, DEFAULT_VARIANT},
        {"cancelInheritParent", false},
    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);

    componentProperties.put(INHERITED_RESOURCE,findInheritedResource(_currentPage,_componentContext));

    componentProperties.put(COMPONENT_ATTRIBUTES, addComponentBackgroundToAttributes(componentProperties,_resource,DEFAULT_BACKGROUND_IMAGE_NODE_NAME));

    componentProperties.put(DEFAULT_I18N_INHERIT_LABEL_PARENTNOTFOUND,getDefaultLabelIfEmpty("",DEFAULT_I18N_INHERIT_CATEGORY,DEFAULT_I18N_INHERIT_LABEL_PARENTNOTFOUND,DEFAULT_I18N_INHERIT_CATEGORY,_i18n));

    Cookie authoringModeCookie = _slingRequest.getCookie("cq-editor-layer.template");
    if (authoringModeCookie != null && !StringUtils.isEmpty(authoringModeCookie.getValue())) {
        try {
            componentProperties.put("editor_template_structure","structure".equals(authoringModeCookie.getValue()));
            componentProperties.put("editor_template_initial","initial".equals(authoringModeCookie.getValue()));
            componentProperties.put("editor_template_layout","Layouting".equals(authoringModeCookie.getValue()));
        } catch (Exception err ) {
            LOG.error("AuthoringUIMode not found for value {}: ", authoringModeCookie.getValue(), err);
        }
    }

    Cookie editingModeCookie = _slingRequest.getCookie("cq-editor-layer.page");

    if (editingModeCookie != null && !StringUtils.isEmpty(editingModeCookie.getValue())) {
        try {
            componentProperties.put("editor_page_edit","Edit".equals(editingModeCookie.getValue()));
        } catch (Exception err) {
            LOG.error("EditorUIMode not found for value {}: ", editingModeCookie.getValue(), err);
        }
    }
    try {
        //render component in template editor to allow policy creation
        componentProperties.put("isTemplateEditor",_slingRequest.getRequestPathInfo().getResourcePath().startsWith("/conf/"));
    } catch (Exception err) {
        LOG.error("Could not check request suffix {}: ", editingModeCookie.getValue(), err);
    }



%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:set var="inheritedResource" value="${componentProperties.inheritedResource}"/>

<c:choose>
    <c:when test="${!componentProperties.cancelInheritParent and empty componentProperties.inheritedResource}">
        <%@ include file="variant.notfound.jsp" %>
    </c:when>
    <c:when test="${!componentProperties.cancelInheritParent}">
        <%@ include file="variant.render.jsp" %>
    </c:when>
    <c:otherwise>
        <%@ include file="variant.default.jsp" %>
    </c:otherwise>
</c:choose>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>