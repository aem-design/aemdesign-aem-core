<div ${componentProperties.componentAttributes}>
    <c:if test="${not empty(componentProperties.menuItems)}">
        <ul><!--
         --><c:forEach items="${componentProperties.menuItems}" var="link">
                <c:set var="activeCssSimple" value=""/>
                <c:if test="${link.current}">
                    <c:set var="activeCssSimple" value=" active"/>
                    <c:set var="sectionNav" value="${link.children}"/>
                </c:if><!--
             --><li class="${link.menuColor}${activeCssSimple}">
                    <c:choose>
                        <c:when test="${CURRENT_WCMMODE == WCMMODE_EDIT}">
                            <a href="${link.authHref}" current="${link.current}" title="${componentProperties.linkTitlePrefix}${" "}${link.title}" accesskey="${link.accesskey}">${link.title}</a>
                        </c:when>
                        <c:otherwise>
                            <a href="${link.href}" current="${link.current}" title="${componentProperties.linkTitlePrefix}${" "}${link.title}" accesskey="${link.accesskey}">${link.title}</a>
                        </c:otherwise>
                    </c:choose>
                </li><!--
         --></c:forEach><!--
     --></ul>
    </c:if>
</div>



