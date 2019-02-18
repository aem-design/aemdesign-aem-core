package design.aem.models.v2.lists;

import com.adobe.cq.sightly.WCMUsePojo;
import com.adobe.cq.wcm.core.components.internal.models.v2.PageImpl;
import com.day.cq.i18n.I18n;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import design.aem.components.ComponentProperties;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.vault.util.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static design.aem.utils.components.CommonUtil.DEFAULT_LIST_DETAILS_SUFFIX;
import static design.aem.utils.components.CommonUtil.DEFAULT_LIST_PAGE_CONTENT;
import static design.aem.utils.components.ComponentDetailsUtil.getPageListInfo;
import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.ConstantsUtil.INHERITED_RESOURCE;
import static design.aem.utils.components.I18nUtil.*;

public class NavList extends WCMUsePojo {

    private static final Logger LOGGER = LoggerFactory.getLogger(NavList.class);
    private static final String DETAILS_MENU_COLOR_DEFAULT = "default";

    private ComponentProperties componentProperties = null;
    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }

    @Override
    public void activate() throws Exception {

        I18n _i18n = new I18n(getRequest());

        final String DEFAULT_LISTFROM = "children";
        final String LISTFROM_CHILDREN = "children";
        final String DEFAULT_VARIANT = "default";
        final String DEFAULT_MENUT_TITLE = "Menu";

        // getDefaultLabelIfEmpty

        //not using lamda is available so this is the best that can be done
        Object[][] componentFields = {
                {"pages", new String[0]},
                {FIELD_VARIANT, DEFAULT_VARIANT},
                {"listFrom", DEFAULT_LISTFROM},
                {"currentPage", getCurrentPage()},
                {"menuTitle", _i18n.get("menuTitle","navlist")},
                {"parentPage", getPrimaryPath(getRequest())},
                {"linkTitlePrefix", _i18n.get("linkTitlePrefix","navlist")},
                {COMPONENT_CANCEL_INHERIT_PARENT, false},
        };
        componentProperties = ComponentsUtil.getComponentProperties(
                this,
                componentFields,
                DEFAULT_FIELDS_STYLE,
                DEFAULT_FIELDS_ACCESSIBILITY);

        String[] supportedDetails = DEFAULT_LIST_DETAILS_SUFFIX;
        String[] supportedRoots = DEFAULT_LIST_PAGE_CONTENT;

        java.util.List<ComponentProperties> pagesInfo = null;
        if (componentProperties.get("listFrom", DEFAULT_LISTFROM).equals(LISTFROM_CHILDREN)) {
            Page parentPage = getPageManager().getPage(componentProperties.get("parentPage", ""));
            if (parentPage != null) {
                pagesInfo = getPageListInfo(getContextObjects(this),getPageManager(), getResourceResolver(), parentPage.listChildren(), supportedDetails, supportedRoots, 2, true);
            }
        } else {
            String[] paths = componentProperties.get("pages", new String[0]);
            if (paths.length != 0) {
                pagesInfo = getPageListInfo(getContextObjects(this),getPageManager(), getResourceResolver(), paths, supportedDetails, supportedRoots, 2, true);
            }
        }

        componentProperties.put("menuItems",pagesInfo);
//    componentProperties.put("mainMenu",_i18n.get("navList","navList"));
//    componentProperties.put("subMenu",_i18n.get("subMenu","navlist"));
//    componentProperties.put("goToTopOfPage",_i18n.get("goToTopOfPage","navlist"));

        componentProperties.put(INHERITED_RESOURCE,findInheritedResource(getResourcePage(),getComponentContext()));
        componentProperties.put(DEFAULT_I18N_INHERIT_LABEL_PARENTNOTFOUND,getDefaultLabelIfEmpty("",DEFAULT_I18N_INHERIT_CATEGORY,DEFAULT_I18N_INHERIT_LABEL_PARENTNOTFOUND,DEFAULT_I18N_INHERIT_CATEGORY,_i18n));
    }



