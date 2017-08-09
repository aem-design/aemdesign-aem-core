<div ${componentProperties.componentAttributes}>
    <c:choose>
        <c:when test="${componentProperties.thumbnailType eq 'icon'}">
            <span class="icon type_${componentProperties.iconType}"><img src="/libs/cq/ui/resources/0.gif" alt="${componentProperties.mimeTypeLabel}"/></span>
        </c:when>
        <c:otherwise>
            <img class="card-img-top" src="${componentProperties.thumbnail}" alt="${componentProperties.title}"
                    <c:if test="${not empty componentProperties.thumbnailWidth}"> width="${componentProperties.thumbnailWidth}"</c:if>
                    <c:if test="${not empty componentProperties.thumbnailHeight}"> height="${componentProperties.thumbnailHeight}"</c:if>
            />
        </c:otherwise>
    </c:choose>
    <div class="card-block">
        <h4 class="card-title">${componentProperties.title}</h4>
        <p class="card-text">${componentProperties.description}</p>
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