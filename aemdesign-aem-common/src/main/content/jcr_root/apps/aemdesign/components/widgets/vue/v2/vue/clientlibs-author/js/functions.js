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
                config : JSON.parse(atob(field.value)).value,
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
    ns.getFieldsConfigurationFromTag(`${tagPath}/component-dialog/vue-widgets/${componentName}/fields.1.json`)
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
            if (formFields[i]) {
              dynamicFields.appendChild(formFields[i]);
            }
          }

          // Tell Coral to load all the new form elements
          $(document).trigger('cui-contentloaded');
        });
      })
      .catch((err) => {
        console.error('[Vue Component] Failed to retrieve the fields for:', componentName);
        console.error(err);

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
        fieldConstructor = () => window.SWINBURNE.components.authoring.vue.fields.autoComplete(
          config,
          fieldPath,
          labelledBy,
          savedValue,
          tagPath
        );
        break;

      case 'pathbrowser':
        fieldConstructor = () => window.SWINBURNE.components.authoring.vue.fields.pathBrowser(
          config,
          fieldPath,
          labelledBy,
          savedValue
        );
        break;

      case 'fileUpload':
        fieldConstructor = () => window.SWINBURNE.components.authoring.vue.fields.fileUpload(
          config,
          fieldPath,
          fieldPathBase,
          labelledBy,
          savedValue,
          componentPath
        );
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
        fieldConstructor = () => window.SWINBURNE.components.authoring.vue.fields.richText(
          config,
          fieldPath,
          fieldLabel,
          savedValue
        );
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
        fieldConstructor = () => window.SWINBURNE.components.authoring.vue.fields.checkbox(
          config,
          fieldPath,
          isRequired,
          savedValue
        );
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

    // When no order is defined, attempt to find a slot between our existing fields
    if (!order) {
      let slotFound = false;

      for (let i = 0; i <= fieldsTotal; i++) {
        if (!formFields[i]) {
          formFields[i] = wrapperElement;
          slotFound = true;
        }
      }

      // If no slots are found, drop it on the end
      if (!slotFound) {
        formFields[fieldsTotal] = formFields[order];
      }
    } else {
      formFields[order] = wrapperElement;
    }
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

})($, Coral, SWINBURNE.components.authoring.vue, window);
