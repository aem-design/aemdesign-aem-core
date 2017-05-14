//eventlist - behaviour
window.AEMDESIGN = window.AEMDESIGN || {};
window.AEMDESIGN.jQuery = window.jQuery || {};
window.AEMDESIGN.$ = window.jQuery || $;
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.eventfilter = AEMDESIGN.components.eventfilter || {};

;(function ($, _, ko, ns, log, window, undefined) { //add additional dependencies

    $(document).ready(function () {

        $("[data-modules='eventlist']").each(function () {

            //log.info(["this", this, $(this), $(this)]);
            //ns.loadIsotopeGrid(this);

        });
    });

})(AEMDESIGN.jQuery,_,ko, AEMDESIGN.components.eventfilter, AEMDESIGN.log, this); //pass in additional dependencies