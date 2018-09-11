<div ${componentProperties.componentAttributes}>
    <ul>
        <li class="previous">
            <c:choose>
                <c:when test="${fn:length(componentProperties.previousPages) > 0}" >
                    <a href="${componentProperties.previousPages[0]}" title="${componentProperties.prevLinkText}">${componentProperties.prevLinkText}</a>
                </c:when>
                <c:otherwise>
                    <span>${componentProperties.prevLinkText}</span>
                </c:otherwise>
            </c:choose>
        </li>
        <li class="all">
            <a href="${componentProperties.backPage}" title="${componentProperties.backLinkText}">${componentProperties.backLinkText}</a>
        </li>
        <li class="next">
            <c:choose>
                <c:when test="${fn:length(componentProperties.nextPages) > 0}" >
                    <a href="${componentProperties.nextPages[0]}" title="${componentProperties.nextLinkText}">${componentProperties.nextLinkText}</a>
                </c:when>
                <c:otherwise>
                    <span>${componentProperties.nextLinkText}</span>
                </c:otherwise>
            </c:choose>
        </li>
    </ul>
</div>
