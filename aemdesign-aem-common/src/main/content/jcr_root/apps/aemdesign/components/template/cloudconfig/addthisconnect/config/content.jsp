<%@page session="false" contentType="text/html"
        pageEncoding="utf-8"
        import="com.day.cq.i18n.I18n" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %>
<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/libs/cq/cloudserviceconfigs/components/configpage/init.jsp" %>

<sling:defineObjects/>

<%
    final I18n i18n = new I18n(request);
    String defaultThumbnailPath = "/apps/aemdesign/templates/cloudconfig/addthisconnect/thumbnail.png";
%>
<div class="content">
    <h3><%= i18n.get("AddThis Account Settings") %></h3>
    <img src="<%= xssAPI.getValidHref(defaultThumbnailPath) %>" alt="<%= xssAPI.encodeForHTMLAttr(serviceName) %>" style="float: left;" />
    <div class="content">
        <ul style="float: left; margin: 0px;">
            <li><div class="li-bullet"><strong><%= i18n.get("Pub ID") %>: </strong><%= xssAPI.encodeForHTML(properties.get("pubId", "")) %></div></li>
        </ul>
    </div>
</div>
