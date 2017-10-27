<a
    href="${componentProperties.pageUrl}"
    title="${componentProperties.title}"${linkAttr}>
    <img src="${componentProperties.pageImageThumbnail}"${imageAttr}
         alt="${componentProperties.title}"
         <c:if test="${not empty componentProperties.pageSecondaryImageThumbnail}">
            class="rollover"
            data-rollover-src="${componentProperties.pageSecondaryImageThumbnail}"</c:if>>
</a>
