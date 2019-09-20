//genericdetails - behaviour
window.AEMDESIGN = window.AEMDESIGN || {};
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.authoring = AEMDESIGN.components.authoring || {};
window.AEMDESIGN.components.authoring.genericdetails = AEMDESIGN.components.authoring.genericdetails || {};


(function ($, $document, Coral, ns, window, undefined) { //NOSONAR convention for wrapping all modules

    console.log(["dialog-genericdetails-behaviour","ready"]);
    $(document).on('dialog-ready', function () {
        console.log(["dialog-genericdetails-behaviour","checking"]);
        if ($('#dialog-genericdetails')[0]) {

            console.log(["dialog-genericdetails-behaviour","loading"]);

            // $document.on('change', '.pageMetaProperty, .pageMetaPropertyContent', function (event) {
            //
            //     var element = event.target,
            //         parentElement = element.closest('.dialog-genericdetails-costomfields'),
            //         childpageMetaPropertyElement = parentElement.querySelector('.pageMetaProperty'),
            //         childpageMetaPropertyContentElement = parentElement.querySelector('.pageMetaPropertyContent')
            //
            // });

            //load content into field
            if ($('#dialog-genericdetails').find('.genericdetails-costomfields')[0]) {

                var $form = $("#dialog-genericdetails").closest('form');
                console.log(['$form', $form]);
                var formUrl = $form[0].action;
                $.getJSON(formUrl + '.json').done(function (data) {
                    console.log(data);
                    var
                        fieldconfigKey = data.pageMetaProperty,
                        fieldconfigValue = data.pageMetaPropertyContent;


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
                            var curr = $('#dialog-genericdetails').find('.genericdetails-costomfields')[0].items.add();
                            Coral.commons.ready(curr, function (readyCurr) {
                                $(readyCurr).find('[name="./pageMetaProperty"]')[0].value = fieldconfigKey[index];
                                $(readyCurr).find('[name="./pageMetaPropertyContent"]')[0].value = fieldconfigValue[index];

                            });
                        });
                    }
                    else {
                        //$('.genericdetails-costomfields')[0].items.add();
                    }


                });

            } else {
                console.log(["dialog-genericdetails-behaviour","genericdetails-costomfields","not found"]);
            }


        }
    });


})($, $(document), Coral, AEMDESIGN.components.authoring.genericdetails,  this); //pass in additional dependencies
