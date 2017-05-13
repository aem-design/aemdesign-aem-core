<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ page import="org.apache.commons.lang3.StringEscapeUtils" %>
<%@ page import="org.apache.sling.api.resource.NonExistingResource" %>
<head>
    <meta charset="utf-8"/>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=0"/>
    <meta name="keywords" content="${componentProperties.keywords}"/>
    <title>${componentProperties.pageTitle}</title>
    <!-- og:tags -->
    <meta name="description" content="${componentProperties.description}"/>
    <c:if test="${CURRENT_WCMMODE ne WCMMODE_DISABLED}">
        <cq:include script="/libs/wcm/core/components/init/init.jsp"/><%-- init.jsp is for author --%>
        <cq:include script="/libs/foundation/components/page/stats.jsp"/>
        <cq:include script="/libs/wcm/mobile/components/simulator/simulator.jsp"/>
        <%currentDesign.writeCssIncludes(pageContext); %>
    </c:if>

    <cq:include script="headlibs.jsp"/>
    <link href="" data-href="" rel="stylesheet" id="theme-stylesheet">

    <c:if test="${not empty componentProperties.faviconsPath}">
    <link rel="apple-touch-icon" sizes="57x57" href="${componentProperties.faviconsPath}/apple-touch-icon-57x57.png">
    <link rel="apple-touch-icon" sizes="60x60" href="${componentProperties.faviconsPath}/apple-touch-icon-60x60.png">
    <link rel="apple-touch-icon" sizes="72x72" href="${componentProperties.faviconsPath}/apple-touch-icon-72x72.png">
    <link rel="apple-touch-icon" sizes="76x76" href="${componentProperties.faviconsPath}/apple-touch-icon-76x76.png">
    <link rel="apple-touch-icon" sizes="114x114" href="${componentProperties.faviconsPath}/apple-touch-icon-114x114.png">
    <link rel="apple-touch-icon" sizes="120x120" href="${componentProperties.faviconsPath}/apple-touch-icon-120x120.png">
    <link rel="apple-touch-icon" sizes="144x144" href="${componentProperties.faviconsPath}/apple-touch-icon-144x144.png">
    <link rel="apple-touch-icon" sizes="152x152" href="${componentProperties.faviconsPath}/apple-touch-icon-152x152.png">
    <link rel="apple-touch-icon" sizes="180x180" href="${componentProperties.faviconsPath}/apple-touch-icon-180x180.png">
    <link rel="icon" type="image/png" href="${componentProperties.faviconsPath}/favicon-32x32.png" sizes="32x32">
    <link rel="icon" type="image/png" href="${componentProperties.faviconsPath}/android-chrome-192x192.png" sizes="192x192">
    <link rel="icon" type="image/png" href="${componentProperties.faviconsPath}/favicon-16x16.png" sizes="16x16">
    <link rel="icon" type="image/vnd.microsoft.icon" href="${componentProperties.faviconsPath}/favicon.ico" />
    <link rel="shortcut icon" type="image/x-icon" href="${componentProperties.faviconsPath}/favicon.ico"/>
    <link rel="manifest" href="${componentProperties.faviconsPath}/manifest.json">
    <link rel="mask-icon" href="${componentProperties.faviconsPath}/safari-pinned-tab.svg" color="#5bbad5">
    <meta name="msapplication-TileColor" content="#da532c">
    <meta name="msapplication-TileImage" content="${componentProperties.faviconsPath}/mstile-144x144.png">
    <meta name="theme-color" content="#ffffff">
    </c:if>

    <link rel="canonical" href="${componentProperties.canonicalUrl}" />

</head>
