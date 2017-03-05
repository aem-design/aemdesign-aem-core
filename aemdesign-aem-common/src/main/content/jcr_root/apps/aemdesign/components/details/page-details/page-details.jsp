<%@ page import="com.day.cq.wcm.api.Page" %>
<%@ page import="org.apache.commons.lang.BooleanUtils" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="./common.jsp" %>
<%

    // default values for the component
    final String DEFAULT_TITLE = getPageTitle(_currentPage);
    final String DEFAULT_DESCRIPTION = "";
    final String DEFAULT_HIDE_SITE_TITLE = "false";
    final String DEFAULT_HIDE_SEPARATOR = "false";
    final String DEFAULT_HIDE_SUMMARY = "false";
    final String DEFAULT_STYLE = "default";
    final String DEFAULT_SHOW_BREADCRUMB = "yes";
    final String DEFAULT_SHOW_TOOLBAR = "yes";


    //no lambada is available so this is the best that can be done
    Object[][] componentFields = {
            {"title", DEFAULT_TITLE},
            {"hideSiteTitle", DEFAULT_HIDE_SITE_TITLE},
            {"description", DEFAULT_DESCRIPTION},
            {"hideSeparator", DEFAULT_HIDE_SEPARATOR},
            {"hideSummary", DEFAULT_HIDE_SUMMARY},
            {"displayStyle", DEFAULT_STYLE},
            {"cssClass", ""},
            {"cssClassRow", ""},
            {"useParentPageTitle", false},
            {"showBreadcrumb", DEFAULT_SHOW_BREADCRUMB},
            {"showToolbar", DEFAULT_SHOW_TOOLBAR},
            {"titleFormat",""}
    };

    ComponentProperties componentProperties = getComponentProperties(pageContext, componentFields);

    componentProperties.put("showBreadcrumb", BooleanUtils.toBoolean(componentProperties.get("showBreadcrumb", String.class)));
    componentProperties.put("showToolbar", BooleanUtils.toBoolean(componentProperties.get("showToolbar", String.class)));


    componentProperties.putAll(getComponentStyleProperties(pageContext));

    componentProperties.put("componentAttributes", compileComponentAttributesAsAdmin(componentProperties,_component,_sling));

    // retrieve component title
    componentProperties.put("componentTitle", _component.getTitle());

    if ((Boolean)(componentProperties.get("useParentPageTitle"))) {
        Page parentPage = _currentPage.getParent();
        String parentPageTitle = parentPage.getPageTitle();
        componentProperties.put("parentPageTitle", parentPageTitle);

    }

    componentProperties.putAll(processComponentFields(componentProperties,_i18n,_sling));

%>

<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:choose>

    <c:when test="${componentProperties.displayStyle == 'hidden'}">
        <%@ include file="style.hidden.jsp" %>
    </c:when>

    <c:when test="${componentProperties.displayStyle == 'simple'}">
        <%@ include file="style.simple.jsp" %>
    </c:when>

    <c:when test="${componentProperties.displayStyle == 'parsys'}">
        <%@ include file="style.parsys.jsp" %>
    </c:when>

    <c:when test="${componentProperties.displayStyle == 'parsys-parent'}">
        <%@ include file="style.parsysparent.jsp" %>
    </c:when>

    <c:otherwise>
        <%@ include file="style.default.jsp" %>
    </c:otherwise>

</c:choose>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>

