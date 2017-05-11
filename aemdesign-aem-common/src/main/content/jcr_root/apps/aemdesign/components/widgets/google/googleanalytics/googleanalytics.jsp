<%@ page import="com.day.cq.wcm.webservicesupport.ConfigurationManager" %>
<%@ page import="com.day.cq.wcm.webservicesupport.Configuration" %>
<%@ page import="com.day.cq.wcm.webservicesupport.ConfigurationConstants" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%

    // Getting attached facebook cloud service config in order to fetch appID
    ConfigurationManager cfgMgr = _resourceResolver.adaptTo(ConfigurationManager.class);
    Configuration gaConfiguration = null;
    String[] services = _pageProperties.getInherited(ConfigurationConstants.PN_CONFIGURATIONS, new String[]{});
    String trackingid = StringUtils.EMPTY;
    String cookiedomain = "auto";
    String trackingname = StringUtils.EMPTY;

    if(cfgMgr != null) {

        gaConfiguration = cfgMgr.getConfiguration("googleanalytics", services);

        if (gaConfiguration != null) {
            trackingid  = gaConfiguration.get("trackingId", trackingid);
            cookiedomain = gaConfiguration.get("cookieDomain", cookiedomain);
            trackingname = gaConfiguration.get("trackingName", trackingname);
        }
    }

    //no lambada is available so this is the best that can be done
    Object[][] componentFields = {
            {"component", "googleanalytics", "modules"},
            {"trackingId", trackingid, "trackingid"},
            {"cookieDomain", cookiedomain, "cookiedomain"},
            {"trackingName", trackingname, "trackingname"}
    };



    ComponentProperties componentProperties = getComponentProperties(pageContext, componentFields);
%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:choose>
    <c:when test="${not empty componentProperties.trackingId }">
        <%@ include file="variant.default.jsp"  %>
    </c:when>

    <c:otherwise>
        <%@ include file="variant.empty.jsp"  %>
    </c:otherwise>
</c:choose>

<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
