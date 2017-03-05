/**
 * Initializes the components on the page
 */
(function($, undefined) {

    $(document).ready(function() {

        (function() {
            var po = document.createElement('script'); po.type = 'text/javascript'; po.async = true;
            po.src = 'https://apis.google.com/js/plusone.js';
            var s = document.getElementsByTagName('script')[0];
            //s.parentNode.insertBefore(po, s); //Hotfix: break the website, we will fix it later
        })();

    });

})(AEMDESIGN.jQuery);