//search - functions
window.AEMDESIGN = window.AEMDESIGN || {"jQuery":{}};
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.search = AEMDESIGN.components.search || {};

(function ($, _, ko, utils, ns, window, undefined) {

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
            console.log("feed urls not set");
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
        function getSuggestions(urls) {
            // Retrieve data if array still contains URLs

            if(urls.length) {
                var url = urls.shift();

                $.ajax({
                    url: url,
                    type: "GET",
                    dataType: 'xml'
                }).done(function(response) {
                    var responseJson = $.xml2json(response);

                    //try to get items first, RSS
                    var items = utils.jsonPath(responseJson,"$..item");

                    //if not found try entry, ATOM
                    if (!items) {
                        items = utils.jsonPath(responseJson,"$..entry");
                    }

                    if (items) {
                        // Append results from response to all prefetched suggestions
                        prefetchedSuggestions = prefetchedSuggestions.concat(items[0]);
                    }

                    getSuggestions(urls);
                });
            } else {
                initSearchSuggestions();
            }
        }
        //
        // /**
        //  * Give 'isModel' flag to array of suggestion items
        //  * @param {array} items Array of suggestions passed in from prefetch function
        //  */
        // function setTemplatedModel(items) {
        //     // First array of suggestions should always have the thumbnail displayed
        //     // The 'isModel' property serves as a flag in the template
        //     if(counter === 0) {
        //         _.each(items, function(item) {
        //             item.isTemplated = true;
        //         });
        //     } else {
        //         _.each(items, function(item) {
        //             item.isTemplated = false;
        //         });
        //     }
        //
        //     counter++;
        //
        //     return items;
        // }

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
                    hint: true,
                    highlight: true,
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
                }
            );
        }

        getSuggestions(suggestionURLS);


        return $el;
    };


})(AEMDESIGN.jQuery,_,ko, AEMDESIGN.utils, AEMDESIGN.components.search, this);

