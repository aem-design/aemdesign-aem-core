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
     * @resolver resource resolver for verifying path
     * @slingRequest current sling request
     * @path path is the path to map to an actual URL
     * @return path for a local domain
     */
    public String mappedUrl(ResourceResolver resolver, SlingHttpServletRequest slingRequest, String path) {
        return mappedUrl(resolver, slingRequest, "local", path, false);
    }

    /**
     * This method maps an absolute path to the canonical URL for HTTP
     *
     * @resolver resource resolver for verifying path
     * @slingRequest current sling request
     * @path path is the path to map to an actual URL
     * @domain domain to use from Externalizer config
     * @return path for a specific domain
     */
    public String mappedUrl(ResourceResolver resolver, SlingHttpServletRequest slingRequest, String domain, String path) {
        return mappedUrl(resolver, slingRequest, domain, path, false);
    }


    /**
     * This method maps an absolute path to the canonical URL in the correct domain.
     *
     * @resolver resource resolver for verifying path
     * @slingRequest current sling request
     * @path path is the path to map to an actual URL
     * @secure force secure to be returned
     * @return path for a local domain
     */
    public String mappedUrl(ResourceResolver resolver, SlingHttpServletRequest slingRequest, String path, Boolean secure) {
        return mappedUrl(resolver, slingRequest, "local", path, secure);
    }

    /**
     * This method maps an absolute path to the canonical URL in the correct domain.
     *
     * @param path is the path to map to an actual URL
     */
    public String mappedUrl(ResourceResolver resolver, SlingHttpServletRequest slingRequest, String domain, String path, Boolean secure) {
        if (path == null || resolver == null || slingRequest == null || domain == null || secure == null) {
            getLogger().error(MessageFormat.format("mappedUrl not enough parameters: resolver=[{0}],slingRequest=[{1}],domain=[{2}],path=[{3}],secure=[{4}]",resolver,slingRequest,domain,path,secure));
            return null;
        }
        Externalizer externalizer = resolver.adaptTo(Externalizer.class);
        if (secure) {
            return externalizer.externalLink(resolver, domain, SECURE_MAP_CONFIG_SCHEMA, resolver.map(path));
        } else {
            return externalizer.externalLink(resolver, domain, DEFAULT_MAP_CONFIG_SCHEMA, resolver.map(path));
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
    public boolean checkResourceHasChildResource(String resourceName, Resource _resource, ResourceResolver _resourceResolver) {
        boolean resourceExist = false;
        String foundResourceType = ResourceUtil.findResourceSuperType(_resource);
        if (foundResourceType != null) {
            Resource foundResource = _resourceResolver.getResource(ResourceUtil.findResourceSuperType(_resource) + "/" + resourceName);
            if (foundResource != null) {
                String file_name = foundResource.getName();
                if (file_name.equals(resourceName)) {
                    resourceExist = true;
                }
            }
        }
        return resourceExist;
    }
%>
