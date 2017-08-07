<div ${componentProperties.componentAttributes}>
    <img class="card-img-top" src="${componentProperties.thumbnail}" alt="${componentProperties.altTitle}">
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
        <small class="text-muted">${componentProperties.licenseInfo}</small>
    </div>
    </c:if>
</div>