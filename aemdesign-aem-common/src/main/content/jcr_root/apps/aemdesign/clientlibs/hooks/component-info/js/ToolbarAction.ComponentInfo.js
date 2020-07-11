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

  const IGNORED_SLING_PROPERTIES = [
    'editable',
    'textIsRich',
  ]

  /**
   * Generates a new Coral UI dialog.
   *
   * @return {Coral.Dialog & HTMLElement}
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
        innerHTML: `${ACTION_TITLE} (<span>${componentName}</span>)`,
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
   *
   * @return {HTMLDivElement}
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
   * Filter the given `props` list and remove anything that doesn't need to be displayed.
   *
   * @param {string[]} props
   * @return {string[]} filtered properties
   */
  function filterSlingProperties(props) {
    return props.filter((prop) =>
      (prop.indexOf('cq:') === -1 && prop !== 'cq:tags') &&
      prop.indexOf('jcr:') === -1 &&
      prop.indexOf('sling:') === -1 &&
      IGNORED_SLING_PROPERTIES.indexOf(prop) === -1
    )
  }

  /**
   * Replaces any HTML within the given `input` with an encoded entity to prevent XSS.
   *
   * @param {string} input
   * @return {string}
   */
  function encodeHTMLEntities(input) {
    return input.replace(/[\u00A0-\u9999<>&]/gim, (char) => `&#${char.charCodeAt(0)};`)
  }

  /**
   * Generate a new Sling request for the given `url`.
   *
   * @param {string} url
   * @return {Promise}
   */
  function makeSlingRequest(url) {
    return window.fetch(url).then((response) => response.json())
  }

  /**
   * Create a new tab list tab (label).
   *
   * @param {Coral.TabList & HTMLElement} tablist
   * @param {string} label
   * @param {boolean} selected
   */
  function createTabListTab(tablist, label, selected = false) {
    tablist.items.add({
      label: {
        innerHTML: label,
      },

      selected: selected,
    });
  }

  /**
   * Create a new tab list panel stack item.
   *
   * @param {Coral.PanelStack & HTMLElement} panelStack
   * @param {string} content
   */
  function createTabListPanelStackItem(panelStack, content) {
    panelStack.items.add({
      content: {
        innerHTML: content,
      },
    })
  }

  /**
   * Handle the JSON response sent back from the Sling request.
   */
  function handleSlingResponses(responses, editable, componentElement, dialog) {
    const componentResponse = responses[0]
    const contentResponse   = responses[1]

    // Create the tabs
    const tabList = new Coral.TabList().set({
      target: `${componentElement.id}-tabs`,
    })

    createTabListTab(tabList, Granite.I18n.get('Component'), true)
    createTabListTab(tabList, Granite.I18n.get('Sling Configuration'))
    createTabListTab(tabList, Granite.I18n.get('Attributes'))

    const panelStack = new Coral.PanelStack().set({
      id: `${componentElement.id}-tabs`,
    })

    panelStack.classList.add('coral-Well')

    // Component
    createTabListPanelStackItem(panelStack, `
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
        </tbody>
      </table>
    `)

    // Sling Configuration
    createTabListPanelStackItem(panelStack, `
      <table is="coral-table" style="width: 800px;">
        <tbody is="coral-table-body" divider="cell">
        ${filterSlingProperties(Object.keys(contentResponse)).map((prop) => `
          <tr is="coral-table-row">
            <th is="coral-table-headercell" width="140">${prop}</th>
            <td is="coral-table-cell">${encodeHTMLEntities(contentResponse[prop])}</td>
          </tr>
        `).join('')}
        </tbody>
      </table>
    `)

    // Attributes
    createTabListPanelStackItem(panelStack, `
      <table is="coral-table" style="width: 800px;">
        <tbody is="coral-table-body" divider="cell">
        ${Array.prototype.slice.call(componentElement.attributes).map((attr) => `
          <tr is="coral-table-row">
            <th is="coral-table-headercell" width="200">${attr.nodeName}</th>
            <td is="coral-table-cell">${encodeHTMLEntities(attr.value)}</td>
          </tr>
        `).join('')}
        </tbody>
      </table>
    `)

    const tabView = new Coral.TabView().set({ panelStack, tabList })

    dialog.content.innerHTML = tabView.outerHTML
    dialog.header.querySelector('span').innerText = componentResponse['jcr:title']

    Coral.commons.ready(tabView, () => dialog.center())
  }

  /**
   * Handle any Sling requests with invalid data.
   */
  function handleInvalidSlingResponse(error, dialog) {
    dialog.content.innerHTML = `
      <strong>${Granite.I18n.get('Unexpected error:')}</strong> ${error}
    `

    dialog.variant = 'error'
    dialog.center()
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
