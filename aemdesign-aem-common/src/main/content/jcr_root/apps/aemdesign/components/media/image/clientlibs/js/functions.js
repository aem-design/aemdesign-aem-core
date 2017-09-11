//image - functions
window.AEMDESIGN = window.AEMDESIGN || {"jQuery":{}};
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.image = AEMDESIGN.components.image || {};


(function ($, _, ko, ns, log, window, undefined) { //add additional dependencies

    // Enable strict mode
    "use strict";
    var _version = "0.1";

    ns.version = function () {
        return _version;
    };


    ns.picturefill = function ($currentPicture) {

        var matches = [];

        $("div[data-media]", $currentPicture).each(function () {
            var media = $(this).attr("data-media");
            if (!media || ( window.matchMedia && window.matchMedia(media).matches )) {
                matches.push(this);
            }
        });

        var $picImg = $("img", $currentPicture).first();

        if (matches.length) {
            if ($picImg.length === 0) {
                $picImg = $("<img />").attr("alt", $currentPicture.attr("data-alt")).appendTo($currentPicture);
            }
            $picImg.attr("src", matches.pop().getAttribute("data-src"));
        } else {
            $picImg.remove();
        }
    };


})(AEMDESIGN.jQuery, _, ko, AEMDESIGN.components.image, AEMDESIGN.log,  this); //pass in additional dependencies