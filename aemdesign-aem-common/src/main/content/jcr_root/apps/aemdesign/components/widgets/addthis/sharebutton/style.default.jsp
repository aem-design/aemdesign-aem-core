<div ${componentProperties.componentAttributes} data-modules="sharebutton" data-pubid="${componentProperties.pubId}" role="toolbar">
    <ul>
        <li class="share">
            <a href="#" title="<%= xssAPI.encodeForHTMLAttr(_i18n.get("shareThisPage",  "addthis.sharebutton")) %>"><%= xssAPI.encodeForHTML(_i18n.get("share",  "addthis.sharebutton")) %></a>
            <div class="atclear"></div>
            <div class="addthis_toolbox addthis_default_style addthis_32x32_style">
                <a class="addthis_button_facebook" title="<%= xssAPI.encodeForHTMLAttr(_i18n.get("shareOnFacebook",  "addthis.sharebutton")) %>"></a>
                <a class="addthis_button_twitter" title="<%= xssAPI.encodeForHTMLAttr(_i18n.get("shareOnTwitter",  "addthis.sharebutton")) %>"></a>
                <a class="addthis_button_google_plusone_share" title="<%= xssAPI.encodeForHTMLAttr(_i18n.get("shareOnGoogle +",  "addthis.sharebutton")) %>"></a>
                <a class="addthis_button_sinaweibo"  title="<%= xssAPI.encodeForHTMLAttr(_i18n.get("shareOnWeibo",  "addthis.sharebutton")) %>"></a>
            </div>
        </li>
    </ul>
</div>
