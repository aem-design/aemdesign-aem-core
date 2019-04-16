//aemdesign.model.postcode
window.AEMDESIGN = window.AEMDESIGN || {"jQuery":{}};
window.AEMDESIGN.model = AEMDESIGN.model || {};
window.AEMDESIGN.model.postcode = AEMDESIGN.model.postcode || {};

(function ($, ko, notify, ns, window, undefined) { //NOSONAR namespace convention

    "use strict";
    var _version = "0.1";

    ns.version = function () {
        return _version;
    };

    ns.PostcodeModel = function () {
        var _self = this;

        // Set postcode - retrieve from cookie or empty
        _self.postcode = ko.observable($.cookie('userPostcode') || '');

        // Flag for whether valid postcode value is set
        _self.postcodeSet = ko.computed(function () {
            var validPostcode = /^(0[289][0-9]{2})|([1345689][0-9]{3})|(2[0-8][0-9]{2})|(290[0-9])|(291[0-4])|(7[0-4][0-9]{2})|(7[8-9][0-9]{2})$/; //NOSONAR multiline regex is more complex

            if (_self.postcode().length === 4) {
                if (_self.postcode().match(validPostcode)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }, _self);

        _self.postcode.subscribe(function(value) {
            notify.postcode.notifySubscribers(value);
        });

        notify.postcode.subscribe(function(value) {
            _self.postcode(value);
        }, _self);

        // Retrieve price from service based on query once valid postcode is set
        _self.postcodeSet.subscribe(function (value) {
            if (value) {
                $.cookie('userPostcode', _self.postcode(), {
                    path: '/'
                });
            }
        }, _self);
    };


})(AEMDESIGN.jQuery, ko, AEMDESIGN.components.notify, AEMDESIGN.model.postcode, this);

