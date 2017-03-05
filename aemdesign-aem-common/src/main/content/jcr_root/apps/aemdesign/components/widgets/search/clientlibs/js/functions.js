//search
window.WKCD = window.WKCD || {};
window.WKCD.components = WKCD.components || {};
window.WKCD.components.search = WKCD.components.search || {};

;(function ($, _, ko, ns, window, undefined) {

    "use strict";
    var _version = "0.1";

    _.templateSettings = {
        interpolate: /\<\@\=(.+?)\@\>/gim,
        evaluate: /\<\@([\s\S]+?)\@\>/gim,
        escape: /\<\@\-(.+?)\@\>/gim
    };

    ns.version = function () {
        return _version;
    };

    ns.init = function($el) {
        /**
         * HTML template for search suggestion dropdown item
         * @type {string}
         */
        var suggestionTpl = _.template($('.suggestion-tpl', $el).html());
        /**
         * URLs to fetch suggestions from. Contents of data-feed-urls on $el are
         * converted to an array
         * @type {array}
         */
        //quick fail
        if ($el.data('feed-urls')===undefined) {
            return;
        }
        var suggestionURLS = $el.data('feed-urls').split(',');
        /**
         * Placeholder for suggestions
         * @type {Array}
         */
        var prefetchedSuggestions = [];
        var counter = 0;

        /**
         * Recursive function that retrieves suggestion data and intialises
         * Typeahead plugin once done.
         * @param  {array} urls Array of suggestion URLs
         */
        function prefetchSuggestions(urls) {
            // Retrieve data if array still contains URLs
            if(urls.length) {
                var url = urls.shift();

                $.ajax({
                    url: url,
                    type: "GET",
                    dataType: 'xml'
                }).done(function(response) {
                    var responseJson = $.xml2json(response);
                    var items = responseJson.channel.item;

                    // Add isModel flag
                    items = setToCarModel(items);

                    // Append results from response to all prefetched suggestions
                    prefetchedSuggestions = prefetchedSuggestions.concat(items);

                    prefetchSuggestions(urls);
                });
            } else {
                initSearchSuggestions();
            }
        }

        /**
         * Give 'isModel' flag to array of suggestion items
         * @param {array} items Array of suggestions passed in from prefetch function
         */
        function setToCarModel(items) {
            // First array of suggestions should always have the thumbnail displayed
            // The 'isModel' property serves as a flag in the template
            if(counter === 0) {
                _.each(items, function(item) {
                    item.isModel = true;
                });
            } else {
                _.each(items, function(item) {
                    item.isModel = false;
                });
            }

            counter++;

            return items;
        }

        /**
         * Initialise Typeahead suggestions plugin
         */
        function initSearchSuggestions() {
            var suggestions = new Bloodhound({
                datumTokenizer: Bloodhound.tokenizers.obj.whitespace('title'),
                queryTokenizer: Bloodhound.tokenizers.whitespace,
                local: prefetchedSuggestions,
                limit: 10
            });

            suggestions.initialize();

            $('input[type="search"]', $el).typeahead(
                {
                    hint: false,
                    minLength: 2
                },
                {
                    name: 'search-results',
                    displayKey: 'title',
                    // `ttAdapter` wraps the suggestion engine in an adapter that
                    // is compatible with the typeahead jQuery plugin
                    source: suggestions.ttAdapter(),
                    templates: {
                        suggestion: suggestionTpl
                    }
                });
        }

        prefetchSuggestions(suggestionURLS);

        return $el;
    };

})(WKCD.jQuery,_,ko, WKCD.components.search, this);

