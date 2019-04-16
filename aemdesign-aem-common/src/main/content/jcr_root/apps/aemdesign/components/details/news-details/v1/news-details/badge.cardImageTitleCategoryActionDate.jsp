<div class="card ${componentProperties.cardSize} ${badgeClassAttr}">
    <div class="card-img-top">
        <img src="${componentProperties.pageThumbnail}" ${badgeImageAttr} alt="${componentProperties.title}">
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
        <a class="card-link ${fn:join(componentProperties.badgeLinkStyle, ' ')}"
           href="${componentProperties.pageUrl}"
           target="${componentProperties.badgeLinkTarget}"
           title="${componentProperties.badgeLinkTitle}"
           ${badgeLinkAttr}><span>${componentProperties.badgeLinkText}</span></a>
        <c:if test="${not componentProperties.newsDateStatusText}">
            <div class="card-date">${componentProperties.newsDateStatusText}</div>
        </c:if>
    </div>
</div>
