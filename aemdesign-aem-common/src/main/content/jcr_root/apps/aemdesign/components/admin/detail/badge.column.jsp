<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ page import="com.day.cq.wcm.foundation.Image"%>


    <%

        //TODO: require extra logic to hide the image if image is null
        //TODO: change "img" at line 10 to the proper img size

        // init
        ValueMap resourceProp = _resource.adaptTo(ValueMap.class);

        String displayImage = "";
        String displayTitle = "";
        String displayDescription = "";
        Image img = new Image(_resource, "image");

        displayTitle = resourceProp.get("title", "");
        if (displayTitle == null || "".equals(displayTitle)) {
            displayTitle = resourceProp.get("navTitle", "");
        }

        displayDescription = resourceProp.get("description", "");

        // set img rendering
        if (img.hasContent()) {
            img.setItemName(Image.NN_FILE, ".");
            img.setNoPlaceholder(true);
            displayImage = img.getSrc();
        }

        String pathLink = _pageManager.getContainingPage(_resource).getPath().concat(".html");
    %>
    <div class="p highlight_highlight">
        <a href="<%= pathLink %>" class="hl">
           <p class="highlightItem" style="background-image: url('<%= mappedUrl(displayImage) %>'); background-position: top center;"></p>
        </a>
        <div class="detail">
            <a class="highlightTitle hoverUnderline" href="<%= pathLink %>">
                <strong><%= escapeBody(displayTitle) %></strong>
            </a>
            <%= escapeBody(displayDescription) %>
        </div>
    </div>

<%@include file="/apps/aemdesign/global/component-badge.jsp" %>