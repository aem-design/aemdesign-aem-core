<%@ page import="com.day.cq.wcm.api.Page" %>
<%@ page import="org.apache.commons.lang.BooleanUtils" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="./common.jsp" %>
<%

    final String DEFAULT_ARIA_ROLE = "banner";
    final String DEFAULT_TITLE_TAG_TYPE = "h1";

    // default values for the component
    final String DEFAULT_TITLE = getPageTitle(_currentPage);
    final String DEFAULT_DESCRIPTION = _currentPage.getDescription();
    final Boolean DEFAULT_HIDE_DESCRIPTION = false;
    final Boolean DEFAULT_SHOW_BREADCRUMB = true;
    final Boolean DEFAULT_SHOW_TOOLBAR = true;
    final Boolean DEFAULT_SHOW_PARSYS = true;


    //no lambada is available so this is the best that can be done
    Object[][] componentFields = {
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {"title", DEFAULT_TITLE},
            {"titleFormat",""},
            {"description", DEFAULT_DESCRIPTION},
            {"hideDescription", DEFAULT_HIDE_DESCRIPTION},
            {"showBreadcrumb", DEFAULT_SHOW_BREADCRUMB},
            {"showToolbar", DEFAULT_SHOW_TOOLBAR},
            {"showParsys", DEFAULT_SHOW_PARSYS},
            {FIELD_ARIA_ROLE,DEFAULT_ARIA_ROLE, FIELD_ARIA_DATA_ATTRIBUTE_ROLE},
            {FIELD_TITLE_TAG_TYPE, DEFAULT_TITLE_TAG_TYPE},

    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY,
            DEFAULT_FIELDS_PAGEDETAILS_OPTIONS);

    componentProperties.put(COMPONENT_ATTRIBUTES, addComponentBackgroundToAttributes(componentProperties,_resource,"secondaryImage"));

    componentProperties.putAll(processComponentFields(componentProperties,_i18n,_sling));

%>

<c:set var="componentProperties" value="<%= componentProperties %>"/>
<!--${componentProperties}-->
<c:choose>
    <c:when test="${componentProperties.variant eq 'default'}">
        <%@ include file="variant.default.jsp" %>
    </c:when>

    <c:otherwise>
        <%@ include file="variant.default.jsp" %>
    </c:otherwise>

</c:choose>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>

