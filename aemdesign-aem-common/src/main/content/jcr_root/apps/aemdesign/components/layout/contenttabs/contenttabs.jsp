<%@ page import="java.util.Iterator"%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.day.cq.wcm.api.Page" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="/apps/aemdesign/global/i18n.jsp" %>
<%@ include file="/apps/aemdesign/global/component-details.jsp" %>
<%

    final String DEFAULT_LISTFROM_CHILDREN = "children";
    final String DEFAULT_LISTFROM_STATIC = "static";
    final String FIELD_LISTFROM = "listFrom";
    final String FIELD_TABPAGES = "pages";
    final String FIELD_PATHTOPARENT = "pathToParent";
    final String FIELD_TABPOSITION = "tabPosition";

    Object[][] componentFields = {
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {FIELD_LISTFROM, ""},
            {FIELD_TABPAGES, new String[0]},
            {FIELD_PATHTOPARENT, ""},
            {FIELD_TABPOSITION, "top"},
    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);

    // init
    Map<String, Object> tabs = new HashMap<String, Object>();

    List<ComponentProperties> tabPagesInfo = null;

    String[] supportedDetails = DEFAULT_LIST_DETAILS_SUFFIX;
    String[] supportedRoots = DEFAULT_LIST_PAGE_CONTENT;

    if (componentProperties.get(FIELD_LISTFROM,"").equals(DEFAULT_LISTFROM_CHILDREN)) {
        String pathToParent = componentProperties.get(FIELD_PATHTOPARENT,"");
        Page tabsParentPage = _currentPage;
        if (isNotEmpty(pathToParent)) {
            Page foundPage = tabsParentPage = _pageManager.getPage(pathToParent);
            if (foundPage !=null ) {
                tabsParentPage = foundPage;
            }
        }

        if (tabsParentPage != null) {
            tabPagesInfo = getPageListInfo(pageContext,_pageManager, _resourceResolver, tabsParentPage.listChildren(), supportedDetails, supportedRoots, null, true);
        }
    } else if (componentProperties.get(FIELD_LISTFROM,"").equals(DEFAULT_LISTFROM_STATIC)) {
        String[] tabPages =  componentProperties.get(FIELD_TABPAGES, new String[0]);

        if (tabPages.length != 0) {
            tabPagesInfo = getPageListInfo(pageContext,_pageManager, _resourceResolver, tabPages, supportedDetails, supportedRoots, null, true);
        }
    }

    componentProperties.put("tabPagesInfo",tabPagesInfo);
%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<div ${componentProperties.componentAttributes}>
<c:choose>
    <c:when test="${not empty componentProperties.tabPagesInfo and componentProperties.variant eq 'default'}">
        <%@ include file="variant.default.jsp" %>
    </c:when>
    <c:when test="${not empty componentProperties.tabPagesInfo and componentProperties.variant eq 'render'}">
        <%@ include file="variant.render.jsp" %>
    </c:when>
    <c:otherwise>
        <%@ include file="variant.empty.jsp" %>
    </c:otherwise>
</c:choose>
</div>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>