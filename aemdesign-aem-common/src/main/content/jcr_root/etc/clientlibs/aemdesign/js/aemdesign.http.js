//aemdesign.http.js
window.AEMDESIGN = window.AEMDESIGN || {"jQuery":{}};
window.AEMDESIGN.http = window.AEMDESIGN.http || {};

(function ($, ns, log, window, undefined) {

    "use strict";
    var _version = "0.1";

    ns.version = function () {
        return _version;
    };

    ns.PARAMS= {
        PARAM_NO_CACHE: "nocache"
    };

    ns.post = function (url, data) {
        var ajaxReturn =  $.ajax({
            type: "POST",
            url: url,
            data: data
        });

        return ajaxReturn;
    };

    ns.getSync = function (url, data, dataType) {
        //return promise
        return $.ajax({
            type: 'GET',
            async: false,
            url: url,
            data: data,
            dataType: dataType
        });
    };

    ns.get = function (url, data, dataType) {
        return $.ajax({
            type: 'GET',
            url: url,
            data: data,
            dataType: dataType
        });
//            .then(
//            /*done*/
//            function (data, textStatus, jqXHR ) {
//                if (typeof callback === "function") {
//                    callback(data);
//                }
//            },
//            /*fail*/
//            function (jqXHR, textStatus, errorThrown ) {
//                log.info({
//                    "event": event,
//                    "jqxhr": jqXHR,
//                    "status": textStatus,
//                    "thrownError": errorThrown
//                });
//            },
//            /*progress*/
//            function (event) {
//
//            }
//        );
    };

    ns.handleLoginRedirect = function () {
        log.warn("not logged in");
    };

    ns.noCaching = function(url) {
        return ns.setParameter(url, PARAMS.PARAM_NO_CACHE, new Date().valueOf());
    };

    ns.addParameter = function(url, name, value) {
        if (value && value instanceof Array) {
            for (var i = 0; i < value.length; i++) {
                url = ns.addParameter(url, name, value[i]);
            }
            return url;
        }
        var separator = url.indexOf("?") == -1 ? "?" : "&";
        var hashIdx = url.indexOf("#");
        if (hashIdx < 0) {
            return url + separator + encodeURIComponent(name) + "=" + encodeURIComponent(value);
        } else {
            var hash = url.substring(hashIdx);
            url = url.substring(0, hashIdx);
            return url + separator + encodeURIComponent(name) + "=" + encodeURIComponent(value) + hash;
        }
    };

    ns.removeParameter = function(url, name) {
        var pattern0 = "?" + encodeURIComponent(name) + "=";
        var pattern1 = "&" + encodeURIComponent(name) + "=";
        var pattern;
        if (url.indexOf(pattern0) != -1) {
            pattern = pattern0;
        }
        else if (url.indexOf(pattern1) != -1) {
            pattern = pattern1;
        }
        else {
            return url;
        }

        var indexCutStart = url.indexOf(pattern);
        var begin = url.substring(0, indexCutStart);

        var indexCutEnd = url.indexOf("&", indexCutStart + 1);
        var end = "";
        if (indexCutEnd != -1) {
            end = url.substring(indexCutEnd);
            if (end.indexOf("&") == 0) {
                end = end.replace("&", "?");
            }
        }
        return begin + end;
    };

    ns.setParameter = function(url, name, value) {
        url = ns.removeParameter(url, name);
        return ns.addParameter(url, name, value);
    };


    ns.removeParameters = function (url) {
        if (url.indexOf("?") != -1) {
            return url.substring(0, url.indexOf("?"));
        }
        return url;
    };

    ns.removeAnchor = function (url) {
        if (url.indexOf("#") != -1) {
            return url.substring(0, url.indexOf("#"));
        }
        return url;
    };

    ns.addSelector = function(url, selector, index) {
        if (!index) index = 0;

        // url:  /x/y.z.json?a=1#b
        // post: ?a=1#b
        // path: /x
        // main: y.z.json
        var post = ""; // string of parameters and anchor
        var pIndex = url.indexOf("?");
        if (pIndex == -1) pIndex = url.indexOf("#");
        if (pIndex != -1) {
            post = url.substring(pIndex);
            url = url.substring(0, pIndex);
        }
        var sIndex = url.lastIndexOf("/");
        var main = url.substring(sIndex); // name, selectors and extension
        if (main.indexOf("." + selector + ".") == -1) {
            var path = url.substring(0, sIndex);
            var obj = main.split(".");
            var newMain = "";
            var delim = "";
            if (index > obj.length - 2 || index == -1) {
                // insert at last position
                index = obj.length - 2;
            }
            for (var i = 0; i < obj.length; i++) {
                newMain += delim + obj[i];
                delim = ".";
                if (index == i) {
                    newMain += delim + selector;
                }
            }
            return path + newMain + post;
        }
        else {
            return url;
        }
    };

    ns.setSelector = function(url, selector, index) {

        var post = "";
        var pIndex = url.indexOf("?");
        if (pIndex == -1) pIndex = url.indexOf("#");
        if (pIndex != -1) {
            post = url.substring(pIndex);
            url = url.substring(0, pIndex);
        }

        var selectors = ns.getSelectors(url);
        var ext = url.substring(url.lastIndexOf("."));
        // cut extension
        url = url.substring(0, url.lastIndexOf("."));
        // cut selectors
        var fragment = (selectors.length > 0) ? url.replace("." + selectors.join("."), "") : url;

        if (selectors.length > 0) {
            for (var i = 0; i < selectors.length; i++) {
                if (index == i) {
                    fragment += "." + selector;
                } else {
                    fragment += "." + selectors[i]
                }
            }
        } else {
            fragment += "." + selector;
        }

        return fragment + ext + post;
    };

    ns.addSelectors= function(url, selectors) {
        var res = url;
        if( url && selectors && selectors.length) {
            for(var i=0;i< selectors.length;i++) {
                res = ns.addSelector(res, selectors[i], i);
            }
        }
        return res;
    };

    ns.getSelectors = function(url) {

        if (!url && window.CQURLInfo) {
            if (CQURLInfo.selectors) {
                return CQURLInfo.selectors;
            }
        }

        var selectors = [];

        url = url || window.location.href;

        url = ns.removeParameters(url);
        url = ns.removeAnchor(url);

        var fragment = url.substring(url.lastIndexOf("/"));
        if (fragment) {
            var split = fragment.split(".");
            if (split.length > 2) {
                for (var i = 0; i < split.length; i++) {
                    // don't add node name and extension as selectors
                    if (i > 0 && i < split.length - 1) {
                        selectors.push(split[i]);
                    }
                }
            }
        }

        return selectors;
    };


    ns.getExtension = function(url) {

        if (!url && window.CQURLInfo) {
            if (CQURLInfo.extension) {
                return CQURLInfo.extension;
            }
        }

        url = url || window.location.href;

        // strip things from the end
        url = ns.removeParameters(url);
        url = ns.removeAnchor(url);

        // extension is everything after the last dot
        var pos = url.lastIndexOf(".");
        if (pos < 0) {
            return "";
        }

        // do not include the dot
        url = url.substring(pos + 1);

        // remove suffix if present
        pos = url.indexOf("/");
        if (pos < 0) {
            return url;
        }

        return url.substring(0, pos);
    };


})(AEMDESIGN.jQuery, AEMDESIGN.http, AEMDESIGN.log, this);
