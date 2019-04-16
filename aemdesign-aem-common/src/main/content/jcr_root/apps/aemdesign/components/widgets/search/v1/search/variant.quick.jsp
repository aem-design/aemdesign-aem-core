<div ${componentProperties.componentAttributes}>
    <form name="search" itemscope itemtype="https://schema.org/WebSite"
          action="${componentProperties.formAction}"
          method="${componentProperties.formMethod}">
        <meta itemprop="target" content="${componentProperties.formAction}?q={${componentProperties.formParameterName}}"/>
        <input type="search" itemprop="query-input"
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