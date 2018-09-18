<c:if test="${not empty componentProperties.pageBackgroundImage}">
    <c:set var="extraAttr" value="${extraAttr} style=\"background-image: url(${componentProperties.pageBackgroundImage})\""/>
</c:if>
<div ${componentProperties.componentAttributes}${extraAttr}>
    <div class="container">
        <header>
        <c:if test="${not componentProperties.hideTitle}">
            <${componentProperties.titleType}>${componentProperties.titleFormatted}</${componentProperties.titleType}>
        </c:if>
        <c:if test="${not componentProperties.hideDescription}">
            <div class="description">${componentProperties.description}</div>
        </c:if>
        <p>
            ${componentProperties.subTitleFormatted}
            </br>${componentProperties.eventDisplayTimeFormatted}
        </p>
        </header>
    </div>
</div>