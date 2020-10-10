//listnav - behaviour
window.AEMDESIGN = window.AEMDESIGN || { "jQuery": {} };
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.listnav = AEMDESIGN.components.listnav || {};

(function ($, _, ko, ns, log, window, undefined) { //NOSONAR convention for wrapping all modules

  $(document).ready(function () {


    // $(".actions .share > a").click(function(a) {
    //         a.preventDefault();
    //         var b = $(this)
    //             , c = $(".actions .addthis_toolbox");
    //         b.hasClass("focus") ? (c.stop(!0, !0).fadeOut({
    //             duration: "fast",
    //             queue: !1
    //         }).slideUp("fast"),
    //             b.removeClass("focus").blur()) : (c.stop(!0, !0).fadeIn({
    //             duration: "fast",
    //             queue: !1
    //         }).css("display", "none").slideDown("fast"),
    //             b.addClass("focus").focus())
    //     }
    // )


  });

})(AEMDESIGN.jQuery, _, ko, AEMDESIGN.components.listnav, AEMDESIGN.log, this); //pass in additional dependencies
