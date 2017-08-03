//link - behaviour
window.AEMDESIGN = window.AEMDESIGN || {"jQuery":{}};
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.link = AEMDESIGN.components.link || {};


(function ($, _, ko, ns, window, undefined) { //add additional dependencies


    $(document).ready(function () {
        $("a[data-analytics-type=aa]").each(function() {
            var link = $(this);
            ns.enableAA(link); //this has been depreciated
        });
        $("a[data-analytics-type=ga]").each(function() {
            var link = $(this);
            ns.enableGA(link);
        });
        $("button.link[href]").each(function() {
            var button = $(this);
            ns.enableButton(button);
        });
    })


})(AEMDESIGN.jQuery, _, ko, AEMDESIGN.components.link, this); //pass in additional dependencies
