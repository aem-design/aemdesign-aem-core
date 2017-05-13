<%@ page import="com.day.cq.wcm.foundation.ELEvaluator" %>
<%@ page import="org.apache.sling.api.SlingHttpServletResponse" %><%!

    final String PAGE_PROP_REDIRECT = "redirectTarget";


    /**
     * This method maps an absolute path to the canonical URL in the correct domain.
     *
     * @param path is the path to map to an actual URL
     */
    public String mappedUrl(ResourceResolver resolver,String path) {
        if (path == null) {
            return null;
        }

        return resolver.map(path);
    }

    public boolean checkRedirect(ValueMap _properties, Page _currentPage, SlingHttpServletRequest _slingRequest, HttpServletResponse response, HttpServletRequest request, PageContext _pageContext) throws Exception {

        // read the redirect target from the 'page properties' and perform the
        // redirect if WCM is disabled.
        String location = _properties.get(PAGE_PROP_REDIRECT, "");
        // resolve variables in path
        location = ELEvaluator.evaluate(location, _slingRequest, _pageContext);
        boolean wcmModeIsDisabled = WCMMode.fromRequest(request) == WCMMode.DISABLED;
        boolean wcmModeIsPreview = WCMMode.fromRequest(request) == WCMMode.PREVIEW;

        if ( (location.length() > 0) && ((wcmModeIsDisabled) || (wcmModeIsPreview)) ) {
            // check for recursion
            if (_currentPage != null && !location.equals(_currentPage.getPath()) && location.length() > 0) {
                // check for absolute path
                final int protocolIndex = location.indexOf(":/");
                final int queryIndex = location.indexOf('?');
                final String redirectPath;
                if ( protocolIndex > -1 && (queryIndex == -1 || queryIndex > protocolIndex) ) {
                    redirectPath = location;
                } else {
                    redirectPath = _slingRequest.getResourceResolver().map(request, location).concat(DEFAULT_EXTENTION);
                }
                response.sendRedirect(redirectPath);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
            return true;
        }
        return false;
    }

%>
