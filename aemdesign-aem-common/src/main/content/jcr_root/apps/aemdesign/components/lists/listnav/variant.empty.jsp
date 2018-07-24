<div ${componentProperties.componentAttributes} empty>
<c:if test="${CURRENT_WCMMODE == WCMMODE_EDIT}">
    <c:if test="${empty componentProperties.filterPage}">
        <div class="component notfound">${componentProperties.missingFilterText}</div>
    </c:if>
    <c:if test="${ fn:length(nextPages) == 0 && fn:length(previousPages) == 0}">
        <div class="component notfound">${componentProperties.noResultsText}</div>
    </c:if>
</c:if>
</div>