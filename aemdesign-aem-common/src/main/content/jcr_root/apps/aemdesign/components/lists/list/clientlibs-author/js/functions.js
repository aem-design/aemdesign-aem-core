//list functions - author
window.WKCD = window.WKCD || {};
window.WKCD.author = WKCD.author || {};
window.WKCD.author.components = WKCD.author.components || {};
window.WKCD.author.components.list = WKCD.author.components.list || {};


;(function ($, ns, nsDialog, CQ, Granite, window, undefined) { //add additional dependencies

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


})(WKCD.jQuery, WKCD.author.components.list, WKCD.dialog, CQ, Granite, this); //pass in additional dependencies