<%@ page import="java.util.List" %>
<%@ page import="com.day.cq.wcm.api.Page" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="/apps/aemdesign/global/component-details.jsp" %>
<%@ include file="/apps/aemdesign/global/utils.jsp" %>
<%@ include file="/apps/aemdesign/global/i18n.jsp" %>
<%@ include file="navlistdata.jsp" %>
<%@page session="false" %>

<%
    final String DEFAULT_LISTFROM = "children";
    final String LISTFROM_CHILDREN = "children";
    final String DEFAULT_VARIANT = "default";

    //no lambada is available so this is the best that can be done
    Object[][] componentFields = {
            {"pages", new String[0]},
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {"listFrom", DEFAULT_LISTFROM},
            {"parentPage", getPrimaryPath(_slingRequest)},
            {"linkTitlePrefix", _i18n.get("linkTitlePrefix","navlist")},
            {COMPONENT_CANCEL_INHERIT_PARENT, false},
    };
    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);

    String[] supportedDetails = DEFAULT_LIST_DETAILS_SUFFIX;
    String[] supportedRoots = DEFAULT_LIST_PAGE_CONTENT;

    List<ComponentProperties> pagesInfo = null;
    if (componentProperties.get("listFrom", DEFAULT_LISTFROM).equals(LISTFROM_CHILDREN)) {
        Page parentPage = _pageManager.getPage(componentProperties.get("parentPage", ""));
        if (parentPage != null) {
            pagesInfo = getPageListInfo(pageContext,_pageManager, _resourceResolver, parentPage.listChildren(), supportedDetails, supportedRoots);
        }
    }else {
        String[] paths = componentProperties.get("pages", new String[0]);
        if (paths.length != 0) {
            pagesInfo = getPageListInfo(pageContext,_pageManager, _resourceResolver, paths, supportedDetails, supportedRoots);
        }
    }

    componentProperties.put("menuItems",pagesInfo);
    componentProperties.put("mainMenu",_i18n.get("mainMenu","navlist"));
    componentProperties.put("subMenu",_i18n.get("subMenu","navlist"));
    componentProperties.put("goToTopOfPage",_i18n.get("goToTopOfPage","navlist"));

    componentProperties.put(INHERITED_RESOURCE,findInheritedResource(_currentPage,_componentContext));
    componentProperties.put(DEFAULT_I18N_INHERIT_LABEL_PARENTNOTFOUND,getDefaultLabelIfEmpty("",DEFAULT_I18N_INHERIT_CATEGORY,DEFAULT_I18N_INHERIT_LABEL_PARENTNOTFOUND,DEFAULT_I18N_INHERIT_CATEGORY,_i18n));

%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:set var="inheritedResource" value="${componentProperties.inheritedResource}"/>
<c:choose>
    <c:when test="${!componentProperties.cancelInheritParent and empty componentProperties.inheritedResource}">
        <%@ include file="parent.notfound.jsp" %>
    </c:when>
    <c:when test="${!componentProperties.cancelInheritParent}">
        <%@ include file="parent.render.jsp" %>
    </c:when>
    <c:when test="${componentProperties.variant == 'simple'}">
        <%@ include file="variant.simple.jsp" %>
    </c:when>
    <c:when test="${componentProperties.variant == 'full'}">
        <%@ include file="variant.full.jsp" %>
    </c:when>
    <c:otherwise>
        <%@ include file="variant.default.jsp" %>
    </c:otherwise>
</c:choose>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>