<div class="card card-horizontal text-center text-md-left ${badgeClassStyleAttr}" ${badgeAnimationAttr}>
    <div class="row align-items-center no-gutters">
        <c:if test="${componentProperties.cardIconShow}">
            <div class="col-md-auto">
                <i class="icon ${badgeClassIconAttr}" title="${componentProperties.title}"></i>
            </div>
        </c:if>
        <div class="col p-collapse pt-reset pt-md-collapse pl-reset pr-reset">
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
        <div class="card-action col-md-auto mt-reset mt-md-collapse">
            <a class="card-link ${fn:join(componentProperties.badgeLinkStyle, ' ')}"
                href="${componentProperties.pageUrl}"
                target="${componentProperties.badgeLinkTarget}"
                title="${componentProperties.badgeLinkTitle}"
                ${badgeLinkAttr}>${componentProperties.badgeLinkText}</a>
        </div>
    </div>
</div>
