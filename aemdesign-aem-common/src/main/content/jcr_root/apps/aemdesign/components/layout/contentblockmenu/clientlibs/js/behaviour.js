//contentblockmenu - behaviour
window.AEMDESIGN = window.AEMDESIGN || {"jQuery":{}};
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.contentblockmenu = AEMDESIGN.components.contentblockmenu || {};

(function ($, _, ko, utils, log, ns, window, undefined) { //add additional dependencies


    $(document).ready(function () {
        // log.info("loading contentblockjs");
        //
        //
        // $('.section-panel').scrollClass({
        //     delay: 30,
        //     threshold: 50
        // });
        //
        //
        // var $fixedNav = $('.landing-fixed-nav').find('ul');
        // var headerHeight = $('#header').height() + $('#title-header').height();
        // var activeClass = "current-section";
        //
        // function smoothAnchorScroll(target, offset) {
        //     offset = offset || { top: 0 };
        //
        //     $('html, body').animate({
        //         scrollTop: offset.top
        //     }, {
        //         duration: 700,
        //         complete: function() {
        //             if(target !== '#') {
        //                 window.location.hash = target;
        //             }
        //         }
        //     });
        // }
        //
        // $('[data-fixed-menu-title]').each(function (i) {
        //     var index = i + 1;
        //     var target = '#' + $(this).attr('id');
        //     var title = $(this).data('fixed-menu-title');
        //     var $item = $('<li />').append(
        //         $('<a/>')
        //             .attr('href', target)
        //             .text(title)
        //             .prepend($('<span/>').text(index))
        //     );
        //     var offset = $(this).offset();
        //
        //     $fixedNav.append($item);
        //
        //     $item.find('a').on('click', function (e) {
        //         e.preventDefault();
        //
        //         smoothAnchorScroll(target, offset);
        //     });
        //
        //     $(window).on('scroll', _.debounce(function () {
        //         contentblockMenu.setActiveItem($(this), offset.top, $item);
        //     }, 250));
        // });
        //
        // $('.fixed-top-link').on('click', function (e) {
        //     e.preventDefault();
        //     smoothAnchorScroll($(this).attr('href'));
        // });
        //
        // $(window).trigger('scroll');
    });


})(AEMDESIGN.jQuery, _, ko, AEMDESIGN.utils, AEMDESIGN.log, AEMDESIGN.components.contentblockmenu, this); //pass in additional dependencies

