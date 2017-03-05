//search - behaviour

;(function($, _, ko, search, log, window, undefined) {

    $(document).ready(function() {
        $("[data-modules='search']").each(function() {

            var base = $(this);
            log.info("loading search box");
            search.init(base);

        });
    });


})(AEMDESIGN.jQuery, _, ko,  AEMDESIGN.components.search, AEMDESIGN.log, this);