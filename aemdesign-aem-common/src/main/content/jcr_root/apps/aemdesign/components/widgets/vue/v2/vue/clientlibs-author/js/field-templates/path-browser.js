window.AEMDESIGN = window.AEMDESIGN || {};
window.AEMDESIGN.components = window.AEMDESIGN.components || {};
window.AEMDESIGN.components.authoring = window.AEMDESIGN.components.authoring || {};
window.AEMDESIGN.components.authoring.vue = window.AEMDESIGN.components.authoring.vue || {};
window.AEMDESIGN.components.authoring.vue.fields = window.AEMDESIGN.components.authoring.vue.fields || {};

((ns, undefined) => { // NOSONAR convention for wrapping all modules

  ns.pathBrowser = (config, fieldPath, labelledBy, savedValue) => {
    const pathbrowserElement = document.createElement('div');

    pathbrowserElement.innerHTML = `
      <span class="coral-PathBrowser"
        data-init="pathbrowser"
        data-root-path="/content"
        data-option-loader="granite.ui.pathBrowser.pages.hierarchyNotFile"
        data-picker-src="/libs/wcm/core/content/common/pathbrowser/column.html/content?predicate=hierarchyNotFile"
        data-picker-title="Select Path"
        data-crumb-root="content"
        data-picker-multiselect="false"
        data-root-path-valid-selection="true">
        <span class="coral-InputGroup coral-InputGroup--block">
          <input class="coral-InputGroup-input coral-Textfield js-coral-pathbrowser-input"
            type="text"
            name="${fieldPath}"
            autocomplete="off"
            aria-labelledby="${labelledBy}"
            value="${savedValue !== undefined ? savedValue : ''}"
            data-validation>
          <span class="coral-InputGroup-button">
            <button class="coral-Button coral-Button--square js-coral-pathbrowser-button" type="button" title="Browse">
              <i class="coral-Icon coral-Icon--sizeS coral-Icon--folderSearch"></i>
            </button>
          </span>
        </span>
      </span>
    `;

    return pathbrowserElement.children[0];
  };

})(AEMDESIGN.components.authoring.vue.fields);
