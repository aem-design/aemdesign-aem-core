<a href="${componentProperties.pageUrl}" title="${componentProperties.title}"${linkAttr}>
    <c:if test="${componentProperties.titleIconShow and fn:length(componentProperties.titleIcon) > 0}">
        <i class="${classAttr}" title="${componentProperties.pageNavTitle}"></i>
    </c:if><span class="title">${componentProperties.pageNavTitle}</span></a>
