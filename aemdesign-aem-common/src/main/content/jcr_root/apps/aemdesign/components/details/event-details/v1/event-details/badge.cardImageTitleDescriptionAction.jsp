
<div class="card ${componentProperties.cardSize} ${badgeClassAttr}" itemscope itemtype="http://schema.org/Event">
    <div class="card-img-top">
        <img src="${componentProperties.pageThumbnail}" ${badgeImageAttr} alt="${componentProperties.title}">
    </div>
    <div class="card-body">
        <${componentProperties.badgeTitleType} class="card-title" itemprop="name">${componentProperties.pageNavTitle}</${componentProperties.badgeTitleType}>
        <div class="card-text" itemprop="description">${componentProperties.description}</div>
        <div class="card-action">
            <a class="card-link ${fn:join(componentProperties.badgeLinkStyle, ' ')}"
               href="${componentProperties.pageUrl}"
               target="${componentProperties.badgeLinkTarget}"
               title="${componentProperties.badgeLinkTitle}"
               ${badgeLinkAttr}><span>${componentProperties.badgeLinkText}</span></a>
        </div>
    </div>
</div>
