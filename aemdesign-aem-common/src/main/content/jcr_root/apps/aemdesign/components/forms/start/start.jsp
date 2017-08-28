<%@include file="/apps/aemdesign/global/global.jsp" %>
<%@include file="/apps/aemdesign/global/components.jsp" %>
<%@include file="../formsData.jsp" %>
    <%

    Object[][] componentFields = {
            {"actionType","aemdesign/components/forms/actions/methodGet"},
            {"formid", StringUtils.EMPTY},
            {"redirect", StringUtils.EMPTY},
            {FIELD_VARIANT,"aemdesign"}
    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);

    Page p = _pageManager.getPage(componentProperties.get("redirect", String.class));

    componentProperties.put("redirectPage", p);


%>

<c:set var="componentProperties" value="<%= componentProperties %>"/>

    <c:choose>
        <c:when test="${componentProperties.variant eq 'aemdesign'}" >
            <%@include file="variant.default.jsp" %>
        </c:when>
        <c:otherwise>
            <%@include file="variant.default.jsp" %>
            <%--<sling:include path="/libs/foundation/components/form/start/start.jsp" />--%>
        </c:otherwise>
    </c:choose>

<%--
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
--%>
