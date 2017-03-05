//eventlist - behaviour
window.WKCD = window.WKCD || {};
window.WKCD.jQuery = window.jQuery || {};
window.WKCD.$ = window.jQuery || $;
window.WKCD.components = WKCD.components || {};
window.WKCD.components.eventfilter = WKCD.components.eventfilter || {};

;(function ($, _, ko, ns, window, undefined) { //add additional dependencies

    $(document).ready(function () {

        $("[data-modules='eventList']").each(function () {

            //console.log(["this", this, $(this), $(this)]);
            ns.loadIsotopeGrid(this);

        });
    });

})(WKCD.jQuery,_,ko, WKCD.components.eventfilter, this); //pass in additional dependencies