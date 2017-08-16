<c:if test="${componentProperties.tabPosition == 'top'}">
    <%@ include file="tabs.default.jsp" %>
</c:if>
<div class="tab-content">
    <c:forEach items="${componentProperties.tabPagesInfo}" var="link" varStatus="status">
        <c:set var="includePath" value="${link.contentPath}"/>
    <div class="tab-pane ${status.first ? 'active' : ''}" id="${link.name}" role="tabpanel">
        <c:catch var="includeException">
            <%=resourceRenderAsHtml(
                    pageContext.getAttribute("includePath").toString(),
                    _resourceResolver,
                    _sling)
            %>
        </c:catch>
        <c:if test="${not empty includeException}">
            <c:out value="${includeException}"></c:out>
        </c:if>
    </div>
    </c:forEach>
</div>
<c:if test="${componentProperties.tabPosition == 'bottom'}">
    <%@ include file="tabs.default.jsp" %>
</c:if>


