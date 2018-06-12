<c:if test="${result.facets.tags.containsHit}">
    <div class="facets">

        <p><%=i18n.get("Tags")%></p>
        <ul>
            <c:forEach var="bucket" items="${result.facets.tags.buckets}">
                <c:set var="bucketValue" value="${bucket.value}"/>
                <c:set var="tag" value="<%= _tagManager.resolve((String) pageContext.getAttribute("bucketValue")) %>"/>
                <li>
                    <c:if test="${tag != null}">
                        <c:set var="label" value="${tag.title}"/>
                        <c:choose>
                            <c:when test="<%= request.getParameter("tag") != null && java.util.Arrays.asList(request.getParameterValues("tag")).contains(pageContext.getAttribute("bucketValue")) %>">
                                ${label} (${bucket.count}) - <a title="filter results" href="<c:url value="${currentPage.path}.html"><c:param name="q" value="${escapedQueryForHref}"/></c:url>">remove filter</a>
                            </c:when>
                            <c:otherwise>
                                <a title="filter results" href="
                                    <c:url value="${currentPage.path}.html">
                                        <c:param name="q" value="${escapedQueryForHref}"/>
                                        <c:param name="tag" value="${bucket.value}"/>
                                    </c:url>">
                                        ${label} (${bucket.count})
                                </a>
                            </c:otherwise>
                        </c:choose>
                    </c:if>
                </li>
            </c:forEach>
        </ul>
    </div>
</c:if>
