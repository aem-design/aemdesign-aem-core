//aemdesign.forms.js
window.AEMDESIGN = window.AEMDESIGN || {"jQuery":{}};
window.AEMDESIGN.forms = window.AEMDESIGN.forms || {};

(function ($, ns, sling, content, log, window, undefined) { //NOSONAR namespace convention

    //"use strict";
    var _version = "0.1";

    ns.version = function () {
        return _version;
    };

    ns.submitLead = function(form) {

        var newLead = new AEMDESIGN.components.LeadDetails({
            infoInterested: form["infoInterested"],
            "infoInterested@TypeHint": "String[]",
            createdTimestamp: "",
            updatedTimestamp: ""
        });

        var newContact = new AEMDESIGN.components.ContactDetails({
            title: form["title"],
            firstName: form["firstName"],
            surname: form["surname"],
            phone: form["phoneNumber"],
            email: form["mailAddress"]
        });

        var newAddress = new AEMDESIGN.components.AddressDetails({
            line1: form["address1"],
            line2: form["address2"],
            suburb: form["suburb"],
            state: form["state"],
            postcode: form["postcode"]
        });

        var newEnquiry = new AEMDESIGN.components.EnquiryDetails({
            enquiryType: form["contactEnquiryType"],
            enquiry: form["contactEnquiry"]
        });

        var newSubscribe = new AEMDESIGN.components.SubscribeDetails({
            stayUpdated: form["isNewsSignup"],
            categories: form["categories"],
            contactMe: form["likeResponse"]
        });

        log.info(["submitLead",AEMDESIGN.services.endpoints.LEADS,form, newLead, newContact, newAddress, newEnquiry, newSubscribe]);

        content.createPage(AEMDESIGN.services.endpoints.LEADS,
            [newLead, newContact, newAddress, newEnquiry, newSubscribe], "lead", "AEMDESIGN.forms.contactus");


    };

})(AEMDESIGN.jQuery, AEMDESIGN.forms, AEMDESIGN.sling, AEMDESIGN.content, AEMDESIGN.log, this);

