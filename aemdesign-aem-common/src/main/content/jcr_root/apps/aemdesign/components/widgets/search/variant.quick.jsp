<div ${componentProperties.componentAttributes}>
    <form name="search"
          action="${componentProperties.formAction}"
          method="${componentProperties.formMethod}">
        <input type="search"
               name="${componentProperties.formParameterName}"
               placeholder="${componentProperties.placeholderText}">
        <button type="submit">Search</button>

        <script type="text/template" class="suggestion-tpl">
            <a href='<@- link @>'>
              <span class='title'>
                <strong><@- title @></strong>
              </span>
            </a>

        </script>
    </form>
</div>