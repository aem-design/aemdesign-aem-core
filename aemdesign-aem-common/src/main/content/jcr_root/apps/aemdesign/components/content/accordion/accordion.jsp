<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ taglib prefix="widgets" uri="http://www.adobe.com/consulting/acs-aem-commons/widgets" %>

<%
    Object[][] componentFields = {
            {"width", "80"},
            {"height", "80"}
    };

    ComponentProperties componentProperties = getComponentProperties(pageContext, componentFields);
    componentProperties.putAll(getComponentStyleProperties(pageContext));

    componentProperties.put("componentAttributes", compileComponentAttributesAsAdmin(componentProperties,_component,_sling));

%>
<c:set var="list" value="${widgets:getMultiFieldPanelValues(resource, 'list')}"/>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:choose>
    <c:when test="${not empty list}">
        <%@include file="style.default.jsp" %>
    </c:when>
    <c:when test="${CURRENT_WCMMODE eq WCMMODE_EDIT}">
        <%@include file="style.empty.jsp" %>
    </c:when>

</c:choose>