<ul ${componentProperties.componentAttributes}>
    <li>
        <div class="col-1 graphics">
            <a ${componentProperties.assetTags} ${componentProperties.target} ${componentProperties.dtmEvent} class="external_link"  href="${componentProperties.href}" title="${componentProperties.altTitle}"
                    <c:forEach var="attribute" items="${componentProperties.download.attributes}" >
                        ${attribute.key}="${attribute.value}"
                    </c:forEach>
            >
            <c:choose>
                <c:when test="${componentProperties.useDocumentIcon}">
                    <span class="icon type_${componentProperties.download.iconType}"><img src="/etc/designs/default/0.gif" alt="${componentProperties.mimeType}" /></span>
                </c:when>
                <c:when test="${componentProperties.thumbnailImageDefault}">
                    <img src="${componentProperties.assetPath}" alt="${componentProperties.assetTitle}" width="${componentProperties.thumbnailWidth}" height="${componentProperties.thumbnailHeight}"/>
                </c:when>
                <c:otherwise>
                    <img src="${componentProperties.assetPath}" alt="${componentProperties.assetTitle}" width="${componentProperties.thumbnailWidth}" height="${componentProperties.thumbnailHeight}"/>
                </c:otherwise>
            </c:choose>
            </a>
        </div>
        <div class="col-4 header">
            <h3>
                <a class="external_link" href="${componentProperties.href}" title="${componentProperties.assetDescription}" target="_blank">
                    ${componentProperties.assetDescription}
                </a>
            </h3>
        </div>

        <div class="body">
            <!--(51 KB pdf file)-->
            <p>(${componentProperties.contentSize}, ${componentProperties.mimeType})</p>
        </div>
    </li>
</ul>