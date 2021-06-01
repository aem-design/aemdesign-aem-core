<%@page session="false"%>
<%@page contentType="text/html"
        pageEncoding="utf-8"
        import="javax.jcr.Node"
%><%@include file="/libs/foundation/global.jsp"
%><%@include file="/libs/wcm/global.jsp"
%><%@include file="/libs/cq/cloudserviceconfigs/components/configpage/init.jsp"
%><% I18n i18n = new I18n(request); %>
<cq:setContentBundle/>
<%
  if (currentPage.getParent().getProperties().get("componentReference","") == "") {
    log.error("Generic Snippet config is missing componentReference, updating to aemdesign/components/cloudservices/genericsnippet.");
    Node contentnode = currentPage.getParent().getContentResource().adaptTo(Node.class);
    contentnode.setProperty("componentReference","aemdesign/components/cloudservices/genericsnippet");
    Session resourceSession = resource.getResourceResolver().adaptTo(Session.class);
    resourceSession.save();
  }
%>
<div class="content">
  <h3><fmt:message key="Generic Snippet Settings"/></h3>
  <ul style="float: left; margin: 0px;">
    <li><div class="li-bullet"><strong><fmt:message key="Snippet"/>: </strong><%= xssAPI.encodeForHTML(properties.get("snippetcode", "")).replaceAll("\\&\\#xa;","<br>") %></div></li>
    <li class="config-successful-message when-config-successful" style="display: none">
      <fmt:message key="Configuration is successful."/><br>
    </li>
  </ul>
</div>
