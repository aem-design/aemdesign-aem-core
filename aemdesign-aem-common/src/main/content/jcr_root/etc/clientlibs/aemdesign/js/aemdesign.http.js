//aemdesign.http.js
window.AEMDESIGN = window.AEMDESIGN || {};
window.AEMDESIGN.http = window.AEMDESIGN.http || {};
(function ($, ns, log, window, undefined) {

    "use strict";
    var _version = "0.1";

    ns.version = function () {
        return _version;
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
//                LOG.log({
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

})(jQuery, AEMDESIGN.http, AEMDESIGN.log, this);
