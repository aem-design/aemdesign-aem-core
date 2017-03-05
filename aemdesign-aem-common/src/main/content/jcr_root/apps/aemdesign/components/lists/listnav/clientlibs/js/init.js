/**
 * Initializes the components on the page
 */
(function($, undefined) {

    $(document).ready(function() {

        (function() {

            $(".actions .share > a").click(function(a) {
                    a.preventDefault();
                    var b = $(this)
                        , c = $(".actions .addthis_toolbox");
                    b.hasClass("focus") ? (c.stop(!0, !0).fadeOut({
                        duration: "fast",
                        queue: !1
                    }).slideUp("fast"),
                        b.removeClass("focus").blur()) : (c.stop(!0, !0).fadeIn({
                        duration: "fast",
                        queue: !1
                    }).css("display", "none").slideDown("fast"),
                        b.addClass("focus").focus())
                }
            )

        })();

    });

})(WKCD.jQuery);
