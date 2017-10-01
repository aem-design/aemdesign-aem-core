<c:if test="${not empty componentProperties.linkTarget}">
    <c:set var="linkAttr" value="${linkAttr} target=\"${componentProperties.linkTarget}\""/>
</c:if>
<c:if test="${not empty componentProperties.redirectTarget}">
    <c:set var="linkAttr" value="${linkAttr} external"/>
</c:if>

<c:if test="${not empty componentProperties.pageImageId or not empty componentProperties.pageSecondaryImageId}">
    <c:set var="imageAttr" value="${imageAttr} data-asset-ids=\"[${componentProperties.pageImageId},${componentProperties.pageSecondaryImageId}]\""/>
</c:if>
<c:if test="${not empty componentProperties.pageImageLicenseInfo or not empty componentProperties.pageSecondaryImageLicenseInfo}">
    <c:set var="imageAttr" value="${imageAttr} data-asset-licenses=\"[${componentProperties.pageImageLicenseInfo},${componentProperties.pageSecondaryImageLicenseInfo}]\""/>
</c:if>
<c:if test="${not empty badgeThumbnailWidth}">
    <c:set var="imageAttr" value="${imageAttr} width=\"${badgeThumbnailWidth}\""/>
</c:if>
<c:if test="${not empty badgeThumbnailHeight}">
    <c:set var="imageAttr" value="${imageAttr} height=\"${badgeThumbnailHeight}\""/>
</c:if>
<!--badgeThumbnail: ${badgeThumbnail} -->
<!--badgeThumbnailId: ${badgeThumbnailId} -->
<!--badgeThumbnailLicenseInfo: ${badgeThumbnailLicenseInfo} -->
<!--badgeThumbnailWidth: ${badgeThumbnailWidth} -->
<!--badgeThumbnailHeight: ${badgeThumbnailHeight} -->
<!--badgeThumbnailRendition: ${badgeThumbnailRendition} -->
<a
    href="${componentProperties.pageUrl}"
    title="${componentProperties.title}"
    ${componentProperties.componentAttributes}${linkAttr}>
    <img src="${componentProperties.pageImage}"${imageAttr}
         alt="${componentProperties.title}"
         <c:if test="${not empty componentProperties.pageSecondaryImage}">
            class="rollover"
            data-rollover-src="${componentProperties.pageSecondaryImage}"</c:if>>
</a>
