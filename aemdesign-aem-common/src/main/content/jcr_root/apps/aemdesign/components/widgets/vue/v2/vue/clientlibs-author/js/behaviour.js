window.AEMDESIGN = window.AEMDESIGN || {};
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.authoring = AEMDESIGN.components.authoring || {};
window.AEMDESIGN.components.authoring.vue = AEMDESIGN.components.authoring.vue || {};

(($, Coral, ns, window, undefined) => { // NOSONAR convention for wrapping all modules

  $(document).on('dialog-ready', () => {
    const componentSelect = document.querySelector('[name="./vueComponentName"]');
    const dialogMarker    = document.querySelector('[name="./configurationMarker"]');

    if (!(componentSelect || dialogMarker)) {
      console.error('[Vue Component] Unable to find one or more required dialog elements!', componentSelect, dialogMarker);
      return;
    }

    // Set some basic configuration values
    const componentPath = $(dialogMarker).closest('form').attr('action');
    const tagPath       = dialogMarker.value;

    componentSelect.addEventListener('change', (event) => {
      const component = event.target.selectedItem.value;

      if (component.length) {
        ns.processComponent(component, componentPath, tagPath, dialogMarker);
      } else {
        ns.handleDynamicFieldsWrapper(dialogMarker);
      }
    });

    // Set the default state of the configuration tab
    ns.handleDynamicFieldsWrapper(dialogMarker);

    // Process the dialog immediately so the configuration fields appear
    const componentName = componentSelect.selectedItem.value;

    if (componentName.length) {
      ns.processComponent(componentName, componentPath, tagPath, dialogMarker);
    }

    // Bind any custom validation handlers
    ns.bindCustomValidationHandlers();
  });

})($, Coral, AEMDESIGN.components.authoring.vue, window);
