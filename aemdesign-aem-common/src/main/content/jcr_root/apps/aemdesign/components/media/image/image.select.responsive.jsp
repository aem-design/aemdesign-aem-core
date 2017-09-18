<c:if test="${not empty componentProperties['photoshop:Headline']}">
    <c:set var="imageAttr" value="${imageAttr} alt=\"${componentProperties['photoshop:Headline']}\""/>
</c:if>
<c:if test="${not empty componentProperties['dc:title']}">
    <c:set var="imageAttr" value="${imageAttr} title=\"${componentProperties['dc:title']}\""/>
</c:if>
<picture>
    <!--[if IE 9]><video style="display: none;"><![endif]-->
    <c:forEach var="rendition" items="${componentProperties.renditions}">
        <source srcset="${rendition.value}" media="${rendition.key}"/>
    </c:forEach>
    <!--[if IE 9]></video><![endif]-->
    <img src="${componentProperties.imageURL}" ${imageAttr}>
</picture>
