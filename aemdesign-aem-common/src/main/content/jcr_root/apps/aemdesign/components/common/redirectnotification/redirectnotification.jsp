<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%
    // {
    //   1 required - property name,
    //   2 required - default value,
    //   3 optional - compile into a data-{name} attribute
    // }
    Object[][] componentFields = {
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {"redirectTitle", "Invalid content path"},
            {"redirectUrl", "#"},
    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);

    String redirectTarget = _pageProperties.get("redirectTarget", "");
    componentProperties.put("redirectTarget",redirectTarget);

    if (StringUtils.isNotEmpty(redirectTarget) && redirectTarget.startsWith("/content")) {
        Page targetPage = _pageManager.getPage(redirectTarget);

        if (targetPage != null) {
            componentProperties.put("redirectUrl",redirectTarget.concat(DEFAULT_EXTENTION));
            componentProperties.put("redirectTitle",getPageTitle(targetPage));
        }
    }

%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:if test="${(CURRENT_WCMMODE eq WCMMODE_EDIT or CURRENT_WCMMODE eq WCMMODE_PREVIEW) and (not empty componentProperties.redirectTarget)}">
    <c:choose>
        <c:when test="${componentProperties.variant eq DEFAULT_VARIANT}">
            <%@ include file="variant.default.jsp" %>
        </c:when>
        <c:otherwise>
            <%@ include file="variant.default.jsp" %>
        </c:otherwise>
    </c:choose>
</c:if>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>

