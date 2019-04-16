<ul ${componentProperties.componentAttributes} itemscope itemtype="http://www.schema.org/SiteNavigationElement">
<c:forEach items="${componentProperties.menuItems}" var="link">
    <c:set var="activeCssSimple" value=""/>
    <c:if test="${link.current}">
        <c:set var="activeCssSimple" value=" active"/>
    </c:if>
    <li itemprop="name" class="nav-item<c:out value="${link.current ? ' active' : ''}"/>">
        <a itemprop="url" href="${link.authHref}"
            class="nav-link${not empty activeCssSimple ? activeCssSimple : ''}"
            <c:out value="${not empty link.accesskey ? ' accesskey=\"${link.accesskey}\"' : ''}"/>
            title="${componentProperties.linkTitlePrefix}${" "}${link.title}">
            ${link.title}<c:if test="${link.current}"><span class="sr-only">(current)</span></c:if>
        </a>
    </li>
</c:forEach>
</ul>


