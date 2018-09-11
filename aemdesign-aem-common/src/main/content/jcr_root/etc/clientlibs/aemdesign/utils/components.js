//aemdesign.components.js
window.AEMDESIGN = window.AEMDESIGN || {"jQuery":{}};
window.AEMDESIGN.components = window.AEMDESIGN.components || {};
(function ($, ns, sling, window, undefined) {

    "use strict";
    var _version = "0.1";

    ns.version = function () {
        return _version;
    };

    ns.defaults = {
        PAGE_COMPONENT: "aemdesign/components/template/content",
        PAGE_TEMPLATE: "/apps/aemdesign/templates/content",
        PAGE_CONTENT: "jcr:content",
        PATH_RESOURCE_DATA: "aemdesign/components/data/",
        PATH_PARSYS: "par",
        PATH_SEPARATOR: "/",
        EMPTY: ""
    };


    ns.componentTemplate = {
        primaryType: ns.defaults.EMPTY,
        resourceType: ns.defaults.EMPTY,
        templatePath: ns.defaults.EMPTY,
        nameHint: ns.defaults.EMPTY,
        extraParams: undefined,
        isChildNode: false
    };

    ns.compileResourcePath = function (name) {
        return ns.defaults.PATH_RESOURCE_DATA
            + name;
    };

    ns.compileComponentName = function (name) {
        return ns.defaults.PAGE_CONTENT
                + ns.defaults.PATH_SEPARATOR
                + ns.defaults.PATH_PARSYS
                + ns.defaults.PATH_SEPARATOR
                + name;
    };

    ns.Page = function (extraParams, nameHint) {
        return {
            primaryType: sling.constants.TYPE_CQ_PAGE,
            resourceType: ns.defaults.EMPTY,
            templatePath: ns.defaults.EMPTY,
            nameHint: nameHint ? nameHint : ns.defaults.EMPTY,
            extraParams: extraParams,
            isChildNode: false
        };
    };

    ns.PageContent = function (extraParams, resourceType, templatePath, nameHint) {
        return {
            primaryType: sling.constants.TYPE_CQ_PAGECONTENT,
            resourceType: resourceType ? resourceType : ns.defaults.PAGE_COMPONENT,
            templatePath: templatePath ? templatePath : ns.defaults.PAGE_TEMPLATE,
            nameHint: nameHint ? nameHint : ns.defaults.PAGE_CONTENT,
            extraParams: extraParams,
            isChildNode: true
        };
    };

    ns.Parsys = function (extraParams, nameHint) {
        return {
            primaryType: sling.constants.TYPE_NT_UNSTRUCTURED,
            resourceType: sling.constants.RESOURCE_PARSYS,
            templatePath: ns.defaults.EMPTY,
            nameHint: nameHint ? nameHint : ns.defaults.PAGE_CONTENT + ns.defaults.PATH_SEPARATOR + ns.defaults.PATH_PARSYS,
            extraParams: extraParams,
            isChildNode: true
        };
    };

    ns.LeadDetails = function (extraParams, nameHint) {
        return {
            primaryType: sling.constants.TYPE_NT_UNSTRUCTURED,
            resourceType: ns.compileResourcePath("lead-details"),
            templatePath: ns.defaults.EMPTY,
            nameHint: nameHint ? nameHint : ns.compileComponentName("lead-details"),
            extraParams: extraParams,
            isChildNode: true
        };
    };

    ns.ContactDetails = function (extraParams, nameHint) {
        return {
            primaryType: sling.constants.TYPE_NT_UNSTRUCTURED,
            resourceType: ns.compileResourcePath("contact-details"),
            templatePath: ns.defaults.EMPTY,
            nameHint: nameHint ? nameHint : ns.compileComponentName("contact-details"),
            extraParams: extraParams,
            isChildNode: true
        };
    };

    ns.AddressDetails = function (extraParams, nameHint) {
        return {
            primaryType: sling.constants.TYPE_NT_UNSTRUCTURED,
            resourceType: ns.compileResourcePath("addressDetails"),
            templatePath: ns.defaults.EMPTY,
            nameHint: nameHint ? nameHint : ns.compileComponentName("address-details"),
            extraParams: extraParams,
            isChildNode: true
        };
    };

    ns.EnquiryDetails = function (extraParams, nameHint) {
        return {
            primaryType: sling.constants.TYPE_NT_UNSTRUCTURED,
            resourceType: ns.compileResourcePath("enquiryDetails"),
            templatePath: ns.defaults.EMPTY,
            nameHint: nameHint ? nameHint : ns.compileComponentName("enquiry-details"),
            extraParams: extraParams,
            isChildNode: true
        };
    };

    ns.SubscribeDetails = function (extraParams, nameHint) {
        return {
            primaryType: sling.constants.TYPE_NT_UNSTRUCTURED,
            resourceType: ns.compileResourcePath("subscribeDetails"),
            templatePath: ns.defaults.EMPTY,
            nameHint: nameHint ? nameHint : ns.compileComponentName("subscribe-details"),
            extraParams: extraParams,
            isChildNode: true
        };
    };

})(AEMDESIGN.jQuery, AEMDESIGN.components, AEMDESIGN.sling, this);

