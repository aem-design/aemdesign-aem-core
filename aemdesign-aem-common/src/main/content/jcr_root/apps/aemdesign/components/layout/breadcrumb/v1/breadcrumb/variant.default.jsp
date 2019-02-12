<nav ${componentProperties.componentAttributes}>
    <ol class="breadcrumb" itemscope itemtype="http://schema.org/BreadcrumbList">
        <c:forEach items="${componentProperties.values}" var="link" varStatus="loop">
            <c:if test="${not empty componentProperties.delimiter}">${componentProperties.delimiter}</c:if>
            <li itemprop="itemListElement" itemscope itemtype="http://schema.org/ListItem"
                class="breadcrumb-item${loop.last ? ' active' : ''}">
                <a itemscope itemtype="http://schema.org/Thing" itemprop="item" href="${link.url}">
                    <span itemprop="name">${link.title}</span></a>
                <meta itemprop="position" content="${loop.index+1}" />
            </li>
        </c:forEach>
        ${componentProperties.trail ? componentProperties.trail : ''}
    </ol>
</nav>
