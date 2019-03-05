<c:choose>
    <c:when test="${not empty componentProperties.linkIcon}">
        <button ${componentProperties.componentAttributes}>
            <c:if test="${componentProperties.linkIconDirection eq 'left'}">
                <i class="icon ${fn:join(componentProperties.linkIcon, ' ')}"></i>
            </c:if>
            <span class="link-text">${componentProperties.label}</span>
            <c:if test="${componentProperties.linkIconDirection eq 'right'}">
                <i class="icon ${fn:join(componentProperties.linkIcon, ' ')}"></i>
            </c:if>
        </button>
    </c:when>
    <c:otherwise>
        <button ${componentProperties.componentAttributes}>
            <span class="link-text">${componentProperties.label}</span>
        </button>
    </c:otherwise>
</c:choose>
