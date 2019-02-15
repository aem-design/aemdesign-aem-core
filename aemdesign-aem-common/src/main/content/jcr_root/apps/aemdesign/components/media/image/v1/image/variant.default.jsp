<div ${componentProperties.componentAttributes} itemscope itemtype="http://schema.org/ImageObject">
    <c:if test="${not empty componentProperties.linkURL}">
    <a href="${componentProperties.linkURL}">
    </c:if>
    <%@include file="image.select.jsp" %>
    <c:if test="${ not empty componentProperties.licenseInfo}">
        <span class="license">${componentProperties.licenseInfo}</span>
    </c:if>
    <c:if test="${not empty componentProperties.linkURL}">
    </a>
    </c:if>
</div>
