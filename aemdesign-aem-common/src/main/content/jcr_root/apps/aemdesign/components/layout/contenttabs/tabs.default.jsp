<ul class="nav nav-tabs" role="tablist">
    <c:forEach items="${componentProperties.tabPagesInfo}" var="link" varStatus="status">
    <li class="nav-item">
        <a class="nav-link ${status.first ? 'active' : ''}" href="#${componentProperties.componentId}_${link.name}" data-toggle="tab" role="tab">
            <c:if test="${link.tabIconShow}">
                <i class="icon ${link.tabIcon}"></i>
            </c:if>
            <span class="title">${link.title}</span></a>
    </li>
    </c:forEach>
</ul>