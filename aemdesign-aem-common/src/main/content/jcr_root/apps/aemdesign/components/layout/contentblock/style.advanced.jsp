<section ${componentProperties.componentAttributes}>
    <div class="wrapper">
        <a name="${componentProperties.instanceName}"></a>
        <c:if test="${componentProperties.hideTitle eq false  }">
            <div class="${componentProperties.styleTitleContainerClass}">
                <c:choose>
                    <c:when test="${empty componentProperties.listPagesLeft}">
                    </c:when>
                    <c:otherwise>
                        <ul class="left-links">
                            <c:forEach items="${componentProperties.listPagesLeft}" var="link">
                                <c:choose>
                                    <c:when test="${link.showAsTitleIcon}">
                                        <li class="title-icon" >
                                            <div<c:if test="${link.class != ''}" ><c:out value=" class=${link.class}"/></c:if>>
                                                <a href="${link.href}"><img class="titleIcon" src="${link.showAsTitleIconPath}" /></a>
                                            </div>
                                        </li>
                                    </c:when>
                                    <c:otherwise>
                                        <li class="title-link"><a href="${link.href}">${link.title}</a></li>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>
                        </ul>
                    </c:otherwise>
                </c:choose>
                <c:if test="${componentProperties.hideTitleSeparator eq true }">
                    <h2 class="${componentProperties.styleTitleClass}">${componentProperties.title}</h2>
                </c:if>
                <c:choose>
                    <c:when test="${empty componentProperties.listPagesRight}">
                    </c:when>
                    <c:otherwise>
                        <ul class="right-links">
                            <c:forEach items="${componentProperties.listPagesRight}" var="link">
                                <c:choose>
                                    <c:when test="${link.showAsTitleIcon}">
                                        <li class="title-icon">
                                            <div<c:if test="${link.class != ''}" ><c:out value=" class=${link.class}"/></c:if>>
                                                <a href="${link.href}"><img src="${link.showAsTitleIconPath}" /></a>
                                            </div>
                                        </li>
                                    </c:when>
                                    <c:otherwise>
                                        <li class="title-link"><a href="${link.href}">${link.title}</a></li>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>
                            <c:if test="${componentProperties.linksRightTitle!=''}">
                                <li class="title">${componentProperties.linksRightTitle}</li>
                            </c:if>
                        </ul>
                    </c:otherwise>
                </c:choose>
            </div>
        </c:if>
        <div class="par">
            <cq:include path="par" resourceType="foundation/components/parsys"/>
        </div>
    </div>
</section>

<c:if test="${not componentProperties.hideTopLink}">
    <a href="#top" target="_self" title="${componentProperties.topLinkTitle}" class="anchorRightUp">${componentProperties.topLinkLabel}</a>
</c:if>
