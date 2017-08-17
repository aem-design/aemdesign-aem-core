<div ${componentProperties.componentAttributes}>
    <c:catch var="referenceException">
        <%=resourceRenderAsHtml(
                pageContext.getAttribute("referencePath").toString(),
                _resourceResolver,
                _sling)
        %>
    </c:catch>
    <c:if test="${not empty referenceException}">
        <c:out value="${referenceException}"/>
    </c:if>
</div>

