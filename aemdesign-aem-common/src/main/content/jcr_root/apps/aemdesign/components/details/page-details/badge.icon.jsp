<c:if test="${not empty componentProperties.linkTarget}">
    <c:set var="linkAttr" value="${linkAttr} target=\"${componentProperties.linkTarget}\""/>
</c:if>
<c:if test="${not empty componentProperties.redirectTarget}">
    <c:set var="linkAttr" value="${linkAttr} external"/>
</c:if>

<c:choose>
    <c:when test="${not empty componentProperties.pageUrl}">
        <a href="${componentProperties.pageUrl}" title="${componentProperties.title}"${linkAttr}>
            <i class="${fn:join(componentProperties.pageIcon," ")}" title="${componentProperties.title}"></i>
        </a>
    </c:when>
    <c:otherwise>
        <i class="${fn:join(componentProperties.pageIcon," ")}" title="${componentProperties.title}"></i>
    </c:otherwise>
</c:choose>
