//aemdesign.commerce.js
window.AEMDESIGN = window.AEMDESIGN || {};
window.AEMDESIGN.commerce = window.AEMDESIGN.commerce || {};

(function ($, _, ns, http, services, utils, window, undefined) {

    "use strict";
    var _version = "0.1";

    ns.version = function () {
        return _version;
    };

    ns.getProductByTags = function (data, productName) {
        var productList = utils.jsonPath(data, "$.[?(@[\"cq:tags\"] instanceof Array && @[\"cq:tags\"].indexOf(\"" + productName + "\")!=-1 )]");
        return JSON.stringify(productList);
    };

    ns.getProductVariants = function (data)
    {
        var variants = [];
        var variantList =  utils.jsonPath(data,"$.[?(@[\"cq:commerceType\"]==\"variant\")]");

        variantList.each(variantList, function(variant) {
            if (variant) {
                variants.push(variant);
            }
        });

        return variants;
    };

    ns.getProductAll = function () {
        var url = services.externalize(services.endpoints.PRODUCTS);
        return http.getSync(url);
    };

    ns.loadProducts = function () {
        ns.products = ns.getProductAll();
    };

    ns.getModelByIdentifier = function (data, identifier) {
        return utils.jsonPath(ns.products, "$.[?(@[\"identifier\"]==\"" + identifier + "\")]");
    };

    ns.getProduct = function (productPath) {
        var endpoint = _.clone(services.endpoints.PRODUCTS);
        endpoint.path = productPath;
        var url = services.externalize(endpoint);
        return http.getSync(url);
    };

    ns.getTag = function (tagPath) {
        var endpoint = _.clone(services.endpoints.TAGS);
        if (tagPath==undefined) {
            return;
        }
        endpoint.path =  tagPath.replace(":", "/");
        var url = services.externalize(endpoint);
        return http.getSync(url);
    };


    ns.getTagNamespace = function (tagPath) {
        var endpoint = _.clone(services.endpoints.TAGS);
        if (tagPath==undefined) {
            return;
        }
        endpoint.path = endpoint.path + tagPath.split("/")[0].replace(":", "/");
        var url = services.externalize(endpoint);
        return http.getSync(url);
    };

    ns.getTagFromNamespace = function(namespaceData, tagPath) {
        var tagPathParts = tagPath.split("/").splice(1);
        if (tagPath==undefined) {
            return;
        }
        if (tagPathParts instanceof Array) {
            var tagSearch = "$."+tagPathParts.join(".");

            return utils.jsonPath(namespaceData,tagSearch);
        }
    };


})(jQuery, _, AEMDESIGN.commerce, AEMDESIGN.http, AEMDESIGN.services, AEMDESIGN.utils, this);

