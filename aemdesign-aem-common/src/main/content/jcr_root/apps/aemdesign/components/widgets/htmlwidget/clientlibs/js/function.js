//html - function
window.WKCD = window.WKCD || {};
window.WKCD.jQuery = window.jQuery || {};
window.WKCD.$ = window.jQuery || $;
window.WKCD.components = WKCD.components || {};
window.WKCD.components.htmlWidget = WKCD.components.htmlWidget || {};

;(function ($, _, ko, ns, log, window, undefined) { //add additional dependencies

    log.log("Html Start !!!");

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

})(WKCD.jQuery,_,ko, WKCD.components.htmlWidget, WKCD.log, this); //pass in additional dependencies

