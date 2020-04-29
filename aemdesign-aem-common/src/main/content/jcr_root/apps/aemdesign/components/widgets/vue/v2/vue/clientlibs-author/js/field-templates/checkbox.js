window.AEMDESIGN = window.AEMDESIGN || {};
window.AEMDESIGN.components = window.AEMDESIGN.components || {};
window.AEMDESIGN.components.authoring = window.AEMDESIGN.components.authoring || {};
window.AEMDESIGN.components.authoring.vue = window.AEMDESIGN.components.authoring.vue || {};
window.AEMDESIGN.components.authoring.vue.fields = window.AEMDESIGN.components.authoring.vue.fields || {};

((ns, undefined) => { // NOSONAR convention for wrapping all modules

  ns.checkbox = (config, fieldPath, isRequired, savedValue) => {
    const checkboxElement = document.createElement('div');

    checkboxElement.innerHTML = `
      <coral-checkbox name="${fieldPath}" value="true" data-foundation-validation data-validation></coral-checkbox>
      <input class="foundation-field-related" type="hidden" name="${fieldPath}@Delete">
      <input class="foundation-field-related" type="hidden" value="false" name="${fieldPath}@DefaultValue">
      <input class="foundation-field-related" type="hidden" value="true" name="${fieldPath}@UseDefaultWhenMissing">
    `;

    const checkbox  = checkboxElement.children[0];
    const isChecked = savedValue === 'true';

    if (isChecked) {
      checkbox.setAttribute('checked', 'checked')
    }

    if (!isChecked && isRequired) {
      checkbox.setAttribute('invalid', 'invalid')
    }

    if (isRequired) {
      checkbox.setAttribute('required', 'required')
    }

    return checkboxElement;
  };

})(AEMDESIGN.components.authoring.vue.fields);
