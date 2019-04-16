<div ${componentProperties.componentAttributes} empty>
    <c:if test="${WCMMODE_EDIT == CURRENT_WCMMODE}">
        <c:choose>
            <c:when test="${MODE_TOUCHUI}">
                ${DEFAULT_TOUCH_PLACEHOLDER_IMAGE}
            </c:when>
            <c:otherwise>
                ${DEFAULT_CLASSIC_PLACEHOLDER_IMAGE}
            </c:otherwise>
        </c:choose>
    </c:if>
</div>