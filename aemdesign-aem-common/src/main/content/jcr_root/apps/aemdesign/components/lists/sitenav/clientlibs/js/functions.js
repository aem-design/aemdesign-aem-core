//sitenav - functions
window.WKCD = window.WKCD || {};
window.WKCD.jQuery = window.jQuery || {};
window.WKCD.$ = window.jQuery || $;
window.WKCD.components = WKCD.components || {};
window.WKCD.components.sitenav = WKCD.components.sitenav || {};

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

})(WKCD.jQuery,_,ko, WKCD.components.sitenav, WKCD.log, this); //pass in additional dependencies