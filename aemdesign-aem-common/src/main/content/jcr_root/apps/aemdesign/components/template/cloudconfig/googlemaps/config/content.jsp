<%@page session="false" contentType="text/html"
        pageEncoding="utf-8"
        import="com.day.cq.i18n.I18n" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %>
<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/libs/cq/cloudserviceconfigs/components/configpage/init.jsp" %>

<sling:defineObjects/>

<%
    final I18n i18n = new I18n(request);
    String ccThumbnailPath = "/apps/aemdesign/templates/cloudconfig/googlemaps/thumbnail.png";

%>
<div class="content">
    <h3><%= i18n.get("Google Maps API Settings") %></h3>
    <img src="<%= xssAPI.getValidHref(ccThumbnailPath) %>" alt="<%= xssAPI.encodeForHTMLAttr(serviceName) %>" style="float: left;" />
    <div class="content">
        <ul style="float: left; margin: 0px;">
            <li><div class="li-bullet">
                <strong><%= i18n.get("Command Method") %>: </strong><%= xssAPI.encodeForHTML(properties.get("commandMethod", "")) %></div></li>
            <li><div class="li-bullet"><strong><%= i18n.get("Google Maps API Key") %>: </strong><%= xssAPI.encodeForHTML(properties.get("googleMapsApiKey", "")) %></div></li>

        </ul>
    </div>
</div>
