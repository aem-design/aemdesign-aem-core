<%@include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="/apps/aemdesign/global/i18n.jsp" %>
<%
    final String DEFAULT_I18N_CATEGORY = "inherit";
    final String DEFAULT_I18N_LABEL_PARENTNOTFOUND = "parentnotfound";

    Boolean cancelInheritParent = _properties.get("cancelInheritParent","").contentEquals("true");

    ComponentProperties componentProperties = getNewComponentProperties(pageContext);

    componentProperties.put("cancelInheritParent",cancelInheritParent);
    componentProperties.put(DEFAULT_I18N_LABEL_PARENTNOTFOUND,getDefaultLabelIfEmpty("",DEFAULT_I18N_CATEGORY,DEFAULT_I18N_LABEL_PARENTNOTFOUND,DEFAULT_I18N_CATEGORY,_i18n));

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
<c:choose>
    <c:when test="<%=!cancelInheritParent%>">
        <%@ include file="findparent.jsp" %>
    </c:when>
    <c:otherwise>
        <%@ include file="render.jsp" %>
    </c:otherwise>
</c:choose>

<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
