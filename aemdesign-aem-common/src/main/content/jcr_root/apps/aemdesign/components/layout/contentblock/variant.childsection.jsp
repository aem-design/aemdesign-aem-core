<div ${componentProperties.componentAttributes}>
    <div class="header" id="${componentProperties.componentId}-heading">
        <button class="btn btn-link title" data-toggle="${componentProperties.dataToggle}" data-target="#${componentProperties.componentId}-content" aria-controls="${componentProperties.componentId}-content">
            ${componentProperties.title}
        </button>
    </div>

    <div id="${componentProperties.componentId}-content" class="${componentProperties.dataToggle}" aria-labelledby="${componentProperties.componentId}-heading" data-parent="#${componentProperties.dataParent}">
        <div class="body">
            <cq:include path="par" resourceType="aemdesign/components/layout/container"/>
        </div>
    </div>
</div>