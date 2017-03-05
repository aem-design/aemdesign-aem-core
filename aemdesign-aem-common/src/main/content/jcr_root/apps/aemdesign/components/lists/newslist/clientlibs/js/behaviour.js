//filter - behaviour
window.AEMDESIGN = window.AEMDESIGN || {};
window.AEMDESIGN.jQuery = window.jQuery || {};
window.AEMDESIGN.$ = window.jQuery || $;
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.newsfilter = AEMDESIGN.components.newsfilter || {};

;(function ($, _, ko, ns, window, undefined) { //add additional dependencies

    $(document).ready(function () {

        $("[data-modules='isotopeGrid']").each(function () {

            ns.loadIsotopeGrid(true);

        });

        $("[data-modules='isotopeFiltersStatus']").each(function () {

            ns.loadIsotopeFiltersStatus(true);

        });


        $("[data-modules='isotopeFilters']").each(function () {

            ns.loadIsotopeFilters(true);

        });

        $("[data-modules='isotopeSortStatus']").each(function () {

            ns.loadIsotopeSortStatus(true);

        });


        $("[data-modules='isotopeSortBy']").each(function () {

            ns.loadIsotopeSortBy(true);

        });

        $("[data-modules='isotopeAppend']").each(function () {

            ns.loadIsotopeAppend(true);

        });


    });

})(AEMDESIGN.jQuery,_,ko, AEMDESIGN.components.newsfilter, this); //pass in additional dependencies