<c:if test="${not empty componentProperties.imageTargetURL && componentProperties.variant ne 'imageTitleDescription'}">
    <a href="${componentProperties.imageTargetURL}">
</c:if>
<div data-picture data-alt="${componentProperties.image.alt}">
    <c:forEach var="rendition" items="${componentProperties.responsiveImageSet}">
        <div data-src="${rendition.value}" data-media="(min-width: ${rendition.key}px)"></div>
    </c:forEach>
    <%-- Fallback content for non-JS browsers. Same img src as the initial, unqualified source element. --%>
    <noscript>
        <img src="${componentProperties.image.fileReference}" alt='${componentProperties.image.alt}' title='${componentProperties.image.title}'>
    </noscript>
</div>
<c:if test="${not empty componentProperties.imageTargetURL && componentProperties.variant ne 'imageTitleDescription'}">
    </a>
</c:if>