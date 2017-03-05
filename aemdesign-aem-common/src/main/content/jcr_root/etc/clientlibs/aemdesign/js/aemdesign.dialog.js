//aemdesign.dialog.js
window.AEMDESIGN = window.AEMDESIGN || {};
window.AEMDESIGN.dialog = window.AEMDESIGN.dialog || {};
(function ($, ns, utils, log, CQ, window, undefined) {

    "use strict";
    var _version = "0.1";

    ns.version = function () {
        return _version;
    };

    ns.silentlySetToDefault = function (component) {
        try {
            if (component.defaultValue != null) {
                component.setValue(component.defaultValue);
            }
        } catch (ex) {
            log.error(["silentlySetToDefault",component,ex]);
        }
    };

    ns.getListFromSource = function (source) {
        try {
            var response = CQ.HTTP.get(source);
            if (CQ.utils.HTTP.isOk(response)) {
                return CQ.Util.eval(response);
            }
        } catch (ex) {
            CQ.Log.error('Could not load list: ' + CQ.utils.HTTP.HEADER_MESSAGE + ';\n' + ex.message);
            log.error(["getListFromSource",source,CQ,ex]);
        }
        return [];
    };


    ns.resetTextValues = function (container) {
        try {
            //set all textField component values to their defaultValue
            CQ.Ext.each(container.findByType('textfield'), function (component) {
                AEMDESIGN.dialog.silentlySetToDefault(component);
            });
        } catch (ex) {
            log.error(["resetTextValues",container,CQ,ex]);
        }
    };

    ns.resetFieldValues = function (container) {
        try {
            //set all non textField component values to their defaultValue
            CQ.Ext.each([].concat(container.findByType('selection'),
                container.findByType('spinner'),
                container.findByType('checkbox')
            ), function (component) {
                AEMDESIGN.dialog.silentlySetToDefault(component);
            });
        } catch (ex) {
            log.error(["resetFieldValues",container,CQ,ex]);
        }
    };

    ns.manageTabs = function (tabPanels, tabToSelectName, noSwitch, noHide) {
        try {
            var tabsFound = tabPanels.find("name",tabToSelectName);
            var tabToSelect;
            if (tabsFound.length == 0) {
                //quick fail
                return;
            } else {
                tabToSelect = tabsFound[0];
            }

            if (tabToSelect) {
                if (!noHide) {
                    for (var index in tabPanels.hiddenTabs) {
                        if (tabToSelect == tabPanels.hiddenTabs[index]) {
                            tabPanels.unhideTabStripItem(tabPanels.hiddenTabs[index]);
                        } else {
                            tabPanels.hideTabStripItem(tabPanels.hiddenTabs[index]);
                        }
                    }
                }
                tabPanels.doLayout();

                if (!noSwitch) {
                    tabPanels.activate(tabToSelect);
                }
            }
        } catch (ex) {
            //console.log(["error","manageTabs",tabPanels, tabPanels.hiddenTabs, tabToSelectName, noSwitch, noHide, ex])
            log.error(["manageTabs",tabPanels, tabPanels.hiddenTabs, tabToSelectName, noSwitch, noHide, ex]);
        }
    };


    ns.hideTab = function (tabToHide, tabPanels) {
        try {
            //init array if not there
            if(!tabPanels.hiddenTabs){
                tabPanels.hiddenTabs=[];
            }
            //remember this tab for unhiding
            tabPanels.hiddenTabs.push(tabToHide);
            //hide this tab
            tabPanels.hideTabStripItem(tabToHide);
        } catch (ex) {
            log.error(["hideTab", tabToHide, tabPanels, ex]);
        }
    };

    ns.getConfigSource = function (dialog, config) {
        try {
            if (config != '') {
                return dialog.configPath + config + dialog.configExt;
            }
        } catch (ex) {
            log.error(["getConfigSource", dialog, config, ex]);
        }
    };

    ns.loadSelectionOptions = function (selectionComponent, source, selectDefault) {
        try {
            var defaultItem = null;
            var options = AEMDESIGN.dialog.getListFromSource(source);

            if (selectDefault) {
                defaultItem = utils.findItem(options, "default", true);
            }

            selectionComponent.setOptions(options);

            //set default values
            if (selectDefault && defaultItem) {
                selectionComponent.setValue(defaultItem.value);
                selectionComponent.defaultValue = defaultItem.value;
            }
        } catch (ex) {
            log.error(["loadSelectionOptions", selectionComponent, source, selectDefault, ex]);
        }
    };

    ns.verifyAction = function (value, action) {
        try {
            if (action.indexOf("link") > -1) {
                if (value.substring(0, 7) == "http://") {
                    return true;
                } else {
                    return "Link must start with http://";
                }
            }
        } catch (ex) {
            log.error(["verifyAction", value, action, ex]);
        }
    };


    ns.generateElementNameFromTitle = function (component) {
        try {

            if (component === 'undefined' || component === null) {
                return;
            }

            var elementValue = component.getValue();
            var values = elementValue.split(/\s+/g);

            if (values.length > 1) {
                elementValue = utils.camelCaseConvert(values).join("");
            }

            component.findParentByType('panel').findBy(function () {
                if (this.name == './elementName') {
                    this.setValue(elementValue);
                }
            });

        } catch (ex) {
            log.error(["generateElementNameFromTitle", component, ex]);
        }

    }

})(jQuery, AEMDESIGN.dialog, AEMDESIGN.utils, AEMDESIGN.http,  AEMDESIGN.log, CQ, this);
