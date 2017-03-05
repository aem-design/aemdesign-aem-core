<%@ include file="/apps/aemdesign/global/global.jsp" %>

<%
    Boolean cancelInheritParent = _properties.get("cancelInheritParent", "").equalsIgnoreCase("true");
%>

<c:choose>
    <c:when test="<%= !cancelInheritParent %>">
        <cq:include script="findparent.jsp"/>
    </c:when>
    <c:otherwise>
        <%-- should be inherited from parent component --%>
        <cq:include path="." resourceType="/apps/aemdesign/components/lists/list"/>
    </c:otherwise>
</c:choose>

