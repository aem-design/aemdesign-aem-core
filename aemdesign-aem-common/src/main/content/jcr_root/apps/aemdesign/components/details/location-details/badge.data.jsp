{
    type: 'Feature',
    geometry: {
        type: 'Point',
        coordinates: [${componentProperties.longitude}, ${componentProperties.latitude}]
    },
    properties: {
    venue: '<c:out value='${componentProperties.title}'/>',
    categoryIconColor: '<c:out value='${componentProperties.menuColorCode}'/>',
    categoryIconPath: '<c:out value='${componentProperties.categoryIconPath}'/>',
    markerPointX: <c:out value='${componentProperties.markerPointX}'/>,
    markerPointY: <c:out value='${componentProperties.markerPointY}'/>,
    zIndex: <c:out value='${componentProperties.zIndex}'/><c:if test="${fn:length(componentProperties.eventList) > 0}">,</c:if>
    <c:if test="${fn:length(componentProperties.eventList) > 0}">
        events: [
            <c:forEach varStatus="status" var="event" items="${componentProperties.eventList}">
                {<c:forEach varStatus="s" var="e" items="${event}">${e.key}:'<c:out value="${e.value}"/>' <c:if test="${s.last == false}">,</c:if></c:forEach>}<c:if test="${status.last == false}">,</c:if>
            </c:forEach>
        ]
    <%--
        image:"http://d3fveiluhe0xc2.cloudfront.net/media/w140/artspavilion-1.jpg",
        title:"M+ Pavilion",
        description:"",
        url:"en/the-district/architecture-facilities/m-4",
        category:""
    --%>
    </c:if>
    }
}