//image
window.WKCD = window.WKCD || {};
window.WKCD.components = WKCD.components || {};
window.WKCD.components.image = WKCD.components.image || {};


;(function ($, _, ko, ns, window, undefined) { //add additional dependencies


    $(document).ready(function () {
        // Run on debounced resize and domready
        ns.picturefill();

        $(window).on("debouncedresize", function () {
            ns.picturefill();
        });
    })


})(WKCD.jQuery, _, ko, WKCD.components.image, this); //pass in additional dependencies
