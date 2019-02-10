<%-- BADGE LINK CONFIG  --%>
<c:if test="${not empty componentProperties.linkTarget}">
    <c:set var="badgeLinkAttr" value="${badgeLinkAttr} target=\"${componentProperties.linkTarget}\""/>
</c:if>
<c:if test="${not empty componentProperties.redirectTarget}">
    <c:set var="badgeLinkAttr" value="${badgeLinkAttr} external"/>
</c:if>
<%-- BADGE LINK CONFIG - ANALYTICS --%>
<c:if test="${not empty componentProperties.badgeAnalyticsEventType}">
    <c:set var="badgeLinkAttr" value="${badgeLinkAttr} data-layer-event=\"${componentProperties.badgeAnalyticsEventType}\""/>
</c:if>
<c:if test="${not empty componentProperties.badgeAnalyticsLinkType}">
    <c:set var="badgeLinkAttr" value="${badgeLinkAttr} data-layer-linktype=\"${componentProperties.badgeAnalyticsLinkType}\""/>
</c:if>
<c:if test="${not empty componentProperties.badgeAnalyticsLinkLocation}">
    <c:set var="badgeLinkAttr" value="${badgeLinkAttr} data-layer-linklocation=\"${componentProperties.badgeAnalyticsLinkLocation}\""/>
</c:if>
<c:if test="${not empty componentProperties.badgeAnalyticsLinkDescription}">
    <c:set var="badgeLinkAttr" value="${badgeLinkAttr} data-layer-linkdescription=\"${componentProperties.badgeAnalyticsLinkDescription}\""/>
</c:if>
<c:if test="${not empty componentProperties.componentInPagePath}">
    <c:set var="badgeLinkAttr" value="${badgeLinkAttr} ${COMPONENT_ATTRIBUTE_INPAGEPATH}=\"${componentProperties.componentInPagePath}\""/>
</c:if>


<%-- BADGE IMAGE CONFIG  --%>
<c:if test="${not empty componentProperties.pageImageId}">
    <c:set var="badgeImageAttr" value="${badgeImageAttr} data-asset-id-primary=\"${componentProperties.pageImageId}\""/>
</c:if>
<c:if test="${not empty componentProperties.pageImageLicenseInfo}">
    <c:set var="badgeImageAttr" value="${badgeImageAttr} data-asset-license-primary=\"${componentProperties.pageImageLicenseInfo}\""/>
</c:if>
<c:if test="${not empty componentProperties.pageSecondaryImageId}">
    <c:set var="badgeImageAttr" value="${badgeImageAttr} data-asset-id-secondary=\"${componentProperties.pageSecondaryImageId}\""/>
</c:if>
<c:if test="${not empty componentProperties.pageSecondaryImageLicenseInfo}">
    <c:set var="badgeImageAttr" value="${badgeImageAttr} data-asset-licenses-secondary=\"${componentProperties.pageSecondaryImageLicenseInfo}\""/>
</c:if>
<c:if test="${not empty componentProperties.thumbnailWidth}">
    <c:set var="badgeImageAttr" value="${badgeImageAttr} width=\"${componentProperties.thumbnailWidth}\""/>
</c:if>
<c:if test="${not empty componentProperties.thumbnailHeight}">
    <c:set var="badgeImageAttr" value="${badgeImageAttr} height=\"${componentProperties.thumbnailHeight}\""/>
</c:if>

<%-- BADGE CLASS CONFIG  --%>
<c:if test="${fn:length(componentProperties.cardStyle) > 0}">
    <c:set var="badgeClassAttr" value="${badgeClassAttr} ${fn:join(componentProperties.cardStyle,' ')}"/>
</c:if>
<c:if test="${componentProperties.titleIconShow and fn:length(componentProperties.titleIcon) > 0}">
    <c:set var="badgeClassAttr" value="${badgeClassAttr} ${fn:join(componentProperties.titleIcon,' ')}"/>
</c:if>

<%-- BADGE CARD ICON CONFIG  --%>
<c:if test="${componentProperties.cardIconShow and fn:length(componentProperties.cardIcon) > 0}">
    <c:set var="badgeClassIconAttr" value="${badgeClassIconAttr} ${fn:join(componentProperties.cardIcon,' ')}"/>
</c:if>

<%-- BADGE TITLE TRIM CONFIG  --%>
<c:if test="${componentProperties.badgeTitleTrim}">
    <c:if test="${empty componentProperties.badgeTitleTrimLengthMax}">
        <c:set target="${componentProperties}" property="badgeTitleTrimLengthMax" value="20" />
    </c:if>
    <c:if test="${fn:length(componentProperties.pageNavTitle) > componentProperties.badgeTitleTrimLengthMax}">
        <c:set target="${componentProperties}" property="pageNavTitle" value="${fn:substring(componentProperties.pageNavTitle,0,componentProperties.badgeTitleTrimLengthMax)}${componentProperties.badgeTitleTrimLengthMaxSuffix}"/>
    </c:if>
</c:if>


<%-- BADGE DESCRIPTION TRIM CONFIG  --%>
<c:if test="${componentProperties.badgeSummaryTrim}">
    <c:if test="${empty componentProperties.badgeSummaryLengthMax}">
        <c:set target="${componentProperties}" property="badgeSummaryLengthMaxSuffix" value="50" />
    </c:if>
    <c:if test="${fn:length(componentProperties.description) > componentProperties.badgeSummaryLengthMax}">
        <c:set target="${componentProperties}" property="description" value="${fn:substring(componentProperties.description,0,componentProperties.badgeSummaryLengthMax)}${componentProperties.badgeSummaryLengthMaxSuffix}"/>
    </c:if>
</c:if>


