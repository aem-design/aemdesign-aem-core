<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%

    Object[][] componentFields = {
            {"text", ""},
            {"variant", ""}
    };

    ComponentProperties componentProperties = getComponentProperties(pageContext, componentFields);
    componentProperties.putAll(getComponentStyleProperties(pageContext));

    componentProperties.put("componentAttributes", compileComponentAttributesAsAdmin(componentProperties,_component,_sling));
%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:choose>
    <c:when test="${componentProperties.variant eq 'simple'}">
        <%@ include file="variant.simple.jsp" %>
    </c:when>
    <c:otherwise>
        <%@ include file="variant.default.jsp" %>
    </c:otherwise>
</c:choose>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