//
//    /**
//     * Get a list of page {title, href ...} info
//     *
//     * @param pageManager resolver to get the pages at the paths
//     * @param paths string array of paths
//     * @return
//     */
//    protected List<Map> getMenuPageList(PageContext pageContext, PageManager pageManager, ResourceResolver resourceResolver, String[] paths, Page currentPage, SlingHttpServletRequest req, boolean isThemeExists) throws RepositoryException {
//        List<Map> pages = new ArrayList<Map>();
//
//
//        for (String path : paths) {
//            //Page child = resolver.getResource(path).adaptTo(Page.class);
//            Page child = pageManager.getPage(path);
//
//            if (child != null) {
//                Map infoStruct = new HashMap();
//
//                //grab page info
////                Resource contentRes = child.getContentResource();
////                Node contentNode = contentRes.adaptTo(Node.class);
//
//                infoStruct.putAll(getPageLinkInfo(child));
//
//                Resource parSys = child.getContentResource("par");
//                Boolean hasChildren = false;
//                if (parSys != null) {
//                    Node parSysNode = parSys.adaptTo(Node.class);
//                    if (parSysNode.hasNodes()) {
//                        hasChildren = true;
//                        infoStruct.put("parsysPath", parSys.getPath());
//                    } else {
//                        infoStruct.put("parsysPath", "");
//                    }
//                } else {
//                    infoStruct.put("parsysPath", "");
//                }
//
//                infoStruct.putAll(getPageInfo(pageContext, child,resourceResolver,null,null));
//
//                //grab page details info
////                infoStruct.putAll(getPageDetailsInfo(child));
//
//
//                //grab children for current 2nd level of current path
//                String currentPath = currentPage != null ? currentPage.getPath() : "";
//
//                infoStruct.put("currentPath", currentPath);
//
//
//                if (StringUtils.isNotEmpty(currentPath)) {
//
//                    //if menu item is of same level as current path then grab its children
//                    String section = Text.getAbsoluteParent(currentPath, 3);
//
//                    infoStruct.put("current", section.equals(child.getPath()));
//                    if (!isThemeExists) {
//                        infoStruct.put("menuColor", getMenuColor(req, child));
//                    }
//
//                    List<Map> children = getChildren(child, currentPath, 3, req, isThemeExists);
//                    infoStruct.put("children", children);
//
//                    hasChildren = children.size()!=0;
//
//                }
//
//
//                String noContentClass = (hasChildren ? "" : "no-content");
//
//                infoStruct.put("cssClass", StringUtils.join("page-", child.getName().trim(), noContentClass, " "));
//
//                pages.add(infoStruct);
//            }
//        }
//
//        return pages;
//    }
//
//    /**
//     * recursevley build a tree for page up defined by levels
//     * @param page starting page
//     * @param currentPath curret path used to determined active pages
//     * @param level level to gather
//     * @param req
//     * @return
//     */
//
//    private List<Map> getChildren(Page page, String currentPath, int level, SlingHttpServletRequest req, boolean isThemeExists) {
//        List<Map> childrenList = new ArrayList<Map>();
//
//        if (page != null && level-- > 0) {
//            Iterator<Page> children = page.listChildren(new PageFilter(req));
//
//            while (children.hasNext()) {
//                Page nextchild = children.next();
//                //skip if hidden
//                if (!nextchild.isHideInNav()) {
//                    String childPath = nextchild.getPath();
//
//                    Map info = getPageLinkInfo(nextchild);
//
//                    boolean current = false;
//                    if (currentPath.equals(childPath)) {
//                        current = true;
//                    } else if (currentPath.startsWith(childPath+"/")) {
//                        current = true;
//                    } else if (currentPath.indexOf(childPath+"/") > 0) {
//                        current = true;
//                    }
//
//                    info.put("current", current);
//                    if (!isThemeExists) {
//                        info.put("menuColor", getMenuColor(req, nextchild));
//                    }
//
//                    if (level > 0) {
//                        //keep going
//                        info.put("children", getChildren(nextchild, currentPath, level, req, isThemeExists));
//                    }
//
//                    childrenList.add(info);
//                }
//            }
//        }
//
//        return childrenList;
//    }
//
//    /***
//     * get information about the page
//     * @param page
//     * @return
//     */
//
//    private Map getPageLinkInfo(Page page) {
//        Map info = new HashMap();
//
//        if (page != null) {
//            ValueMap childVM = page.getProperties();
//
//            info.put("title", getPageNavTitle(page));
//
//            String vanityPath = StringUtils.defaultIfEmpty(page.getVanityUrl(), "");
//
//            info.put("vanityPath", vanityPath);
//
//            String redirectTarget = page.getPath();
//
//            if (childVM.containsKey("redirectTarget")) {
//                redirectTarget = childVM.get("redirectTarget", "");
//            }
//
//            //respect redirect targets
//            if (redirectTarget.contains("http:")) {
//                info.put("href", redirectTarget);
//            } else {
//                info.put("href", redirectTarget.concat(DEFAULT_EXTENTION));
////                //respect vanity URLs
////                if (isNotEmpty(vanityPath)){
////                    info.put("href", vanityPath);
////                } else {
////                    info.put("href", redirectTarget.concat(DEFAULT_EXTENTION));
////                }
//            }
//
//            info.put("authHref", page.getPath().concat(DEFAULT_EXTENTION));
//            info.put("pathHref", page.getPath());
//
//        }
//
//        return info;
//
//    }


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
//
//    private String getMenuColor(SlingHttpServletRequest request, Page page) {
//        if (page == null) {
//            return null;
//        }
//        String menuColor = DETAILS_MENU_COLOR_DEFAULT;
//        try {
//            Node detailNode = findDetailNode(page);
//
//            if (detailNode != null) {
//                if (detailNode.hasProperty(DETAILS_MENU_COLOR)) {
//                    menuColor = detailNode.getProperty(DETAILS_MENU_COLOR).getString();
//                }
//            }
//        } catch (Exception ex) {
//
//        }
//
//        return "default".equalsIgnoreCase(menuColor) ? "" : menuColor;
//    }



}