<%@ page import="java.util.Map" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>

<%
    com.day.cq.wcm.foundation.List list = (com.day.cq.wcm.foundation.List) request.getAttribute("list");

    // get component properties
    String[] mediaPaths = _properties.get("mediaItems", new String[0]);
    boolean splitList = _properties.get("splitList", false);
    boolean hideThumbnail = _properties.get("hideThumbnail", Boolean.FALSE);
    boolean showIcons = _properties.get("showIcons", Boolean.FALSE);
    boolean showSocial = _properties.get("showShareButton", Boolean.FALSE);
    String detailsBadge = _properties.get("detailsBadge", "imageCategory");

    ArrayList<HashMap> badges =
            getBadgesForPaths(
                    _resourceResolver,
                    detailsBadge,
                    mediaPaths
            );

    for (Map map: badges) {
        Resource res = (Resource)map.get("resource");
        String s = (String)map.get("script");
    }

    // setup variables
    boolean isEmpty = mediaPaths.length == 0;

    // list start html (done here to not break XML structure later on)
    String listStartTag = "<ul class=\"list_row " + (splitList ? "split-list " : "") + "\">";
    String listEndTag = "</ul>";

    request.setAttribute("hideThumbnail", hideThumbnail);
    request.setAttribute("showBadgeIcon", showIcons);
    request.setAttribute("showSocial", showSocial);

%>
<c:choose>
    <c:when test="<%= isEmpty %>">
        <cq:include script="empty.jsp" />
    </c:when>
    <c:otherwise>
        <%= listStartTag %>
        <c:forEach items="<%= badges %>" var="badge" varStatus="badgeStatus">

            <%-- make available to other cq:includes --%>
            <c:set var="badgeResource" value="${badge.resource}" scope="request" />

            <%-- set the list class to put on the <li /> --%>
            <c:choose>
                <c:when test="${!badgeStatus.last && !badgeStatus.first}">
                    <c:set var="listClass" value="item" />
                </c:when>
                <c:when test="${badgeStatus.first}">
                    <c:set var="listClass" value="first" />
                </c:when>
                <c:when test="${badgeStatus.last}">
                    <c:set var="listClass" value="last" />
                </c:when>
            </c:choose>

            <%-- render item --%>
            <li class="${listClass}">
            <%

                String defDecor =_componentContext.getDefaultDecorationTagName();

                try {
                    disableEditMode(_componentContext, IncludeOptions.getOptions(request, true), _slingRequest);
                    %><cq:include script="${badge.script}" /><%
                }
                catch (Exception ex) {
                    if (!"JspException".equals(ex.getClass().getSimpleName())) {
                        throw ex;
                    }
                    %><p class="cq-error">Variation not found for this content type</p><%
                }
                finally {

                    enableEditMode(CURRENT_WCMMODE, _componentContext, defDecor, IncludeOptions.getOptions(request, true), _slingRequest);
                }
            %>
            </li>

            <%-- split list? --%>
            <c:if test="<%= splitList %>">
                <c:if test="${badgeStatus.index % 2 == 1 && !badgeStatus.last}">
                    <%= listEndTag %>
                    <%= listStartTag %>
                </c:if>
            </c:if>

        </c:forEach>
        <%= listEndTag %>
    </c:otherwise>
</c:choose>
