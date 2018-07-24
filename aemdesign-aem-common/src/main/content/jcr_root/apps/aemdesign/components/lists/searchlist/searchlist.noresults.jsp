<c:if test="${result.spellcheck != null}">
    <div class="spellcheck">
        <div class="text">${componentProperties.spellcheckText}</div>
        <a class="link" href="<c:url value="${currentPage.path}.html"><c:param name="q" value="${result.spellcheck}"/></c:url>">
            <b><c:out value="${result.spellcheck}"/></b>
        </a>
    </div>
</c:if>
<div class="no-results">${componentProperties.noResultsText}</div>
<span data-tracking="{event:'noresults', values:{'keyword': '<c:out value="${escapedQuery}"/>', 'results':'zero', 'executionTime':'${result.executionTime}'}, componentPath:'${componentProperties.componentPath}'}"></span>
${result.trackerScript}
