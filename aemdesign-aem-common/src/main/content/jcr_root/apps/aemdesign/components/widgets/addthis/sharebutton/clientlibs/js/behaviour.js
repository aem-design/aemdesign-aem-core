//sharebutton - behaviour
window.AEMDESIGN = window.AEMDESIGN || { "jQuery": {} };
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.sharebutton = AEMDESIGN.components.sharebutton || {};

//add additional dependencies
(function ($, _, ko, ns, window, undefined) { //NOSONAR convention for wrapping all modules

  $(document).ready(function () {


    $("[data-modules*='sharebutton']").each(function () {

      ns.loadShareButton(this);

    });


  });

})(AEMDESIGN.jQuery, _, ko, AEMDESIGN.components.sharebutton, this); //pass in additional dependencies
