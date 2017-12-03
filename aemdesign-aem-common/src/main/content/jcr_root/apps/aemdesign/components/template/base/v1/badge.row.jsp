<%--
//TODO: require extra logic to hide the image if image is null
//TODO: change "img" at line 10 to the proper img size
--%>
<%@ page import="com.day.cq.wcm.foundation.Image"%>
<%@ include file="/apps/aemdesign/global/global.jsp"%>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%!
    private boolean noImageAvailable(String fileReference, Image img) {
        return !img.hasContent() && (fileReference == null || fileReference.length() == 0 );
    }
%>
<%

    // init
    Page thisPage = _pageManager.getContainingPage(_resource);
    
    Boolean hideThumbnail = (Boolean) request.getAttribute("hideThumbnail");

    Image img = this.getScaledProcessedImage(_resource, "image", 60);
    String displayImage = "";
    String displayTitle = "";
    String displayDescription = "";
    String cssClass = hideThumbnail ? "listBox" : "listBox withImage";

    // set title and description
    displayTitle = getPageTitle(thisPage);

    displayDescription = thisPage.getDescription();
    if (displayDescription == null) {
    	displayDescription = "";
    }

    // set img rendering
    if (img.hasContent()) {
        displayImage = img.getSrc();
    }
    else {
        cssClass = "listBox";
    }
%>
<div class="<%= cssClass %>">
    <a href="<%= thisPage.getPath().concat(DEFAULT_EXTENTION) %>" class="linkTxt">
        <%= displayTitle %>
    </a>
    <div class="longTxt">
        <p><%= displayDescription %></p>
    </div>

    <c:if test="<%= !hideThumbnail %>">
        <div class="image">
            <c:if test="<%= img.hasContent() %>">
                <% img.draw(out); %>
            </c:if>
        </div>
    </c:if>

</div>
