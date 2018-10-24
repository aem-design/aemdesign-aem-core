<a class="card ${componentProperties.cardSize} ${badgeClassAttr}"
    href="${componentProperties.pageUrl}"
    target="${componentProperties.badgeLinkTarget}"
    title="${componentProperties.badgeLinkTitle}"
    ${badgeLinkAttr}>
    <div class="card-img-top">
        <img src="${componentProperties.pageThumbnail}" ${badgeImageAttr} alt="${componentProperties.title}">
    </div>
    <div class="card-body">
        <${componentProperties.badgeTitleType} class="card-title">${componentProperties.pageNavTitle}</${componentProperties.badgeTitleType}>
        <div class="card-text">${componentProperties.description}</div>
    </div>
</a>