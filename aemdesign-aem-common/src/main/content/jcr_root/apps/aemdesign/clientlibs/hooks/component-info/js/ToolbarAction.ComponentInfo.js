/*
 * Toolbar Action hook for Component Info which displays a CoralUI dialog with information about
 * the component that has been authored.
 *
 * Copyright (c) 2020 AEM.Design
 */

/* globals Granite,Coral */
(function ($, ns, channel, window, undefined) {
  'use strict'

  const EditorFrame = ns.EditorFrame

  /**
   * Constants
   */

  const ACTION_ICON  = 'coral-Icon--infoCircle'
  const ACTION_TITLE = Granite.I18n.get('Component Info')
  const ACTION_NAME  = 'AEM_DESIGN_COMPONENT_INFO'

  /**
   * Generates a new Coral UI dialog.
   */
  function buildDialog({ closable, componentName, content, header, id, interaction, variant }) {
    if (content && !(content instanceof HTMLElement)) {
      const contentElement = new Coral.Dialog.Content()
      contentElement.innerHTML = content

      content = contentElement
    }

    return new Coral.Dialog().set({
      id,

      header: header || {
        innerHTML: `${ACTION_TITLE} (${componentName})`,
      },

      content: content || {
        innerHTML: content,
      },

      footer: {
        innerHTML: `
          <button is="coral-button" variant="primary" class="coral-Button coral-Button--primary" size="M" coral-close>
            <coral-button-label>Close</coral-button-label>
          </button>
        `,
      },

      closable    : closable    || 'on',
      interaction : interaction || 'off',
      variant     : variant     || 'info',
    })
  }

  /**
   * Create a waiting element that uses the CoralUI waiting component.
   */
  function createWaitingElement() {
    const waitingElement = document.createElement('div')

    waitingElement.style.alignItems     = 'center'
    waitingElement.style.display        = 'flex'
    waitingElement.style.height         = '100px'
    waitingElement.style.justifyContent = 'center'
    waitingElement.style.width          = '100%'

    waitingElement.appendChild(new Coral.Wait())

    return waitingElement
  }

  /**
   * Generate a new Sling request for the given `url`.
   */
  function makeSlingRequest(url) {
    return window.fetch(url).then((response) => response.json())
  }

  /**
   * Handle the JSON response sent back from the Sling request.
   */
  function handleSlingResponses(responses, editable, componentElement, dialog) {
    const componentResponse = responses[0]
    const contentResponse   = responses[1]

    dialog.content.innerHTML = `
      <table is="coral-table" style="width: 800px;">
        <tbody is="coral-table-body" divider="cell">
          <tr is="coral-table-row">
            <th is="coral-table-headercell" width="140">${Granite.I18n.get('Resource Name')}</th>
            <td is="coral-table-cell">
              ${editable.path.substr(editable.path.lastIndexOf('/') + 1)}
            </td>
          </tr>
          <tr is="coral-table-row">
            <th is="coral-table-headercell">${Granite.I18n.get('Resource Type')}</th>
            <td is="coral-table-cell">${editable.type}</td>
          </tr>
          <tr is="coral-table-row">
            <th is="coral-table-headercell">${Granite.I18n.get('Super Resource Type')}</th>
            <td is="coral-table-cell">${componentResponse['sling:resourceSuperType'] || 'n/a'}</td>
          </tr>
          <tr is="coral-table-row">
            <th is="coral-table-headercell">${Granite.I18n.get('Resource Path')}</th>
            <td is="coral-table-cell">${editable.path}</td>
          </tr>
          <tr is="coral-table-row">
            <th is="coral-table-headercell">${Granite.I18n.get('Component Path')}</th>
            <td is="coral-table-cell">${editable.path.split('jcr:content/')[1]}</td>
          </tr>
          <tr is="coral-table-row">
            <th is="coral-table-headercell">${Granite.I18n.get('Component Group')}</th>
            <td is="coral-table-cell">${componentResponse['componentGroup'] || 'n/a'}</td>
          </tr>
          <tr is="coral-table-row">
            <th is="coral-table-headercell">${Granite.I18n.get('Component ID')}</th>
            <td is="coral-table-cell">${contentResponse['componentId'] || 'n/a'}</td>
          </tr>
        </tbody>
      </table>

      <h2 class="coral-Heading coral-Heading--2 margin-t-2">Attributes</h2>
      <table is="coral-table" style="width: 800px;">
        <tbody is="coral-table-body" divider="cell">
        ${Array.prototype.slice.call(componentElement.attributes).map((attr) => `
          <tr is="coral-table-row">
            <th is="coral-table-headercell" width="200">${attr.nodeName}</th>
            <td is="coral-table-cell">${attr.value}</td>
          </tr>
        `).join('')}
        </tbody>
      </table>
    `

    dialog.center()
  }

  /**
   * Handle any Sling requests with invalid data.
   */
  function handleInvalidSlingResponse(error, dialog) {
    dialog.content.innerHTML = `
      <strong>${Granite.I18n.get('Unexpected error:')}</strong> ${error}
    `

    dialog.variant = 'error'
  }

  /**
   * Generates a dialog for the component information.
   */
  function generateDialogForComponent(editable) {
    const componentElement = editable.dom.get(0).querySelector('[component]')

    let dialog = null

    if (!componentElement) {
      if (!window.componentInfoErrorDialog) {
        dialog = buildDialog({
          content : Granite.I18n.get('Unable to show the component information due to an unknown error!'),
          header  : Granite.I18n.get(`${ACTION_TITLE} - unexpected error`),
          id      : 'component-info-error',
          variant : 'error',
        })

        window.componentInfoErrorDialog = dialog
      } else {
        window.componentInfoErrorDialog.show()
      }
    }

    if (componentElement) {
      if (!componentElement.dialog) {
        const contentElement = new Coral.Dialog.Content()
        contentElement.appendChild(createWaitingElement())

        dialog = buildDialog({
          componentName : editable.type.substr(editable.type.lastIndexOf('/') + 1),
          content       : contentElement,
          id            : `${componentElement.id}-dialog`,
        })

        // Retrieve some additional information about the component from Sling
        Promise.all([
          makeSlingRequest(`/apps/${editable.type}.json`),
          makeSlingRequest(`${editable.path}.json`),
        ])
          .then((responses) => handleSlingResponses(responses, editable, componentElement, dialog))
          .catch(error => handleInvalidSlingResponse(error, dialog))

        componentElement.dialog = dialog
      } else {
        componentElement.dialog.show()
      }
    }

    if (dialog) {
      document.body.appendChild(dialog)
      dialog.show()
    }
  }

  /**
   * Defines the 'ComponentInfo' Toolbar Action
   *
   * @type Granite.author.ui.ToolbarAction
   * @alias ComponentInfo
   */
  const componentInfoAction = new ns.ui.ToolbarAction({
    icon: ACTION_ICON,
    name: ACTION_NAME,
    text: ACTION_TITLE,

    condition  : (editable) => editable && editable.dom && editable.type.indexOf('aemdesign') === 0,
    execute    : generateDialogForComponent,
    isNonMulti : true,
  })

  // When the Edit Layer gets activated
  channel.on('cq-layer-activated', (event) => {
    if (event.layer === 'Edit') {
      EditorFrame.editableToolbar.registerAction(ACTION_NAME, componentInfoAction)
    }
  })

}(jQuery, Granite.author, jQuery(document), this));
