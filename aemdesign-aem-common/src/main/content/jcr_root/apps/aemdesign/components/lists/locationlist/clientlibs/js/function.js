//locationlist - functions
window.AEMDESIGN = window.AEMDESIGN || {};
window.AEMDESIGN.jQuery = window.jQuery || {};
window.AEMDESIGN.$ = window.jQuery || $;
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.locationlist = AEMDESIGN.components.locationlist || {};

;(function ($, _, ko, ns, log, window, undefined) { //add additional dependencies

    "use strict";
    var _version = "0.1";

    ns.version = function () {
        return _version;
    };

    if (ns.topicQueue != undefined ) {
        ns.topicQueue = "";
    }

    ns.googleMapInstances = new Array();

    ns.geoFeatureCollections = new Array();

    ns.googleMapInfowindows = new Array();


    if (ns.googleMapLoaded != undefined ) {
        ns.googleMapLoaded = false;
    }

    if (ns.googleMapCompleteLoaded != undefined ) {
        ns.googleMapCompleteLoaded = false;
    }

    ns.topicMapModelNS = function() {


        //_self.grid = gridContainer;
        ns.currentFilter =  ko.observableArray();
        ns.currentFilterText = ko.observable();

        //log.info(["topicMapModelNS",ns.topicQueue]);

        //listen for selections in the filters - watch for global events and update local value
        window.AEMDESIGN.components.topicFilter.topicFilterNotify.subscribe(function(filterLine) {

            //log.info(["MapModel", "Filter","ns.isotopeNotify.subscribe:",filterLine.name()]);
            if (ns.currentFilter().length > 0) {
                ns.currentFilter().pop();
            }
            ns.currentFilter().push(filterLine);
            ns.currentFilterText(filterLine.name());

        } , ns, ns.topicQueue);

        //log.info(["MapModel","loaded","topicMapModel2",ns.currentFilter()]);
    };


    ns.loadGoogleMap = function ($element){

        var _self = this;
        //_self.grid = gridContainer;
        var googleApiKey = $($element).data("mapapikey");

        ns.topicQueue = $($element).data("topicqueue");

        //monitor initla filter selection before maps have loaded
        ns.topicMapModelNS();

        //log.info(["MapModel","loaded"]);

        if(ns.googleMapLoaded == undefined || ns.googleMapLoaded == false) {

            if (googleApiKey){
                googleApiKey = "&key=" + googleApiKey;
            }else{
                googleApiKey = "";
            }

            var script_tag = document.createElement('script');
            script_tag.setAttribute("type", "text/javascript");
            script_tag.setAttribute("src", "//maps.googleapis.com/maps/api/js?callback=window.AEMDESIGN.components.locationlist.googleMapCallback"+googleApiKey);
            script_tag.setAttribute("async", "false");
            (document.getElementsByTagName("head")[0] || document.documentElement).appendChild(script_tag);
            ns.googleMapLoaded = true;
            log.info("Loaded Google Map API with key = " +googleApiKey);
        }
    };


    //Use 140 rendition for info window
    /**
     * Helper to build content for Info Windows
     * @param properties json
     * @returns {string}
     */
    ns.renderInfoWindows = function(p){
        var area ="<div>";
        for (var e in p.events){

            //check only number because AEM author mode has additional thing attached
            if (isNaN(Number(e))== false){
                //area +="<li style='top: 215px; left: 440px;'>";
                /*if (p.events[e].title){
                 area += "<a href='#' title='"+p.events[e].title+"'>"+ p.events[e].title +"</a>";
                 }*/
                area += "<div class='col-2 "+ p.events[e].category +"' data-filter='"+ p.events[e].category +"'>";
                if (p.events[e].image) {
                    area += "<a href='" + p.events[e].url + "'";
                    if (p.events[e].altText) {
                        area += " title='" + p.events[e].altText + "'";
                    }
                    area += ">";
                    area += "<img src='" + p.events[e].image + "'" ;
                    if (p.events[e].altText) {
                        area +=" alt='" + p.events[e].altText + "'";
                    }
                    area += ">";
                    area += "</a>";
                }
                area += "<div class='body'>";
                area += "<h4>";
                if (p.events[e].url) {
                    area += "<a href='"+p.events[e].url+"' title='"+p.events[e].title+"'>";
                }
                area += p.events[e].title;
                if (p.events[e].url) {
                    area += "</a>";
                }
                area += "</h4>";
                if (p.events[e].description) {
                    area += p.events[e].description;
                }
                area += "</div>";
                area += "</div>";
                //area += "</li>";
            }

        }
        area +="</div>"
        return area;
    };

     /**
     * Initial Google Map
     * @param targetDiv
     * @param mapOptions optional
     * @returns {google.maps.Map}
     */
    ns.initMap = function(targetDiv, mapOptions) {

        log.info("initMap !!! "+targetDiv);

        // Create a map.
        var map = new google.maps.Map(targetDiv, {
            zoom: 0,
            center: {lat: 0, lng: 0},
            draggable: false,
            mapTypeControl: false,
            sensor: 'false',
            disableDefaultUI: true
        });

        return map;
    };

    /**
     * Initial Projection System
     * @param map
     * @returns {*}
     */
    ns.initProjectionSystem = function(el,map) {

        log.info("initProjectionSystem !!! ");

        var base = $(el);

        var projectionType = base.data("map");

        log.info("initProjectionSystem projectionType = "+projectionType);

        if (projectionType === 'gallPeters'){

            var imageSourceUrl = base.data("mapimagesrc");
            log.info("initProjectionSystem imageSourceUrl = "+imageSourceUrl);

            var GALL_PETERS_RANGE_Y = base.data("mapimagey");
            log.info("initProjectionSystem GALL_PETERS_RANGE_Y = "+GALL_PETERS_RANGE_Y);

            var GALL_PETERS_RANGE_X = base.data("mapimagex");
            log.info("initProjectionSystem GALL_PETERS_RANGE_X = "+GALL_PETERS_RANGE_X);

            // Fetch Gall-Peters tiles stored locally on our server.
            var gallPetersMapType = new google.maps.ImageMapType({
                //this case only a single title
                getTileUrl: function(coord, zoom) {
//              var scale = 1 << zoom;
//
//              // Wrap tiles horizontally.
//              var x = ((coord.x % scale) + scale) % scale;
//
//              // Don't wrap tiles vertically.
//              var y = coord.y;
//              if (y < 0 || y >= scale) return null;

                    //return 'images/gall-peters_' + zoom + '_' + x + '_' + y + '.png';
                    return imageSourceUrl;
                },
                tileSize: new google.maps.Size(GALL_PETERS_RANGE_X, GALL_PETERS_RANGE_Y),
                isPng: true,
                minZoom: 0,
                maxZoom: 0,
                name: 'Gall-Peters'
            });

            // Describe the Gall-Peters projection used by these tiles.
            gallPetersMapType.projection = {
                fromLatLngToPoint: function(latLng) {
                    var latRadians = latLng.lat() * Math.PI / 180;
                    return new google.maps.Point(
                        GALL_PETERS_RANGE_X * (0.5 + latLng.lng() / 360),
                        GALL_PETERS_RANGE_Y * (0.5 - 0.5 * Math.sin(latRadians)));
                },
                fromPointToLatLng: function(point, noWrap) {
                    var x = point.x / GALL_PETERS_RANGE_X;
                    var y = Math.max(0, Math.min(1, point.y / GALL_PETERS_RANGE_Y));

                    return new google.maps.LatLng(
                        Math.asin(1 - 2 * y) * 180 / Math.PI,
                        -180 + 360 * x,
                        noWrap);
                }
            };

            map.mapTypes.set(projectionType, gallPetersMapType);
            map.setMapTypeId(projectionType);

        }else {

            log.info("projectionType is not defined");
        }

        return map;
    };


    /**
     * Wrapping up Google Map Logic to add Info Windows
     * @param map
     * @returns {*}
     */
    ns.updateInfoWindows = function(map) {
        // global infowindow

        var infoWindowOptions = {
            disableAutoPan:true
        };

        ns.googleMapInfowindows.push(new google.maps.InfoWindow(infoWindowOptions)) ;


        // When the user clicks, open an infowindow
        map.data.addListener('click', function(event) {


            for (var property in event.feature) {
                if (event.feature.hasOwnProperty(property)) {

                    if (event.feature[property] != undefined && event.feature[property].hasOwnProperty('events')){

                        var p = event.feature[property];
                        //log.info(ns.renderInfoWindows(p));
                        _.first(ns.googleMapInfowindows).setContent(ns.renderInfoWindows(p));

                        _.first(ns.googleMapInfowindows).setPosition(event.feature.getGeometry().get());
                        //Remove the pixelOffset to avoid the Tile justified
                        //infowindow.setOptions({pixelOffset: new google.maps.Size(0,-30)});
                        //Set the display order
            //            _.first(ns.googleMapInfowindows).setZIndex(event.feature.N.zIndex);
                        _.first(ns.googleMapInfowindows).open(map);
                    }

                }
            }

        });

        return map;
    };


    /**
     *  Wrapping up Google Map Logic to add GeoJson
     * @param map
     * @returns {*}
     */
    ns.updateGeoJson = function(el, map) {
        log.info("updateGeoJson !!! ");

        var base = $(el);
        var locationsJsonString = base.data("maplocationsid");
         //Calls the function below to load up all the map markers.
        var locations = eval(locationsJsonString);

        ns.geoFeatureCollections.push(locations);

        map.data.addGeoJson(locations);

        return map;
    };


    /**
     * Wrapping up Google Map Logic to add Marker
     * @param map
     * @returns {*}
     */
    ns.updateMarkers = function(el, map) {

        log.info("updateMarkers !!! ");
        // Add some markers to the map.
        map.data.setStyle(function(feature) {
            var venue = feature.getProperty('venue');
            var categoryIconColor = feature.getProperty('categoryIconColor');
            var categoryIconPath = feature.getProperty('categoryIconPath');
            var markerPointX = feature.getProperty('markerPointX');
            var markerPointY = feature.getProperty('markerPointY');

            return {
                icon: {
                    path: eval(categoryIconPath),
                    scale: 0.7,
                    strokeWeight: 0.2,
                    strokeColor: 'black',
                    strokeOpacity: 1,
                    fillColor: categoryIconColor,
                    fillOpacity: 1,
                    anchor:new google.maps.Point(markerPointX, markerPointY)
                },
                //icon:categoryIconPath,
                optimized: false,
                visible: true,
                clickable: true,
                title: venue
            };
        });
        return map;
    };


    /**
     * Show the lat and lng under the mouse cursor.
     * @param map
     * @returns {*}
     */
    ns.showCoordinationMessage = function (el, map){

        log.info("showCoordinationMessage !!! ");

        var base = $(el);

        if (base.data("wcmmode") != 'disabled' && base.data("wcmmode") != undefined ){

            //create a div for debug message
            var coordsDiv = document.createElement("div");

            el.parentNode.appendChild(coordsDiv);

            // Show the lat and lng under the mouse cursor.
            map.controls[google.maps.ControlPosition.TOP_CENTER].push(coordsDiv);

            //EDIT MODE
            map.addListener('mousemove', function(event) {
                coordsDiv.textContent =
                    'latitude : ' + Math.round(event.latLng.lat()) + ', ' +
                    'longitude : ' + Math.round(event.latLng.lng());
            });

        }
        return map;
    };

    ns.handleResponsiveMap = function(map){
        google.maps.event.addDomListener(window, "resize", function() {
            //log.info("Resize !!!!");
            google.maps.event.trigger(map, "resize");
            map.setCenter( {lat: 0, lng: 0});
        });
        return map;
    };

    /**
     * Set the value true to indicate the Google Map is loaded
     * @param map
     */
    ns.handleIdleMap = function(map){
        google.maps.event.addListenerOnce(map, 'idle', function(){
            ns.googleMapCompleteLoaded = true;
        });
        return map;
    };

    ns.googleMapCallback = function () {

        log.info(["googleMapCallback start !!!",ns.currentFilter()]);
        $("[data-modules='map']").each(function () {

            var map = ns.initMap($(this).get(0));

            map = ns.initProjectionSystem(this, map);

            map = ns.updateMarkers(this, map);

            map = ns.showCoordinationMessage(this, map);

            map = ns.updateGeoJson(this, map);

            map = ns.updateInfoWindows(map);

            map = ns.handleResponsiveMap(map);

            map = ns.handleIdleMap(map);

            ns.googleMapInstances.push(map);

            ns.topicQueue = $(this).data("topicqueue");

            //log.info(["need to select defauts1",map,ns.currentFilter()]);
            /**
             * TODO:handle the default is not all
             */
            if (ns.topicQueue != undefined && ns.topicQueue.length > 0){
                ns.topicMapModel(map);
            }

            if (ns.currentFilter().length > 0) {
                ns.filterMap(map,ns.currentFilter()[0]);
            }
        });
        log.info("googleMapCallback end !!! ");
    };

    /***
     * filter items shown on the map
     * @param map
     * @param filterLine
     */
    ns.filterMap = function(map,filterLine) {

        if (window.AEMDESIGN.components.topicFilter.CONST_ALL().test(filterLine.filter())){

            //default remove all
            map.data.forEach(function(feature) {
                map.data.remove(feature);
            });
            //log.info(["geoFeatureCollections ",_.first(this.geoFeatureCollections)]);

            //log.info(["actualVar ",this.geoFeatureCollections]);
            map.data.addGeoJson(_.first(this.geoFeatureCollections));

        }else {

            //Using Deep clone to create a new set of records for the map markers.
            var actualVar = jQuery.extend(true, {}, _.first(ns.geoFeatureCollections));

            var filteredOutLocations = _.filter(actualVar.features, function (feature) {

                if (feature.properties != undefined) {
                    //Unneeded Event
                    var events = _.filter(feature.properties.events, function (event) {

                        var category = event.category.split(',');

                        if (_.contains(category, filterLine.filter())) {
                            // log.info(["feature.properties.events.category ", event.category]);
                            return event;
                        }

                    });

                    //log.info(["feature.properties.events ",events]);

                    if (events != undefined && events.length > 0) {
                        feature.properties.events = events;
                        //log.info(["feature ",feature]);
                        return feature;
                    }
                }

            });

            //log.info(["filteredLocation ",filteredOutLocations]);
            log.info(["filteredLocation ", filteredOutLocations]);

            //default remove all
            map.data.forEach(function (feature) {
                //If you want, check here for some constraints.
                //log.info(["removeFeature ",feature]);
                map.data.remove(feature);
            });
            //Adding back the marker
            var filterFeatureCollection = {
                type: 'FeatureCollection',
                features: {}
            };
            filterFeatureCollection.features = filteredOutLocations;

            //log.info(["filterFeatures ",filterFeatureCollection]);
            map.data.addGeoJson(filterFeatureCollection);

        }
    };

    /**
     * Initialize the TopicMapModel for subscribing Topic Filter
     * @param map
     */
    ns.topicMapModel = function() {

        var _self = this;
        //_self.grid = gridContainer;

        //listen for selections in the filters - watch for global events and update local value
        window.AEMDESIGN.components.topicFilter.topicFilterNotify.subscribe(ns.topicMapFilterAction , _self, ns.topicQueue);

        log.info(["MapModel","loaded"]);
    };


    ns.topicMapFilterAction = function(filterLine) {

        //log.info(["mapModel", "Filter",this.currentFilter(),filterLine,filterLine.name(),filterLine.filter(),filterLine.isdefault(), this]);

        //Close infowindow before filter
        _.first(this.googleMapInfowindows).close();

        var map = _.first(this.googleMapInstances);

        ns.filterMap(map,filterLine);

        log.info(["mapModel","Listen","new filter",filterLine.name()]);

    }

})(AEMDESIGN.jQuery,_,ko, AEMDESIGN.components.locationlist, AEMDESIGN.log, window); //pass in additional dependencies