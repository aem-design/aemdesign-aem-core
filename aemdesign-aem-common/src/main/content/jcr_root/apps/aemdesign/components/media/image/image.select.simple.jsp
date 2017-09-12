<c:if test="${not empty componentProperties['photoshop:Headline']}">
    <c:set var="imageAttr" value="${imageAttr} alt=\"${componentProperties['photoshop:Headline']}\""/>
</c:if>
<c:if test="${not empty componentProperties['dc:title']}">
    <c:set var="imageAttr" value="${imageAttr} title=\"${componentProperties['dc:title']}\""/>
</c:if>
<c:if test="${not empty componentProperties.htmlWidth}">
    <c:set var="imageAttr" value="${imageAttr} width=\"${componentProperties.htmlWidth}\""/>
</c:if>
<c:if test="${not empty componentProperties.htmlHeight}">
    <c:set var="imageAttr" value="${imageAttr} height=\"${componentProperties.htmlHeight}\""/>
</c:if>
<img class="img-fluid" src="${componentProperties.imageURL}" ${imageAttr}/>
