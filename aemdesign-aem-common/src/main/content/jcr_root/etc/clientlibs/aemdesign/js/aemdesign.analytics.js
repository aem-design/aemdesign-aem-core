//aemdesign.analytics.js
window.AEMDESIGN = window.AEMDESIGN || {"jQuery":{}};
window.AEMDESIGN.analytics = AEMDESIGN.analytics || {};

(function ($, _, ko, ns, log, window, undefined) { //add additional dependencies


    "use strict";
    var _version = "0.1";

    ns.version = function () {
        return _version;
    };

    ns.getAnalyticsInfoAA = function($object) {

        var pageTitle = document.title;
        var linkLabel = $object.attr("data-label") || $object.text().trim(); //link title
        var hitType = $object.attr("data-analytics-hit-type"); //eg link_o ...
        var eventLabel = linkLabel || $object.attr("data-analytics-event-label"); //title of link
        var eventCategory = $object.attr("data-analytics-event-category"); //eg ${Page Title}:${Link Click}:${Link Title}

        var linkEvars = {
            event: hitType,
            category: [pageTitle,eventCategory,eventLabel].join(":"),
            label: eventLabel
        };
        return linkEvars;
    };

    ns.getAnalyticsInfoGA = function($object) {
        var hitType = $object.attr("data-analytics-hit-type"); //event
        var eventCategory = $object.attr("data-analytics-event-category"); //eg Link Click
        var eventAction = $object.attr("data-analytics-event-action"); //eg Content, Navigation, Email, Phone
        var eventLabel = $object.attr("data-analytics-event-label"); //title of link
        var eventTransport = $object.attr("data-analytics-transport"); //beacon
        var eventNonInteraction = $object.attr("data-analytics-noninteraction"); //hidden transactions


        var linkEvars = {
            hitType: hitType,
            eventCategory: eventCategory,
            eventAction: eventAction,
            eventLabel: eventLabel
        };

        if (eventNonInteractive) {
            linkEvars["nonInteraction"] = eventNonInteraction;
        }

        if (eventTransport) {
            linkEvars["transport"] = eventTransport;
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

        var href = $button.attr("href"); //event
        var target = $button.attr("target") || ""; //event
        var analyticsType = $button.attr("data-analytics-type") || ""; //event

        if (reset) {
            $button.attr('onclick', '');
        }

        if (analyticsType==="aa") {
            ns.enableAA($button);
        } else if (analyticsType==="ga") {
            ns.enableGA($button);
        }

        var onClick = new Function('window.open("'+ window.encodeURI(href)+'","'+target+'")');

        $button.click(onClick);


    };

})(AEMDESIGN.jQuery, _, ko, AEMDESIGN.analytics, AEMDESIGN.log, this); //pass in additional dependencies

