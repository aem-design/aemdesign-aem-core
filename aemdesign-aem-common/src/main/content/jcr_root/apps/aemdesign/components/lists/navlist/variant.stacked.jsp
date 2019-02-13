<ul ${componentProperties.componentAttributes}${extraAttr} itemscope itemtype="http://www.schema.org/SiteNavigationElement">

    <c:if test="${not empty(componentProperties.menuItems)}">

        <c:forEach items="${componentProperties.menuItems}" var="linkL1">

            <c:set var="itemCSS" value=""/>
            <c:set var="itemLinkCSS" value=""/>

            <c:if test="${linkL1.hasChildren}">
                <c:set var="itemCSS" value=" dropdown"/>
            </c:if>

            <c:if test="${linkL1.current}">
                <c:set var="itemCSS" value="${itemCSS} current"/>
                <c:set var="itemLinkCSS" value="${itemLinkCSS} active"/>
            </c:if>

            <li class="nav-item${itemCSS}" itemprop="name">
                <c:if test="${linkL1.hasChildren}">
                    <a class="nav-link dropdown-toggle l-1${itemLinkCSS}" href="#" id="${linkL1.name}" data-toggle="dropdown">${linkL1.title}</a>
                    <div class="dropdown-menu" aria-labelledby="${linkL1.name}">
                        <c:forEach items="${linkL1.children}" var="linkL2">
                            <c:set var="linkL2Href" value="${linkL2.href}"/>
                            <c:set var="childCSS" value=""/>

                            <c:if test="${CURRENT_WCMMODE == WCMMODE_EDIT}">
                                <c:set var="linkL2Href" value="${linkL2.authHref}"/>
                            </c:if>
                            <c:if test="${linkL2.current}">
                                <c:set var="childCSS" value="${childCSS} current"/>
                            </c:if>

                            <a itemprop="url" class="parent l-2${childCSS}" href="${linkL2Href}">${linkL2.title}</a>
                        </c:forEach>
                    </div>
                </c:if>
                <c:if test="${not linkL1.hasChildren}">
                    <a itemprop="url" class="nav-link l-1${itemLinkCSS}" href="${linkL1.href}">${linkL1.title}</a>
                </c:if>
            </li>
        </c:forEach>
    </c:if>

</ul>
