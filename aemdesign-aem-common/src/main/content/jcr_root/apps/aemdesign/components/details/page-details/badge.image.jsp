<a
    href="${componentProperties.pageUrl}"
    title="${componentProperties.title}"${badgeLinkAttr}>
    <img src="${componentProperties.pageImageThumbnail}"${badgeImageAttr}
         alt="${componentProperties.title}"
         <c:if test="${not empty componentProperties.pageSecondaryImageThumbnail}">
            class="rollover"
            data-rollover-src="${componentProperties.pageSecondaryImageThumbnail}"</c:if>>
</a>
