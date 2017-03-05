<%@ page import="java.util.Iterator"%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.day.cq.wcm.api.Page" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="contenttabsdata.jsp" %>


<%

    // init
    Map<String, Object> tabs = new HashMap<String, Object>();

    String instanceName = _currentNode.getName();
    tabs.put("instanceName", instanceName);

    String listFrom = _properties.get("listFrom", "children");
    tabs.put("listFrom", listFrom);

    String[] tabPages = _properties.get("tabPages", new String[0]);
    tabs.put("tabPages", StringUtils.join(tabPages,","));

    tabs.put("tabPosition", _properties.get("tabPosition", "top"));
    tabs.put("cssClass", _properties.get("cssClass", ""));

    String pathToParent = _properties.get("pathToParent", (String)null);
    tabs.put("pathToParent",_properties.get("pathToParent", pathToParent));

    String cssThemeClass = _properties.get("cssThemeClass", "");
    tabs.put("cssThemeClass",cssThemeClass.length()>0?" tabs-"+cssThemeClass:"");

    List<Map> tabPagesInfo = null;

    if (listFrom.equals("children") & !StringUtils.isEmpty(pathToParent)) {
        Page tabsParentPage = _pageManager.getPage(pathToParent);
        if (tabsParentPage!=null) {
            tabPagesInfo = this.getTabPageList(_pageManager, _resourceResolver, tabsParentPage.listChildren());
        }
    }
    if (listFrom.equals("static") & tabPages.length != 0) {
        tabPagesInfo = this.getTabPageList(_pageManager, _resourceResolver, tabPages);
    }

    String path = _resource.getPath();

%>

<c:set var="tabs" value="<%= tabs %>" />
<c:set var="tabPagesInfo" value="<%= tabPagesInfo %>" />
<div class="${tabs.cssClass} ${tabs.cssThemeClass}">
<c:choose>
    <c:when test="${empty tabPagesInfo}">
        <c:if test="${tabs.editMode}">
            <ul class="nav nav-tabs">
               <li><a href="#">Empty</a></li>
            </ul>
            <ul class="tab-content panel panel-default panel-body">
                <li>Empty</li>
            </ul>
        </c:if>
    </c:when>
    <c:otherwise>
        <c:choose>
            <c:when test="${tabs.tabPosition == 'bottom'}">
                <ul class="tab-content panel panel-default panel-body">
                <c:forEach items="${tabPagesInfo}" var="link" varStatus="status">
                    <li id="${link.name}"${status.first ? ' class="active"' : ''}>
                    <%

                    String defDecor =_componentContext.getDefaultDecorationTagName();

                    disableEditMode(_componentContext, IncludeOptions.getOptions(request, true), _slingRequest);

                    try {
                        String key = "apps.aemdesign.components.default.inherit";
                        if (request.getAttribute(key) == null) {
                            request.setAttribute(key, path);
                        } else {
                            throw new IllegalStateException("Reference loop: " + request.getAttribute(key).toString());
                        }

                        %><sling:include path="${link.contentPath}"/><%
                    } catch (Exception ex) {
                        if (CURRENT_WCMMODE == WCMMode.EDIT) {
                        %><p class="cq-info"><small>Content Tabs - Target: <%= path %>; Error in: ${link.contentPath}</small></p><%
                        }
                    } finally {

                        enableEditMode(CURRENT_WCMMODE, _componentContext, defDecor, IncludeOptions.getOptions(request, true), _slingRequest);


                    }
                    %>
                    </li>
                </c:forEach>
                </ul>
                <ul class="nav nav-tabs">
                <c:forEach items="${tabPagesInfo}" var="link" varStatus="status">
                    <li${status.first ? ' class="active"' : ''}><a href="#${link.name}" data-toggle="tab">
                    <c:if test="${link.showAsTabIcon}">
                        <img src="${link.showAsTabIconPath}" />
                    </c:if>
                    ${link.title}</a></li>
                </c:forEach>
                </ul>
            </c:when>
            <c:otherwise>
                <ul class="nav nav-tabs">
                <c:forEach items="${tabPagesInfo}" var="link" varStatus="status">
                    <li${status.first ? ' class="active"' : ''}><a href="#${link.name}" data-toggle="tab">
                    <c:if test="${link.showAsTabIcon}">
                        <img src="${link.showAsTabIconPath}" />
                    </c:if>
                    ${link.title}</a></li>
                </c:forEach>
                </ul>
                <ul class="tab-content panel panel-default panel-body">
                <c:forEach items="${tabPagesInfo}" var="link" varStatus="status">
                    <li id="${link.name}"${status.first ? ' class="active"' : ''}><!-- ITEM START -->
                    <%
                        String defDecor =_componentContext.getDefaultDecorationTagName();

                        disableEditMode(_componentContext, IncludeOptions.getOptions(request, true), _slingRequest);

                        String pathRef = resource.getPath();
                        String key = "apps.aemdesign.components.layout.contenttabs" + pathRef;
                        try {
                            if (request.getAttribute(key) == null || Boolean.FALSE.equals(request.getAttribute(key))) {
                                request.setAttribute(key,Boolean.TRUE);
                            } else {
                                throw new IllegalStateException("Reference loop: " + path);
                            }


                    %>
                    <sling:include path="${link.contentPath}"/>
                    <%
                    } catch (Exception ex) {
                        // Log errors always
                        //log.error("Reference component error", e);
                        if (CURRENT_WCMMODE == WCMMode.EDIT) {
                    %><p class="cq-error">Content error (<%= _xssAPI.encodeForHTML(ex.toString()) %>)</p><%
                        }
                    }
                    finally {

                        enableEditMode(CURRENT_WCMMODE, _componentContext, defDecor, IncludeOptions.getOptions(request, true), _slingRequest);

                        request.setAttribute(key,Boolean.FALSE);

                        String imageCount= "";

                        if (request.getAttribute("au.com.aemdesign.aem.components.image")!= null) {
                            imageCount = request.getAttribute("au.com.aemdesign.aem.components.image").toString();
                        }

                    }
                    %><!-- ITEM END -->
                    </li>
                </c:forEach>
                </ul>
            </c:otherwise>
        </c:choose>
    </c:otherwise>
</c:choose>
</div>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
<c:if test="<%= CURRENT_WCMMODE == WCMMode.EDIT %>">
    <p class="cq-info"><small>Content Tabs - Type: ${tabs.listFrom} Path: ${tabs.pathToParent}${tabs.tabPages} Tab Position: ${tabs.tabPosition}</small></p>
</c:if>
