<%@ page import="com.google.common.base.Throwables" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%
    final String DEFAULT_ARIA_ROLE = "article";

    // {
    //   { name, defaultValue, attributeName, valueTypeClass }
    // }
    Object[][] componentFields = {
        {"ariaRole",DEFAULT_ARIA_ROLE, "role"},
        {"variant", DEFAULT_VARIANT},
    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);

    componentProperties.put(COMPONENT_ATTRIBUTES, addComponentBackgroundToAttributes(componentProperties,_resource,"bgimage"));

%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:choose>
    <c:when test="${componentProperties.variant eq DEFAULT_VARIANT}">
        <%@ include file="variant.default.jsp" %>
    </c:when>
    <c:otherwise>
        <%@ include file="variant.default.jsp" %>
    </c:otherwise>
</c:choose>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>

