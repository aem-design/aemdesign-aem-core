//aemdesign.services.js
window.AEMDESIGN = window.AEMDESIGN || {"jQuery":{}};
window.AEMDESIGN.services = window.AEMDESIGN.services || {};
(function ($, ns, sling, window, undefined) {

    "use strict";
    var _version = "0.1";

    ns.version = function () {
        return _version;
    };

    ns.target = {

        URL_API_CONTENT: {url: "http://content.aemdesign.local"},
        URL_API: {url: "https://api.aemdesign.local"}

    };

    ns.endpoints = {


        PRODUCTS: {path: "/etc/tags/products.valuelist.json" },
        ENQUIRY_TYPE: {path: "/etc/tags/forms/enquiry-type.valuelist.json"},
        SALUTATIONS: {path: "/etc/tags/forms/salutations.valuelist.json"},

        PRICES: {path: "/service/price"},

        LEADS: {
            path: "/leads/",
            addHash: true,
            levels: 3,
            target: ns.target.URL_API
        },
        MODELS: {
            path: "/etc/commerce/products/aemdesign/auProductCatalog/en/categories/products",
            levels:"5",
            extension: "json"
        },
        TAG: {
            path: "/etc/tags/",
            levels:"",
            extension: "valuelist.current.json"
        },
        CHILDTAG: {
            path: "/etc/tags/",
            levels:"",
            extension: "valuelist.json"
        },
        TAGS: {
            path: "/etc/tags/",
            levels:"5",
            extension: "json"
        }



    };

    ns.getPathHash = function (levels) {
        return new Date().toISOString().substring(0,10).replace(/-/gi,"/");
    };

    ns.getServiceURL = function (endpoint) {
        var extension = endpoint.extension || "";
        var levels = endpoint.levels || "";

        var url = "";
        var target = endpoint.target ? endpoint.target.url : "";

        if (extension) {
            url = endpoint.path
                + (levels != "" ? sling.constants.SLING_FILENAME_SEPARATOR + levels : "")
                + (extension != "" ? sling.constants.SLING_FILENAME_SEPARATOR + extension : "");
        } else {
            url = endpoint.path
                + (endpoint.addHash
                    ? ns.getPathHash(endpoint.levels) + sling.constants.SLING_PATH_SUFFIX_SLASHSTAR
                    : "");
        }

        return target + url;
    };

    ns.externalize = function (endpoint) {

        var url = typeof(endpoint) == 'object' ? ns.getServiceURL(endpoint) : endpoint;
        var target = typeof(endpoint) == 'object' && endpoint.target ? endpoint.target.url : "";

        if (target) {
            return target + url;
        } else if (window.location.hostname == 'localhost') {
            return window.location.protocol + "//" + window.location.host + url;
        }   else  {
            return window.location.protocol + "//" + window.location.host + url;
        }
    };


})(AEMDESIGN.jQuery, AEMDESIGN.services, AEMDESIGN.sling, this);

