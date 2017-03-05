//topic - behaviour
window.AEMDESIGN = window.AEMDESIGN || {};
window.AEMDESIGN.jQuery = window.jQuery || {};
window.AEMDESIGN.$ = window.jQuery || $;
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.topicFilter = AEMDESIGN.components.topicFilter || {};

;(function ($, _, ko, ns, window, undefined) { //add additional dependencies

    $(document).ready(function () {


        $("[data-modules='topicFilters']").each(function () {
            //AEMDESIGN.log.enableLog();
            ns.loadTopicFilters(this);

        });

    });

})(AEMDESIGN.jQuery,_,ko, AEMDESIGN.components.topicFilter, this); //pass in additional dependencies