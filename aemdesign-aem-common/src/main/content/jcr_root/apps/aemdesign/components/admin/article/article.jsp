<%@ page import="com.google.common.base.Throwables" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%


    Object[][] componentFields = {

    };

    ComponentProperties componentProperties = getComponentProperties(pageContext, componentFields);
    componentProperties.putAll(getComponentStyleProperties(pageContext));

    componentProperties.put("componentAttributes", compileComponentAttributesAsAdmin(componentProperties,_component,_sling));

%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<article role="article" ${componentProperties.componentAttributes}>
    <cq:include path="par" resourceType="foundation/components/parsys"/>
    <%@include file="/apps/aemdesign/global/component-badge.jsp" %>
</article>
