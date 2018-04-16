//aemdesign.log.js
window.AEMDESIGN = window.AEMDESIGN || {"jQuery":{}};
window.AEMDESIGN.log = window.AEMDESIGN.log || {};

(function ($, ns, window, undefined) {

    //"use strict";
    var _version = "0.1";
    ns.version = function () {
        return _version;
    };

    var DEFAULT_LOG_LEVEL = 4;

    var logLevels = {
        0: " OFF ",
        1: "FATAL",
        2: "ERROR",
        3: "WARN ",
        4: "INFO ",
        5: "DEBUG",
        6: "TRACE",
        7: " ALL "
    };

    ns.LEVEL = {
        OFF: 0,
        FATAL: 1,
        ERROR: 2,
        WARN: 3,
        INFO: 4,
        DEBUG: 5,
        TRACE: 6,
        ALL: 7
    };

    ns.getInitialLogLevel = function() {
        if (window.aemdesign_init_log_level) {
            if (typeof window.aemdesign_init_log_level === "string") {
                for (var i in logLevelNames) {
                    if (logLevels[i].indexOf(window.aemdesign_init_log_level) >= 0) {
                        return i;
                    }
                }
            } else if (typeof window.aemdesign_init_log_level === "number") {
                return window.aemdesign_init_log_level;
            }
        }
        return DEFAULT_LOG_LEVEL;
    };

    var settings = {
        enableLog: false,
        initLogLevel: DEFAULT_LOG_LEVEL,
        logLevel: ns.getInitialLogLevel()
    };

    ns.setLevel = function(level) {
        settings.logLevel = level;
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

    ns.logex = function(level, message, snippets) {
        if (settings.logLevel >= level) {
            var text = (++logCounter)+"";
            var i = text.length;
            while ((4-i) > 0) {
                text = "0"+text;
                i++;
            }
            text += " " + logLevels[level];
            text += " " + new Date().toLocaleDateString();
            text += " " + AEMDESIGN.utils.patchText(message, snippets);
            try {
                console.log(text);
            } catch (e) {
            }
        }
    };

    ns.fatalex = function(message, snippets) {
        ns.logex(ns.LEVEL["FATAL"], message, snippets);
    };

    ns.errorex = function(message, snippets) {
        ns.logex(ns.LEVEL["ERROR"], message, snippets);
    };

    ns.warnex = function(message, snippets) {
        ns.logex(ns.LEVEL["WARN"], message, snippets);
    };

    ns.infoex = function(message, snippets) {
        ns.logex(ns.LEVEL["INFO"], message, snippets);
    };

    ns.debugex = function(message, snippets) {
        ns.logex(ns.LEVEL["DEBUG"], message, snippets);
    };

    ns.traceex = function(message, snippets) {
        ns.logex(ns.LEVEL["TRACE"], message, snippets);
    };



})(AEMDESIGN.jQuery, AEMDESIGN.log, this);

