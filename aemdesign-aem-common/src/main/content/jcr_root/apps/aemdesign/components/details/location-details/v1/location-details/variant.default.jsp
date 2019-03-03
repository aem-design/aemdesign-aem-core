<div ${componentProperties.componentAttributes}>
    <h1>Title: ${componentProperties.title}</h1>
    <h2>Latitude : ${componentProperties.latitude}</h2>
    <h2>Longitude : ${componentProperties.longitude}</h2>

    <c:forEach varStatus="status" var="page" items="${componentProperties.pages}">
        Page : <b><a href="${page}.html">${page}</a></b> <br/>
    </c:forEach>
</div>