//sitenav - behaviour
window.WKCD = window.WKCD || {};
window.WKCD.jQuery = window.jQuery || {};
window.WKCD.$ = window.jQuery || $;
window.WKCD.components = WKCD.components || {};
window.WKCD.components.sitenav = WKCD.components.sitenav || {};

;(function ($, _, ko, ns, window, undefined) { //add additional dependencies

    $(document).ready(function () {

        ns.setSectionColorStylesheet(this);

    });

})(WKCD.jQuery,_,ko, WKCD.components.sitenav, this); //pass in additional dependencies