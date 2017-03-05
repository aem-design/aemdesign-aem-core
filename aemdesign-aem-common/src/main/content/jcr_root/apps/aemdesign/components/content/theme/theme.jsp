<%@ page import="com.day.cq.wcm.api.WCMMode" %>
<%@ page import="com.day.cq.wcm.foundation.External" %>
<%@ page import="org.apache.commons.lang3.StringUtils"%>
<%@ include file="/apps/aemdesign/global/global.jsp"%>
<%@ include file="/apps/aemdesign/global/components.jsp"%>
<%

    Object[][] componentFields = {
            {"backgroundColor", ""},
            {"backgroundTextColor", ""},
            {"backgroundAccentColor", ""},
            {"featureBackgroundColor", ""},
            {"featureTextColor", ""},
            {"navigationHighlightColor", ""},
            {"cssPath", ""}
    };

    ComponentProperties componentProperties = getComponentProperties(pageContext, componentFields);
    componentProperties.putAll(getComponentStyleProperties(pageContext));

    componentProperties.put("componentAttributes", compileComponentAttributesAsAdmin(componentProperties,_component,_sling));
%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<%@include file="style.empty.jsp" %>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
