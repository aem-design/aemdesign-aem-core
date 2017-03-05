<%@include file="/apps/aemdesign/global/global.jsp" %>
<%
    String source = _properties.get("source", "");
    if (WCMMode.DISABLED == CURRENT_WCMMODE) {
        out.write(source);
    } else {
        out.write("<div class=\"source info\">Following content will be included in the page as is:</div><div class=\"source code\">"+escapeBody(source)+"</div>");
    }
%>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>