<%@ page import="com.google.common.base.Throwables" %>
<%@page session="false"%>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%
    //no lambada is available so this is the best that can be done

    Object[][] componentFields = {

    };

    ComponentProperties componentProperties = getComponentProperties(pageContext, componentFields);
    componentProperties.putAll(getComponentStyleProperties(pageContext));

    componentProperties.put("componentAttributes", compileComponentAttributesAsAdmin(componentProperties,_component,_sling));
%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>

    <%@include file="style.default.jsp" %>
    <%@include file="/apps/aemdesign/global/component-badge.jsp" %>
