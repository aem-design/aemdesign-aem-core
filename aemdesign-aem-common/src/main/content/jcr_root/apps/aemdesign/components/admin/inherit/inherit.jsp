<%@include file="/apps/aemdesign/global/global.jsp" %>
<%
Boolean cancelInheritParent = _properties.get("cancelInheritParent","").contentEquals("true");
%><c:choose>
    <c:when test="<%=!cancelInheritParent%>">
        <cq:include script="findparent.jsp"/>
    </c:when>
    <c:otherwise>
        <cq:include script="render.jsp"/>
    </c:otherwise>
</c:choose>

