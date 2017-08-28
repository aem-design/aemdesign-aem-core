<%@ page import="com.google.common.base.Throwables" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%
    final String DEFAULT_TOOLTIP_COORDS_FORMAT = "px";
    final String DEFAULT_ARIA_ROLE = "tooltip";

    Object[][] componentFields = {
        {"cssClass", ""},
        {"title", ""},
        {"description", ""},
        {"positionX", "auto"},
        {"positionY", "auto"},
        {"positionFormatX", DEFAULT_TOOLTIP_COORDS_FORMAT},
        {"positionFormatY", DEFAULT_TOOLTIP_COORDS_FORMAT},
        {"ariaRole", DEFAULT_ARIA_ROLE ,"role"},
        {FIELD_VARIANT, DEFAULT_VARIANT},
    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);

%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:choose>

    <c:when test="${componentProperties.variant eq DEFAULT_VARIANT}">
        <%@ include file="variant.default.jsp" %>
    </c:when>

    <c:otherwise>
        <%@ include file="variant.default.jsp" %>
    </c:otherwise>

</c:choose>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
