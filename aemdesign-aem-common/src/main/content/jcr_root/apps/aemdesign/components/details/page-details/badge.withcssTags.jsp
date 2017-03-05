<%@ page import="com.day.cq.wcm.api.Page"%>

<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/images.jsp" %>

<%
   //init
	Page thisPage = (Page) request.getAttribute("badgePage");
    String cssTags = (String) request.getAttribute("cssTags");
%>

<c:set var="cssTags" value="<%= cssTags %>" />

<div <c:if test="${not empty cssTags}"> class="${cssTags}" </c:if>>
   <%-- TBD add the code to show images from  pages--%>
</div>