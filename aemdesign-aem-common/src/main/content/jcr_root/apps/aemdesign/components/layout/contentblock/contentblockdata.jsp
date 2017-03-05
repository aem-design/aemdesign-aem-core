<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%!

    private static final String PAR_PAGEDETAILS = "par/pagedetails";
    private static final String DETAILS_MENU_ICON = "menuIcon";
    private static final String DETAILS_MENU_ICONPATH = "menuIconPath";
    private static final String DETAILS_TAB_ICON = "tabIcon";
    private static final String DETAILS_TAB_ICONPATH = "tabIconPath";
    private static final String DETAILS_TITLE_ICON = "titleIcon";
    private static final String DETAILS_TITLE_ICONPATH = "titleIconPath";
    private static final String DETAILS_TITLE = "title";
    private static final String STYLE_CLASS = "cssClass";
    private static final String STYLE_THEME_CLASS = "cssThemeClass";

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
     * Return a JCR node for a first found matching path
     *
     * @param thisPage is the page to inspect for newsdetails
     * @return a JCR node or null when not found
     */
    private Node getDetailsNode(Page thisPage,String[] nodePaths) {
        if (thisPage == null) {
            return null;
        }

        Node detailsNode = null;

        for (String nodePath : nodePaths) {
            Resource detailResource = thisPage.getContentResource(nodePath);
            if (detailResource != null) {
                detailsNode = detailResource.adaptTo(Node.class);
                return detailsNode;
            }
        }
        return null;
    }

    /**
     * Get a list of page {title, href ...} info
     *
     * @param pageManager resolver to get the pages at the paths
     * @param paths string array of paths
     * @return
     */

    protected List<Map> getContentPageList(PageManager pageManager, String[] paths) {
        List<Map> pages = new ArrayList<Map>();

        for (String path : paths) {
            //Page child = resolver.getResource(path).adaptTo(Page.class);
            Page child = pageManager.getPage(path);

            if (child!=null) {
                Map infoStruct = new HashMap();

                //infoStruct.put("title",child.getTitle());
                infoStruct.put("href", child.getPath() + ".html");

                Image image = getPageImage(child);

                if(image!=null) {
                    infoStruct.put("pageImage", image.getFileReference());
                }

                String[] supportedDetails = {PAR_PAGEDETAILS};

                Node page-details = getDetailsNode(child,supportedDetails);
                if (page-details!=null) {

                    String title="";
                    boolean showAsMenuIcon = false;
                    String showAsMenuIconPath = "";
                    try {
                        title = getPropertyWithDefault(page-details, DETAILS_TITLE, child.getTitle());
                        showAsMenuIcon = Boolean.parseBoolean(getPropertyWithDefault(page-details, DETAILS_MENU_ICON,"false"));
                        showAsMenuIconPath = getPropertyWithDefault(page-details, DETAILS_MENU_ICONPATH,"");
                    } catch (Exception ex) {
                        getLogger().warn("JCR ERROR: {}",ex);
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
                        getLogger().warn("JCR ERROR: {}",ex);
                    }
                    infoStruct.put("showAsTabIcon", showAsTabIcon);
                    infoStruct.put("showAsTabIconPath", showAsTabIconPath);

                    boolean showAsTitleIcon = false;
                    String showAsTitleIconPath = "";
                    try {
                        showAsTitleIcon = Boolean.parseBoolean(getPropertyWithDefault(page-details, DETAILS_TITLE_ICON,"false"));
                        showAsTitleIconPath = getPropertyWithDefault(page-details, DETAILS_TITLE_ICONPATH,"");
                    } catch (Exception ex) {
                        getLogger().warn("JCR ERROR: {}",ex);
                    }
                    infoStruct.put("showAsTitleIcon", showAsTitleIcon);
                    infoStruct.put("showAsTitleIconPath", showAsTitleIconPath);

                } else {
                    infoStruct.put("title", child.getTitle());
                }

                pages.add(infoStruct);
            }
        }

        return pages;
    }

%>