//searchlist - functions
window.AEMDESIGN = window.AEMDESIGN || {};
window.AEMDESIGN.jQuery = window.jQuery || {};
window.AEMDESIGN.$ = window.jQuery || $;
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.sitenav = AEMDESIGN.components.searchlist || {};

;(function ($, _, ko, ns, window, undefined) { //add additional dependencies

    $(document).ready(function() {

        // // on ready, focus the search keywords field
        // $("#article .search input[type='text']").focus();
        //
        // // autosubmit form when 'max items' field changes
        // $("#maxItems").change(function() {
        //     $(this).closest("form").submit();
        // });

    });

})(AEMDESIGN.jQuery,_,ko, AEMDESIGN.components.sitenav, this); //pass in additional dependencies
