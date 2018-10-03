<c:if test="${not empty componentProperties.pageBackgroundImage}">
    <c:set var="extraAttr" value="${extraAttr} style=\"background-image: url(${componentProperties.pageBackgroundImage})\""/>
</c:if>
<div ${componentProperties.componentAttributes}${extraAttr} itemscope itemtype="http://schema.org/Event">
    <div class="container">
        <header>
        <c:if test="${not componentProperties.hideTitle}">
            <${componentProperties.titleType} itemprop="name">${componentProperties.titleFormatted}</${componentProperties.titleType}>
        </c:if>
        <c:if test="${not componentProperties.hideDescription}">
            <div class="description" itemprop="description">${componentProperties.description}</div>
        </c:if>
        <div class="card-subtitle">${componentProperties.subTitleFormatted}</div>
        <div class="card-date">${componentProperties.eventDisplayTimeFormatted}</div>
        </header>
    </div>
</div>