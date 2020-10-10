window.AEMDESIGN = window.AEMDESIGN || {};
window.AEMDESIGN.components = window.AEMDESIGN.components || {};
window.AEMDESIGN.components.authoring = window.AEMDESIGN.components.authoring || {};
window.AEMDESIGN.components.authoring.vue = window.AEMDESIGN.components.authoring.vue || {};
window.AEMDESIGN.components.authoring.vue.fields = window.AEMDESIGN.components.authoring.vue.fields || {};

((ns, undefined) => { // NOSONAR convention for wrapping all modules

  /**
   * Generates any autocompletion tags needed for the picker component.
   *
   * @param {Array|String} values Saved JCR values or a single value
   * @return {String}
   */
  function generateAutocompleteTags(values) {
    values = values instanceof Array ? values : [values];

    return values.map((value) => (
      `<coral-tag value="${value}">${value}</coral-tag>`
    )).join('');
  }

  ns.autoComplete = (config, fieldPath, labelledBy, savedValue, tagPath) => {
    const autocompleteElement = document.createElement('div');
    const needsMultiple = config.multiple === true;
    const pickerSource = encodeURIComponent(`${tagPath}/${config.source}`);

    autocompleteElement.innerHTML = `
      <foundation-autocomplete
        class="cq-ui-tagfield"
        name="${fieldPath}"
        pickersrc="/mnt/overlay/cq/gui/content/coral/common/form/tagfield/picker.html?root=${pickerSource}&selectionCount=${needsMultiple ? 'multiple' : 'single'}"
        labelledby="${labelledBy}"
        valuedisplaymode="block"
        data-foundation-validation
        required
      >
        <coral-overlay
          foundation-autocomplete-suggestion
          class="foundation-picker-buttonlist"
          data-foundation-picker-buttonlist-src="/mnt/overlay/cq/gui/content/coral/common/form/tagfield/suggestion{.offset,limit}.html?root=${pickerSource}{&amp;query}"
        ></coral-overlay>

        <coral-taglist foundation-autocomplete-value name="${fieldPath}">
          ${savedValue && generateAutocompleteTags(savedValue)}
        </coral-taglist>

        <input class="foundation-field-related" type="hidden" name="${fieldPath}@TypeHint" value="String${needsMultiple ? '[]' : ''}">
        <input class="foundation-field-related" type="hidden" name="${fieldPath}s@Delete">

        <ui:includeClientLib categories="cq.ui.coral.common.tagfield" />
      </foundation-autocomplete>
    `;

    const autocomplete = autocompleteElement.children[0];

    autocomplete.invalid = !savedValue || false;
    autocomplete.multiple = needsMultiple;

    return autocomplete;
  };

})(AEMDESIGN.components.authoring.vue.fields);
