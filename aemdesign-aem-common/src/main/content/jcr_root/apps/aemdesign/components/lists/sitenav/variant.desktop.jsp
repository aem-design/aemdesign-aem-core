<header role="banner" class="dropdown_nav topmenu">
    <c:set var="menuColor" value=""/>
    <nav id="site_nav" role="navigation" aria-labelledby="firstLabel" data-modules='sitenav' current-menu-color="${componentProperties.currentMenuColor}">
        <h2 span id="firstLabel" class="hidden">${componentProperties.mainMenu}</h2>
        <div class="wrapper">
            <c:if test="${not empty(componentProperties.menuItems)}">
                <ul>
                <c:forEach items="${componentProperties.menuItems}" var="link">
                    <c:set var="activeCssD" value=""/>
                    <c:if test="${link.current}">
                        <c:set var="activeCssD" value=" active"/>
                        <c:set var="sectionNav" value="${link.children}"/>
                        <c:set var="menuColor" value=" ${link.menuColor}"/>
                    </c:if>
                    <li class="${link.menuColor}${activeCssD}">
                        <c:choose>
                            <c:when test="<%= CURRENT_WCMMODE == WCMMode.EDIT %>">
                                <a href="${link.authHref}" current="${link.current}" title="${componentProperties.linkTitlePrefix}${" "}${link.title}" accesskey="${link.accesskey}">${link.title}</a>
                            </c:when>
                            <c:otherwise>
                                <a href="${link.href}" current="${link.current}" title="${componentProperties.linkTitlePrefix}${" "}${link.title}" accesskey="${link.accesskey}">${link.title}</a>
                            </c:otherwise>
                        </c:choose>
                    </li>
                </c:forEach>
                </ul>
            </c:if>

        </div>
    </nav>
    <nav class="nav_preview" role="navigation" aria-labelledby="secondLabel">
        <h2 span id="secondLabel" class="hidden">${componentProperties.subMenu}</h2>
        <div class="target${menuColor}">
            <div class="wrapper">
                <c:if test="${not empty(sectionNav)}">
                    <ul>
                    <c:forEach items="${sectionNav}" var="link">
                        <c:set var="activeCssP" value=""/>
                        <c:if test="${link.current}">
                            <c:set var="activeCssP" value=" active"/>
                        </c:if>

                        <li class="twin${activeCssP}">
                            <c:choose>
                                <c:when test="<%= CURRENT_WCMMODE == WCMMode.EDIT %>">
                                    <a href="${link.authHref}" current="${link.current}" title="${componentProperties.linkTitlePrefix}${" "}${link.title}">${link.title}</a>
                                </c:when>
                                <c:otherwise>
                                    <a href="${link.href}" current="${link.current}" title="${componentProperties.linkTitlePrefix}${" "}${link.title}">${link.title}</a>
                                </c:otherwise>
                            </c:choose>
                        </li>
                    </c:forEach>
                    </ul>
                </c:if>

            </div>
        </div>
    </nav>

    <c:if test="${componentProperties.showLangNav}">
        <cq:include path="langnav" resourceType="aemdesign/components/lists/langnav"/>
    </c:if>

    <c:if test="${componentProperties.showBackToTop}">
    <div id="page_anchor">
        <a href="#" title="${componentProperties.goToTopOfPage}">${componentProperties.goToTopOfPage}</a>
    </div>
    </c:if>
</header>