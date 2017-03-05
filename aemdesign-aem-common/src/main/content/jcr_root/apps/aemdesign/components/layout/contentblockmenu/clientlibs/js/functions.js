//contentblock
window.WKCD = window.WKCD || {};
window.WKCD.components = WKCD.components || {};
window.WKCD.components.contentblockMenu = WKCD.components.contentblockMenu || {};

;(function ($, ns, window, undefined) {

    "use strict";
    var _version = "0.1";

    ns.version = function () {
        return _version;
    };


    ns.setActiveItem = function ($section, sectionOffset, $item) {

        var headerHeight = $('#header').height() + $('#title-header').height();
        var $fixedNav = $('.landing-fixed-nav').find('ul');
        var activeClass = "current-section";

        var scrollTop = $(window).scrollTop() + headerHeight;

        if (scrollTop >= sectionOffset) {
            $fixedNav.find('li').removeClass(activeClass);
            $item.addClass(activeClass);
        }


    }

    /*
        function setActiveItem($section, sectionOffset, $item) {
            var scrollTop = $(window).scrollTop() + headerHeight;

            if (scrollTop >= sectionOffset) {
                $fixedNav.find('li').removeClass(activeClass);
                $item.addClass(activeClass);
            }
        }
    */


    ns.smoothAnchorScroll = function (target, offset) {
        offset = offset || { top: 0 };

        $('html, body').animate({
            scrollTop: offset.top
        }, {
            duration: 700,
            complete: function() {
                if(target !== '#') {
                    window.location.hash = target;
                }
            }
        });

    }
/*
    function smoothAnchorScroll(target, offset) {
        offset = offset || { top: 0 };

        $('html, body').animate({
            scrollTop: offset.top
        }, {
            duration: 700,
            complete: function() {
                if(target !== '#') {
                    window.location.hash = target;
                }
            }
        });
    }
    */



})(jQuery, WKCD.components.contentblockMenu, this);

