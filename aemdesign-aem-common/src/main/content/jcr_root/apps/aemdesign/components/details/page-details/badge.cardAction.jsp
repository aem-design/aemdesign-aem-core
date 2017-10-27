<div class="card ${componentProperties.cardSize} ${classAttr}" ${animationAttr}>
    <div class="card-img-top">
        <img src="${componentProperties.pageImageThumbnail}" ${imageAttr} alt="${componentProperties.title}">
    </div>
    <div class="card-body">
        <${componentProperties.badgeTitleType} class="card-title">${componentProperties.pageNavTitle}</${componentProperties.badgeTitleType}>
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
        <a class="card-link ${linkClassAttr}"
           href="${componentProperties.pageUrl}"
           target="${componentProperties.badgeLinkTarget}"
           title="${componentProperties.badgeLinkTitle}"
           ${linkAttr}>${componentProperties.badgeLinkText}</a>
    </div>
</div>
