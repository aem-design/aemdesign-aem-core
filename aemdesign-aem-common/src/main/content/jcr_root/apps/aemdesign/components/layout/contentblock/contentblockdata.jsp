<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%!

    private static final String DETAILS_TITLE = "title";


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
                infoStruct.put("href", child.getPath().concat(DEFAULT_EXTENTION));

                Image image = getPageImage(child);

                if(image!=null) {
                    infoStruct.put("pageImage", image.getFileReference());
                }

                String[] supportedDetails = {PAR_PAGEDETAILS,ARTICLE_PAR_PAGEDETAILS};

                Node pageDetails = getDetailsNode(child,supportedDetails);
                if (pageDetails!=null) {

                    String title="";
                    boolean showAsMenuIcon = false;
                    String showAsMenuIconPath = "";
                    try {
                        title = getPropertyWithDefault(pageDetails, DETAILS_TITLE, child.getTitle());
                        showAsMenuIcon = Boolean.parseBoolean(getPropertyWithDefault(pageDetails, DETAILS_MENU_ICON,"false"));
                        showAsMenuIconPath = getPropertyWithDefault(pageDetails, DETAILS_MENU_ICONPATH,"");
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
                        showAsTabIconPath = getPropertyWithDefault(pageDetails, DETAILS_TAB_ICONPATH,"");
                    } catch (Exception ex) {
                        getLogger().warn("JCR ERROR: {}",ex);
                    }
                    infoStruct.put("showAsTabIcon", showAsTabIcon);
                    infoStruct.put("showAsTabIconPath", showAsTabIconPath);

                    boolean showAsTitleIcon = false;
                    String showAsTitleIconPath = "";
                    try {
                        showAsTitleIcon = Boolean.parseBoolean(getPropertyWithDefault(pageDetails, DETAILS_TITLE_ICON,"false"));
                        showAsTitleIconPath = getPropertyWithDefault(pageDetails, DETAILS_TITLE_ICONPATH,"");
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