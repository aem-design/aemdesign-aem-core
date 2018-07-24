<div ${componentProperties.componentAttributes}>
    <c:choose>
        <c:when test="${not empty componentProperties.redirectTarget}">
            ${componentProperties.redirectIsSet}
            <a href="${componentProperties.redirectUrl}">${componentProperties.redirectTitle}</a>
        </c:when>
        <c:otherwise>
            ${componentProperties.redirectIsNotSet}
        </c:otherwise>
    </c:choose>
</div>