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
<c:if test="${not empty componentProperties.thumbnailWidth}">
    <c:set var="imageAttr" value="${imageAttr} width=\"${componentProperties.thumbnailWidth}\""/>
</c:if>
<c:if test="${not empty componentProperties.thumbnailHeight}">
    <c:set var="imageAttr" value="${imageAttr} height=\"${componentProperties.thumbnailHeight}\""/>
</c:if>

<a
    href="${componentProperties.pageUrl}"
    title="${componentProperties.title}"${linkAttr}>
    <img src="${componentProperties.pageImageThumbnail}"${imageAttr}
         alt="${componentProperties.title}"
         <c:if test="${not empty componentProperties.pageSecondaryImage}">
            class="rollover"
            data-rollover-src="${componentProperties.pageSecondaryImage}"</c:if>>
</a>
