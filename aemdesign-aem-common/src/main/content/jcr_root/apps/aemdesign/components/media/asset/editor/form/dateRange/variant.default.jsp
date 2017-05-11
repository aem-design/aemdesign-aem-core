<c:if test="${(not empty componentProperties.fromDate || not empty componentProperties.toDate) || (CURRENT_WCMMODE == WCMMODE_EDIT) }">
    <dt role="columnheader"><strong>${componentProperties.title}</strong></dt>
    <dd role="gridcell">
        <c:if test="${not empty componentProperties.fromDate}">
            <fmt:formatDate value="${componentProperties.fromDate.time}" pattern="${componentProperties.fromDatePattern}" />
        </c:if>
        <c:if test="${not empty componentProperties.fromDate && not empty componentProperties.toDate}">
            ${componentProperties.separator}
        </c:if>
        <c:if test="${not empty componentProperties.toDate}">
            <fmt:formatDate value="${componentProperties.toDate.time}" pattern="${componentProperties.toDatePattern}" />
        </c:if>

    </dd>
</c:if>