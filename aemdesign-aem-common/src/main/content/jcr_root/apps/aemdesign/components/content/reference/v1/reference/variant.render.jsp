<c:catch var="referenceException">
    <%=resourceRenderAsHtml(
            (String)pageContext.getAttribute("referencePath"),
            _resourceResolver,
            _sling)
    %>
</c:catch>
<c:if test="${not empty referenceException}">
    <c:out value="${referenceException}"/>
</c:if>
