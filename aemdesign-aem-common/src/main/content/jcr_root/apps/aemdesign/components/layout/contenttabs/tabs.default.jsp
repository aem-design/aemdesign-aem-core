<ul class="nav nav-tabs" role="tablist">
    <c:forEach items="${componentProperties.tabPagesInfo}" var="link" varStatus="status">
    <li class="nav-item">
        <a class="nav-link ${status.first ? 'active' : ''}" href="#${componentProperties.componentId}_${link.name}" data-toggle="tab" role="tab">
            <c:if test="${link.showAsTabIcon}">
                <img src="${link.showAsTabIconPath}" />
            </c:if>
            ${link.title}</a>
    </li>
    </c:forEach>
</ul>