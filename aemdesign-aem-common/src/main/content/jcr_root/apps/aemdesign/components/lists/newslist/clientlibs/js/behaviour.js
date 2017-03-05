//filter - behaviour
window.WKCD = window.WKCD || {};
window.WKCD.jQuery = window.jQuery || {};
window.WKCD.$ = window.jQuery || $;
window.WKCD.components = WKCD.components || {};
window.WKCD.components.newsfilter = WKCD.components.newsfilter || {};

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

})(WKCD.jQuery,_,ko, WKCD.components.newsfilter, this); //pass in additional dependencies