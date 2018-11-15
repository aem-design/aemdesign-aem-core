//processpayload - functions
window.AEMDESIGN = window.AEMDESIGN || {};
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.workflow = AEMDESIGN.components.workflow || {};
window.AEMDESIGN.components.workflow.processpayload = AEMDESIGN.components.workflow.processpayload || {};

(function ($, $document, Coral, ns, window, undefined) { //NOSONAR convention for wrapping all modules


    "use strict";
    var _version = "0.1";

    ns.version = function () {
        return _version;
    };


    ns.authorUrl = '';
    ns.publishUrl = '';

    ns.getExternalizeUrl = function() {
        $.ajax({
            type: 'GET',
            url: Granite.HTTP.externalize
            ('/libs/fd/workflow/components/servlets/fetchexternalizerurl.json'),
            cache: false,
        }).done(function (data, textStatus, jqXHR) {
            if (data && data.authorUrl) {
                ns.authorUrl = data.authorUrl;
            }
            if (data && data.publishUrl) {
                ns.publishUrl = data.publishUrl;
            }

            console.log(["worfklow-processpayload-functions","getExternalizeUrl",ns.authorUrl,ns.publishUrl]);

        }).fail(function (jqXHR, textStatus, errorThrown) {
            console.log('error');
        });
    };

    ns.processMultifieldItem = function(multifieldClass) {
        var multifieldItems = $(multifieldClass + " coral-multifield-item");
        for (var i = 0; i < multifieldItems.length; i++) {
            Coral.commons.ready(multifieldItems[i], function (currentMultifieldItem) {
                hideOrderButton(currentMultifieldItem);
            });
        }
    }

    ns.hideOrderButton = function(currentMultifieldItem) {
        var buttonsOnMultifieldItem = $(currentMultifieldItem).find("button");
        if (buttonsOnMultifieldItem[2]) {
            buttonsOnMultifieldItem[2].hide();
        }
    }

    ns.hideDelAndOrderButtons = function(curr) {
        var buttons = $(curr).find('button');
        if (buttons) {
            $(buttons[0]).hide();
            if (buttons.length > 1) {
                $(buttons[1]).hide();
            }
        }
    };

    ns.hideMultifieldAddButton = function(element) {
        $(element).find('button[coral-multifield-add]')[0].hide();
    };

    ns.getExternalizeUrl();
    console.log(["worfklow-processpayload-functions","loaded"]);


})($, $(document), Coral, AEMDESIGN.components.workflow.processpayload, this); //pass in additional dependencies
