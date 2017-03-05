//search - behaviour

;(function($, _, ko, search, window, undefined) {

    $(document).ready(function() {
        $("[data-modules='search']").each(function() {

            var base = $(this);
            WKCD.log.log("loading search box");
            search.init(base);

        });
    });


})(WKCD.jQuery, _, ko,  WKCD.components.search, this);