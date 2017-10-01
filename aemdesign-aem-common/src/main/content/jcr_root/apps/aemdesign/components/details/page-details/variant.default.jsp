<c:if test="${not empty componentProperties.pageBackgroundImage}">
    <c:set var="extraAttr" value="${extraAttr} style=\"background-image: url(${componentProperties.pageBackgroundImage})\""/>
</c:if>
<div ${componentProperties.componentAttributes}${extraAttr}>
    <%@include file="page-details.header.jsp" %>
    <header>
    <${componentProperties.titleType}>${componentProperties.titleFormatted}</${componentProperties.titleType}>
    <c:if test="${not componentProperties.hideDescription}">
        <div class="description">${componentProperties.description}</div>
    </c:if>
    </header>
    <%@include file="page-details.footer.jsp" %>
</div>