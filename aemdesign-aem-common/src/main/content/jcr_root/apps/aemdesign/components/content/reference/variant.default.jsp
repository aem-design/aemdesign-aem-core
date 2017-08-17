<div ${componentProperties.componentAttributes}>
    <c:catch var="referenceException">
        <%=resourceIncludeAsHtml(
                pageContext.getAttribute("referencePath").toString(),
                _slingResponse,
                _slingRequest)
        %>
    </c:catch>
    <c:if test="${not empty referenceException}">
        <c:out value="${referenceException}"/>
    </c:if>
</div>

