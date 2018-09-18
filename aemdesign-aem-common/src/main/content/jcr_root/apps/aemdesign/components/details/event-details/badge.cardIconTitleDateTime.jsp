<div class="card ${componentProperties.cardSize} ${badgeClassStyleAttr}">
    <c:if test="${componentProperties.cardIconShow}">
        <div class="card-icon">
            <i class="icon ${badgeClassIconAttr}" title="${componentProperties.title}"></i>
        </div>
    </c:if>
    <div class="card-body">
        <${componentProperties.badgeTitleType} class="card-title">${componentProperties.pageNavTitle}</${componentProperties.badgeTitleType}>
        <div class="card-date">
            <div class="date">
                <p>
                    ${componentProperties.subTitleFormatted}
                    </br>${componentProperties.eventDisplayTimeFormatted}
                </p>
            </div>
        </div>
    </div>
</div>
