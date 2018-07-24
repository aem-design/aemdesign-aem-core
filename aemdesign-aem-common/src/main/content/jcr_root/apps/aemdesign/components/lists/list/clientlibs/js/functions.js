//list - functions
window.AEMDESIGN = window.AEMDESIGN || {"jQuery":{}};
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.list = AEMDESIGN.components.list || {};


(function ($, _, ko, ns, log, window, undefined) { //NOSONAR convention for wrapping all modules


    "use strict";
    var _version = "0.1";

    var _currentState = null;

    var _startParaNameSuffix = /_start$/;

    var _maxParaNameSuffix = /_max$/;


    ns.version = function () {
        return _version;
    };


    ns.currentState = function () {
        return _currentState;
    };

    ns.startParaNameSuffix = function () {
        return _startParaNameSuffix;
    };

    ns.maxParaNameSuffix = function () {
        return _maxParaNameSuffix;
    };

    /**
     * Pass the querystring part of a URL or current URL
     * @param str
     * @returns {*}
     */
    ns.getQueryParameters = function(str) {
        return (str || document.location.search)
            .replace(/(^\?)/,'')
            .split("&")
            .map(function(n){
                n = n.split("=");
                this[n[0]] = n[1];
                return this;
            }.bind({}))
            [0];
    };


    ns.extractObject = function(queryString) {
        var params = queryString.split("&");
        var paramObj = {

        };

        for (var idx = 0 ; idx < params.length ; idx++) {
            var param = params[idx];
            var paramInfo = param.split("=");
            if (paramInfo.length >= 2) {
                paramObj[paramInfo[0]] = paramInfo[1];
            }
        }

        return paramObj;
    };

    ns.combineQueryStrings = function(queryString1, queryString2) {
        var paramObj1 = ns.extractObject(queryString1);
        var paramObj2 = ns.extractObject(queryString2);

        for (var param in paramObj2) {
            paramObj1[param] = paramObj2[param];
        }

        return ns.conslidateQueryStrings(paramObj1);
    };
    /**
     * Update the parameters if the user refresh the browser, the number of Items will be the same
     * @param paramObj
     * @returns {string}
     */
    ns.conslidateQueryStrings = function(paramObj) {

        var conslidateQueryStrings = "";

        var start=0;
        var max=0;

        for (var param1 in paramObj) {
            if (param1.match(ns.startParaNameSuffix())){
                start = paramObj[param1];
            }

            if (param1.match(ns.maxParaNameSuffix())){
                max = paramObj[param1];
            }

        }

        if (start !== undefined && max !== undefined &&  start > 0 && max > 0){

            for (var param2 in paramObj) {
                if (param2.match(ns.startParaNameSuffix())){
                    paramObj[param2] = 0;
                }

                if (param2.match(ns.maxParaNameSuffix())){

                    paramObj[param2] = parseInt(start) + parseInt(max);
                }
                if (conslidateQueryStrings !== "") {
                    conslidateQueryStrings += "&";
                }
                conslidateQueryStrings += param2 + "=" + paramObj[param2];

            }

        }

        return conslidateQueryStrings;
    };


    ns.updateList = function (listId, content) {
        var targetList = document.getElementById(listId);
        if (targetList) {
            targetList.outerHTML = content;
        }
    };


    ns.linkClick = function(element, listId) {
        var goToLink = element.href;

        var urlRegex = /https?:\/\/[^\\/]*(.*)/gi;
        var matches = urlRegex.exec(goToLink);
        var queryString = "";

        if (matches
            && matches.length > 0) {
            var linkPath = matches[1];
            var queryIdx = linkPath.indexOf("?");
            if (queryIdx >= 0) {
                queryString = linkPath.substring(queryIdx + 1);
            }
        }

        // do XHR to load list
        var  xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function() {
            if (xhr.readyState === 4
                && xhr.status === 200
                && xhr.responseText !== "") {
                var targetList = document.getElementById(listId);
                if (targetList) {

                    if (!window.history.state) {
                        window.history.replaceState({
                            listId: listId,
                            content: targetList.outerHTML
                        }, null, window.location.href);
                    }

                    if (ns.currentState()
                        && ns.currentState().listId !== listId) {
                        ns.currentState()["extraListId"] = listId;
                        ns.currentState()["extraContent"] = targetList.outerHTML;
                        window.history.replaceState(ns.currentState(), null, window.location.href);
                    }

                    var $data = $(xhr.responseText);
                    $data.find( ".page_list").attr("style", "padding-top:0px");
                    var innerHTML = $data.html();

                    targetList.outerHTML = innerHTML;


                    // Update the more page url
                    //var anchor = $(targetList).find("a");
                    //var path = anchor.prop('href').split("?")[0];
                    //var map = ns.getQueryParameters(anchor.prop('href').split("?")[1]);
                    //$.extend(map, ns.nextPageStart());
                    //var url = path + "?" + $.param(map);


                    var stateContent = innerHTML;
                    var currentUrl = window.location.href;
                    var currentQueryString = "";
                    var queryIdx = currentUrl.indexOf("?"); //NOSONAR internal to this function
                    if (queryIdx >= 0) {
                        currentQueryString = currentUrl.substring(queryIdx + 1);
                        currentUrl = currentUrl.substring(0, queryIdx);
                    }


                    var useQueryString = ns.combineQueryStrings(currentQueryString, queryString);

                    var currentState = {
                        listId: listId,
                        content: stateContent
                    };

                    window.history.pushState(currentState, null, currentUrl + "?" + useQueryString);
                }
            }
        };
        xhr.open("GET", goToLink, true);
        xhr.send();

        return false;
    };

    ns.handlePopStateListNavigation = function(event) {

        if (event.state && event.state.listId) {
            window.location.reload();
        }
    };

    ns.bindHistoryPopStateEvent = function() {
        if (window.addEventListener) {
            window.addEventListener("popstate", ns.handlePopStateListNavigation);
        } else if (window.attachEvent) {
            window.attachEvent("popstate", ns.handlePopStateListNavigation);
        }
    };


})(AEMDESIGN.jQuery,_,ko, AEMDESIGN.components.list, AEMDESIGN.log, this); //pass in additional dependencies