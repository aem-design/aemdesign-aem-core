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

CQ.tagging.TagAdmin.baseDialogConfig = {
    xtype: "dialog",
    params: {
        "_charset_": "utf-8"
    },
    buttons: CQ.Dialog.OKCANCEL
};

CQ.tagging.TagAdmin.createTag = function () {
    var dialogConfig = CQ.Util.applyDefaults({
        title: CQ.I18n.getMessage("Create Tag"),
        formUrl: CQ.tagging.TagAdmin.TAG_COMMAND_URL,
        params: {
            cmd: "createTag"
        },
        okText: CQ.I18n.getMessage("Create"),
        items: {
            xtype: "panel",
            items: [
                {
                    name: "jcr:title",
                    fieldLabel: CQ.I18n.getMessage("Title"),
                    allowBlank: false
                },
                {
                    name: "tag",
                    fieldLabel: CQ.I18n.getMessage("Name"),
                    vtype: "itemname",
                    allowBlank: false
                },
                {
                    name: "jcr:description",
                    fieldLabel: CQ.I18n.getMessage("Description"),
                    xtype: "textarea"
                }
            ]
        }
    }, CQ.tagging.TagAdmin.baseDialogConfig);

    var tagPath = CQ.tagging.TagAdmin.getCurrentTreePath();
    if (tagPath == this.tagsBasePath) {
        // creating a new namespace
        // not setting parentTagID here => create namespace instead of tag
        dialogConfig.title = CQ.I18n.getMessage("Create Namespace");
        dialogConfig.items.items[0].fieldLabel = CQ.I18n.getMessage("Namespace Title");
        dialogConfig.items.items[1].fieldLabel = CQ.I18n.getMessage("Namespace Name");

    } else {
        var parentTagID = tagPath.substring(this.tagsBasePath.length + 1);

        // ensure the first path element (namespace) ends with ":"
        if (parentTagID.indexOf("/") > 0) {
            // replace (first) slash after namespace and end it with a slash
            parentTagID = parentTagID.replace("/", ":") + "/";
        } else {
            // add colon after namespace
            parentTagID = parentTagID + ":";
        }
        dialogConfig.params.parentTagID = parentTagID;
    }

    var dialog = this.createDialog(dialogConfig, CQ.I18n.getMessage("Could not create tag."));
    dialog.show();
};

(function () {

    var languages = null;

    CQ.tagging.TagAdmin.editTag = function () {
        var tag = CQ.tagging.TagAdmin.getSelectedTag();
        if (tag == null) {
            return;
        }

        if (!languages) {
            languages = CQ.HTTP.eval(CQ.tagging.LANGUAGES_URL).languages;
        }

        var langTitles = CQ.I18n.getLanguages();

        var localizedTitles = [];
        CQ.Ext.each(languages, function (lang) {
            lang = CQ.tagging.getTagLocaleCode(lang);
            localizedTitles.push({
                name: "jcr:title." + lang,
                fieldLabel: langTitles[lang] ? langTitles[lang].title : lang,
                xtype: "textfield"
            });
        });

        var dialogConfig = CQ.Util.applyDefaults({
            title: CQ.I18n.getMessage("Edit Tag"),
            okText: CQ.I18n.getMessage("Save"),
            items: {
                xtype: "panel",
                items: [
                    {
                        name: "jcr:title",
                        fieldLabel: CQ.I18n.getMessage("Title"),
                        allowBlank: false
                    },
                    {
                        name: "jcr:description",
                        fieldLabel: CQ.I18n.getMessage("Description"),
                        xtype: "textarea"
                    },
                    {
                        name: "jcr:lastModified",
                        xtype: "hidden",
                        ignoreData: true
                    },
                    {
                        name: "jcr:lastModifiedBy",
                        xtype: "hidden",
                        ignoreData: true
                    },
                    {
                        title: CQ.I18n.getMessage("Localization"),
                        xtype: "dialogfieldset",
                        collapsible: true,
                        items: localizedTitles
                    }
                ]
            }
        }, CQ.tagging.TagAdmin.baseDialogConfig);

        var dialog = this.createDialog(dialogConfig, CQ.I18n.getMessage("Could not save tag."));
        dialog.loadContent(tag, ".0.json");
        dialog.show();
    };

})();

