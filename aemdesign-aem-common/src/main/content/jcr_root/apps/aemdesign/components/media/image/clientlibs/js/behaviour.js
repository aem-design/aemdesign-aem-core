//image - behaviour
window.AEMDESIGN = window.AEMDESIGN || {"jQuery":{}};
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.image = AEMDESIGN.components.image || {};


(function ($, _, ko, ns, window, undefined) { //add additional dependencies

    $(window).on("load resize", function () {
        $("div[data-picture]").each(function () {
            var picture = $(this);
            ns.picturefill(picture);
        });

    });

})(AEMDESIGN.jQuery, _, ko, AEMDESIGN.components.image, this); //pass in additional dependencies
