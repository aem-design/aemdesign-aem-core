<%@page session="false"
			import="com.day.cq.wcm.webservicesupport.Configuration,
              com.day.cq.wcm.webservicesupport.ConfigurationManager" %><%
%><%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %><%
%><cq:defineObjects/><%

String[] services = pageProperties.getInherited("cq:cloudserviceconfigs", new String[]{});
ConfigurationManager cfgMgr = resource.getResourceResolver().adaptTo(ConfigurationManager.class);
if(cfgMgr != null) {
    String snippetCode = null;
    Configuration cfg = cfgMgr.getConfiguration("generic-snippet", services);
    if(cfg != null) {
        snippetCode = cfg.get("snippetcode", null);
    }

    if(snippetCode != null) {
    %><%= snippetCode %><%
    }
}
%>
