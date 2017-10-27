<a
        href="${componentProperties.pageUrl}"
        target="${componentProperties.badgeLinkTarget}"
        title="${componentProperties.badgeLinkTitle}"
        class="card ${componentProperties.cardSize} ${badgeClassStyleAttr}"
        ${badgeLinkAttr} ${badgeAnimationAttr}>
    <c:if test="${componentProperties.cardIconShow}">
        <div class="card-icon">
            <i class="icon ${badgeClassIconAttr}" title="${componentProperties.title}"></i>
        </div>
    </c:if>
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
    </div>
</a>
