<%
%><%@ page session="false" %><%@include file="/libs/foundation/global.jsp"%>
<cq:includeClientLib categories="aemdesign.social.twitter"/>
<%
    String uniqSuffix = resource.getPath().replaceAll("/","-").replaceAll(":","-");
    String divID = "twitter-follow" + uniqSuffix;
    boolean showUsername=false,largeButton=false;
    String url="";
    String user = properties.get("user","");
    if(user.equals("")){
        user = currentStyle.get("user","");
    }

    if(!user.equals("")) {

        // Twitter api takes care of appending relevant symbols to each category.
        // It fails if user explicitly append's those symbols.
        // So taking care of deleting those symbols in case of user input.
        user = trimIfexists(user,"@");

        url = "https://twitter.com/" + user;
        if(currentStyle.get("showUsername","false").equals("true")) {
            showUsername = true;
        }
        if(currentStyle.get("largeButton","false").equals("true")) {
            largeButton = true;
        }
    }
%>

<script type="text/javascript" charset="utf-8">
    var twtrFollowComponentPath = '<%=resource.getResourceType()%>';
    var twtrFollowComponentName = twtrFollowComponentPath.substring(twtrFollowComponentPath.lastIndexOf('/')+1);
    twttr.ready(function (twttr) {
       twttr.events.bind('follow', function (event) {
            if (CQ_Analytics) {
                if (CQ_Analytics.Sitecatalyst) {
                    CQ_Analytics.record({ event: ['aemdesign','twitterfollow'], values: {
                        socialchannel:'twitter',
                        componentname:twtrFollowComponentName,
                        user:"<%= xssAPI.encodeForJSString(user) %>"
                    }, componentPath:twtrFollowComponentPath});
                }
            }
        });
    });
</script>

<%
  if(user.equals("")) {
%>    <div id="twitter-config" class="cq-social-twitter-widgets-image"></div>
<% } else {%>
    <div id="<%=xssAPI.encodeForHTMLAttr(divID)%>" class="cq-social-twitter-widgets-div">
        <a href= "<%= xssAPI.getValidHref(url) %>"  class="twitter-follow-button" data-show-count="false" <%if(!showUsername) {%> data-show-screen-name="false" <%}%> <%if(largeButton) {%> data-size="large" <%}%> >Follow @<%= xssAPI.encodeForHTML(user)%></a>
    </div>
<% } %>

<%!
    private String trimIfexists(String text,String charToRemove) {
        if(text != null && !text.equals("") && text.indexOf(charToRemove) == 0){
            return text.substring(1);
        }
        return text;
    }
%>
