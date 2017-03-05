<%--
//TODO: require extra logic to hide the image if image is null
//TODO: change "img" at line 10 to the proper img size
--%>
<%@ include file="/apps/aemdesign/global/global.jsp"%>
<%@ include file="/apps/aemdesign/global/images.jsp" %>



    <%
        // init
        ValueMap resourceProp = _resource.adaptTo(ValueMap.class);

        Boolean hideThumbnail = (Boolean) request.getAttribute("hideThumbnail");

        String displayImage = "";
        String displayTitle = "";
        String displayDescription = "";
        Image img = this.getScaledProcessedImage(_resource, "image", 60);

        // set title and description
        displayTitle = resourceProp.get("title", "");
        displayDescription = resourceProp.get("description", "");

        // set img rendering
        if (img.hasContent()) {
            displayImage = img.getSrc();
        }
    %>
    <div class="listBox <%= !hideThumbnail ? "withImage" : "" %>">
        <a href="<%= linkToPage(_pageManager, _resource) %>" class="linkTxt">
            <%= escapeBody(displayTitle) %>
        </a>

        <div class="longTxt">
            <p>
                <%= escapeBody(displayDescription) %>
            </p>
        </div>

        <c:if test="<%= !hideThumbnail %>">
            <div class="image">
                <% img.draw(out); %>
            </div>
        </c:if>
    </div>

<%@include file="/apps/aemdesign/global/component-badge.jsp" %>