<div ${componentProperties.componentAttributes}>
    <a class="prev" href="#" title="${componentProperties.prevLinkText}">${componentProperties.prevLinkText}</a>
    <ul>
        <c:forEach items="${componentProperties.listItems}" var="item">

            <li>
                <a class="caption lightbox <c:if test='${item.isvideo || item.hasVideoSource}'>media</c:if>"
                   data-description="&lt;strong&gt;${item.title}&lt;br&gt;${item.creator}&lt;/strong&gt;&lt;p&gt;${item.description}&lt;/p&gt;&lt;br&gt;&lt;span&gt;${item.copyright}&lt;/span&gt;"
                        <c:if test='${not empty componentProperties.lightboxWidth && not empty componentProperties.lightboxHeight}'>
                            data-width="${componentProperties.lightboxWidth}%" data-height="${componentProperties.lightboxHeight}%"
                        </c:if>
                        <c:if test='${empty componentProperties.lightboxWidth && empty componentProperties.lightboxHeight && (item.isvideo || item.hasVideoSource)}'>
                            data-width="${item.videoWidth}" data-height="${item.videoHeight}"
                        </c:if>
                   data-group="${componentProperties.galleryGroup}" href="${item.href}"
                   title="${componentProperties.titleAltPrefixText}${item.title}"><strong>${item.title}<br>${item.creator}</strong><em>${item.description}</em></a>
                <c:if test='${item.isvideo}'>
                    <img alt="${item.title}" height="${item.displayHeight}" src="${item.thumbnail}" width="${item.displayWidth}">
                </c:if>
                <c:if test='${item.isimage && (item.hasVideoSource || item.hasOtherSource)}'>
                    <img alt="${item.title}" height="${item.displayHeight}" src="${item.thumbnail}" width="${item.displayWidth}">
                </c:if>
                <c:if test='${item.isimage && !item.hasVideoSource && !item.hasOtherSource}'>
                    <img alt="${item.title}" height="${item.displayHeight}" src="${item.href}" width="${item.displayWidth}">
                </c:if>

            </li>

        </c:forEach>
    </ul>
    <a class="next" href="#" title="${componentProperties.nextLinkText}">${componentProperties.nextLinkText}</a>
</div>
