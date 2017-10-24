<c:if test="${not empty componentProperties.linkTarget}">
    <c:set var="linkAttr" value="${linkAttr} target=\"${componentProperties.linkTarget}\""/>
</c:if>
<c:if test="${not empty componentProperties.pageImageId}">
    <c:set var="imageAttr" value="${imageAttr} data-asset-id=\"${componentProperties.pageImageId}\""/>
</c:if>
<c:if test="${not empty componentProperties.redirectTarget}">
    <c:set var="linkAttr" value="${linkAttr} external"/>
</c:if>

<c:if test="${componentProperties.cardIconShow and fn:length(componentProperties.cardIcon) > 0}">
    <c:set var="classIconAttr" value="${classIconAttr} ${fn:join(componentProperties.cardIcon,' ')}"/>
</c:if>
<c:if test="${fn:length(componentProperties.cardStyle) > 0}">
    <c:set var="classStyleAttr" value="${classStyleAttr} ${fn:join(componentProperties.cardStyle,' ')}"/>
</c:if>

<a
        href="${componentProperties.pageUrl}"
        target="${componentProperties.badgeLinkTarget}"
        title="${componentProperties.badgeLinkTitle}"
        class="card ${componentProperties.cardSize} ${classStyleAttr}"
        ${linkAttr} ${animationAttr}>
    <c:if test="${componentProperties.cardIconShow}">
        <div class="card-icon">
            <i class="icon ${classIconAttr}" title="${componentProperties.title}"></i>
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
