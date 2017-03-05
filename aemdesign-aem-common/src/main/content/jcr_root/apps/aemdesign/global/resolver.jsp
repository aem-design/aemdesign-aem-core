<%@ page import="org.apache.sling.api.resource.ResourceResolver" %><%!
    /**
     * Local resolver
     */
    private ThreadLocal<ResourceResolver> localResolver = new ThreadLocal<ResourceResolver>();

    /**
     * This method maps an absolute path to the canonical URL in the correct domain.
     *
     * @param path is the path to map to an actual URL
     */
    public synchronized String mappedUrl(String path) {
        if (path == null) {
            return null;
        }

        ResourceResolver resolver = localResolver.get();
        return resolver.map(path);
    }

%><%
    localResolver.set(resourceResolver);
%>