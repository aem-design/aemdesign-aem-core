<%@ taglib prefix="xss" uri="http://www.adobe.com/consulting/acs-aem-commons/xss" %>
<script type="text/javascript">
    (function() {

        var linkDiv = document.getElementById("${xss:encodeForJSString(xssAPI, componentProperties.divId)}");
        var linkEvars = '{ ' +
                '"hitType": "${xss:encodeForJSString(xssAPI, componentProperties.hitType)}", ' +
                '"eventCategory": "${xss:encodeForJSString(xssAPI, componentProperties.eventCategory)}", ' +
                '"eventAction": "${xss:encodeForJSString(xssAPI, componentProperties.eventAction)}", ' +
                '"eventLabel": "${xss:encodeForJSString(xssAPI, componentProperties.eventLabel)}"' +
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
