<div ${componentProperties.componentAttributes}>
    <%@include file="page-details.header.jsp" %>
    <header>
    <${componentProperties.titleType}>${componentProperties.titleFormatted}</${componentProperties.titleType}>
    <c:if test="${not componentProperties.hideDescription}">
        <div class="description">${componentProperties.description}</div>
    </c:if>
    </header>
    <%@include file="page-details.footer.jsp" %>
</div>