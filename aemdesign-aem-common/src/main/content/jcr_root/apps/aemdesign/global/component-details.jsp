<%@ page import="com.day.cq.wcm.api.Page" %>
<%@ page import="com.day.cq.wcm.api.PageManager" %>
<%@ page import="org.apache.sling.api.resource.ResourceResolver" %>
<%@ page import="java.util.*" %><%!



    protected List<ComponentProperties> getPageListInfo(PageContext pageContext, PageManager pageManager, ResourceResolver resourceResolver, String[] paths) {
        return getPageListInfo(pageContext,pageManager,resourceResolver,paths,DEFAULT_LIST_DETAILS_SUFFIX,DEFAULT_LIST_PAGE_CONTENT);
    }
    protected List<ComponentProperties> getPageListInfo(PageContext pageContext, PageManager pageManager, ResourceResolver resourceResolver, String[] paths, String[] componentNames, String[] pageRoots) {
        List<ComponentProperties> pages = new ArrayList<>();
        for (String path : paths) {
            Page child = pageManager.getPage(path);
            if (child!=null) {
                pages.add(getPageInfo(pageContext,child,resourceResolver,componentNames,pageRoots));
            }
        }
        return pages;
    }

    protected List<ComponentProperties> getPageListInfo(PageContext pageContext, PageManager pageManager, ResourceResolver resourceResolver, Iterator<Page> pageList) {
        return getPageListInfo(pageContext,pageManager,resourceResolver,pageList,DEFAULT_LIST_DETAILS_SUFFIX,DEFAULT_LIST_PAGE_CONTENT);
    }
    protected List<ComponentProperties> getPageListInfo(PageContext pageContext, PageManager pageManager, ResourceResolver resourceResolver, Iterator<Page> pageList, String[] detailsComponentName, String[] pageRoots) {
        List<ComponentProperties> pages = new ArrayList<>();

        if (pageList != null) {

            while (pageList.hasNext()) {
                Page child = pageList.next();

                pages.add(getPageInfo(pageContext, child,resourceResolver,detailsComponentName,pageRoots));
            }
        }
        return pages;
    }

    /**
     * Get page info from list of page paths
     *
     * @param page page to get info from
     * @return
     */

    protected ComponentProperties getPageInfo(PageContext pageContext, Page page, ResourceResolver resourceResolver, String[] componentNames, String[] pageRoots) {
        ComponentProperties componentProperties = new ComponentProperties();

        if (page!=null) {

            String detailsNodePath = findComponentInPage(page, componentNames, pageRoots);

            if (isNotEmpty(detailsNodePath)) {

                Resource detailsNodeResource = resourceResolver.resolve(detailsNodePath);

                if (detailsNodeResource != null) {

                    componentProperties = getComponentProperties(
                            pageContext,
                            detailsNodeResource.getPath(),
                            DEFAULT_FIELDS_DETAILS_OPTIONS
                    );

                    componentProperties.put("detailsPath",detailsNodeResource.getPath());


                    componentProperties.putAll(getAssetInfo(resourceResolver,
                            getResourceImagePath(detailsNodeResource,DEFAULT_SECONDARY_IMAGE_NODE_NAME),
                            FIELD_PAGE_IMAGE_SECONDARY));

                    componentProperties.putAll(getAssetInfo(resourceResolver,
                            getResourceImagePath(detailsNodeResource,DEFAULT_BACKGROUND_IMAGE_NODE_NAME),
                            FIELD_PAGE_IMAGE_BACKGROUND));
//                    Node detailsNode = detailsNodeResource.adaptTo(Node.class);
//                    componentProperties.putAll(getDetailsBadgeConfig(detailsNode,page.getTitle()));
                }

            }

            componentProperties.put("title", page.getTitle());
            componentProperties.put("hideInNav", page.isHideInNav());
            componentProperties.put("pageNavTitle", getPageNavTitle(page));
            componentProperties.put("name", page.getName());
            componentProperties.put("href", getPageUrl(page));
            componentProperties.put("authHref", page.getPath().concat(DEFAULT_EXTENTION));
            componentProperties.put("path", page.getPath());
            componentProperties.put("vanityPath", defaultIfEmpty(page.getVanityUrl(), ""));

            componentProperties.putAll(getAssetInfo(resourceResolver,
                    getPageImgReferencePath(page),
                    FIELD_PAGE_IMAGE));


//            Image image = getPageImage(page);
//
//            if(image!=null) {
//                componentProperties.put("pageImage", image.getFileReference());
//            }

            String contentNode = getComponentNodePath(page, pageRoots);

            if (isNotEmpty(contentNode)) {
                componentProperties.put("pageContent",contentNode);
            }

        }

        return componentProperties;
    }



    /***
     * get request fields passed by list and translate to
     * @param componentProperties current component properties
     * @param resourceResolver
     * @param request
     * @return
     */
    public Map getBadgeRequestConfig(ComponentProperties componentProperties, ResourceResolver resourceResolver, HttpServletRequest request) {
        Map badgeConfig = new HashMap();

        //get primary image
        String pageImagePath = componentProperties.get(FIELD_PAGE_IMAGE,"");

        String badgeThumbnailDefault = (String)request.getAttribute(BADGE_THUMBNAIL_DEFAULT);
        if (badgeThumbnailDefault != null) {
            badgeConfig.put(FIELD_PAGE_IMAGE_THUMBNAIL, badgeThumbnailDefault);
            if (isEmpty(pageImagePath)) {
                badgeConfig.put(FIELD_PAGE_IMAGE, badgeThumbnailDefault);
            }
        }

        String badgeThumbnailType = (String)request.getAttribute(BADGE_THUMBNAIL_TYPE);
        if (badgeThumbnailType != null) {
            badgeConfig.put(FIELD_THUMBNAIL_TYPE, badgeThumbnailType);
        }

        Object badgeThumbnailWidth = request.getAttribute(BADGE_THUMBNAIL_WIDTH);
        if (badgeThumbnailWidth != null) {
            badgeConfig.put(FIELD_THUMBNAIL_WIDTH, badgeThumbnailWidth);

            if (isNotEmpty(pageImagePath)) {
                Resource pageImage = resourceResolver.resolve(pageImagePath);

                if (pageImage != null) {
                    com.adobe.granite.asset.api.Asset pageImageAsset = pageImage.adaptTo(com.adobe.granite.asset.api.Asset.class);
                    if (pageImageAsset != null) {
                        getLogger().error("getBadgeRequestConfig: " + badgeThumbnailType);
                        switch (badgeThumbnailType) {
//                    case IMAGE_OPTION_GENERATED:
//                        String imageHref = "";
//                        Long lastModified = getLastModified(_resource);
//                        imageHref = MessageFormat.format(DEFAULT_IMAGE_GENERATED_FORMAT, _resource.getPath(), lastModified.toString());
//
//                        componentProperties.put(FIELD_IMAGEURL, imageHref);
//                        break;
//                    case IMAGE_OPTION_RESPONSIVE:
//                        String[] renditionImageMapping = componentProperties.get(FIELD_RESPONSIVE_MAP, DEFAULT_RENDITION_IMAGE_MAP);
//
//                        //get rendition profile prefix selected
//                        String renditionPrefix = componentProperties.get(FIELD_RENDITION_PREFIX, "");
//
//                        //get best fit renditions set
//                        responsiveImageSet = getBestFitMediaQueryRenditionSet(asset, renditionImageMapping, renditionPrefix);
//
//                        componentProperties.put(FIELD_RENDITIONS, responsiveImageSet);
//                    case IMAGE_OPTION_ADAPTIVE:
//                        String[] adaptiveImageMapping = componentProperties.get(FIELD_ADAPTIVE_MAP, DEFAULT_ADAPTIVE_IMAGE_MAP);
//
//                        responsiveImageSet = getAdaptiveImageSet(adaptiveImageMapping, _resourceResolver, fileReference, null, _sling);
//
//                        componentProperties.put(FIELD_RENDITIONS, responsiveImageSet);
//
//                        break;
                            case IMAGE_OPTION_RENDITION:
                                int badgeThumbnailWidthInt = tryParseInt(badgeThumbnailWidth.toString(), 319);
                                com.adobe.granite.asset.api.Rendition bestRendition = getBestFitRendition(badgeThumbnailWidthInt, pageImageAsset);
                                if (bestRendition != null) {
                                    badgeConfig.put(FIELD_PAGE_IMAGE_THUMBNAIL, bestRendition.getPath());
                                }
                                break;
                            default: //IMAGE_OPTION_RENDITION
                                break;
                        }
                    }
                }
            }
        }

        Object badgeThumbnailHeight = request.getAttribute(BADGE_THUMBNAIL_HEIGHT);
        if (badgeThumbnailHeight != null) {
            badgeConfig.put(FIELD_THUMBNAIL_HEIGHT, badgeThumbnailHeight);
        }

        String badgeTitleType = (String)request.getAttribute(BADGE_TITLE_TAG_TYPE);
        if (badgeTitleType != null) {
            badgeConfig.put(FIELD_TITLE_TAG_TYPE, badgeTitleType);
        }

        String badgeThumbnailId = (String)request.getAttribute(BADGE_THUMBNAIL_ID);
        if (badgeThumbnailId != null) {
            badgeConfig.put(FIELD_PAGE_IMAGE_ID, badgeThumbnailId);
        }

        String badgeThumbnailLicenseInfo = (String)request.getAttribute(BADGE_THUMBNAIL_LICENSE_INFO);
        if (badgeThumbnailLicenseInfo != null) {
            badgeConfig.put(FIELD_PAGE_IMAGE_LICENSE_INFO, badgeThumbnailLicenseInfo);
        }
        return badgeConfig;
    }


%>