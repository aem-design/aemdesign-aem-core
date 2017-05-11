<div ${componentProperties.componentAttributes}>
    <div id="accordion" style="width:${componentProperties.width}%;height:${componentProperties.height}%;">
        <c:forEach items="${list}" var="row">
            <h3>${row['title']}</h3>
            <div>
                <p>
                        ${row['desc']}
                </p>
                <c:if test="${not empty row['image']}">
                    <c:set var="size" value="${fn:split(row['imageSize'],',')}" />
                    <img src="${row['image']}" width="${size[0]}" height="${size[1]}" />
                </c:if>

            </div>
        </c:forEach>

    </div>
</div>
