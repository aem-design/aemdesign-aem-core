//link - functions
window.AEMDESIGN = window.AEMDESIGN || {"jQuery":{}};
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.link = AEMDESIGN.components.link || {};

(function ($, _, ko, ns, log, window, undefined) { //add additional dependencies


    "use strict";
    var _version = "0.1";

    ns.version = function () {
        return _version;
    };

    ns.enableAA = function(link) {

        var pageTitle = document.title;
        var linkLabel = link.getAttribute("data-label") || $(link).text().trim(); //link title
        var hitType = link.getAttribute("data-analytics-hit-type"); //eg link_o ...
        var eventLabel = linkLabel || link.getAttribute("data-analytics-event-label"); //title of link
        var eventCategory = link.getAttribute("data-analytics-event-category"); //eg ${Page Title}:${Link Click}:${Link Title}

        try {
            var linkDom = link.get();

            var linkEvars = {
                event: hitType,
                category: [pageTitle,eventCategory,eventLabel].join(":"),
                label: eventLabel
            };

            linkDom.setAttribute('onclick', '_dtmLayer.push('+JSON.stringify(linkEvars)+')');
        } catch (ex) {
            //error
        }
    };

    ns.enableGA = function(link) {

        var hitType = link.getAttribute("data-analytics-hit-type"); //event
        var eventCategory = link.getAttribute("data-analytics-event-category"); //eg Link Click
        var eventAction = link.getAttribute("data-analytics-event-action"); //eg Content, Navigation, Email, Phone
        var eventLabel = link.getAttribute("data-analytics-event-label"); //title of link
        var eventTransport = link.getAttribute("data-analytics-transport"); //beacon
        var eventNonInteraction = link.getAttribute("data-analytics-noninteraction"); //hidden transactions


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

        try {
            var linkDom = link.get();
            // linkDom.setAttribute('onclick', 'ga("send", '+JSON.stringify(linkEvars)+')');
            linkDom.setAttribute('onclick', 'dataLayer.push('+JSON.stringify(linkEvars)+')');
        } catch (ex) {
            //error
        }

    };

})(AEMDESIGN.jQuery, _, ko, AEMDESIGN.components.link, AEMDESIGN.log, this); //pass in additional dependencies

