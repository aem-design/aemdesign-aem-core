<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.day.cq.replication.ReplicationStatus" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="/apps/aemdesign/global/i18n.jsp" %>
<%

    final String FIELD_PAGETITLE = "pageTitle";

    Object[][] componentFields = {
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {FIELD_PAGETITLE, ""}
    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY,
            DEFAULT_FIELDS_DETAILS_OPTIONS);

    String jcrTitle = _pageProperties.get(JcrConstants.JCR_TITLE, "");
    String overrideTitle = componentProperties.get(FIELD_PAGETITLE, "");

    componentProperties.put("pagetitle", StringUtils.isEmpty(overrideTitle) ? jcrTitle : overrideTitle);

%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>

<c:choose>
    <c:when test="${componentProperties.variant eq 'default'}">
        <%@ include file="variant.default.jsp" %>
    </c:when>
    <c:otherwise>
        <%@ include file="variant.default.jsp" %>
    </c:otherwise>
</c:choose>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>

