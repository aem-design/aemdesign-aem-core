//onlinemedia - behaviour
window.WKCD = window.WKCD || {};
window.WKCD.jQuery = window.jQuery || {};
window.WKCD.$ = window.jQuery || $;
window.WKCD.components = WKCD.components || {};
window.WKCD.components.onlinemedia = WKCD.components.onlinemedia || {};

;(function ($, _, ko, ns, window, undefined) { //add additional dependencies

    $(document).ready(function () {

        $("[data-modules='onlinemedia']").each(function () {

            ns.loadOnlineMedia(this);

        });

    });

})(WKCD.jQuery,_,ko, WKCD.components.onlinemedia, this); //pass in additional dependencies