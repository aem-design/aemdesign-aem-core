<c:if test="${not empty componentProperties['photoshop:Headline']}">
    <c:set var="imageAttr" value="${imageAttr} data-alt=\"${componentProperties['photoshop:Headline']}\""/>
    <c:set var="imageAttrNS" value="${imageAttrNS} alt=\"${componentProperties['photoshop:Headline']}\""/>
</c:if>
<c:if test="${not empty componentProperties['dc:title']}">
    <c:set var="imageAttr" value="${imageAttr} data-title=\"${componentProperties['dc:title']}\""/>
    <c:set var="imageAttrNS" value="${imageAttrNS} title=\"${componentProperties['dc:title']}\""/>
</c:if>
<div data-picture ${imageAttr}>
    <c:forEach var="rendition" items="${componentProperties.renditions}">
        <div data-src="${rendition.value}" data-media="(min-width: ${rendition.key}px)"></div>
    </c:forEach>
    <noscript>
        <img src="${componentProperties.imageURL}" ${imageAttrNS}>
    </noscript>
</div>
