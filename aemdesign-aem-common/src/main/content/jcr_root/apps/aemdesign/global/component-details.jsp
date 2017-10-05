<%@ page import="com.day.cq.wcm.api.Page" %>
<%@ page import="com.day.cq.wcm.api.PageManager" %>
<%@ page import="org.apache.sling.api.resource.ResourceResolver" %>
<%@ page import="java.util.*" %><%!



    protected List<Map> getPageListInfo(PageManager pageManager, ResourceResolver resourceResolver, String[] paths) {
        return getPageListInfo(pageManager,resourceResolver,paths,DEFAULT_LIST_DETAILS_SUFFIX,DEFAULT_LIST_PAGE_CONTENT);
    }
    protected List<Map> getPageListInfo(PageManager pageManager, ResourceResolver resourceResolver, String[] paths, String[] componentNames, String[] pageRoots) {
        List<Map> pages = new ArrayList<Map>();
        for (String path : paths) {
            Page child = pageManager.getPage(path);
            if (child!=null) {
                pages.add(getPageInfo(child,resourceResolver,componentNames,pageRoots));
            }
        }
        return pages;
    }

    protected List<Map> getPageListInfo(PageManager pageManager, ResourceResolver resourceResolver, Iterator<Page> pageList) {
        return getPageListInfo(pageManager,resourceResolver,pageList,DEFAULT_LIST_DETAILS_SUFFIX,DEFAULT_LIST_PAGE_CONTENT);
    }
    protected List<Map> getPageListInfo(PageManager pageManager, ResourceResolver resourceResolver, Iterator<Page> pageList, String[] detailsComponentName, String[] pageRoots) {
        List<Map> pages = new ArrayList<Map>();

        if (pageList != null) {

            while (pageList.hasNext()) {
                Page child = pageList.next();

                pages.add(getPageInfo(child,resourceResolver,detailsComponentName,pageRoots));
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

    protected Map getPageInfo(Page page, ResourceResolver resourceResolver, String[] componentNames, String[] pageRoots) {
        Map infoStruct = new HashMap();

        if (page!=null) {

            infoStruct.put("title", page.getTitle());
            infoStruct.put("name", page.getName());
            infoStruct.put("href", page.getPath().concat(DEFAULT_EXTENTION));
            infoStruct.put("path", page.getPath());

            Image image = getPageImage(page);

            if(image!=null) {
                infoStruct.put("pageImage", image.getFileReference());
            }

            String detailsNodePath = findComponentInPage(page, componentNames, pageRoots);

            if (isNotEmpty(detailsNodePath)) {

                Resource detailsNodeResource = resourceResolver.resolve(detailsNodePath);

                if (detailsNodeResource != null) {
                    Node detailsNode = detailsNodeResource.adaptTo(Node.class);
                    infoStruct.putAll(getDetailsBadgeConfig(detailsNode,page.getTitle()));
                }

            }

            String contentNode = getComponentNodePath(page, pageRoots);

            if (isNotEmpty(contentNode)) {
                infoStruct.put("pageContent",contentNode);
            }

        }

        return infoStruct;
    }

    /***
     * get badge config info from details node
     * @param pageDetails
     * @param defaultTitle
     * @return
     */
    public Map<String, Object> getDetailsBadgeConfig(Node pageDetails, String defaultTitle) {
        Map<String, Object> infoStruct = new HashMap();


        if (pageDetails != null) {

            try {
                infoStruct.put("detailsPath", pageDetails.getPath());
            } catch (Exception ex) {
                getLogger().warn("JCR ERROR: {}", ex);
            }

            String title = "";
            boolean showAsMenuIcon = false;
            String showAsMenuIconPath = "";
            try {
                title = getPropertyWithDefault(pageDetails, DETAILS_TITLE, defaultTitle);
                showAsMenuIcon = Boolean.parseBoolean(getPropertyWithDefault(pageDetails, DETAILS_MENU_ICON, "false"));
                showAsMenuIconPath = getPropertyWithDefault(pageDetails, DETAILS_MENU_ICONPATH, "");
            } catch (Exception ex) {
                getLogger().warn("JCR ERROR: {}", ex);
            }
            infoStruct.put("showAsMenuIcon", showAsMenuIcon);
            infoStruct.put("showAsMenuIconPath", showAsMenuIconPath);
            infoStruct.put(DETAILS_TITLE, title);

            boolean showAsTabIcon = false;
            String showAsTabIconPath = "";
            try {
                showAsTabIcon = Boolean.parseBoolean(getPropertyWithDefault(pageDetails, DETAILS_TAB_ICON, "false"));
                showAsTabIconPath = getPropertyWithDefault(pageDetails, DETAILS_TAB_ICONPATH, "");
            } catch (Exception ex) {
                getLogger().warn("JCR ERROR: {}", ex);
            }
            infoStruct.put("showAsTabIcon", showAsTabIcon);
            infoStruct.put("showAsTabIconPath", showAsTabIconPath);

            boolean showAsTitleIcon = false;
            String showAsTitleIconPath = "";
            try {
                showAsTitleIcon = Boolean.parseBoolean(getPropertyWithDefault(pageDetails, DETAILS_TITLE_ICON, "false"));
                showAsTitleIconPath = getPropertyWithDefault(pageDetails, DETAILS_TITLE_ICONPATH, "");
            } catch (Exception ex) {
                getLogger().warn("JCR ERROR: {}", ex);
            }
            infoStruct.put("showAsTitleIcon", showAsTitleIcon);
            infoStruct.put("showAsTitleIconPath", showAsTitleIconPath);
        } else {
            infoStruct.put(DETAILS_TITLE, defaultTitle);
        }

        return infoStruct;
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

        Object badgeThumbnailDefault = request.getAttribute(BADGE_THUMBNAIL_DEFAULT);
        if (badgeThumbnailDefault != null) {
            badgeConfig.put(FIELD_PAGE_IMAGE_THUMBNAIL, badgeThumbnailDefault.toString());
            if (isEmpty(pageImagePath)) {
                badgeConfig.put(FIELD_PAGE_IMAGE, badgeThumbnailDefault.toString());
            }
        }
        Object badgeThumbnailType = request.getAttribute(BADGE_THUMBNAIL_TYPE);
        if (badgeThumbnailType != null) {
            badgeConfig.put(FIELD_THUMBNAIL_TYPE, badgeThumbnailType.toString());
        }

        Object badgeThumbnailWidth = request.getAttribute(BADGE_THUMBNAIL_WIDTH);
        if (badgeThumbnailWidth != null) {
            badgeConfig.put(FIELD_THUMBNAIL_WIDTH, badgeThumbnailWidth.toString());

            if (isNotEmpty(pageImagePath)) {
                Resource pageImage = resourceResolver.resolve(pageImagePath);

                if (pageImage != null) {
                    com.adobe.granite.asset.api.Asset pageImageAsset = pageImage.adaptTo(com.adobe.granite.asset.api.Asset.class);
                    if (pageImageAsset != null) {
                        getLogger().error("getBadgeRequestConfig: " + badgeThumbnailType.toString());
                        switch (badgeThumbnailType.toString()) {
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
            badgeConfig.put(FIELD_THUMBNAIL_HEIGHT, badgeThumbnailHeight.toString());
        }

        Object badgeTitleType = request.getAttribute(BADGE_TITLE_TAG_TYPE);
        if (badgeTitleType != null) {
            badgeConfig.put(FIELD_TITLE_TAG_TYPE, badgeTitleType.toString());
        }

        Object badgeThumbnailId = request.getAttribute(BADGE_THUMBNAIL_ID);
        if (badgeThumbnailId != null) {
            badgeConfig.put(FIELD_PAGE_IMAGE_ID, badgeThumbnailId.toString());
        }

        Object badgeThumbnailLicenseInfo = request.getAttribute(BADGE_THUMBNAIL_LICENSE_INFO);
        if (badgeThumbnailLicenseInfo != null) {
            badgeConfig.put(FIELD_PAGE_IMAGE_LICENSE_INFO, badgeThumbnailLicenseInfo.toString());
        }
        return badgeConfig;
    }


%>