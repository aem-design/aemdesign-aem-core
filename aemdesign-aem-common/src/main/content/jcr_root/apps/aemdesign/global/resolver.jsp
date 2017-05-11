<%!
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

%>
