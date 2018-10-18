<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%
    final String DEFAULT_VARIANT = "default";
    final String RESOURCE_EXISTS = "resourceExists";

    Object[][] componentFields = {
        {FIELD_VARIANT, DEFAULT_VARIANT},
        {FIELD_MEDIA_PROVIDER, StringUtils.EMPTY, FIELD_DATA_MEDIA_PROVIDER},
        {FIELD_MEDIA_TITLE, StringUtils.EMPTY, FIELD_DATA_MEDIA_TITLE},
        {FIELD_MEDIA_ID, StringUtils.EMPTY, FIELD_DATA_MEDIA_ID},
        {FIELD_MEDIA_PARTNER_ID, StringUtils.EMPTY, FIELD_DATA_MEDIA_PARTNER_ID},
        {FIELD_MEDIA_PLAYER_ID, StringUtils.EMPTY, FIELD_DATA_MEDIA_PLAYER_ID},
        {FIELD_PROVIDER_URL, StringUtils.EMPTY, FIELD_SOURCE_ATTRIBUTE},
    };

    ComponentProperties componentProperties = getComponentProperties(
        pageContext,
        componentFields,
        DEFAULT_FIELDS_STYLE,
        DEFAULT_FIELDS_ACCESSIBILITY);

    String field_variant = componentProperties.get(FIELD_VARIANT,StringUtils.EMPTY);
    String resourceName = String.format("%s.%s.%s", FIELD_VARIANT, field_variant, EXTENSION_JSP);
    componentProperties.put(RESOURCE_EXISTS, checkResourceHasChildResource(resourceName, _resource, _resourceResolver));
%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>

<c:choose>
    <c:when test="${componentProperties.resourceExists eq true and componentProperties.variant eq 'iframe'}">
        <%@ include file="variant.iframe.jsp" %>
    </c:when>
    <c:otherwise>
        <%@ include file="variant.default.jsp" %>
    </c:otherwise>
</c:choose>

<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
