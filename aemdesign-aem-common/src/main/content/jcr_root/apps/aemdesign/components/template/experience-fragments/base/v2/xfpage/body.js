"use strict";

var global = this;

use([], function () {
    var rootResource = resource.getChild('root'); //hardcoded to allow converting component into xpf on pages
    var resourcePath = "";

    if (rootResource != null) {
        resourcePath = rootResource.getPath();
    } else {
        // if we don't have a "root" subnode just take the first one
        var children = rootResource.getChildren();
        if (children.length > 0) {
            resourcePath = children[0].getPath();
        }
    }
    return {
        cssClasses: "xf-web-container",
        resourcePath: resourcePath,
        rootResource:rootResource,
    };
});
