<%@page import="com.day.cq.wcm.api.WCMMode,
    com.day.cq.wcm.api.components.DropTarget" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="/apps/aemdesign/global/i18n.jsp" %>
<%

    //no lambada is available so this is the best that can be done
    Object[][] componentFields = {
            {"path", StringUtils.EMPTY},
            {FIELD_VARIANT, DEFAULT_VARIANT}
    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);

%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:set var="referencePath" value="${componentProperties.path}"/>
<div ${componentProperties.componentAttributes}>
    <c:choose>
        <c:when test="${componentProperties.variant eq 'default'}">
            <%@ include file="variant.default.jsp" %>
        </c:when>
        <c:when test="${componentProperties.variant eq 'render'}">
            <%@ include file="variant.render.jsp" %>
        </c:when>
        <c:otherwise>
            <%@ include file="variant.empty.jsp" %>
        </c:otherwise>
    </c:choose>
</div>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>