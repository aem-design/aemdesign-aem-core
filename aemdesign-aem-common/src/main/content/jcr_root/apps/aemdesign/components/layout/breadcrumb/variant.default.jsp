<nav ${componentProperties.componentAttributes}>
    <ol class="breadcrumb">
        <c:forEach items="${componentProperties.values}" var="link" varStatus="loop">
            <c:if test="${not empty componentProperties.delimiter}">${componentProperties.delimiter}</c:if>
            <li class="breadcrumb-item${loop.last ? ' active' : ''}"><a href="${link.url}">${link.title}</a></li>
        </c:forEach>
        ${componentProperties.trail ? componentProperties.trail : ''}
    </ol>
</nav>
