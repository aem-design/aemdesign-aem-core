<%@ page import="com.day.cq.wcm.api.WCMMode" %>
<%@ page import="java.net.URL" %>
<%@ page import="java.net.MalformedURLException" %>
<%@ include file="/apps/aemdesign/global/global.jsp"%>
<%@ include file="/apps/aemdesign/global/components.jsp"%>
<%

    Object[][] componentFields = {
            {"hrefTargets", StringUtils.EMPTY},
            {"linkUrl", StringUtils.EMPTY, "data-href"},
            {"analyticsType", StringUtils.EMPTY},
            {"label", _i18n.get("Edit Me")},
    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY,
            DEFAULT_FIELDS_ANALYTICS);

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

    String hrefTargets = componentProperties.get("hrefTargets", StringUtils.EMPTY);
    if (hrefTargets.length() > 0){
        hrefTargets = " target=\"" + _xssAPI.encodeForHTMLAttr(hrefTargets) +"\"";
        componentProperties.put("hrefTargets", hrefTargets);
    }

    String divId = "cq-ctalink-jsp-" + _resource.getPath();
    divId = _xssAPI.encodeForHTMLAttr(divId);
    componentProperties.put("divId", divId);

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

<c:choose>
    <c:when test="${componentProperties.analyticsType eq 'aa'}">
        <%@include file="aa-tracking-js.jsp" %>
    </c:when>
    <c:when test="${componentProperties.analyticsType eq 'ga'}">
        <%@include file="ga-tracking-js.jsp" %>
    </c:when>
    <c:otherwise>

    </c:otherwise>
</c:choose>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
