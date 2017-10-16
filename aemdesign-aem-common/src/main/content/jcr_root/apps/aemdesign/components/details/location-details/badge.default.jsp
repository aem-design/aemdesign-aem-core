<c:if test="${not empty componentProperties.linkTarget}">
    <c:set var="linkAttr" value="${linkAttr} target=\"${componentProperties.linkTarget}\""/>
</c:if>
<c:if test="${not empty componentProperties.redirectTarget}">
    <c:set var="linkAttr" value="${linkAttr} external"/>
</c:if>

<c:if test="${componentProperties.titleIconShow and fn:length(componentProperties.titleIcon) > 0}">
    <c:set var="classAttr" value="${classAttr} ${fn:join(componentProperties.titleIcon,' ')}"/>
</c:if>

<a href="${componentProperties.pageUrl}" title="${componentProperties.title}"${linkAttr}>
    <c:if test="${componentProperties.titleIconShow and fn:length(componentProperties.titleIcon) > 0}">
        <i class="${classAttr}" title="${componentProperties.pageNavTitle}"></i>
    </c:if><span class="title">${componentProperties.pageNavTitle}</span></a>
