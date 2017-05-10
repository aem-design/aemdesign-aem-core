<%@ page import="java.util.List" %>
<%@ page import="java.util.Map,
    	com.day.cq.wcm.api.PageFilter,
		com.day.cq.wcm.api.Page" %>
<%@ page import="com.sun.xml.internal.fastinfoset.util.StringArray" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="/apps/aemdesign/global/utils.jsp" %>
<%@ include file="sitenavdata.jsp" %>
<%@page session="false" %>

<%!
    // /content/aemdesign-showcase/en/layout/header
    private Page getPage(SlingHttpServletRequest request, String path) {
        ResourceResolver resourceResolver = request.getResourceResolver();
        Resource resource = resourceResolver.getResource(path);

        if (resource instanceof NonExistingResource)
            resource = request.getResourceResolver().resolve(path.toLowerCase());

        if (resource instanceof NonExistingResource)
            return null;

        return resource.adaptTo(Page.class);
    }

    // /content/aemdesign-showcase/en/layout/header/jcr:content/article/par/theme
    private Node getNode(SlingHttpServletRequest request, String path) throws RepositoryException {
        ResourceResolver resourceResolver = request.getResourceResolver();
        Resource resource = resourceResolver.getResource(path);

        if (resource instanceof NonExistingResource)
            resource = request.getResourceResolver().resolve(path.toLowerCase());

        if (resource instanceof NonExistingResource)
            return null;

        return resource.adaptTo(Node.class);
    }

    // Path: article/par/theme (path without jcr:content)
    private Node getNode(Page page, String path) throws RepositoryException {
        Resource resource = page.getContentResource(path);

        if (resource instanceof NonExistingResource)
            resource = page.getContentResource(path.toLowerCase());

        if (resource instanceof NonExistingResource)
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
    final String DEFAULT_LISTFROM = "children";
    final String LISTFROM_CHILDREN = "children";
    final String DEFAULT_TOGGLE_STATE = "yes";
    final String DEFAULT_VARIANT = "default";
    final String VARIANT_DEFAULT = "default";
    final String VARIANT_SIMPLE = "simple";
    final String VARIANT_MAIN_NAV = "mainnav";

    //no lambada is available so this is the best that can be done
    Object[][] componentFields = {
            {"pages", new String[0]},
            {"showLangNav", DEFAULT_TOGGLE_STATE},
            {"showBackToTop", DEFAULT_TOGGLE_STATE},
            {"showSearch", DEFAULT_TOGGLE_STATE},
            {"variant", DEFAULT_VARIANT},
            {"listFrom", DEFAULT_LISTFROM},
            {"parentPage", getPrimaryPath(_slingRequest)},
            {"linkTitlePrefix", _i18n.get("linkTitlePrefix","sitenav")}
    };
    ComponentProperties componentProperties = getComponentProperties(pageContext, componentFields);
    componentProperties.putAll(getComponentStyleProperties(pageContext));
    componentProperties.put("componentAttributes", compileComponentAttributesAsAdmin(componentProperties,_component,_sling));

    String variant = componentProperties.get("variant",DEFAULT_VARIANT);

    String[] paths = {};
    if (componentProperties.get("listFrom", DEFAULT_LISTFROM).equals(LISTFROM_CHILDREN)) {
        Page parentPage = _pageManager.getPage(componentProperties.get("parentPage", ""));
        if (parentPage != null) {
            List<String> list = new ArrayList<String>();
            if (variant.equals(VARIANT_SIMPLE) || variant.equals(VARIANT_MAIN_NAV)) {
                Iterator<Page> pages = parentPage.listChildren();
                while (pages.hasNext()) {
                    Page item = pages.next();
                    list.add(item.getPath());
                }
            }else {
                list.add(parentPage.getPath());
            }
            paths = list.toArray(new String[list.size()]);
        }
    }else {
        paths = componentProperties.get("pages", new String[0]);
    }

    String currentMenuColor = getMenuColor(_slingRequest, _currentPage);
    componentProperties.put("currentMenuColor", currentMenuColor);

    boolean isThemeExists = false;
    Node theme = getThemeNode(_slingRequest, _currentPage);
    if (theme != null) {
        String cssPath = getProperty(_slingRequest, theme, "cssPath");
        isThemeExists = cssPath != null;
    }

    List<Map> pagesInfo = variant.equals(VARIANT_SIMPLE)
            ? this.getSimpleMenuPageList(_pageManager, paths, _currentPage, _slingRequest)
            : this.getMenuPageList(_pageManager, paths, _currentPage, _slingRequest, isThemeExists);

    componentProperties.put("showLangNav",isYes(componentProperties.get("showLangNav", DEFAULT_TOGGLE_STATE)));
    componentProperties.put("showBackToTop",isYes(componentProperties.get("showBackToTop", DEFAULT_TOGGLE_STATE)));
    componentProperties.put("showSearch",isYes(componentProperties.get("showSearch", DEFAULT_TOGGLE_STATE)));

    componentProperties.put("menuItems",pagesInfo);
    componentProperties.put("mainMenu",_i18n.get("mainMenu","sitenav"));
    componentProperties.put("subMenu",_i18n.get("subMenu","sitenav"));
    componentProperties.put("goToTopOfPage",_i18n.get("goToTopOfPage","sitenav"));

%>
<c:set var="componentProperties" value="<%= componentProperties %>" />
<c:choose>
    <c:when test="${componentProperties.variant == 'mainnav'}">
        <%@ include file="style.desktop.jsp" %>
        <%@ include file="style.mobile.jsp" %>
    </c:when>
    <c:when test="${componentProperties.variant == 'simple'}">
        <%@ include file="style.simple.jsp" %>
    </c:when>
    <c:otherwise>
        <%@ include file="style.default.jsp" %>
    </c:otherwise>
</c:choose>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
