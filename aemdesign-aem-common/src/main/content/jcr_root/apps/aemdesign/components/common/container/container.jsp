<%@page session="false"%>
<%@include file="/apps/aemdesign/global/global.jsp"%>

<c:choose>
    <c:when test="${!INCLUDE_USE_GRID}">
        <cq:include path="par" resourceType="aemdesign/components/common/parsys"/>
    </c:when>
    <c:otherwise>
        <cq:include path="par" resourceType="wcm/foundation/components/responsivegrid"/>
    </c:otherwise>
</c:choose>
