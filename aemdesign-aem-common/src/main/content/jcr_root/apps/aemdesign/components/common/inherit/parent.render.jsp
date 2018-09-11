<%@include file="/apps/aemdesign/global/global.jsp" %>
<c:catch var="referenceException">
    <%=resourceRenderAsHtml(
            (Resource)pageContext.getAttribute(INHERITED_RESOURCE),
            _resourceResolver,
            _sling)
    %>
</c:catch>
<c:if test="${not empty referenceException}">
    <c:out value="${referenceException}"/>
</c:if>
