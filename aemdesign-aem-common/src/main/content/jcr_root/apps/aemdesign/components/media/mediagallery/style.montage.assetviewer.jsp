<div ${componentProperties.componentAttributes}>
    <div>
        <c:forEach items="${componentProperties.listItems}" var="item">
        <div class="cell">
            <a class="caption" href="${item.href}" title="${componentProperties.titleAltPrefixText}${item.title}"><em>${item.description}</em> <strong>${item.title}</strong></a>
            <img alt="${item.title}"
                 width="${item.displayWidth}" height="${item.displayHeight}" src="${item.imgUrl}" >
        </div>
        </c:forEach>
    </div>
</div>
