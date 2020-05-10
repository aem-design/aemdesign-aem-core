//genericdetails - behaviour
window.AEMDESIGN = window.AEMDESIGN || {};
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.authoring = AEMDESIGN.components.authoring || {};
window.AEMDESIGN.components.authoring.genericdetails = AEMDESIGN.components.authoring.genericdetails || {};


(function ($, $document, Granite, Coral, ns, window, undefined) { //NOSONAR convention for wrapping all modules

  var componentName = "genericdetails";

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
        console.log(["dialog-" + componentName + "-behaviour", "pagemetadata", "not found"]);
      }


      //load content into variationName field after fragmentPath is changed
      if ($("#dialog-" + componentName + "").find("[name='./fragmentPath']")[0]) {
        var fragmentPath = $("#dialog-" + componentName + "").find("[name='./fragmentPath']")[0];

        console.log(["fragmentPath", fragmentPath, fragmentPath.value]);


        //load content into field
        if ($("#dialog-" + componentName + "").find("[name='./variationName']")[0]) {
          var variationName = $("#dialog-" + componentName + "").find("[name='./variationName']")[0];
          var variationNamePath = variationName.dataset.fieldPath;

          console.log(["variationName", variationName, variationNamePath]);

          $(fragmentPath).on("foundation-field-change", function () {

            var data = {
              fragmentPath: fragmentPath.value
            };

            var url = Granite.HTTP.externalize(variationNamePath) + ".html";
            var variationNameRequest = $.get({
              url: url,
              data: data
            });


            // $.getJSON(url).done(function (data) {
            //     console.log(["variationName",data]);
            // });

            console.log(["variationNameRequest", variationNameRequest]);

            $.when(variationNameRequest).then(function (variantResult) {

              console.log(["variantResult", variantResult]);
              var SELECTOR_VARIATION_NAME = "[name='./variationName']";
              var newVariationName = $(variantResult).find(SELECTOR_VARIATION_NAME)[0];
              // get the fields from the resulting markup and create a test state
              Coral.commons.ready(newVariationName, function () {

                console.log(["variationNameRequest", newVariationName, variantResult]);

                variationName = variationName.parentNode.replaceChild(newVariationName, variationName);
                variationName = $("#dialog-" + componentName + "").find("[name='./variationName']")[0];
                variationName.removeAttribute("disabled");

              });

            }, function (ex) {
              // display error dialog if one of the requests failed
              // ui.prompt(errorDialogTitle, errorDialogMessage, "error");
              console.log(["error", ex]);
              variationName.setAttribute("disabled", "");
            });


          });


        } else {
          console.log(["dialog-" + componentName + "-behaviour", "variationName", "not found"]);
        }

      } else {
        console.log(["dialog-" + componentName + "-behaviour", "fragmentPath", "not found"]);
      }


    }
  });


})($, $(document), Granite, Coral, AEMDESIGN.components.authoring.genericdetails, window); //pass in additional dependencies
