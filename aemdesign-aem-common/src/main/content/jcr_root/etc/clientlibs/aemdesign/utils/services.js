//aemdesign.services.js
window.AEMDESIGN = window.AEMDESIGN || {"jQuery":{}};
window.AEMDESIGN.services = window.AEMDESIGN.services || {};
(function ($, ns, sling, window, undefined) { //NOSONAR namespace convention

    "use strict";
    var _version = "0.1";

    ns.version = function () {
        return _version;
    };

    ns.target = {

        URL_API_CONTENT: {url: "http://aem.design.local"},
        URL_API: {url: "https://aem.design.local"}

    };

    ns.endpoints = {


        PRODUCTS: {path: "/content/cq:tags/aemdesign/products.valuelist.json" },
        ENQUIRY_TYPE: {path: "/content/cq:tags/aemdesign/forms/enquiry-type.valuelist.json"},
        SALUTATIONS: {path: "/content/cq:tags/aemdesign/forms/salutations.valuelist.json"},

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
            path: "/content/cq:tags/aemdesign",
            levels:"",
            extension: "valuelist.current.json"
        },
        CHILDTAG: {
            path: "/content/cq:tags/aemdesign",
            levels:"",
            extension: "valuelist.json"
        },
        TAGS: {
            path: "/content/cq:tags/aemdesign",
            levels:"5",
            extension: "json"
        }



    };

    ns.getPathHash = function () {
        return new Date().toISOString().substring(0,10).replace(/-/gi,"/");
    };

    ns.getServiceURL = function (endpoint) {
        var extension = endpoint.extension || "";
        var levels = endpoint.levels || "";

        var url = "";
        var target = endpoint.target ? endpoint.target.url : "";

        if (extension && levels) {
            var urlLevels = sling.constants.SLING_FILENAME_SEPARATOR + levels;
            var urlExtension = sling.constants.SLING_FILENAME_SEPARATOR + extension;

            url = endpoint.path + urlLevels + urlExtension;
        } else {
            url = endpoint.path
                + (endpoint.addHash
                    ? ns.getPathHash(endpoint.levels) + sling.constants.SLING_PATH_SUFFIX_SLASHSTAR
                    : "");
        }

        return target + url;
    };

    ns.externalize = function (endpoint) {

        var url = typeof(endpoint) === 'object' ? ns.getServiceURL(endpoint) : endpoint;
        var target = typeof(endpoint) === 'object' && endpoint.target ? endpoint.target.url : "";

        if (target) {
            return target + url;
        } else if (window.location.hostname === 'localhost') {
            return window.location.protocol + "//" + window.location.host + url;
        }   else  {
            return window.location.protocol + "//" + window.location.host + url;
        }
    };


})(AEMDESIGN.jQuery, AEMDESIGN.services, AEMDESIGN.sling, this);

