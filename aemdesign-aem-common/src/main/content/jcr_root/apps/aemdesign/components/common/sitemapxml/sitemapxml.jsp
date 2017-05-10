<%@include file="/apps/aemdesign/global/global.jsp" %>
<%
    if (CURRENT_WCMMODE != WCMMode.EDIT) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return;
    }

%>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
