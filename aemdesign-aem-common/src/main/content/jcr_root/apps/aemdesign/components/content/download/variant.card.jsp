<div ${componentProperties.componentAttributes}>
    <c:if test="${not componentProperties.thumbnailType eq 'icon'}">
        <c:if test="${not empty componentProperties.thumbnailWidth}">
            <c:set var="attr" value="width=\"${componentProperties.thumbnailWidth}\""/>
        </c:if>
        <c:if test="${not empty componentProperties.thumbnailHeight}">
            <c:set var="attr" value="${attr} height=\"${componentProperties.htmlHeight}\""/>
        </c:if>
        <c:if test="${not empty componentProperties.title}">
            <c:set var="attr" value="${attr} alt=\"${componentProperties.title}\""/>
        </c:if>
        <img class="card-img-top" src="${componentProperties.thumbnail}" ${attr}/>
    </c:if>
    <div class="card-block">
        <c:if test="${componentProperties.thumbnailType eq 'icon'}">
            <span class="icon type_${componentProperties.iconType}"><img src="/libs/cq/ui/resources/0.gif" alt="${componentProperties.mimeTypeLabel}"/></span>
        </c:if>
        <c:if test="${not componentProperties.hideTitle and not empty componentProperties.title}">
        <${componentProperties.titleType} class="card-title" id="${componentProperties.ariaLabelledBy}">${componentProperties.title}</${componentProperties.titleType}>
        </c:if>
        <c:if test="${not empty componentProperties.description}">
        <p class="card-text">${componentProperties.description}</p>
        </c:if>
        <c:if test="${ not empty componentProperties.info}">
            <p class="card-text info">${componentProperties.info}</p>
        </c:if>
        <a href="${componentProperties.href}" class="btn btn-primary" download>${componentProperties.label}</a>
    </div>
    <c:if test="${ not empty componentProperties.licenseInfo}">
    <div class="card-footer">
        <small class="text-muted license">${componentProperties.licenseInfo}</small>
    </div>
    </c:if>
</div>