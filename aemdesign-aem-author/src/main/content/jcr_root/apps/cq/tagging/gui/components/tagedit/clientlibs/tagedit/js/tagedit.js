/*
 * ADOBE CONFIDENTIAL
 *
 * Copyright 2015 Adobe Systems Incorporated
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated and its
 * suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 */
(function (window, document, Granite, $) {
    "use strict";    
    var MODAL_HTML =
        "<div class=\"coral-Modal-header\">"
        + "<h2 class=\"coral-Modal-title coral-Heading coral-Heading--2\"></h2>"
        + "<i class=\"coral-Modal-typeIcon coral-Icon coral-Icon--sizeS\"></i>"
        + "<button type=\"button\" "
        + "class=\"coral-MinimalButton coral-Modal-closeButton\" "
        + "data-dismiss=\"modal\">"
        + "<i class=\"coral-Icon coral-Icon--sizeXS coral-Icon--close "
        + "coral-MinimalButton-icon\"></i>" + "</button>" + "</div>"
        + "<div class=\"coral-Modal-body legacy-margins\"></div>"
        + "<div class=\"coral-Modal-footer\"></div>";

    var selected = new Object();

    $(document).on("foundation-contentloaded", function () {
        // hide the default coral taglist on multiselect
        // TODO find a better way to do this
        $("#tagedit-settings-languagepicker .coral-TagList").remove();
        var requestPath = $(".foundation-content-path").data("foundationContentPath");
        // prefill the fields
        // TODO find a way to eliminate this ajax and perform the prefilling in some jsp itself
        $.ajax({
            type: "GET",
            url: requestPath + ".json" + '?_ck=' + Date.now(),
            contentType: "application/json"
        }).success(function (res) {
            var title = res["jcr:title"];
            var description = res["jcr:description"];
            var tagvalue = res["value"];
            var taghref = res["href"];
            $("#tagtitle").val(title);
            $("#tagdescription").val(description);
            $("#tagvalue").val(tagvalue);
            $("#taghref").val(taghref);

            for (var key in res) {
                var titlePrefix = "jcr:title.";
                if (res.hasOwnProperty(key) && key.lastIndexOf(titlePrefix, 0) === 0) {
                    var lang = key.substring(titlePrefix.length);
                    var val = res[key];
                    handleAdd(lang, val);
                }
            }

        });

        $(document).on("selected", function(e){
            e.preventDefault();
            var newSelectedList = e.selected;
            for (var i = 0; i < newSelectedList.length; i++) {
                if (!selected[newSelectedList[i]]) {                                        
                    handleAdd(newSelectedList[i], null);
                    break;
                }
            }
        });

        $(document).on("click", ".tag-added-language-remove-button", function (e) {
            var $this = $(this);
            var addedLangDiv = $this.closest(".tag-added-language");
            var lang = addedLangDiv.data("lang");
            selected[lang] = false;
            var $select = $(".tag-languages-select");
            var currentSelectList = $select.data("select").getValue();
            var updatedSelectList = new Array();
            for (var i = 0; i < currentSelectList.length; i++) {
                if (lang != currentSelectList[i]) {                    
                    updatedSelectList.push(currentSelectList[i]);
                }
            }
            $select.data("select").setValue(updatedSelectList);
            addedLangDiv.remove();            
        });

        var wizard = $("form#tag-edit-form");
        wizard.on("submit", function (e) {
            e.preventDefault();
            submit(wizard);
        });

    });

    function handleAdd(lastSelected, fieldValue) {
        selected[lastSelected] = true;
        var lastSelectedTitle = document.getElementsByClassName(lastSelected)[0].text;
        var fieldName = "./jcr:title." + lastSelected;
        var fieldValueAttr = (fieldValue != null) ? ("value = \"" + fieldValue + "\"") : "";

        var languageMarkup = "<div class = \"tag-added-language\" data-lang=\"" + lastSelected + "\">"
                                  + "<div class=\"coral-Form-fieldlabel tag-added-language-label\">" + lastSelectedTitle + "</div>"
                                  + "<input class = \"coral-Textfield tag-added-language-textfield\" name = \"" + fieldName + "\"" + fieldValueAttr + ">"
                                  + "<button type=\"button\" class=\"coral-Button coral-Button--secondary coral-Button--square tag-added-language-remove-button\">"
                                      + "<i class=\"coral-Icon coral-Icon--sizeS coral-Icon--minus\"></i>"   
                                  + "</button>"
                           + "</div>";  
        var parent = $("#tag-selected-languages");
        parent.append(languageMarkup);

    }

    function submit(wizard) {
        var foundationContentPath = $(".foundation-content-path").data("foundationContentPath");
        var successMessage = "";
        var errorMessage = "";
        var editTagSettings = $("#edittagsettings");

        var data;
        data = wizard.serialize();
        var processData = true;
        var contentType = wizard.prop("enctype");        
        successMessage = Granite.I18n.get("Tag Edited Successfully");
        errorMessage = Granite.I18n.get("Failed to edit tag");

        var length = Object.keys(selected).length;
        for(var i = 0; i < length; i++) {
            if(selected[Object.keys(selected)[i]] == false) {
                var toDelete = "./jcr:title." + Object.keys(selected)[i];
                data += "&" + toDelete + "@Delete";
            }
        }
        $.ajax({
            type: wizard.prop("method"),
            url: foundationContentPath,
            data: data,
            processData: processData,
            contentType: contentType
        }).done(function (html) {
            if ($("#tagedit-success").length === 0) {
                var insertModal = $("<div>", {"class": "coral-Modal", "id": "tagedit-success", "style": "width:30rem"}).hide().html(MODAL_HTML);
                $(document.body).append(insertModal);
            }
            var successModal = new CUI.Modal({
                element: '#tagedit-success',
                heading: Granite.I18n.get('Success'),
                type: 'success',
                content: successMessage,
                buttons: [{
                        label: Granite.I18n.get('OK'),
                        className: 'primary',
                        click: function (evt) {
                            this.hide();
                            var index = window.location.href.indexOf("/tagedit");
                            var length = window.location.href.length;
                            window.location = window.location.href.substr(0, index) + window.location.href.substr(index + 8, length);
                        }
                    }
                ]
            });

        }).fail(function (xhr, error, errorThrown) {
            if ($("#tagedit-failure").length === 0) {
                var insertModal = $("<div>", {"class": "coral-Modal", "id": "tagedit-failure", "style": "width:30rem"}).hide().html(MODAL_HTML);
                $(document.body).append(insertModal);
            }
            var failureModal = new CUI.Modal({
                element: '#tagedit-failure',
                heading: Granite.I18n.get('Error'),
                type: 'error',
                content: errorMessage,
                buttons: [{
                        label: Granite.I18n.get('Close'),
                        className: 'primary',
                        click: function (evt) {
                            this.hide();
                            var index = window.location.href.indexOf("/tagedit");
                            var length = window.location.href.length;
                            window.location = window.location.href.substr(0, index) + window.location.href.substr(index + 8, length);
                        }
                    }
                ]
            });
            failureModal.appendTo("body").modal("show");
        });
    }

})(window, document, Granite, Granite.$);
