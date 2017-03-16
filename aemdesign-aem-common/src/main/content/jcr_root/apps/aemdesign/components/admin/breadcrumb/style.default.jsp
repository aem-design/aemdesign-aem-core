<div ${componentProperties.componentAttributes}>
    <ul>
        <c:forEach items="${componentProperties.values}" var="link">
            <c:if test="${not empty componentProperties.delimiter}">${componentProperties.delimiter}</c:if>
            <li><a href="${link.path}.html"
                   onclick="CQ_Analytics?CQ_Analytics.record({event:'followBreadcrumb',values: { breadcrumbPath: '${link.path}' },collect: false,options: { obj: this },componentPath: '${componentProperties.componentPath}'}):true;"
                >${link.title}</a></li>
        </c:forEach>
        <c:if test="${not empty componentProperties.trail}">${componentProperties.trail}</c:if>
    </ul>
</div>
