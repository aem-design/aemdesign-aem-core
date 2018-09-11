//facebook - behaviour
window.AEMDESIGN = window.AEMDESIGN || {"jQuery":{}};
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.facebook = AEMDESIGN.components.facebook || {};

(function ($, _, ko, ns, log, window, undefined) { //add additional dependencies

    $(document).ready(function () {


        (function(d, s, id) {
            var js, fjs = d.getElementsByTagName(s)[0];
            if (d.getElementById(id)) return;
            js = d.createElement(s); js.id = id;
            js.src = "//connect.facebook.net/en_US/all.js#xfbml=1";
            fjs.parentNode.insertBefore(js, fjs);
        }(document, 'script', 'facebook-jssdk'));


    });

})(AEMDESIGN.jQuery,_,ko, AEMDESIGN.components.facebook, AEMDESIGN.log, this); //pass in additional dependencies
