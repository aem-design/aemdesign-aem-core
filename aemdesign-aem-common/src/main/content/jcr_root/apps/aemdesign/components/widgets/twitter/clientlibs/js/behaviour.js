//twitter - behaviour
window.AEMDESIGN = window.AEMDESIGN || {"jQuery":{}};
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.twitter = AEMDESIGN.components.twitter || {};


(function ($, _, ko, ns, analytics, window, undefined) { //add additional dependencies


    $(document).ready(function () {

        window.twttr = (function (d,s,id) {
            var t, js, fjs = d.getElementsByTagName(s)[0];
            if (d.getElementById(id)) return; js=d.createElement(s); js.id=id;
            js.src="//platform.twitter.com/widgets.js"; fjs.parentNode.insertBefore(js, fjs);
            return window.twttr || (t = { _e: [], ready: function(f){ t._e.push(f) } });
        }(document, "script", "twitter-wjs"));

    });


})(AEMDESIGN.jQuery, _, ko, AEMDESIGN.components.twitter, AEMDESIGN.analytics, this); //pass in additional dependencies
