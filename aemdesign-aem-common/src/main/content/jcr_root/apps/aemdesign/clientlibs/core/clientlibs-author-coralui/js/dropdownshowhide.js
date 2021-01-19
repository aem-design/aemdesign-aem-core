/**
 * Extension to the standard dropdown/select component. It enabled hidding/unhidding of other components based on the
 * selection made in the dropdown/select.
 *
 * How to use:
 *
 * - add the class dialog-dropdown-showhide to the dropdown/select element
 * - add the data attribute dialog-dropdown-showhide-target to the dropdown/select element, value should be the
 *   selector, usually a specific class name, to find all possible target elements that can be shown/hidden.
 * - add the target class to each target component that can be shown/hidden
 * - add the class hidden to each target component to make them initially hidden
 * - add the attribute showhidetargetvalue to each target component, the value should equal the value of the select
 *   option that will unhide this element.
 *
 *   source:
 *   /libs/cq/gui/components/authoring/dialog/dropdownshowhide/clientlibs/dropdownshowhide/js/dropdownshowhide.js
 */
(function(document, $) {
  "use strict";

  // when dialog gets injected
  $(document).on("foundation-contentloaded", function(e) {
    // if there is already an inital value make sure the according target element becomes visible
    showHideHandler($(".dialog-dropdown-showhide", e.target));

    $(document).on("selected", ".dialog-dropdown-showhide", function() {
      showHideHandler($(this));
    });

    $(document).on("coral-overlay:open", "coral-dialog", function(event) {
      showHideHandler($(".dialog-dropdown-showhide", e.target));
    });

  });


  function showHideHandler(el) {
    el.each(function (i, element) {
      if($(element).is("coral-select")) {
        // handle Coral3 base drop-down
        Coral.commons.ready(element, function (component) { //NOSONAR acceptable jquery pattern
          showHide(component, element);

          component.on("change", function () {
            showHide(component, element);
          });
        });
      } else {
        // handle Coral2 based drop-down
        var component = $(element).data("select");
        if (component) {

          showHide(component, element);
        }
      }
    });
  }

  function showHide(component, element) {
    // get the selector to find the target elements. its stored as data-.. attribute
    var target = $(element).data("dialogDropdownShowhideTarget");
    var isCoral = $(element).is("coral-select");
    var $target = $(target);
    var selectedItem = component.selectedItem;
    var value = selectedItem ? selectedItem.value : "";
    var valueAlt = selectedItem ? selectedItem.getAttribute("data-valuealt") : "";

    if (isCoral) {
      target = $(element).data("dialog-dropdown-showhide-target");
      $target = $(target)
    }

    if (component._nativeSelect && component._nativeSelect[0]) {
      var selectedIndex = component._nativeSelect[0].selectedIndex || 0;
      var selectedOption = $(component._nativeSelect[0][selectedIndex]);
      if (selectedOption.length > 0) {
        valueAlt = selectedOption.data("valuealt");
      }
    }

    if (typeof component.getValue == "function") {
      value = component.getValue();
    }

    if ($target) {

      $target.each(function (i, element) {

        var $element = $(element);

        // make sure all unselected target elements are hidden.
        $element.addClass("hide");

        if (!valueAlt) {
          // unhide the target element that contains the selected value as data-showhidetargetvalue attribute
          $element.filter("[data-showhidetargetvalue='" + value + "']").removeClass("hide"); // deprecated
        } else {
          $element.filter("[data-showhidetargetvalue='" + valueAlt + "']").removeClass("hide"); // deprecated
        }
      });

    }
  }

})(document,Granite.$);
