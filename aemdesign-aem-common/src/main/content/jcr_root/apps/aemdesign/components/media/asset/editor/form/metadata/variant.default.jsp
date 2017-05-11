<c:if test="${(fn:length(componentProperties.values) > 0) || (CURRENT_WCMMODE == WCMMODE_EDIT) }">

    <dt role="columnheader"><strong>${componentProperties.title}</strong></dt>
    <dd role="gridcell">
        <c:forEach items="${componentProperties.values}" var="item" varStatus="varStatus" >
            <c:choose>
                <c:when test="${componentProperties['meta/type'] eq 'Tags'}">
                    ${componentProperties.tagMap[item]}
                    <c:if test="${varStatus.last eq false}">
                        ${componentProperties.multiValueSeparator}
                    </c:if>
                </c:when>
                <c:otherwise>
                    ${item}
                </c:otherwise>
            </c:choose>
        </c:forEach>
    </dd>
</c:if>