CQ.tagging.TagAdmin.deleteTag = function () {
    var tag = CQ.tagging.TagAdmin.getSelectedTag();
    if (tag == null) {
        return;
    }

    CQ.Ext.Msg.confirm(
        CQ.tagging.TagAdmin.getCurrentTreePath() == this.tagsBasePath ?
            CQ.I18n.getMessage("Delete Namespace?") :
            CQ.I18n.getMessage("Delete Tag?"),
        CQ.I18n.getMessage("You are going to delete: {0}<br/><br/>Are you sure?", [CQ.shared.XSS.getXSSValue(tag)]),
        function (btnId) {
            if (btnId == "yes") {
                this.postTagCommand("deleteTag", tag);
            }
        },
        this
    );
};

CQ.tagging.TagAdmin.getParent = function (path) {
    var pathSteps = path.split("/");
    var parentPath = "";
    for (var i = 0; i < pathSteps.length - 1; i++) {
        if (i > 0) {
            parentPath += "/";
        }
        parentPath += pathSteps[i];
    }
    return parentPath;
};

CQ.tagging.TagAdmin.getName = function (path) {
    var pathSteps = path.split("/");
    return pathSteps[pathSteps.length - 1];
};

// register "tagPath" vtype
(function () {
    CQ.Ext.apply(CQ.Ext.form.VTypes, {
        tagPath: function (val, field) {
            return ( /^\/etc\/tags(\/|$)/.test(val) );
        },
        tagPathText: CQ.I18n.getMessage("Not a valid tag path. Must start with /content/cq:tags.")
    });
})();

CQ.tagging.TagAdmin.moveTag = function () {
    var tag = CQ.tagging.TagAdmin.getSelectedTag();
    if (tag == null) {
        return;
    }

    var admin = this;

    var dialog = new CQ.Dialog({
        title: CQ.I18n.getMessage("Move Tag"),
        okText: CQ.I18n.getMessage("Move"),
        buttons: CQ.Dialog.OKCANCEL,
        items: {
            xtype: "panel",
            items: [
                {
                    name: "tag",
                    fieldLabel: CQ.I18n.getMessage("Move"),
                    disabled: true,
                    value: tag
                },
                {
                    xtype: "pathfield",
                    name: "destParent",
                    fieldLabel: CQ.I18n.getMessage("to"),
                    rootPath: this.tagsBasePath,
                    predicate: "tag",
                    allowBlank: false,
                    vtype: "tagPath",
                    value: CQ.tagging.TagAdmin.getParent(tag)
                },
                {
                    name: "destName",
                    fieldLabel: CQ.I18n.getMessage("Rename to"),
                    allowBlank: false,
                    vtype: "itemname",
                    value: CQ.tagging.TagAdmin.getName(tag)
                }
            ]
        },
        ok: function () {
            if (this.form.isValid()) {
                var dest = this.getField("destParent").getValue();
                var name = this.getField("destName").getValue();
                // if ends with "/"
                if (dest.match(/\/$/)) {
                    dest += name;
                } else {
                    dest += "/" + name;
                }
                admin.postTagCommand("moveTag", tag, { dest: dest });
                this.hide();
            } else {
                CQ.Ext.Msg.show({
                    title: CQ.I18n.getMessage('Validation Failed'),
                    msg: CQ.I18n.getMessage('Verify the values of the marked fields.'),
                    buttons: CQ.Ext.Msg.OK,
                    icon: CQ.Ext.Msg.ERROR
                });
            }
        }
    });
    dialog.show();
};

CQ.tagging.TagAdmin.mergeTag = function () {
    var tag = CQ.tagging.TagAdmin.getSelectedTag();
    if (tag == null) {
        return;
    }

    var admin = this;

    var dialog = new CQ.Dialog({
        title: CQ.I18n.getMessage("Merge Tag"),
        okText: CQ.I18n.getMessage("Merge"),
        buttons: CQ.Dialog.OKCANCEL,
        items: {
            xtype: "panel",
            items: [
                {
                    name: "tag",
                    fieldLabel: CQ.I18n.getMessage("Merge"),
                    disabled: true,
                    value: tag
                },
                {
                    xtype: "pathfield",
                    name: "dest",
                    fieldLabel: CQ.I18n.getMessage("into"),
                    fieldDescription: CQ.I18n.getMessage("After the merge, this will be the only tag left of the two."),
                    rootPath: this.tagsBasePath,
                    predicate: "tag",
                    allowBlank: false,
                    vtype: "tagPath",
                    value: tag
                }
            ]
        },
        ok: function () {
            if (this.form.isValid()) {
                var dest = this.getField("dest").getValue();
                admin.postTagCommand("mergeTag", tag, { dest: dest });
                this.hide();
            } else {
                CQ.Ext.Msg.show({
                    title:CQ.I18n.getMessage('Validation Failed'),
                    msg: CQ.I18n.getMessage('Verify the values of the marked fields.'),
                    buttons: CQ.Ext.Msg.OK,
                    icon: CQ.Ext.Msg.ERROR
                });
            }
        }
    });
    dialog.show();
};

