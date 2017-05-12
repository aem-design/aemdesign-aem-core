<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ page session="false" import="com.day.cq.commons.Externalizer,
                 com.day.cq.i18n.I18n,
                 org.apache.sling.api.resource.ResourceResolver,
                 com.day.cq.wcm.webservicesupport.ConfigurationManager"%><%
%><%
    I18n i18n = new I18n(slingRequest);

    // Getting publish link for current page.
    Externalizer externalizer = sling.getService(Externalizer.class);
    ResourceResolver resolver = sling.getService(ResourceResolver.class);
    String currentPageURL = "";
    if(externalizer != null){
        currentPageURL = externalizer.publishLink(resolver, currentPage.getPath()).concat(DEFAULT_EXTENTION);
    }
	currentPageURL = currentPageURL.replaceFirst("//localhost","//127.0.0.1");

    String url = properties.get("url", currentPageURL);

    // Getting attached facebook cloud service config in order to fetch appID
    ConfigurationManager cfgMgr = sling.getService(ConfigurationManager.class);
    com.day.cq.wcm.webservicesupport.Configuration facebookConfiguration = null;
    String[] services = pageProperties.getInherited("cq:cloudserviceconfigs", new String[]{});
    if(cfgMgr != null) {
        facebookConfiguration = cfgMgr.getConfiguration("facebookconnect", services);
    }

    // Once cloud service config found. getting relevant URLparams if configured.
    if(facebookConfiguration != null) {
        Resource configResource = facebookConfiguration.getResource();
        Page configPage = configResource.adaptTo(Page.class);
        final String params[] = (String[])configPage.getProperties().get("params",String[].class);
        StringBuilder urlParams = new StringBuilder();
        if(params!= null && params.length > 0) {
            for(int i=0;i<params.length;i++){
                if(i>0){
                    urlParams.append("&");
                }
                urlParams.append(params[i]);
            }
        }
        if(urlParams != null && urlParams.toString().length() >0) {
            if (url.indexOf('?') == -1) {
                url = url + "?";
            } else {
                url = url + "&";
            }
            url = url + urlParams.toString();
        }
    }

    String layout = properties.get("layout", "button_count");
    boolean showFaces = currentStyle.get("showFaces", false);
    boolean includeSend = properties.get("includeSend", false);
    String action = currentStyle.get("action", "like");

    String width = currentStyle.get("width", "300");
    String font = currentStyle.get("font", "");
    String colorscheme = currentStyle.get("color", "light");

    String height="35";
    if (showFaces) {
        height="80";
    }
    if (layout.equals("button_count")) {
        height = "21";
    } else if (layout.equals("box_count")) {
        height = "65";
    }

%>

<cq:includeClientLib categories="cq.social.plugins.facebook"/>

<script type="text/javascript">
    var fbLikeComponentPath = '<%=resource.getResourceType()%>';
    var fbLikeComponentName = fbLikeComponentPath.substring(fbLikeComponentPath.lastIndexOf('/')+1);
    window.fbAsyncInit = function() {
        FB.Event.subscribe('edge.create', function(href) {
            if (CQ_Analytics) {
                if (CQ_Analytics.Sitecatalyst) {
                    CQ_Analytics.record({ event: ['socialshare','facebooklike'], values: {
                    socialchannel:'facebook',
                    componentname:fbLikeComponentName,
                    url:"<%= xssAPI.encodeForJSString(url) %>"
                    }, componentPath:fbLikeComponentPath});
                }
            }
        });
    };
</script>

<% if(isURL(url)) { %>
    <div class="fb-like cq-social-facebook-plugins-div" data-notify="true" data-href="<%=url%>" data-send="<%=includeSend%>" data-layout="<%=layout%>" data-width="<%=width%>" data-show-faces="<%=showFaces%>" data-action="<%=action%>" data-colorscheme="<%=colorscheme%>" data-font="<%=font%>"></div>
<% } else { %>
    <h3 class="cq-texthint-placeholder"><%= i18n.get("Please enter a valid url.") %></h3>
<% } %>

<%--<%@include file="/apps/aemdesign/global/component-badge.jsp" %>--%>

<%!
    boolean isURL(String value) {
        return value != null && (value.startsWith("http://") || value.startsWith("https://"));
    }
%>


