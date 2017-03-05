<c:if test="<%= CURRENT_WCMMODE == WCMMode.EDIT %>">

    <c:if test="${empty componentProperties.filterPage}">
        <p class="cq-info"><small>Missing Filter Page or Wrong Content Type</small></p>
    </c:if>
    <c:if test="${ fn:length(nextPages) == 0 && fn:length(previousPages) == 0}">
        <p class="cq-info"><small>No result found</small></p>
    </c:if>

</c:if>