//aemdesign.utils.js
window.AEMDESIGN = window.AEMDESIGN || {};
window.AEMDESIGN.utils = window.AEMDESIGN.utils || {};
(function ($, ko, ns, log, http, window, undefined) {

    "use strict";
    var _version = "0.1";

    ns.version = function () {
        return _version;
    };

    ns.findItem = function (array, prop, value) {
        for (var i = 0, len = array.length; i < len; i++) {
            if (array[i][prop] === value) {
                return array[i];
            }
        }
    };

    ns.camelCaseConvert = function (values) {
        var camelCaseConverted = [];
        for (var i = 0; i < values.length; i++) {
            var value = values[i];
            if (i === 0) {
                camelCaseConverted.push(value);
                continue;
            }
            var newValue = value.charAt(0).toUpperCase() + value.slice(1);
            camelCaseConverted.push(newValue);
        }

        return camelCaseConverted;
    };

    String.prototype.endsWith = function(suffix) {
        return this.indexOf(suffix, this.length - suffix.length) !== -1;
    };

    String.prototype.startsWith = function(prefix) {
        return this.indexOf(prefix) == 0;
    };

    String.prototype.right = function(length) {
        return this.substring(this.length-length);
    };

    String.prototype.left = function(length) {
        return this.substring(0,length);
    };

    ns.numberWithCommas = function (x) {
        return x.toString().replace(/\B(?=(?:\d{3})+(?!\d))/g, ",");
    };

    ns.smoothAnchorScroll = function (target, offset) {
        offset = offset || { top: 0 };

        $('html, body').animate({
            scrollTop: offset.top
        }, {
            duration: 700,
            complete: function() {
                if(target !== '#') {
                    window.location.hash = target;
                }
            }
        });
    };



    ns.jsonPath = function (obj, expr, arg) {
        return jsonPath(obj, expr, arg);
    };

    ns.getJsonPathValue = function (obj, expr, arg, defaultValue) {
        var returnValue = ns.jsonPath(obj, expr, arg);

        if (returnValue instanceof Array) {
            if (returnValue.length != 0) {
                returnValue = returnValue[0];
                return returnValue;
            }
        }

        return defaultValue;
    };

    ns.getPropertyValue = function (obj,property,defaultValue) {
        var returnValue = defaultValue;
        try {
            if (obj.hasOwnProperty(property)) {
                returnValue = obj[property];
                if (typeof returnValue === 'undefined') {
                    returnValue = defaultValue;
                }
            }
        } catch (ex) {
            //jyst in case
            returnValue = defaultValue;
        }

        return returnValue;
    };

    ns.getParameterByName = function (name) {
        name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
        var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
            results = regex.exec(location.search);
        return results == null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
    };

    ns.patchText = function(text, snippets) {
        if (snippets) {
            if (!$.isArray(snippets)) {
                text = text.replace("{0}", snippets);
            } else {
                for (var i=0; i < snippets.length; i++) {
                    text = text.replace(("{" + i + "}"), snippets[i]);
                }
            }
        }
        return text;
    };

    ns.htmlEncode = function(value) {
        return !value ? value : String(value).replace(/&/g, "&amp;").replace(/>/g, "&gt;").replace(/</g, "&lt;").replace(/"/g, "&quot;");
    };

    ns.htmlDecode = function(value) {
        return !value ? value : String(value).replace(/&gt;/g, ">").replace(/&lt;/g, "<").replace(/&quot;/g, '"').replace(/&amp;/g, "&");
    };



    ns.reload = function(win, url, preventHistory) {
        if (!win) win = window;
        if (!url) {
            url = http.noCaching(win.location.href);
        }

        if (preventHistory) {
            win.location.replace(url);
        } else {
            win.location.href = url;
        }
    };

    ns.load = function(url, preventHistory) {
        ns.reload(window, url, preventHistory);
    };

    ns.open = function(url, win, name, options) {
        if (!win) win = window;
        if (!url) {
            return;
        }

        if (!name) {
            name = "";
        }
        if (!options) {
            options = "";
        }

        return win.open(url, name, options);
    };

    ns.ellipsis = function(value, length, word) {
        if (value && value.length > length) {
            if (word) {
                var vs = value.substr(0, length - 2);
                var index = Math.max(vs.lastIndexOf(' '), vs.lastIndexOf('.'), vs.lastIndexOf('!'), vs.lastIndexOf('?'), vs.lastIndexOf(';'));
                if (index == -1 || index < (length - 15)) {
                    return value.substr(0, length - 3) + "...";
                } else {
                    return vs.substr(0, index) + "...";
                }
            } else {
                return value.substr(0, length - 3) + "...";
            }
        }
        return value;
    };

    /* ko error handler*/
    var ErrorHandlingBindingProvider = function() {
        var original = new ko.bindingProvider();

        //determine if an element has any bindings
        this.nodeHasBindings = original.nodeHasBindings;

        //return the bindings given a node and the bindingContext
        this.getBindings = function(node, bindingContext) {
            var result;
            try {
                result = original.getBindings(node, bindingContext);
            }
            catch (e) {
                if (window.console && window.console.log) {
                    log.info("Error in binding: " + e.message);
                    log.info([node, bindingContext]);
                }
            }

            return result;
        };
    };

    $(document).ready(function() {

        ko.bindingProvider.instance = new ErrorHandlingBindingProvider();

        log.info("Updated ko.bindingProvider");

    });


})(jQuery, ko, AEMDESIGN.utils, AEMDESIGN.log, AEMDESIGN.http, this);
