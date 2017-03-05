//list
window.WKCD = window.WKCD || {};
window.WKCD.components = WKCD.components || {};
window.WKCD.components.list = WKCD.components.list || {};


;(function ($, _, ko, ns, window, undefined) { //add additional dependencies

    $(document).ready(function () {
        ns.bindHistoryPopStateEvent();
    });

})(WKCD.jQuery,_,ko, WKCD.components.list, this); //pass in additional dependencies
