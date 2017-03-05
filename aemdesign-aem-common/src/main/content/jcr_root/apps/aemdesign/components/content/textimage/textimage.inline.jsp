<div class="longTxt <%= imageLocation %> <%= compCSSClass%>">

    <cq:text property="text" tagClass="text"/>


    <%-- render the image with caption, on the correct side --%>
    <div class="image">
        <%@ include file="image.jsp" %>
    </div>
</div>
