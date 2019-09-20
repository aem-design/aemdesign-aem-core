window.AEMDESIGN = window.AEMDESIGN || {};
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.authoring = AEMDESIGN.components.authoring || {};
window.AEMDESIGN.components.authoring.vue = AEMDESIGN.components.authoring.vue || {};

(($, Coral, ns, window, undefined) => { // NOSONAR convention for wrapping all modules
  const _version = '0.1';

  // Create the loading element
  const waitingElement = new Coral.Wait().set({ size: 'M' });
  waitingElement.classList.add('vue-loading');

  // Get the Foundation Registry instance
  const foundationRegistry = $(window).adaptTo('foundation-registry');

  // Define some core namespace values
  ns.name    = 'aemdesign.components.widgets.vue';
  ns.version = () => _version;

  let formFields = {};

  /**
   * Loads and parses a given tag to retrieve the fields and their configurations.
   *
   * @param {String} [path=null] Path to the tag
   * @return {Promise<any>}
   */
  ns.getFieldsConfigurationFromTag = (path = null) => {
    return new Promise((resolve, reject) => {
      fetch(path, { credentials: 'include' })
        .then((r) => r.json())
        .then((r) => {
          const fields = {};

          for (const key of Object.keys(r)) {
            const field = r[key];

            if (typeof field === 'object') {
              fields[key] = {
                config : JSON.parse(atob(field.value)),
                title  : field['jcr:title'],
              }
            }
          }

          resolve(fields);
        })
        .catch(reject);
    });
  };

  /**
   * Retrieves the component level field configuration.
   *
   * @param {String} componentPath JCR location to the component
   * @param {String} componentName Name of the selected component
   * @param {Function} callback A callback to run after a successful response
   */
  ns.getComponentConfiguration = (componentPath, componentName, callback) => {
    fetch(`${componentPath}/dynamic/${componentName}.2.json`, { credentials: 'include' })
      .then(r => r.json())
      .then(callback)
      .catch(err => {
        console.warn('[Vue Component] Failed to retrieve the saved configuration data for:', componentName, err);
        callback({});
      })
  };

  /**
   * Updates and maintains the dynamic form fields and waiting state.
   *
   * @param {Element} dialogMarker Position in the dialog to add dynamic nodes
   * @return {Element}
   */
  ns.handleDynamicFieldsWrapper = (dialogMarker) => {
    const parentElement = dialogMarker.parentNode;

    let dynamicFields = parentElement.querySelector('.vue-dynamics');

    if (!dynamicFields) {
      dynamicFields = document.createElement('div');
      dynamicFields.setAttribute('class', 'vue-dynamics');

      parentElement.appendChild(waitingElement);
      parentElement.appendChild(dynamicFields);
    }

    // Empty out the dynamic fields wrapper
    dynamicFields.innerHTML = '';

    // Show the waiting element
    waitingElement.show();

    return dynamicFields;
  };

  /**
   * Retrieves the component level configuration for both fields and saved data and constucts
   *
   * @param {String} componentName Name of the selected component
   * @param {String} componentPath JCR location to the component
   * @param {String} tagPath JCR location of the site tags
   * @param {Element} dialogMarker Position in the dialog to add dynamic nodes
   */
  ns.processComponent = (componentName, componentPath, tagPath, dialogMarker) => {
    ns.getFieldsConfigurationFromTag(`${tagPath}/component-dialog/vue-widgets/${componentName}.1.json`)
      .then((fields) => {
        const dynamicFields = ns.handleDynamicFieldsWrapper(dialogMarker);

        // Attempt to retrieve the component config and parse the fields
        ns.getComponentConfiguration(componentPath, componentName, (config) => {
          formFields = {};

          // Hide the waiting element
          waitingElement.hide();

          for (const field of Object.keys(fields)) {
            try {
              ns.processField(field, fields[field], config[field], componentName, tagPath, componentPath);
            } catch (e) {
              console.warn('[Vue Component] Failed to create form field!', e);
            }
          }

          // Add the form fields in their original order
          for (let i = 1; i <= Object.keys(formFields).length; i++) {
            dynamicFields.appendChild(formFields[i]);
          }

          // Tell Coral to load all the new form elements
          $(document).trigger('cui-contentloaded');
        });
      })
      .catch(() => {
        console.error('[Vue Component] Failed to retrieve the fields for:', componentName);
        const dynamicFields = ns.handleDynamicFieldsWrapper(dialogMarker);
        const fieldlessPrompt = document.createElement('p');
        fieldlessPrompt.innerHTML = `<strong>The ${componentName} component has no configurable fields.</strong>`;
        dynamicFields.appendChild(fieldlessPrompt);
        waitingElement.hide();
      });
  };

  /**
   * Escapes the input text to prevent XSS injection in dialog fields.
   *
   * @param {String} text Input to escape
   * @return {String}
   */
  ns.escapeHtml = (text) => {
    const map = {
      '&': '&amp;',
      '<': '&lt;',
      '>': '&gt;',
      '"': '&quot;',
      "'": '&#039;',
    };

    return text.replace(/[&<>"']/g, (m) => map[m]);
  };

  /**
   * Generates and sets up the field required for the dialog.
   *
   * @param {String} field Name of the field
   * @param {Object} data Field configuration
   * @param {Object|String} savedValue JCR value that was saved previously
   * @param {String} componentName Name of the selected component
   * @param {String} tagPath JCR location of the site tags
   * @param {String} componentPath JCR location of the Vue component
   */
  ns.processField = (field, data, savedValue, componentName, tagPath, componentPath) => {
    const config = data.config;

    const fieldLabel       = Coral.i18n.get(data.title);
    const fieldIdentifier  = field;
    const fieldPath        = `./dynamic/${componentName}/${fieldIdentifier}`;
    const fieldPathBase    = `dynamic/${componentName}/${fieldIdentifier}`;
    const fieldPlaceholder = config.placeholder || '';
    const fieldTooltip     = config.tooltip || false;
    const isRequired       = config.required !== undefined ? config.required : true;
    const labelledBy       = `vue_label_${fieldIdentifier}`;

    let fieldConstructor = false;

    switch (config.field) {
      case 'autocomplete':
        fieldConstructor = () => {
          const autocompleteElement = document.createElement('div');
          const needsMultiple       = config.multiple === true;
          const pickerSource        = encodeURIComponent(`${tagPath}/${config.source}`);

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
                ${savedValue && ns.generateAutocompleteTags(savedValue)}
              </coral-taglist>

              <input class="foundation-field-related" type="hidden" name="${fieldPath}@TypeHint" value="String${needsMultiple ? '[]' : ''}">
              <input class="foundation-field-related" type="hidden" name="${fieldPath}s@Delete">

              <ui:includeClientLib categories="cq.ui.coral.common.tagfield" />
            </foundation-autocomplete>
          `;

          const autocomplete = autocompleteElement.children[0];

          autocomplete.invalid  = !savedValue || false;
          autocomplete.multiple = needsMultiple;

          return autocomplete;
        };
        break;

      case 'fileUpload':
        fieldConstructor = () => {
          const fileUploadElement = document.createElement('div');
          const hasSelectedImage  = savedValue && savedValue['fileReference'];

          fileUploadElement.innerHTML = `
            <coral-fileupload class="coral-Form-field cq-FileUpload cq-droptarget"
              name="${fieldPath}"
              async
              data-foundation-validation
              accept="image"
              action="${componentPath}"
              data-cq-fileupload-temporaryfilename="${fieldPath}.sftmp"
              data-cq-fileupload-temporaryfiledelete="${fieldPath}.sftmp@Delete"
              data-cq-fileupload-temporaryfilepath="${componentPath}/${fieldPathBase}.sftmp">
              <div class="cq-FileUpload-thumbnail">
                <div class="cq-FileUpload-thumbnail-img" data-cq-fileupload-thumbnail-img>
                  ${hasSelectedImage ?
                    `<img src="${savedValue['fileReference']}" alt="${savedValue['fileReference']}" title="${savedValue['fileReference']}">`
                    : ''
                  }
                </div>
                
                <button type="button" class="cq-FileUpload-clear" is="coral-button" variant="quiet" coral-fileupload-clear>Clear</button>
                
                <div class="cq-FileUpload-thumbnail-dropHere">
                  <coral-icon icon="image" class="cq-FileUpload-icon"></coral-icon>
                  <span class="cq-FileUpload-label">Drop an asset here.</span>
                </div>
              </div>
              
              <input type="hidden" name="${fieldPath}/fileName" data-cq-fileupload-parameter="filename" value="${(hasSelectedImage && savedValue['fileName']) || ''}" disabled>
              <input type="hidden" name="${fieldPath}/fileReference" data-cq-fileupload-parameter="filereference" value="${(hasSelectedImage && savedValue['fileReference']) || ''}" disabled>
              <input type="hidden" name="${fieldPath}@Delete" data-cq-fileupload-parameter="filedelete" disabled>
              <input type="hidden" name="${fieldPath}@MoveFrom" data-cq-fileupload-parameter="filemovefrom" value="${componentPath}/${fieldPathBase}.sftmp" disabled>
              <input type="hidden" name="${fieldPath}/jcr:lastModified">
              <input type="hidden" name="${fieldPath}/jcr:lastModifiedBy">
            </coral-fileupload>
          `;

          if (hasSelectedImage) {
            fileUploadElement.children[0].classList.add('is-filled')
          }

          //if (isRequired) {
          //  fileUploadElement.children[0].setAttribute('required', 'required')
          //}

          return fileUploadElement;
        };
        break;

      case 'textfield':
        fieldConstructor = () => {
          return new Coral.Textfield().set({
            invalid     : (!savedValue && isRequired) || false,
            labelledBy  : labelledBy,
            name        : fieldPath,
            placeholder : fieldPlaceholder,
            required    : isRequired,
            value       : savedValue || '',
          });
        };
        break;

      case 'textarea':
        fieldConstructor = () => {
          return new Coral.Textarea().set({
            invalid     : (!savedValue && isRequired) || false,
            labelledBy  : labelledBy,
            name        : fieldPath,
            required    : isRequired,
            value       : savedValue || '',
          });
        };
        break;

      case 'richtext':
        fieldConstructor = () => {
          const escapedValue = ns.escapeHtml(savedValue || '');

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
          ns.bindRichTextValidation(richText);

          return richText;
        };
        break;

      case 'select':
        fieldConstructor = () => {
          const selectElement = new Coral.Select().set({
            invalid     : (!savedValue && isRequired) || false,
            labelledBy  : labelledBy,
            name        : fieldPath,
            placeholder : fieldPlaceholder,
            required    : isRequired,
          });

          if (config.items) {
            for (const item of config.items) {
              selectElement.items.add({
                content: {
                  innerHTML: item.label,
                },

                disabled : false,
                selected : savedValue === item.value,
                value    : item.value,
              });
            }
          }

          return selectElement;
        };
        break;

      case 'checkbox':
        fieldConstructor = () => {
          return new Coral.Checkbox().set({
            invalid     : (!savedValue && isRequired) || false,
            labelledBy  : labelledBy,
            name        : fieldPath,
            required    : isRequired,
            value       : savedValue || '',
          });
        };
        break;

      default:
        console.warn("[Vue Component] '%s' is not a valid field type!", config.field);
        return;
    }

    ns.createFormField(
      fieldLabel,
      fieldIdentifier,
      fieldTooltip,
      fieldConstructor,
      config.order || false,
      labelledBy
    );
  };

  /**
   * Generates any autocompletion tags needed for the picker component.
   *
   * @param {Array|String} values Saved JCR values or a single value
   * @return {String}
   */
  ns.generateAutocompleteTags = (values) => {
    values = values instanceof Array ? values : [values];

    return values.map((value) => (
      `<coral-tag value="${value}">${value}</coral-tag>`
    )).join('');
  };

  /**
   * Generates the form field wrapper and all associated elements required for the field to work.
   *
   * @param {String} label Title/name of the field
   * @param {String} identifier Unique identifier used for the field
   * @param {Boolean|String} tooltip A small message to display
   * @param {Function} fieldConstructor Callback to run that contains the newly generated form input
   * @param {Number|Boolean} order Order by which the field should be added
   * @param {String} labelIdentifier Unique identifier for the field label
   */
  ns.createFormField = (label, identifier, tooltip, fieldConstructor, order, labelIdentifier) => {
    const wrapperElement = document.createElement('div');
    wrapperElement.classList.add('coral-Form-fieldwrapper');
    wrapperElement.classList.add('vue-dynamics-form-field');

    const labelElement = document.createElement('label');
    labelElement.setAttribute('id', labelIdentifier);
    labelElement.classList.add('coral-Form-fieldlabel');
    labelElement.innerText = label;

    wrapperElement.appendChild(labelElement);

    if (typeof fieldConstructor === 'function') {
      const newField = fieldConstructor();
      newField.classList.add('coral-Form-field');

      wrapperElement.appendChild(newField);
    }

    if (tooltip !== false) {
      ns.generateTooltip('infoCircle', Coral.Icon.size.SMALL, tooltip, false, wrapperElement);
    }

    // Add the form field back in order
    const fieldsTotal = Object.keys(formFields).length + 1;

    if (formFields[order]) {
      formFields[fieldsTotal] = formFields[order];
    }

    formFields[order ? order : fieldsTotal] = wrapperElement;
  };

  /**
   * Generates a tooltip component.
   *
   * @param {String} icon
   * @param iconSize
   * @param text
   * @param error
   * @param target
   * @param bindTo
   */
  ns.generateTooltip = (icon, iconSize, text, error, target, bindTo) => {
    const tooltipIcon = new Coral.Icon().set({
      icon: icon,
      size: iconSize,
    });

    tooltipIcon.classList.add(error ? 'coral-Form-fielderror': 'coral-Form-fieldinfo');

    const tooltipElement = new Coral.Tooltip().set({
      content: {
        innerHTML: text,
      },

      placement : 'left',
      target    : tooltipIcon,
      variant   : Coral.Tooltip.variant[error ? 'ERROR' : 'INFO'],
    });

    target.appendChild(tooltipIcon);
    target.appendChild(tooltipElement);

    if (!bindTo) {
      bindTo = target
    }

    bindTo.icon    = tooltipIcon;
    bindTo.tooltip = tooltipElement;
  };

  /**
   * Binds custom validation logic to the RichText form component.
   *
   * @param {Element} richText Parent element
   */
  ns.bindRichTextValidation = (richText) => {
    const hiddenText      = richText.querySelector('input[type="hidden"]');
    const invisibleText   = richText.querySelector('input[type="text"]');
    const richTextElement = richText.querySelector('.coral-RichText');

    richTextElement.addEventListener('keyup', () => {
      invisibleText.value = hiddenText.value;

      $(invisibleText).checkValidity();
    });
  };

  //
  ns.bindCustomValidationHandlers = () => {
    foundationRegistry.register('foundation.validation.validator', {
      selector: '.richtext-container > input:text',

      validate(el) {
        const $el          = $(el);
        const $hidden      = $el.parent().find('input[type=hidden]');
        const fieldWrapper = $el.closest('.coral-Form-fieldwrapper').get(0);
        const isRequired   = $hidden.attr('required') === 'required' || $hidden.attr('aria-required') === 'true';

        // Hide the field icon and tooltip
        if (el.icon && el.tooltip) {
          el.icon.hide();
          el.tooltip.hide();
        }

        // Show the main icon and tooltip
        fieldWrapper.icon.show();
        fieldWrapper.tooltip.show();

        if (isRequired && !$el.val().trim().length) {
          return $el.message('validation.required') || Coral.i18n.get('Please fill out this field');
        }

        return null;
      },

      show(el, message) {
        const $el          = $(el);
        const fieldWrapper = $el.closest('.coral-Form-fieldwrapper').get(0);

        ns.clearValidationState($el.siblings('.coral-RichText'), el, fieldWrapper);

        if (!(el.icon && el.tooltip)) {
          ns.generateTooltip(
            'alert',
            Coral.Icon.size.SMALL,
            message,
            true,
            fieldWrapper,
            el
          );
        } else {
          el.icon.show();
          el.tooltip.show();
        }

        // Hide the main icon and tooltip
        fieldWrapper.icon.hide();
        fieldWrapper.tooltip.hide();

        $el.siblings('.coral-RichText')
          .attr('aria-invalid', true)
          .addClass('is-invalid', true);
      },
    });
  };

  //
  ns.clearValidationState = ($el) => {
    $el.siblings('.coral-RichText')
      .removeAttr('aria-invalid')
      .removeClass('is-invalid');
  };

})($, Coral, AEMDESIGN.components.authoring.vue, window);
