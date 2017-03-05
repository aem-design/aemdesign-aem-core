//googleanalytics - function.js
window.WKCD = window.WKCD || {};
window.WKCD.jQuery = window.jQuery || {};
window.WKCD.$ = window.jQuery || $;
window.WKCD.components = WKCD.components || {};
window.WKCD.components.googleanalytics = WKCD.components.googleanalytics || {};



;(function ($, _, ko, ns, log, window, undefined) { //add additional dependencies


    log.log("Google Analytics Start !!!");

    "use strict";
    var _version = "0.1";

    ns.version = function () {
        return _version;
    };

    var _googleanalyticsLoaded = false;


    ns.googleanalyticsLoaded = function () {
        return _googleanalyticsLoaded;
    };

    ns.version = function () {
        return _version;
    };

    ns.loadGoogleAnalytics = function (){
        if(ns.googleanalyticsLoaded() == false) {

            window.GoogleAnalyticsObject = 'ga';
            window.ga = window.ga || function(){
                (window.ga.q = window.ga.q || []).push(arguments)
            },
            window.ga.l =1 * new Date();
            var a = document.createElement('script');
            var m = document.getElementsByTagName('script')[0];
            a.async = 1;
            a.src = '//www.google-analytics.com/analytics.js';
            m.parentNode.insertBefore(a, m)

            _googleanalyticsLoaded = true;

            log.log("Loaded Google Analytics analytics.js ");
        }
    };

    ns.loadGoogleAnalytcsTracker = function(el){

        var base = $(el);

        var trackingId = base.data("trackingid");
        var cookiedomain = base.data("cookiedomain");
        var trackingname = base.data("trackingname");

        log.log("Binding Loaded analytics.js "+  trackingId);

        ns.loadGoogleAnalytics();

        ga('create', trackingId, cookiedomain);

    };

    ns.sendPageView = function(el){
         ga('send', 'pageview' ,{
            'hitCallback': function() {
                var milliseconds = new Date().getTime();
                $(el).attr("pageview-callback-timestamp", milliseconds);
            }
          }
        );
    };

    // log.disableLog();

})(WKCD.jQuery,_,ko, WKCD.components.googleanalytics, WKCD.log, this); //pass in additional dependencies


