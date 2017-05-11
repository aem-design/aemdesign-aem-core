<%@ page session="false" import="org.apache.commons.lang3.StringUtils"%>
<%@ page import="com.day.cq.wcm.api.Page"%>
<%@ page import="com.day.cq.wcm.foundation.Image" %>
<%@ page import="java.text.MessageFormat" %>

<%@ include file="/apps/aemdesign/global/global.jsp"%>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>

<%

    final String DEFAULT_STYLE = "default";
    final String I18N_CATEGORY = "locationdetail";


    Object[][] componentFields = {
            {"title", _pageProperties.get(JcrConstants.JCR_TITLE, StringUtils.EMPTY)},
            {"latitude", 0.0},
            {"longitude", 0.0},
            {"pages", new String[0]},
            {"variant", DEFAULT_STYLE}
    };


    ComponentProperties componentProperties = getComponentProperties(pageContext, componentFields);

    componentProperties.putAll(getComponentStyleProperties(pageContext));
    componentProperties.put("componentAttributes", compileComponentAttributesAsAdmin(componentProperties,_component,_sling));

%>

<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:choose>
    <c:when test="${componentProperties.variant == 'default'}">
        <%@include file="variant.default.jsp" %>
    </c:when>

    <c:otherwise>
        <%@include file="variant.default.jsp" %>
    </c:otherwise>
</c:choose>

<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
