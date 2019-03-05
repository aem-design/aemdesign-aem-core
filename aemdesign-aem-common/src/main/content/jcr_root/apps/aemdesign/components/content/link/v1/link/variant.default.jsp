<c:choose>
    <c:when test="${not empty componentProperties.linkIcon}">
        <a ${componentProperties.componentAttributes}>
            <c:if test="${componentProperties.linkIconPosition eq 'left'}">
                <i class="icon ${fn:join(componentProperties.linkIcon, ' ')}"></i>
            </c:if>
            <span class="link-text">${componentProperties.label}</span>
            <c:if test="${componentProperties.linkIconPosition eq 'right'}">
                <i class="icon ${fn:join(componentProperties.linkIcon, ' ')}"></i>
            </c:if>
        </a>
    </c:when>
    <c:otherwise>
        <a ${componentProperties.componentAttributes}>
            <span class="link-text">${componentProperties.label}</span>
        </a>
    </c:otherwise>
</c:choose>
