<div class="card ${componentProperties.cardSize} ${badgeClassStyleAttr}">
    <c:if test="${componentProperties.cardIconShow}">
        <div class="card-icon">
            <i class="icon ${badgeClassIconAttr}" title="${componentProperties.title}"></i>
        </div>
    </c:if>
    <div class="card-body">
        <div class="card-text">${componentProperties.description}</div>
    </div>
</div>
