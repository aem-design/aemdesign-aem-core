<%@page session="false" %>
<%@page contentType="text/html"
        pageEncoding="utf-8"
%>
<%@include file="/libs/foundation/global.jsp"
%>
<%@include file="/libs/wcm/global.jsp"
%>
<%@include file="/libs/cq/cloudserviceconfigs/components/configpage/init.jsp"
%>
<% I18n i18n = new I18n(request); %>
<cq:setContentBundle/>
<div class="content">
  <h3><fmt:message key="Salesforce API Settings"/></h3>
  <ul style="float: left; margin: 0px;">
    <li>
      <div class="li-bullet"><strong><fmt:message
        key="Login Token Url"/>: </strong><%= xssAPI.encodeForHTML(properties.get("./metaData/environment", "")) %>
      </div>
    </li>
    <li>
      <div class="li-bullet"><strong><fmt:message
        key="Service Url"/>: </strong><%= xssAPI.encodeForHTML(properties.get("./metaData/redirectUri", "")) %>
      </div>
    </li>
    <li>
      <div class="li-bullet"><strong><fmt:message
        key="Authentication Type"/>: </strong><%= xssAPI.encodeForHTML(properties.get("./metaData/grant_type", "")) %>
      </div>
    </li>
    <li>
      <div class="li-bullet"><strong><fmt:message
        key="User Id"/>: </strong><%= xssAPI.encodeForHTML(properties.get("./metaData/user", "")) %>
      </div>
    </li>
    <li class="config-successful-message when-config-successful" style="display: none">
      <fmt:message key="Configuration is successful."/><br>
    </li>
  </ul>
</div>
