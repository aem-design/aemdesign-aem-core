<%@include file="/apps/aemdesign/global/global.jsp" %>
<%@include file="/apps/aemdesign/global/components.jsp" %>
<%@include file="../formsData.jsp" %>
    <%

    Object[][] componentFields = {
            {"actionType","aemdesign/components/forms/actions/methodGet"},
            {"formid", StringUtils.EMPTY},
            {"redirect", StringUtils.EMPTY},
            {"variant","aemdesign"}
    };

    ComponentProperties componentProperties = getComponentProperties(pageContext, componentFields);

    Page p = _pageManager.getPage(componentProperties.get("redirect", String.class));

    componentProperties.put("redirectPage", p);

    componentProperties.putAll(getComponentStyleProperties(pageContext));

    componentProperties.put("componentAttributes", compileComponentAttributesAsAdmin(componentProperties,_component,_sling));


%>

<c:set var="componentProperties" value="<%= componentProperties %>"/>

    <c:choose>
        <c:when test="${componentProperties.variant eq 'aemdesign'}" >
            <%@include file="style.default.jsp" %>
        </c:when>
        <c:otherwise>
            <%@include file="style.default.jsp" %>
            <%--<sling:include path="/libs/foundation/components/form/start/start.jsp" />--%>
        </c:otherwise>
    </c:choose>

<%--
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
--%>