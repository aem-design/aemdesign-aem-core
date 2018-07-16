<a ${componentProperties.componentAttributes} download>
    <c:choose>
        <c:when test="${componentProperties.thumbnailType eq 'icon'}">
            <span class="icon type_${componentProperties.iconType}"><img src="/apps/settings/wcm/design/aemdesign/blank.png" alt="${componentProperties.mimeTypeLabel}"/></span>
        </c:when>
        <c:otherwise>
            <c:if test="${not empty componentProperties.thumbnailWidth}">
                <c:set var="imageAttr" value="${imageAttr} width=\"${componentProperties.thumbnailWidth}\""/>
            </c:if>
            <c:if test="${not empty componentProperties.thumbnailHeight}">
                <c:set var="imageAttr" value="${imageAttr} height=\"${componentProperties.thumbnailHeight}\""/>
            </c:if>
            <c:if test="${not empty componentProperties.title}">
                <c:set var="imageAttr" value="${imageAttr} alt=\"${componentProperties.title}\""/>
            </c:if>
            <img src="${componentProperties.thumbnail}" ${imageAttr}/>
        </c:otherwise>
    </c:choose>
    <c:if test="${ not empty componentProperties.title}">
        <div class="title">${componentProperties.title}</div>
    </c:if>
    <c:if test="${ not empty componentProperties.description}">
        <div class="description">${componentProperties.description}</div>
    </c:if>
    <c:if test="${ not empty componentProperties.info}">
        <div class="info">${componentProperties.info}</div>
    </c:if>
    <c:if test="${ not empty componentProperties.licenseInfo}">
        <div class="license">${componentProperties.licenseInfo}</div>
    </c:if>
</a>