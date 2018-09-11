<c:if test="${fn:length(search.relatedQueries) > 0}">
    <div class="related">
        <div class="related-searches">${componentProperties.relatedSearchesText}</div>
        <c:forEach var="rq" items="${search.relatedQueries}">
            <a style="margin-right:10px" href="${currentPage.path}.html?q=${rq}"><c:out value="${rq}"/></a>
        </c:forEach>
    </div>
</c:if>
