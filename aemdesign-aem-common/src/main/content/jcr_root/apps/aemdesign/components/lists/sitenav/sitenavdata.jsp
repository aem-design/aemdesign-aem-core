<%@ page import="com.day.text.Text, java.util.ArrayList" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.day.cq.wcm.api.PageFilter" %>
<%@ page import="com.day.cq.wcm.api.Page" %>
<%@ page import="org.apache.sling.api.resource.Resource" %>
<%@ page import="javax.jcr.Node" %>
<%@ page import="org.apache.sling.api.resource.NonExistingResource" %>
<%!

    private static final String PAR_PAGEDETAILS = "par/page-details";
    private static final String DETAILS_MENU_ICON = "menuIcon";
    private static final String DETAILS_MENU_ICONPATH = "menuIconPath";
    private static final String DETAILS_MENU_COLOR = "menuColor";
    private static final String DETAILS_MENU_COLOR_DEFAULT = "default";
    private static final String DETAILS_MENU_ACCESSKEY = "accesskey";
    private static final String DETAILS_TAB_ICON = "tabIcon";
    private static final String DETAILS_TAB_ICONPATH = "tabIconPath";
    private static final String DETAILS_TAB_GALLERYBGIMAGE = "par/page-details/gallerybgimage";
    private static final String DETAILS_TITLE = "title";

    protected List<Map> getSimpleMenuPageList(PageManager pageManager, String[] paths, Page currentPage, SlingHttpServletRequest req) throws RepositoryException {
        List<Map> pages = new ArrayList<Map>();


        for (String path : paths) {
            //Page child = resolver.getResource(path).adaptTo(Page.class);
            Page child = pageManager.getPage(path);

            if (child != null) {
                Map infoStruct = new HashMap();

                //grab page info
//                Resource contentRes = child.getContentResource();
//                Node contentNode = contentRes.adaptTo(Node.class);

                infoStruct.putAll(getPageLinkInfo(child));

                Resource parSys = child.getContentResource("par");
                Boolean hasChildren = false;
                if (parSys != null) {
                    Node parSysNode = parSys.adaptTo(Node.class);
                    if (parSysNode.hasNodes()) {
                        hasChildren = true;
                        infoStruct.put("parsysPath", parSys.getPath());
                    } else {
                        infoStruct.put("parsysPath", "");
                    }
                } else {
                    infoStruct.put("parsysPath", "");
                }

                //grab page details info
                infoStruct.putAll(getPageDetailsInfo(child));

                //grab children for current 2nd level of current path
                String currentPath = currentPage != null ? currentPage.getPath() : "";

                infoStruct.put("currentPath", currentPath);

                infoStruct.put("current", currentPath.equals(child.getPath()));

                pages.add(infoStruct);
            }
        }

        return pages;
    }


    /**
     * grab page details info
     * @param currentPage
     * @return
     */
    protected Map getPageDetailsInfo(Page currentPage) {

        Map infoStruct = new HashMap();

        Node pageDetails = getDetailsNode(currentPage, PAR_PAGEDETAILS);
        if (pageDetails != null) {


            String title = "";
            boolean showAsMenuIcon = false;
            String showAsMenuIconPath = "";
            try {
                title = getPropertyWithDefault(pageDetails, DETAILS_TITLE, getPageNavTitle(currentPage));
                showAsMenuIcon = Boolean.parseBoolean(getPropertyWithDefault(pageDetails, DETAILS_MENU_ICON, "false"));
                showAsMenuIconPath = getPropertyWithDefault(pageDetails, DETAILS_MENU_ICONPATH, "");
            } catch (Exception ex) {
                getLogger().warn("showAsMenuIcon: ", ex);
            }
            infoStruct.put("showAsMenuIcon", showAsMenuIcon);
            infoStruct.put("showAsMenuIconPath", showAsMenuIconPath);
            infoStruct.put("title", title);

            boolean showAsTabIcon = false;
            String showAsTabIconPath = "";
            try {
                showAsTabIcon = Boolean.parseBoolean(getPropertyWithDefault(pageDetails, DETAILS_TAB_ICON, "false"));
                showAsTabIconPath = getPropertyWithDefault(pageDetails, DETAILS_TAB_ICONPATH, "");
            } catch (Exception ex) {
                getLogger().warn("showAsTabIcon: ", ex);
            }
            infoStruct.put("showAsTabIcon", showAsTabIcon);
            infoStruct.put("showAsTabIconPath", showAsTabIconPath);

            String gallerybgimage = "empty";
            try {

                Node galleryBG = getComponentNode(currentPage, DETAILS_TAB_GALLERYBGIMAGE);
                if (galleryBG != null) {
                    if (galleryBG.hasProperty("fileReference")) {
                        gallerybgimage = galleryBG.getProperty("fileReference").getString();
                    }
                }

            } catch (Exception ex) {
                getLogger().warn("gallerybgimage: ", ex);
            }
            infoStruct.put("gallerybgimage", gallerybgimage);

            String color = "";
            try {
                color = getPropertyWithDefault(pageDetails, DETAILS_MENU_COLOR, "");
            } catch (Exception ex) {
                getLogger().warn("color: ", ex);
            }
            infoStruct.put("menuColor", color);


            String accesskey = "";
            try {
                accesskey = getPropertyWithDefault(pageDetails, DETAILS_MENU_ACCESSKEY, "");
            } catch (Exception ex) {
                getLogger().warn("accesskey: ", ex);
            }
            infoStruct.put("accesskey", accesskey);

        } else {
            infoStruct.put("title", getPageNavTitle(currentPage));
        }

        return infoStruct;
    }

    /**
     * Get a list of page {title, href ...} info
     *
     * @param pageManager resolver to get the pages at the paths
     * @param paths string array of paths
     * @return
     */
    protected List<Map> getMenuPageList(PageManager pageManager, String[] paths, Page currentPage, SlingHttpServletRequest req, boolean isThemeExists) throws RepositoryException {
        List<Map> pages = new ArrayList<Map>();


        for (String path : paths) {
            //Page child = resolver.getResource(path).adaptTo(Page.class);
            Page child = pageManager.getPage(path);

            if (child != null) {
                Map infoStruct = new HashMap();

                //grab page info
//                Resource contentRes = child.getContentResource();
//                Node contentNode = contentRes.adaptTo(Node.class);

                infoStruct.putAll(getPageLinkInfo(child));

                Resource parSys = child.getContentResource("par");
                Boolean hasChildren = false;
                if (parSys != null) {
                    Node parSysNode = parSys.adaptTo(Node.class);
                    if (parSysNode.hasNodes()) {
                        hasChildren = true;
                        infoStruct.put("parsysPath", parSys.getPath());
                    } else {
                        infoStruct.put("parsysPath", "");
                    }
                } else {
                    infoStruct.put("parsysPath", "");
                }

                //grab page details info
                infoStruct.putAll(getPageDetailsInfo(child));


                //grab children for current 2nd level of current path
                String currentPath = currentPage != null ? currentPage.getPath() : "";

                infoStruct.put("currentPath", currentPath);


                if (StringUtils.isNotEmpty(currentPath)) {

                    //if menu item is of same level as current path then grab its children
                    String section = Text.getAbsoluteParent(currentPath, 3);

                    infoStruct.put("current", section.equals(child.getPath()));
                    if (!isThemeExists) {
                        infoStruct.put("menuColor", getMenuColor(req, child));
                    }

                    List<Map> children = getChildren(child, currentPath, 3, req, isThemeExists);
                    infoStruct.put("children", children);

                    hasChildren = children.size()!=0;

                }


                String noContentClass = (hasChildren ? "" : "no-content");

                infoStruct.put("cssClass", StringUtils.join("page-", child.getName().trim(), noContentClass, " "));

                pages.add(infoStruct);
            }
        }

        return pages;
    }

    /**
     * recursevley build a tree for page up defined by levels
     * @param page starting page
     * @param currentPath curret path used to determined active pages
     * @param level level to gather
     * @param req
     * @return
     */

    private List<Map> getChildren(Page page, String currentPath, int level, SlingHttpServletRequest req, boolean isThemeExists) {
        List<Map> childrenList = new ArrayList<Map>();

        if (page != null && level-- > 0) {
            Iterator<Page> children = page.listChildren(new PageFilter(req));

            while (children.hasNext()) {
                Page nextchild = children.next();
                //skip if hidden
                if (!nextchild.isHideInNav()) {
                    String childPath = nextchild.getPath();

                    Map info = getPageLinkInfo(nextchild);

                    boolean current = false;
                    if (currentPath.equals(childPath)) {
                        current = true;
                    } else if (currentPath.startsWith(childPath+"/")) {
                        current = true;
                    } else if (currentPath.indexOf(childPath+"/") > 0) {
                        current = true;
                    }

                    info.put("current", current);
                    if (!isThemeExists) {
                        info.put("menuColor", getMenuColor(req, nextchild));
                    }

                    if (level > 0) {
                        //keep going
                        info.put("children", getChildren(nextchild, currentPath, level, req, isThemeExists));
                    }

                    childrenList.add(info);
                }
            }
        }

        return childrenList;
    }

    /***
     * get information about the page
     * @param page
     * @return
     */

    private Map getPageLinkInfo(Page page) {
        Map info = new HashMap();

        if (page != null) {
            ValueMap childVM = page.getProperties();

            info.put("title", getPageNavTitle(page));

            String vanityPath = defaultIfEmpty(page.getVanityUrl(), "");

            info.put("vanityPath", vanityPath);

            String redirectTarget = page.getPath();

            if (childVM.containsKey("redirectTarget")) {
                redirectTarget = childVM.get("redirectTarget", "");
            }

            //respect redirect targets
            if (redirectTarget.contains("http:")) {
                info.put("href", redirectTarget);
            } else {
                info.put("href", redirectTarget + ".html");
//                //respect vanity URLs
//                if (isNotEmpty(vanityPath)){
//                    info.put("href", vanityPath);
//                } else {
//                    info.put("href", redirectTarget + ".html");
//                }
            }

            info.put("authHref", page.getPath() + ".html");
            info.put("pathHref", page.getPath());

        }

        return info;

    }

    /**
     * Return a JCR node for the news details of <code>thisPage</code>
     *
     * @param thisPage is the page to inspect for newsdetails
     * @return a JCR node or null when not found
     */
    private Node getDetailsNode(Page thisPage, String nodePath) {
        if (thisPage == null) {
            return null;
        }

        Resource detailResource = thisPage.getContentResource(nodePath);
        Node detailsNode = null;
        if (detailResource != null) {
            detailsNode = detailResource.adaptTo(Node.class);
        }
        return detailsNode;
    }

    /**
     * Get a list of page {title, href ...} info
     *
     * @param pageManager resolver to get the pages at the paths
     * @param paths string array of paths
     * @return
     */

    protected List<Map> getPageList(PageManager pageManager, String[] paths) {
        List<Map> pages = new ArrayList<Map>();

        for (String path : paths) {
            //Page child = resolver.getResource(path).adaptTo(Page.class);
            Page child = pageManager.getPage(path);

            if (child != null) {
                Map infoStruct = new HashMap();

                infoStruct.put("title", getPageNavTitle(child));
                infoStruct.put("href", child.getPath() + ".html");
                //infoStruct.put("alt",child.getProperties().get("redirectTarget","#"));

                Image image = getPageImage(child);

                if (image != null) {
                    infoStruct.put("image", image.getFileReference());
                    infoStruct.put("class", child.getName());
                } else {
                    infoStruct.put("class", "blank");
                }

                pages.add(infoStruct);
            }
        }

        return pages;
    }

    /***
     * return path before /JCR:CONTENT
     * @param slingRequest
     * @return
     */

    private String getPrimaryPath(SlingHttpServletRequest slingRequest) {
        String requestPath = slingRequest.getRequestPathInfo().getResourcePath();
        if (requestPath.contains(JcrConstants.JCR_CONTENT)) {
            return requestPath.substring(0, requestPath.indexOf(JcrConstants.JCR_CONTENT)-1);
        }

        return "";
    }

    private String getMenuColor(SlingHttpServletRequest request, Page page) {
        if (page == null) {
            return null;
        }
        String menuColor = DETAILS_MENU_COLOR_DEFAULT;
        try {
            Node detailNode = findDetailNode(page);

            if (detailNode != null) {
                if (detailNode.hasProperty(DETAILS_MENU_COLOR)) {
                    menuColor = detailNode.getProperty(DETAILS_MENU_COLOR).getString();
                }
            }
        } catch (Exception ex) {

        }

        return "default".equalsIgnoreCase(menuColor) ? "" : menuColor;
    }

%>