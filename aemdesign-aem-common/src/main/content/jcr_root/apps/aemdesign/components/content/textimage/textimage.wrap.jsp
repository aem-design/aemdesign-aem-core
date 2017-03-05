<div class="longTxt image-wrap <%= compCSSClass%>">

    <%-- render the image with caption, on the correct side --%>
    <div class="image <%= imageLocation %>" id="<%= divId %>">
        <%@ include file="image.jsp" %>
    </div>

    <cq:text property="text" tagClass="text"/>
    <div class="clear"><!-- --></div>

</div>
