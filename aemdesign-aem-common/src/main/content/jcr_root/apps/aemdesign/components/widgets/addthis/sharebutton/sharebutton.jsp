<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%
    final String DEFAULT_ARIA_ROLE = "banner";
    final String DEFAULT_CLOUDCONFIG_ADDTHIS = "addthisconnect";
    final String DEFAULT_CLOUDCONFIG_ADDTHIS_ID = "pubId";
    final String DEFAULT_MODULE_TAG = "component-style:theme/widgets/sharebutton";

    Object[][] componentFields = {
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {FIELD_ARIA_ROLE,DEFAULT_ARIA_ROLE, FIELD_ARIA_DATA_ATTRIBUTE_ROLE},
            {FIELD_STYLE_COMPONENT_MODULE, new String[]{DEFAULT_MODULE_TAG},"data-module", Tag.class.getCanonicalName()},
            {DEFAULT_CLOUDCONFIG_ADDTHIS_ID,
                    getCloudConfigProperty(_pageProperties,DEFAULT_CLOUDCONFIG_ADDTHIS,DEFAULT_CLOUDCONFIG_ADDTHIS_ID,_sling),
                    "data-pubid"},
    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);

%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:choose>
    <c:when test="${not empty componentProperties.pubId}">
        <%@ include file="variant.default.jsp"  %>
    </c:when>
    <c:otherwise>
        <%@ include file="variant.empty.jsp"  %>
    </c:otherwise>
</c:choose>

<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
