<div ${componentProperties.componentAttributes}>
    <figure>
        <c:if test="${not empty componentProperties.linkURL}">
        <a href="${componentProperties.linkURL}">${componentProperties['dc:title']}</a>
        </c:if>
        <%@include file="image.select.jsp" %>
        <figcaption>
            ${componentProperties['dc:title']}
        </figcaption>
        <div class="desc">${componentProperties['dc:description']}</div>
        <c:if test="${not empty componentProperties.licenseInfo}">
            <small class="text-muted license">${componentProperties.licenseInfo}</small>
        </c:if>
    </figure>
</div>
