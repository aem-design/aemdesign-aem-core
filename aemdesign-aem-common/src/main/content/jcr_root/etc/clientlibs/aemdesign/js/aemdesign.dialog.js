//aemdesign.dialog.js
window.AEMDESIGN = window.AEMDESIGN || {"jQuery":{}};
window.AEMDESIGN.dialog = window.AEMDESIGN.dialog || {};

(function ($, ns, utils, http, log, CQ, window, undefined) {

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
    ns.manageTabGroup = function (tabPanels, tabToSelectName, tabGroupItems, noSwitch) {
        // console.log(["manageTabGroup",tabPanels, tabToSelectName, tabGroupItems, noSwitch]);
        try {
            //hide all tabs
            for (var item in tabGroupItems) {
                var tabName = tabGroupItems[item].value;
                if (tabGroupItems[item].selectTab) {
                    tabName = tabGroupItems[item].selectTab;
                }
                // console.log(["manageTabGroup",tabGroupItems[item], tabName]);
                if (tabName!=="") {
                    var tabsFound = tabPanels.find("name", tabName);
                    if (tabsFound.length != 0) {
                        tabPanels.hideTabStripItem(tabsFound[0]);
                    }
                }
            }
            //unhide selected
            var tabsFound = tabPanels.find("name",tabToSelectName);
            if (tabsFound.length != 0) {
                tabPanels.unhideTabStripItem(tabsFound[0]);
            }

            //update tabs layout
            tabPanels.doLayout();

            //switch to tab if needed
            if (tabsFound.length != 0) {
                if (!noSwitch) {
                    tabPanels.activate(tabsFound[0]);
                }
            }

        } catch (ex) {
            //log.info(["error","manageTabs",tabPanels, tabPanels.hiddenTabs, tabToSelectName, noSwitch, ex])
            log.error(["manageTabGroup",tabPanels, tabToSelectName, tabGroupItems, noSwitch, ex]);
        }
    };
    ns.manageTabs = function (tabPanels, tabToSelectName, noSwitch, noHide) {
        try {
            //find tab by name
            var tabsFound = tabPanels.find("name",tabToSelectName);
            var tabToSelect;
            //select first tab if not found
            if (tabsFound.length == 0) {
                if(tabPanels.includeTabs){
                    tabToSelect = tabPanels.includeTabs[0];
                    tabPanels.unhideTabStripItem(tabPanels.includeTabs[0]);
                }
            } else {
                //select firs found tab for selection
                tabToSelect = tabsFound[0];
            }

            if (tabToSelect) {

                if (!noHide) {
                    for (var index in tabPanels.hiddenTabs) {
                        if (tabToSelect == tabPanels.hiddenTabs[index]) {
                            if (tabToSelect.isVisible() == false) {
                                tabPanels.unhideTabStripItem(tabPanels.hiddenTabs[index]);
                            }
                        } else {
                            tabPanels.hideTabStripItem(tabPanels.hiddenTabs[index]);
                        }
                    }
                }
                //update tabs layout
                tabPanels.doLayout();

                //switch to tab if needed
                if (!noSwitch) {
                    tabPanels.activate(tabToSelect);
                }
            }
        } catch (ex) {
            //log.info(["error","manageTabs",tabPanels, tabPanels.hiddenTabs, tabToSelectName, noSwitch, noHide, ex])
            log.error(["manageTabs",tabPanels, tabPanels.hiddenTabs, tabToSelectName, noSwitch, noHide, ex]);
        }
    };


    ns.includeTab = function (tabToInclude, tabPanels) {
        try {
            //init array if not there
            if(!tabPanels.includeTabs){
                tabPanels.includeTabs=[];
            }
            //remember this tab for unhiding
            tabPanels.includeTabs.push(tabToInclude);
        } catch (ex) {
            log.error(["useTab", tabToInclude, tabPanels, ex]);
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

    };

    ns.checkDialogPermissions = function (permissionsTab, dialog) {
        //console.log(["checkDialogPermissions",permissionsTab, dialog]);
        var hideTab = false;
        if (permissionsTab.find("name","permissionCheckTabAccessCheck").length==0) {
            var fields = CQ.Util.findFormFields(dialog);
            var lockFieds = true;
            var isLocked = fields["./islocked"];
            //console.log(["isLocked",isLocked,isLocked[0]["value"],isLocked[0]["value"]=="false"]);
            if (isLocked) {
                if (isLocked.length != 0) {
                    if (isLocked[0]["value"] == "false") {
                        lockFieds = false;
                        hideTab=true;
                    }
                }
            }

            if (lockFieds) {
                var items = dialog.buttons;
                dialog.editLock = true;
                for (var item in items) {
                    if (items[item]["text"] == dialog.okText) {
                        items[item].setDisabled(true);
                    }
                }

                CQ.utils.Util.disableFields(dialog);

            }
        } else {
            hideTab = true;
        }

        if (hideTab) {
            dialog.findByType('tabpanel')[0].setActiveTab(1);
            dialog.findByType('tabpanel')[0].hideTabStripItem("permissionCheckTab");
        } else {
            dialog.findByType('tabpanel')[0].setActiveTab("permissionCheckTab");
            dialog.findByType('tabpanel')[0].unhideTabStripItem("permissionCheckTab");

        }
    };

})(AEMDESIGN.jQuery, AEMDESIGN.dialog, AEMDESIGN.utils, AEMDESIGN.http,  AEMDESIGN.log, CQ, this);
