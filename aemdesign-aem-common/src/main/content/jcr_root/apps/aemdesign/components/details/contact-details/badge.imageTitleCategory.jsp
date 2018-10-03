<div class="image">
    <a
        href="${componentProperties.pageUrl}"
        title="${componentProperties.title}"${badgeLinkAttr}>
        <img src="${componentProperties.pageThumbnail}"${badgeImageAttr}
             alt="${componentProperties.title}">
    </a>
</div>
<div class="title">
    <a href="${componentProperties.pageUrl}" title="${componentProperties.title}"${linkAttr}>${componentProperties.title}"</a>
</div>
<c:if test="${not empty componentProperties.category}">
    <div class="category">
        <ul class="tags">
            <c:forEach items="${componentProperties.category}" var="tag" varStatus="entryStatus">
                <li id="${tag.key}">${tag.value}</li>
            </c:forEach>
        </ul>
    </div>
</c:if>
