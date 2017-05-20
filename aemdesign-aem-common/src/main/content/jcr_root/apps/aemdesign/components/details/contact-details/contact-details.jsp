<%@ page import="com.day.cq.tagging.TagConstants" %>
<%@ page import="com.google.common.base.Throwables" %>
<%@ page import="org.apache.commons.lang3.BooleanUtils" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%

    final String DEFAULT_SHOW_BREADCRUMB = "yes";
    final String DEFAULT_SHOW_TOOLBAR = "yes";
    final String I18N_CATEGORY = "contact-detail";

    Object[][] componentFields = {
            {"title", _pageProperties.get(JcrConstants.JCR_TITLE, StringUtils.EMPTY)},
            {TagConstants.PN_TAGS, new String[]{}},
            {"showBreadcrumb", DEFAULT_SHOW_BREADCRUMB},
            {"showToolbar", DEFAULT_SHOW_TOOLBAR},
            {"variant", DEFAULT_VARIANT},
    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);

    componentProperties.put("showBreadcrumb", BooleanUtils.toBoolean(componentProperties.get("showBreadcrumb", String.class)));
    componentProperties.put("showToolbar", BooleanUtils.toBoolean(componentProperties.get("showToolbar", String.class)));

    String[] tags = getMultiplePropertyString(_currentNode,TagConstants.PN_TAGS);

    componentProperties.put("tags",getTagsAsAdmin(_sling, tags, _slingRequest.getLocale()));

%>

<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:choose>
    <c:when test="${componentProperties.variant eq DEFAULT_VARIANT}">
        <%@include file="variant.default.jsp" %>
    </c:when>

    <c:otherwise>
        <%@include file="variant.default.jsp" %>
    </c:otherwise>
</c:choose>

<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
