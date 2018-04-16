//search - behaviour
window.AEMDESIGN = window.AEMDESIGN || {"jQuery":{}};
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.search = AEMDESIGN.components.search || {};

(function ($, _, ko, utils, log, search, window, undefined) { //add additional dependencies

    //log.enableLog();

    $(document).ready(function () {

        $("[data-modules*='search']").each(function() {

            var base = $(this);

            search.init(base);


        });


    });


})(AEMDESIGN.jQuery, _, ko, AEMDESIGN.utils, AEMDESIGN.log, AEMDESIGN.components.search, this); //pass in additional dependencies
