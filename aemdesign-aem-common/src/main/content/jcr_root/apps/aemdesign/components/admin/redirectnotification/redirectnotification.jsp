<%@ include file="/apps/aemdesign/global/global.jsp" %>

<%
    String
        redirectTarget = _pageProperties.get("redirectTarget", (String) null),
        redirectUrl = redirectTarget;

    boolean editing = WCMMode.EDIT == CURRENT_WCMMODE;

    if (redirectTarget != null && redirectTarget.startsWith("/content")) {
        Page targetPage = _pageManager.getPage(redirectTarget);

        // found page?
        if (targetPage != null) {
            redirectUrl = redirectUrl.concat(".html");
            redirectTarget = getPageTitle(targetPage);
        }

        // set to invalid text.
        else {
            redirectTarget = "Invalid content path";
            redirectUrl = "#";
        }
    }
%>
<c:if test="<%= editing && redirectTarget != null %>">
    <p class="cq-info">
        This page is set to redirect to:
        <a href="<%= escapeBody(redirectUrl) %>"><%= escapeBody(redirectTarget) %></a>
    </p>
</c:if>

