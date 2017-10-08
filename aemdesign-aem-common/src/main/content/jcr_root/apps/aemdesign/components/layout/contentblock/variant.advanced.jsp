<section ${componentProperties.componentAttributes}>
    <div class="content">
        <a name="${componentProperties.instanceName}"></a>
        <c:if test="${componentProperties.hideTitle eq false  }">
            <div class="title">
                <c:choose>
                    <c:when test="${empty componentProperties.listLinksLeft and empty componentProperties.linksLeftTitle}">
                    </c:when>
                    <c:otherwise>
                        <ul class="linksleft float-left">
                            <c:if test="${not empty componentProperties.linksLeftTitle}">
                                <li class="title">${componentProperties.linksLeftTitle}</li>
                            </c:if>
                            <c:forEach items="${componentProperties.linksLeftList}" var="link">
                                <c:choose>
                                    <c:when test="${link.titleIconShow and not empty link.titleIcon}">
                                        <li class="title-icon"><a href="${link.href}"><i class="icon ${link.titleIcon}" title="${link.title}"></i><span class="title">${link.pageNavTitle}</span></a></li>
                                    </c:when>
                                    <c:otherwise>
                                        <li class="title-link"><a href="${link.href}">${link.pageNavTitle}</a></li>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>
                        </ul>
                    </c:otherwise>
                </c:choose>
                <c:choose>
                    <c:when test="${empty componentProperties.listLinksRight and empty componentProperties.linksRightTitle}">
                    </c:when>
                    <c:otherwise>
                        <ul class="linksright float-right">
                            <c:if test="${not empty componentProperties.linksRightTitle}">
                                <li class="title">${componentProperties.linksRightTitle}</li>
                            </c:if>
                            <c:forEach items="${componentProperties.linksRightList}" var="link">
                                <c:choose>
                                    <c:when test="${link.titleIconShow and not empty link.titleIcon}">
                                        <li class="title-icon"><a href="${link.href}"><i class="icon ${link.titleIcon}" title="${link.title}"></i><span class="title">${link.pageNavTitle}</span></a></li>
                                    </c:when>
                                    <c:otherwise>
                                        <li class="title-link"><a href="${link.href}">${link.pageNavTitle}</a></li>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>
                        </ul>
                    </c:otherwise>
                </c:choose>
                <c:if test="${not componentProperties.hideTitle and not empty componentProperties.title}">
                    <${componentProperties.titleType} class="heading" id="${componentProperties.ariaLabelledBy}">${componentProperties.title}</${componentProperties.titleType}>
                </c:if>
            </div>
        </c:if>
        <div class="contents">
            <cq:include path="par" resourceType="foundation/components/parsys"/>
        </div>
    </div>
    <c:if test="${not componentProperties.hideTopLink}">
        <a href="#top" target="_self" title="${componentProperties.topLinkTitle}" class="back-to-top">${componentProperties.topLinkLabel}</a>
    </c:if>
</section>

