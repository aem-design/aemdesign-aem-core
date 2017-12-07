//list functions - author
window.AEMDESIGN = window.AEMDESIGN || {"jQuery":{}};
window.AEMDESIGN.author = AEMDESIGN.author || {};
window.AEMDESIGN.author.components = AEMDESIGN.author.components || {};
window.AEMDESIGN.author.components.list = AEMDESIGN.author.components.list || {};


(function ($, ns, nsDialog, CQ, Granite, window, undefined) { //add additional dependencies

    "use strict";
    var _version = "0.1";

    ns.version = function () {
        return _version;
    };

    ns.manageTabs = function (tabPanels, tabToSelectName, noSwitch, noHide) {
        nsDialog.manageTabs(tabPanels, tabToSelectName, noSwitch, noHide);
    };

    ns.hideTab = function (tabToHide, tabPanels) {
        nsDialog.hideTab(tabToHide, tabPanels);
    };


})(AEMDESIGN.jQuery, AEMDESIGN.author.components.list, AEMDESIGN.dialog, Granite, this); //pass in additional dependencies