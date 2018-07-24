<%--
// TODO: require extra logic to hide the image if image is null
// TODO: change "img" at line 10 to the proper img size
--%>
<%@ include file="/apps/aemdesign/global/global.jsp"%>
<%@ page import="com.day.cq.wcm.foundation.Image"%>
<%

    // init
    Page thisPage = _pageManager.getContainingPage(_resource);
    Boolean hideThumbnail = (Boolean) request.getAttribute("hideThumbnail");

    String fileReference = null;
    ValueMap imageProperties = thisPage.getProperties("image");
    if (imageProperties != null) {
        fileReference = imageProperties.get("fileReference", (String) null);
    }

    String displayTitle = getPageNavTitle(thisPage);

    String displayDescription = thisPage.getDescription();
    String pagePath = thisPage.getPath().concat(DEFAULT_EXTENTION);

%>
<div class="p highlight_highlight">

    <a href="<%= pagePath %>" class="hl">
        <c:choose>
            <c:when test="<%= fileReference != null || hideThumbnail %>">
                <div class="highlightItem"></div>
            </c:when>
            <c:otherwise>
                <div class="highlightItem"
                   style="background-image: url('<%= mappedUrl(_resourceResolver, fileReference) %>'); background-position: top center;"><!----></div>
            </c:otherwise>
        </c:choose>
    </a>

    <div class="detail">
        <a class="highlightTitle hoverUnderline" href="<%= pagePath %>">
            <strong><%= displayTitle %></strong>
        </a>
        <c:if test="<%= displayDescription != null %>">
            <%= displayDescription %>
        </c:if>
    </div>
</div>