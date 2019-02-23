
function populateGuideBridgeWithQueryStringValues(guideBridge,query) {
    if (guideBridge) {
        var vars = query.split("&");
        for (var i=0;i<vars.length;i++) {
            var pair = vars[i].split("=");
            var name = pair[0];
            var value = pair[1];
            console.log(["updating",name]);
            var formElement = guideBridge.resolveNode(unescape(name));
            if (formElement) {
                console.log(["updated",name,value]);
                formElement.value = unescape(value);
            }

        }

    }
}


$(document).ready(function() {
    //console.log(["loaded","doc"]);

    $('iframe').each(function() {
        $(this).on("load",function() {
            //console.log(["frame",this.id,this.contentWindow,this.contentWindow.guideBridge,this.contentWindow.guideBridge.isGuideLoaded()]);
            if (this.contentWindow["guideBridge"]) {
                if (this.contentWindow.guideBridge.isGuideLoaded()) {
                    populateGuideBridgeWithQueryStringValues(this.contentWindow.guideBridge, window.location.search.substring(1));
                }
            }
        });
    });

    if (window["guideBridge"]) {
        if (guideBridge.isGuideLoaded()) {
            //console.log(["form",guideBridge.isGuideLoaded()]);
            populateGuideBridgeWithQueryStringValues(guideBridge, window.location.search.substring(1));
        }
    }

});



