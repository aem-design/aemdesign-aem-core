<%@include file="/apps/aemdesign/global/global.jsp" %>
<% if (WCMMode.DISABLED != CURRENT_WCMMODE) { %>
<cq:include path="redirectnotification" resourceType="aemdesign/components/common/redirectnotification" />
<% } %>
<article id="main" role="main">
    <div class="wrapper visible">
        <cq:include path="aside-left" resourceType="aemdesign/components/layout/aside"/>
        <cq:include path="article" resourceType="aemdesign/components/layout/article"/>
        <cq:include path="aside-right" resourceType="aemdesign/components/layout/aside"/>
    </div>
</article>
