<c:if test="${not empty componentProperties.linkTarget}">
    <c:set var="linkAttr" value="${linkAttr} target=\"${componentProperties.linkTarget}\""/>
</c:if>
<c:if test="${not empty componentProperties.pageImageId}">
    <c:set var="imgAttr" value="${imgAttr} data-asset-id=\"${componentProperties.pageImageId}\""/>
</c:if>
<c:if test="${not empty componentProperties.pageImageLicenseInfo or not empty componentProperties.pageSecondaryImageLicenseInfo}">
    <c:set var="imgAttr" value="${imgAttr} data-asset-license=\"${componentProperties.pageImageLicenseInfo}\""/>
</c:if>
<c:if test="${not empty componentProperties.redirectTarget}">
    <c:set var="linkAttr" value="${linkAttr} external"/>
</c:if>
<a
    href="${componentProperties.pageUrl}"
    title="${componentProperties.title}"
    ${componentProperties.componentAttributes}${linkAttr}>${componentProperties.title}
    <div class="card">
        <img src="${componentProperties.pageImage}"${imgAttr}
             alt="${componentProperties.title}"
             class="card-img-top"/>
        <div class="card-block">
            <${componentProperties.titleType}>${componentProperties.title}</${componentProperties.titleType}>
            <c:if test="${not empty componentProperties.category}">
            <div class="card-category">${componentProperties.category}</div>
            </c:if>
            <p class="card-text">${componentProperties.description}</p>
        </div>
    </div>
</a>
