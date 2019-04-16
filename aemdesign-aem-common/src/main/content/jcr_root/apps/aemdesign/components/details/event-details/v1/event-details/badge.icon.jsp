
<c:choose>
    <c:when test="${not empty componentProperties.pageUrl}">
        <a href="${componentProperties.pageUrl}" title="${componentProperties.title}"${badgeLinkAttr}>
            <i class="icon ${badgeClassIconAttr}" title="${componentProperties.title}"></i>
        </a>
    </c:when>
    <c:otherwise>
        <i class="icon ${badgeClassIconAttr}" title="${componentProperties.title}"></i>
    </c:otherwise>
</c:choose>
