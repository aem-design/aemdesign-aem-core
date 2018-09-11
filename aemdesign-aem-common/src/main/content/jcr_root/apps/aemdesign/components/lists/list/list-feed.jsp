<%@ include file="/apps/aemdesign/global/global.jsp" %>

<c:if test="${componentProperties.feedEnabled}" >
    <link rel="alternate" type="${componentProperties.feedType}" title="${componentProperties.feedTitle}" href="${componentProperties.feedUrl}" />
</c:if>
