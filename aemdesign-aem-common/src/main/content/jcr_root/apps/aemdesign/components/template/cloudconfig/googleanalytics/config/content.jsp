<%@page session="false" contentType="text/html"
        pageEncoding="utf-8"
        import="com.day.cq.i18n.I18n" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %>
<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/libs/cq/cloudserviceconfigs/components/configpage/init.jsp" %>

<sling:defineObjects/>

<%
  final I18n i18n = new I18n(request);
  String ccThumbnailPath = "/apps/aemdesign/templates/cloudconfig/googleanalytics/thumbnail.png";

%>
<div class="content">
  <h3><%= i18n.get("Google Analytics Account Settings") %>
  </h3>
  <img src="<%= xssAPI.getValidHref(ccThumbnailPath) %>" alt="<%= xssAPI.encodeForHTMLAttr(serviceName) %>"
       style="float: left;"/>
  <div class="content">
    <ul style="float: left; margin: 0px;">
      <li>
        <div class="li-bullet">
          <strong><%= i18n.get("Command Method") %>
            : </strong><%= xssAPI.encodeForHTML(properties.get("commandMethod", "")) %>
        </div>
      </li>
      <li>
        <div class="li-bullet"><strong><%= i18n.get("Tracking ID") %>
          : </strong><%= xssAPI.encodeForHTML(properties.get("trackingId", "")) %>
        </div>
      </li>
      <li>
        <div class="li-bullet"><strong><%= i18n.get("Cookie Domain") %>
          : </strong><%= xssAPI.encodeForHTML(properties.get("cookieDomain", "")) %>
        </div>
      </li>
      <li>
        <div class="li-bullet"><strong><%= i18n.get("Tracking Name") %>
          : </strong><%= xssAPI.encodeForHTML(properties.get("trackingName", "")) %>
        </div>
      </li>
    </ul>
  </div>
</div>
