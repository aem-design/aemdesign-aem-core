<a class="card ${componentProperties.cardSize} ${badgeClassAttr}" ${badgeAnimationAttr}
   href="${componentProperties.pageUrl}"
   target="${componentProperties.badgeLinkTarget}"
   title="${componentProperties.badgeLinkTitle}">
    <div class="card-img-top">
        <img src="${componentProperties.pageImageThumbnail}" ${badgeImageAttr} alt="${componentProperties.title}">
    </div>
    <div class="card-body">
        <${componentProperties.badgeTitleType} class="card-title">${componentProperties.pageNavTitle}</${componentProperties.badgeTitleType}>
    </div>
</a>
