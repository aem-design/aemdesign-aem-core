<%@taglib prefix="xss" uri="http://www.adobe.com/consulting/acs-aem-commons/xss" %>
<%@page session="false"%>
<%@include file="/apps/aemdesign/global/global.jsp" %>
<%@include file="/apps/aemdesign/global/components.jsp" %>
<%@page import="java.util.List" %>
<%@page import="com.day.cq.dam.api.Asset" %>
<%@page import="com.day.cq.wcm.foundation.forms.FormResourceEdit" %>
<%@page import="com.day.cq.wcm.foundation.forms.FormsHelper" %>
<%@ page import="org.apache.commons.lang3.BooleanUtils" %>
<%
    final String DEFAULT_SHOW_COPYRIGHT_OWNER = "yes";
    final String I18N_CATEGORY = "assetviewer";


    Object[][] componentFields = {
            {"variant", "aemdesign"},
            {"path", "/etc/designs/default/images/dam/asseteditor/thumbnail/placeholder.gif"},
            {"profile", "assetViewer"},
            {"title", StringUtils.EMPTY},
            {"copyrightOwner", StringUtils.EMPTY},
            {"showCopyrightOwner", DEFAULT_SHOW_COPYRIGHT_OWNER}
    };

    ComponentProperties componentProperties = getComponentProperties(pageContext, componentFields);

    componentProperties.putAll(getComponentStyleProperties(pageContext));

    componentProperties.put("showCopyrightOwner", BooleanUtils.toBoolean(componentProperties.get("showCopyrightOwner", String.class)));

    List<Resource> resources = FormResourceEdit.getResources(_slingRequest);

    if (resources != null) {

        if (resources.size() > 1) {

        } else {

            Resource r = resources.get(0);

            String path = r.getPath() +".transform/" + componentProperties.get("profile", String.class) + "/image.jpg";

            componentProperties.put("path", path);

            Asset asset = r.adaptTo(Asset.class);

            String title = StringUtils.EMPTY;

            String copyrightOwner = StringUtils.EMPTY;

            try {
                // it might happen that the adobe xmp lib creates an array
                Object titleObj = asset.getMetadata("dc:title");
                if (titleObj instanceof Object[]) {
                    Object[] titleArray = (Object[]) titleObj;
                    title = (titleArray.length > 0) ? titleArray[0].toString() : "";
                } else {
                    title = titleObj.toString();
                }
                componentProperties.put("title", title);

                Object copyrightOwnerObj = asset.getMetadata("xmpRights:Owner");
                if (copyrightOwnerObj instanceof Object[]) {
                    Object[] copyrightOwnerArray = (Object[]) copyrightOwnerObj;
                    copyrightOwner = (copyrightOwnerArray.length > 0) ? copyrightOwnerArray[0].toString() : "";
                } else {
                    copyrightOwner = copyrightOwnerObj.toString();
                }
                componentProperties.put("copyrightOwner", copyrightOwner);


            } catch (NullPointerException e) {
                _log.error("failed to retrieve metadata object " + e.getMessage(), e);
            }
        }
    }
    componentProperties.put("titleAltReadMore", _xssAPI.encodeForHTMLAttr(_i18n.get("titleAltReadMore", I18N_CATEGORY)));


%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:choose>
    <c:when test="${componentProperties.variant == 'aemdesign'}">
        <%@ include file="variant.default.jsp" %>
    </c:when>
</c:choose>
<%--<%@include file="/apps/aemdesign/global/component-badge.jsp" %>--%>
