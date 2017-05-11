<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="mainnavdata.jsp" %>
<%
    final String DEFAULT_ARIA_ROLE = "navigation";
    final String DEFAULT_ARIA_LABEL = "main nav";

    // {
    //   1 required - property name,
    //   2 required - default value,
    //   3 optional - compile into a data-{name} attribute
    // }
    Object[][] componentFields = {
        {"ariaRole",DEFAULT_ARIA_ROLE},
        {"variant", "default"},
    };

    ComponentProperties componentProperties = getComponentProperties(pageContext, componentFields);
    componentProperties.putAll(getComponentStyleProperties(pageContext));

    componentProperties.put("componentAttributes", compileComponentAttributesAsAdmin(componentProperties,_component,_sling));

%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:choose>
    <c:when test="${componentProperties.variant eq 'default'}">
        <%@ include file="variant.default.jsp" %>
    </c:when>
    <c:otherwise>
        <%@ include file="variant.default.jsp" %>
    </c:otherwise>
</c:choose>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
