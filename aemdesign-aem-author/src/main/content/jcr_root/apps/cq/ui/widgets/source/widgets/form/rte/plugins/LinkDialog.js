/*************************************************************************
*
* ADOBE CONFIDENTIAL
* ___________________
*
*  Copyright 2012 Adobe Systems Incorporated
*  All Rights Reserved.
*
* NOTICE:  All information contained herein is, and remains
* the property of Adobe Systems Incorporated and its suppliers,
* if any.  The intellectual and technical concepts contained
* herein are proprietary to Adobe Systems Incorporated and its
* suppliers and are protected by trade secret or copyright law.
* Dissemination of this information or reproduction of this material
* is strictly forbidden unless prior written permission is obtained
* from Adobe Systems Incorporated.
**************************************************************************/

/**
 * @class CUI.rte.plugins.LinkDialog
 * @extends CQ.form.rte.ui.BaseWindow
 * @private
 * The LinkDialog is a dialog for creating a link.
 * @constructor
 * Creates a new LinkDialog.
 * @param {Object} config The config object
 */
CQ.form.rte.plugins.LinkDialog = CQ.Ext.extend(CQ.form.rte.ui.BaseWindow, {

    constructor: function(config) {
        config = config || { };
        var defaults = {
            "title": CQ.I18n.getMessage("Hyperlink"),
            "modal": true,
            "width": 400,
            "height": 160,
            "dialogItems": [ {
                    "itemId": "href",
                    "name": "href",
                    "parBrowse": true,
                    "anchor": CQ.themes.Dialog.ANCHOR,
                    "fieldLabel": CQ.I18n.getMessage("Link to"),
                    "xtype": "pathfield",
                    "ddGroups": [
                        CQ.wcm.EditBase.DD_GROUP_PAGE,
                        CQ.wcm.EditBase.DD_GROUP_ASSET
                    ],
                    "fieldDescription": CQ.I18n.getMessage("Drop files or pages from the Content Finder"),
                    "listeners": {
                        "dialogselect": {
                            "fn": this.selectAnchor,
                            "scope": this
                        },
                        "render": this.initHrefDragAndDrop
                    },
                    "validator": CUI.rte.Utils.scope(this.validateLink, this),
                    "validationEvent": "keyup",
                    "escapeAmp": true
                }, {
                    "itemId": "targetBlank",
                    "name": "targetBlank",
                    "xtype": "checkbox",
                    "boxLabel": CQ.I18n.getMessage("Open in new window"),
                    "value": "targetBlank"
                }
            ]
        };
        CQ.Util.applyDefaults(config, defaults);
        CQ.form.rte.plugins.LinkDialog.superclass.constructor.call(this, config);
    },

    /**
     * @private
     */
    selectAnchor: function(pathfield, path, anchor) {
        // custom path + anchor handling
        path = CQ.HTTP.encodePath(path);
        // encodePath will not encode '&', so we're doing it here, as other callees of
        // encodePath might rely on that documented behaviour - see bug #30206
        path = path.replace(/&/g, "%26");
        if (anchor && (anchor.length > 0)) {
            path += ".html#" + anchor;
        }
        pathfield.setValue(path);
    },

    /**
     * <p>Note that this method is executed in the scope of the pathfield.</p>
     * @private
     */
    initHrefDragAndDrop: function() {
        if (this.ddGroups) {
            if (typeof(this.ddGroups) == "string") {
                this.ddGroups = [ this.ddGroups ];
            }
            var field = this;
            var target = new CQ.wcm.EditBase.DropTarget(this.el, {
                "notifyDrop": function(dragObject, evt, data) {
                    if (dragObject && dragObject.clearAnimations) {
                        dragObject.clearAnimations(this);
                    }
                    if (dragObject.isDropAllowed(this)) {
                        if (data.records && data.single) {
                            var record = data.records[0];
                            var path = record.get("path");
                            path = CQ.HTTP.encodePath(path);
                            // again, '&' needs to be encoded explicitly - see bug #30206
                            path = path.replace(/&/g, "%26");
                            field.setValue(path);
                            evt.stopEvent();
                            return true;
                        }
                        return false;
                    }
                }
            });

            var dialog = this.findParentByType(CQ.form.rte.plugins.LinkDialog);
            dialog.on("activate", function(dialog) {
                if (dialog && dialog.el && this.highlight) {
                    var dialogZIndex = parseInt(dialog.el.getStyle("z-index"), 10);
                    if (!isNaN(dialogZIndex)) {
                        this.highlight.zIndex = dialogZIndex + 1;
                    }
                }
            }, target);
            dialog.on("deactivate", function(dialog) {
                if (dialog && dialog.el && this.highlight) {
                    var dialogZIndex = parseInt(dialog.el.getStyle("z-index"), 10);
                    if (!isNaN(dialogZIndex)) {
                        this.highlight.zIndex = dialogZIndex + 1;
                    }
                }
            }, target);
            var editorKernel = dialog.getParameter("editorKernel");
            dialog.on("show", function() {
                editorKernel.fireUIEvent("preventdrop");
                CQ.WCM.registerDropTargetComponent(field);
            }, target);
            dialog.on("hide", function() {
                CQ.WCM.unregisterDropTargetComponent(field);
                editorKernel.fireUIEvent("reactivatedrop");
            }, target);

            for (var i = 0; i < this.ddGroups.length; i++) {
                target.addToGroup(this.ddGroups[i]);
            }
            target.removeFromGroup(CQ.wcm.EditBase.DD_GROUP_DEFAULT);
            this.dropTargets = [ target ];
        }
    },

    /**
     * Gets a field with the provided key from this panel.
     *
     * @param {String} key Field name
     */
    getField: function(key) {
        var items = this.find("name", "./" + key);
        if( (CUI.rte.Utils.isArray(items)) && (items.length > 0) )
            return items[0];
    },

    preprocessModel: function() {
        var showAdvanced = false;
        if (this.objToEdit && this.objToEdit.dom) {
            this.objToEdit.href = CUI.rte.HtmlRules.Links.getLinkHref(
                    this.objToEdit.dom);
            var com = CUI.rte.Common;
            var attribNames = com.getAttributeNames(this.objToEdit.dom, false,
                function(dom, attribName, attribNameLC) {
                    // exclude href, rte_href & target from generic attribute handling, as
                    // they are handled explicitly and not genrically
                    return attribNameLC == com.HREF_ATTRIB || attribNameLC == "href"
                            || attribNameLC == "target";
                });
            for (var i = 0; i < attribNames.length; i++) {
                var attribName = attribNames[i];
                var value = com.getAttribute(this.objToEdit.dom, attribName);
                if (typeof value !== 'undefined') {
                    this.objToEdit.attributes[attribName] = value;
                }
            }
            if (this.objToEdit.attributes.onclick) {
                showAdvanced = true;
            }
        }
        var advancedField = this.getField("linkdialog/cq:adhocLinkTrackingTab");
        if (advancedField) {
            if (showAdvanced) {
                advancedField.expand(false);
            } else {
                advancedField.collapse(false);
            }
        }
    },

    dlgFromModel: function() {
        var hrefField = this.getFieldByName("href");
        if (hrefField) {
            var value = "";
            if (this.objToEdit) {
                var href = this.objToEdit.href;
                if (href) {
                    value = href;
                }
            }
            // using setRawValue to bypass validation initially
            hrefField.setRawValue(value);
            // reset potential previous invalid state (dialog is being reused)
            hrefField.clearInvalid();
        }
        var targetBlankField = this.getFieldByName("targetBlank");
        if (targetBlankField) {
            var target = (this.objToEdit && this.objToEdit.target
                    ? this.objToEdit.target.toLowerCase() : null);
            targetBlankField.setValue(target == "_blank");
        }
        if (typeof CQ_Analytics !== 'undefined' && typeof CQ_Analytics.adhocLinkTracking !== 'undefined' && CQ_Analytics.adhocLinkTracking == "true") {
            var enableField = this.getField("linkdialog/cq:adhocLinkTrackingEnableTracking");
            var eventsField = this.getField("linkdialog/cq:adhocLinkTrackingEvents");
            var evarsField = this.getField("linkdialog/cq:adhocLinkTrackingEvars");
            if (eventsField && evarsField) {
                var events = "", evars = "", enable = false;
                if (this.objToEdit) {
                    var value = this.objToEdit.attributes.adhocevents;
                    if (typeof value !== 'undefined') {
                        events = value;
                    }
                    value = this.objToEdit.attributes.adhocevars;
                    if (typeof value !== 'undefined') {
                        evars = value;
                    }
                    value = this.objToEdit.attributes.adhocenable;
                    if (typeof value !== 'undefined') {
                        enable = value == "true";
                    }
                }
                eventsField.setValue(events);
                evarsField.setValue(evars);
                if (enable) {
                    enableField.setValue(enable);
                }
                this.enableSCFields(enable);
            }
        }
        else {
            var trackBox = this.getField("linkdialog/cq:adhocLinkTrackingTab");
            if (trackBox) {
                trackBox.hide();
            }
        }

        var titleField = this.getFieldByName("title");
        if (titleField) {
            var value = "";
            if (this.objToEdit) {
                var ovalue = this.objToEdit["title"];
                if (ovalue) {
                    value = ovalue;
                }
            }
            // using setRawValue to bypass validation initially
            titleField.setRawValue(value);
            // reset potential previous invalid state (dialog is being reused)
            titleField.clearInvalid();
        }

        var cssField = this.getFieldByName("cssClass");
        if (cssField) {
            var value = "";
            if (this.objToEdit) {
                var ovalue = this.objToEdit["class"];
                if (ovalue) {
                    value = ovalue;
                }
            }
            // using setRawValue to bypass validation initially
            cssField.setRawValue(value);
            // reset potential previous invalid state (dialog is being reused)
            cssField.clearInvalid();
        }

        var targetField = this.getFieldByName("dataTarget");
        if (targetField) {
            var value = "";
            if (this.objToEdit) {
                var ovalue = this.objToEdit["data-target"];
                if (ovalue) {
                    value = ovalue;
                }
            }
            // using setRawValue to bypass validation initially
            targetField.setRawValue(value);
            // reset potential previous invalid state (dialog is being reused)
            targetField.clearInvalid();
        }

    },

    dlgToModel: function() {
        if (this.objToEdit) {
            var hrefField = this.getFieldByName("href");
            if (hrefField) {
                var href = hrefField.getValue();
                if (href) {
                    this.objToEdit.href = href;
                }
            }
            var targetBlankField = this.getFieldByName("targetBlank");
            if (targetBlankField) {
                if (targetBlankField.getValue()) {
                    this.objToEdit.target = "_blank";
                } else {
                    this.objToEdit.target = null;
                }
            }
            var enableField = this.getField("linkdialog/cq:adhocLinkTrackingEnableTracking");
            var eventsField = this.getField("linkdialog/cq:adhocLinkTrackingEvents");
            var evarsField = this.getField("linkdialog/cq:adhocLinkTrackingEvars");
            if (enableField && eventsField && evarsField) {
                this.objToEdit.attributes.adhocenable = enableField.getValue() ? "true" : "false";
                this.objToEdit.attributes.adhocevents = eventsField.getValue() || "";
                this.objToEdit.attributes.adhocevars = evarsField.getValue() || "";
                if (enableField.getValue()) {
                    this.objToEdit.attributes.onclick = "CQ_Analytics.Sitecatalyst.customTrack(this)";
                } else {
                    delete this.objToEdit.attributes.onclick;
                    if (this.objToEdit.dom && this.objToEdit.dom.attributes) {
                        CUI.rte.Common.removeAttribute(this.objToEdit.dom, "onclick");
                    }
                }
            }

            var titleField = this.getFieldByName("title");
            if (titleField) {
                var ovalue = titleField.getValue();
                if (ovalue) {
                    this.objToEdit.attributes.title = ovalue;
                }
            }

            var cssField = this.getFieldByName("cssClass");
            if (cssField) {
                var ovalue = cssField.getValue();
                if (ovalue) {
                    this.objToEdit.attributes.class = ovalue;
                }
            }

            var targetField = this.getFieldByName("dataTarget");
            if (targetField) {
                var ovalue = targetField.getValue();
                if (ovalue) {
                    this.objToEdit.attributes["data-target"] = ovalue;
                }
            }

        }
    },

    enableSCFields: function(state) {
        var eventsField = this.getField("linkdialog/cq:adhocLinkTrackingEvents");
        var evarsField = this.getField("linkdialog/cq:adhocLinkTrackingEvars");
        if(state) {
            eventsField.enable();
            evarsField.enable();
        } else {
            eventsField.disable();
            evarsField.disable();
        }
    },

    postprocessModel: function() {
        var linkRules = this.parameters.linkRules;
        if (linkRules) {
            linkRules.applyToObject(this.objToEdit);
        }
    },

    validateLink: function(href) {
        var linkRules = this.getParameter("linkRules");
        if (!linkRules) {
            return (href.length > 0 ? true : CQ.I18n.getMessage("No link provided"));
        }
        var isValid = linkRules.validateHref(href);
        return isValid || CQ.I18n.getMessage("Invalid link");
    }

});

// register LinkDialog component as xtype
CQ.Ext.reg("rtelinkdialog", CQ.form.rte.plugins.LinkDialog);