<div ${componentProperties.componentAttributes}>
    <figure>
        <%@include file="image.select.jsp" %>
        <figcaption>
            <div class="description" itemprop="description">${componentProperties['dc:description']}</div>
            <c:if test="${not empty componentProperties.licenseInfo}">
                <small class="text-muted license">${componentProperties.licenseInfo}</small>
            </c:if>
        </figcaption>
    </figure>
</div>
