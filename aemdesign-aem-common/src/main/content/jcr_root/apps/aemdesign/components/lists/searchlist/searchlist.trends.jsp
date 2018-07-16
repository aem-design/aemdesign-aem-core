<c:if test="${fn:length(trends.queries) > 0}">
    <div class="trends">
        <div class="text">${componentProperties.searchTrendsText}</div>
        <div class="searchTrends">
            <ul>
                <c:forEach var="query" items="${trends.queries}">
                    <li>
                        <a href="<c:url value="${currentPage.path}.html"><c:param name="q" value="${query.query}"/></c:url>"><span style="font-size:${query.size}px"><c:out value="${query.query}"/></span></a>
                    </li>
                </c:forEach>
            </ul>
        </div>
    </div>
</c:if>
