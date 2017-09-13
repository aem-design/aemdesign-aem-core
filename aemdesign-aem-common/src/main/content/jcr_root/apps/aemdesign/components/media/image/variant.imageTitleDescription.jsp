<div ${componentProperties.componentAttributes}>
    <figure>
        <c:if test="${not empty componentProperties.linkURL}">
        <a href="${componentProperties.linkURL}">${componentProperties['dc:title']}</a>
        </c:if>
        <%@include file="image.select.jsp" %>
        <figcaption>
            <c:if test="${not empty componentProperties.linkURL}">
            <a href="${componentProperties.linkURL}" title="${componentProperties['dc:title']}">
            </c:if>
            ${componentProperties['dc:title']}
            <c:if test="${not empty componentProperties.linkURL}">
            </a>
            </c:if>
            <p>${componentProperties['dc:description']}</p>
            <c:if test="${not empty componentProperties.licenseInfo}">
            <small class="text-muted license">${componentProperties.licenseInfo}</small>
            </c:if>
        </figcaption>
    </figure>
</div>
