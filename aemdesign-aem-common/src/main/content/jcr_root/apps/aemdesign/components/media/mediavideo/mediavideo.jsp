<%@ page import="java.util.Map" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/media.jsp" %>


    <%

        boolean editing = WCMMode.EDIT == CURRENT_WCMMODE;

        String
            videoUrl = _properties.get("video-url", (String) null),
            default_posterUrl = _currentStyle.get("poster-url", (String) null),
            default_videoType = _currentStyle.get("videoType", "http"),
            default_playerSrc = _currentStyle.get("player-source", PLAYER_ADDRESS),
            default_streamSrc = _currentStyle.get("stream-source", STREAM_ADDRESS),
            posterUrl = _properties.get("poster-url", default_posterUrl),
            videoType = _properties.get("video-type", default_videoType);

        Map<String, Integer> dimensions = getVideoDimensions(_properties);

        Integer
            width = null,
            height = null;

        if (dimensions != null) {
            width = dimensions.get("width");
            height = dimensions.get("height");
        }

        if (videoUrl != null && videoUrl.startsWith("/content")) {
            videoUrl = mappedUrl(videoUrl);
        }

    %>

    <c:choose>

        <c:when test="<%= videoUrl != null && dimensions != null %>">

            <div class="<%= editing ? "video-edit" : "" %>">

                <c:choose>
                    <c:when test="<%= videoType.equals("http") %>">

                        <div class="video-container"
                             style="width: <%= escapeBody(width) %>px; height: <%= escapeBody(height) %>px"
                             data-provider="<%= escapeBody(videoType) %>"
                             data-player-src="<%= escapeBody(default_playerSrc) %>"
                             data-video="<%= escapeBody(videoUrl) %>"
                             data-poster="<%= escapeBody(mappedUrl(posterUrl)) %>"
                             data-width="<%= escapeBody(width) %>"
                             data-height="<%= escapeBody(height) %>"><!----></div>

                    </c:when>
                    <c:when test="<%= videoType.equals("rtmp") %>">
                        <div class="video-container"
                              style="width: <%= escapeBody(width) %>px; height: <%= escapeBody(height) %>px"
                              data-provider="<%= escapeBody(videoType) %>"
                              data-player-src="<%= escapeBody(default_playerSrc) %>"
                              data-rtmp-streamer="<%= escapeBody(default_streamSrc) %>"
                              data-video="<%= escapeBody(videoUrl) %>"
                              data-poster="<%= escapeBody(mappedUrl(posterUrl)) %>"
                              data-width="<%= escapeBody(width) %>"
                              data-height="<%= escapeBody(height) %>"><!----></div>
                    </c:when>
                </c:choose>

            </div>

        </c:when>

        <c:otherwise>
            <p class="cq-info">Please set the video URL and its dimensions</p>
        </c:otherwise>
    </c:choose>

