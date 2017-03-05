/**
 * This focuses
 */
(function($, undefined) {

    $(document).ready(function() {

        // on ready, focus the search keywords field
        $("#article .search input[type='text']").focus();

        // autosubmit form when 'max items' field changes
        $("#maxItems").change(function() {
            $(this).closest("form").submit();
        });

    });

})(AEMDESIGN.jQuery);