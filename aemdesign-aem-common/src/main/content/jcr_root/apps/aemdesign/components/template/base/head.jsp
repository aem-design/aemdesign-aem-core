<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ page import="org.apache.commons.lang3.StringEscapeUtils" %>
<%@ page import="org.apache.sling.api.resource.NonExistingResource" %>
<%!
    // /content/aemdesign-showcase/en/admin/header
    private Page getPage(SlingHttpServletRequest request, String path) {
        ResourceResolver resourceResolver = request.getResourceResolver();
        Resource resource = resourceResolver.getResource(path);

        if (resource instanceof NonExistingResource)
            resource = request.getResourceResolver().resolve(path.toLowerCase());

        if (resource instanceof NonExistingResource || resource == null)
            return null;

        return resource.adaptTo(Page.class);
    }

    // /content/aemdesign-showcase/en/admin/header/jcr:content/article/par/theme
    private Node getNode(SlingHttpServletRequest request, String path) throws RepositoryException {
        ResourceResolver resourceResolver = request.getResourceResolver();
        Resource resource = resourceResolver.getResource(path);

        if (resource instanceof NonExistingResource)
            resource = request.getResourceResolver().resolve(path.toLowerCase());

        if (resource instanceof NonExistingResource || resource == null)
            return null;

        return resource.adaptTo(Node.class);
    }

    // Path: article/par/theme (path without jcr:content)
    private Node getNode(Page page, String path) throws RepositoryException {
        Resource resource = page.getContentResource(path);

        if (resource instanceof NonExistingResource)
            resource = page.getContentResource(path.toLowerCase());

        if (resource instanceof NonExistingResource || resource == null)
            return null;

        return resource.adaptTo(Node.class);
    }

    private Node getJcrNode(Page page) throws RepositoryException {
        return page.getContentResource().adaptTo(Node.class);           // page.getContentResource(): return jcr:content
    }

    private List<Node> getNodes(Node node) throws RepositoryException {
        List<Node> list = new ArrayList<Node>();
        list.add(node);

        NodeIterator nodes = node.getNodes();
        while (nodes.hasNext()) {
            list.addAll(getNodes(nodes.nextNode()));
        }
        return list;
    }

    private List<Node> getNodes(Node node, String name) throws RepositoryException {
        List<Node> list = new ArrayList<Node>();
        if (node.getPath().matches(String.format(".*/%s$", name)))
            list.add(node);

        NodeIterator nodes = node.getNodes();
        while (nodes.hasNext()) {
            list.addAll(getNodes(nodes.nextNode(), name));
        }
        return list;
    }

    private ValueMap getProperties(SlingHttpServletRequest request, Node node) throws RepositoryException {
        ResourceResolver resourceResolver = request.getResourceResolver();
        Resource resource = resourceResolver.resolve(node.getPath());
        return resource.adaptTo(ValueMap.class);
    }

    private String getProperty(SlingHttpServletRequest request, Node node, String name) throws RepositoryException {
        ValueMap properties = getProperties(request, node);
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            if (entry.getKey().equals(name))
                return (String)entry.getValue();
        }
        return null;
    }

    private List<Page> getParentPages(SlingHttpServletRequest request, Page page) {
        List<Page> list = new ArrayList<Page>();
        list.add(page);
        if (page.getParent() != null)
            list.addAll(getParentPages(request, page.getParent()));
        return list;
    }

    private Node getThemeNode(SlingHttpServletRequest request, Page page) throws RepositoryException {
        List<Page> pages = getParentPages(request, page);
        for (Page p: pages) {
            List<Node> references = getNodes(getJcrNode(p), "reference");
            for (Node reference: references) {
                String referencePath = getProperty(request, reference, "path");             // Node path
                if (referencePath != null && !referencePath.equals("")) {
                    Node refNode = getNode(request, referencePath);
                    if (refNode != null) {
                        List<Node> themes = getNodes(refNode, "theme");
                        if (themes.size() > 0)
                            return themes.get(0);
                    }
                }
            }
        }
        return null;
    }
%>
<%
    String favIcon = _currentDesign.getPath() + "/icons/aemdesign/aemdesign-favicon.gif";
    if (_resourceResolver.getResource(favIcon) == null) {
        favIcon = null;
    } else {
        // make sure it maps.
        favIcon = mappedUrl(_resourceResolver, favIcon);
    }

    // get home page - to get clientlib for sites

    Page homePage = _currentPage.getAbsoluteParent(DEPTH_HOMEPAGE);
    if (homePage == null){
        homePage = _currentPage;
    }

    String currentPageTitle = getPageTitleBasedOnDepth(_currentPage);
    // fix for bluetooth pages
    currentPageTitle=currentPageTitle.replace("<sup>","");
    currentPageTitle=currentPageTitle.replace("</sup>","");

    Page rootPage = _currentPage.getAbsoluteParent(DEPTH_ROOTNODE);

    String rootTitle = "";
    if (rootPage != null) {
        rootTitle = getPageTitleBasedOnDepth(rootPage);
        if (!"".equals(rootTitle)) {
            rootTitle = " - " + rootTitle;
        }
    }

    String keywords = getTagsAsKeywords(_tagManager,",",_properties.get(TagConstants.PN_TAGS,new String[0]),_slingRequest.getLocale());
    String description = _properties.get("description", "");

    //get canonical url - if the hostname does not start with www., append it.
    String canonicalUrl = mappedUrl(_resourceResolver, request.getPathInfo());

    String sectionColorStylesheetUrl = "";

    boolean isThemeExists = false;
    Node theme = getThemeNode(_slingRequest, _currentPage);
    if (theme != null) {
        String cssPath = getProperty(_slingRequest, theme, "cssPath");
        isThemeExists = cssPath != null;
    }

    if (isThemeExists)
        sectionColorStylesheetUrl = theme.getPath() + ".css";

%>
<head>
    <meta charset="utf-8"/>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=0"/>
    <meta name="keywords" content="<%= StringEscapeUtils.escapeHtml4(keywords) %>"/>
    <title><%=currentPageTitle %><%=rootTitle%></title>
    <!-- og:tags -->
    <meta name="description" content="<%= StringEscapeUtils.escapeHtml4(description) %>"/>

    <% if (WCMMode.DISABLED != CURRENT_WCMMODE) { %>
    <cq:include script="/libs/wcm/core/components/init/init.jsp"/><%-- init.jsp is for author --%>
    <cq:include script="/libs/foundation/components/page/stats.jsp"/>
    <cq:include script="/libs/wcm/mobile/components/simulator/simulator.jsp"/>
    <%currentDesign.writeCssIncludes(pageContext); %>

    <% } %>

    <cq:include script="headlibs.jsp"/>
    <link href="<%=sectionColorStylesheetUrl%>" data-href="" rel="stylesheet" id="section-color-stylesheet">
    <link href="" data-href="" rel="stylesheet" id="theme-stylesheet">

    <style>
        body {
            /*background-image: url('');*/
        }
    </style>

    <% if (favIcon != null) { %>
    <link rel="icon" type="image/vnd.microsoft.icon" href="<%= favIcon %>" />
    <link rel="shortcut icon" type="image/x-icon" href="<%= favIcon %>"/>
    <% } %>
    <link rel="canonical" href="<%= canonicalUrl %>" />

</head>
