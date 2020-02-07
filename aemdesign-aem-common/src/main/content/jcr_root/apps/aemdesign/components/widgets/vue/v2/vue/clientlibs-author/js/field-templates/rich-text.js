window.AEMDESIGN = window.AEMDESIGN || {};
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.authoring = AEMDESIGN.components.authoring || {};
window.AEMDESIGN.components.authoring.vue = AEMDESIGN.components.authoring.vue || {};
window.AEMDESIGN.components.authoring.vue.fields = AEMDESIGN.components.authoring.vue.fields || {};

(($, ns, undefined) => { // NOSONAR convention for wrapping all modules

  /**
   * Binds custom validation logic to the RichText form component.
   *
   * @param {Element} richText Parent element
   */
  const bindRichTextValidation = (richText) => {
    const hiddenText      = richText.querySelector('input[type="hidden"]');
    const invisibleText   = richText.querySelector('input[type="text"]');
    const richTextElement = richText.querySelector('.coral-RichText');

    richTextElement.addEventListener('keyup', () => {
      invisibleText.value = hiddenText.value;

      $(invisibleText).checkValidity();
    });
  };

  ns.richText = (config, fieldPath, fieldLabel, savedValue) => {
    const escapedValue = AEMDESIGN.components.authoring.vue.escapeHtml(savedValue || '');

    const richTextElement = `
      <input
        type="hidden"
        data-cq-richtext-input="true"
        class="coral-Form-field"
        data-xtype="richtext"
        data-useFixedInlineToolbar="true"
        data-fieldLabel="${fieldLabel}"
        data-fieldDescription=""
        data-textPropertyName="${fieldPath}"
        data-hideLabel="false"
        name="${fieldPath}"
        required="required"
        value="${escapedValue}">

      <input type="hidden" name="./textIsRich" value="true">
      <input class="u-coral-screenReaderOnly" type="text" value="${escapedValue}" required>

      <div
        data-cq-richtext-editable="true"
        class="cq-RichText-editable coral-RichText-editable coral-RichText coral-DecoratedTextfield-input"
        data-config-path="/apps/aemdesign/global/dialog/touch/description/content/items/descriptionTab/items/column/items/description.infinity.json"
        data-use-fixed-inline-toolbar="true"
        data-custom-start="null"
        data-editor-type="text"
        data-external-style-sheets=""
        class="coral-Form-field"
        data-xtype="richtext"
        data-useFixedInlineToolbar="true"
        data-fieldLabel="${fieldLabel}"
        data-fieldDescription=""
        data-textPropertyName="${fieldPath}"
        data-hideLabel="false"
        name="${fieldPath}"
        value="${escapedValue}"></div>
    `;

    const richText = document.createElement('div');
    richText.setAttribute('class', 'cq-RichText richtext-container coral-DecoratedTextfield');

    richText.innerHTML = richTextElement;

    // Bind a change listener to the hidden input field
    bindRichTextValidation(richText);

    return richText;
  };

})($, AEMDESIGN.components.authoring.vue.fields);
