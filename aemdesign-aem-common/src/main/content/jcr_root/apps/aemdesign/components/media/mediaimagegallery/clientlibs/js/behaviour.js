//(function($, undefined) {
//
//    $(document).ready(function() {
//
//        //galleria
//        /*    $(document).ready(function() {
//         $(".media-gallery").each(function() {
//
//         var base = $(this);
//
//         base.mediaGallery({
//         input : base.find(".gallery-content"),
//         theme : base.data("theme"),
//         galleriaOptions : {
//         transition: 'fade',
//         width: 800,
//         height: 600,
//         maxScaleRatio: 1
//         }
//         });
//
//         });
//
//         });*/
//
//        //new gallery
//
//
//        var $container = $('.gallery-inner'),
//            $filters = {},
//            $filterDropdowns = $('.js-filter-dropdown'),
//            $galleryThumbs = $('.gallery-thumb'),
//            $filterOpenClass = 'filter-select--open';
//
//
//        $galleryThumbs.fancybox({
//            padding: 0,
//            openEffect: 'none',
//            closeEffect: 'none',
//            beforeLoad: function () {
//                this.captionData = this.element.data();
//            },
//            afterShow: generateGalleryCaption
//        }).each(function () {
//            generateGalleryRollover($(this));
//        });
//
//        $container.isotope({
//            itemSelector: '.gallery-thumb',
//            layoutMode: 'fitRows'
//        });
//
//        $filterDropdowns.on('click', function (e) {
//
//            e.preventDefault();
//
//            if (!$(this).hasClass($filterOpenClass)) {
//                $filterDropdowns.removeClass($filterOpenClass);
//                $(this).addClass($filterOpenClass);
//
//            } else {
//                $filterDropdowns.removeClass($filterOpenClass);
//                $('.filter-reset').removeClass('selected');
//                $('.filter-link').removeClass('selected');
//            }
//        });
//
//        $(document).on('click', function (e) {
//            if (!$(e.target).hasClass('filter-selected-option')) {
//                $filterDropdowns.removeClass($filterOpenClass);
//            }
//        });
//
//        $('.filter-link').on('click', function (e) {
//            e.preventDefault();
//
//
//            $filterDropdowns.each(function () {
//                $(this).find('a').first().trigger('click');
//            });
//
//            $('.filter-selected-option').removeClass('active');
//        });
//
//        $('.filter-reset').on('click', function(e){
//
//            e.preventDefault();
//            $filterDropdowns.each(function () {
//                $(this).find('a').first().trigger('click');
//            });
//
//            $('.filter-selected-option').removeClass('active');
//        });
//
//
//        $('.filter-options').on('click', function (e) {
//            $('#both-filters').trigger('click');
//            $('.filter-selected-option').addClass('active');
//            $('.js-isotope-reset').removeClass('selected');
//        });
//
//        $('.sort-filter').on('click', function (e) {
//            $('.js-isotope-reset').trigger('click');
//        });
//
//
//        $('.js-isotope-reset').on('click', function (e) {
//
//            e.preventDefault();
//
//            $filterDropdowns.each(function () {
//                $(this).find('a').first().trigger('click');
//            });
//
//            $('.filter-selected-option').removeClass('active');
//
//        });
//
//        $('.js-isotope-filter').on('click', function (e) {
//            e.preventDefault();
//            var $this = $(this);
//            var $optionSet = $this.parents('.filter-select');
//            //WKCD.log.log(["ddd",$optionSet]);
//            var isoFilters = [];
//            var selector;
//            var $selectedOption = $optionSet.find('.filter-selected-option');
//
//            // store filter value in object
//            // i.e. filters.color = 'red'
//            var group = $optionSet.attr('data-filter-group');
//            $filters[group] = $this.attr('data-filter-value');
//
//            // don't proceed if already selected
//            if ($this.hasClass('selected')) {
//                return;
//            }
//
//            // change selected class
//            $optionSet.find('.selected').removeClass('selected');
//            $this.addClass('selected');
//            $selectedOption.text($this.text());
//
//            // convert object into array
//            for (var prop in $filters) {
//                isoFilters.push($filters[prop]);
//            }
//
//            // construct selector string
//            selector = isoFilters.join('');
//
//            // add filter
//            $container.isotope({
//                filter: selector
//            });
//
//            // reset fancybox gallery selector
//            $galleryThumbs.attr('rel', 'all');
//
//            if (selector !== '') {
//                // remove thumbnails not in filter from fancybox gallery selector
//                $galleryThumbs.not(selector).attr('rel', '');
//
//                if ($(selector).length === 0) {
//                    // show "No results" content
//                    $container.parent().addClass('gallery--hidden');
//                    $('.gallery-no-items').addClass('gallery-no-items--show');
//                } else {
//                    // hide "No results" content
//                    $container.parent().removeClass('gallery--hidden');
//                    $('.gallery-no-items').removeClass('gallery-no-items--show');
//                }
//            } else {
//                $container.parent().removeClass('gallery--hidden');
//            }
//        });
//    });
//})(WKCD.jQuery);