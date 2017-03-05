//onlinemedia - function
window.WKCD = window.WKCD || {};
window.WKCD.jQuery = window.jQuery || {};
window.WKCD.$ = window.jQuery || $;
window.WKCD.components = WKCD.components || {};
window.WKCD.components.onlinemedia = WKCD.components.onlinemedia || {};

;(function ($, _, ko, ns, log, window, undefined) { //add additional dependencies

    log.log("OnlineMedia Start !!!");

    "use strict";
    var _version = "0.1";

    var _issuuViewerLoaded = false;

    ns.version = function () {
        return _version;
    };


    ns.addIssuuViewerLoaded = function () {
        return _issuuViewerLoaded;
    };

    ns.loadIssuuViewer = function (){
        if(ns.addIssuuViewerLoaded() == false) {

            var script_tag = document.createElement('script');
            script_tag.setAttribute("type", "text/javascript");
            script_tag.setAttribute("src", "//e.issuu.com/embed.js");
            (document.getElementsByTagName("head")[0] || document.documentElement).appendChild(script_tag);

            _issuuViewerLoaded = true;
            log.log("Loaded Issuu Viewer ");
        }
    };

    ns.loadOnlineMedia = function(el){

        var base = $(el);

        var provider = base.data("provider");

        log.log("Binding Loaded widget.js "+  provider);

        if (provider == 'issuu'){

            ns.loadIssuuViewer();

        }else {
            // set default width & height
            //there is responsive theme for this case
            //if (($(el).attr("class") == "onlinemedia") && ($(el).attr("style") == undefined)) {
                //$(el).css("width", "160")
                //$(el).css("height", "120")
            //}

            $(el).find("video").each(function () {
                $(this).mediaelementplayer({
                    pluginPath: "/etc/designs/aemdesign/vendor/mediaelementplayer/",
                    enablePluginDebug: false
                });
                $('span.mejs-offscreen').css("display", "none");
            });

            // for vimeo
            $(el).find("source[type='video/vimeo']").each(function () {
                var w = $(this).closest("div.onlinemedia").css("width");
                var h = $(this).closest("div.onlinemedia").css("height");
                $(this).parent().prev().css("width", w);
                $(this).parent().prev().css("height", h);

            });



        }
    }

    // log.disableLog();

})(WKCD.jQuery,_,ko, WKCD.components.onlinemedia, WKCD.log, this); //pass in additional dependencies

