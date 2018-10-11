<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%
    final String DEFAULT_VARIANT = "default";
    final String RESOURCE_EXISTS = "resourceExists";

    Object[][] componentFields = {
            {FIELD_VARIANT, DEFAULT_VARIANT}
    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY,
            DEFAULT_FIELDS_MEDIA);
    String field_variant = componentProperties.get(FIELD_VARIANT,StringUtils.EMPTY);
    String resourceName = LABEL_VARIANT + "." + field_variant + EXTENTION_JSP;
    componentProperties.put(RESOURCE_EXISTS,checkResourceExist(resourceName, _resource, _resourceResolver));
%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>

<c:choose>
    <c:when test="${componentProperties.resourceExists eq true}">
        <c:if test="${componentProperties.variant eq 'iframe'}">
            <%@ include file="variant.iframe.jsp"  %>
        </c:if>
    </c:when>
    <c:otherwise>
        <%@ include file="variant.default.jsp"  %>
    </c:otherwise>
</c:choose>

<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
