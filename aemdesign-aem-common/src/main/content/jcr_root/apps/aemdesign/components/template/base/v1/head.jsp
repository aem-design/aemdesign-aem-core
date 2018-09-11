<%@ page session="false" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ page import="org.apache.commons.lang3.StringEscapeUtils" %>
<%@ page import="org.apache.sling.api.resource.NonExistingResource" %>
<c:set var="templateProperties" value="<%= request.getAttribute("templateProperties") %>"/>
<head>
    <meta charset="utf-8"/>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=0"/>
    <meta name="keywords" content="${templateProperties.keywords}"/>
    <title>${templateProperties.pageTitle}</title>
    <meta name="description" content="${templateProperties.description}"/>
    <c:if test="${CURRENT_WCMMODE ne WCMMODE_DISABLED}">
        <c:if test="${INCLUDE_PAGE_COMPONENTINIT}">
            <cq:include script="/libs/wcm/core/components/init/init.jsp"/><%-- init.jsp is for author --%>
        </c:if>
    </c:if>

    <cq:include script="headlibs.jsp"/>

    <c:if test="${not empty templateProperties.themeStyle}">
        <cq:includeClientLib categories="${templateProperties.themeStyle}"/>
    </c:if>

    <c:if test="${not empty templateProperties.faviconsPath}">
    <link rel="apple-touch-icon" sizes="57x57" href="${templateProperties.faviconsPath}/apple-touch-icon-57x57.png">
    <link rel="apple-touch-icon" sizes="60x60" href="${templateProperties.faviconsPath}/apple-touch-icon-60x60.png">
    <link rel="apple-touch-icon" sizes="72x72" href="${templateProperties.faviconsPath}/apple-touch-icon-72x72.png">
    <link rel="apple-touch-icon" sizes="76x76" href="${templateProperties.faviconsPath}/apple-touch-icon-76x76.png">
    <link rel="apple-touch-icon" sizes="114x114" href="${templateProperties.faviconsPath}/apple-touch-icon-114x114.png">
    <link rel="apple-touch-icon" sizes="120x120" href="${templateProperties.faviconsPath}/apple-touch-icon-120x120.png">
    <link rel="apple-touch-icon" sizes="144x144" href="${templateProperties.faviconsPath}/apple-touch-icon-144x144.png">
    <link rel="apple-touch-icon" sizes="152x152" href="${templateProperties.faviconsPath}/apple-touch-icon-152x152.png">
    <link rel="apple-touch-icon" sizes="180x180" href="${templateProperties.faviconsPath}/apple-touch-icon-180x180.png">
    <link rel="icon" type="image/png" href="${templateProperties.faviconsPath}/favicon-32x32.png" sizes="32x32">
    <link rel="icon" type="image/png" href="${templateProperties.faviconsPath}/android-chrome-192x192.png" sizes="192x192">
    <link rel="icon" type="image/png" href="${templateProperties.faviconsPath}/favicon-16x16.png" sizes="16x16">
    <link rel="icon" type="image/vnd.microsoft.icon" href="${templateProperties.faviconsPath}/favicon.ico" />
    <link rel="shortcut icon" type="image/x-icon" href="${templateProperties.faviconsPath}/favicon.ico"/>
    <link rel="manifest" href="${templateProperties.faviconsPath}/manifest.json">
    <link rel="mask-icon" href="${templateProperties.faviconsPath}/safari-pinned-tab.svg" color="#5bbad5">
    <meta name="msapplication-TileImage" content="${templateProperties.faviconsPath}/mstile-144x144.png">
    </c:if>
    <c:if test="${not empty templateProperties.siteThemeColor}">
    <meta name="theme-color" content="${templateProperties.siteThemeColor}">
    </c:if>
    <c:if test="${not empty templateProperties.siteTileColor}">
    <meta name="msapplication-TileColor" content="${templateProperties.siteTileColor}">
    </c:if>

    <link rel="canonical" href="${templateProperties.canonicalUrl}" />

    <cq:include script="metadata.jsp"/>
</head>
