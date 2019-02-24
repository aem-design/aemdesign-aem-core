$.extend($.ui.autocomplete.prototype.options, {
    open: function(event, ui) {
        var width = $(this).width();
        width+=parseInt($(this).css("border-right-width"),10);
        width+=parseInt($(this).css("border-left-width"),10);
        width+=parseInt($(this).css("paddingRight"),10);
        width+=parseInt($(this).css("paddingLeft"),10);


        $(this).autocomplete("widget").css({
            "width": (width + "px")
        });
    }
});