<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%
    Object[][] componentFields = {
            {"text",""},
            {"tableData",""}
    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);

    //backwards compatibility for components that use textData
    String tableData = componentProperties.get("tableData","");
    String text = componentProperties.get("text","");

    if (isEmpty(text) && isNotEmpty(tableData)) {
        componentProperties.put("text",tableData);
    }

%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:choose>
    <c:when test="${empty componentProperties.variant}">
        <%@ include file="variant.default.jsp" %>
    </c:when>
    <c:otherwise>
        <%@ include file="variant.default.jsp" %>
    </c:otherwise>
</c:choose>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
