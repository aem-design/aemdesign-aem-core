/*
 * Copyright 1997-2008 Day Management AG
 * Barfuesserplatz 6, 4001 Basel, Switzerland
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Day Management AG, ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Day.
 */

/**
 * @class CQ.dam.form.Metadata
 * @extends CQ.form.CompositeField
 * <p>Metadata provides a set of fields to determine the information
 * required for a metadata field as used e.g. on asset editor pages.</p>
 * <p>It provides the following fields:</p><ul>
 * <li><b>Field Label</b><br>
 * The label displayed in the form.</li>
 * <li><b>Namespace</b><br>
 * The namespace part of the metadata name. The options are provided by default from
 * "/libs/dam/options/metadata".</li>
 * <li><b>Local Part</b><br>
 * The local part of the metadata name. The options depend on the selected namespace and are provided by
 * the options (see namespace).</li>
 * <li><b>Qualified Name</b><br>
 * A read-only field that displays the final metadata name consisting of namespace and local part.</li>
 * <li><b>Type</b><br>
 * The type of the metadata. If an option is providing the type this field will be adjusted when
 * selecting the local part  e.g. changes to "Date" when selecting "dc:date".</li>
 * <li><b>Multi Value</b><br>
 * A checkbox to define if the metadata is a multi value propterty. Like the type this value
 * can be provided by the selected option.</li>
 * @constructor
 * Creates a new Metadata Field.
 * @param {Object} config The config object
 * @xtype metadata
 * @since 5.3
 */
