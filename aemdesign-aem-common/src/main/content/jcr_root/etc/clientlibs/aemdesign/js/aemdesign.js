//aemdesign.js - Namespace
window.AEMDESIGN = window.AEMDESIGN || {"jQuery":{}};
window.AEMDESIGN.jQuery = window.jQuery || {};
window.AEMDESIGN.$ = window.jQuery || $;

(function ($, ns, http, window, undefined) {

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
