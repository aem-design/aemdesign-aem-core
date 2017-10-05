<c:if test="${componentProperties.tabPosition == 'top'}">
    <%@ include file="tabs.default.jsp" %>
</c:if>
<div class="tab-content">
    <c:forEach items="${componentProperties.tabPagesInfo}" var="link" varStatus="status">
        <c:set var="includePath" value="${link.contentPath}"/>
    <div class="tab-pane ${status.first ? 'active' : ''}" id="${componentProperties.componentId}_${link.name}" role="tabpanel">
        <c:catch var="referenceException">
            <%=resourceIncludeAsHtml(
                    (String)pageContext.getAttribute("includePath"),
                    _slingResponse,
                    _slingRequest)
            %>
        </c:catch>
        <c:if test="${not empty referenceException}">
            <c:out value="${referenceException}"/>
        </c:if>
    </div>
    </c:forEach>
</div>
<c:if test="${componentProperties.tabPosition == 'bottom'}">
    <%@ include file="tabs.default.jsp" %>
</c:if>


