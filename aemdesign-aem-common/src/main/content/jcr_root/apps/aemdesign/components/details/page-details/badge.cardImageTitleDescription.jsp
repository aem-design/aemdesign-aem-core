<div class="card ${componentProperties.cardSize} ${badgeClassAttr}" ${badgeAnimationAttr}>
    <div class="card-img-top">
        <img src="${componentProperties.pageImageThumbnail}" ${badgeImageAttr} alt="${componentProperties.title}">
    </div>
    <div class="card-body">
        <${componentProperties.badgeTitleType} class="card-title">${componentProperties.pageNavTitle}</${componentProperties.badgeTitleType}>
        <p class="card-text">${componentProperties.description}</p>
    </div>
</div>
