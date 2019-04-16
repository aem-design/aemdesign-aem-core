<c:if test="${WCMMODE_EDIT == CURRENT_WCMMODE}">
<style>
    [component] .tooltip {
        display: block !important;
        position: unset;
        opacity: unset;
    }
</style>

<div>Tool Tip: ${componentProperties.componentId}</div>
<div>Title: ${componentProperties.title}</div>
<div>Description: ${componentProperties.description}</div>
</c:if>
<span ${componentProperties.componentAttributes}></span>
