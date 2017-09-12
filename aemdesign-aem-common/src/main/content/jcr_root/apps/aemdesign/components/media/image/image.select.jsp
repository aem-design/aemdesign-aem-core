<c:choose>
    <c:when test="${componentProperties.imageOption eq 'responsive'}">
        <%@include file="image.select.responsive.jsp" %>
    </c:when>
    <c:otherwise>
        <%@include file="image.select.simple.jsp" %>
    </c:otherwise>
</c:choose>
