<%@ page import="java.util.Map" %>
<%@ page import="java.util.LinkedHashMap" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="/apps/aemdesign/global/i18n.jsp" %>
<%@ include file="contentblockmenudata.jsp" %>
<%

    final String DEFAULT_MENUSOURCE_PARENT = "parent";
    final String DEFAULT_MENUSOURCE_PAGEPATH = "pagepath";
    final String FIELD_MENUSOURCE = "menuSource";
    final String FIELD_MENUSOURCEPAGEPATH = "menuSourcePagePath";

    Object[][] componentFields = {
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {FIELD_MENUSOURCE, DEFAULT_MENUSOURCE_PARENT},
            {FIELD_MENUSOURCEPAGEPATH, ""},
    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);


    Map<String, String> contentBlockList = new LinkedHashMap<String, String>();
    Resource menuSource = resource.getParent();

    if(componentProperties.get(FIELD_MENUSOURCE, DEFAULT_MENUSOURCE_PARENT).equals(DEFAULT_MENUSOURCE_PAGEPATH)) {
        String menuSourcePagePath = componentProperties.get(FIELD_MENUSOURCEPAGEPATH, "");
        if (isNotEmpty(menuSourcePagePath)) {
            Resource menuSourcePagePathRes = _currentPage.getContentResource(menuSourcePagePath);
            if (menuSourcePagePathRes != null) {
                menuSource = menuSourcePagePathRes;
            }
        }
    }


    if (menuSource != null) {
        contentBlockList = getContentBlockMenu(menuSource);
    }

    componentProperties.put("contentBlockList",contentBlockList);
%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:choose>
    <c:when test="${not empty componentProperties.contentBlockList}">
        <%@ include file="variant.default.jsp" %>
    </c:when>
    <c:otherwise>
        <%@ include file="variant.default.jsp" %>
    </c:otherwise>
</c:choose>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
