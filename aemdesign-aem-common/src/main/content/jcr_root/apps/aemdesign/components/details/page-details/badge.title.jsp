<%@ page session="false"%>
<%@ page import="org.apache.commons.lang3.StringUtils"%>
<%@ page import="com.day.cq.wcm.api.Page"%>
<%@ page import="java.text.MessageFormat" %>

<%@ include file="/apps/aemdesign/global/global.jsp"%>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>

<%
    com.day.cq.wcm.foundation.List list = (com.day.cq.wcm.foundation.List) request.getAttribute("list");

    // init
    Page thisPage = (Page) request.getAttribute("badgePage");

    String componentPath = "./"+PATH_DEFAULT_CONTENT+"/page-details";
    Object[][] componentFields = {
            {"title", getPageTitle(thisPage)}
    };

    ComponentProperties componentProperties = getComponentProperties(thisPage, componentPath, componentFields);

    // set title and description
    String displayTitle = getPageTitle(thisPage);
    String navTitle = getPageNavTitle(thisPage);

    String url = getPageUrl(thisPage);

    String ancestorPage = _properties.get("ancestorPage", String.class);
    if (ancestorPage != null){
        int ancesterDepth = _pageManager.getPage(ancestorPage).getDepth();
        int currentDepth = thisPage.getDepth();
        componentProperties.put("relativeDepth", currentDepth - ancesterDepth);

    }
    String parentPath = _properties.get("parentPage", _resource.getPath());
    boolean isSubPageLevel1 = false;
    if (parentPath != null){
        int parentPathDepth = _pageManager.getContainingPage(parentPath).getDepth();
        int currentPagePathDepth = thisPage.getDepth();
        isSubPageLevel1 = (currentPagePathDepth - parentPathDepth > 1);
    }


    Map<String, Page> subPageLevel1Map = new LinkedHashMap<String, Page>();

    Iterator<Page> items = list.getPages();
    int currentPageDepth = thisPage.getDepth();
    while (items.hasNext()) {
         Page p =items.next();

        if (p.getPath().startsWith(thisPage.getPath()) && (p.getDepth() - currentPageDepth == 1)){

            String subPageLevel1 = getPageUrl(p);
            subPageLevel1Map.put(mappedUrl(_resourceResolver, subPageLevel1), p);
        }
    }
    componentProperties.put("url", mappedUrl(_resourceResolver, url));
    componentProperties.put("subPageLevel1Map", subPageLevel1Map);
    componentProperties.put("isSubPageLevel1", isSubPageLevel1);

    //componentProperties.put("title", displayTitle);
    componentProperties.put("navTitle", navTitle);

    componentProperties.put("imgAlt", _i18n.get("filterByText", "pagedetail", displayTitle));


%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:if test="${componentProperties.isSubPageLevel1 == false}">

    <span>${componentProperties.navTitle}</span>
    <a href="<c:out value="${componentProperties.url}"/>" title="<c:out value="${componentProperties.imgAlt}"/>"><c:out value="${componentProperties.title}"/></a>
    <c:if test="${fn:length(componentProperties.subPageLevel1Map) > 0}">
        <ul>
            <c:forEach var="subPageLevel1" items="${componentProperties.subPageLevel1Map}">
                <li>
                    <span>${subPageLevel1.value.navigationTitle}</span>
                    <a href="${subPageLevel1.key}" title="<c:out value="${subPageLevel1.value.title}"/>">${subPageLevel1.value.title}</a>
                </li>
            </c:forEach>
        </ul>
    </c:if>

</c:if>