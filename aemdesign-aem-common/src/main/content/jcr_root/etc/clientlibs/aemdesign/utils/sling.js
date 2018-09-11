//aemdesign.sling.js
window.AEMDESIGN = window.AEMDESIGN || {"jQuery":{}};
window.AEMDESIGN.sling = window.AEMDESIGN.sling || {};

(function ($, ns, http, log, window, undefined) { //NOSONAR namespace convention

    "use strict";
    var _version = "0.1";


    ns.version = function () {
        return _version;
    };


    ns.PostRequest = function () {
        this.params = {};
    };


    ns.PostRequest.prototype.addHashContent = function (endPoint, resourceType, template) {
        if (endPoint.addHash) {
            var nlevels = isNaN(endPoint.levels) ? 3 : parseInt(endPoint.levels);

            var params = {};
            var clevel = "";
            var levelstep = "../";

            for (var i = 0; i < nlevels; i++) {
                clevel += levelstep;
                params[clevel + ns.constants.FIELD_JCR_PRIMARY_TYPE] = ns.constants.TYPE_CQ_PAGE;
                params[clevel
                    + ns.constants.PAGE_CONTENT
                    + ns.constants.SLING_PATH_SEPARATOR
                    + ns.constants.FIELD_JCR_PRIMARY_TYPE] = ns.constants.TYPE_CQ_PAGECONTENT;
                params[clevel
                    + ns.constants.PAGE_CONTENT
                    + ns.constants.SLING_PATH_SEPARATOR
                    + ns.constants.FIELD_SLING_RESOURCE_TYPE] = resourceType;
                params[clevel
                    + ns.constants.PAGE_CONTENT
                    + ns.constants.SLING_PATH_SEPARATOR
                    + ns.constants.FIELD_CQ_TEMPLATE] = template;

            }

            return this.setParams(params, true);
        } else {
            return this;
        }
    };

    ns.PostRequest.prototype.getName = function () {
        return "";
    };

    ns.PostRequest.prototype.setURL = function (url) {

        if (!url.endsWith(ns.constants.SLING_PATH_SUFFIX_SLASH) && !url.endsWith(ns.constants.SLING_PATH_SUFFIX_SLASHSTAR)) {
            url = url + ns.constants.SLING_PATH_SUFFIX_SLASHSTAR;
        }
        this.url = url;
        return this;
    };

    ns.PostRequest.prototype.getParams = function () {
        var returnParams = {};
        if (!this.params) {
            return returnParams;
        }

        for (var i in this.params) {
            if (typeof(this.params[i]) === 'object') {
                var rootPath = i;
                if (this.params[i].hasOwnProperty("params")) {
                    for (var x in this.params[i].params) { //NOSONAR this is not complex
                        if (this.params[i].params.hasOwnProperty("x")) {
                            var path = x.startsWith(ns.constants.SLING_FIELD_INCLUDE_PREFIX)
                                ? x.right(x.length - ns.constants.SLING_FIELD_INCLUDE_PREFIX.length)
                                : x;
                            returnParams[rootPath + "/" + path] = this.params[i].params[x];
                        }
                    }
                }
            } else {
                returnParams[i]=this.params[i];
            }
        }
        return returnParams;
    };


    ns.PostRequest.prototype.setParam = function (name, value, exclude) {
        var prefix = ns.constants.SLING_FIELD_INCLUDE_PREFIX;
        if (exclude) {
            prefix = "";
        }
        this.params[prefix + name] = value;
        return this;
    };

    ns.PostRequest.prototype.setParams = function (params, exclude) {
        if (!params) {
            return this;
        }

        for (var i in params) {
            if (params.hasOwnProperty(i)) {
                this.setParam(i, params[i], exclude);
            }
        }
        return this;
    };
    ns.PostRequest.prototype.addChild = function (name, child) {
        if (!name || !child) {
            return this;
        }

        return this.setParam(name,child);
    };

    ns.PostRequest.prototype.setJcrCreatedParams = function () {
        return (
            this
                .setParam(ns.constants.FIELD_JCR_CREATED, "")
                .setParam(ns.constants.FIELD_JCR_CREATEDBY, "")
            );
    };

    ns.PostRequest.prototype.setJcrModifiedParams = function () {
        return (
            this
                .setParam(ns.constants.FIELD_JCR_MODIFIED, "")
                .setParam(ns.constants.FIELD_JCR_MODIFIEDBY, "")
            );
    };

    ns.PostRequest.prototype.setSlingResTypeParam = function (resourceType) {
        if (resourceType === "") {
            return this;
        }
        return (
            this
                .setParam(ns.constants.FIELD_SLING_RESOURCE_TYPE, resourceType)
            );
    };

    ns.PostRequest.prototype.setJcrPrimaryTypeParam = function (primaryType) {
        if (primaryType === "") {
            return this;
        }
        return (
            this
                .setParam(ns.constants.FIELD_JCR_PRIMARY_TYPE, primaryType)
            );
    };

    ns.PostRequest.prototype.setCqTemplatePathParam = function (templatePath) {
        if (templatePath === "") {
            return this;
        }
        return (
            this
                .setParam(ns.constants.FIELD_CQ_TEMPLATE, templatePath)
            );
    };

    ns.PostRequest.prototype.setCharset = function (charset) {
        charset = charset || ns.constants.CHARSET_UTF8;
        return this.setParam(ns.constants.CHARSET, charset);
    };

    ns.PostRequest.prototype.setNameHintParam = function (nameHint, resourceType) {
        if (nameHint === "" && resourceType === "") {
            return this;
        }
        return (
            this
                .setParam(":nameHint", nameHint ? nameHint : resourceType.substring(resourceType.lastIndexOf("/") + 1), true)
            );
    };

    ns.PostRequest.prototype.setOrderParam = function (relativePosition, neighborName) {
        if (relativePosition === "" || neighborName === "") {
            return this;
        }
        return (
            this
                .setParam(":order", neighborName && neighborName !== "*" ?
                    (relativePosition + " " + neighborName) :
                    ns.constants.PARAGRAPH_ORDER.last, true)
            );
    };

    ns.PostRequest.prototype.prepareCreateComponents = function(components) {
        if (!components) {
            return this;
        }

        for (var i in components) {
            if (components.hasOwnProperty(i)) {
                var component = components[i];

                var childNode = new ns.PostRequest().prepareCreateNode(
                    component.primaryType,
                    component.resourceType,
                    component.templatePath,
                    component.extraParams,
                    component.nameHint,
                    false
                );

                this.addChild(component.nameHint,childNode);
            }
        }
        return(this);
    };


    ns.PostRequest.prototype.prepareCreateComponent = function(componentTemplate) {
        if (!componentTemplate) {
            return this;
        }

        return (
            this.prepareCreateNode(
                componentTemplate.primaryType,
                componentTemplate.resourceType,
                componentTemplate.templatePath,
                componentTemplate.extraParams,
                componentTemplate.nameHint,
                componentTemplate.isChildNode
            )
        );
    };

    ns.PostRequest.prototype.prepareCreateNode = function (primaryType,
                                                           resourceType,
                                                           templatePath,
                                                           extraParams,
                                                           nameHint,
                                                           isChildNode) {
        if (!isChildNode) {
            return (
                this
                    .setJcrPrimaryTypeParam(primaryType)
                    .setSlingResTypeParam(resourceType)
                    .setCqTemplatePathParam(templatePath)
                    .setParams(extraParams)
                );
        } else {

            var childNode = new ns.PostRequest()
                .prepareCreateNode(primaryType,
                    resourceType,
                    templatePath,
                    extraParams, "", false)
                .setJcrPrimaryTypeParam(primaryType)
                .setSlingResTypeParam(resourceType)
                .setCqTemplatePathParam(templatePath)
                .setParams(extraParams);

            return (
                this
                    .addChild(nameHint,childNode)
                );
        }
    };

    ns.PostRequest.prototype.send = function (/*success*/) {
        log.warn([this,this.getParams()]);
        return (http.post(this.url, this.getParams()));
    };


    ns.constants = {

        TYPE_CQ_PAGE: "cq:Page",
        TYPE_CQ_PAGECONTENT: "cq:PageContent",
        TYPE_NT_UNSTRUCTURED: "nt:unstructured",

        PAGE_CONTENT: "jcr:content",
        PATH_PARSYS: "par",

        RESOURCE_PARSYS: "aemdesign/components/layout/container",

        FIELD_SLING_RESOURCE_TYPE: "sling:resourceType",
        FIELD_JCR_PRIMARY_TYPE: "jcr:primaryType",
        FIELD_CQ_TEMPLATE: "cq:template",
        FIELD_JCR_CREATED: "jcr:created",
        FIELD_JCR_CREATEDBY: "jcr:createdBy",
        FIELD_JCR_MODIFIED: "jcr:lastModified",
        FIELD_JCR_MODIFIEDBY: "jcr:lastModifiedBy",
        CHARSET_UTF8: "utf-8",

        SLING_PATH_SUFFIX_SLASHSTAR: "/*",
        SLING_PATH_SUFFIX_SLASH: "/",
        SLING_FIELD_INCLUDE_PREFIX: "./",
        SLING_PATH_SEPARATOR: "/",
        SLING_FILENAME_SEPARATOR: ".",

        /**
         * The selector for infinite hierarchy depth when retrieving
         * repository content.
         * @static
         * @final
         * @type String
         */
        SELECTOR_INFINITY: ".infinity",

        /**
         * The parameter name for the used character set.
         * @static
         * @final
         * @type String
         */
        CHARSET: "_charset_",

        /**
         * The parameter name for the status.
         * @static
         * @final
         * @type String
         */
        STATUS: ":status",

        /**
         * The parameter value for the status type "browser".
         * @static
         * @final
         * @type String
         */
        STATUS_BROWSER: "browser",

        /**
         * The parameter name for the operation.
         * @static
         * @final
         * @type String
         */
        OPERATION: ":operation",

        /**
         * The parameter value for the delete operation.
         * @static
         * @final
         * @type String
         */
        OPERATION_DELETE: "delete",

        /**
         * The parameter value for the move operation.
         * @static
         * @final
         * @type String
         */
        OPERATION_MOVE: "move",

        /**
         * The parameter name suffix for deleting.
         * @static
         * @final
         * @type String
         */
        DELETE_SUFFIX: "@Delete",

        /**
         * The parameter name suffix for setting a type hint.
         * @static
         * @final
         * @type String
         */
        TYPEHINT_SUFFIX: "@TypeHint",

        /**
         * The parameter name suffix for copying.
         * @static
         * @final
         * @type String
         */
        COPY_SUFFIX: "@CopyFrom",

        /**
         * The parameter name suffix for moving.
         * @static
         * @final
         * @type String
         */
        MOVE_SUFFIX: "@MoveFrom",

        /**
         * The parameter name for the ordering.
         * @static
         * @final
         * @type String
         */
        ORDER: ":order",

        /**
         * The parameter name for the replace flag.
         * @static
         * @final
         * @type String
         */
        REPLACE: ":replace",

        /**
         * The parameter name for the destination flag.
         * @static
         * @final
         * @type String
         */
        DESTINATION: ":dest",

        /**
         * The parameter name for the save parameter prefix.
         * @static
         * @final
         * @type String
         */
        SAVE_PARAM_PREFIX: ":saveParamPrefix",

        /**
         * The parameter name for input fields that should
         * be ignored by Sling.
         * @static
         * @final
         * @type String
         */
        IGNORE_PARAM: ":ignore",

        /**
         * The parameter name for login requests.
         * @static
         * @final
         * @type String
         */
        REQUEST_LOGIN_PARAM: "sling:authRequestLogin",

        /**
         * Login URL
         * @static
         * @final
         * @type String
         */
        LOGIN_URL: "/system/sling/login.html",

        /**
         * Logout URL
         * @static
         * @final
         * @type String
         */
        LOGOUT_URL: "/system/sling/logout.html",

        PARAGRAPH_ORDER: {
            after: "after",
            before: "before",
            last: "last"
        }
    };

})(AEMDESIGN.jQuery, AEMDESIGN.sling, AEMDESIGN.http, AEMDESIGN.log, this);

