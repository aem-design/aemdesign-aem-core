<div ${componentProperties.componentAttributes}>
    <form action="${componentProperties.formAction}" method="${componentProperties.formMethod}" novalidate="" role="form search">
        <fieldset>
            <legend hidden="">${componentProperties.legendText}</legend> <label for="nav_search" hidden="">${componentProperties.labelText}</label>
            <input autocomplete="off" id="nav_search" name="search" placeholder="${componentProperties.placeholderText}" type="search"> <input type="submit" value="${componentProperties.searchButtonText}">
        </fieldset>
    </form>
</div>