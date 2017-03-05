/**
 * doc: http://www.longtailvideo.com/support/forums/jw-player/player-development-and-customization/29264/jwplayer-6-different-sources-for-modes/
 *
 * implementation of video loader
 */
(function($, undefined) {

    var uniqueVideoId = 0;

    function serializeParameters(map) {
        var arr = [];
        $.each(map, function(key, val) {
            if (val === null || typeof(val) === "undefined") {
                return;
            }
            arr.push(key + "=" + encodeURIComponent(val));
        });

        return arr.join("&");
    }

    $(document).ready(function() {

        $("#article .mediavideo .video-container").each(function() {

            var
                video = $(this),
                provider = video.data("provider"),
                rtmpStreamer = video.data("rtmp-streamer"),
                playerLocation = video.data("player-src"),
                url = video.data("video"),
                poster = video.data("poster"),
                width = video.data("width"),
                height = video.data("height");


            /**
             * Return the correct flash parameter mapping depending on the type
             * of provider this video is trying to use.
             *
             * @returns {Object} map with flash parameters
             */
            function flashParameters() {
                if (provider === "http") {
                    return {
                        provider: "http",
                        image : poster,
                        file: url
                    };
                }
                else if (provider === "rtmp") {
                    return {
                        provider: "rtmp",
                        streamer: rtmpStreamer,
                        image: poster,
                        bufferLength: 5,
                        screencolor: "FFFFFF",
                        file: url
                    };
                }
                else {
                    console.error("Illegal provider: " + provider);
                    return {};
                }
            }

            video.attr("id", "media_video_" + uniqueVideoId);

            $(this).css({
                height: height + "px",
                width: width + "px"
            });

            var embed = $("<embed/>", {
                width: width,
                height: height,
                src: playerLocation,

                allowscriptaccess: "always",
                allowfullscreen: "true",
                flashvars: serializeParameters(flashParameters())
            });

            video.append(embed);

        });

    });

})(AEMDESIGN.jQuery);