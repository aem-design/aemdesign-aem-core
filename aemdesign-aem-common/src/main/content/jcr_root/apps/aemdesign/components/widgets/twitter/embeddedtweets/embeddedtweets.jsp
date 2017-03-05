<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %>

<c:set var="statusId" value="<%=_properties.get("statusId", "")%>"/>
<c:set var="user" value="<%=_properties.get("twitterfollow/user", "")%>"/>
<c:set var="embeddedTweet" value="<%=_properties.get("embeddedTweet", "")%>"/>

<c:choose>
    <c:when test="${not empty statusId and not empty embeddedTweet}">
        <div class="twitter-wrapper">
            <cq:include resourceType="nrl/components/content/image" path="image" />
            ${embeddedTweet}
            <ul class="twt-actions">
                <li>
                    <button type="button" class="btn btn-default btn-xs">
                        <a title="Reply" class="reply-action twt-intent" href="https://twitter.com/intent/tweet?in_reply_to=${statusId}"><span class="glyphicon glyphicon-share-alt"></span> Reply</a>
                    </button>
                </li>
                <li>
                    <button type="button" class="btn btn-default btn-xs">
                        <a title="Retweet" class=" retweet-action  twt-intent" href="https://twitter.com/intent/retweet?tweet_id=${statusId}"><span class="glyphicon glyphicon-retweet"></span> Retweet</a>
                    </button>
                <li>
                    <button type="button" class="btn btn-default btn-xs">
                        <a title="Favorite" class="favorite-action twt-intent" href="https://twitter.com/intent/favorite?tweet_id=${statusId}"><span class="glyphicon glyphicon-star"></span> Favorite</a>
                    </button>
                </li>
                <c:if test="${not empty user}">
                    <li>
                        <cq:include resourceType="social/plugins/twitter/twitterfollow" path="twitterfollow" />
                    </li>
                </c:if>
            </ul>
        </div>
    </c:when>
    <c:otherwise>
        <h3 class="cq-texthint-placeholder">Status/Tweet id and embedded tweet is not set</h3>
    </c:otherwise>
</c:choose>

<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
