//aem.design.components.coral.widgets.form.assetoptions - behaviour
window.AEMDESIGN = window.AEMDESIGN || {};
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.authoring = AEMDESIGN.components.authoring || {};
window.AEMDESIGN.components.authoring.assetoptions = AEMDESIGN.components.authoring.assetoptions || {};


(function ($, Coral, ns, window, undefined) { //NOSONAR convention for wrapping all modules

    ns.debug(["behaviour","loading"]);
    $(document).on('dialog-ready', function (event) {

        var multiFieldSelector = ".asset-mediaqueryrendition";

        var $dialog = $(multiFieldSelector).closest(".cq-dialog-content");

        ns.debug(["behaviour","processing dialog", $dialog]);

        if ($dialog[0]) {
            ns.debug(["behaviour","processing dialog", $dialog, $dialog[0]]);

            var $coralMultified = $dialog.find(multiFieldSelector);
            var coralMultified = $dialog.find(multiFieldSelector)[0];

            if (coralMultified) {
                ns.debug(["behaviour","processing multi-field", coralMultified,$coralMultified]);

                var componentAssetNodePath = "componentAssetNodePath".toLowerCase();
                var componentAssetName = "componentAssetName".toLowerCase();

                var pathPrefix = $coralMultified.data(componentAssetNodePath);
                var fieldNameAsset = $coralMultified.data(componentAssetName);

                ns.debug(["behaviour","loading",componentAssetNodePath,pathPrefix,componentAssetName,fieldNameAsset]);

                if (fieldNameAsset === "" || pathPrefix === "") {
                    ns.debug(["behaviour","error","please setup following attributes for mutlifield:",componentAssetNodePath,componentAssetName]);
                    return;
                }

                var fieldNameKey = "assetMediaQuery";
                var fieldNameValue = "assetMediaQueryRendition";
                var fieldNameKeyPath = pathPrefix + fieldNameKey;
                var fieldNameValuePath = pathPrefix + fieldNameValue;
                var fieldNameKeySelector = '[name="'+fieldNameKeyPath+'"]';
                var fieldNameValueSelector = '[name="'+fieldNameValuePath+'"]';
                var fieldNameAssetSelector = 'coral-fileupload[name="'+fieldNameAsset+'"]';
                var renditionListSuffix = "/jcr:content/renditions.tidy.1.json";


                var fieldNameValueHasEmptyItem = false;

                ns.debug(["behaviour","config",fieldNameKeyPath,fieldNameValuePath,fieldNameKeySelector,fieldNameValueSelector,fieldNameAssetSelector]);

                //when clear is pressed
                $dialog.find(fieldNameAssetSelector).find("[coral-fileupload-clear]").on("click", function (event) {
                    ns.debug(["behaviour","clear multi-field"]);
                    ns.clearItems(coralMultified);
                    ns.clearItems(coralTemplateFieldValue);
                    //use new template
                    coralMultified.template = templateBlank;
                });


                //check and remember if select field has an empty option
                var templateEmptyOption = $(coralMultified.template.content).find(fieldNameValueSelector + " coral-select-item[value]");
                if (templateEmptyOption.length > 0) {
                    fieldNameValueHasEmptyItem = true;
                }

                //create new select multified template from cpoy of existing template
                var templateBlank = document.createElement("template");
                templateBlank.setAttribute('coral-multifield-template', '');
                templateBlank.content.append(document.importNode(coralMultified.template.content,true));
                var coralTemplateFieldValue = $(templateBlank.content).find(fieldNameValueSelector)[0];

                //get current asset path
                var assetPath = $dialog.find(fieldNameAssetSelector).find("[data-cq-fileupload-filereference]").attr("data-cq-fileupload-filereference");
                if (assetPath) {
                    ns.debug(["behaviour","loading existing asset", assetPath]);
                    //load rendition names into template
                    ns.loadAssetRenditionNamesIntoSelect(assetPath, renditionListSuffix, coralTemplateFieldValue, fieldNameValueHasEmptyItem);
                    //use new template
                    coralMultified.template = templateBlank;
                } else {
                    ns.debug(["behaviour","no asset selected", assetPath]);
                }

                //when new asset is added
                $dialog.find(fieldNameAssetSelector).on('change', function (event) {
                    ns.debug(["behaviour","new asset"]);

                    coralMultified.items.clear();

                    var $element = $(this);
                    assetPath = $element.find("input[type='hidden'][data-cq-fileupload-parameter='filereference']").val();

                    //update template with list of asset renditions
                    ns.loadAssetRenditionNamesIntoSelect(assetPath,renditionListSuffix,coralTemplateFieldValue,fieldNameValueHasEmptyItem);

                    //use new template
                    coralMultified.template = templateBlank;


                });

                //load multi-field content into multi-field rows
                var $form = $dialog.closest('form');
                var formUrl = $form[0].action;

                var formFieldValuesUrl = formUrl + ".1.json";

                ns.debug(["behaviour","loading existing elements", formFieldValuesUrl]);

                $.getJSON(formFieldValuesUrl).done(function (data) {

                    ns.debug(["behaviour","loading existing elements"]);

                    var fieldconfigKey;
                    var fieldconfigValue;

                    if (pathPrefix !== "" && data[pathPrefix]) {
                        fieldconfigKey = data[pathPrefix][fieldNameKey];
                        fieldconfigValue = data[pathPrefix][fieldNameValue];
                    } else {
                        fieldconfigKey = data[fieldNameKey];
                        fieldconfigValue = data[fieldNameValue];
                    }

                    if (fieldconfigKey) {
                        //turn plain string into array
                        if (typeof fieldconfigKey === 'string') {
                            fieldconfigKey = [fieldconfigKey];
                        }
                        //turn plain string into array
                        if (fieldconfigValue && (typeof fieldconfigValue === 'string')) {
                            fieldconfigValue = [fieldconfigValue];
                        }


                        fieldconfigKey.forEach(function (key, index) {
                            var curr = coralMultified.items.add();
                            Coral.commons.ready(curr, function (readyCurr) {
                                $(readyCurr).find(fieldNameKeySelector)[0].value = fieldconfigKey[index];
                                $(readyCurr).find(fieldNameValueSelector)[0].value = fieldconfigValue[index];

                            });
                        });
                    } else {
                        //coralMultified.items.add();
                    }



                });

            } else {
                ns.debug(["behaviour","field: " + multiFieldSelector,"not found"]);
            }


        } else {
            ns.debug(["behaviour","no dialog found"]);
        }
    });


})($, Coral, AEMDESIGN.components.authoring.assetoptions,  this); //pass in additional dependencies
