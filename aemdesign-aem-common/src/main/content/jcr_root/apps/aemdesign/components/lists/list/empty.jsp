<%@ page session="false" import="com.day.cq.wcm.api.WCMMode"%><%
%>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/aemdesign/global/context-objects.jsp" %>
<%
    boolean inEditMode = WCMMode.fromRequest(_slingRequest) == WCMMode.EDIT;
%>
<c:if test="<%= inEditMode %>">
    <img src="/apps/settings/wcm/design/aemdesign/blank.png" class="cq-list-placeholder" alt="" />
</c:if>