//contentblock - functions
window.AEMDESIGN = window.AEMDESIGN || {};
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.authoring = AEMDESIGN.components.authoring || {};
window.AEMDESIGN.components.authoring.contentblock = AEMDESIGN.components.authoring.contentblock || {};

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

            console.log(["dialog-contentblock-functions","getExternalizeUrl",ns.authorUrl,ns.publishUrl]);

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

                console.log(["loadAssetRenditionNamesIntoSelect",assetPath,renditionListSuffix,coralTemplateSelect, addEmptyItem]);
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

        // $.getJSON(assetPath + renditionListSuffix).done(function (data) {
        //     console.log(["loadAssetRenditionNamesIntoSelect",assetPath,renditionListSuffix,coralTemplateSelect, addEmptyItem]);
        //     coralTemplateSelect.items.clear();
        //
        //     if (addEmptyItem) {
        //         coralTemplateSelect.items.add({value: "", content: {textContent: ""}});
        //     }
        //
        //     for (var a in data) {
        //         if (typeof data[a] === 'object') {
        //             coralTemplateSelect.items.add({value: a, content: { textContent: a}});
        //         }
        //     }
        //
        //
        // });
    }

    ns.getExternalizeUrl();
    console.log(["dialog-contentblock-functions","loaded"]);


})($, $(document), Coral, AEMDESIGN.components.authoring.contentblock, this); //pass in additional dependencies
