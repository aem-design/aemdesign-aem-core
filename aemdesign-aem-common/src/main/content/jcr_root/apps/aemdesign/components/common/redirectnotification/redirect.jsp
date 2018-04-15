<%@page session="false"
        contentType="text/html; charset=utf-8"
        import="com.day.cq.wcm.api.WCMMode,
                com.day.cq.wcm.foundation.ELEvaluator,
                org.apache.sling.settings.SlingSettingsService" %><%
%><%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %><%
%><cq:defineObjects/><%
    // read the redirect target from the 'page properties' and perform the
    // redirect if WCM is disabled.
    String location = properties.get("redirectUrl", "");
    // resolve variables in path
    location = ELEvaluator.evaluate(location, slingRequest, pageContext);
    boolean wcmModeIsDisabled = WCMMode.fromRequest(request) == WCMMode.DISABLED;
    boolean isPublishMode = sling.getService(SlingSettingsService.class).getRunModes().contains("publish");
    if ( (location.length() > 0) && (wcmModeIsDisabled) ) {
        // check for recursion
        if (currentPage != null && !location.equals(currentPage.getPath()) && location.length() > 0) {
            // check for absolute path
            final int protocolIndex = location.indexOf(":/");
            final int queryIndex = location.indexOf('?');
            String redirectPath;

            if ( protocolIndex > -1 && (queryIndex == -1 || queryIndex > protocolIndex) ) {
                redirectPath = location;
            } else {
                redirectPath = slingRequest.getResourceResolver().map(request, location) + ".html";
            }

            // include wcmmode=disabled (except on publish) to redirected url if original request also had that parameter
            if (wcmModeIsDisabled && !isPublishMode) {
                redirectPath += ((redirectPath.indexOf('?') == -1) ? '?' : '&') + "wcmmode=disabled";
            }

            response.sendRedirect(redirectPath);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
        return;
    }
    // set doctype
    if (currentDesign != null) {
        currentDesign.getDoctype(currentStyle).toRequest(request);
    }

%>