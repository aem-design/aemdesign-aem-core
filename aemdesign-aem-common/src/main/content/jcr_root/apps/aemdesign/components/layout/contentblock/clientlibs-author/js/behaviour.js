//contentblock - behaviour
window.AEMDESIGN = window.AEMDESIGN || {};
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.authoring = AEMDESIGN.components.authoring || {};
window.AEMDESIGN.components.authoring.contentblock = AEMDESIGN.components.authoring.contentblock || {};


(function ($, Coral, ns, window, undefined) { //NOSONAR convention for wrapping all modules

    var pathSuffix = "bgimage";
    var fieldNameKey = "assetMediaQuery";
    var fieldNameValue = "assetMediaQueryRendition";
    var fieldNameKeyPath = "./" + pathSuffix + "/" + fieldNameKey;
    var fieldNameValuePath = "./" + pathSuffix + "/" + fieldNameValue;
    var fieldNameKeySelector = '[name="'+fieldNameKeyPath+'"]';
    var fieldNameValueSelector = '[name="'+fieldNameValuePath+'"]';
    var fieldNameAsset = "./"+pathSuffix+"/file";
    var fieldNameAssetSelector = 'coral-fileupload[name="'+fieldNameAsset+'"]';
    var renditionListSuffix = "/jcr:content/renditions.tidy.1.json";

    var componentName = "contentblock";
    var multiFieldSelector = ".asset-mediaqueryrendition";

    var fieldNameValueHasEmptyItem = false;

    console.log(["dialog-"+componentName+"-behaviour","loading"]);
    $(document).on('dialog-ready', function () {
        //console.log(["dialog-"+componentName+"-behaviour","loading"]);

        var $dialog = $("#dialog-"+componentName);
        var coralMultified = $dialog.find(multiFieldSelector)[0];

        if ($dialog[0]) {

            //console.log(["dialog-"+componentName+"-behaviour","loading"]);


            if (coralMultified) {

                //when clear is pressed
                $dialog.find(fieldNameAssetSelector).find("[coral-fileupload-clear]").on("click", function (event) {
                    console.log(["dialog-"+componentName+"-behaviour","clear multi-field"]);
                    coralMultified.items.clear();
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
                    console.log(["dialog-" + componentName + "-behaviour", "loading existing asset", assetPath]);
                    //load rendition names into template
                    ns.loadAssetRenditionNamesIntoSelect(assetPath, renditionListSuffix, coralTemplateFieldValue, fieldNameValueHasEmptyItem);
                    //use new template
                    coralMultified.template = templateBlank;
                } else {
                    console.log(["dialog-" + componentName + "-behaviour", "no asset selected", assetPath]);
                }

                //when new asset is added
                $dialog.find(fieldNameAssetSelector).on('change', function (event) {
                    console.log(["dialog-"+componentName+"-behaviour","new asset"]);

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
                //console.log(['$form', $form]);
                var formUrl = $form[0].action;

                var formFieldValuesUrl = formUrl + ".1.json";

                console.log(["dialog-"+componentName+"-behaviour","loading existing elements", formFieldValuesUrl]);

                $.getJSON(formFieldValuesUrl).done(function (data) {

                    console.log(["dialog-" + componentName + "-behaviour", "loading existing elements"]);

                    if (data[pathSuffix]) {

                        var
                            fieldconfigKey = data[pathSuffix][fieldNameKey],
                            fieldconfigValue = data[pathSuffix][fieldNameValue];


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

                    }

                });

            } else {
                console.log(["dialog-"+componentName+"-behaviour","field: " + multiFieldSelector,"not found"]);
            }


        } else {
            console.log(["dialog-"+componentName+"-behaviour","dialog: " + "#dialog-" + componentName,"not found"]);
        }
    });


})($, Coral, AEMDESIGN.components.authoring.contentblock,  this); //pass in additional dependencies
