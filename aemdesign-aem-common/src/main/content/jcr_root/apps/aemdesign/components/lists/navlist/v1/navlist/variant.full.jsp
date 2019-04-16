<c:if test="${not empty(componentProperties.menuItems)}">
    <c:forEach items="${componentProperties.menuItems}" var="link">
        <c:set var="secondNav" value=""/>
        <c:set var="activeCssM" value=""/>
        <c:set var="inactiveStyle" value=""/>

        <c:if test="${not empty(link.children)}">
            <c:set var="secondNav" value="${link.children}"/>
        </c:if>
        <c:if test="${link.current}">
            <c:set var="activeCssM" value=" active"/>
        </c:if>
        <c:if test="${not link.current}">
            <c:set var="inactiveStyle" value="display: none;"/>
        </c:if>

        <c:if test="${not empty(secondNav)}">
            <c:forEach items="${secondNav}" var="link2">
                <c:set var="thirdNav" value=""/>
                <c:set var="activeCss2" value=""/>
                <c:set var="inactiveStyle2" value=""/>

                <c:if test="${not empty(link2.children)}">
                    <c:set var="thirdNav" value="${link2.children}"/>
                </c:if>
                <c:if test="${link2.current}">
                    <c:set var="activeCss2" value="active"/>
                </c:if>
                <c:if test="${not link2.current}">
                    <c:set var="inactiveStyle2" value="display: none;"/>
                </c:if>

                <h2 style="${inactiveStyle2}"><c:choose>
                    <c:when test="${CURRENT_WCMMODE == WCMMODE_EDIT}">
                        <a href="${link2.authHref}" title="${componentProperties.linkTitlePrefix}${" "}${link2.title}"><span>${link2.title}</span></a>
                    </c:when>
                    <c:otherwise>
                        <a href="${link2.href}" title="${componentProperties.linkTitlePrefix}${" "}${link2.title}"><span>${link2.title}</span></a>
                    </c:otherwise>
                </c:choose></h2>

                <c:if test="${not empty(thirdNav)}">
                    <ul style="${inactiveStyle2}" itemscope itemtype="http://www.schema.org/SiteNavigationElement">
                        <c:forEach items="${thirdNav}" var="link3">
                            <c:set var="fourthNav" value=""/>
                            <c:set var="activeCss3" value=""/>
                            <c:set var="inactiveStyle3" value=""/>

                            <c:if test="${not empty(link3.children)}">
                                <c:set var="fourthNav" value="${link3.children}"/>
                            </c:if>
                            <c:if test="${link3.current}">
                                <c:set var="activeCss3" value="active"/>
                            </c:if>
                            <c:if test="${not link3.current}">
                                <c:set var="inactiveStyle3" value="display: none;"/>
                            </c:if>

                            <li class="${activeCss3}" itemprop="name">
                                <c:choose>
                                    <c:when test="${CURRENT_WCMMODE == WCMMODE_EDIT}">
                                        <a itemprop="url" href="${link3.authHref}" title="${componentProperties.linkTitlePrefix}${" "}${link3.title}"><span>${link3.title}</span></a>
                                    </c:when>
                                    <c:otherwise>
                                        <a itemprop="url" href="${link3.href}" title="${componentProperties.linkTitlePrefix}${" "}${link3.title}"><span>${link3.title}</span></a>
                                    </c:otherwise>
                                </c:choose>

                                <c:if test="${not empty(fourthNav)}">
                                    <ul style="${inactiveStyle3}">
                                        <c:forEach items="${fourthNav}" var="link4">
                                            <c:set var="fifthNav" value=""/>
                                            <c:set var="activeCss4" value=""/>
                                            <c:set var="inactiveStyle4" value=""/>

                                            <c:if test="${not empty(link4.children)}">
                                                <c:set var="fifthNav" value="${link4.children}"/>
                                            </c:if>
                                            <c:if test="${link4.current}">
                                                <c:set var="activeCss4" value="active"/>
                                            </c:if>
                                            <c:if test="${not link4.current}">
                                                <c:set var="inactiveStyle4" value="display: none;"/>
                                            </c:if>

                                            <li class="${activeCss4}">
                                                <c:choose>
                                                    <c:when test="${CURRENT_WCMMODE == WCMMODE_EDIT}">
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
            </c:forEach>
        </c:if>
    </c:forEach>
</c:if>


