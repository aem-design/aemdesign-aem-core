<%--
  ADOBE CONFIDENTIAL

  Copyright 2013 Adobe Systems Incorporated
  All Rights Reserved.

  NOTICE:  All information contained herein is, and remains
  the property of Adobe Systems Incorporated and its suppliers,
  if any.  The intellectual and technical concepts contained
  herein are proprietary to Adobe Systems Incorporated and its
  suppliers and may be covered by U.S. and Foreign Patents,
  patents in process, and are protected by trade secret or copyright law.
  Dissemination of this information or reproduction of this material
  is strictly forbidden unless prior written permission is obtained
  from Adobe Systems Incorporated.
--%><%
%>
<%@page session="false" contentType="text/html"
        pageEncoding="utf-8"
        import="com.day.cq.i18n.I18n" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%
%>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %>
<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/libs/cq/cloudserviceconfigs/components/configpage/init.jsp" %>

<sling:defineObjects/>

<%
    final I18n i18n = new I18n(request);
    thumbnailPath = "/apps/aemdesign/templates/cloudconfig/googlemap/thumbnail.png";

%>
<div class="content">
    <h3><%= i18n.get("Google Map API Settings") %></h3>
    <img src="<%= xssAPI.getValidHref(thumbnailPath) %>" alt="<%= xssAPI.encodeForHTMLAttr(serviceName) %>" style="float: left;" />
    <div class="content">
        <ul style="float: left; margin: 0px;">
            <li><div class="li-bullet">
                <strong><%= i18n.get("Command Method") %>: </strong><%= xssAPI.encodeForHTML(properties.get("commandMethod", "")) %></div></li>
            <li><div class="li-bullet"><strong><%= i18n.get("Google MAP API Key") %>: </strong><%= xssAPI.encodeForHTML(properties.get("googleApiKey", "")) %></div></li>

        </ul>
    </div>
</div>