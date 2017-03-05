<%--
//TODO: need to add defaults. jira integration
 --%>
<%@ include file="/apps/aemdesign/global/global.jsp" %>

<%
    String title = _properties.get("title", "");
    boolean hasTitle = !"".equals(title);
%>

<div class="title"><%= escapeBody(title) %></div>
<div class="content">
    <sling:include path="par" />
</div>

