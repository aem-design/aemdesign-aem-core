<div class="card ${componentProperties.cardSize} ${badgeClassAttr}" ${badgeAnimationAttr}>
    <div class="card-img-top">
        <img src="${componentProperties.pageImageThumbnail}" ${badgeImageAttr} alt="${componentProperties.title}">
    </div>
    <div class="card-body">
        <${componentProperties.badgeTitleType} class="card-title">${componentProperties.pageNavTitle}</${componentProperties.badgeTitleType}>
        <div class="card-action">
            <a class="card-link ${fn:join(componentProperties.badgeLinkStyle, ' ')}"
               href="${componentProperties.pageUrl}"
               target="${componentProperties.badgeLinkTarget}"
               title="${componentProperties.badgeLinkTitle}"
               ${badgeLinkAttr}>${componentProperties.badgeLinkText}</a>
        </div>
    </div>
</div>
