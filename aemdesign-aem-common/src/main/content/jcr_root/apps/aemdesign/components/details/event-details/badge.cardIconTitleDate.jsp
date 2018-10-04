<div class="card ${componentProperties.cardSize} ${badgeClassAttr}" itemscope itemtype="http://schema.org/Event">
    <c:if test="${componentProperties.cardIconShow}">
        <div class="card-icon">
            <i class="icon ${badgeClassIconAttr}" title="${componentProperties.title}"></i>
        </div>
    </c:if>
    <div class="card-body">
        <${componentProperties.badgeTitleType} class="card-title" itemprop="name">${componentProperties.pageNavTitle}</${componentProperties.badgeTitleType}>
        <div class="card-subtitle">${componentProperties.subTitleFormatted}</div>
        <div class="card-date">${componentProperties.eventDisplayTimeFormatted}</div>
    </div>
</div>
