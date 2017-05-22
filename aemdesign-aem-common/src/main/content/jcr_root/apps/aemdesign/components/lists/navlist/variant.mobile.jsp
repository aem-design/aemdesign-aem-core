<nav ${componentProperties.componentAttributes}>
    <ul>
        <c:if test="${componentProperties.showSearch}">
            <li>
                <cq:include path="langnav" resourceType="aemdesign/components/widgets/search"/>
            </li>
        </c:if>

        <c:if test="${not empty(componentProperties.menuItems)}">
            <c:forEach items="${componentProperties.menuItems}" var="link">
                <c:set var="activeCssM" value=""/>
                <c:set var="secondNav" value=""/>

                <c:if test="${not empty(link.children)}">
                    <c:set var="secondNav" value="${link.children}"/>
                </c:if>
                <c:if test="${link.current}">
                    <c:set var="activeCssM" value=" active"/>
                </c:if>
                <c:set var="inactiveStyle" value=""/>
                <c:if test="${not link.current}">
                    <c:set var="inactiveStyle" value="display: none;"/>
                </c:if>

                <li class="${link.menuColor}${activeCssM}">
                    <c:choose>
                        <c:when test="<%= CURRENT_WCMMODE == WCMMode.EDIT %>">
                            <a href="${link.authHref}" title="${componentProperties.linkTitlePrefix}${" "}${link.title}"><span>${link.title}</span></a>
                        </c:when>
                        <c:otherwise>
                            <a href="${link.href}" title="${componentProperties.linkTitlePrefix}${" "}${link.title}"><span>${link.title}</span></a>
                        </c:otherwise>
                    </c:choose>

                    <c:if test="${not empty(secondNav)}">
                        <ul style="${inactiveStyle}">
                            <c:forEach items="${secondNav}" var="link2">
                                <c:set var="activeCss2" value=""/>
                                <c:set var="thirdNav" value=""/>

                                <c:if test="${link2.current}">
                                    <c:set var="activeCss2" value="active"/>
                                </c:if>
                                <c:if test="${not empty(link2.children)}">
                                    <c:set var="thirdNav" value="${link2.children}"/>
                                </c:if>

                                <li class="${activeCss2}">
                                    <c:choose>
                                        <c:when test="<%= CURRENT_WCMMODE == WCMMode.EDIT %>">
                                            <a href="${link2.authHref}" title="${componentProperties.linkTitlePrefix}${" "}${link2.title}"><span>${link2.title}</span></a>
                                        </c:when>
                                        <c:otherwise>
                                            <a href="${link2.href}" title="${componentProperties.linkTitlePrefix}${" "}${link2.title}"><span>${link2.title}</span></a>
                                        </c:otherwise>
                                    </c:choose>

                                    <c:if test="${not empty(thirdNav)}">
                                        <ul>
                                            <c:forEach items="${thirdNav}" var="link3">
                                                <c:set var="activeCss3" value=""/>
                                                <c:set var="fourthNav" value=""/>

                                                <c:if test="${link3.current}">
                                                    <c:set var="activeCss3" value="active"/>
                                                </c:if>
                                                <c:if test="${not empty(link3.children)}">
                                                    <c:set var="fourthNav" value="${link3.children}"/>
                                                </c:if>

                                                <li class="${activeCss3}">
                                                    <c:choose>
                                                        <c:when test="<%= CURRENT_WCMMODE == WCMMode.EDIT %>">
                                                            <a href="${link3.authHref}" title="${componentProperties.linkTitlePrefix}${" "}${link3.title}"><span>${link3.title}</span></a>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <a href="${link3.href}" title="${componentProperties.linkTitlePrefix}${" "}${link3.title}"><span>${link3.title}</span></a>
                                                        </c:otherwise>
                                                    </c:choose>

                                                    <c:if test="${link3.current && not empty(fourthNav)}">
                                                        <ul>
                                                            <c:forEach items="${fourthNav}" var="link4">
                                                                <c:set var="activeCss4" value=""/>
                                                                <c:set var="fifthNav" value=""/>

                                                                <c:if test="${link4.current}">
                                                                    <c:set var="activeCss4" value="active"/>
                                                                </c:if>
                                                                <c:if test="${not empty(link4.children)}">
                                                                    <c:set var="fifthNav" value="${link4.children}"/>
                                                                </c:if>

                                                                <li class="${activeCss4}">
                                                                    <c:choose>
                                                                        <c:when test="<%= CURRENT_WCMMODE == WCMMode.EDIT %>">
                                                                            <a href="${link4.authHref}" title="${componentProperties.linkTitlePrefix}${" "}${link4.title}"><span>${link4.title}</span></a>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <a href="${link4.href}" title="${componentProperties.linkTitlePrefix}${" "}${link4.title}"><span>${link4.title}</span></a>
                                                                        </c:otherwise>
                                                                    </c:choose>

                                                                </li>
                                                            </c:forEach>
                                                        </ul>
                                                    </c:if>

                                                </li>
                                            </c:forEach>
                                        </ul>
                                    </c:if>

                                </li>
                            </c:forEach>
                        </ul>
                    </c:if>

                </li>
            </c:forEach>
        </c:if>
    </ul>

    <c:if test="${componentProperties.showLangNav}">
        <cq:include path="langnav" resourceType="aemdesign/components/lists/langnav"/>
    </c:if>
</nav>
