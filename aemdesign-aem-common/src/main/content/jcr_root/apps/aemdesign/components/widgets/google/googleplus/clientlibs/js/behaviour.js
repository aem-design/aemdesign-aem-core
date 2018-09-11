//googleplus - behaviour
window.AEMDESIGN = window.AEMDESIGN || {"jQuery":{}};
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.googleplus = AEMDESIGN.components.googleplus || {};

(function ($, _, ko, ns, log, window, undefined) { //add additional dependencies

    $(document).ready(function () {

        var po = document.createElement('script'); po.type = 'text/javascript'; po.async = true;
        po.src = 'https://apis.google.com/js/plusone.js';
        var s = document.getElementsByTagName('script')[0];
        //s.parentNode.insertBefore(po, s); //Hotfix: break the website, we will fix it later


    });

})(AEMDESIGN.jQuery,_,ko, AEMDESIGN.components.googleplus, AEMDESIGN.log, this); //pass in additional dependencies
