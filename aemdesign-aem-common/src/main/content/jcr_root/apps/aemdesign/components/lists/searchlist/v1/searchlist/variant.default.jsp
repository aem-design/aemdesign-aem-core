<div ${componentProperties.componentAttributes}>
    <div class="content">
        <div class="results">
            <c:if test="${componentProperties.printStructure}">
                <${componentProperties.listTag}>
            </c:if>

            <c:forEach items="${results}" var="result" varStatus="itemStatus">
                <c:set var="resultItem" value="${result}" scope="request" />

                <c:if test="${componentProperties.printStructure}">
                    <li>
                </c:if>

                <c:choose>
                    <c:when test="${not empty resultItem.damAsset}">
                        <%@ include file="badge.asset.jsp" %>
                    </c:when>
                    <c:when test="${resultItem.pageDetails}">
                        <%@ include file="badge.details.jsp" %>
                    </c:when>
                    <c:otherwise>
                        <%@ include file="badge.default.jsp" %>
                    </c:otherwise>
                </c:choose>

                <c:if test="${componentProperties.printStructure}">
                    </li>
                </c:if>

                <c:if test="${componentProperties.listSplit}">
                    <c:if test="${(itemStatus.index + 1) % componentProperties.listSplitEvery == 0 && !itemStatus.last}">
                        <c:if test="${componentProperties.printStructure}">
                            </${componentProperties.listTag}>
                            <!--split-->
                            <${componentProperties.listTag}>
                        </c:if>
                    </c:if>
                </c:if>
            </c:forEach>

            <c:if test="${componentProperties.printStructure}">
                </${componentProperties.listTag}>
            </c:if>
        </div>

        <%--<%@ include file="searchlist.pagination.jsp" %>--%>
    </div>
</div>