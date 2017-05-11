<%@ page import="com.day.cq.wcm.webservicesupport.ConfigurationManager" %>
<%@ page import="com.day.cq.wcm.webservicesupport.Configuration" %>
<%@ page import="com.day.cq.wcm.webservicesupport.ConfigurationConstants" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%
    Object[][] componentFields = {
            {"cssClassRow", ""}
    };

    ComponentProperties componentProperties = getComponentProperties(pageContext, componentFields);
    componentProperties.putAll(getComponentStyleProperties(pageContext));

    ResourceResolver adminResourceResolver  = this.openAdminResourceResolver(_sling);

    try {

        // Getting attached facebook cloud service config in order to fetch appID
        ConfigurationManager cfgMgr = adminResourceResolver.adaptTo(ConfigurationManager.class);
        Configuration addthisConfiguration = null;
        String[] services = _pageProperties.getInherited(ConfigurationConstants.PN_CONFIGURATIONS, new String[]{});
        String pubid = _properties.get("pubId","");
        if(cfgMgr != null) {
            addthisConfiguration = cfgMgr.getConfiguration("addthisconnect", services);
            if (addthisConfiguration != null) {
                pubid = addthisConfiguration.get("pubId", "");

            }
        }

        componentProperties.put("pubId",pubid);

        TagManager _adminTagManager = adminResourceResolver.adaptTo(TagManager.class);
        componentProperties.put("componentAttributes", compileComponentAttributes(_adminTagManager,componentProperties,_component));

    } catch (Exception ex) {

        out.write( Throwables.getStackTraceAsString(ex) );

    } finally {
        this.closeAdminResourceResolver(adminResourceResolver);
    }
%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>

<c:choose>
    <c:when test="${not empty componentProperties.pubId}">
        <%@ include file="variant.default.jsp"  %>
    </c:when>
    <c:when test="${empty componentProperties.pubId}">
        <%@ include file="variant.empty.jsp"  %>
    </c:when>
    <c:otherwise>
        <%@ include file="variant.hidden.jsp" %>
    </c:otherwise>
</c:choose>

<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
