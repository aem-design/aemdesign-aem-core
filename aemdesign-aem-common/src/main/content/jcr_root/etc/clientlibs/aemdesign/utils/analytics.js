//aemdesign.analytics.js
window.AEMDESIGN = window.AEMDESIGN || {"jQuery":{}};
window.AEMDESIGN.analytics = AEMDESIGN.analytics || {};

(function ($, log, ns, window, undefined) { //NOSONAR module conventions


    "use strict";
    var _version = "0.1";

    ns.version = function () {
        return _version;
    };

    /*

digitalData.event[n].eventInfo = {
    eventName: "Add News Portal",
    eventAction: "addportal",
    eventPoints: 200,
    type: "contentModifier",
    timeStamp: new Date(),
    effect: "include portal 1234"
};
     */
    // ns.getEventData = function($element) {
    //     return {
    //         "componentid"               : $element.attr("componentid"), //component id
    //         "href"                      : $element.attr("href"), //event
    //         "target"                    : $element.attr("target") || "", //event
    //         "eventName"             : $element.attr("data-analytics-type") || "", //event
    //         "analyticsHitType"          : $element.attr("data-analytics-hit-type") || "", //hit / type
    //         "analyticsEventCategory"    : $element.attr("data-analytics-event-category") || "", //eg Link Click
    //         "eventAction"      : $element.attr("data-analytics-event-action") || "", //eg Content, Navigation, Email, Phone
    //         "eventName"       : $element.attr("data-analytics-event-label") || "", //title of link
    //         "analyticsTransport"        : $element.attr("data-analytics-transport") || "", //beacon
    //         "analyticsNonInteraction"   : $element.attr("data-analytics-noninteraction") || "" //hidden transactions
    //     };
    // };



    ns.getAnalyticsData = function($element) {
        return {
            "componentid"               : $element.attr("componentid"), //component id
            "href"                      : $element.attr("href"), //event
            "target"                    : $element.attr("target") || "", //event
            "analyticsType"             : $element.attr("data-analytics-type") || "", //event
            "analyticsHitType"          : $element.attr("data-analytics-hit-type") || "", //hit / type
            "analyticsEventCategory"    : $element.attr("data-analytics-event-category") || "", //eg Link Click
            "analyticsEventAction"      : $element.attr("data-analytics-event-action") || "", //eg Content, Navigation, Email, Phone
            "analyticsEventLabel"       : $element.attr("data-analytics-event-label") || "", //title of link
            "analyticsTransport"        : $element.attr("data-analytics-transport") || "", //beacon
            "analyticsNonInteraction"   : $element.attr("data-analytics-noninteraction") || "" //hidden transactions
        };
    };

    ns.getAnalyticsInfoAA = function($object) {

        var data = ns.getAnalyticsData($object);

        var pageTitle = document.title;
        var linkLabel = data.analyticsEventLabel || $object.text().trim(); //link title
        var eventLabel = linkLabel || data.analyticsEventLabel; //title of link

        var linkEvars = {
            event: data.analyticsHitType,
            category: [pageTitle,data.analyticsEventCategory,eventLabel].join(":"),
            label: eventLabel
        };
        return linkEvars;
    };

    ns.getAnalyticsInfoGA = function($object) {

        var data = ns.getAnalyticsData($object);

        var linkEvars = {
            hitType: data.analyticsHitType,
            eventCategory: data.analyticsEventCategory,
            eventAction: data.analyticsEventAction,
            eventLabel: data.analyticsEventLabel
        };

        if (data.analyticsNonInteraction) {
            linkEvars["nonInteraction"] = data.analyticsNonInteraction;
        }

        if (data.analyticsTransport) {
            linkEvars["transport"] = data.analyticsTransport;
        }
        return linkEvars;
    };

    ns.getAnalyticsOnClickAA = function(linkEvars) {
        return '_dtmLayer.push('+JSON.stringify(linkEvars)+');';
    };

    ns.getAnalyticsOnClickGA = function(linkEvars) {
        //'ga("send", '+JSON.stringify(linkEvars)+')'
        return 'dataLayer.push('+JSON.stringify(linkEvars)+');';
    };

    ns.enableAA = function($link,reset) {

        var linkEvars = ns.getAnalyticsInfoAA($link);

        var onClick = new Function(ns.getAnalyticsOnClickAA(linkEvars)); //NOSONAR params are abstract by design

        if (reset) {
            $link.attr('onclick', '');
        }

        $link.click(onClick);

    };

    ns.enableGA = function($link,reset) {

        var linkEvars = ns.getAnalyticsInfoGA($link);

        var onClick = new Function(ns.getAnalyticsOnClickAA(linkEvars)); //NOSONAR params are abstract by design

        if (reset) {
            $link.attr('onclick', '');
        }

        $link.click(onClick);


    };

    ns.enableButton = function($button,reset) {

        var data = ns.getAnalyticsData($button);

        if (reset) {
            $button.attr('onclick', '');
        }

        if (data.analyticsType==="aa") {
            ns.enableAA($button);
        } else if (data.analyticsType==="ga") {
            ns.enableGA($button);
        }

        var onClick = new Function('window.open("'+ window.encodeURI(data.href)+'","'+data.target+'")');  //NOSONAR params are abstract by design

        $button.click(onClick);


    };

    ns.imageLoad = function($image) {

        var data = ns.getAnalyticsData($image);

        if (data.analyticsType==="aa") {
            if (assetAnalytics) {
                assetAnalytics.core.assetLoaded($image);
            }
        } else if (data.analyticsType==="ga") {
            //ready for update
        }

    };

    ns.getBreakpoint = function() {
        var breakpoints = [
            {label: "XS", width: 320, height: 480},
            {label: "SM", width: 640, height: 480},
            {label: "MD", width: 1024, height: 768},
            {label: "LG", width: 1366, height: 1024},
            {label: "XLG", width: 1920, height: 1280},
            {label: "XXLG", width: 2560, height: 1440}
        ];

        // var results = breakpoints.map(function(breakpoint) {
        //     var query = window.matchMedia('(min-width: ' + breakpoint.width + 'px) and (min-height: ' + breakpoint.height + 'px)');
        //     return query && query.matches && breakpoint || null
        // }).filter(function(x) {
        //     return x;
        // });
        // console.log(results);
        // if (results.length === 0) {
        //     return breakpoints[0];
        // }
        //
        // return results[0];

        var w = window.innerWidth || window.document.documentElement.clientWidth || window.document.body.clientWidth;

        var match = breakpoints.filter(function(breakpoint) { breakpoint.width < w });

        if (match.length === 0) {
            return breakpoints[0];
        } else {
            return match[match.length-1];
        }
    };

    ns.getDestinationUrl = function() {
        return window.location.href.split("?")[0];
    };
    ns.getReferringUrl = function() {
        return window.document.referrer.split("?")[0] || "";
    };

    ns.getSections = function() {
        return $.map($("header[component],header[component] > div > [component],aside[component],aside[component] > div > [component],article[component],article[component] > div > [component],footer[component],footer[component] > div > [component]"), function(a){return a.id}).join(",");
    };

})(AEMDESIGN.jQuery, AEMDESIGN.log, AEMDESIGN.analytics, this); //pass in additional dependencies

