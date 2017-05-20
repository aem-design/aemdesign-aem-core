<%@ page import="com.day.cq.wcm.api.WCMMode" %>
<%@ page import="com.day.cq.wcm.foundation.External" %>
<%@ page import="org.apache.commons.lang3.StringUtils"%>
<%@ page import="org.apache.sling.api.resource.ResourceUtil"%>
<%@ include file="/apps/aemdesign/global/global.jsp"%>
<%@ include file="/apps/aemdesign/global/components.jsp"%>
<%@ page import="org.apache.sling.api.resource.NonExistingResource" %>
<%
    Object[][] componentFields = {
            {"backgroundColor", ""},
            {"backgroundTextColor", ""},
            {"backgroundAccentColor", ""},
            {"featureBackgroundColor", ""},
            {"featureTextColor", ""},
            {"navigationHighlightColor", ""},
            {"cssPath", ""}
    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);

    String backgroundColor = (String)componentProperties.get("backgroundColor");
    String backgroundTextColor = (String)componentProperties.get("backgroundTextColor");
    String backgroundAccentColor = (String)componentProperties.get("backgroundAccentColor");
    String featureBackgroundColor = (String)componentProperties.get("featureBackgroundColor");
    String featureTextColor = (String)componentProperties.get("featureTextColor");
    String navigationHighlightColor = (String)componentProperties.get("navigationHighlightColor");
    String cssPath = (String)componentProperties.get("cssPath");

    response.setHeader("Content-Type", "text/css");

    if (!"".equals(cssPath)) {
        Resource res = _resourceResolver.getResource(cssPath);
        if (!(res instanceof NonExistingResource)) {
            Node jcrContent = res.adaptTo(Node.class).getNode("jcr:content");
            Property jcrData = jcrContent.getProperty("jcr:data");
            String css = jcrData.getString();

            css = css.replaceAll("@background-color", "#" + backgroundColor);
            css = css.replaceAll("@background-text-color", "#" + backgroundTextColor);
            css = css.replaceAll("@background-accent-color", "#" + backgroundAccentColor);
            css = css.replaceAll("@feature-background-color", "#" + featureBackgroundColor);
            css = css.replaceAll("@feature-text-color", "#" + featureTextColor);
            css = css.replaceAll("@Navigation-highlight-color", "#" + navigationHighlightColor);
            out.print(css);
        }
    }
%>
