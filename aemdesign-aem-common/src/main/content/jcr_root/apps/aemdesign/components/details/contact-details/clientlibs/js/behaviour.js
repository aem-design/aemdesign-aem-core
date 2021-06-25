//eventdetails - behaviour
window.AEMDESIGN = window.AEMDESIGN || {};
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.authoring = AEMDESIGN.components.authoring || {};
window.AEMDESIGN.components.authoring.contactdetails = AEMDESIGN.components.authoring.contactdetails || {};


(function ($, $document, Coral, ns, window, undefined) { //NOSONAR convention for wrapping all modules

    var componentName = "contactdetails";

    console.log(["dialog-"+componentName+"-behaviour","ready"]);
    $(document).on('dialog-ready', function () {
        console.log(["dialog-"+componentName+"-behaviour","checking"]);
        if ($("#dialog-"+componentName+"")[0]) {

            console.log(["dialog-"+componentName+"-behaviour","loading"]);

            //load content into pagedetails-pagemetadata field
            if ($("#dialog-"+componentName+"").find(".pagedetails-pagemetadata")[0]) {

                console.log(["dialog-"+componentName+"-behaviour","loading", "pagedetails-pagemetadata"]);
                var $form = $("#dialog-"+componentName+"").closest('form');
                console.log(['$form', $form]);
                var formUrl = $form[0].action;
                $.getJSON(formUrl + '.json').done(function (data) {
                    console.log(["pagedetails-pagemetadata",data]);
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
                            var curr = $("#dialog-"+componentName+"").find(".pagedetails-pagemetadata")[0].items.add();
                            Coral.commons.ready(curr, function (readyCurr) {
                                $(readyCurr).find('[name="./pageMetaProperty"]')[0].value = fieldconfigKey[index];
                                $(readyCurr).find('[name="./pageMetaPropertyContent"]')[0].value = fieldconfigValue[index];

                            });
                        });
                    }
                    else {
                        //add empty row if needed
                        //$(".pagedetails-pagemetadata")[0].items.add();
                    }


                });

            } else {
                console.log(["dialog-"+componentName+"-behaviour","pagedetails-pagemetadata","not found"]);
            }

            //load content into pagedetails-sociallink field
            if ($("#dialog-"+componentName+"").find(".pagedetails-sociallink")[0]) {
              console.log(["dialog-"+componentName+"-behaviour","loading", "pagedetails-sociallink"]);
              var $form = $("#dialog-"+componentName+"").closest('form');
              console.log(['$form', $form, $form[0].action]);
              var formUrl = $form[0].action;
              $.getJSON(formUrl + '.json').done(function (data) {
                console.log(["pagedetails-sociallink",formUrl,data]);
                var
                  fieldconfigKey = data.linksType,
                  fieldconfigValue = data.linksContent;


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
                    console.log(['field', fieldconfigKey[index], fieldconfigValue[index]]);
                    var curr = $("#dialog-"+componentName+"").find(".pagedetails-sociallink")[0].items.add();
                    Coral.commons.ready(curr, function (readyCurr) {
                      $(readyCurr).find('[name="./linksType"]')[0].value = fieldconfigKey[index];
                      $(readyCurr).find('[name="./linksContent"]')[0].value = fieldconfigValue[index];

                    });
                  });
                }
                else {
                  //add empty row if needed
                  //$(".pagedetails-pagemetadata")[0].items.add();
                }


              });

            } else {
              console.log(["dialog-"+componentName+"-behaviour","pagedetails-pagemetadata","not found"]);
            }

        }
    });


})($, $(document), Coral, AEMDESIGN.components.authoring.contactdetails,  this); //pass in additional dependencies
