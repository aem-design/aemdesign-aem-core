<c:choose>
    <c:when test="${not empty componentProperties.pageUrl}">
        <a href="${componentProperties.pageUrl}" title="${componentProperties.title}"${linkAttr}>
            <i class="icon ${fn:join(componentProperties.pageIcon," ")}" title="${componentProperties.title}"></i>
        </a>
    </c:when>
    <c:otherwise>
        <i class="icon ${fn:join(componentProperties.pageIcon," ")}" title="${componentProperties.title}"></i>
    </c:otherwise>
</c:choose>
