//embedsource - functions
window.AEMDESIGN = window.AEMDESIGN || {"jQuery":{}};
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.embedsource = AEMDESIGN.components.embedsource || {};

(function ($, _, ko, ns, log, window, undefined) { //NOSONAR convention for wrapping all modules

    "use strict";
    var _version = "0.1";

    ns.version = function () {
        return _version;
    };

    ns.isSelfClosingTag = function(tagName) {
        return tagName.match(/area|base|br|col|embed|hr|img|input|keygen|link|menuitem|meta|param|source|track|wbr|script/i);
    };

    ns.findUnclosedTags = function(input) {
        var result = {
            "valid": false,
            "error": ""
        };
        var tags = [];
        $.each(input.split('\n'), function (i, line) { //NOSONAR acceptable jquery pattern
            $.each(line.match(/<[^>]*[^/]>/g) || [], function (j, tag) { //NOSONAR acceptable jquery pattern
                var matches = tag.match(/<\/?([a-z0-9]+)/i);
                if (matches) {
                    tags.push({tag: tag, name: matches[1], line: i+1, closing: tag[1] === '/'});
                }
            });
        });
        if (tags.length == 0) {
            result.error = 'No tags found.';
            return result;
        }
        var openTags = [];
        var error = false;
        var indent = 0;
        var openTag;
        for (var i = 0; i < tags.length; i++) {
            var tag = tags[i];
            if (tag.closing) {
                var closingTag = tag;
                if (ns.isSelfClosingTag(closingTag.name)) {
                    continue;
                }
                if (openTags.length == 0) {
                    result.error = 'Closing tag ' + closingTag.tag
                        + ' on line ' + closingTag.line
                        + ' does not have corresponding open tag.';
                    return result;
                }
                openTag = openTags[openTags.length - 1];
                if (closingTag.name !== openTag.name) {
                    result.error = 'Closing tag ' + closingTag.tag
                        + ' on line ' + closingTag.line
                        + ' does not match open tag ' + openTag.tag
                        + ' on line ' + openTag.line + '.';
                    return result;
                } else {
                    openTags.pop();
                }
            } else {
                openTag = tag;
                if (ns.isSelfClosingTag(openTag.name)) {
                    continue;
                }
                openTags.push(openTag);
            }
        }
        if (openTags.length > 0) {
            openTag = openTags[openTags.length - 1];
            result.error = 'Open tag ' + openTag.tag
                + ' on line ' + openTag.line
                + ' does not have a corresponding closing tag.';
            return result;
        }
        result.error = 'Success: No unclosed tags found.';
        result.valid = true;
        return result;

    };

})(AEMDESIGN.jQuery,_,ko, AEMDESIGN.components.embedsource, AEMDESIGN.log, this); //pass in additional dependencies

