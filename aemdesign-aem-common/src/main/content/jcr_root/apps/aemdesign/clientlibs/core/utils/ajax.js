//aemdesign.ajax.js - Namespace

(function ($, ns, http, window, undefined) { //NOSONAR module conventions

    "use strict";
    var _version = "0.1";

    ns.version = function () {
        return _version;
    };

    $.ajaxSetup({ // necessary global modifications for ajax calls
        statusCode: {
            403: function(jqXHR) {
                if (jqXHR.getResponseHeader("X-Reason") === "Authentication Failed") {
                    // login session expired: redirect to login page
                    AEMDESIGN.http.handleLoginRedirect(); //access directly as this is first class that runs
                }
            }
        }
    });

    $.ajaxSettings.traditional = true;


})(AEMDESIGN.jQuery, AEMDESIGN, AEMDESIGN.http, this);
