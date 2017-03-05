////js
//
//var thumbTpl = $('#gallery-thumb-template').html();
//
//function generateGalleryCaption($currentItem) {
//    this.inner.find('.caption-content').remove();
//
//    // get thumbnail data
//    var captionData = this.element.data();
//    // clone custom caption template to use in modal
//    var $customCaption = $('.caption-content').clone().removeClass('hide');
//    // placeholder for option row
//    var $modelOptionRow = $('<tr/>').append('<th></th><td>' + captionData.optionType + '</td>');
//    // Models with option available
//    var optionModels = captionData.optionModels ? captionData.optionModels.split(',') : 0,
//        optionModelsCount = optionModels === 0 ? 0 : optionModels.length;
//
//    $customCaption.find('.caption-title').html(captionData.title || '');
//    $customCaption.find('.caption-description').html(captionData.description || '');
//    $customCaption.find('.caption-type').addClass(captionData.type ? 'icon-' + captionData.type.toLowerCase() : '').text(captionData.type || '');
//    $customCaption.find('.caption-disclaimer').html(captionData.disclaimer || '').appendTo($customCaption.find('.caption-description'));
//
//    if (optionModelsCount > 1) {
//        for (var i = 0; i < optionModelsCount; i++) {
//            $modelOptionRow.clone()
//                .find('th')
//                .text(optionModels[i])
//                .parent()
//                .appendTo($customCaption.find('.caption-options table'));
//        }
//    } else if (optionModelsCount === 1) {
//        $modelOptionRow.clone()
//            .find('th')
//            .text(optionModels[0])
//            .parent()
//            .appendTo($customCaption.find('.caption-options table'));
//    }
//
//    $('.fancybox-inner').append($customCaption);
//}
//
//function generateGalleryRollover($currentItem) {
//    var itemData = $currentItem.data();
//    var $rollover = $(thumbTpl);
//    var truncatedDesc;
//
//    if (itemData.disclaimer.length) {
//        truncatedDesc = truncate(itemData.description, 80);
//    } else {
//        truncatedDesc = truncate(itemData.description, 160);
//    }
//
//    // populate rollover with truncated description
//    $rollover.removeClass('hide').find('.gallery-thumb-hover-text').append(truncatedDesc);
//
//    // populate rollover with disclaimer if available
//    if (itemData.disclaimer.length) {
//        $rollover.find('.gallery-thumb-hover-text').append('<br><br>').append(
//            $('<span/>')
//                .addClass('gallery-thumb-disclaimer')
//                .append(itemData.disclaimer)
//        );
//    }
//
//    $currentItem.append($rollover);
//}
//
//function truncate(str, limit) {
//    var newStr = str.substring(0, limit);
//
//    if (str.length > limit) {
//        newStr += '...';
//    }
//
//    return newStr;
//}