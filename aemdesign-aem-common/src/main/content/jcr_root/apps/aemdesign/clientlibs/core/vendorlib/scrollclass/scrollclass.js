/*
 * scrollClass jQuery Plugin v1.1
 *
 * Author: Virgiliu Diaconu
 * http://www.virgiliu.com
 * Licensed under the MIT license.
 */
;(function ($, window, document, undefined) {
    'use strict';
    $.scrollClass = function (el, options) {
        var base = this;

        // Access to jQuery and DOM versions of element
        base.$el = $(el);
        base.el = el;

        // Cached objects
        base.$win = $(window);
        base.$doc = $(document);

        var viewed = false,
            timer;

        // Initialize
        base.init = function () {
            base.options = $.extend({}, $.scrollClass.defaultOptions, options);
        };

        // Scroll handler
        base.scrollHandler = function () {
            // Return if element viewed
            if (viewed) {
                return;
            }
            base.onScroll();
        };

        // On scroll
        base.onScroll = function () {
            if (base.inViewport()) {
                if (base.options.delay !== 0) {
                    window.clearTimeout(timer);
                    timer = window.setTimeout(base.addScrollClass, base.options.delay);
                } else {
                    base.addScrollClass();
                }
                // Callback
                if (typeof base.options.callback === 'function') {
                    base.options.callback.call(el);
                }
                viewed = true;
                return;
            }
        };

        // Add scroll class
        base.addScrollClass = function () {
            var dataAttr = base.$el.data("scrollClass");
            base.$el.addClass(dataAttr);
        }

        // If element visible in viewport
        base.inViewport = function () {
            var elRect = base.el.getBoundingClientRect(),
                winHeight = base.$win.height(),
                elThreshold = base.options.threshold;
            // If window height smaller than element height use 50% threshold
            if (winHeight < elRect.height) {
                elThreshold = 50;
            }
            // Convert threshold percent to px of element
            var thresholdPx = (elThreshold / 100) * elRect.height;
            // Return true if element in viewport
            return (
                elRect.top + thresholdPx <= winHeight && elRect.bottom - thresholdPx >= 0 + base.options.offsetTop
            );
        };

        // Run initializer
        base.init();

        // On scroll and load listener
        base.$win.on('scroll load', base.scrollHandler);
    };

    $.scrollClass.defaultOptions = {
        delay: 10,
        threshold: 50,
        offsetTop: 0
    };

    $.fn.scrollClass = function (options) {
        return this.each(function () {
            (new $.scrollClass(this, options));
        });
    };
})(jQuery, window, document);