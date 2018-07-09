<div ${componentProperties.componentAttributes} empty>
<c:if test="${CURRENT_WCMMODE == WCMMODE_EDIT}">
    <c:if test="${empty componentProperties.filterPage}">
        <p class="component notfound">${componentProperties.missingFilterText}</p>
    </c:if>
    <c:if test="${ fn:length(nextPages) == 0 && fn:length(previousPages) == 0}">
        <p class="component notfound">${componentProperties.noResultsText}</p>
    </c:if>
</c:if>
</div>