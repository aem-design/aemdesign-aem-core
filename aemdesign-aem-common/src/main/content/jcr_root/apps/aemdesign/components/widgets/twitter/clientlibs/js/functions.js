//twitter - functions
window.AEMDESIGN = window.AEMDESIGN || {"jQuery":{}};
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.twitter = AEMDESIGN.components.twitter || {};

(function ($, _, ko, ns, log, window, undefined) { //add additional dependencies

    "use strict";
    var _version = "0.1";

    ns.version = function () {
        return _version;
    };

    ns.searchWidget = function(divID,widgetType,searchQuery,title,caption,width,height,shellBackground,shellText,tweetBackground,tweetText,links,scrollBar,pollResults) {
        new TWTR.Widget({
            id: divID,
            version: 2,
            type: widgetType,
            search: searchQuery,
            interval: 10000,
            title: title,
            subject: caption,
            width: width,
            height: height,
            theme: {
                shell: {
                    background: shellBackground,
                    color: shellText
                },
                tweets: {
                    background: tweetBackground,
                    color: tweetText,
                    links: links
                }
            },
            features: {
                scrollbar: scrollBar,
                loop: true,
                live: pollResults,
                behavior: 'default'
            }
        }).render().start();
    };

    ns.profileWidget= function(divID,widgetType,user,width,height,shellBackground,shellText,tweetBackground,tweetText,links,scrollBar,pollResults) {
        new TWTR.Widget({
            id: divID,
            version: 2,
            type: widgetType,
            rpp: 4,
            interval: 30000,
            width: width,
            height: height,
            theme: {
                shell: {
                    background: shellBackground,
                    color: shellText
                },
                tweets: {
                    background: tweetBackground,
                    color: tweetText,
                    links: links
                }
            },
            features: {
                scrollbar: scrollBar,
                loop: false,
                live: pollResults,
                behavior: 'all'
            }
        }).render().setUser(user).start();
    };

    ns.listsWidget= function(divID,widgetType,user,title,caption,width,height,shellBackground,shellText,tweetBackground,tweetText,links,scrollBar,pollResults,listName) {
        new TWTR.Widget({
            id: divID,
            version: 2,
            type: widgetType,
            rpp: 4,
            interval: 30000,
            title: title,
            subject: caption,
            width: width,
            height: height,
            theme: {
                shell: {
                    background: shellBackground,
                    color: shellText
                },
                tweets: {
                    background: tweetBackground,
                    color: tweetText,
                    links: links
                }
            },
            features: {
                scrollbar: scrollBar,
                loop: false,
                live: pollResults,
                behavior: 'all'
            }
        }).render().setList(user, listName).start();
    };

    ns.favesWidget= function(divID,widgetType,user,title,caption,width,height,shellBackground,shellText,tweetBackground,tweetText,links,scrollBar,pollResults) {
        new TWTR.Widget({
            id: divID,
            version: 2,
            type: widgetType,
            rpp: 4,
            interval: 30000,
            title: title,
            subject: caption,
            width: width,
            height: height,
            theme: {
                shell: {
                    background: shellBackground,
                    color: shellText
                },
                tweets: {
                    background: tweetBackground,
                    color: tweetText,
                    links: links
                }
            },
            features: {
                scrollbar: scrollBar,
                loop: false,
                live: pollResults,
                behavior: 'all'
            }
        }).render().setUser(user).start();
    };

})(AEMDESIGN.jQuery, _, ko, AEMDESIGN.components.twitter, AEMDESIGN.log, this); //pass in additional dependencies

