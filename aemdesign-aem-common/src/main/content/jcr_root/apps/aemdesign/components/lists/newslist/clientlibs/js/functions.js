//newslist - functions
window.AEMDESIGN = window.AEMDESIGN || {"jQuery":{}};
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.newsfilter = AEMDESIGN.components.newsfilter || {};

(function ($, _, ko, ns, log, window, undefined) { //add additional dependencies

    "use strict";
    var _version = "0.1";

    ns.version = function () {
        return _version;
    };

    //KO Helpers functions
    ////usage of publishOn
    //this.myObservable = ko.observable("myValue").publishOn("myTopic");
    ko.subscribable.fn.publishOn = function(topic) {
        this.subscribe(function(newValue) {
            ns.isotopeNotify.notifySubscribers(newValue, topic);
        });

        return this; //support chaining
    };

    ////usage
    //this.observableFromAnotherVM = ko.observable().subscribeTo("myTopic");
    ko.subscribable.fn.subscribeTo = function(topic) {
        ns.isotopeNotify.subscribe(this, null, topic);
        return this;  //support chaining
    };



    //ISOTOPE Helpers functions
    ns.filterFns = {
        // show if number is greater than 50
        numberGreaterThan50: function() {
            var number = $(this).find('.number').text();
            return parseInt( number, 10 ) > 50;
        },
        // show if name ends with -ium
        ium: function() {
            var name = $(this).find('.name').text();
            return name.match( /ium$/ );
        }
    };


    ns.loadIsotopeGrid = function(soft) {
        var base = $(this);
        var alreadyLoaded = false;

        if (soft) {
            if (base.data("modulesloaded")=="isotopeGrid") {
                alreadyLoaded = true;
            }
        }

        log.info("alreadyLoaded: "+alreadyLoaded);

        if (!alreadyLoaded) {
            log.info("loading isotopeGrid");

            ns.init(base);

            base.data("modulesloaded","isotopeGrid");

        }
    };

    ns.loadIsotopeFiltersStatus = function(soft) {

        var base = $(this);
        var alreadyLoaded = false;

        if (soft) {
            if (base.data("modulesloaded")=="isotopeFiltersStatus") {
                alreadyLoaded = true;
            }
        }

        log.info("alreadyLoaded: "+alreadyLoaded);

        if (!alreadyLoaded) {
            log.info("loading isotopeFiltersStatus");

            ns.initFilterListen(base);

            base.data("modulesloaded","isotopeFiltersStatus");
            log.info("loaded isotopeFiltersStatus");
        }

    };



    ns.loadIsotopeFilters = function(soft) {

        var base = $(this);
        var alreadyLoaded = false;

        if (soft) {
            if (base.data("modulesloaded")=="isotopeFilters") {
                alreadyLoaded = true;
            }
        }

        log.info("alreadyLoaded: "+alreadyLoaded);

        if (!alreadyLoaded) {
            log.info("loading isotopeFilters");
            ns.initFilters(base);

            base.data("modulesloaded","isotopeFilters");
        }


    };

    ns.loadIsotopeSortStatus = function(soft) {

        var base = $(this);
        var alreadyLoaded = false;

        if (soft) {
            if (base.data("modulesloaded")=="isotopeSortStatus") {
                alreadyLoaded = true;
            }
        }

        log.info("alreadyLoaded: "+alreadyLoaded);

        if (!alreadyLoaded) {
            log.info("loading isotopeSortStatus");

            ns.initSortListen(base);

            base.data("modulesloaded","isotopeSortStatus");
            log.info("loaded isotopeSortStatus");
        }


    };



    ns.loadIsotopeSortBy = function(soft) {
        var base = $(this);
        var alreadyLoaded = false;

        if (soft) {
            if (base.data("modulesloaded")=="isotopeSortBy") {
                alreadyLoaded = true;
            }
        }

        log.info("alreadyLoaded: "+alreadyLoaded);

        if (!alreadyLoaded) {
            log.info("loading isotopeSortBy");
            ns.initSortBy(base);

            base.data("modulesloaded","isotopeSortBy");
            log.info("loaded isotopeSortBy");
        }

    };




    ns.loadIsotopeAppend = function(soft) {

        var base = $(this);
        var alreadyLoaded = false;

        if (soft) {
            if (base.data("modulesloaded")=="isotopeAppendByList") {
                alreadyLoaded = true;
            }
        }

        log.info("alreadyLoaded: "+alreadyLoaded);

        if (!alreadyLoaded) {
            log.info("loading isotopeAppendByList");

            // ns.initSortBy(base);
            ns.initAppendBy(base);

            base.data("modulesloaded","isotopeAppendByList");
            log.info("loaded isotopeAppendByList");
        }

    };


    ns.load = function(soft) {


    };

    ns.isotopeNotify = new ko.subscribable();

    ns.FilterLine = function(name,filter,isdefault) {
        var _self = this;
        _self.selected = ko.observable(false);
        _self.name = ko.observable(name);
        _self.filter = ko.observable(filter);
        _self.isdefault = ko.observable(isdefault || false);

        _self.select = function () {
            log.info(["FilterLine","selected",_self.name()]);
            _self.selected(true);
            ns.isotopeNotify.notifySubscribers(_self, "filterLineSelected");
        };

        _self.unselect = function () {
            log.info(["FilterLine","unselected",_self.name()]);
            _self.selected(false);
        };

    };


    //functions
    ns.FilterModel = function(filters) {

        var _self = this;

        _self.filters = ko.observableArray();

        //register current filter as observable
        _self.currentFilter =  ko.observableArray();

        //tell everyone that current filter has changed - when current filter is updated send to watchers
        _self.currentFilter.subscribe(function(filterLine) {
            log.info(["FilterModel", "_self.currentFilter.subscribe:",filterLine.name()]);
            ns.isotopeNotify.notifySubscribers(filterLine, "filter");
        });

        //listen for selections in the filters - watch for global events and update local value
        ns.isotopeNotify.subscribe(function(filterLine) {
            log.info(["FilterModel", "ns.isotopeNotify.subscribe:",filterLine.name()]);
            var currentFilter = _self.currentFilter()[0].filter();
            var newFilter = filterLine.filter();
            log.info(["FilterModel", "index check",currentFilter,newFilter]);
            if (currentFilter !== newFilter) {
                log.info("changing filter");
                if (_self.currentFilter().length > 0) {
                    log.info("index check");
                    _self.currentFilter()[0].unselect();
                    _self.currentFilter().pop(0);
                }
                log.info(["FilterModel", "index check", filterLine ]);
                _self.currentFilter().push(filterLine);
            } else {
                log.info(["FilterModel", "already selected filter"]);
            }
        }, _self, "filterLineSelected");

        //add filters to array
        _.each(filters, function(filter) {
            var newindex = _self.filters.push(new ns.FilterLine(filter["name"],filter["filter"],filter["isdefault"]));
            var isDefault = filter["isdefault"]?filter["isdefault"]:false;
            if (isDefault) {
                //_self.filters()[newindex-1].selected(true);
                _self.currentFilter().push(_self.filters()[newindex-1]);
                _self.currentFilter()[0].selected(true);
                ns.isotopeNotify.notifySubscribers(_self.currentFilter()[0], "filterLineSelected");
            }
            //log.info(["FilterModel", _self.filters().length,newindex,_self.filters()[newindex-1],filter["name"],filter["filter"],filter["isdefault"],_self.currentFilter()[0].name()]);
        });

    };

    ns.FilterListen = function() {

        var _self = this;

        //register current filter as observable
        _self.currentFilter =  ko.observableArray();

        _self.currentFilterText = ko.observable();

        //listen for selections in the filters - watch for global events and update local value
        ns.isotopeNotify.subscribe(function(filterLine) {
            log.info(["FilterListen","ns.isotopeNotify.subscribe:",filterLine.name()]);
            if (_self.currentFilter().length > 0) {
                _self.currentFilter().pop(0);
            }
            _self.currentFilter().push(filterLine);
            _self.currentFilterText(filterLine.name());

            log.info(["FilterListen","new filter",filterLine.name()]);
        }, _self, "filterLineSelected");

    };

    ns.SortLine = function(name,sortBy,isdefault) {
        var _self = this;
        _self.selected = ko.observable(false);
        _self.name = ko.observable(name);
        _self.sortBy = ko.observable(sortBy);
        _self.isdefault = ko.observable(isdefault || false);

        _self.select = function () {
            log.info(["SortLine","selected",_self.name()]);
            _self.selected(true);
            ns.isotopeNotify.notifySubscribers(_self, "sortByLineSelected");
        };

        _self.unselect = function () {
            log.info(["SortLine","unselected",_self.name()]);
            _self.selected(false);
        };

    };

    //functions
    ns.SortModel = function(sortList) {

        var _self = this;

        log.info(["SortModel",sortList]);

        _self.sortByList = ko.observableArray();

        //register current Sort as observable
        _self.currentSortBy =  ko.observableArray();


        //tell everyone that current sort has changed - when current sort is updated send to watchers
        _self.currentSortBy.subscribe(function(sortLine) {
            log.info(["SortModel","_self.currentSortBy.subscribe:",sortLine.name()]);
            ns.isotopeNotify.notifySubscribers(sortLine, "sort");
        });

        //listen for selections in the sorts - watch for global events and update local value
        ns.isotopeNotify.subscribe(function(sortLine) {
            log.info(["SortModel","ns.isotopeNotify.subscribe:",sortLine.name()]);
            var currentSort = _self.currentSortBy()[0].sortBy();
            var newSort = sortLine.sortBy();
            log.info(["SortModel","index check",currentSort,newSort]);
            if (currentSort !== newSort) {
                log.info(["SortModel","changing sort"]);
                if (_self.currentSortBy().length > 0) {
                    log.info(["SortModel","index check"]);
                    _self.currentSortBy()[0].unselect();
                    _self.currentSortBy().pop(0);
                }
                _self.currentSortBy().push(sortLine);
            } else {
                log.info(["SortModel","already selected sort"]);
            }
        }, _self, "sortByLineSelected");

        //add filters to array
        _.each(sortList, function(sort) {
            var newindex = _self.sortByList.push(new ns.SortLine(sort["name"],sort["sort-by"],sort["isdefault"]));
            var isDefault = sort["isdefault"]?sort["isdefault"]:false;
            if (isDefault) {
                //_self.sorts()[newindex-1].selected(true);
                _self.currentSortBy().push(_self.sortByList()[newindex-1]);
                _self.currentSortBy()[0].selected(true);
                ns.isotopeNotify.notifySubscribers(_self.currentSortBy()[0], "sortByLineSelected");
            }
            log.info(["SortModel",_self.sortByList().length,newindex,_self.sortByList()[newindex-1],sort["name"],sort["sort-by"],sort["isdefault"],_self.currentSortBy()[0].name()]);
        });

        log.info(["SortModel","loaded"]);
    };

    ns.SortListen = function() {

        var _self = this;

        //register current sort as observable
        _self.currentSortBy =  ko.observableArray();

        _self.currentSortByText = ko.observable();

        //listen for selections in the filters - watch for global events and update local value
        ns.isotopeNotify.subscribe(function(sortLine) {
            log.info(["SortListen","ns.isotopeNotify.subscribe:",sortLine.name()]);
            if (_self.currentSortBy().length > 0) {
                _self.currentSortBy().pop(0);
            }
            _self.currentSortBy().push(sortLine);
            _self.currentSortByText(sortLine.name());

            log.info(["SortListen","new sortby",sortLine.name()]);
        }, _self, "sortByLineSelected");

    };

    ns.AppendLine = function(name,appendBy,isdefault) {
        var _self = this;
        _self.selected = ko.observable(false);
        _self.name = ko.observable(name);
        _self.appendBy = ko.observable(appendBy);
        _self.isdefault = ko.observable(isdefault || false);

        _self.select = function () {
            log.info(["AppendLine","selected",_self.name()]);
            _self.selected(true);
            ns.isotopeNotify.notifySubscribers(_self, "appendByLineSelected");
        };

        _self.unselect = function () {
            log.info(["AppendLine","unselected",_self.name()]);
            _self.selected(false);
        };

    };

    //functions
    ns.AppendModel = function(appendList) {

        var _self = this;

        log.info(["AppendModel",appendList]);

        _self.appendByList = ko.observableArray();

        //register current Append as observable
        _self.currentAppendBy =  ko.observableArray();


        //tell everyone that current append has changed - when current append is updated send to watchers
        _self.currentAppendBy.subscribe(function(appendList) {
            log.info(["AppendModel","_self.currentAppendBy.subscribe:",appendList.name()]);
            ns.isotopeNotify.notifySubscribers(appendList, "append");
        });

        //listen for selections in the sorts - watch for global events and update local value
        ns.isotopeNotify.subscribe(function(appendLine) {
            log.info(["AppendModel","ns.isotopeNotify.subscribe:",appendLine.name()]);

            var currentAppend = _self.currentAppendBy()[0].appendBy();
            var newAppend = appendLine.appendBy(); //.filter();
            log.info(["AppendModel", "index check",currentAppend,newAppend]);
            if (currentAppend !== newAppend) {
                log.info("changing append");
                if (_self.currentAppendBy().length > 0) {
                    log.info("index check");
                    _self.currentAppendBy()[0].unselect();
                    _self.currentAppendBy().pop(0);
                }
                log.info(["AppendModel", "index check", appendLine ]);
                _self.currentAppendBy().push(appendLine);
            } else {
                log.info(["AppendModel", "already selected append"]);
            }

        }, _self, "appendByLineSelected");

        //add filters to array
        _.each(appendList, function(append) {
            var newindex = _self.appendByList.push(new ns.AppendLine(append["name"],append["append-by"],append["isdefault"]));
            var isDefault = append["isdefault"]?append["isdefault"]:false;
            if (isDefault) {
                //_self.sorts()[newindex-1].selected(true);
                _self.currentAppendBy().push(_self.appendByList()[newindex-1]);
                _self.currentAppendBy()[0].selected(true);
                ns.isotopeNotify.notifySubscribers(_self.currentAppendBy()[0], "appendByLineSelected");
                //remove the default 0 item
                _self.appendByList.pop(0);
            }
            log.info(["AppendModel",_self.appendByList().length,newindex,_self.appendByList()[newindex-1],append["name"],append["append-by"],append["isdefault"],_self.currentAppendBy()[0].name()]);
        });

        log.info(["AppendModel","loaded"]);
    };

    ns.AppendListen = function() {

        var _self = this;

        //register current append as observable
        _self.currentAppendBy =  ko.observableArray();

        _self.currentAppendByText = ko.observable();

        //listen for selections in the filters - watch for global events and update local value
        ns.isotopeNotify.subscribe(function(appendLine) {
            log.info(["AppendListen","ns.isotopeNotify.subscribe:",appendLine.name()]);
            if (_self.currentAppendBy().length > 0) {
                _self.currentAppendBy().pop(0);
            }
            _self.currentAppendBy().push(appendLine);
            _self.currentAppendByText(appendLine.name());

            log.info(["AppendListen","new appendby",appendLine.name()]);
        }, _self, "appendByLineSelected");

    };


    //functions
    ns.GridModel = function(gridContainer) {

        var _self = this;

        _self.grid = gridContainer;

        _self.currentSortBy =  ko.observableArray();
        _self.currentSortByText = ko.observable();


        //listen for selections in the filters - watch for global events and update local value
        ns.isotopeNotify.subscribe(function(sortLine) {
            log.info(["SortListen","ns.isotopeNotify.subscribe:",sortLine.name()]);
            if (_self.currentSortBy().length > 0) {
                _self.currentSortBy().pop(0);
            }
            _self.currentSortBy().push(sortLine);
            _self.currentSortByText(sortLine.name());

            _self.grid.isotope({ sortBy: sortLine.sortBy() });

            log.info(["SortListen","new sortby",sortLine.name()]);
        }, _self, "sortByLineSelected");

        //register current filter as observable
        _self.currentFilter =  ko.observableArray();
        _self.currentFilterText = ko.observable();

        //listen for selections in the filters - watch for global events and update local value
        ns.isotopeNotify.subscribe(function(filterLine) {
            log.info(["GridModel", "Filter","ns.isotopeNotify.subscribe:",filterLine.name()]);
            if (_self.currentFilter().length > 0) {
                _self.currentFilter().pop(0);
            }
            _self.currentFilter().push(filterLine);
            _self.currentFilterText(filterLine.name());

            var filterValue = filterLine.filter();
            filterValue = ns.filterFns[ filterValue ] || filterValue;
            _self.grid.isotope({ filter: filterValue });

            log.info(["GridModel","Listen","new filter",filterLine.name()]);
        }, _self, "filterLineSelected");


        //register current filter as observable
        _self.currentAppendBy =  ko.observableArray();
        _self.currentAppendByText = ko.observable();

        //listen for selections in the filters - watch for global events and update local value
        ns.isotopeNotify.subscribe(function(appendLine) {
            log.info(["GridModel","AppendListen","ns.isotopeNotify.subscribe:",appendLine.name()]);
            if (_self.currentAppendBy().length > 0) {
                _self.currentAppendBy().pop(0);
            }
            _self.currentAppendBy().push(appendLine);
            _self.currentAppendByText(appendLine.appendBy());

            if (_self.currentAppendByText() > 0){

                var $items = getMorePage(_self.currentAppendByText());


                //_self.grid.isotope( 'insert', $items );
                _self.grid.append( $items ).isotope('appended', $items );

            }

            log.info(["GridModel","AppendListen","new Append",appendLine.name()]);
        }, _self, "appendByLineSelected");


        log.info(["GridModel","loaded"]);
    };



    ns.initFilters = function($el) {
        ko.cleanNode($el[0]);

        var availableFilters = $el.data("filters");

        ko.applyBindings(new ns.FilterModel(availableFilters), $el[0]); // This makes Knockout get to work

        log.info("init isotope filters");

        return $el; //chaining
    };

    ns.initFilterListen = function($el) {
        ko.cleanNode($el[0]);

        ko.applyBindings(new ns.FilterListen(), $el[0]); // This makes Knockout get to work

        log.info("init isotope filter listen");

        return $el; //chaining
    };

    ns.initSortBy = function($el) {
        ko.cleanNode($el[0]);

        var availableSortBy = $el.data("sort-by");

        ko.applyBindings(new ns.SortModel(availableSortBy), $el[0]); // This makes Knockout get to work

        log.info("init isotope sortby");

        return $el; //chaining
    };


    ns.initAppendBy = function($el) {
        ko.cleanNode($el[0]);

        var availableAppendBy = $el.data("append-by");

        ko.applyBindings(new ns.AppendModel(availableAppendBy), $el[0]); // This makes Knockout get to work

        log.info("init isotope appendBy");

        return $el; //chaining
    };

    ns.initSortListen = function($el) {
        ko.cleanNode($el[0]);

        ko.applyBindings(new ns.SortListen(), $el[0]); // This makes Knockout get to work

        log.info("init isotope sort listen");

        return $el; //chaining
    };

    ns.initAppendListen = function($el) {
        ko.cleanNode($el[0]);

        ko.applyBindings(new ns.AppendListen(), $el[0]); // This makes Knockout get to work

        log.info("init isotope append listen");

        return $el; //chaining
    };


    ns.init = function($el) {
        ko.cleanNode($el[0]);

        var $container = $el.isotope({
            itemSelector: '.element-item',
            layoutMode: 'vertical',
            transitionDuration: '0',
            // only opacity for reveal/hide transition
            hiddenStyle: {
                opacity: 0
            },
            visibleStyle: {
                opacity: 1
            }
        });

        ko.applyBindings(new ns.GridModel($container), $el[0]); // This makes Knockout get to work
        log.info("init isotope vertical");
        return $el; //chaining

    };

})(AEMDESIGN.jQuery,_,ko, AEMDESIGN.components.newsfilter, AEMDESIGN.log, this); //pass in additional dependencies