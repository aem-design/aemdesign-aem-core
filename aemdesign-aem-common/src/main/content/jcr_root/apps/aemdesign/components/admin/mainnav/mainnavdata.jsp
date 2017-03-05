<%@ page import="com.day.text.Text, java.util.ArrayList" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="org.apache.sling.api.resource.Resource" %>
<%@ page import="javax.jcr.Node" %>
<%!

    private static final String PAR_PAGEDETAILS = "article/par/page-details";
    private static final String DETAILS_MENU_ICON = "menuIcon";
    private static final String DETAILS_MENU_ICONPATH = "menuIconPath";
    private static final String DETAILS_TAB_ICON = "tabIcon";
    private static final String DETAILS_TAB_ICONPATH = "tabIconPath";
    private static final String DETAILS_TAB_GALLERYBGIMAGE = "article/par/page-details/gallerybgimage";
    private static final String DETAILS_TITLE = "title";

    /**
     * Get a list of page {title, href ...} info
     *
     * @param pageManager resolver to get the pages at the paths
     * @param paths string array of paths
     * @return
     */

    protected List<Map> getMenuPageList(PageManager pageManager, String[] paths) throws RepositoryException{
        List<Map> pages = new ArrayList<Map>();

        for (String path : paths) {
            //Page child = resolver.getResource(path).adaptTo(Page.class);
            Page child = pageManager.getPage(path);

            if (child!=null) {
                Map infoStruct = new HashMap();

                Resource contentRes = child.getContentResource();
                Node contentNode = contentRes.adaptTo(Node.class);
                String redirectTarget = child.getPath();
                if(contentNode.hasProperty("redirectTarget")){
                    redirectTarget = contentNode.getProperty("redirectTarget").getString();
                }

                if (redirectTarget.contains("http:")){
                    infoStruct.put("href", redirectTarget);
                } else {
                    infoStruct.put("href", redirectTarget + ".html");
                }

                infoStruct.put("authHref", child.getPath() + ".html");
                infoStruct.put("pathHref", child.getPath());

                Resource parSys = child.getContentResource("par");
                Boolean hasChildren = false;
                if (parSys != null) {
                    Node parSysNode = parSys.adaptTo(Node.class);
                    if(parSysNode.hasNodes()) {
                        hasChildren = true;
                        infoStruct.put("parsysPath", parSys.getPath());
                    } else {
                        infoStruct.put("parsysPath", "");
                    }
                } else {
                    infoStruct.put("parsysPath", "");
                }

                String noContentClass = (hasChildren?"":" no-content");

                infoStruct.put("cssClass", StringUtils.join(" page-",child.getName().trim(),noContentClass," "));

                Node page-details = getDetailsNode(child,PAR_PAGEDETAILS);
                if (page-details!=null) {

                    String title="";
                    boolean showAsMenuIcon = false;
                    String showAsMenuIconPath = "";
                    try {
                        title = getPropertyWithDefault(page-details, DETAILS_TITLE, child.getTitle());
                        showAsMenuIcon = Boolean.parseBoolean(getPropertyWithDefault(page-details, DETAILS_MENU_ICON,"false"));
                        showAsMenuIconPath = getPropertyWithDefault(page-details, DETAILS_MENU_ICONPATH,"");
                    } catch (Exception ex) {
                        getLogger().warn("showAsMenuIcon: ",ex);
                    }
                    infoStruct.put("showAsMenuIcon", showAsMenuIcon);
                    infoStruct.put("showAsMenuIconPath", showAsMenuIconPath);
                    infoStruct.put("title", title);

                    boolean showAsTabIcon = false;
                    String showAsTabIconPath = "";
                    try {
                        showAsTabIcon = Boolean.parseBoolean(getPropertyWithDefault(page-details, DETAILS_TAB_ICON,"false"));
                        showAsTabIconPath = getPropertyWithDefault(page-details, DETAILS_TAB_ICONPATH,"");
                    } catch (Exception ex) {
                        getLogger().warn("showAsTabIcon: ",ex);
                    }
                    infoStruct.put("showAsTabIcon", showAsTabIcon);
                    infoStruct.put("showAsTabIconPath", showAsTabIconPath);

                    String gallerybgimage = "empty";
                    try {

                        Node galleryBG = getComponentNode(child,DETAILS_TAB_GALLERYBGIMAGE);
                        if (galleryBG != null) {
                            if (galleryBG.hasProperty("fileReference")) {
                                gallerybgimage = galleryBG.getProperty("fileReference").getString();
                            }
                        }

                    } catch (Exception ex) {
                        getLogger().warn("gallerybgimage: ",ex);
                    }
                    infoStruct.put("gallerybgimage", gallerybgimage);


                } else {
                    infoStruct.put("title", child.getTitle());
                }

                pages.add(infoStruct);
            }
        }

        return pages;
    }
    /**
     * Return a JCR node for the news details of <code>thisPage</code>
     *
     * @param thisPage is the page to inspect for newsdetails
     * @return a JCR node or null when not found
     */
    private Node getDetailsNode(Page thisPage,String nodePath) {
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

            if (child!=null) {
                Map infoStruct = new HashMap();

                infoStruct.put("title", child.getTitle());
                infoStruct.put("href", child.getPath() + ".html");
                //infoStruct.put("alt",child.getProperties().get("redirectTarget","#"));

                Image image = getPageImage(child);

                if(image!=null) {
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
%>