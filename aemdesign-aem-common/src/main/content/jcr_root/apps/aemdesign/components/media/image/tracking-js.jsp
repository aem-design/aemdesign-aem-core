<%@page session="false"%>
<script type="text/javascript">
    (function() {
        try {

            var $imageDiv = $("<%= xssAPI.encodeForJSString(divId) %>");
            var imageEvars = '{ imageLink: "<%= xssAPI.getValidHref(image.get(Image.PN_LINK_URL)) %>", imageAsset: "<%= image.getFileReference() %>", imageTitle: "<%= xssAPI.encodeForJSString(image.getTitle()) %>" }';
            $imageDiv.find("a").each(function(index) {
                this.setAttribute('onclick', 'CQ_Analytics.record({event: "imageClick", values: ' + imageEvars + ', collect: false, options: { obj: this }, componentPath: "<%=resource.getResourceType()%>"})');
            });


 /*           var imageDiv = document.getElementById("<%= xssAPI.encodeForJSString(divId) %>");
            var imageEvars = '{ imageLink: "<%= xssAPI.getValidHref(image.get(Image.PN_LINK_URL)) %>", imageAsset: "<%= image.getFileReference() %>", imageTitle: "<%= xssAPI.encodeForJSString(image.getTitle()) %>" }';
            var tagNodes = imageDiv.getElementsByTagName('A');
            for (var i = 0; i < tagNodes.length; i++) {
                var link = tagNodes.item(i);
                link.setAttribute('onclick', 'CQ_Analytics.record({event: "imageClick", values: ' + imageEvars + ', collect: false, options: { obj: this }, componentPath: "<%=resource.getResourceType()%>"})');
            }*/
            <%--
        // Leave this out, because only the last image processed is taken into account.
        tagNodes = imageDiv.getElementsByTagName('IMG');
        for (var i = 0; i < tagNodes.length; i++) {
            var img = tagNodes.item(i);
            img.setAttribute("data-tracking", "{event:'imageView', values:" + imageEvars + ", componentPath:'<%=resource.getResourceType()%>'}");
        }
        --%>
        }catch(ex){
            console.log(ex);
        }
    })();
</script>
