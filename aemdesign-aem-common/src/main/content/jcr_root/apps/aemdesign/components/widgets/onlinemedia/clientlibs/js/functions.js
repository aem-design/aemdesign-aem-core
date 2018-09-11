//onlinemedia - functions
window.AEMDESIGN = window.AEMDESIGN || {"jQuery":{}};
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.onlinemedia = AEMDESIGN.components.onlinemedia || {};

(function ($, _, ko, ns, log, window, undefined) { //add additional dependencies

    log.info("OnlineMedia Start !!!");

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
            log.info("Loaded Issuu Viewer ");
        }
    };

    ns.loadOnlineMedia = function(el){

        var base = $(el);

        var provider = base.data("provider");

        log.info("Binding Loaded widget.js "+  provider);

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
                    pluginPath: "/apps/settings/wcm/design/vendorlib/mediaelementplayer/",
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

})(AEMDESIGN.jQuery,_,ko, AEMDESIGN.components.onlinemedia, AEMDESIGN.log, this); //pass in additional dependencies

