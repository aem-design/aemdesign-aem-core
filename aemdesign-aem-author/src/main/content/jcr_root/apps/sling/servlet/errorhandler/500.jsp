<%@ page session="false" %>
<%@ page import="
    java.net.URLEncoder,
    org.apache.sling.api.scripting.SlingBindings,
    org.apache.sling.engine.auth.Authenticator,
    org.apache.sling.engine.auth.NoAuthenticationHandlerException,
    com.day.cq.wcm.api.WCMMode" %><%!

    private boolean isAnonymousUser(HttpServletRequest request) {
        return request.getAuthType() == null
            || request.getRemoteUser() == null;
    }

    private boolean isBrowserRequest(HttpServletRequest request) {
        // check if user agent contains "Mozilla" or "Opera"
        final String userAgent = request.getHeader("User-Agent");
        return userAgent != null
            && (userAgent.indexOf("Mozilla") > -1
                || userAgent.indexOf("Opera") > -1);
    }
    
%><%
    /*

    //LET CQ HANDLE AUTH ORDER

    org.apache.sling.api.scripting.SlingScriptHelper _sling = (org.apache.sling.api.scripting.SlingScriptHelper)  pageContext.getAttribute("sling");

    boolean isAuthor = WCMMode.fromRequest(request) != WCMMode.DISABLED
            && !_sling.getService(SlingSettingsService.class).getRunModes().contains("publish");

    // decide whether to redirect to the (wcm) login page, or to send a plain 404
    if (isAuthor
            && isAnonymousUser(request)
            && isBrowserRequest(request)) {
        
        SlingBindings bindings = (SlingBindings) request.getAttribute("org.apache.sling.api.scripting.SlingBindings");
        Authenticator auth = bindings.getSling().getService(Authenticator.class);
        if (auth != null) {
            try {
                auth.login(request, response);
                
                // login has been requested, nothing more to do
                return;
            } catch (NoAuthenticationHandlerException nahe) {
                bindings.getLog().warn("Cannot login: No Authentication Handler is willing to authenticate");
            }
        } else {
            bindings.getLog().warn("Cannot login: Missing Authenticator service");
        }
        
    }
    */
    // get here if authentication should not take place or if
    // no Authenticator service is available or if no
    // AuthenticationHandler is willing to authenticate
    // So we fall back to plain old 500/NOT FOUND    
%><%@include file="custom.jsp"%>