CQ.dam.form.Metadata = CQ.Ext.extend(CQ.form.CompositeField, {

    /**
     * @cfg {String} labelParameter
     * Name of the field label property (defaults to "label").
     */
    labelParameter: "label",

    /**
     * @cfg {String} namespaceParameter
     * Name of the namespace property (defaults to "namespace").
     */
    namespaceParameter: "namespace",

    /**
     * @cfg {String} localPartParameter
     * Name of the local part property (defaults to "localPart").
     */
    localPartParameter: "localPart",

    /**
     * @cfg {String} typeParameter
     * Name of the type property (defaults to "type").
     */
    typeParameter: "type",

    /**
     * @cfg {String} multivalueParameter
     * Name of the multi value property (defaults to "multivalue").
     */
    multivalueParameter: "multivalue",

    /**
     * @cfg {String} defaultNamespace
     * The default value of the namespace field. Defaults to "dc" (Dublin Core).
     */
    defaultNamespace: "dc",

    /**
     * @cfg {String} defaultType
     * The default value of the type field (defaults to "String").
     */
    defaultType: "String",

    /**
     * @cfg {String} addFieldsToParent
     * Because of a layout issue inside a dialog the fields must be added
     * directly to the parent panel (which typically is a tab panel).
     * <code>addFieldsToParent</code> indicates if the fields should be added to the
     * or not. Defaults to true.
     * @private
     */
    addFieldsToParent: true,

    /**
     * @cfg {String} options
     * <p>The URL where the options are requested from
     * (defaults to "/libs/dam/options/metadata.overlay.2.json").</p>
     * <p>To customize the options either overlay it in
     * "/apps/dam/options/metadata" or provide custom options by by adjusting
     * this config property.</p>
     * <p>Sample:
     * <pre><code>
     {
          dc: {
              "jcr:title": "XMP Dublin Core",
              "description": {
                  "type": "String"
              },
              "date": {
                  "date": "Date",
                  "multivalue": true
              }
     }
            </code></pre></p>
          */
    options: null,

    /**
     * @cfg {String} constraintFieldName
     * Name of the constraint field in the dialog. If the dialog holding the Metadata contains
     * a field of this name the field will automatically be updated according to {@link #constraintsMap}.
     * Defaults to "./constraintType".
     */
    constraintFieldName: "./constraintType",

    /**
     * @cfg {Object} constraintsMap
     * Some types of metadata add automatically a constraint if a constraint field
     * is existing in the dialog. The keys correspond to the type. Defaults to:
     * <pre><code>
     {
        "Date": "foundation/components/form/constraints/date",
        "Long": "foundation/components/form/constraints/numeric"
     }
     </code></pre></p>
     */
    constraintsMap: {
        "Date": "foundation/components/form/constraints/date",
        "Long": "foundation/components/form/constraints/numeric"
    },

    // overriding CQ.form.CompositeField#processRecord
    processRecord: function(record, path) {
        if (this.fireEvent('beforeloadcontent', this, record, path) !== false) {
            var v = record.get(this.getName());

            if (v == undefined) {
                if (this.defaultNamespace) {
                    this.namespaceField.setValue(this.defaultNamespace);
                    this.localPartField.setOptions(this.getLocalPartOptions(this.defaultNamespace));
                    this.setType(this.defaultType);
                }
            }
            else {
                this.labelField.setValue(v.label);
                this.namespaceField.setValue(v.namespace);
                this.localPartField.setOptions(this.getLocalPartOptions(v.namespace));
                this.localPartField.setValue(v.localPart);
                this.setType(v.type);
                this.multivalueField.setValue(v.multivalue);
                this.setQualified();
            }

            this.fireEvent('loadcontent', this, record, path);
        }
    },

    initComponent: function() {
        CQ.dam.form.Metadata.superclass.initComponent.call(this);
        this.localPartOptions = {};
        var nsOptions = [];
        if (typeof this.options == "string") {
            try {
                this.options = CQ.HTTP.eval(this.options);
                var regNs = CQ.HTTP.eval("/libs/dam/namespaces.json");
                this.regNamespaces = regNs.namespaces;
                for (var name in this.options) {
                    if (typeof this.options[name] == "object") {
                        //todo: check for metadata nodetype?
                        // ns is a namespace (otherwise property like jcr:title)
                        var title = this.options[name]["jcr:title"];
                        if (this.regNamespaces.indexOf(name) != -1) {
                            nsOptions.push({
                                "value": name,
                                "qtip": title ? title : ""
                            });
                        }
                    }
                }
            }
            catch (e) {
                CQ.Log.warn("CQ.WCM#getDialogConfig failed: " + e.message);
                this.options = {};
            }
        }
        else {
            //todo: cfg options as array resp. object?
        }

        var m = this;

        this.labelField = new CQ.Ext.form.TextField({
            "fieldLabel": "Field Label",
            "name": this.name + "/" + this.labelParameter,
            "ignoreData": true,
            "fieldDescription": CQ.I18n.getMessage("Leave empty to use the local part", [], "sample: 'dc:title' - 'dc' is the namespace, 'title' the localpart")
        });

        nsOptions.sort();
        nsOptions.sort(function(a, b) {
            var va = a.value.toLowerCase();
            var vb = b.value.toLowerCase();
            if (va < vb) {
                return -1;
            } else if (va == vb) {
                return 0;
            } else {
                return 1;
            }
        });

        this.namespaceField = new CQ.form.Selection({
            "fieldLabel": "Namespace",
            "name": this.name + "/" + this.namespaceParameter,
            "type": "select",
            "ignoreData": true,
            "options": nsOptions,
            "listeners": {
                "selectionchanged": {
                    "fn": m.changeNamespace,
                    "scope": m
                }
            }
        });

        this.localPartField = new CQ.form.Selection({
            "fieldLabel": "Local Part",
            "name": this.name + "/" + this.localPartParameter,
            "type": "combobox",
            "fieldDescription": CQ.I18n.getMessage("Select a namespace first to receive the accordant local parts" , [], "two select boxes; after selecting a namespace all possible local parts are loaded into the second select box"),
            "ignoreData": true,
            "allowBlank": false,
            "vtype": this.vtype,
            "listeners": {
                "selectionchanged": {
                    "fn": m.changeLocalPart,
                    "scope": m
                }
            }
        });

        this.qualifiedField = new CQ.Ext.form.TextField({
            "fieldLabel": "Qualified Name",
            "readOnly": true,
            "fieldDescription": CQ.I18n.getMessage("Generated from namespace and local part", [], "sample: 'dc:title' - 'dc' is the namespace, 'title' the localpart"),
            "ignoreData": true
        });

        this.typeField = new CQ.form.Selection({
            "fieldLabel": "Type",
            "name": this.name + "/" + this.typeParameter,
            "type": "select",
            "ignoreData": true,
            "options": [{
                "value": "String",
                "text": "String"
            },{
                "value": "Long",
                "text": "Number"
            },{
                "value": "Date",
                "text": "Date"
            },{
                "value": "Boolean",
                "text": "Boolean"
            }
            ],
            "listeners": {
                "selectionchanged": function() {
                    m.setConstraint(this.getValue());
                }
            }
        });

        this.multivalueField = new CQ.form.Selection({
            "fieldLabel": "",
            "name": this.name + "/" + this.multivalueParameter,
            "type": "checkbox",
            "ignoreData": true,
            "inputValue": "true",
            "boxLabel": CQ.I18n.getMessage("Property is multi value")
        });

    },

    // private
    afterRender : function(){
        CQ.dam.form.Metadata.superclass.afterRender.call(this);

        // add fields to the tab panel (layout issue)
        var panel = this.addFieldsToParent ? this.findParentByType("panel") : this;
        if (!panel) panel = this;
        panel.add(this.labelField);
        panel.add(this.namespaceField);
        panel.add(this.localPartField);
        panel.add(this.qualifiedField);
        panel.add(this.typeField);
        panel.add(this.multivalueField);
    },

    /**
     * Returns the selected namespace.
     * @return {String} The selected namespace
     */
    getNamespace: function() {
        return this.namespaceField.getValue();
    },

    /**
     * Returns the selected local part.
     * @return {String} The selected local part
     */
    getLocalPart: function() {
        return this.localPartField.getValue();
    },

    /**
     * Returns the local parts of the specified namespace as options.
     * @param {String} namespace The name of the namespace
     * @private
     */
    getLocalPartOptions: function(namespace) {
        var o = [];
        if (this.localPartOptions[namespace]) {
            return this.localPartOptions[namespace];
        }
        else {
            var ns = this.options[namespace];
            if (ns) {
                for (var name in ns) {
                    if (typeof ns[name] == "object") {
                        //todo: check for metadata nodetype?
                        // lp is a local part (otherwise property like jcr:title)
                        var title = ns[name]["jcr:title"];
                        o.push({
                            "value": name,
                            "qtip": title ? title : ""
                        });
                    }
                }
                o.sort(function(a, b) {
                    var va = a.value.toLowerCase();
                    var vb = b.value.toLowerCase();
                    if (va < vb) {
                        return -1;
                    } else if (va == vb) {
                        return 0;
                    } else {
                        return 1;
                    }
                });
                this.localPartOptions[namespace] = o;
            }
        }
        return o;
    },

    /**
     * @private
     */
    changeNamespace: function() {
        var o = this.getLocalPartOptions(this.getNamespace());
        this.localPartField.setOptions(o);
        this.setQualified();
    },

    /**
     * @private
     */
    changeLocalPart: function() {
        this.setQualified();
        try {
            var lp = this.options[this.getNamespace()][this.getLocalPart()];
            this.setType(lp["type"]);
            this.multivalueField.setValue(lp["multivalue"]);
        }
        catch (e) {
            // no accordant local part definition
        }
    },

    /**
     * Sets the qualified name by combining namespace and local part.
     * @private
     */
    setQualified: function() {
        var ns = this.getNamespace();
        var lp = this.getLocalPart();
        var value = "";
        if (lp) {
            if (ns) value = ns + ":" + lp;
            else value = lp;
        }
        //todo:escape name
        this.qualifiedField.setValue(value);
    },

    /**
     * Sets the value of the type field and the constraint field.
     * @private
     */
    setType: function(v) {
        this.typeField.setValue(v);
        this.setConstraint(v);
    },

    /**
     * Tries to find and set the constraint field according to the given type.
     * @private
     */
    setConstraint: function(type) {
        if (!this.constraintField) {
            try {
                var dialog = this.findParentByType("dialog");
                this.constraintField = dialog.getField(this.constraintFieldName);
            }
            catch (e) {
                // create dummy field
                this.constraintField = {
                    setValue: function() {}
                };
            }
        }
        if (this.constraintsMap[type]) {
            this.constraintField.setValue(this.constraintsMap[type]);
        }
        else {
            // clear constraint
            this.constraintField.setValue("");
        }
    },

    constructor : function(config) {
        this.hiddenField = new CQ.Ext.form.Hidden({
            "name": config.name
        });

        CQ.Ext.applyIf(config, {
            "options": "/libs/dam/options/metadata.overlay.2.json",
            "border": false,
            "hideLabel": true
        });


        CQ.dam.form.Metadata.superclass.constructor.call(this, config);
    }

});

CQ.Ext.reg("metadata", CQ.dam.form.Metadata);

