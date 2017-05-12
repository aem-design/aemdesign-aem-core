<%@ page import="java.util.Iterator"%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.day.cq.wcm.api.Page" %>
<%!

    private static final String PAR_PAGEDETAILS = "par/page-details";
    private static final String DETAILS_MENU_ICON = "menuIcon";
    private static final String DETAILS_MENU_ICONPATH = "menuIconPath";
    private static final String DETAILS_TAB_ICON = "tabIcon";
    private static final String DETAILS_TAB_ICONPATH = "tabIconPath";
    private static final String DETAILS_TITLE = "title";
    private static final String PAR_CONTENTS = "par";
    /**
     * Get a list of page {title, href ...} info
     *
     * @param pageManager resolver to get the pages at the paths
     * @param paths string array of paths
     * @return
     */

    protected List<Map> getTabPageList(PageManager pageManager, ResourceResolver resourceResolver, String[] paths) {
        List<Map> pages = new ArrayList<Map>();
        for (String path : paths) {
            //Page child = resolver.getResource(path).adaptTo(Page.class);
            Page child = pageManager.getPage(path);
            if (child!=null) {
                pages.add(getTabInfo(child));
            }
        }
        return pages;
    }

    protected List<Map> getTabPageList(PageManager pageManager, ResourceResolver resourceResolver, Iterator<Page> pageList) {
        List<Map> pages = new ArrayList<Map>();

        while (pageList.hasNext()) {
            Page child = pageList.next();

            pages.add(getTabInfo(child));
        }
        return pages;
    }

    protected Map getTabInfo(Page page) {
        Map infoStruct = new HashMap();

        if (page != null) {

            infoStruct.put("title", page.getTitle());
            infoStruct.put("name", page.getName());
            infoStruct.put("href", page.getPath().concat(DEFAULT_EXTENTION));
            infoStruct.put("path", page.getPath());
            infoStruct.put("contentPath", page.getContentResource(PAR_CONTENTS).getPath());

            Node pageDetails = getDetailsNode(page,PAR_PAGEDETAILS);
            if (pageDetails!=null) {

                String title="";
                boolean showAsMenuIcon = false;
                String showAsMenuIconPath = "";
                try {
                    title = (String) getPropertyWithDefault(pageDetails, DETAILS_TITLE, page.getTitle());
                    showAsMenuIcon = Boolean.parseBoolean(getPropertyWithDefault(pageDetails, DETAILS_MENU_ICON,"false"));
                    showAsMenuIconPath = (String) getPropertyWithDefault(pageDetails, DETAILS_MENU_ICONPATH,"");
                } catch (Exception ex) {
                    getLogger().warn("JCR ERROR: {}",ex);
                }
                infoStruct.put("showAsMenuIcon", showAsMenuIcon);
                infoStruct.put("showAsMenuIconPath", showAsMenuIconPath);
                infoStruct.put("title", title);

                boolean showAsTabIcon = false;
                String showAsTabIconPath = "";
                try {
                    showAsTabIcon = Boolean.parseBoolean(getPropertyWithDefault(pageDetails, DETAILS_TAB_ICON,"false"));
                    showAsTabIconPath = (String) getPropertyWithDefault(pageDetails, DETAILS_TAB_ICONPATH,"");
                } catch (Exception ex) {
                    getLogger().warn("JCR ERROR: {}",ex);
                }
                infoStruct.put("showAsTabIcon", showAsTabIcon);
                infoStruct.put("showAsTabIconPath", showAsTabIconPath);

            } else {
                infoStruct.put("title", page.getTitle());
            }

        }
        return infoStruct;
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
%>
