<a href="${componentProperties.pageUrl}" title="${componentProperties.title}"${badgeLinkAttr}>
    <c:if test="${componentProperties.titleIconShow and fn:length(componentProperties.titleIcon) > 0}">
        <i class="${badgeClassAttr}" title="${componentProperties.pageNavTitle}"></i>
    </c:if><span class="title">${componentProperties.pageNavTitle}</span></a>
