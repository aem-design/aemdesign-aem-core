<ul ${componentProperties.componentAttributes}${extraAttr}>

    <c:if test="${not empty(componentProperties.menuItems)}">

        <c:forEach items="${componentProperties.menuItems}" var="linkL1">

            <c:set var="itemCSS" value=""/>
            <c:if test="${linkL1.hasChildren}">
                <c:set var="itemCSS" value=" dropdown"/>
            </c:if>

            <c:if test="${linkL1.current}">
                <c:set var="itemCSS" value="${itemCSS} current"/>
            </c:if>

            <li class="nav-item${itemCSS}">
                <c:if test="${linkL1.hasChildren}">
                    <a class="nav-link dropdown-toggle l-1" href="#" id="${linkL1.name}" data-toggle="dropdown">${linkL1.title}</a>
                    <div class="dropdown-menu brand-header__drop-categories" aria-labelledby="${linkL1.name}">
                        <c:forEach items="${linkL1.children}" var="linkL2">
                            <c:set var="linkL2Href" value="${linkL2.href}"/>
                            <c:if test="${CURRENT_WCMMODE == WCMMODE_EDIT}">
                                <c:set var="linkL2Href" value="${linkL2.authHref}"/>
                            </c:if>

                            <div class="brand-header__drop-category">
                            <a class="parent l-2" href="${linkL2Href}">${linkL2.title}</a>
                            <c:forEach items="${linkL2.children}" var="linkL3">
                                <c:set var="linkL3Href" value="${linkL3.href}"/>
                                <c:if test="${CURRENT_WCMMODE == WCMMODE_EDIT}">
                                    <c:set var="linkL3Href" value="${linkL3.authHref}"/>
                                </c:if>
                                <a class="l-3" href="${linkL3Href}">${linkL3.title}</a>
                            </c:forEach>
                            </div>

                        </c:forEach>
                    </div>
                </c:if>
                <c:if test="${not linkL1.hasChildren}">
                    <a class="nav-link l-1" href="#">${linkL1.title}</a>
                </c:if>
            </li>
        </c:forEach>
    </c:if>

</ul>
