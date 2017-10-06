<c:if test="${not empty componentProperties.linkTarget}">
    <c:set var="linkAttr" value="${linkAttr} target=\"${componentProperties.linkTarget}\""/>
</c:if>
<c:if test="${not empty componentProperties.pageImageId}">
    <c:set var="imageAttr" value="${imageAttr} data-asset-id=\"${componentProperties.pageImageId}\""/>
</c:if>
<c:if test="${not empty componentProperties.pageImageLicenseInfo}">
    <c:set var="imageAttr" value="${imageAttr} data-asset-license=\"${componentProperties.pageImageLicenseInfo}\""/>
</c:if>
<c:if test="${not empty componentProperties.redirectTarget}">
    <c:set var="linkAttr" value="${linkAttr} external"/>
</c:if>
<c:if test="${not empty componentProperties.thumbnailWidth}">
    <c:set var="imageAttr" value="${imageAttr} width=\"${componentProperties.thumbnailWidth}\""/>
</c:if>
<c:if test="${not empty componentProperties.thumbnailHeight}">
    <c:set var="imageAttr" value="${imageAttr} height=\"${componentProperties.thumbnailHeight}\""/>
</c:if>
<div ${componentProperties.componentAttributes}>
    <div class="card">
        <img src="${componentProperties.pageImageThumbnail}"${imageAttr}
             alt="${componentProperties.title}"
             class="card-img-top"/>
        <div class="card-block">
            <${componentProperties.titleType}>${componentProperties.pageNavTitle}</${componentProperties.titleType}>
            <c:if test="${not empty componentProperties.category}">
                <div class="card-category">
                    <ul class="tags">
                        <c:forEach items="${componentProperties.category}" var="tag" varStatus="entryStatus">
                            <li id="${tag.key}">${tag.value}</li>
                        </c:forEach>
                    </ul>
                </div>
            </c:if>
            <p class="card-text">${componentProperties.description}</p>
            <a class="card-link" href="${componentProperties.pageUrl}"
               title="${componentProperties.title}"${linkAttr}>${componentProperties.pageNavTitle}</a>
        </div>
    </div>
</div>
