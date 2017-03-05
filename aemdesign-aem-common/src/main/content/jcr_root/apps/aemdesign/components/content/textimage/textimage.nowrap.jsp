
<div class="longTxt image-no-wrap <%= compCSSClass%>">
    <c:choose>
        <c:when test="<%= imageLocation.equals("image-left")%>">

            <%-- render the image with caption, on the correct side --%>
            <div class="image <%= imageLocation %>" id="<%= divId %>">
                <%@ include file="image.jsp" %>
            </div>
            <cq:text property="text" tagClass="text"/>

        </c:when>

        <c:otherwise>

            <cq:text property="text" tagClass="text"/>

            <%-- render the image with caption, on the correct side --%>
            <div class="image <%= imageLocation %>" id="<%= divId %>">
                <%@ include file="image.jsp" %>
            </div>

        </c:otherwise>
    </c:choose>

    <div class="clear"><!-- --></div>

</div>
