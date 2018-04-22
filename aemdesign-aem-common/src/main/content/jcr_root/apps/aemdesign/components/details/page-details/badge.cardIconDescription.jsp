<div class="card ${componentProperties.cardSize} ${badgeClassStyleAttr}" ${badgeAnimationAttr}>
    <c:if test="${componentProperties.cardIconShow}">
        <div class="card-icon">
            <i class="icon ${badgeClassIconAttr}" title="${componentProperties.title}"></i>
        </div>
    </c:if>
    <div class="card-body">
        <p class="card-text">${componentProperties.description}</p>
    </div>
</div>
