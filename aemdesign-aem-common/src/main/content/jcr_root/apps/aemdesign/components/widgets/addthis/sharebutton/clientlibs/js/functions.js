//sharebutton - function
window.WKCD = window.WKCD || {};
window.WKCD.jQuery = window.jQuery || {};
window.WKCD.$ = window.jQuery || $;
window.WKCD.components = WKCD.components || {};
window.WKCD.components.sharebutton = WKCD.components.sharebutton || {};



;(function ($, _, ko, ns, log, window, undefined) { //add additional dependencies


    log.log("ShareButton Start !!!");

    "use strict";
    var _version = "0.1";

    ns.version = function () {
        return _version;
    };

    var _addThisLoaded = false;


    ns.addThisLoaded = function () {
        return _addThisLoaded;
    };

    ns.version = function () {
        return _version;
    };

    ns.loadAddThis = function (pubId){
        if(ns.addThisLoaded() == false) {

            var script_tag = document.createElement('script');
            script_tag.setAttribute("type", "text/javascript");
            script_tag.setAttribute("src", "//s7.addthis.com/js/300/addthis_widget.js#pubid=" + pubId);
            (document.getElementsByTagName("head")[0] || document.documentElement).appendChild(script_tag);

            _addThisLoaded = true;
            log.log("Loaded addThis widget.js " + pubId);
        }
    };



    ns.loadShareButton = function(el){

        var base = $(el);

        var pubId = base.data("pubid");

        log.log("Binding Loaded addThis widget.js "+  pubId);


        $(el).find(".share > a").bind('click', function(a){
            a.preventDefault();
            var b = $(el), c = $(el).find(".addthis_toolbox");
            b.hasClass("focus") ? (c.stop(!0, !0).fadeOut({
                duration: "fast",
                queue: !1
            }).slideUp("fast"),
                b.removeClass("focus").blur()) : (c.stop(!0, !0).fadeIn({
                duration: "fast",
                queue: !1
            }).css("display", "none").slideDown("fast"),
                b.addClass("focus").focus())

        });

        ns.loadAddThis(pubId);
    };

   // log.disableLog();

})(WKCD.jQuery,_,ko, WKCD.components.sharebutton, WKCD.log, this); //pass in additional dependencies

