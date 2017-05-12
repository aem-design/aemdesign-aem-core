<script type="text/javascript">
    (function() {

        var linkDiv = document.getElementById("${xssAPI.encodeForJSString(componentProperties.divId)}");
        var linkEvars = '{ ' +
                '"hitType": "${xssAPI.encodeForJSString(componentProperties.hitType)}", ' +
                '"eventCategory": "${xssAPI.encodeForJSString(componentProperties.eventCategory)}", ' +
                '"eventAction": "${xssAPI.encodeForJSString(componentProperties.eventAction)}", ' +
                '"eventLabel": "${xssAPI.encodeForJSString(componentProperties.eventLabel)}"' +
        '}';

        try {
            var tagNodes = linkDiv.getElementsByTagName('A');
            for (var i = 0; i < tagNodes.length; i++) {
                var link = tagNodes.item(i);
                link.setAttribute('onclick', 'ga("send", '+linkEvars+')');
            }
        } catch (ex) {
            //error
        }
        <%--
        // Leave this out, because only the last image processed is taken into account.
        tagNodes = imageDiv.getElementsByTagName('IMG');
        for (var i = 0; i < tagNodes.length; i++) {
            var img = tagNodes.item(i);
            img.setAttribute('record', "'imageDisplay', " + imageEvars);
        }
        --%> 
    })();
</script>
