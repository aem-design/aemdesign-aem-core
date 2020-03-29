//locationlist - behaviour
window.AEMDESIGN = window.AEMDESIGN || {"jQuery":{}};
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.locationlist = AEMDESIGN.components.locationlist || {};

(function ($, _, ko, ns, log, window, undefined) { //add additional dependencies

    $(document).ready(function () {


        $(".locationlist[data-modules='map']").each(function () {

            log.info("Start to process Google Map");

            //var mapKey = $("[data-modules='map']").data("mapapikey");

            ns.loadGoogleMaps(this);

            log.info("Finished to process Google Map");


        });


    });

})(AEMDESIGN.jQuery,_,ko, AEMDESIGN.components.locationlist, AEMDESIGN.log, this); //pass in additional dependencies
