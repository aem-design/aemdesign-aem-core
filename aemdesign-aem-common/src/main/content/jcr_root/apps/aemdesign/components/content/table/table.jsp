<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%
    Object[][] componentFields = {
            {"cssClass",""},
            {"alternate",false},
    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);

%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<div ${componentProperties.componentAttributes}>

    <c:if test="${componentProperties.alternate}">
    <div class="table table-resposive alternating-table">
    </c:if>
        <cq:text property="tableData"
                 escapeXml="false"
                 placeholder="<img src=\"/libs/cq/ui/resources/0.gif\" class=\"cq-table-placeholder\" alt=\"\" />"
            />
        <%@include file="/apps/aemdesign/global/component-badge.jsp" %>
    <c:if test="${componentProperties.alternate}">
    </div>
    </c:if>
</div>
