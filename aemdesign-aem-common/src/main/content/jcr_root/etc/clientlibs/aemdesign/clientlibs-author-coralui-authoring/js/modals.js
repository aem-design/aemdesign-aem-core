//clientlibs-author-coralui-authoring - modals
(function ($) {
    'use strict';

    // make sure modals are displayed in the middle of the page when in preview mode
    $('.modal').on('show.bs.modal', function () {
        var viewportHeight = parent.innerHeight;
        $('.modal-dialog').css('top', viewportHeight * 0.5);
    });

    // console.log("clientlibs-author-coralui-authoring - modals");

}(jQuery));
