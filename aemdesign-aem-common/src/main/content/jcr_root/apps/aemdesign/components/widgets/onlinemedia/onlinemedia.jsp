<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%
    final String DEFAULT_VARIANT = "default";
    final String RESOURCE_EXISTS = "resourceExists";

    Object[][] componentFields = {
        {FIELD_VARIANT, DEFAULT_VARIANT},
        {FIELD_PROVIDER_URL, StringUtils.EMPTY},
    };

    ComponentProperties componentProperties = getComponentProperties(
        pageContext,
        componentFields,
        DEFAULT_FIELDS_STYLE,
        DEFAULT_FIELDS_ACCESSIBILITY);

    String field_variant = componentProperties.get(FIELD_VARIANT,StringUtils.EMPTY);
    String resourceName = String.format("%s.%s.%s", FIELD_VARIANT, field_variant, EXTENSION_JSP);
    componentProperties.put(RESOURCE_EXISTS,checkResourceExist(resourceName, _resource, _resourceResolver));
%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>

<c:choose>
    <c:when test="${componentProperties.resourceExists eq true and componentProperties.variant eq 'videoIframe'}">
        <%@ include file="variant.videoIframe.jsp"  %>
    </c:when>
    <c:otherwise>
        <%@ include file="variant.default.jsp"  %>
    </c:otherwise>
</c:choose>

<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
