//pagedetails - behaviour
window.AEMDESIGN = window.AEMDESIGN || {};
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.authoring = AEMDESIGN.components.authoring || {};
window.AEMDESIGN.components.authoring.pagedetails = AEMDESIGN.components.authoring.pagedetails || {};


(function ($, $document, Coral, ns, window, undefined) { //NOSONAR convention for wrapping all modules

  console.log(["dialog-pagedetails-behaviour", "ready"]);
  $(document).on('dialog-ready', function () {
    console.log(["dialog-pagedetails-behaviour", "checking"]);
    if ($('#dialog-pagedetails')[0]) {

      console.log(["dialog-pagedetails-behaviour", "loading"]);

      // $document.on('change', '.pageMetaProperty, .pageMetaPropertyContent', function (event) {
      //
      //     var element = event.target,
      //         parentElement = element.closest('.dialog-pagedetails-pagemetadata'),
      //         childpageMetaPropertyElement = parentElement.querySelector('.pageMetaProperty'),
      //         childpageMetaPropertyContentElement = parentElement.querySelector('.pageMetaPropertyContent')
      //
      // });

      //load content into field
      if ($('#dialog-pagedetails').find('.pagedetails-pagemetadata')[0]) {

        var $form = $("#dialog-pagedetails").closest('form');
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
              var curr = $('#dialog-pagedetails').find('.pagedetails-pagemetadata')[0].items.add();
              Coral.commons.ready(curr, function (readyCurr) {
                $(readyCurr).find('[name="./pageMetaProperty"]')[0].value = fieldconfigKey[index];
                $(readyCurr).find('[name="./pageMetaPropertyContent"]')[0].value = fieldconfigValue[index];

              });
            });
          } else {
            //$('.pagedetails-pagemetadata')[0].items.add();
          }


        });

      } else {
        console.log(["dialog-pagedetails-behaviour", "pagedetails-pagemetadata", "not found"]);
      }


    }
  });


})($, $(document), Coral, AEMDESIGN.components.authoring.pagedetails, this); //pass in additional dependencies
