//image - functions
window.AEMDESIGN = window.AEMDESIGN || {};
window.AEMDESIGN.jQuery = window.jQuery || {};
window.AEMDESIGN.$ = window.jQuery || $;
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.image = AEMDESIGN.components.image || {};


;(function ($, _, ko, ns, log, window, undefined) { //add additional dependencies

    // Enable strict mode
    "use strict";
    var _version = "0.1";

    ns.version = function () {
        return _version;
    };


    ns.picturefill = function (context) {

        log.info("picturefill functions is called ");

        var undefined;
        if (context === undefined) {
            context = $("body");
        }


        $("div[data-picture]", context).each(function () {
            var currentPicture = this;
            var matches = [];

            $("div[data-media]", currentPicture).each(function () {
                var media = $(this).attr("data-media");
                if (!media || ( window.matchMedia && window.matchMedia(media).matches )) {
                    matches.push(this);
                }
            });

            var $picImg = $("img", currentPicture).first();

            if (matches.length) {
                if ($picImg.size() === 0) {
                    var $currentPicture = $(currentPicture);
                    $picImg = $("<img />").attr("alt", $currentPicture.attr("data-alt")).appendTo($currentPicture);
                }
                $picImg.attr("src", matches.pop().getAttribute("data-src"));
            } else {
                $picImg.remove();
            }
        });
    };


})(AEMDESIGN.jQuery, _, ko, AEMDESIGN.components.image, AEMDESIGN.log,  this); //pass in additional dependencies