

    <c:if test="${not empty componentProperties.imageTargetURL}">
    <a href="${componentProperties.imageTargetURL}">
    </c:if>
        <c:choose>
            <c:when test="${componentProperties.imageOption eq 'fixedImageGenerated'}">
                <%
                    Image normalImage = componentProperties.get("image", Image.class);
                    normalImage.draw(out);
                %>
            </c:when>
            <c:when test="${componentProperties.imageOption eq 'fixedImageRendition'}">
                <img src="<c:forEach var="rendition" items="${componentProperties.responsiveImageSet}">${rendition.value}</c:forEach>" alt='${componentProperties.image.alt}' title='${componentProperties.image.title}'>
            </c:when>
            <c:otherwise>
                <img src="${componentProperties.image.fileReference}" alt="${componentProperties.image.alt}" title="${componentProperties.image.title}" class="cq-dd-image img-responsive">
            </c:otherwise>
        </c:choose>
    <c:if test="${not empty componentProperties.imageTargetURL}">
        </a>
    </c:if>
