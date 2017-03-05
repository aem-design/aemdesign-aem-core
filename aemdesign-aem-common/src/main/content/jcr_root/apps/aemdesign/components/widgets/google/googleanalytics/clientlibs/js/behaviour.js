//googleanalytics - behaviour.js
window.WKCD = window.WKCD || {};
window.WKCD.jQuery = window.jQuery || {};
window.WKCD.$ = window.jQuery || $;
window.WKCD.components = WKCD.components || {};
window.WKCD.components.googleanalytics = WKCD.components.googleanalytics || {};

;(function ($, _, ko, ns, window, undefined) { //add additional dependencies

    $(document).ready(function () {


        $("[data-modules='googleanalytics']").each(function () {
            //Per Page
            ns.loadGoogleAnalytcsTracker(this);
            ns.sendPageView(this);
        });


    });

})(WKCD.jQuery,_,ko, WKCD.components.googleanalytics, this); //pass in additional dependencies