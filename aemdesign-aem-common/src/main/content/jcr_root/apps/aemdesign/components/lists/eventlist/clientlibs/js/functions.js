//eventfilter - functions
window.AEMDESIGN = window.AEMDESIGN || { "jQuery": {} };
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.eventlist = AEMDESIGN.components.eventlist || {};

(function ($, _, ko, ns, log, window, undefined) { //add additional dependencies

  "use strict";
  var _version = "0.1";

  var _topicQueue;


  ns.version = function () {
    return _version;
  };


  ns.topicQueue = function () {
    return _topicQueue;
  };


  ns.loadIsotopeGrid = function (soft) {


    var base = $(soft);
    var alreadyLoaded = false;

    if (soft) {
      if (base.data("modulesloaded") == "eventList") {
        alreadyLoaded = true;
      }
    }

    log.info("alreadyLoaded: " + alreadyLoaded);

    if (!alreadyLoaded) {
      log.info("loading eventList");

      ns.init(base, soft);

      base.data("modulesloaded", "eventList");

    }
  };

  /**
   * Initialize the TopicMapModel for subscribing Topic Filter
   * @param map
   */
  ns.topicIsotopeGridModel = function (gridContainer) {

    var _self = this;


    _self.grid = gridContainer;

    log.info("topicIsotopeGridModel is started !!! ");

    //register current filter as observable
    _self.currentFilter = ko.observableArray();
    _self.currentFilterText = ko.observable();

    //listen for selections in the filters - watch for global events and update local value
    window.AEMDESIGN.components.topicFilter.topicFilterNotify.subscribe(function (filterLine) {

      log.info(["GridModel", "Filter", "ns.isotopeNotify.subscribe:", filterLine.name()]);
      if (_self.currentFilter().length > 0) {
        _self.currentFilter().pop();
      }
      _self.currentFilter().push(filterLine);
      _self.currentFilterText(filterLine.name());

      var filterValue = filterLine.filter();
      if (window.AEMDESIGN.components.topicFilter.CONST_ALL().test(filterLine.filter())) {
        filterValue = "*";
      } else {
        filterValue = "." + filterValue.replace(/[^a-zA-Z]/g, '-');

      }

      _self.grid.isotope({ filter: filterValue });

      log.info(["GridModel", "Listen", "new filter", filterLine.name()]);

    }, _self, ns.topicQueue());

    log.info(["topicIsotopeGridModel", "loaded"]);
  };

  //functions
  ns.topicEventListFilterAction = function (filterLine) {
    log.info(["topicEventListFilterAction", this.currentFilter(), this.currentFilter()]);
    if (this.currentFilter().length > 0) {
      this.currentFilter().pop();
    }
    this.currentFilter().push(filterLine);
    this.currentFilterText(filterLine.name());

    var filterValue = filterLine.filter();
    filterValue = ns.filterFns[filterValue] || filterValue;
    this.grid.isotope({ filter: filterValue });

    log.info(["topicEventListFilterAction", "Listen", "new filter", filterLine.name()]);

  };


  ns.init = function ($el, $element) {

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


    if (ns.topicQueue() == undefined) {
      _topicQueue = $($element).data("topicqueue");
    }

    ko.applyBindings(new ns.topicIsotopeGridModel($container), $element); // This makes Knockout get to work
    log.info("init isotope vertical");
    return $el; //chaining

  };

})(AEMDESIGN.jQuery, _, ko, AEMDESIGN.components.eventlist, AEMDESIGN.log, this); //pass in additional dependencies
