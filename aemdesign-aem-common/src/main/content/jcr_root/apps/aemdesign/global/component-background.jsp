<c:if test="${not empty componentProperties.componentBackgroundAssets}">
    <style type="text/css" id="background-${componentProperties.componentId}">
        <c:forEach var="backgroundAsset" items="${componentProperties.componentBackgroundAssets}">
            <c:if test="${not empty backgroundAsset.key}">
            @media only screen and ${backgroundAsset.key} {
            </c:if>
                #${componentProperties.componentId} {
                    background-image: url('${backgroundAsset.value}');
                }
            <c:if test="${not empty backgroundAsset.key}">
            }
            </c:if>
        </c:forEach>
    </style>
</c:if>