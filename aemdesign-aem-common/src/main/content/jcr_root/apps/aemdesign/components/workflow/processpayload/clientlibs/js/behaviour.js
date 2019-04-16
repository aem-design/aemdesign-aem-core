//processpayload - behaviour
window.AEMDESIGN = window.AEMDESIGN || {};
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.workflow = AEMDESIGN.components.workflow || {};
window.AEMDESIGN.components.workflow.processpayload = AEMDESIGN.components.workflow.processpayload || {};


(function ($, $document, Coral, ns, window, undefined) { //NOSONAR convention for wrapping all modules

    console.log(["worfklow-processpayload-behaviour","ready"]);
    $(document).on('dialog-ready', function () {
        console.log(["worfklow-processpayload-behaviour","checking"]);
        if ($('#worfklow-processpayload')[0]) {

            console.log(["worfklow-processpayload-behaviour","loading"]);

            $('.workflow-templatemetadata-container').css('display', 'none');
            $('.workflow-nometadata-container').attr('hidden', true);


            $document.on('change', '.workflow-fieldconfigKey, .workflow-fieldconfigValue, .workflow-fieldconfigAction', function (event) {

                var element = event.target,
                    parentElement = element.closest('.workflow-attachementFieldSet'),
                    childfieldconfigKeyElement = parentElement.querySelector('.workflow-fieldconfigKey'),
                    childfieldconfigValueElement = parentElement.querySelector('.workflow-fieldconfigValue'),
                    childfieldconfigActionElement = parentElement.querySelector('.workflow-fieldconfigAction');

            });


            if ($('#worfklow-processpayload').find('.workflow-fieldconfigmultifield')[0]) {

                var $form = $("#worfklow-processpayload").closest('form');
                console.log(['$form', $form]);
                var formUrl = $form[0].action;
                $.getJSON(formUrl + '/metaData.json').done(function (data) {
                    console.log(data);
                    var
                        fieldconfigKey = data.fieldconfigKey,
                        fieldconfigValue = data.fieldconfigValue,
                        fieldconfigAction = data.fieldconfigAction;


                    if (fieldconfigKey) {
                        //turn plain string into array
                        if (typeof fieldconfigKey === 'string') {
                            fieldconfigKey = [fieldconfigKey];
                        }
                        //turn plain string into array
                        if (fieldconfigValue && (typeof fieldconfigValue === 'string')) {
                            fieldconfigValue = [fieldconfigValue];
                        }
                        if (fieldconfigAction && (typeof fieldconfigAction === 'string')) {
                            fieldconfigAction = [fieldconfigAction];
                        }


                        fieldconfigKey.forEach(function (key, index) {
                            var curr = $('#worfklow-processpayload').find('.workflow-fieldconfigmultifield')[0].items.add();
                            Coral.commons.ready(curr, function (readyCurr) {
                                $(readyCurr).find('[name="./metaData/fieldconfigKey"]')[0].value = fieldconfigKey[index];
                                $(readyCurr).find('[name="./metaData/fieldconfigValue"]')[0].value = fieldconfigValue[index];
                                $(readyCurr).find('[name="./metaData/fieldconfigAction"]')[0].value = fieldconfigAction[index];

                            });
                        });
                    }
                    else {
                        //$('.workflow-fieldconfigmultifield')[0].items.add();
                    }


                });

            } else {
                console.log(["worfklow-processpayload-behaviour","workflow-fieldconfigmultifield","not found"]);
            }


        }
    });


})($, $(document), Coral, AEMDESIGN.components.workflow.processpayload,  this); //pass in additional dependencies
