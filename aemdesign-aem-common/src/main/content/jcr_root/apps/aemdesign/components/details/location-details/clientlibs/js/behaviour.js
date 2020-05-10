//newsdetails - behaviour
window.AEMDESIGN = window.AEMDESIGN || {};
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.authoring = AEMDESIGN.components.authoring || {};
window.AEMDESIGN.components.authoring.locationdetails = AEMDESIGN.components.authoring.locationdetails || {};


(function ($, $document, Coral, ns, window, undefined) { //NOSONAR convention for wrapping all modules

  var componentName = "locationdetails";

  console.log(["dialog-" + componentName + "-behaviour", "ready"]);
  $(document).on('dialog-ready', function () {
    console.log(["dialog-" + componentName + "-behaviour", "checking"]);
    if ($("#dialog-" + componentName + "")[0]) {

      console.log(["dialog-" + componentName + "-behaviour", "loading"]);

      //load content into field
      if ($("#dialog-" + componentName + "").find(".pagedetails-pagemetadata")[0]) {

        var $form = $("#dialog-" + componentName + "").closest('form');
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
              var curr = $("#dialog-" + componentName + "").find(".pagedetails-pagemetadata")[0].items.add();
              Coral.commons.ready(curr, function (readyCurr) {
                $(readyCurr).find('[name="./pageMetaProperty"]')[0].value = fieldconfigKey[index];
                $(readyCurr).find('[name="./pageMetaPropertyContent"]')[0].value = fieldconfigValue[index];

              });
            });
          } else {
            //add empty row if needed
            //$(".pagedetails-pagemetadata")[0].items.add();
          }


        });

      } else {
        console.log(["dialog-" + componentName + "-behaviour", "pagedetails-pagemetadata", "not found"]);
      }


    }
  });


})($, $(document), Coral, AEMDESIGN.components.authoring.locationdetails, this); //pass in additional dependencies
