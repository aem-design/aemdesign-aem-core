/**
 * Looks for table implementations that need to be using alternating row colours, will then
 * add odd and even classes to the rows that need it.
 */
(function($, undefined) {

    $(document).ready(function() {
        $("#article .alternating-table").each(function() {
            var table = $(this);

            table.find("tr:odd").toggleClass("odd", true);
            table.find("tr:even").toggleClass("even", true);
        });

    });

})(AEMDESIGN.jQuery);