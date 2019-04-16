//aem.design.components.coral.widgets.form.assetoptions - functions
window.AEMDESIGN = window.AEMDESIGN || {};
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.authoring = AEMDESIGN.components.authoring || {};
window.AEMDESIGN.components.authoring.assetoptions = AEMDESIGN.components.authoring.assetoptions || {};

(function ($, $document, Coral, ns, window, undefined) { //NOSONAR convention for wrapping all modules


    "use strict";
    var _version = "0.1";

    ns.version = function () {
        return _version;
    };

    ns.name = "aem.design.components.coral.widgets.form.assetoptions";

    ns.authorUrl = '';
    ns.publishUrl = '';


    ns.debug = function(itemsArray) {
        console.log([ns.name].concat(itemsArray));
    };

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

            ns.debug(["functions","getExternalizeUrl",ns.authorUrl,ns.publishUrl]);

        }).fail(function (jqXHR, textStatus, errorThrown) {
            ns.debug(["functions",'error',jqXHR, textStatus, errorThrown]);
        });
    };

    ns.processMultifieldItem = function(multifieldClass) {
        var multifieldItems = $(multifieldClass + " coral-multifield-item");
        for (var i = 0; i < multifieldItems.length; i++) {
            Coral.commons.ready(multifieldItems[i], function (currentMultifieldItem) {
                hideOrderButton(currentMultifieldItem);
            });
        }
    };

    ns.hideOrderButton = function(currentMultifieldItem) {
        var buttonsOnMultifieldItem = $(currentMultifieldItem).find("button");
        if (buttonsOnMultifieldItem[2]) {
            buttonsOnMultifieldItem[2].hide();
        }
    };

    ns.hideDelAndOrderButtons = function(curr) {
        var buttons = $(curr).find('button');
        if (buttons) {
            $(buttons[0]).hide();
            if (buttons.length > 1) {
                $(buttons[1]).hide();
            }
        }
    };


    ns.clearItems =function(coralField) {
        if (coralField["items"]) {
            coralField.items.clear();
        } else {
            ns.debug(["functions","clearItems","not valid coralField",coralField]);
        }
    };

    ns.hideMultifieldAddButton = function(element) {
        $(element).find('button[coral-multifield-add]')[0].hide();
    };

    ns.loadAssetRenditionNamesIntoSelect = function(assetPath,renditionListSuffix,coralTemplateSelect, addEmptyItem) {
        //update template with list of asset renditions

        $.ajax({
            dataType: "json",
            url: assetPath + renditionListSuffix,
            async: false,
            success: function(data) {

                ns.debug(["functions","loadAssetRenditionNamesIntoSelect",assetPath,renditionListSuffix,coralTemplateSelect, addEmptyItem]);
                coralTemplateSelect.items.clear();

                if (addEmptyItem) {
                    coralTemplateSelect.items.add({value: "", content: {textContent: ""}});
                }

                for (var a in data) {
                    if (typeof data[a] === 'object') {
                        coralTemplateSelect.items.add({value: a, content: { textContent: a}});
                    }
                }

            }
        });

    }

    ns.getExternalizeUrl();
    ns.debug(["functions","loaded"]);


})($, $(document), Coral, AEMDESIGN.components.authoring.assetoptions, this); //pass in additional dependencies
