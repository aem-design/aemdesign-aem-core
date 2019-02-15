<nav ${componentProperties.componentAttributes}>
    <button class="navbar-toggler navbar-toggler-right"
            type="button"
            data-toggle="collapse"
            data-target="#${componentProperties.componentId}Content"
            aria-controls="${componentProperties.componentId}Content"
            aria-expanded="false"
            aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
    </button>
    <a class="navbar-button" href="#">${componentProperties.menuTitle}</a>
<c:if test="${not empty(componentProperties.menuItems)}">
    <div class="collapse navbar-collapse" id="${componentProperties.componentId}Content">
    <ul class="menu" itemscope itemtype="http://www.schema.org/SiteNavigationElement">
    <c:forEach items="${componentProperties.menuItems}" var="link">
        <c:set var="activeCssSimple" value=""/>
        <c:if test="${link.current}">
            <c:set var="activeCssSimple" value=" active"/>
            <c:set var="sectionNav" value="${link.children}"/>
        </c:if>
        <li itemprop="name" class="nav-item<c:out value="${link.current ? ' active' : ''}"/>">
            <c:choose>
                <c:when test="${CURRENT_WCMMODE == WCMMODE_EDIT}">
                    <a itemprop="url" href="${link.authHref}"
                        class="nav-link"
                        title="${componentProperties.linkTitlePrefix}${" "}${link.title}"
                        <c:out value="${not empty link.accesskey ? ' accesskey=\"${link.accesskey}\"' : ''}"/>
                    >${link.title}<c:out value="${link.current ? ' <span class=\"sr-only\">(current)</span>' : ''}"/></a>
                </c:when>
                <c:otherwise>
                    <a itemprop="url" href="${link.href}"
                        class="nav-link"
                        title="${componentProperties.linkTitlePrefix}${" "}${link.title}"
                        <c:out value="${not empty link.accesskey ? ' accesskey=\"${link.accesskey}\"' : ''}"/>
                    >${link.title}<c:out value="${link.current ? ' <span class=\"sr-only\">(current)</span>' : ''}"/></a>
                </c:otherwise>
            </c:choose>
        </li>
    </c:forEach>
    </ul>
    </div>
</c:if>
</nav>


