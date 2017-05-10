<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%

    final String DEFAULT_ARIA_ROLE = "contentinfo";

    Object[][] componentFields = {
        {"ariaRole",DEFAULT_ARIA_ROLE},
    };

    ComponentProperties componentProperties = getComponentProperties(pageContext, componentFields);
    componentProperties.putAll(getComponentStyleProperties(pageContext));

    componentProperties.put("componentAttributes", compileComponentAttributesAsAdmin(componentProperties,_component,_sling));
%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>

<footer role="${componentProperties.ariaRole}" ${componentProperties.componentAttributes}>
    <cq:include path="par" resourceType="foundation/components/parsys"/>
    <%@include file="/apps/aemdesign/global/component-badge.jsp" %>
</footer>
