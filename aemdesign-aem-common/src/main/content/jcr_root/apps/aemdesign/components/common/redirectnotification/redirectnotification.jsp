<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%
    // {
    //   1 required - property name,
    //   2 required - default value,
    //   3 optional - compile into a data-{name} attribute
    // }
    Object[][] componentFields = {
            {"variant", DEFAULT_VARIANT},
    };

    ComponentProperties componentProperties = getComponentProperties(pageContext, componentFields);
    componentProperties.putAll(getComponentStyleProperties(pageContext));

    componentProperties.put("componentAttributes", compileComponentAttributesAsAdmin(componentProperties,_component,_sling));

    String redirectTarget = _pageProperties.get("redirectTarget", "");
    String redirectTitle = "";
    String redirectUrl = "";

    if (StringUtils.isNotEmpty(redirectTarget) && redirectTarget.startsWith("/content")) {
        Page targetPage = _pageManager.getPage(redirectTarget);

        // found page?
        if (targetPage != null) {
            redirectUrl = redirectTarget.concat(DEFAULT_EXTENTION);
            redirectTitle = getPageTitle(targetPage);
        }

        // set to invalid text.
        else {
            redirectTitle = "Invalid content path";
            redirectUrl = "#";
        }
    }
    componentProperties.put("redirectUrl",redirectUrl);
    componentProperties.put("redirectTitle",redirectTitle);
    componentProperties.put("redirectTarget",redirectTarget);

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

