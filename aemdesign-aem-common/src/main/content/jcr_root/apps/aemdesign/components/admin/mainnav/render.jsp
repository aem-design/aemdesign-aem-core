<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="mainnavdata.jsp" %>
<%
%><%@page session="false" %>

<%
    String[] menuPages = _properties.get("menuPages", new String[0]);

    List<Map> tabPagesInfo = this.getMenuPageList(_pageManager, menuPages);

    //no lambada is available so this is the best that can be done
    Object[][] componentFields = {
            {"menuPages", new String[0]},
            {"cssClassNav", ""},
            {"cssClassContainer", ""},
            {"cssClassList", ""},
            {"cssClassItem", ""},
            {"cssClassItemLink", ""},
            {"cssClassSubNav", ""},
            {"displayMode", "desktop"}
    };
    ComponentProperties componentProperties = getComponentProperties(pageContext, componentFields);
%>

<c:set var="componentProperties" value="<%= componentProperties %>" />
<c:set var="menuItems" value="<%= tabPagesInfo %>" />

<nav class="nav ${componentProperties.cssClassNav}" role="navigation">
    <c:if test="${not empty menuItems}">
        <ul class="list ${componentProperties.cssClassList}">
            <c:forEach items="${menuItems}" var="link">
                <li class="item ${componentProperties.cssClassItem} ${link.cssClass}">
                    <c:choose>
                        <c:when test="<%= CURRENT_WCMMODE == WCMMode.EDIT %>">
                            <a href="${link.authHref}" class="${componentProperties.cssClassItemLink}">${link.title}</a>
                        </c:when>
                        <c:otherwise>
                            <a href="${link.href}" class="${componentProperties.cssClassItemLink}">${link.title}</a>
                        </c:otherwise>
                    </c:choose>
                    <c:if test="${not empty link.parsysPath}">
                        <div class="subnav ${componentProperties.cssClassSubNav}">
                            <%
                                String defDecor =_componentContext.getDefaultDecorationTagName();

                                disableEditMode(_componentContext, IncludeOptions.getOptions(request, true), _slingRequest);

                                String path = resource.getPath();
                                String key = "apps.aemdesign.components.default.mainnav" + path;
                                try {
                                    if (request.getAttribute(key) == null || Boolean.FALSE.equals(request.getAttribute(key))) {
                                        request.setAttribute(key,Boolean.TRUE);
                                    } else {
                                        throw new IllegalStateException("Reference loop: " + path);
                                    }


                            %><cq:include path="${link.parsysPath}" resourceType="foundation/components/parsys" /><%

                            } catch (Exception ex) {
                                // Log errors always
                                //log.error("Reference component error", e);
                                if (CURRENT_WCMMODE == WCMMode.EDIT) {
                            %><p class="cq-error">Content error (<%= xssAPI.encodeForHTML(ex.toString()) %>)</p><%
            }
        }
        finally {

            enableEditMode(CURRENT_WCMMODE, _componentContext, defDecor, IncludeOptions.getOptions(request, true), _slingRequest);

            request.setAttribute(key,Boolean.FALSE);

            //@TODO: add tracking inside cq:include/sling:request to track image.out
            String imageCount= "";

            if (request.getAttribute("apps.aemdesign.components.content.image")!= null) {
                imageCount = request.getAttribute("apps.aemdesign.components.content.image").toString();
            }


                        }
                    %><!--ITEM CONTENT END-->
                        </div>
                    </c:if>
                </li>
            </c:forEach>
        </ul>
    </c:if>
</nav>
