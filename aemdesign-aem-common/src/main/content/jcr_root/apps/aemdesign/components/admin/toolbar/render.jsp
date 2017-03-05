<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%


    Object[][] componentFields = {
            {"text", ""}
    };

    ComponentProperties componentProperties = getComponentProperties(pageContext, componentFields);
    componentProperties.putAll(getComponentStyleProperties(pageContext));

    componentProperties.put("componentAttributes", compileComponentAttributesAsAdmin(componentProperties,_component,_sling));
%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<div ${componentProperties.componentAttributes}>
    <c:if test="${WCMMODE_EDIT eq CURRENT_WCMMODE}">
        <div style="padding-left: 150px">
            &nbsp;
        </div>
    </c:if>
    <cq:include path="par" resourceType="foundation/components/parsys"/>
    <%@include file="/apps/aemdesign/global/component-badge.jsp" %>
</div>