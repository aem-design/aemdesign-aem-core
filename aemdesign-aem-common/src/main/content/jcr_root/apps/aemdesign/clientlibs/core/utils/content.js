//aemdesign.content.js
window.AEMDESIGN = window.AEMDESIGN || {"jQuery":{}};
window.AEMDESIGN.content = window.AEMDESIGN.content || {};

(function ($, ns, sling, services, log, window, undefined) { //NOSONAR namespace convention

    "use strict";
    var _version = "0.3";

    ns.version = function () {
        return _version;
    };

    ns.createPage = function(endPoint,components,nameHint, trackingEvent) {

        var url = services.getServiceURL(endPoint);

        try {

            //Adobe Target integration
            window._dtmLayer.components = components;
            window._dtmLayer.endPoint = endPoint;
            window._dtmLayer.name = nameHint;
            window._dtmLayer.functionCall = "design.aem.content.createPage";

            var componentData = {};

            //output data from components
            for (var i in components) {
                if (components.hasOwnProperty(i)) {
                    var component = components[i];
                    var extraParams = component["extraParams"];
                    var resourceType = component["resourceType"];
                    var componentName = resourceType.right(resourceType.length - resourceType.lastIndexOf("/") - 1);

                    componentData[componentName] = {};

                    for (var param in extraParams) { //NOSONAR this is not complex
                        componentData[componentName][param] = extraParams[param];
                    }
                }
            }

            window._dtmLayer.data = componentData;
            window._satellite.track(trackingEvent);

            log.warn(["createPage","trigger analytics, done",
                endPoint,
                components,
                nameHint,
                trackingEvent,
                window._dtmLayer,
                window._satellite
            ]);
        } catch (ex) {
            log.error(["createPage","trigger analytics, failed",
                endPoint,
                components,
                nameHint,
                trackingEvent,
                window._dtmLayer,
                window._satellite,
                ex
            ]);
        }

        return (new sling.PostRequest()
            .setURL(url)
            .addHashContent(endPoint, AEMDESIGN.components.defaults.PAGE_COMPONENT, AEMDESIGN.components.defaults.PAGE_TEMPLATE)
            .setNameHintParam(nameHint,"")
            .prepareCreateComponent(AEMDESIGN.components.Page())
            .prepareCreateComponent(AEMDESIGN.components.PageContent())
            .prepareCreateComponent(AEMDESIGN.components.Parsys())
            .prepareCreateComponents(components)
            .send()
        );

    };


})(AEMDESIGN.jQuery, AEMDESIGN.content, AEMDESIGN.sling, AEMDESIGN.services, AEMDESIGN.log, this);
