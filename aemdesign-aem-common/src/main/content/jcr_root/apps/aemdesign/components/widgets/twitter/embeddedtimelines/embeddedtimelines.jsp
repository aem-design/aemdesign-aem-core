<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<%

    /*Map<String, Object> style = new HashMap<String, Object>();

    String userId = _properties.get("userId", "");
    String widgetId = _properties.get("widgetId", "");

    pageContext.setAttribute("userId", userId, PageContext.PAGE_SCOPE);
    pageContext.setAttribute("widgetId", widgetId, PageContext.PAGE_SCOPE);

    style.put("width", _properties.get("width", _currentStyle.get("width", "")));
    style.put("height", _properties.get("height", _currentStyle.get("height", "")));
    style.put("theme", _properties.get("theme", _currentStyle.get("theme", "")));
    style.put("linkColor", _properties.get("linkColor", _currentStyle.get("linkColor", "")));
    style.put("chrome", _properties.get("chrome", _currentStyle.get("chrome", "")));
    style.put("borderColor", _properties.get("borderColor", _currentStyle.get("borderColor", "")));
    style.put("language", _properties.get("language", _currentStyle.get("language", "")));
    style.put("limit", _properties.get("limit", _currentStyle.get("limit", "")));
    style.put("related", _properties.get("related", _currentStyle.get("related", "")));
    style.put("polite", _properties.get("polite", _currentStyle.get("polite", "")));
    style.put("screenName", _properties.get("screenName", _currentStyle.get("screenName", "")));
    style.put("listOwnerScreenName", _properties.get("listOwnerScreenName", _currentStyle.get("listOwnerScreenName", "")));
    style.put("listSlug", _properties.get("listSlug", _currentStyle.get("listSlug", "")));
    style.put("showReplies", _properties.get("showReplies", _currentStyle.get("showReplies", "")));
    style.put("favoritesScreenName", _properties.get("favoritesScreenName", _currentStyle.get("favoritesScreenName", "")));
    style.put("favoritesScreenId", _properties.get("favoritesScreenId", _currentStyle.get("favoritesScreenId", "")));*/
    Object[][] componentFields = {
            {"href", StringUtils.EMPTY},
            {"hrefTitle", StringUtils.EMPTY},
            {"widgetScriptSrc", StringUtils.EMPTY},
            {"widgetWidth", StringUtils.EMPTY},
            {"widgetHeight", StringUtils.EMPTY},
            {"fileReference", StringUtils.EMPTY},
            {"cssClass", StringUtils.EMPTY},
            {"attrId", StringUtils.EMPTY},
            {"cssClassWidgetHeader", StringUtils.EMPTY},
            {"cssClassImage", StringUtils.EMPTY},
            {"cssClassTwitterFeed", StringUtils.EMPTY},
            {"cssClassTwitterTimeline", StringUtils.EMPTY},
            {"cssIDTwitterWidget", StringUtils.EMPTY},
            {"cssIDTwitterScript", StringUtils.EMPTY}

    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);

%>
<%--<c:set var="style" value="<%= style %>" />--%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>

<%--<c:choose>
    <c:when test="${not empty userId &&  not empty widgetId}">
    <a class="twitter-timeline" href="https://twitter.com/${userId}" data-widget-id="${widgetId}"
            <c:if test="${style.width != ''}">width="${style.width}"</c:if>
            <c:if test="${style.height != ''}">height="${style.width}"</c:if>
            <c:if test="${style.theme != ''}">data-theme="${style.theme}"</c:if>
            <c:if test="${style.linkColor != ''}">data-link-color="${style.width}"</c:if>
            <c:if test="${style.chrome != ''}">data-chrome="${style.chrome}"</c:if>
            <c:if test="${style.borderColor != ''}">data-border-color="${style.borderColor}"</c:if>
            <c:if test="${style.language != ''}">data-lang="${style.language}"</c:if>
            <c:if test="${style.limit != ''}">data-tweet-limit="${style.limit}"</c:if>
            <c:if test="${style.related != ''}">data-related="${style.related}"</c:if>
            <c:if test="${style.polite != ''}">data-aria-polite="${style.polite}"</c:if>
            <c:if test="${style.screenName != ''}">data-screen-name="${style.screenName}"</c:if>
            <c:if test="${style.listOwnerScreenName != ''}">data-list-owner-screen-name="${style.listOwnerScreenName}"</c:if>
            <c:if test="${style.listSlug != ''}">data-list-slug="${style.listSlug}"</c:if>
            <c:if test="${style.showReplies != ''}">data-show-replies="${style.showReplies}"</c:if>
            <c:if test="${style.favoritesScreenName != ''}">data-favorites-screen-name="${style.favoritesScreenName}"</c:if>
            <c:if test="${style.favoritesScreenId != ''}">data-favorites-screen-id="${style.favoritesScreenId}"</c:if>>Tweets by @${userId}</a>
    <script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0],p=/^http:/.test(d.location)?'http':'https';if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src=p+"://platform.twitter.com/widgets.js";fjs.parentNode.insertBefore(js,fjs);}}(document,"script","twitter-wjs");</script>
    </c:when>
    <c:otherwise>
        <h3 class="cq-texthint-placeholder">Twitter user id and widget id not set</h3>
    </c:otherwise>
</c:choose>--%>
<div class="${componentProperties.cssClass}">
    <div class="${componentProperties.cssClassWidgetHeader}"><img src="${componentProperties.fileReference}" class="${cssClassHeader.cssClassImage}"/>
        <cq:text property="text" tagClass="text"/>
    </div>
    <div class="${componentProperties.cssClassTwitterFeed}"><a href="${componentProperties.href}" data-widget-id="${componentProperties.cssIDTwitterWidget}" class="${componentProperties.cssClassTwitterTimeline}">${componentProperties.hrefTitle}</a>
        <script id="${componentProperties.cssIDTwitterScript}" src="${componentProperties.widgetScriptSrc}"></script>
        <script>
            !function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0],p=/^http:/.test(d.location)?'http':'https';
                if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src="https://platform.twitter.com/widgets.js";fjs.parentNode.insertBefore(js,fjs);}}
            (document,"script","twitter-wjs");
        </script>
    </div>
</div>

<%@include file="/apps/aemdesign/global/component-badge.jsp" %>