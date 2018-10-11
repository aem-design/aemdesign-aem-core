<%@ page import="com.day.cq.wcm.foundation.ELEvaluator" %>
<%@ page import="com.day.cq.commons.Externalizer" %>
<%@ page import="org.apache.sling.api.SlingHttpServletResponse" %>
<%@ page import="org.apache.sling.api.resource.ResourceUtil" %><%!

    final String PAGE_PROP_REDIRECT = "redirectTarget";

    final String DEFAULT_MAP_CONFIG_SCHEMA = "http";
    final String SECURE_MAP_CONFIG_SCHEMA = "https";

    /**
     * map path using resolver
     *
     * @param path is the path to map to an actual URL
     */
    public String mappedUrl(ResourceResolver resolver,String path) {
        if (path == null) {
            return null;
        }
        return resolver.map(path);
    }

    /**
     * This method maps an absolute path to the canonical URL for HTTP
     *
     * @param path is the path to map to an actual URL
     */
    public String mappedUrl(ResourceResolver resolver, SlingHttpServletRequest slingRequest, String path) {
        return mappedUrl(resolver, slingRequest, path, false);
    }

    /**
     * This method maps an absolute path to the canonical URL in the correct domain.
     *
     * @param path is the path to map to an actual URL
     */
    public String mappedUrl(ResourceResolver resolver, SlingHttpServletRequest slingRequest, String path, Boolean secure) {
        if (path == null || resolver == null || slingRequest == null) {
            return null;
        }
        Externalizer externalizer = resolver.adaptTo(Externalizer.class);
        if (secure) {
            return externalizer.absoluteLink(slingRequest, SECURE_MAP_CONFIG_SCHEMA, resolver.map(path));
        } else {
            return externalizer.absoluteLink(slingRequest, DEFAULT_MAP_CONFIG_SCHEMA, resolver.map(path));
        }
    }

    /**
     * check if page redirect URL has been set
     * @param _properties
     * @param _currentPage
     * @param _slingRequest
     * @param response
     * @param request
     * @param _pageContext
     * @return
     * @throws Exception
     */
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

    /**
     * check if provided resource exists
     * @param resourceName
     * @param _resource
     * @param _resourceResolver
     * @return
     * @throws Exception
     */
    public boolean checkResourceExist (String resourceName, Resource _resource, ResourceResolver _resourceResolver) {
        String file_name = _resourceResolver.getResource(ResourceUtil.findResourceSuperType(_resource)+ "/" + resourceName).getName();
        Boolean resourceExist = false;
        if (file_name.equals(resourceName)) {
            resourceExist = true;
        }
        return resourceExist;
    }
%>
