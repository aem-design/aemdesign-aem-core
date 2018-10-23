<div ${componentProperties.componentAttributes} data-toggle="${componentProperties.dataToggle}" data-target="#${componentProperties.componentId}-content" aria-controls="${componentProperties.componentId}-content">
    <div class="header" id="${componentProperties.componentId}-heading">
        <button class="btn btn-link title">
            ${componentProperties.title}
        </button>
    </div>

    <div id="${componentProperties.componentId}-content" class="${componentProperties.dataToggle}" aria-labelledby="${componentProperties.componentId}-heading" data-parent="#${componentProperties.dataParent}">
        <div class="body">
            <cq:include path="par" resourceType="aemdesign/components/layout/container"/>
        </div>
    </div>
</div>