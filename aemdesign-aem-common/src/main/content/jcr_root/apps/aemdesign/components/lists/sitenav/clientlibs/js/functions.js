//sitenav - functions
window.AEMDESIGN = window.AEMDESIGN || {};
window.AEMDESIGN.jQuery = window.jQuery || {};
window.AEMDESIGN.$ = window.jQuery || $;
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.sitenav = AEMDESIGN.components.sitenav || {};

;(function ($, _, ko, ns, log, window, undefined) { //add additional dependencies

    "use strict";
    var _version = "0.1";

    ns.version = function () {
        return _version;
    };

    ns.setSectionColorStylesheet = function(el) {

        if ($("#section-color-stylesheet").attr("href") == "")
            if ($("header #site_nav li.active").length > 0) {
                $($($("header #site_nav li.active")[0]).attr("class").split(" ")).each(function () {
                    if (this != "active")
                        $("#section-color-stylesheet").attr("href", "/etc/designs/aemdesign/css/main/section-colour-" + this + ".css")
                })
            }

    };

})(AEMDESIGN.jQuery,_,ko, AEMDESIGN.components.sitenav, AEMDESIGN.log, this); //pass in additional dependencies