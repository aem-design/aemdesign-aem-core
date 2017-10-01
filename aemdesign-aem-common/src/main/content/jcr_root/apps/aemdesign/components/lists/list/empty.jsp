<%@ page session="false" import="com.day.cq.wcm.api.WCMMode"%><%
%>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/aemdesign/global/context-objects.jsp" %>
<%
    boolean inEditMode = WCMMode.fromRequest(_slingRequest) == WCMMode.EDIT;
%>
<c:if test="<%= inEditMode %>">
    <img src="/libs/cq/ui/resources/0.gif" class="cq-list-placeholder" alt="" />
</c:if>