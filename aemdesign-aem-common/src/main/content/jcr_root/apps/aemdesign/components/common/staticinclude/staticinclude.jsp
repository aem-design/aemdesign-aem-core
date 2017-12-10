<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@include file="/apps/aemdesign/global/global.jsp" %>
<%@include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="/apps/aemdesign/global/i18n.jsp" %>
<%

    // {
    //   1 required - property name,
    //   2 required - default value,
    //   3 optional - compile into a data-{name} attribute
    // }
    Object[][] componentFields = {
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

    componentProperties.put("componentName", _component.getProperties().get(JcrConstants.JCR_TITLE,""));

    String[] includePaths = _properties.get(SITE_INCLUDE_PATHS, new String[0]);
    boolean isIncludePathsEmpty = includePaths.length == 0;
    componentProperties.put("includePaths", StringUtils.join(includePaths,","));

    Boolean showContentPreview = Boolean.parseBoolean(_properties.get("showContentPreview", "false"));
    Boolean showContent = Boolean.parseBoolean(_properties.get("showContent", "false"));
    componentProperties.put("showContentPreview", showContentPreview);
    componentProperties.put("showContent", showContent);
    componentProperties.put("showContentSet", showContent);

    String includeContents = "";

    if(!isIncludePathsEmpty) {
        //Resource contentResource = _resourceResolver.getResource(_resourceResolver,includePaths,null);
        componentProperties.put("includeContents", getResourceContent(_resourceResolver,includePaths,""));
        componentProperties.put("hasContent", StringUtils.isNotEmpty(includeContents));
    }

    //only allow hiding when in edit mode
    if (CURRENT_WCMMODE == WCMMode.EDIT) {
        componentProperties.put("showContent", showContentPreview);
    }


%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:set var="inheritedResource" value="${componentProperties.inheritedResource}"/>

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

