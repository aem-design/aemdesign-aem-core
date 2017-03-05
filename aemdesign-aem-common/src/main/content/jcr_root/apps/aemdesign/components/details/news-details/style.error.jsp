<div class="${componentProperties.cssClassRow}">
</div>
<c:if test="<%= CURRENT_WCMMODE == WCMMode.EDIT %>">
    <c:if test="${empty componentProperties.publishDate}">
        <p class="cq-info"><small>Missing Publish Date</small></p>
    </c:if>
    <c:if test="${fn:length(componentProperties.tags) == 0}">
        <p class="cq-info"><small>Missing Category Tag</small></p>
    </c:if>

</c:if>