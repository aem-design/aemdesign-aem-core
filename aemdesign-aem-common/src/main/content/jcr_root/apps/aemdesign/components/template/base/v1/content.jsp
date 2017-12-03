<%@include file="/apps/aemdesign/global/global.jsp" %>
<c:if test="${CURRENT_WCMMODE ne WCMMODE_DISABLED}">
    <cq:include path="redirectnotification" resourceType="aemdesign/components/common/redirectnotification" />
</c:if>
<cq:include script="/apps/aemdesign/components/layout/container/container.jsp"/>