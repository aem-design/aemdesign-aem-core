//googleanalytics - behaviour
window.AEMDESIGN = window.AEMDESIGN || {"jQuery":{}};
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.googleanalytics = AEMDESIGN.components.googleanalytics || {};

(function ($, _, ko, ns, window, undefined) { //add additional dependencies

    $(document).ready(function () {


        $("[data-modules='googleanalytics']").each(function () {
            //Per Page
            // ns.loadGoogleAnalytcsTracker(this);
            // ns.sendPageView(this);
        });


    });

})(AEMDESIGN.jQuery,_,ko, AEMDESIGN.components.googleanalytics, this); //pass in additional dependencies