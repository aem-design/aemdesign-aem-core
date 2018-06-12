<div ${componentProperties.componentAttributes}>

    <%@ include file="searchlist.tracking.jsp" %>

    <div class="results-total">
        ${componentProperties.statisticsText}
    </div>

    <div class="content">

        <%@ include file="searchlist.trends.jsp" %>
        <%@ include file="searchlist.facets.jsp" %>
        <%@ include file="searchlist.related.jsp" %>

        <div class="results">
            <c:forEach var="hit" items="${result.hits}" varStatus="status">
                <c:if test="${hit.extension != \"\" && hit.extension != \"html\"}">
                    <span class="icon type_${hit.extension}"><img src="/etc/clientlibs/aemdesign/icons/file/${hit.extension}.gif" alt="*"></span>
                </c:if>
                <div class="result-item">
                    <div class="result-item-title">
                        <a href="${hit.URL}" onclick="trackSelectedResult(this, ${status.index + 1})">${hit.title}</a>
                    </div>
                    <div class="result-item-excerpt-link">
                        <a href="${hit.URL}">${hit.URL}</a>
                    </div>

                    <div class="result-item-excerpt">${hit.excerpt}</div>

                </div>

            </c:forEach>

        </div>

        <%@ include file="searchlist.pagination.jsp" %>

    </div>
</div>