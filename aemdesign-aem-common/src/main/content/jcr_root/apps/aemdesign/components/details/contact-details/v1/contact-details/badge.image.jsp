<a
    href="${componentProperties.pageUrl}"
    title="${componentProperties.title}"${badgeLinkAttr} ${badgeLinkAttr}>
    <img src="${componentProperties.pageThumbnail}" ${badgeImageAttr}
         alt="${componentProperties.title}"
         <c:if test="${not empty componentProperties.pageSecondaryImageThumbnail}">
            class="rollover"
            data-rollover-src="${componentProperties.pageSecondaryImageThumbnail}"</c:if>>
</a>
