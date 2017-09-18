//aemdesign.analytics.js
window.AEMDESIGN = window.AEMDESIGN || {"jQuery":{}};
window.AEMDESIGN.analytics = AEMDESIGN.analytics || {};

(function ($, _, ko, ns, log, window, undefined) { //add additional dependencies


    "use strict";
    var _version = "0.1";

    ns.version = function () {
        return _version;
    };

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
        }
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

        var onClick = new Function(ns.getAnalyticsOnClickAA(linkEvars));

        if (reset) {
            $link.attr('onclick', '');
        }

        $link.click(onClick);

    };

    ns.enableGA = function($link,reset) {

        var linkEvars = ns.getAnalyticsInfoGA($link);

        var onClick = new Function(ns.getAnalyticsOnClickAA(linkEvars));

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

        var onClick = new Function('window.open("'+ window.encodeURI(data.href)+'","'+data.target+'")');

        $button.click(onClick);


    };

    ns.imageLoad = function($image) {

        var data = ns.getAnalyticsData($image);

        if (data.analyticsType==="aa") {
            if (assetAnalytics) {
                assetAnalytics.core.assetLoaded($image)
            }
        } else if (data.analyticsType==="ga") {

        }

    };


})(AEMDESIGN.jQuery, _, ko, AEMDESIGN.analytics, AEMDESIGN.log, this); //pass in additional dependencies

