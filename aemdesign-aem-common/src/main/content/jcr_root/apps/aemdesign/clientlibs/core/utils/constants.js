//aemdesign.constants.js
window.AEMDESIGN = window.AEMDESIGN || {"jQuery":{}};
window.AEMDESIGN.constants = window.AEMDESIGN.constants || {};
(function ($, ns, window, undefined) { //NOSONAR namespace convention

    "use strict";
    var _version = "0.1";

    ns.version = function () {
        return _version;
    };

    ns.PATH_SEPARATOR = "/";


})(AEMDESIGN.jQuery, AEMDESIGN.constants, this);

