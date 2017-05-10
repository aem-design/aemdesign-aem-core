<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="mainnavdata.jsp" %>
<%@page session="false" %>
<%
    final String DEFAULT_ARIA_ROLE = "navigation";
    final String DEFAULT_ARIA_LABEL = "main nav";

    //no lambada is available so this is the best that can be done
    Object[][] componentFields = {
            {"ariaRole",DEFAULT_ARIA_ROLE},
            {"ariaLabel",DEFAULT_ARIA_LABEL},
    };
    ComponentProperties componentProperties = getComponentProperties(pageContext, componentFields);
    componentProperties.putAll(getComponentStyleProperties(pageContext));

    componentProperties.put("componentAttributes", compileComponentAttributesAsAdmin(componentProperties,_component,_sling));

%>

<c:set var="componentProperties" value="<%= componentProperties %>" />

<nav aria-label="${componentProperties.ariaLabel}"
     role="${componentProperties.ariaRole}"
    ${componentProperties.componentAttributes}>
    <cq:include path="par" resourceType="foundation/components/parsys"/>
</nav>
