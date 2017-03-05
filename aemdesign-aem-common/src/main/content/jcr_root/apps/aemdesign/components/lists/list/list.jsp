<%@ page import="com.day.cq.wcm.api.Page" %>
<%@ page import="com.day.cq.wcm.api.PageManager" %>
<%@ page import="com.day.cq.wcm.api.components.ComponentContext"%>
<%@ page import="com.day.cq.wcm.api.PageFilter" %>
<%@ page import="org.apache.commons.lang.BooleanUtils" %>
<%@ page import="org.apache.sling.commons.json.JSONException" %>
<%@ page import="org.apache.sling.commons.json.io.JSONWriter" %>
<%@ page import="org.slf4j.Logger" %>

<%@ page import="java.io.StringWriter" %>
<%@ page import="java.lang.reflect.Array" %>

<%@ page import="javax.jcr.query.Query" %>
<%@ page import="javax.jcr.Node" %>
<%@ page import="javax.jcr.query.QueryResult" %>
<%@ page import="javax.jcr.NodeIterator" %>
<%@ page import="javax.jcr.query.RowIterator" %>
<%@ page import="javax.jcr.query.Row" %>
<%@ page import="javax.jcr.RepositoryException" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.GregorianCalendar" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="listData.jsp" %>
<%


//no lambada is available so this is the best that can be done
Object[][] componentFields = {
        {"feedEnabled", false},
        {"feedType", "rss"},
        {"splitList", false},
        {"tags", new String[]{}},
        {"cssClass", ""},
        {"cssIncludeTags", false},
        {"listFrom", ""},
        {"orderBy", ""},
        {"detailsBadge", "default"},
        {"cssClass", ""},
        {"titleLink", ""},
        {"imageLink", ""},
        {"title", ""},
        {"showTitle",false},
        {"cssClassTitle", ""},
        {"cssClassTitleLink", ""},
        {"cssClassListContainer", "container"},
        {"cssClassPaginationContainer", "pagination"},
        {"cssClassList", ""},
        {"cssClassItem", ""},
        {"cssClassItemLink", ""},
        {"cssClassSubNav", ""},
        {"showlistItemLink", false},
        {"cssIncludeTags", false},
        {"showListLink", false},
        {"hideThumbnail", false},
        {"feedEnabled", false},
        {"thumbnailCustom", false},
        {"listLinkText", "More Items"},
        {"listLinkTitle", "More Items"},
        {"listItemLinkText", "More"},
        {"listItemLinkTitle", "More"},
        {"listItemTitleLengthMax", "50"},
        {"listItemTitleLengthMaxSuffix", "..."},
        {"listItemSummaryLengthMax", "50"},
        {"listItemSummaryLengthMaxSuffix", "..."},
        {"displayOverlayIcon", "on"},
        {"redirectIcon", false},
        {"printStructure", "yes"},
        {FIELD_THUMBNAIL_WIDTH, ""},
        {FIELD_THUMBNAIL_HEIGHT, ""},

};



ComponentProperties componentProperties = getComponentProperties(pageContext, componentFields);

componentProperties.putAll(getComponentStyleProperties(pageContext));

componentProperties.put("componentAttributes", compileComponentAttributesAsAdmin(componentProperties,_component,_sling));


request.setAttribute(FIELD_THUMBNAIL_WIDTH,componentProperties.get(FIELD_THUMBNAIL_WIDTH));
request.setAttribute(FIELD_THUMBNAIL_HEIGHT,componentProperties.get(FIELD_THUMBNAIL_HEIGHT));

//get the image reference from the title link
String imageLink = componentProperties.get("imagelink", String.class);
if (!isEmpty(imageLink)) {
    Page imagePage = _pageManager.getPage(imageLink);
    if (imagePage != null) {
        String imageSource = "";
        if (imagePage != null) {
            imageSource = imagePage.getProperties().get("image/fileReference", (String) null);
        }
        componentProperties.put("imageSource", imageSource);
        componentProperties.put("imagePage", imagePage);
        componentProperties.put("imagePageTitle", imagePage.getTitle());
    }
}

// retrieve component title
componentProperties.put("componentTitle", _component.getTitle());

//convert tags to classes
if (componentProperties.get("cssIncludeTags",Boolean.class)) {
    String currentClass = componentProperties.get("cssClass",String.class);
    String fullClass = getTagsAsClasses(componentProperties.get("tags", new String[] {}));
    componentProperties.put("cssClass", addClasses(currentClass,fullClass));
}

componentProperties.put("resourcePath", _resource.getPath());

componentProperties.put("ddClassName",  DropTarget.CSS_CLASS_PREFIX + "pages");


