//aemdesign.components.section.js
window.AEMDESIGN = window.AEMDESIGN || {};
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.section = AEMDESIGN.components.section || {};

;(function ($, _, ko, ns, components, window, undefined) {

    $(document).ready(function () {

        $("[data-modules*='scrollClass']").each(function () {

            $(this).scrollClass({
                delay: 30,
                threshold: 50
            });
        });

    });



})(jQuery, _, ko, AEMDESIGN.components.section, AEMDESIGN.components, this);
