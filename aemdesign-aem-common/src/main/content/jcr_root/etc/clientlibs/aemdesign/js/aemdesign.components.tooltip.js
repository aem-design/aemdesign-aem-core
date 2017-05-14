//aemdesign.components.tooltip.js
window.AEMDESIGN = window.AEMDESIGN || {"jQuery":{}};
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.tooltip = AEMDESIGN.components.tooltip || {};

(function ($, _, ko, ns, components, window, undefined) {


    $(document).ready(function() {

        $("[data-modules*='tooltip']").each(function () {

            $(this).mouseover(function () {
                var tipTitle = $(this).data('title');
                var tipContent = $(this).data('content');
                var tipWidth = $(this).data('width');

                if (tipWidth == "") {
                    $(this).append('<div class="tooltip-inner"><div class="popup""><h6>' + tipTitle + '</h6><p>' + tipContent + '</p></div></div>');
                } else {
                    $(this).append('<div class="tooltip-inner"><div class="popup" style="width:' + tipWidth + ';"><h6>' + tipTitle + '</h6><p>' + tipContent + '</p></div></div>');
                }

            }).mouseout(function () {
                $('.tooltip-inner').remove();
                $(this).removeClass('active');
            });

        });

    });

})(AEMDESIGN.jQuery, _, ko, AEMDESIGN.components.tooltip, AEMDESIGN.components, this);
