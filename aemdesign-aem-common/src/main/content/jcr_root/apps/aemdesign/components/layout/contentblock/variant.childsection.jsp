<c:if test="${not empty componentProperties.analyticsLinkDescription}">
    <c:set var="dataLayerAttributes" value="${dataLayerAttributes} data-layer-linkdescription=\"${componentProperties.analyticsLinkDescription}\""/>
</c:if>
<c:if test="${not empty componentProperties.analyticsLinkLocation}">
    <c:set var="dataLayerAttributes" value="${dataLayerAttributes} data-layer-linklocation=\"${componentProperties.analyticsLinkLocation}\""/>
</c:if>
<c:if test="${not empty componentProperties.analyticsLinkType}">
    <c:set var="dataLayerAttributes" value="${dataLayerAttributes} data-layer-linktype=\"${componentProperties.analyticsLinkType}\""/>
</c:if>
<c:if test="${not empty componentProperties.analyticsEventType}">
    <c:set var="dataLayerAttributes" value="${dataLayerAttributes} data-layer-event=\"${componentProperties.analyticsEventType}\""/>
</c:if>
<div ${componentProperties.componentAttributes} data-toggle="${componentProperties.dataToggle}" data-target="#${componentProperties.componentId}-content" aria-controls="${componentProperties.componentId}-content">
    <div class="header" id="${componentProperties.componentId}-heading">
        <button class="btn btn-link title"${dataLayerAttributes}>
            ${componentProperties.title}
        </button>
    </div>

    <div id="${componentProperties.componentId}-content" class="${componentProperties.dataToggle}" aria-labelledby="${componentProperties.componentId}-heading" data-parent="#${componentProperties.dataParent}">
        <div class="body">
            <cq:include path="par" resourceType="aemdesign/components/layout/container"/>
        </div>
    </div>
</div>