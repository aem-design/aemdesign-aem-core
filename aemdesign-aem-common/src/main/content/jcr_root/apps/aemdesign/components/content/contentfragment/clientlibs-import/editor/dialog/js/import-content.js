(function(window, $, channel, Granite, Coral) {
    "use strict";

    // class of the edit dialog content
    var CLASS_EDIT_DIALOG = "cmp-contentfragment__editor";

    // field selectors
    var SELECTOR_FRAGMENT_PATH = "[name='./fragmentPath']";
    var SELECTOR_ELEMENT_NAMES_CONTAINER = "[data-element-names-container='true']";
    var SELECTOR_ELEMENT_NAMES = "[data-granite-coral-multifield-name='./elementNames']";
    var SELECTOR_SINGLE_TEXT_ELEMENT = "[data-single-text-selector='true']";
    var SELECTOR_VARIATION_NAME = "[name='./variationName']";
    var SELECTOR_DISPLAY_MODE_RADIO_GROUP = "[data-display-mode-radio-group='true']";
    var SELECTOR_DISPLAY_MODE_CHECKED = "[name='./displayMode']:checked";
    var SELECTOR_PARAGRAPH_CONTENT_IMPORT_CONTROLS = ".cmp-contentfragment__editor-paragraph-content-import";
    var SELECTOR_PARAGRAPH_IMPORT_ACTION = ".js-contentfragment-importassets";
    var SELECTOR_PARAGRAPH_IMPORT_ACTION_INFO = ".paragraphcontentimportinfo";

    // mode in which only one multiline text element could be selected for display
    var SINGLE_TEXT_DISPLAY_MODE = "singleText";

    // ui helper
    var ui = $(window).adaptTo("foundation-ui");

    // dialog texts
    var confirmationDialogTitle = Granite.I18n.get("Warning");
    var confirmationDialogMessage = Granite.I18n.get("Please confirm replacing the current content fragment and its configuration");
    var confirmationDialogCancel = Granite.I18n.get("Cancel");
    var confirmationDialogConfirm = Granite.I18n.get("Confirm");
    var errorDialogTitle = Granite.I18n.get("Error");
    var errorDialogMessage = Granite.I18n.get("Failed to load the elements of the selected content fragment");

    // the fragment path field (foundation autocomplete)
    var fragmentPath;

    // the paragraph controls (field set)
    var contentImportControls;
    // the tab containing paragraph control
    var contentImportControlsTab;

    // keeps track of the current fragment path
    var currentFragmentPath;

    var editDialog;

    var elementsController;

    var importAssetsAction;
    var importAssetsActionInfo;

    /**
     * A class which encapsulates the logic related to element selectors and variation name selector.
     */
    var ElementsController = function() {
        this._updateFields();
    };

    /**
     * Updates the member fields of this class according to current dom of dialog.
     */
    ElementsController.prototype._updateFields = function() {
        this.elementImportContentAction = editDialog.querySelector(SELECTOR_PARAGRAPH_IMPORT_ACTION);
        this.singleTextSelector = editDialog.querySelector(SELECTOR_SINGLE_TEXT_ELEMENT);
    };

  /**
   * Disable all the fields of this controller.
   */
  ElementsController.prototype.toggleFields = function() {
    console.log(["this.singleTextSelector",this.singleTextSelector]);
    if (this.singleTextSelector == null) {
      this.disableFields();
    } else if (this.singleTextSelector.value === "") {
      this.disableFields();
    } else {
      this.enableFields();
    }
  };

    /**
     * Disable all the fields of this controller.
     */
    ElementsController.prototype.disableFields = function() {
        if (this.elementImportContentAction && this.singleTextSelector.value === "") {
            this.elementImportContentAction.setAttribute("disabled", "");
        }
    };

    /**
     * Enable all the fields of this controller.
     */
    ElementsController.prototype.enableFields = function() {
        if (this.elementImportContentAction && this.singleTextSelector.value !== "") {
            this.elementImportContentAction.removeAttribute("disabled");
        }
    };

    /**
     * Resets all the fields of this controller.
     */
    ElementsController.prototype.resetFields = function() {
    };

    /**
     * Creates an http request object for retrieving fragment's element names or variation names and returns it.
     *
     * @param {String} displayMode - displayMode parameter for element name request. Should be "singleText" or "multi"
     * @param {String} type - type of request. It can have the following values -
     * 1. "variation" for getting variation names
     * 2. "elements" for getting element names
     * @returns {Object} the resulting request
     */
    ElementsController.prototype.prepareRequest = function(displayMode, type) {
        if (typeof displayMode === "undefined") {
            displayMode = editDialog.querySelector(SELECTOR_DISPLAY_MODE_CHECKED).value;
        }
        var data = {
            fragmentPath: fragmentPath.value,
            displayMode: displayMode
        };
        var url;
        if (type === "variation") {
            url = Granite.HTTP.externalize(this.variationNamePath) + ".html";
        } else if (type === "elements") {
            url = Granite.HTTP.externalize(this.elementsContainerPath) + ".html";
        }
        var request = $.get({
            url: url,
            data: data
        });
        return request;
    };

    /**
     * Retrieve element names and update the current element names with the retrieved data.
     *
     * @param {String} displayMode - selected display mode of the component
     */
    ElementsController.prototype.fetchAndUpdateElementsHTML = function(displayMode) {
        var elementNamesRequest = this.prepareRequest(displayMode, "elements");
        var self = this;
        // wait for requests to load
        $.when(elementNamesRequest).then(function(result) {
            self._updateElementsHTML(result);
        }, function() {
            // display error dialog if one of the requests failed
            ui.prompt(errorDialogTitle, errorDialogMessage, "error");
        });
    };

    /**
     * Updates inner html of element container.
     *
     * @param {String} html - outerHTML value for elementNamesContainer
     */
    ElementsController.prototype._updateElementsHTML = function(html) {
        //this.elementNamesContainer.innerHTML = $(html)[0].innerHTML;
        this._updateFields();
    };

    /**
     * Updates dom of element container with the passed dom. If the passed dom is multifield, the current multifield
     * template would be replaced with the dom's template otherwise the dom would used as the new singleTextSelector
     * member.
     *
     * @param {HTMLElement} dom - new dom
     */
    ElementsController.prototype._updateElementsDOM = function(dom) {
        if (dom.tagName === "CORAL-MULTIFIELD") {
            // replace the element names multifield's template
            this.elementNames.template = dom.template;
        } else {
            dom.value = this.singleTextSelector.value;
            this.singleTextSelector.parentNode.replaceChild(dom, this.singleTextSelector);
            this.singleTextSelector = dom;
            this.singleTextSelector.removeAttribute("disabled");
        }
        this._updateFields();
    };

    /**
     * Updates dom of variation name select dropdown.
     *
     * @param {HTMLElement} dom - dom for variation name dropdown
     */
    ElementsController.prototype._updateVariationDOM = function(dom) {
        // replace the variation name select, keeping its value
        dom.value = this.variationName.value;
        this.variationName.parentNode.replaceChild(dom, this.variationName);
        this.variationName = dom;
        this.variationName.removeAttribute("disabled");
        this._updateFields();
    };

    /**
     * Executes after the fragment path has changed. Shows a confirmation dialog to the user if the current
     * configuration is to be reset and updates the fields to reflect the newly selected content fragment.
     */
    function onFragmentPathChange() {
        console.log(["onFragmentPathChange",fragmentPath]);

        // if the fragment was reset (i.e. the fragment path was deleted)
        if (!fragmentPath.value) {
            currentFragmentPath = fragmentPath.value;
            elementsController.disableFields();
        } else {
          console.log(["onFragmentPathChange",fragmentPath]);
          currentFragmentPath = fragmentPath.value;
          elementsController.enableFields();
        }


    }

    /**
     * Compares two arrays containing select items, returning true if the arrays have the same size and all contained
     * items have the same value and label.
     *
     * @param {Array} a1 - first array to compare
     * @param {Array} a2 - second array to compare
     * @returns {Boolean} true if both arrays are equal, false otherwise
     */
    function itemsAreEqual(a1, a2) {
        // verify that the arrays have the same length
        if (a1.length !== a2.length) {
            return false;
        }
        for (var i = 0; i < a1.length; i++) {
            var item1 = a1[i];
            var item2 = a2[i];
            if (item1.attributes.value.value !== item2.attributes.value.value ||
                item1.textContent !== item2.textContent) {
                // the values or labels of the current items didn't match
                return false;
            }
        }
        return true;
    }

    // Toggles the display of paragraph control tab depending on display mode
    function updateParagraphImportTabState() {

      var displayMode = editDialog.querySelector(SELECTOR_DISPLAY_MODE_CHECKED).value;
      if (displayMode === SINGLE_TEXT_DISPLAY_MODE) {
        contentImportControlsTab.hidden = false;
        updateParagraphImportState();
      } else {
        contentImportControlsTab.hidden = true;
      }

    }

    // Toggles the display of paragraph control tab depending on display mode
    function updateParagraphImportState() {
      elementsController._updateFields();
      elementsController.toggleFields();
    }

    /**
     * Initializes the dialog after it has loaded.
     */
    channel.on("foundation-contentloaded", function(e) {
        if (e.target.getElementsByClassName(CLASS_EDIT_DIALOG).length > 0) {
            Coral.commons.ready(e.target, function(dialog) {
                initialize(dialog);
            });
        }
    });


    function initialize(dialog) {
      // get path of component being edited
      editDialog = dialog;
      // get the fields
      fragmentPath = dialog.querySelector(SELECTOR_FRAGMENT_PATH);

      contentImportControls = dialog.querySelector(SELECTOR_PARAGRAPH_CONTENT_IMPORT_CONTROLS);
      //get Content Import tab
      contentImportControlsTab = dialog.querySelector("coral-tabview").tabList.items.getAll()[2];
      // paragraphControlsTab = dialog.querySelector("coral-tabview").tabList.items.getAll()[2];

      importAssetsAction = dialog.querySelector(SELECTOR_PARAGRAPH_IMPORT_ACTION);
      importAssetsActionInfo = dialog.querySelector(SELECTOR_PARAGRAPH_IMPORT_ACTION_INFO);

      console.log(["fragmentPath",fragmentPath]);
      console.log(["contentImportControls",contentImportControls]);
      console.log(["contentImportControlsTab",contentImportControlsTab]);
      console.log(["importAssetsAction",importAssetsAction]);
      console.log(["importAssetsActionInfo",importAssetsActionInfo]);


      // initialize state variables
      currentFragmentPath = fragmentPath.value;
      elementsController = new ElementsController();

      console.log(["importAssetsActionInfo",elementsController.singleTextSelector]);

      // disable add button and variation name if no content fragment is currently set
      if (!currentFragmentPath) {
        elementsController.disableFields();
      }
      // register change listener
      $(fragmentPath).on("foundation-field-change", onFragmentPathChange);

      //update state when Single Element is updated
      $(document).on("change", SELECTOR_SINGLE_TEXT_ELEMENT, updateParagraphImportState);

      //update state when Single or Multiple is changed
      var $radioGroup = $(dialog).find(SELECTOR_DISPLAY_MODE_RADIO_GROUP);
      $radioGroup.on("change", function(e) {
        updateParagraphImportTabState();
      });

      //when activated allow asset import
      if (importAssetsAction) {

        $(document).on("click", ".js-contentfragment-importassets", function (e) {
          console.log("import assets.");
        });

        console.log("activated on: "+ importAssetsAction);
      }

      console.log("aemdesign content fragment dialog extension loaded")

    }


})(window, jQuery, jQuery(document), Granite, Coral);
