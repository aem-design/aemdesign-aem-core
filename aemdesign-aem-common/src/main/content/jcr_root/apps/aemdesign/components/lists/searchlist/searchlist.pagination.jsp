
<c:if test="${fn:length(result.resultPages) > 1}">
    <div class="pagination">
        <div class="pagination-title">${componentProperties.resultPagesText}</div>
        <div class="pages">
            <ul>
                <c:if test="${result.previousPage != null}">
                    <li class="previous"><a href="${result.previousPage.URL}">${componentProperties.previousText}</a></li>
                </c:if>
                <c:forEach var="page" items="${result.resultPages}">
                    <li<c:if test="${page.currentPage}"> class="current"</c:if>>
                        <c:choose>
                            <c:when test="${page.currentPage}">${page.index + 1}</c:when>
                            <c:otherwise>
                                <a href="${page.URL}">${page.index + 1}</a>
                            </c:otherwise>
                        </c:choose>
                    </li>
                </c:forEach>
                <c:if test="${result.nextPage != null}">
                    <li class="next"><a href="${result.nextPage.URL}">${componentProperties.nextText}</a></li>
                </c:if>
            </ul>
        </div>

    </div>
</c:if>