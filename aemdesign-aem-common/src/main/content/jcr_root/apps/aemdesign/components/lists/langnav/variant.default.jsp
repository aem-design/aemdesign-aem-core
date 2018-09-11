
<div ${componentProperties.componentAttributes}>
    <ul>
        <c:forEach items="${languageToggleMap}" var="langToggle">
            <c:set var="status" value="" />
            <c:if test="${LOCALE eq langToggle.key}">
                <c:set var="status" value=" class=\"active\"" />
            </c:if>
            <li ${status}>
                <a href="${langToggle.value['path']}"
                   lang="${componentProperties.language}"
                   hreflang="${langToggle.value['hreflang']}"
                   title="${langToggle.value['langSwitchTo']}">${langToggle.value['langSimpleTo']}</a>
            </li>
        </c:forEach>
    </ul>
</div>
