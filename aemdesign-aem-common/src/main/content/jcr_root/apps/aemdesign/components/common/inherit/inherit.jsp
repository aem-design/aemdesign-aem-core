<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="/apps/aemdesign/global/i18n.jsp" %>
<%

    Boolean cancelInheritParent = _properties.get(COMPONENT_CANCEL_INHERIT_PARENT,"").contentEquals("true");

    ComponentProperties componentProperties = getNewComponentProperties(pageContext);

    componentProperties.put(COMPONENT_CANCEL_INHERIT_PARENT,cancelInheritParent);
    componentProperties.put(DEFAULT_I18N_INHERIT_CATEGORY,getDefaultLabelIfEmpty("",DEFAULT_I18N_INHERIT_CATEGORY, JspClass.DEFAULT_I18N_INHERIT_LABEL_PARENTNOTFOUND,DEFAULT_I18N_INHERIT_CATEGORY,_i18n));
    componentProperties.put(INHERITED_RESOURCE,findInheritedResource(_currentPage,_componentContext));

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
<!-- inherit:start -->
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:set var="inheritedResource" value="${componentProperties.inheritedResource}"/>
<c:choose>
    <c:when test="${componentProperties.isTemplateEditor}">
        <cq:include script="render.jsp"/>
    </c:when>
    <c:when test="${!componentProperties.cancelInheritParent and empty componentProperties.inheritedResource and CURRENT_WCMMODE eq WCMMODE_EDIT}">
        <cq:include script="parent.notfound.jsp"/>
    </c:when>
    <c:when test="${!componentProperties.cancelInheritParent}">
        <cq:include script="parent.render.jsp"/>
    </c:when>
    <c:otherwise>
        <cq:include script="render.jsp"/>
    </c:otherwise>
</c:choose>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
<!-- inherit:end -->