CQ.tagging.TagAdmin.activateTag = function () {
    var tag = CQ.tagging.TagAdmin.getSelectedTag();
    if (tag == null) {
        return;
    }

    this.postTagCommand("activateTag", tag);
};

CQ.tagging.TagAdmin.deactivateTag = function () {
    var tag = CQ.tagging.TagAdmin.getSelectedTag();
    if (tag == null) {
        return;
    }

    this.postTagCommand("deactivateTag", tag);
};

CQ.tagging.TagAdmin.listTaggedItems = function () {
    var tag = CQ.tagging.TagAdmin.getSelectedTag();
    if (tag == null) {
        return;
    }

    var renderPageTitle = function (value, p, record) {
        var url = CQ.HTTP.externalize(record.json.path);
        if (url.indexOf(".") == -1) {
            url += ".html";
        }
        return String.format('<a href="{0}" target="_blank">{1}</a>', url, value);
    };
    var grid = new CQ.Ext.grid.GridPanel({
        store: new CQ.Ext.data.GroupingStore({
            proxy: new CQ.Ext.data.HttpProxy({
                url: CQ.tagging.TagAdmin.TAG_COMMAND_URL,
                method: "GET"
            }),
            baseParams: { cmd: "list", path: tag},
            autoLoad: true,
            reader: new CQ.Ext.data.JsonReader({
                root: 'taggedItems',
                totalProperty: 'results',
                id: 'item',
                fields:
                    [
                        'title',
                        'itemPath'
                    ]
            })
        }),
        cm: new CQ.Ext.grid.ColumnModel([
            new CQ.Ext.grid.RowNumberer(),
            {
                header: CQ.I18n.getMessage("Title"),
                dataIndex: 'title',
                renderer: renderPageTitle
            }, {
                header: CQ.I18n.getMessage("Path"),
                dataIndex: 'itemPath'
            }
        ]),
        viewConfig: {
            forceFit: true,
            groupTextTpl: '{text} ({[values.rs.length]} {[values.rs.length > 1 ? "Items" : "Item"]})'
        },
        sm: new CQ.Ext.grid.RowSelectionModel({singleSelect: true})
    });
    var win = new CQ.Ext.Window({
        title: CQ.I18n.getMessage("Items tagged with") + CQ.shared.XSS.getXSSValue(tag),
        width: 800,
        height: 400,
        autoScroll: true,
        items: grid,
        layout: 'fit',
        maximizable: true,
        minimizable: true,
        y: 200
    }).show();
};

// the ext tree path (eg. /default/bla)
CQ.tagging.TagAdmin.getCurrentTreePath = function () {
    var tree = CQ.Ext.getCmp("cq-tagadmin-tree");
    var node = tree.getSelectionModel().getSelectedNode();
    if (node != null) {
        return node.getPath();
    } else {
        return tree.selectedPath;
    }
};

CQ.tagging.TagAdmin.getSelectedTags = function () {
    var grid = CQ.Ext.getCmp("cq-tagadmin-grid");
    return grid.getSelectionModel().getSelections();
};

CQ.tagging.TagAdmin.getSelectedTag = function () {
    var selections = CQ.tagging.TagAdmin.getSelectedTags();
    if (selections.length > 0) {
        return selections[0].id;
    }
    return null;
};

CQ.tagging.TagAdmin.getAnyTarget = function () {
    return CQ.tagging.TagAdmin.getSelectedTag() ||
        CQ.tagging.TagAdmin.getCurrentTreePath();
};

CQ.tagging.TagAdmin.getSingleTarget = function () {
    if (CQ.tagging.TagAdmin.getSelectedTags().length > 0) {
        // make sure list selection is single
        if (CQ.tagging.TagAdmin.getSelectedTags().length > 1) {
            return null;
        } else {
            return CQ.tagging.TagAdmin.getSelectedTag();
        }
    } else {
        // no list selection, use tree
        return CQ.tagging.TagAdmin.getCurrentTreePath();
    }
};

