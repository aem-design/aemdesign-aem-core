<%@ page import="java.util.List" %>
<%@ page import="java.util.Map,
    	com.day.cq.wcm.api.PageFilter,
		com.day.cq.wcm.api.Page" %>
<%@ page import="com.sun.xml.internal.fastinfoset.util.StringArray" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="/apps/aemdesign/global/utils.jsp" %>
<%@ include file="navlistdata.jsp" %>
<%@page session="false" %>

<%
    final String DEFAULT_LISTFROM = "children";
    final String LISTFROM_CHILDREN = "children";
    final Boolean DEFAULT_SHOW_LANGNAV = true;
    final Boolean DEFAULT_SHOW_BACKTOTOP = true;
    final Boolean DEFAULT_SHOW_SEARCH = true;
    final String DEFAULT_VARIANT = "default";
    final String VARIANT_DEFAULT = "default";
    final String VARIANT_SIMPLE = "simple";
    final String VARIANT_MAIN_NAV = "mainnav";

    //no lambada is available so this is the best that can be done
    Object[][] componentFields = {
            {"pages", new String[0]},
            {"showLangNav", DEFAULT_SHOW_LANGNAV},
            {"showBackToTop", DEFAULT_SHOW_BACKTOTOP},
            {"showSearch", DEFAULT_SHOW_SEARCH},
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {"listFrom", DEFAULT_LISTFROM},
            {"parentPage", getPrimaryPath(_slingRequest)},
            {"linkTitlePrefix", _i18n.get("linkTitlePrefix","navlist")}
    };
    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);

    String variant = componentProperties.get(FIELD_VARIANT, DEFAULT_VARIANT);

    String[] paths = {};
    if (componentProperties.get("listFrom", DEFAULT_LISTFROM).equals(LISTFROM_CHILDREN)) {
        Page parentPage = _pageManager.getPage(componentProperties.get("parentPage", ""));
        if (parentPage != null) {
            List<String> list = new ArrayList<String>();
            if (variant.equals(VARIANT_SIMPLE) || variant.equals(VARIANT_MAIN_NAV)) {
                Iterator<Page> pages = parentPage.listChildren();
                while (pages.hasNext()) {
                    Page item = pages.next();
                    list.add(item.getPath());
                }
            }else {
                list.add(parentPage.getPath());
            }
            paths = list.toArray(new String[list.size()]);
        }
    }else {
        paths = componentProperties.get("pages", new String[0]);
    }

    String currentMenuColor = getMenuColor(_slingRequest, _currentPage);
    componentProperties.put("currentMenuColor", currentMenuColor);

    boolean isThemeExists = false;
    Node theme = getThemeNode(_slingRequest, _currentPage);
    if (theme != null) {
        String cssPath = getProperty(_slingRequest, theme, "cssPath");
        isThemeExists = cssPath != null;
    }

    List<Map> pagesInfo = variant.equals(VARIANT_SIMPLE)
            ? this.getSimpleMenuPageList(_pageManager, paths, _currentPage, _slingRequest)
            : this.getMenuPageList(_pageManager, paths, _currentPage, _slingRequest, isThemeExists);

    componentProperties.put("menuItems",pagesInfo);
    componentProperties.put("mainMenu",_i18n.get("mainMenu","navlist"));
    componentProperties.put("subMenu",_i18n.get("subMenu","navlist"));
    componentProperties.put("goToTopOfPage",_i18n.get("goToTopOfPage","navlist"));

%>
<c:set var="componentProperties" value="<%= componentProperties %>" />
<c:choose>
    <c:when test="${componentProperties.variant == 'mainnav'}">
        <%@ include file="variant.desktop.jsp" %>
        <%@ include file="variant.mobile.jsp" %>
    </c:when>
    <c:when test="${componentProperties.variant == 'simple'}">
        <%@ include file="variant.simple.jsp" %>
    </c:when>
    <c:otherwise>
        <%@ include file="variant.default.jsp" %>
    </c:otherwise>
</c:choose>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
