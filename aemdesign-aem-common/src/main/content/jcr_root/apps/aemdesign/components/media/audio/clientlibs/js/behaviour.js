/*audio - behaviour*/
window.AEMDESIGN = window.AEMDESIGN || {"jQuery":{}};
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.audio = AEMDESIGN.components.audio || {};

(function ($, _, ko, utils, log, ns, window, undefined) { //add additional dependencies


    $(document).ready(function () {

        // var audioPlayerId = 0;
        //
        // $("#article a[role='audio-player']").each(function() {
        //
        //     ++audioPlayerId;
        //
        //     var
        //         audioAnchor = $(this),
        //         elementId = "audio-player-" + audioPlayerId,
        //         playerDiv = $("<div />", { id: elementId });
        //
        //     // add player div
        //     playerDiv.insertAfter(audioAnchor);
        //     audioAnchor.hide();
        //
        //     jwplayer("audio-player-" + audioPlayerId).setup({
        //         flashplayer: audioAnchor.data("player-address"),
        //         controlbar : 'bottom',
        //         width : 400,
        //         height : 24,
        //         file :  audioAnchor.attr("href"),
        //         stretching : 'fill',
        //         autostart : false,
        //         wmode : 'opaque',
        //         menu : false,
        //         allowFullScreen : false
        //     });
        //
        // });
    });


})(AEMDESIGN.jQuery, _, ko, AEMDESIGN.utils, AEMDESIGN.log, AEMDESIGN.components.audio, this); //pass in additional dependencies
