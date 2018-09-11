//topic filter - functions
window.AEMDESIGN = window.AEMDESIGN || {"jQuery":{}};
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.topicFilter = AEMDESIGN.components.topicFilter || {};

(function ($, _, ko, ns, log, window, undefined) { //add additional dependencies

    "use strict";
    var _version = "0.1";

    ns.version = function () {
        return _version;
    };


    var ALL = /.*\/all$/;

    ns.CONST_ALL = function () {
        return ALL;
    };


    var _topicQueue;

    ns.topicQueue = function () {
        return _topicQueue;
    };

    //KO Helpers functions
    //usage of publishOn
    ko.subscribable.fn.publishOn = function(topic) {
        this.subscribe(function(newValue) {
            ns.topicFilterNotify.notifySubscribers(newValue, topic);
        });

        return this; //support chaining
    };

    //usage
    ko.subscribable.fn.subscribeTo = function(topic) {
        ns.topicFilterNotify.subscribe(this, null, topic);
        return this;  //support chaining
    };

    /**
     *
     * @param soft
     */
    ns.loadTopicFilters = function(soft) {

        var base = $(this);
        var alreadyLoaded = false;

        if (soft) {
            if (base.data("modulesloaded")=="topicFilters") {
                alreadyLoaded = true;
            }
        }

        log.info("alreadyLoaded: "+alreadyLoaded);

        if (!alreadyLoaded) {
            log.info("loading topicFilters");
            //log.info(["loading topicFilters",soft]);
            ns.initFilter(base, soft);

            base.data("modulesloaded","topicFilters");
        }


    };

    ns.topicFilterNotify = new ko.subscribable();

    /**
     * if selected -> notify -> filterLineSelected
     * @param name
     * @param filter
     * @param isdefault
     * @constructor
     */
    ns.FilterLine = function(name,filter,isdefault) {
        var _self = this;
        //Default value is false
        _self.selected = ko.observable(false);
        _self.name = ko.observable(name);
        _self.filter = ko.observable(filter);
        _self.isdefault = ko.observable(isdefault || false);

        //Function to be invoked because event click
        _self.select = function () {
            log.info(["FilterLine","selected",_self.name()]);

            //This is Observable
            _self.selected(true);
            ns.topicFilterNotify.notifySubscribers(_self, ns.topicQueue());
        };

        _self.unselect = function () {
            log.info(["FilterLine","unselected",_self.name()]);
            _self.selected(false);
        };

    };


    /**
     * Event -> Current Filter (Observer)
     *      notify -> filter
     *
     * Event ->
     *      subscribe -> filterLineSelected
     *      Update the current filter
     *
     * @param filters
     * @constructor
     */
    ns.FilterModel = function(filters) {

        //log.info(["FilterModel", filters,"ns.topicFilterNotify.subscribe:"]);


        var _self = this;

        _self.filters = ko.observableArray();

        //register current filters as observable
        _self.currentFilter =  ko.observableArray();

        //listen for selections in the filters - watch for global events and update local value
        ns.topicFilterNotify.subscribe(function(filterLine) {
            log.info(["FilterModel", "ns.topicFilterNotify.subscribe:",filterLine.name()]);
            var currentFilter = _self.currentFilter()[0].filter();
            var newFilter = filterLine.filter();
            //log.info(["FilterModel", "index check",currentFilter,newFilter]);
            if (currentFilter !== newFilter) {
                //log.info("changing filter");
                if (_self.currentFilter().length > 0) {
                    log.info("index check");
                    _self.currentFilter()[0].unselect();
                    _self.currentFilter().pop();
                }
                log.info(["FilterModel", "index check", filterLine ]);
                _self.currentFilter().push(filterLine);
            } else {
                log.info(["FilterModel", "already selected filter"]);
            }
        }, _self, ns.topicQueue());
        //log.info(["Finished topicFilterNotify"]);

        //add filters to array
        _.each(filters, function(filter) {

            //log.info(["filter", filter["name"],filter["filter"],filter["isdefault"]]);
            var newindex = _self.filters.push(new ns.FilterLine(filter["name"],filter["filter"],filter["isdefault"]));
            var isDefault = filter["isdefault"]?filter["isdefault"]:false;
            if (isDefault) {
                //_self.filters()[newindex-1].selected(true);
                _self.currentFilter().push(_self.filters()[newindex-1]);
                _self.currentFilter()[0].selected(true);
                ns.topicFilterNotify.notifySubscribers(_self.currentFilter()[0], ns.topicQueue());
            }
            log.info(["FilterModel", _self.filters().length,newindex,_self.filters()[newindex-1],filter["name"],filter["filter"],filter["isdefault"]]);
            //log.info(["FilterModel", _self.filters().length,newindex,_self.filters()[newindex-1],filter["name"],filter["filter"],filter["isdefault"],_self.currentFilter()[0].name()]);
        });

        //After the rendition, it notifies the subscribers the default filter
        _self.renderedHandler = function(elements, data) {
            //log.info(["Trying to fire the default value as filter",data]);
            if(data.isdefault() == true){
                //log.info(["Firing the default value as filter",data]);
                ns.topicFilterNotify.notifySubscribers(data, ns.topicQueue());
            }
        };

        //log.info(["Finished each"]);


    };

    /**
     * Event -> subscribe ->  filterLineSelected
     * remove current filter and push new filter as current filter
     * @constructor
     */
    ns.FilterListen = function() {

        var _self = this;

        //register current filter as observable
        _self.currentFilter =  ko.observableArray();

        _self.currentFilterText = ko.observable();

        //listen for selections in the filters - watch for global events and update local value
        ns.topicFilterNotify.subscribe(function(filterLine) {
            log.info(["FilterListen","ns.topicFilterNotify.subscribe:",filterLine.name()]);
            if (_self.currentFilter().length > 0) {
                _self.currentFilter().pop();
            }
            _self.currentFilter().push(filterLine);
            _self.currentFilterText(filterLine.name());

            log.info(["FilterListen","new filter",filterLine.name()]);
        }, _self, ns.topicQueue());

    };

    /**
     *
     * @param $el
     * @returns {*}
     */
    ns.initFilter = function($el, soft) {

        ko.cleanNode($el[0]);

        var availableFilters = $(soft).data("filters");

        if (ns.topicQueue() == undefined){
           _topicQueue = $(soft).data("topicqueue");
        }

        log.info(["init topic filters ", availableFilters, ns.topicQueue()]);

        ko.applyBindings(new ns.FilterModel(availableFilters), soft); // This makes Knockout get to work

        return $el; //chaining
    };


})(AEMDESIGN.jQuery,_,ko, AEMDESIGN.components.topicFilter, AEMDESIGN.log, this); //pass in additional dependencies