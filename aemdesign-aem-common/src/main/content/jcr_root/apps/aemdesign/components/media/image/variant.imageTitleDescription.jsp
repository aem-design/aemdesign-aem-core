<div ${componentProperties.componentAttributes}>
    <figure style="opacity: 1;">
        <c:if test="${not empty componentProperties.imageTargetURL}">
            <a href="${componentProperties.imageTargetURL}"> ${componentProperties.image.title}</a>
        </c:if>
        <c:choose>
            <%-- Responsive Image --%>
            <c:when test="${componentProperties.imageOption eq 'fixedImageRendition' ||
                                    componentProperties.imageOption eq 'responsiveRendition' ||
                                    componentProperties.imageOption eq 'responsiveRenditionOverride'||
                                    componentProperties.imageOption eq 'responsiveGenerated'}">
                <%@include file="variant.responsiveImage.jsp" %>
            </c:when>
            <%-- Normal Image --%>
            <c:otherwise>
                <%@include file="variant.normalImage.jsp" %>
            </c:otherwise>
        </c:choose>

        <figcaption>
            <c:if test="${not empty componentProperties.imageTargetURL}">
            <a href="${componentProperties.imageTargetURL}" title="${componentProperties.image.title}">
                </c:if>
                <strong>${componentProperties.image.title}</strong>
                <c:if test="${not empty componentProperties.imageTargetURL}">
            </a>
            </c:if>
            <p>${componentProperties.image.description}</p>
        </figcaption>
    </figure>

</div>
