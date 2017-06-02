<%@ page import="com.day.cq.wcm.api.WCMMode" %>
<%@ page import="java.net.URL" %>
<%@ page import="java.net.MalformedURLException" %>
<%@ include file="/apps/aemdesign/global/global.jsp"%>
<%@ include file="/apps/aemdesign/global/components.jsp"%>
<%

    Object[][] componentFields = {
            {"linkTarget", StringUtils.EMPTY, "target"},
            {"linkUrl", StringUtils.EMPTY, "href"},
            {"analyticsType", StringUtils.EMPTY, "data-analytics-type"},
            {"variant", StringUtils.EMPTY},
            {"linkId", _xssAPI.encodeForHTMLAttr(_resource.getPath())},
            {"label", _i18n.get("Edit Me")},
            {"componentPath", getResourceContentPath(resource) , "data-component-path"},
    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY,
            DEFAULT_FIELDS_ANALYTICS,
            DEFAULT_FIELDS_ATTRIBUTES);

    String linkUrl = componentProperties.get("linkUrl", StringUtils.EMPTY);

    if (linkUrl.length() > 0){
        try {
            new URL(linkUrl);
        } catch (MalformedURLException e) {

            if(!linkUrl.endsWith(DEFAULT_EXTENTION) && !linkUrl.contains("#")){
                linkUrl= linkUrl.concat(DEFAULT_EXTENTION);
            }

        }
        linkUrl = _xssAPI.getValidHref(linkUrl);
        componentProperties.put("linkUrl", linkUrl);
    }

%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>

<c:choose>
    <c:when test="${componentProperties.variant eq 'button'}">
        <%@ include file="variant.button.jsp" %>
    </c:when>
    <c:otherwise>
        <%@ include file="variant.default.jsp" %>
    </c:otherwise>
</c:choose>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
