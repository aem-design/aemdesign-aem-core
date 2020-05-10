//navlist - functions
window.AEMDESIGN = window.AEMDESIGN || { "jQuery": {} };
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.navlist = AEMDESIGN.components.navlist || {};

(function ($, _, ko, ns, log, window, undefined) { //NOSONAR convention for wrapping all modules

  "use strict";
  var _version = "0.1";

  ns.version = function () {
    return _version;
  };

})(AEMDESIGN.jQuery, _, ko, AEMDESIGN.components.navlist, AEMDESIGN.log, this); //pass in additional dependencies
