<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%

%>
<c:catch var="includeException">
    <%
        String[] listLookForDetailComponent = DEFAULT_LIST_DETAILS_SUFFIX;
        Page componentPage = _pageManager.getContainingPage(_resource.getPath());
        String detailsPath = findComponentInPage(_currentPage,listLookForDetailComponent);
        String componentPath = detailsPath + ".badge.metadata";
    %>
    <%=resourceRenderAsHtml(
            componentPath,
            _resourceResolver,
            _sling)
    %>
</c:catch>
<c:if test="${not empty includeException}">
    <c:out value="${includeException}"/>
</c:if>
