<%@ page import="com.day.cq.wcm.api.WCMMode" %>
<%@ page import="com.day.cq.wcm.foundation.External" %>
<%@ page import="org.apache.commons.lang3.StringUtils"%>
<%@ include file="/apps/aemdesign/global/global.jsp"%>
<%@ include file="/apps/aemdesign/global/components.jsp"%>
<%

    Object[][] componentFields = {
            {"target", ""},
            {"height", ""},
            {"width", ""},
            {"showScrollbar", "yes"},
            {"variant", "default"}
    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);

    componentProperties.put("instanceName", _currentNode.getName());


    String scrolling = componentProperties.get("showScrollbar","");
    if (isNotEmpty(scrolling)) {
        scrolling = MessageFormat.format("scrolling=\"{0}\"",scrolling);
    }

    componentProperties.put("scrollingAttr", scrolling);

%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:choose>
    <c:when test="${componentProperties.variant eq 'default'}">
        <%@include file="variant.default.jsp" %>
    </c:when>
    <c:otherwise>
        <%@include file="variant.default.jsp" %>
    </c:otherwise>
</c:choose>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
