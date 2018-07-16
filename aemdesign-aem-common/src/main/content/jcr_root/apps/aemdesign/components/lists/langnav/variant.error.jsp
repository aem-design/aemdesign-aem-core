<c:if test="<%= CURRENT_WCMMODE == WCMMode.EDIT %>">

    <c:if test="${empty componentProperties.filterPage}">
        <div class="component notfound">Missing Filter Page or Wrong Content Type</div>
    </c:if>
    <c:if test="${ fn:length(nextPages) == 0 && fn:length(previousPages) == 0}">
        <div class="component notfound">No result found</div>
    </c:if>

</c:if>