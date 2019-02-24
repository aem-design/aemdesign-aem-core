<div ${componentProperties.componentAttributes} download>
    <c:if test="${componentProperties.thumbnailType ne 'icon'}">
        <c:if test="${not empty componentProperties.thumbnailWidth}">
            <c:set var="imageAttr" value="${imageAttr} width=\"${componentProperties.thumbnailWidth}\""/>
        </c:if>
        <c:if test="${not empty componentProperties.thumbnailHeight}">
            <c:set var="imageAttr" value="${imageAttr} height=\"${componentProperties.thumbnailHeight}\""/>
        </c:if>
        <c:if test="${not empty componentProperties.title}">
            <c:set var="imageAttr" value="${imageAttr} alt=\"${componentProperties.title}\""/>
        </c:if>
        <img class="card-img-top" src="${componentProperties.thumbnail}" ${attr}/>
    </c:if>
    <div class="card-block">
        <c:if test="${componentProperties.thumbnailType eq 'icon'}">
            <span class="card-icon type_${componentProperties.iconType}">
                <img class="icon" src="http://localhost:4502/etc.clientlibs/settings/wcm/designs/aemdesign/clientlibs-theme/resources/blank.png" alt="${componentProperties.mimeTypeLabel}"/>
            </span>
        </c:if>
        <c:if test="${not componentProperties.hideTitle and not empty componentProperties.title}">
        <${componentProperties.titleType} class="card-title" id="${componentProperties.ariaLabelledBy}">${componentProperties.title}</${componentProperties.titleType}>
        </c:if>
        <c:if test="${not empty componentProperties.description}">
        <div class="card-description">${componentProperties.description}</div>
        </c:if>
        <c:if test="${ not empty componentProperties.info}">
            <div class="card-info">${componentProperties.info}</div>
        </c:if>
        <a href="${componentProperties.href}" class="btn btn-primary" download>${componentProperties.label}</a>
    </div>
    <c:if test="${ not empty componentProperties.licenseInfo}">
    <div class="card-footer">
        <span class="license">${componentProperties.licenseInfo}</span>
    </div>
    </c:if>
</div>