//slidergallery
window.WKCD = window.WKCD || {};
window.WKCD.components = WKCD.components || {};
window.WKCD.components.slidergallery = WKCD.components.slidergallery || {};
window.WKCD.components.slidergallery.function = WKCD.components.slidergallery.function || {};

(function ($, _, ns, window, undefined) {

    "use strict";
    var _version = "0.1";

    ns.version = function () {
        return _version;
    };

    /**
     * Instead of using Flexslider directly, this module gets options from the data attributes
     * of the slider container. For advanced configurations, additional options can be passed
     * into the second parameter.
     * @param   {object} $slider jQuery object of the slider container
     * @param   {object} additionalOptions Object literal with advanced slider configurations that cannot be set in the data attributes. Recommended for callbacks.
     * @return  {object} jQuery object of the slider initially passed in, now with Flexslider bound to it
     */
    ns.slider = function ($slider, additionalOptions) {
        'use strict';

        var options = $slider.data() || {};
        additionalOptions = additionalOptions || {};

        _.extend(options, additionalOptions);

        $slider.flexslider(options);

        // Whole slider is returned in case performing actions with helper strings is required
        return $slider;
    };




    ns.generateGalleryCaption = function($currentItem) {
        //WKCD.log.log("generating caption");
        //$(this).inner.find('.caption-content').remove();

        // get thumbnail data
        var captionData =  $currentItem;//.data();
        //WKCD.log.log(captionData);
        // clone custom caption template to use in modal
        var $customCaption = $('.caption-content').clone().removeClass('hide');
        // placeholder for option row
        var $modelOptionRow = $('<tr/>').append('<th></th><td>' + captionData.optionType + '</td>');
        // Models with option available
        var optionModels = captionData.optionModels ? captionData.optionModels.split(' ') : 0,
            optionModelsCount = optionModels === 0 ? 0 : optionModels.length;

        $customCaption.find('.caption-title').html(captionData.title || '');
        $customCaption.find('.caption-description').html(captionData.description || '');
        $customCaption.find('.caption-type').addClass(captionData.type ? 'icon-' + captionData.type.toLowerCase() : '').text(captionData.type || '');
        $customCaption.find('.caption-disclaimer').html(captionData.disclaimer || '').appendTo($customCaption.find('.caption-description'));

        if (captionData.body != '' && optionModelsCount < 1) {
            $customCaption.find('.caption-options').html(decodeURIComponent(escape(captionData.body)));
        }

        if (optionModelsCount > 1) {
            for (var i = 0; i < optionModelsCount; i++) {
                $modelOptionRow.clone()
                    .find('th')
                    .text(optionModels[i])
                    .parent()
                    .appendTo($customCaption.find('.caption-options table'));
            }
        } else if (optionModelsCount === 1) {
            $modelOptionRow.clone()
                .find('th')
                .text(optionModels[0])
                .parent()
                .appendTo($customCaption.find('.caption-options table'));
        }

        $('.fancybox-inner').append($customCaption);
    };

    ns.generateGalleryRollover = function($currentItem,thumbTpl) {
        var itemData = $currentItem.data();
        var $rollover = $(thumbTpl);
        var truncatedDesc;

        if (itemData.disclaimer.length) {
            truncatedDesc = ns.truncate(itemData.description, 80);
        } else {
            truncatedDesc = ns.truncate(itemData.description, 160);
        }

        // populate rollover with truncated description
        $rollover.removeClass('hide').find('.gallery-thumb-hover-text').append(truncatedDesc);

        // populate rollover with disclaimer if available
        if (itemData.disclaimer.length) {
            $rollover.find('.gallery-thumb-hover-text').append('<br><br>').append(
                $('<span/>')
                    .addClass('gallery-thumb-disclaimer')
                    .append(itemData.disclaimer)
            );
        }

        $currentItem.append($rollover);
    };

    ns.truncate = function(str, limit) {
        var newStr = str.substring(0, limit);

        if (str.length > limit) {
            newStr += '...';
        }

        return newStr;
    };


})(jQuery, _, WKCD.components.slidergallery.function, this);

