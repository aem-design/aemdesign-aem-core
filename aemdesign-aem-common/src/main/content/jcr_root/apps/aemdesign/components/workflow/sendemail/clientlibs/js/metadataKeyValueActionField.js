CQ.wcm.MetadataKeyValueActionField = CQ.Ext.extend(CQ.form.CompositeField, {

    /**
     * @private
     * @type CQ.Ext.form.TextField
     */
    hiddenField: null,

    /**
     * @private
     * @type CQ.Ext.form.TextField
     */
    propertyField: null,

    /**
     * @private
     * @type CQ.Ext.form.TextField
     */
    valueField: null,

    /**
     * @private
     * @type CQ.form.Selection
     */
    actionField: null,

    constructor: function (config) {
        config = config || { };
        var defaults = {
            "border": false,
            "layout": "table",
            "columns": 4,
            anchor:'100%',
            "autoWidth": true

        };
        config = CQ.Util.applyDefaults(config, defaults);
        CQ.wcm.MetadataKeyValueActionField.superclass.constructor.call(this, config);
    },

    // overriding CQ.Ext.Component#initComponent
    initComponent: function () {
        CQ.wcm.MetadataKeyValueActionField.superclass.initComponent.call(this);

        this.propertyField = new CQ.Ext.form.TextField({
            listeners: {
                change: {
                    scope: this,
                    fn: this.updateHidden
                }
            },
            regex: /^[a-zA-Z0-9-_]+$/,
            regexText: 'Only alphanumeric characters, "_" and "-" are allowed',
            width: 80,
            emptyText: CQ.I18n.getMessage("Key")
        });
        this.add(this.propertyField);

        var staticField = new CQ.Ext.form.DisplayField();
        staticField.setValue("&nbsp;=&nbsp;");
        this.add(staticField);

        this.valueField = new CQ.Ext.form.TextField({
            listeners: {
                change: {
                    scope: this,
                    fn: this.updateHidden
                }
            },
            width: 100,
            emptyText: CQ.I18n.getMessage("Value")
        });
        this.add(this.valueField);

        var staticField = new CQ.Ext.form.DisplayField();
        staticField.setValue("&nbsp;&&nbsp;");
        this.add(staticField);

        var actionOptions = [{"value":"","text":"none"}];

        if (CQ.Ext.form.FieldActions) {
            for (var item in CQ.Ext.form.FieldActions) {

                actionOptions.push({
                    "value": item,
                    "qtip": item
                });

            }
        }

        //actionOptions
        this.actionField = new CQ.form.Selection({
            listeners: {
                selectionchanged: {
                    scope: this,
                    fn: this.updateHidden
                }
            },
            type: "select",
            width: 120,
            defaultValue: '',
            layout: 'fit',
            anchor:'100%',
            listWidth:120,
            minWidth: '100',
            style: {
                width: "100px!important;"
            },
            options: actionOptions
        });

        this.add(this.actionField);




        this.hiddenField = new CQ.Ext.form.TextField({
            hidden: true,
            name: this.name
        });
        this.add(this.hiddenField);

    },

    // overriding CQ.form.CompositeField#processPath
    processPath: function (path) {
        this.propertyField.processPath(path);
    },

    // overriding CQ.form.CompositeField#processRecord
    processRecord: function (record, path) {
        this.propertyField.processRecord(record, path);
    },

    // overriding CQ.form.CompositeField#setValue
    setValue: function (value) {
        var keyValPair = JSON.parse(value);
        this.propertyField.setValue(keyValPair.key);
        this.valueField.setValue(keyValPair.value);
        this.actionField.setValue(keyValPair.action);
        this.hiddenField.setValue(value);
    },

    validateProperty: function(){
        this.updateHidden();
    },

    updateHidden: function() {
        var dialog = this.findParentByType('dialog');
        if(dialog.getField("./clickExp")){
            window.FD.FP.SaveConfigChangeListener(this);
        }
        this.hiddenField.setValue(this.getValue());
    },

    // overriding CQ.form.CompositeField#getValue
    getValue: function () {
        return this.getRawValue();
    },

    // overriding CQ.form.CompositeField#getRawValue
    getRawValue: function () {
        if (!this.propertyField) {
            return null;
        }
        var keyValPair = {
            "key": this.propertyField.getValue(),
            "value": this.valueField.getValue(),
            "action": this.actionField.getValue()
        };

        return JSON.stringify(keyValPair);
    }
});

// register xtype
CQ.Ext.reg('metadatakeyvalueactionfield', CQ.wcm.MetadataKeyValueActionField);
