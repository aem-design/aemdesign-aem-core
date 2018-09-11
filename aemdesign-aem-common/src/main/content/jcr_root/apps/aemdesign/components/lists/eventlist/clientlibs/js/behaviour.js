//eventlist - behaviour
window.AEMDESIGN = window.AEMDESIGN || {"jQuery":{}};
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.eventfilter = AEMDESIGN.components.eventfilter || {};

(function ($, _, ko, log, ns, window, undefined) { //add additional dependencies

    $(document).ready(function () {

        $("[data-modules='eventList']").each(function () {

            //log.info(["this", this, $(this), $(this)]);
            //ns.loadIsotopeGrid(this);

        });
    });

})(AEMDESIGN.jQuery,_,ko, AEMDESIGN.log, AEMDESIGN.components.eventfilter, this); //pass in additional dependencies