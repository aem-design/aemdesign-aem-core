//topic - behaviour
window.WKCD = window.WKCD || {};
window.WKCD.jQuery = window.jQuery || {};
window.WKCD.$ = window.jQuery || $;
window.WKCD.components = WKCD.components || {};
window.WKCD.components.topicFilter = WKCD.components.topicFilter || {};

;(function ($, _, ko, ns, window, undefined) { //add additional dependencies

    $(document).ready(function () {


        $("[data-modules='topicFilters']").each(function () {
            //WKCD.log.enableLog();
            ns.loadTopicFilters(this);

        });

    });

})(WKCD.jQuery,_,ko, WKCD.components.topicFilter, this); //pass in additional dependencies