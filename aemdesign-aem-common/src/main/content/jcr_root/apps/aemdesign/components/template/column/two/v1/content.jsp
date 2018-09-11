<%@page session="false"
        contentType="text/html; charset=utf-8"%>
<%@include file="/apps/aemdesign/global/global.jsp" %>
<c:if test="${CURRENT_WCMMODE ne WCMMODE_DISABLED}">
    <cq:include path="redirectnotification" resourceType="aemdesign/components/common/redirectnotification" />
</c:if>

<cq:include path="aside" resourceType="aemdesign/components/layout/aside"/>
<cq:include path="article" resourceType="aemdesign/components/layout/article"/>