if ((Boolean)componentProperties.get("feedEnabled")) {
    if ("atom".equals(componentProperties.get("feedEnabled"))) {
        componentProperties.put("feedExt", ".feed");
        componentProperties.put("feedTitle", "Atom 1.0 (List)");
        componentProperties.put("feedType", "application/atom+xml");
    } else {
        componentProperties.put("feedExt", ".rss");
        componentProperties.put("feedTitle", "RSS Feed");
        componentProperties.put("feedType", "application/rss+xml");
    }
    componentProperties.put("feedUrl", componentProperties.get("resourcePath").toString() + componentProperties.get("feedExt").toString());
}

String defDecor =_componentContext.getDefaultDecorationTagName();

    boolean printStructure = BooleanUtils.toBoolean(componentProperties.get("printStructure", "yes"));
    componentProperties.put("printStructure", printStructure);

    _componentContext.setAttribute("componentProperties", componentProperties);

%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:if test="${componentProperties.printStructure}">
<div ${componentProperties.componentAttributes}>
</c:if>

    <c:catch var="exception">
        <% //disableEditMode(_componentContext, IncludeOptions.getOptions(request, true) , _slingRequest); %>
        <%@ include file="init.jsp"  %>
    </c:catch>
    <c:if test="${ exception != null }">
        <p class="cq-error">List initialize error.<br>${exception.message}<br>${exception.stackTrace}</p>
    </c:if>
    <c:if test="${ exception == null }">
        <% //enableEditMode(CURRENT_WCMMODE, _componentContext, defDecor, IncludeOptions.getOptions(request, true), _slingRequest); %>
    </c:if>

    <c:set var="list" value="<%= request.getAttribute("list") %>"/>
    <c:set var="emptyList" value="<%= request.getAttribute("emptyList") %>"/>
    <c:if test="${componentProperties.feedEnabled}" >
        <link rel="alternate" type="${componentProperties.feedType}" title="${componentProperties.feedTitle}" href="${componentProperties.feedUrl}" />
    </c:if>


    <c:if test="${componentProperties.showTitle && not empty componentProperties.title && (not emptyList || WCMMODE_EDIT == CURRENT_WCMMODE)}" >

        <c:choose>
            <%-- when there is a link but no image --%>
            <c:when test="${not empty componentProperties.titleLink  && empty componentProperties.imageSource}">
                <div class="${componentProperties.cssClassTitle}">
                    <a href="${componentProperties.titleLink}" class="${componentProperties.cssClassTitleLink}">${componentProperties.title}</a>
                </div>
            </c:when>

            <%-- when there is a link and image --%>
            <c:when test="${not empty componentProperties.titleLink  && not empty componentProperties.imageSource}">
                <div class="${componentProperties.cssClassTitle}">
                    <a href="${componentProperties.titleLink}" class="${componentProperties.cssClassTitleLink}">${componentProperties.title}</a>
                    <a href="${componentProperties.imagePage}">
                        <img src="${componentProperties.imageSource}" alt="${componentProperties.imagePageTitle}" />
                    </a>
                </div>
            </c:when>

            <%-- any other time --%>
            <c:otherwise>
                <div class="${componentProperties.cssClassTitle}">${componentProperties.title}</div>
            </c:otherwise>
        </c:choose>
    </c:if>
<c:if test="${componentProperties.printStructure}">
    <div class="${componentProperties.cssClassListContainer}">
</c:if>
    <c:catch var="exception">
        <% //disableEditMode(_componentContext, IncludeOptions.getOptions(request, true) , _slingRequest); %>
        <cq:include script="body.jsp" />
    </c:catch>
    <c:if test="${ exception != null }">
        <p class="cq-error">List initialize error.<br>${exception.message}<br>${exception.stackTrace}</p>
    </c:if>
    <c:if test="${ exception == null }">
        <% //enableEditMode(CURRENT_WCMMODE, _componentContext, defDecor, IncludeOptions.getOptions(request, true), _slingRequest); %>
    </c:if>
<c:if test="${componentProperties.printStructure}">
    </div>
</c:if>

    <c:if test="${listIsPaginating}">


        <c:catch var="exception">
            <% //disableEditMode(_componentContext, IncludeOptions.getOptions(request, true) , _slingRequest); %>
            <cq:include script="pagination.jsp" />
        </c:catch>
        <c:if test="${ exception != null }">
            <p class="cq-error">List initialize error.<br/>${exception.message}<br/>${exception.stackTrace}</p>
        </c:if>
        <c:if test="${ exception == null }">
            <% //enableEditMode(CURRENT_WCMMODE, _componentContext, defDecor, IncludeOptions.getOptions(request, true), _slingRequest); %>
        </c:if>

    </c:if>



<c:if test="${componentProperties.printStructure}">
</div>
</c:if>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>