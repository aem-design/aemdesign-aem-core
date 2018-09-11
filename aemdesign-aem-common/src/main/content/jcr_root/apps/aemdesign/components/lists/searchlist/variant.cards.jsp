<div ${componentProperties.componentAttributes}>

    <div class="content">

        <div class="results">
            <c:forEach var="hit" items="${result.hits}" varStatus="status">

                <div class="card card--result">
                    <c:if test="${hit.extension != \"\" && hit.extension != \"html\"}">
                        <div class="card-file-type">${hit.extension}</div>
                    </c:if>
                    <div class="card-body">
                        <div class="card-product-name">${hit.title}</div>
                        <c:if test="${not empty hit.properties and not empty hit.properties.subtitle}">
                            <h5 class="card-title">${hit.properties.subtitle}</h5>
                        </c:if>
                        <div class="card-text">
                            <div class="excerpt">${hit.excerpt}</div>
                        </div>
                        <div class="card-buttons">
                            <c:choose>
                                <c:when test="${hit.extension != \"\" && hit.extension != \"html\"}">
                                    <c:set var="actionText" value="${componentProperties.assetActionText}"/>
                                </c:when>
                                <c:when test="${hit.extension == \"html\"}">
                                    <c:set var="actionText" value="${componentProperties.pageActionText}"/>
                                </c:when>
                                <c:otherwise>
                                    <c:set var="actionText" value="${componentProperties.otherActionText}"/>
                                </c:otherwise>
                            </c:choose>
                            <a href="${hit.URL}" class="btn btn-link">${actionText}</a>
                        </div>
                    </div>

                </div>
            </c:forEach>

        </div>

        <%@ include file="searchlist.pagination.jsp" %>

    </div>
</div>