<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>


<%
    /*Map<String, Object> info = new HashMap<String, Object>();

    String dataHref = properties.get("href", StringUtils.EMPTY),
           dataWidth = _properties.get("width", ""),
           dataHeight = _properties.get("height", ""),
           dataShowFaces = _properties.get("showFaces", ""),
           dataShowStream = _properties.get("showStream", ""),
           dataShowHeader = _properties.get("showHeader", ""),
           dataColorScheme = _properties.get("colorScheme", ""),
           dataShowScrolling = properties.get("showScrolling", "yes"),
           dataShowBorder = properties.get("showBorder", "0"),
           dataAllowTransparency = properties.get("allowTransparency", "true");

    if(dataHref!=null && dataHref.length() > 0)
       info.put("dataHref"," data-href="+dataHref);
    else
        info.put("dataHref"," data-href=www.facebook.com/nrl");
    if(dataWidth!=null && dataWidth.length() > 0)
       info.put("dataWidth"," data-width="+dataWidth);
    if(dataHeight!=null && dataHeight.length() > 0)
       info.put("dataHeight"," data-height="+dataHeight);
    if(dataShowFaces!=null && dataShowFaces.length() > 0)
       info.put("dataShowFaces"," data-show-faces="+dataShowFaces);
    if(dataShowStream!=null && dataShowStream.length() > 0)
       info.put("dataShowStream"," data-stream="+dataShowStream);
    if(dataShowHeader!=null && dataShowHeader.length() > 0)
       info.put("dataShowHeader"," data-header="+dataShowHeader);
    if(dataColorScheme!=null && dataColorScheme.length() > 0)
       info.put("dataColorScheme"," data-colorscheme="+dataColorScheme);
    if(dataShowBorder!=null && dataShowBorder.length() > 0)
       info.put("dataShowBorder"," data-show-border="+dataShowBorder);*/

    //no lambada is available so this is the best that can be done
    Object[][] componentFields = {
            {"href", StringUtils.EMPTY},
            /*{"handle", StringUtils.EMPTY},*/
            {"widgetWidth", StringUtils.EMPTY},
            {"widgetHeight", StringUtils.EMPTY},
            {"showScrolling", StringUtils.EMPTY},
            {"showBorder", StringUtils.EMPTY},
            {"allowTransparency", StringUtils.EMPTY},
            {"fileReference", StringUtils.EMPTY},
            {"cssClass", StringUtils.EMPTY},
            {"attrId", StringUtils.EMPTY},
            {"cssClassHeader", StringUtils.EMPTY},
            {"cssClassImage", StringUtils.EMPTY},
            {"cssClassFBFeed", StringUtils.EMPTY},
            {"cssIDFBFeed", StringUtils.EMPTY}

    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);


%>
<%--<c:set var="info" value="<%= info %>" />--%>

<c:set var="componentProperties" value="<%= componentProperties %>"/>

<%--<div class="fb-like-box" ${info.dataHref} ${info.dataWidth} ${info.dataHeight} ${info.dataShowFaces} ${info.dataShowStream} ${info.dataShowHeader} ${info.dataShowBorder} ${info.dataColorScheme}></div>--%>
<div class="${componentProperties.cssClass}">
    <header class="${componentProperties.cssClassHeader}"><img src="${componentProperties.fileReference}" class="${cssClassHeader.cssClassImage}"/>
        <%--<h4>/${componentProperties.handle}</h4>--%>
        <cq:text property="text" tagClass="text"/>
    </header>
    <div class="${componentProperties.cssClassFBFeed}">
        <iframe src="${componentProperties.href}" scrolling="${componentProperties.showScrolling}" frameborder="${componentProperties.showBorder}" allowTransparency="${componentProperties.allowTransparency}"></iframe>
    </div>
</div>

<%@include file="/apps/aemdesign/global/component-badge.jsp" %>