<div ${componentProperties.componentAttributes}>
    <figure>
        <%@include file="image.select.jsp" %>
        <figcaption>
            <div class="description" itemprop="description">${componentProperties['dc:description']}</div>
            <c:if test="${not empty componentProperties.licenseInfo}">
                <span class="text-muted license">${componentProperties.licenseInfo}</span>
            </c:if>
        </figcaption>
    </figure>
</div>
