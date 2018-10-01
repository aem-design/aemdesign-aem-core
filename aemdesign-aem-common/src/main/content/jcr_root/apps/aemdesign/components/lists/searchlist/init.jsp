<%@ page import="com.day.cq.search.PredicateConverter" %>
<%@ page import="com.day.cq.search.PredicateGroup" %>
<%@ page import="com.day.cq.search.Query" %>
<%@ page import="com.day.cq.search.QueryBuilder" %>
<%@ page import="com.day.cq.search.result.SearchResult" %>
<%@ page import="java.net.URLDecoder" %>
<%!

    /**
     * Composes the `QueryBuilder` instance using the query parameter sent from the client-side.
     *
     * @param slingRequest `SlingHttpServletRequest` instance
     * @param resourceResolver `ResourceResolver` instance
     */
    public Query composeQueryBulilder(
            SlingHttpServletRequest slingRequest,
            ResourceResolver resourceResolver
    ) {
        Query query = null;

        if (slingRequest.getRequestParameter("q") != null) {
            String escapedQuery = slingRequest.getRequestParameter("q").toString();

            try {
                String unescapedQuery = URLDecoder.decode(escapedQuery,"UTF-8");
                QueryBuilder queryBuilder = resourceResolver.adaptTo(QueryBuilder.class);

                // Create props for query
                java.util.Properties props = new java.util.Properties();

                // Load query candidate
                props.load(new ByteArrayInputStream(unescapedQuery.getBytes()));

                // Create predicate from query candidate
                PredicateGroup predicateGroup = PredicateConverter.createPredicates(props);
                javax.jcr.Session jcrSession = slingRequest.getResourceResolver().adaptTo(javax.jcr.Session.class);

                query = queryBuilder.createQuery(predicateGroup, jcrSession);
            } catch (Exception ex) {
                getLogger().error("Error using QueryBuilder with query [{}]. {}", escapedQuery, ex);
            }
        }

        return query;
    }

    /**
     * Normalise the result data for the final output.
     *
     * @param searchResults Array to store the normalised dataset in
     * @param sling `SlingScriptHelper` instance
     * @param slingRequest `SlingHttpServletRequest` instance
     */
    public void normaliseContentTree(
            List<CustomSearchResult> searchResults,
            SlingScriptHelper sling,
            SlingHttpServletRequest slingRequest,
            SearchResult result
    ) {
        try {
            if (!result.getHits().isEmpty()) {
                for (com.day.cq.search.result.Hit h: result.getHits()) {
                    CustomSearchResult newResult = new CustomSearchResult(h.getPath());
                    String jcrPrimaryType = h.getProperties().get("jcr:primaryType").toString();

                    if (jcrPrimaryType.equals("cq:PageContent")) {
                        newResult.setExtension(DEFAULT_EXTENTION.substring(1));

                        Resource hitResource = h.getResource();

                        if (hitResource != null) {
                            ResourceResolver resourceResolver = slingRequest.getResourceResolver();
                            String resourcePath = hitResource.getPath();

                            // Check if the resource has a 'Page Details' component within it
                            String pageDetailsPath = resourcePath + "/jcr:content/article/par/page_details";

                            if (resourceResolver != null && resourceResolver.resolve(pageDetailsPath) != null) {
                                newResult.setPageDetails(true);

                                String thumbnailImagePath = getPathFromImageResource(resourceResolver, pageDetailsPath + "/thumbnail");
                                String backgroundImagePath = getPathFromImageResource(resourceResolver, pageDetailsPath + "/bgimage");

                                if (thumbnailImagePath != null) {
                                    newResult.setImgResource(thumbnailImagePath);
                                } else if(backgroundImagePath != null) {
                                    newResult.setImgResource(backgroundImagePath);
                                } else {
                                    newResult.setImgResource(DEFAULT_IMAGE_BLANK);
                                }
                            }
                        }
                    }

                    if (jcrPrimaryType.equals("dam:AssetContent")) {
                        String relativePath = h.getProperties().get("dam:relativePath").toString();
                        String extension = relativePath.substring(relativePath.lastIndexOf(".") + 1); //exclude the .

                        newResult.setExtension(extension);
                        newResult.setDamAsset(true);
                    }

                    if (h.getProperties().get("subtitle") != null) {
                        newResult.setSubTitle(h.getProperties().get("subtitle").toString());
                    }

                    // Get description from page else get the excerpt from search hit
                    newResult.setExcerpt(h.getProperties().get(JcrConstants.JCR_DESCRIPTION, h.getExcerpt()));
                    newResult.setTitle(h.getTitle());
                    newResult.setPathUrl(h.getPath());

                    searchResults.add(newResult);
                }
            }
        } catch (RepositoryException re) {
            getLogger().warn("Repository exception thrown: " + re.toString());
        }
    }

    /**
     * Gets the Image File Reference from the Resource at the given path.
     *
     * @param resourceResolver `ResourceResolver` instance
     * @param path The JCR path to the resource
     * @return A string containing the resource path
     */
    private String getPathFromImageResource(ResourceResolver resourceResolver, String path) {
        String imagePath = null;

        if(resourceResolver != null) {
            Resource image = resourceResolver.resolve(path);

            if (image != null && !ResourceUtil.isNonExistingResource(image)) {
                ValueMap valueMap = image.adaptTo(ValueMap.class);

                if (valueMap != null && valueMap.containsKey("fileReference")) {
                    imagePath = valueMap.get("fileReference", String.class);
                }
            }
        }

        return imagePath;
    }

%>