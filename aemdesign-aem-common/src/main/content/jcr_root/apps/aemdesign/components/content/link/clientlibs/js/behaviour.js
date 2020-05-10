//link - behaviour
window.AEMDESIGN = window.AEMDESIGN || { "jQuery": {} };
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.link = AEMDESIGN.components.link || {};


(function ($, _, ko, ns, analytics, window, undefined) { //NOSONAR convention for wrapping all modules


  $(document).ready(function () {
    $("a[data-analytics-type=aa]").each(function () {
      var link = $(this);
      analytics.enableAA(link);
    });
    $("a[data-analytics-type=ga]").each(function () {
      var link = $(this);
      analytics.enableGA(link);
    });
    $("button.link[href]").each(function () {
      var button = $(this);
      analytics.enableButton(button);
    });
  });


})(AEMDESIGN.jQuery, _, ko, AEMDESIGN.components.link, AEMDESIGN.analytics, this); //pass in additional dependencies
