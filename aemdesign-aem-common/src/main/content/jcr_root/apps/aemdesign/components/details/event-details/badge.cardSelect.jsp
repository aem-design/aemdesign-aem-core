<!-- TODO: Refactoring & cleanup, Please refer to `badge.cardIconTitleDateTimeDescriptionAction` changes. -->

<a
        href="${componentProperties.pageUrl}"
        target="${componentProperties.badgeLinkTarget}"
        title="${componentProperties.badgeLinkTitle}"
        class="card  ${componentProperties.cardSize} ${badgeClassAttr}"
        ${badgeLinkAttr}>
    <img src="${componentProperties.pageThumbnail}" ${badgeImageAttr}
         alt="${componentProperties.title}"
         class="card-img-top"/>
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
        <div class="card-text">${componentProperties.description}</div>
    </div>
</a>
