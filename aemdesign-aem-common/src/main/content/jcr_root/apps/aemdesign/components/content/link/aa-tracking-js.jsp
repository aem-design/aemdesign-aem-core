<script type="text/javascript">
    (function() {
        var linkDiv = document.getElementById("${componentProperties.componentId}");
        var linkLabel = '"${componentProperties.label}"';
        var linkEvars = '{ linkLabel: '+linkLabel+' , linkTarget: "${componentProperties.linkUrl}" }';
        try {
            var tagNodes = linkDiv.getElementsByTagName('A');
            for (var i = 0; i < tagNodes.length; i++) {
                var link = tagNodes.item(i);
                link.setAttribute('onclick', 'CQ_Analytics.record({event: "linkClick", values: ' + linkEvars + ', collect:  false, options: { obj: this }, componentPath: "<%=resource.getResourceType()%>"})');
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
