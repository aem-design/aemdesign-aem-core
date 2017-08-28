<%@ page import="com.day.cq.wcm.api.WCMMode" %>
<%@ page import="java.net.URL" %>
<%@ page import="java.net.MalformedURLException" %>
<%@ include file="/apps/aemdesign/global/global.jsp"%>
<%@ include file="/apps/aemdesign/global/components.jsp"%>
<%@ include file="/apps/aemdesign/global/i18n.jsp" %>
<%

    final String FIELD_LINKURL = "linkUrl";
    final String DEFAULT_LINKURL = "#";
    final String DEFAULT_I18N_CATEGORY = "link";
    final String DEFAULT_I18N_LABEL = "linklabel";


    // {
    //   1 required - property name,
    //   2 required - default value,
    //   3 optional - compile into a data-{name} attribute
    // }

    Object[][] componentFields = {
            {"linkTarget", StringUtils.EMPTY, "target"},
            {FIELD_LINKURL, StringUtils.EMPTY},
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {"linkId", _xssAPI.encodeForHTMLAttr(_resource.getPath())},
            {"label", getDefaultLabelIfEmpty("",DEFAULT_I18N_CATEGORY,DEFAULT_I18N_LABEL,DEFAULT_I18N_CATEGORY,_i18n)},
            {"componentPath", getResourceContentPath(resource) , "data-component-path"},
    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY,
            DEFAULT_FIELDS_ANALYTICS,
            DEFAULT_FIELDS_ATTRIBUTES);

    String linkUrl = componentProperties.get(FIELD_LINKURL, StringUtils.EMPTY);

    if (isNotEmpty(linkUrl)) {
        if(!linkUrl.endsWith(DEFAULT_EXTENTION) && !linkUrl.contains(DEFAULT_LINKURL)){
            linkUrl= linkUrl.concat(DEFAULT_EXTENTION);
        }

        linkUrl = _xssAPI.getValidHref(linkUrl);

        componentProperties.put(COMPONENT_ATTRIBUTES, addComponentAttributes(componentProperties,"href",linkUrl));

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
