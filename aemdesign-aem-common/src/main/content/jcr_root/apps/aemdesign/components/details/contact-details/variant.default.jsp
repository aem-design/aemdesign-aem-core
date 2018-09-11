<c:if test="${not empty componentProperties.pageBackgroundImage}">
    <c:set var="extraAttr" value="${extraAttr} style=\"background-image: url(${componentProperties.pageBackgroundImage})\""/>
</c:if>

<c:set var="imageTitle" value="${componentProperties.titleFormattedText}"/>

<c:if test="${not empty componentProperties.pageImageTitle}">
    <c:set var="imageTitle" value="${componentProperties.pageImageTitle}"/>
</c:if>

<div ${componentProperties.componentAttributes}${extraAttr}>
    <%@include file="contact-details.header.jsp" %>
    <header>
        <div itemscope itemtype="http://schema.org/Person">
            <img class="image" src="${componentProperties.pageImageThumbnail}" itemprop="image" alt="${imageTitle}"/>
            <div class="title">${componentProperties.titleFormatted}</div>
        <c:if test="${not componentProperties.hideDescription}">
            <div class="description">${componentProperties.descriptionFormatted}</div>
        </c:if>
        <c:if test="${not empty componentProperties.tags}">
            <div class="tags" itemprop="keywords">
            <c:forEach items="${componentProperties.tags}" var="tag">
                <div class="text">${tag.description}</div>
            </c:forEach>
            </div>
        </c:if>
        </div>
    </header>
    <%@include file="contact-details.footer.jsp" %>
</div>
