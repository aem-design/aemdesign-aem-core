window.WKCD = window.WKCD || {};
window.WKCD.jQuery = window.jQuery || {};
window.WKCD.$ = window.jQuery || $;
window.WKCD.components = WKCD.components || {};
window.WKCD.components.image = WKCD.components.image || {};


;(function ($, _, ko, ns, log, window, undefined) { //add additional dependencies

    // Enable strict mode
    "use strict";
    var _version = "0.1";

    ns.version = function () {
        return _version;
    };


    ns.picturefill = function (context) {

        log.log("picturefill functions is called ");

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


})(WKCD.jQuery, _, ko, WKCD.components.image, WKCD.log,  this); //pass in additional dependencies