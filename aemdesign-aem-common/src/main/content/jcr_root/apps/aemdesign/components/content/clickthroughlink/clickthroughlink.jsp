<%@ page import="com.day.cq.wcm.api.WCMMode" %>
<%@ page import="java.net.URL" %>
<%@ page import="java.net.MalformedURLException" %>
<%@ include file="/apps/aemdesign/global/global.jsp"%>
<%@ include file="/apps/aemdesign/global/components.jsp"%>
<%

    Object[][] componentFields = {
            {"variant", "none"},

            {"hitType", StringUtils.EMPTY},
            {"eventCategory", StringUtils.EMPTY},
            {"eventAction", StringUtils.EMPTY},
            {"eventLabel", StringUtils.EMPTY},

            {"css", StringUtils.EMPTY},
            {"hrefTargets", StringUtils.EMPTY},
            {"id", StringUtils.EMPTY},
            {"linkUrl", StringUtils.EMPTY},
            {"label", _i18n.get("Edit Me")},
    };

    ComponentProperties componentProperties = getComponentProperties(pageContext, componentFields);
    componentProperties.putAll(getComponentStyleProperties(pageContext));

    componentProperties.put("componentAttributes", compileComponentAttributesAsAdmin(componentProperties,_component,_sling));

    String linkUrl = componentProperties.get("linkUrl", StringUtils.EMPTY);

    if (linkUrl.length() > 0){
        try {
            new URL(linkUrl);
        } catch (MalformedURLException e) {

            if(!linkUrl.endsWith(DEFAULT_PAGE_EXTENTION) && !linkUrl.contains("#")){
                linkUrl= linkUrl+ DEFAULT_PAGE_EXTENTION;
            }

        }
        linkUrl = _xssAPI.getValidHref(linkUrl);
        componentProperties.put("linkUrl", linkUrl);
    }
    String css = componentProperties.get("css", StringUtils.EMPTY);
    if (css.length() > 0){
        css = " class=\"" + _xssAPI.encodeForHTMLAttr(css) +"\"";
        componentProperties.put("css", css);
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


<%@ include file="variant.default.jsp" %>
<c:choose>
    <c:when test="${componentProperties.variant eq 'aa'}">
        <%@include file="aa-tracking-js.jsp" %>
    </c:when>
    <c:when test="${componentProperties.variant eq 'ga'}">
        <%@include file="ga-tracking-js.jsp" %>
    </c:when>
    <c:otherwise>

    </c:otherwise>
</c:choose>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
