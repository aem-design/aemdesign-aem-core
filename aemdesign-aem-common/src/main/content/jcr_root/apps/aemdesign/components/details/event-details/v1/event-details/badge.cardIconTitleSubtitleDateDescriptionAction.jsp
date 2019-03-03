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
        <div class="card-text" itemprop="description">${componentProperties.description}</div>
        <div class="card-action">
            <a class="card-link ${fn:join(componentProperties.badgeLinkStyle, ' ')}"
               href="${componentProperties.pageUrl}"
               target="${componentProperties.badgeLinkTarget}"
               title="${componentProperties.badgeLinkTitle}"
               ${badgeLinkAttr}>
                <span>${componentProperties.badgeLinkText}</span>
            </a>
        </div>
    </div>
</div>
