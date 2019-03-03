
<c:set var="imageTitle" value="${componentProperties.titleFormattedText}"/>

<c:if test="${not empty componentProperties.pageImageTitle}">
    <c:set var="imageTitle" value="${componentProperties.pageImageTitle}"/>
</c:if>

<div ${componentProperties.componentAttributes}${extraAttr} itemscope itemtype="http://schema.org/Person">
    <%@include file="contact-details.header.jsp" %>
    <header>
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
    </header>
    <%@include file="contact-details.footer.jsp" %>
</div>
