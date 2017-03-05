//sitenav - behaviour
window.AEMDESIGN = window.AEMDESIGN || {};
window.AEMDESIGN.jQuery = window.jQuery || {};
window.AEMDESIGN.$ = window.jQuery || $;
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.sitenav = AEMDESIGN.components.sitenav || {};

;(function ($, _, ko, ns, window, undefined) { //add additional dependencies

    $(document).ready(function () {

        ns.setSectionColorStylesheet(this);

    });

})(AEMDESIGN.jQuery,_,ko, AEMDESIGN.components.sitenav, this); //pass in additional dependencies