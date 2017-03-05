//sharebutton - behaviour
window.WKCD = window.WKCD || {};
window.WKCD.jQuery = window.jQuery || {};
window.WKCD.$ = window.jQuery || $;
window.WKCD.components = WKCD.components || {};
window.WKCD.components.sharebutton = WKCD.components.sharebutton || {};

;(function ($, _, ko, ns, window, undefined) { //add additional dependencies

    $(document).ready(function () {


        $("[data-modules='sharebutton']").each(function () {

            ns.loadShareButton(this);

        });


    });

})(WKCD.jQuery,_,ko, WKCD.components.sharebutton, this); //pass in additional dependencies