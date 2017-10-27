<c:if test="${not empty componentProperties.linkTarget}">
    <c:set var="linkAttr" value="${linkAttr} target=\"${componentProperties.linkTarget}\""/>
</c:if>

<c:if test="${not empty componentProperties.pageImageId and empty componentProperties.pageSecondaryImageId}">
    <c:set var="imageAttr" value="${imageAttr} data-asset-id-primary=\"${componentProperties.pageImageId}\""/>
</c:if>
<c:if test="${not empty componentProperties.pageImageLicenseInfo}">
    <c:set var="imageAttr" value="${imageAttr} data-asset-license-primary=\"${componentProperties.pageImageLicenseInfo}\""/>
</c:if>

<c:if test="${not empty componentProperties.pageSecondaryImageId}">
    <c:set var="imageAttr" value="${imageAttr} data-asset-id-secondary=\"${componentProperties.pageSecondaryImageId}\""/>
</c:if>
<c:if test="${not empty componentProperties.pageSecondaryImageLicenseInfo}">
    <c:set var="imageAttr" value="${imageAttr} data-asset-licenses-secondary=\"${componentProperties.pageSecondaryImageLicenseInfo}\""/>
</c:if>

<c:if test="${not empty componentProperties.redirectTarget}">
    <c:set var="linkAttr" value="${linkAttr} external"/>
</c:if>
<c:if test="${not empty componentProperties.thumbnailWidth}">
    <c:set var="imageAttr" value="${imageAttr} width=\"${componentProperties.thumbnailWidth}\""/>
</c:if>
<c:if test="${not empty componentProperties.thumbnailHeight}">
    <c:set var="imageAttr" value="${imageAttr} height=\"${componentProperties.thumbnailHeight}\""/>
</c:if>

<c:if test="${fn:length(componentProperties.cardStyle) > 0}">
    <c:set var="classAttr" value="${classAttr} ${fn:join(componentProperties.cardStyle,' ')}"/>
</c:if>

<c:if test="${componentProperties.cardIconShow and fn:length(componentProperties.cardIcon) > 0}">
    <c:set var="classIconAttr" value="${classIconAttr} ${fn:join(componentProperties.cardIcon,' ')}"/>
</c:if>
<c:if test="${fn:length(componentProperties.cardStyle) > 0}">
    <c:set var="classStyleAttr" value="${classStyleAttr} ${fn:join(componentProperties.cardStyle,' ')}"/>
</c:if>

<c:if test="${componentProperties.titleIconShow and fn:length(componentProperties.titleIcon) > 0}">
    <c:set var="classAttr" value="${classAttr} ${fn:join(componentProperties.titleIcon,' ')}"/>
</c:if>


<c:if test="${componentProperties.animationEnabled and not empty componentProperties.animationName}">
    <c:set var="animationAttr" value="data-aos=\"${componentProperties.animationName}\""/>
</c:if>
<c:if test="${componentProperties.animationEnabled and not empty componentProperties.animationOnce}">
    <c:set var="animationAttr" value="${animationAttr} data-aos-once=\"${componentProperties.animationOnce}\""/>
</c:if>
<c:if test="${componentProperties.animationEnabled and not empty componentProperties.animationEasing}">
    <c:set var="animationAttr" value="${animationAttr} data-aos-easing=\"${componentProperties.animationEasing}\""/>
</c:if>
<c:if test="${componentProperties.animationEnabled and not empty componentProperties.animationDelay}">
    <c:set var="animationAttr" value="${animationAttr} data-aos-delay=\"${componentProperties.animationDelay}\""/>
</c:if>
<c:if test="${componentProperties.animationEnabled and not empty componentProperties.animationDuration}">
    <c:set var="animationAttr" value="${animationAttr} data-aos-duration=\"${componentProperties.animationDuration}\""/>
</c:if>

<c:if test="${fn:length(componentProperties.badgeLinkStyle) > 0}">
    <c:set var="linkClassAttr" value="${fn:join(componentProperties.badgeLinkStyle, ' ')}"/>
</c:if>
