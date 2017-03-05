<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/media.jsp" %>


    <%
        String audioUrl = _properties.get("audio-url", (String) null);

        // make it a nicely mapped url if possible
        if (!StringUtils.isEmpty(audioUrl)) {
            if (audioUrl.startsWith("/content")) {
                audioUrl = mappedUrl(audioUrl);
            }
        }

    %>
    <c:choose>

        <c:when test="<%= !StringUtils.isEmpty(audioUrl) %>">
            <a role="audio-player" href="<%= audioUrl %>" data-player-address="<%= PLAYER_ADDRESS %>">
                Audio fragment
            </a>
        </c:when>

        <c:otherwise>
            <p class="cq-info">Please configure the audio component</p>
        </c:otherwise>
    </c:choose>
