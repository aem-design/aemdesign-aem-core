//image - behaviour
window.AEMDESIGN = window.AEMDESIGN || { "jQuery": {} };
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.image = AEMDESIGN.components.image || {};


(function ($, _, ko, ns, analytics, window, undefined) { //NOSONAR convention for wrapping all modules

  $("div.image img").on("load", function () {
    analytics.imageLoad($(this));
  });


  $(window).on("load resize", function () {
    //depreciated using picturefill.js
    // $("div[data-picture]").each(function () {
    //     var picture = $(this);
    //     ns.picturefill(picture);
    // });
  });

  $(document).ready(function () {
    $("div.image[data-analytics-type=aa]").each(function () {
      var link = $(this).find("a");
      if (link) {
        analytics.enableAA(link);
      }
    });
    $("div.image[data-analytics-type=ga]").each(function () {
      var link = $(this).find("a");
      if (link) {
        analytics.enableGA(link);
      }
    });
  });

})(AEMDESIGN.jQuery, _, ko, AEMDESIGN.components.image, AEMDESIGN.analytics, this); //pass in additional dependencies
