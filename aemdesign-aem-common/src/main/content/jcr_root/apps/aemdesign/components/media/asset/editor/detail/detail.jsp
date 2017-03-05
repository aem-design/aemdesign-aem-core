<%@taglib prefix="xss" uri="http://www.adobe.com/consulting/acs-aem-commons/xss" %>
<%@page session="false"%>
<%@include file="/apps/aemdesign/global/global.jsp" %>
<%@include file="/apps/aemdesign/global/components.jsp" %>
<%@page import="java.util.List" %>
<%@page import="com.day.cq.dam.api.Asset" %>
<%@page import="com.day.cq.wcm.foundation.forms.FormResourceEdit" %>
<%@page import="com.day.cq.wcm.foundation.forms.FormsHelper" %>
<%
    Object[][] componentFields = {
            {"authorDescription", StringUtils.EMPTY},
            {"title", StringUtils.EMPTY}
    };

    ComponentProperties componentProperties = getComponentProperties(pageContext, componentFields);

    componentProperties.putAll(getComponentStyleProperties(pageContext));

    List<Resource> resources = FormResourceEdit.getResources(_slingRequest);

    if (resources != null) {

        Resource r = resources.get(0);
        String title = StringUtils.EMPTY;
        String path = StringUtils.EMPTY;
        String authorDescription = StringUtils.EMPTY;

        path = r.getPath();
        Asset asset = r.adaptTo(Asset.class);
        try {
            // it might happen that the adobe xmp lib creates an array
            Object titleObj = asset.getMetadata("dc:title");
            if (titleObj instanceof Object[]) {
                Object[] titleArray = (Object[]) titleObj;
                title = (titleArray.length > 0) ? titleArray[0].toString() : "";
            } else {
                title = titleObj.toString();
            }
        } catch (NullPointerException e) {
            title = path.substring(path.lastIndexOf("/") + 1);
        }
        componentProperties.put("title", title);


        try {
            Object descObj = asset.getMetadata("wkcd_dam:authorDescription");
            if (descObj instanceof Object[]) {
                Object[] descArray = (Object[]) descObj;
                authorDescription = (descArray.length > 0) ? descArray[0].toString() : "";
            } else {
                authorDescription = descObj.toString();
            }
        } catch (NullPointerException e) {
            _log.error("failed to retrieve metadata object " + e.getMessage(), e);
        }
        componentProperties.put("authorDescription", authorDescription);


        String pageTitle = title ;
        if (authorDescription.length() > 0){
            pageTitle += authorDescription;
        }
        componentProperties.put("pageTitle", pageTitle);
    }
%>
<c:set var="componentProperties" value="<%= componentProperties %>" />

    <%@ include file="style.default.jsp" %>

<%--<%@include file="/apps/aemdesign/global/component-badge.jsp" %>--%>