<a ${componentProperties.componentAttributes} download>
    <c:choose>
        <c:when test="${componentProperties.thumbnailType eq 'icon'}">
            <span class="icon type_${componentProperties.iconType}"><img src="/libs/cq/ui/resources/0.gif" alt="${componentProperties.mimeTypeLabel}"/></span>
        </c:when>
        <c:otherwise>
            <c:if test="${not empty componentProperties.thumbnailWidth}">
                <c:set var="attr" value="width=\"${componentProperties.thumbnailWidth}\""/>
            </c:if>
            <c:if test="${not empty componentProperties.thumbnailHeight}">
                <c:set var="attr" value="${attr} height=\"${componentProperties.htmlHeight}\""/>
            </c:if>
            <c:if test="${not empty componentProperties.title}">
                <c:set var="attr" value="${attr} alt=\"${componentProperties.title}\""/>
            </c:if>
            <img src="${componentProperties.thumbnail}" ${attr}/>
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