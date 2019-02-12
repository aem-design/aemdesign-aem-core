<c:catch var="referenceException">
    <%=resourceIncludeAsHtml(
            _componentContext,
            (String)pageContext.getAttribute("referencePath"),
            _slingResponse,
            _slingRequest)
    %>
</c:catch>
<c:if test="${not empty referenceException}">
    <c:out value="${referenceException}"/>
</c:if>