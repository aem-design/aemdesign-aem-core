<div ${componentProperties.componentAttributes} itemscope itemtype="http://schema.org/ImageObject">
    <c:if test="${not empty componentProperties.imageURL}">
        <c:set var="imageAttr" value="class=\"card-img-top\""/>
        <%@include file="image.select.simple.jsp" %>
    </c:if>
    <div class="card-block">
        <c:if test="${not empty componentProperties.linkURL}">
        <a href="${componentProperties.linkURL}">
        </c:if>
        <c:if test="${not empty componentProperties['dc:title']}">
        <${componentProperties.titleType} class="card-title" id="${componentProperties.ariaLabelledBy}">${componentProperties['dc:title']}</${componentProperties.titleType}>
        </c:if>
        <c:if test="${not empty componentProperties.linkURL}">
        </a>
        </c:if>
        <c:if test="${not empty componentProperties['dc:description']}">
        <div class="card-text">${componentProperties['dc:description']}</div>
        </c:if>
    </div>
    <c:if test="${not empty componentProperties.licenseInfo}">
    <div class="card-footer">
        <span class="text-muted license">${componentProperties.licenseInfo}</span>
    </div>
    </c:if>
</div>