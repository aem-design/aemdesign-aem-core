<div class="styled-overlay ${componentProperties.displayStyle}">
    <c:if test="${componentProperties.useParentPageTitle}">
        <h2>${componentProperties.parentPageTitle}</h2>
    </c:if>
    <h3>${componentProperties.title}</h3>
    <div class="${componentProperties.cssClassRow}">
        <cq:include path="par" resourceType="foundation/components/parsys"/>
    </div>
</div>