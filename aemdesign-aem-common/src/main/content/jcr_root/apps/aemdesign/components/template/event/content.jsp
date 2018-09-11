<%@include file="/apps/aemdesign/global/global.jsp" %>
<% if (WCMMode.DISABLED != CURRENT_WCMMODE) { %>
<cq:include path="redirectnotification" resourceType="aemdesign/components/common/redirectnotification" />
<% }
%>
<article id="main" role="main">
    <div class="content visible">
        <cq:include path="article" resourceType="aemdesign/components/layout/article"/>
        <cq:include path="aside" resourceType="aemdesign/components/layout/aside"/>
    </div>
</article>
