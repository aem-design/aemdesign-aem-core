//aemdesign.log.js
window.AEMDESIGN = window.AEMDESIGN || {};
window.AEMDESIGN.log = window.AEMDESIGN.log || {};

(function ($, ns, window, undefined) {

    //"use strict";
    var _version = "0.1";
    var settings = {
        enableLog: false
    };

    ns.version = function () {
        return _version;
    };

    ns.enableLog = function() {
        settings.enableLog = true;
    };

    ns.disableLog = function() {
        settings.enableLog = false;
    };

    ns.warn = function(data) {
        ns.log(data);
    };

    ns.info = function(data) {
        ns.log(data);
    };

    ns.debug = function(data) {
        ns.log(data);
    };

    ns.error = function(data) {
        ns.log(data);
    };

    ns.log = function(data) {

        if (window.console && window.console.log) {
            var url = $(location).attr('href');

            var traceStack;
            if (typeof printStackTrace == "function") {
                traceStack = printStackTrace();
            }
            var debug = {
                "caller": arguments.caller,
                "traceStack": traceStack
            };

            if (settings.enableLog) {
                console.log([url,data,debug]);
            }
        }

    };

})(jQuery, AEMDESIGN.log, this);

