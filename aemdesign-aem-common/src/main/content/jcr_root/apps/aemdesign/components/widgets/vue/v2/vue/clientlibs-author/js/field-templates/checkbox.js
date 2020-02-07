window.AEMDESIGN = window.AEMDESIGN || {};
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.authoring = AEMDESIGN.components.authoring || {};
window.AEMDESIGN.components.authoring.vue = AEMDESIGN.components.authoring.vue || {};
window.AEMDESIGN.components.authoring.vue.fields = AEMDESIGN.components.authoring.vue.fields || {};

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

    checkbox.checked  = isChecked;
    checkbox.invalid  = (!isChecked && isRequired) || false;
    checkbox.required = isRequired;

    return checkboxElement;
  };

})(AEMDESIGN.components.authoring.vue.fields);
