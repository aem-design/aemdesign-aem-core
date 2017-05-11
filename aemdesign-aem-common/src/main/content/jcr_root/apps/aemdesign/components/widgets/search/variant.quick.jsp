<div ${componentProperties.componentAttributes}>
    <form ${componentProperties.dataAttributes} name="search" action="${componentProperties.searchURL}"
                                                method="${componentProperties.formMethod}"
                                                data-modules="Search">
        <input type="search"
               name="${componentProperties.formParameterName}"
               placeholder="${componentProperties.placeholderText}">
        <button type="submit"><img src="/etc/designs/aemdesign/img/icon-search-header.gif"/></button>

        <script type="text/template" class="suggestion-tpl">
            <a href='<@- link @>'>
                <@ if (isModel) { @>
                <img src='<@- image @>' alt='<@- title @>'>
                          <span class='title'>
                            <strong><@- title @></strong>
                          </span>
                <@ } else {@>
                <@- title @>
                <@ } @>
            </a>

        </script>
    </form>
</div>