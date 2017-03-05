//html - function
window.AEMDESIGN = window.AEMDESIGN || {};
window.AEMDESIGN.jQuery = window.jQuery || {};
window.AEMDESIGN.$ = window.jQuery || $;
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.htmlWidget = AEMDESIGN.components.htmlWidget || {};

;(function ($, _, ko, ns, log, window, undefined) { //add additional dependencies

    log.info("Html Start !!!");

    "use strict";
    var _version = "0.1";

    ns.version = function () {
        return _version;
    };

    ns.validate = function(htmleditor){
        var html = htmleditor.el.dom.value;
        html = html.replace(/&nbsp;/g, ' ');
        html = html.replace(/\s+/g, ' ');
        html = html.replace(/ = /g, '=');
        html = $('<div/>').html(html).text();
        var parser = new DOMParser();
        var doc = parser.parseFromString(html, 'text/html');
        return $(doc).find('body').html().toLowerCase() == html.replace(/'/g, '"').trim().toLowerCase().replace(/<br\/>/g, '<br>')
    }

    // log.disableLog();

})(AEMDESIGN.jQuery,_,ko, AEMDESIGN.components.htmlWidget, AEMDESIGN.log, this); //pass in additional dependencies

