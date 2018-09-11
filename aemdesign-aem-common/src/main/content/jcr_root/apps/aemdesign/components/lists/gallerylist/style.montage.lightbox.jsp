<div ${componentProperties.componentAttributes}>
    <div class="content">
        <c:forEach items="${componentProperties.listItems}" var="item">
            <div class="cell aqua">
                <a class="caption lightbox <c:if test='${item.isvideo}'>media</c:if>"
                   data-description="&lt;b&gt;${item.title}&lt;/b&gt;&lt;br&gt;&lt;b&gt;${item.description}&lt;/b&gt;&lt;br&gt;&lt;p&gt;${item.creator}&lt;/p&gt;&lt;span&gt;${item.copyright}&lt;/span&gt;"
                       data-width="${componentProperties.lightboxWidth}%" data-height="${componentProperties.lightboxHeight}%" data-group="${componentProperties.galleryGroup}" href="${item.href}"
                   title="${componentProperties.titleAltPrefixText}${item.title}">${item.title}</a>
                <c:if test='${item.isvideo}'>
                    <img alt="${item.title}" height="${item.displayHeight}" src="${item.thumbnail}" width="${item.displayWidth}">
                </c:if>
                <c:if test='${item.isimage}'>
                    <img alt="${item.title}" height="${item.displayHeight}" src="${item.href}" width="${item.displayWidth}">
                </c:if>

            </div>
        </c:forEach>
    </div>
</div>