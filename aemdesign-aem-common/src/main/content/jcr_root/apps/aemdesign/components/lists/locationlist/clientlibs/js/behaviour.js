//locationlist - behaviour
window.WKCD = window.WKCD || {};
window.WKCD.jQuery = window.jQuery || {};
window.WKCD.$ = window.jQuery || $;
window.WKCD.components = WKCD.components || {};
window.WKCD.components.locationlist = WKCD.components.locationlist || {};

;(function ($, _, ko, ns, log, window, undefined) { //add additional dependencies

    $(document).ready(function () {


        $("[data-modules='map']").each(function () {

            log.log("Start to process Google Map");

            //var mapKey = $("[data-modules='map']").data("mapapikey");

            ns.loadGoogleMap(this);

            log.log("Finished to process Google Map");


        });


    });

})(WKCD.jQuery,_,ko, WKCD.components.locationlist, WKCD.log, this); //pass in additional dependencies