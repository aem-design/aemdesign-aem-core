<div ${componentProperties.componentAttributes}>
    <fieldset>

        <legend class="${componentProperties.styleTitleClass}" ${componentProperties.hideTitle ? "hidden":"" }>${componentProperties.title}</legend>
        <cq:include path="par" resourceType="foundation/components/parsys"/>
    </fieldset>
</div>