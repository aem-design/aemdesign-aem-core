<%@include file="/apps/aemdesign/global/global.jsp" %>
<% if (WCMMode.DISABLED != CURRENT_WCMMODE) { %>
<cq:include path="redirectnotification" resourceType="aemdesign/components/admin/redirectnotification" />
<% } %>
<article id="main" role="main">
    <div class="wrapper visible">
        <cq:include path="aside" resourceType="aemdesign/components/admin/aside"/>
        <cq:include path="article" resourceType="aemdesign/components/admin/article"/>
    </div>
</article>
