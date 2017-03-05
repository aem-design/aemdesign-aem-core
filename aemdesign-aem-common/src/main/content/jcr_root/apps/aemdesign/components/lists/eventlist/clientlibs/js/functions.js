//filter - functions
window.WKCD = window.WKCD || {};
window.WKCD.jQuery = window.jQuery || {};
window.WKCD.$ = window.jQuery || $;
window.WKCD.components = WKCD.components || {};
window.WKCD.components.eventfilter = WKCD.components.eventfilter || {};

;(function ($, _, ko, ns, log, window, undefined) { //add additional dependencies

    "use strict";
    var _version = "0.1";

    var _topicQueue;


    ns.version = function () {
        return _version;
    };


    ns.topicQueue = function() {
        return _topicQueue;
    }


    ns.loadIsotopeGrid = function(soft) {


        var base = $(soft);
        var alreadyLoaded = false;

        if (soft) {
            if (base.data("modulesloaded")=="eventList") {
                alreadyLoaded = true;
            }
        }

        log.log("alreadyLoaded: "+alreadyLoaded);

        if (!alreadyLoaded) {
            log.log("loading eventList");

            ns.init(base, soft);

            base.data("modulesloaded","eventList");

        }
    };

    /**
     * Initialize the TopicMapModel for subscribing Topic Filter
     * @param map
     */
    ns.topicIsotopeGridModel = function(gridContainer) {

        var _self = this;


        _self.grid = gridContainer;

        log.log("topicIsotopeGridModel is started !!! ");

        //register current filter as observable
        _self.currentFilter =  ko.observableArray();
        _self.currentFilterText = ko.observable();

        //listen for selections in the filters - watch for global events and update local value
        window.WKCD.components.topicFilter.topicFilterNotify.subscribe( function(filterLine) {

            log.log(["GridModel", "Filter","ns.isotopeNotify.subscribe:",filterLine.name()]);
            if (_self.currentFilter().length > 0) {
                _self.currentFilter().pop();
            }
            _self.currentFilter().push(filterLine);
            _self.currentFilterText(filterLine.name());

            var filterValue = filterLine.filter();
            if (window.WKCD.components.topicFilter.CONST_ALL().test(filterLine.filter())){
                filterValue = "*";
            }else{
                filterValue = "." +filterValue.replace(/[^a-zA-Z]/g, '-');

            }

            _self.grid.isotope({ filter: filterValue });

            log.log(["GridModel","Listen","new filter",filterLine.name()]);

        } , _self, ns.topicQueue());

        log.log(["topicIsotopeGridModel","loaded"]);
    };

    //functions
    ns.topicEventListFilterAction = function(filterLine) {
        console.log(["topicEventListFilterAction",this.currentFilter(),this.currentFilter()]);
        if (this.currentFilter().length > 0) {
            this.currentFilter().pop();
        }
        this.currentFilter().push(filterLine);
        this.currentFilterText(filterLine.name());

        var filterValue = filterLine.filter();
        filterValue = ns.filterFns[ filterValue ] || filterValue;
        this.grid.isotope({ filter: filterValue });

        log.log(["topicEventListFilterAction","Listen","new filter",filterLine.name()]);

    };



    ns.init = function($el, $element) {

        ko.cleanNode($el);

        var $container = $($element).isotope({
            itemSelector: '.element-item',
            layoutMode: 'fitRows',
            transitionDuration: 0,
            hiddenStyle: {
                opacity: 0,
                transform: 'scale(0.001)'
            },
            visibleStyle: {
                opacity: 1,
                transform: 'scale(1)'
            },
            // options for masonry layout mode
            masonry: {
                columnWidth: '.grid-sizer'
            }
        });


        if (ns.topicQueue() == undefined){
            _topicQueue = $($element).data("topicqueue");
        }

        ko.applyBindings(new ns.topicIsotopeGridModel($container), $element); // This makes Knockout get to work
        log.log("init isotope vertical");
        return $el; //chaining

    };

})(WKCD.jQuery,_,ko, WKCD.components.eventfilter, WKCD.log, this); //pass in additional dependencies