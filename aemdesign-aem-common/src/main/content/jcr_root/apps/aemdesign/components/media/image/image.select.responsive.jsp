<c:if test="${not empty componentProperties['photoshop:Headline']}">
    <c:set var="imageAttr" value="${imageAttr} data-alt=\"${componentProperties['photoshop:Headline']}\""/>
    <c:set var="imageAttrNS" value="${imageAttrNS} alt=\"${componentProperties['photoshop:Headline']}\""/>
</c:if>
<c:if test="${not empty componentProperties['dc:title']}">
    <c:set var="imageAttr" value="${imageAttr} data-title=\"${componentProperties['dc:title']}\""/>
    <c:set var="imageAttrNS" value="${imageAttrNS} title=\"${componentProperties['dc:title']}\""/>
</c:if>
<picture>
    <c:forEach var="rendition" items="${componentProperties.renditions}">
        <source srcset="${rendition.value}" media="${rendition.key}"/>
    </c:forEach>
    <img src="${componentProperties.imageURL}" ${imageAttr} ${imageAttrNS}>
</picture>
