//contentblock - functions
window.AEMDESIGN = window.AEMDESIGN || { "jQuery": {} };
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.contentblockmenu = AEMDESIGN.components.contentblockmenu || {};

(function ($, ns, window, undefined) { //NOSONAR convention for wrapping all modules

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


  };

  ns.smoothAnchorScroll = function (target, offset) {
    offset = offset || { top: 0 };

    $('html, body').animate({
      scrollTop: offset.top
    }, {
      duration: 700,
      complete: function () {
        if (target !== '#') {
          window.location.hash = target;
        }
      }
    });

  };


})(AEMDESIGN.jQuery, AEMDESIGN.components.contentblockmenu, this);

