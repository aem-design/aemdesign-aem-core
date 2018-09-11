//aemdesign.notify.js
window.AEMDESIGN = window.AEMDESIGN || {"jQuery":{}};
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.notify = AEMDESIGN.components.notify || {};

(function ($, _, ko, ns, components, events, window, undefined) { //NOSONAR namespace convention

    ns.postcode = new ko.subscribable();

})(AEMDESIGN.jQuery, _, ko, AEMDESIGN.components.notify, AEMDESIGN.components, AEMDESIGN.events, this);
