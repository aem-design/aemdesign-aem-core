<%@ page import="com.day.cq.wcm.api.WCMMode" %>
<%@ page import="com.day.cq.wcm.foundation.External" %>
<%@ page import="org.apache.commons.lang3.StringUtils"%>
<%@ include file="/apps/aemdesign/global/global.jsp"%>
<%@ include file="/apps/aemdesign/global/components.jsp"%>
<%

    Object[][] componentFields = {
            {"html", ""},
            {FIELD_VARIANT, DEFAULT_VARIANT}
    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);

    String html = componentProperties.get("html","");
    if (isNotEmpty(html)) {
        html = html.replaceAll("&nbsp;", " ");
        html = html.replaceAll("\\s+", " ");
        html = html.replaceAll(" = ", "=");
        html = org.apache.commons.lang.StringEscapeUtils.unescapeHtml(html);
    }
    componentProperties.put("html", html);

%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:choose>
    <c:when test="${componentProperties.variant == 'default' and not empty componentProperties.html}">
        <%@ include file="variant.default.jsp" %>
    </c:when>
    <c:when test="${componentProperties.variant == 'clean' and not empty componentProperties.html}">
        <%@ include file="variant.default.jsp" %>
    </c:when>
    <c:otherwise>
        <%@ include file="variant.empty.jsp" %>
    </c:otherwise>
</c:choose>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
