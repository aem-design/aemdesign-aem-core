package design.aem.utils.components;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import design.aem.components.ComponentProperties;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static design.aem.utils.components.CommonUtil.*;
import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.ConstantsUtil.*;
import static design.aem.utils.components.ImagesUtil.*;
import static design.aem.utils.components.TagUtil.getPageTags;
import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class ComponentDetailsUtil {

    public static final Logger LOGGER = LoggerFactory.getLogger(ComponentDetailsUtil.class);


    public static List<ComponentProperties> getPageListInfo(PageContext pageContext, PageManager pageManager, ResourceResolver resourceResolver, String[] paths) {
        return getPageListInfo(pageContext, pageManager, resourceResolver, paths, DEFAULT_LIST_DETAILS_SUFFIX, DEFAULT_LIST_PAGE_CONTENT);
    }

    public static List<ComponentProperties> getPageListInfo(PageContext pageContext, PageManager pageManager, ResourceResolver resourceResolver, String[] paths, String[] componentNames, String[] pageRoots) {
        return getPageListInfo(pageContext, pageManager, resourceResolver, paths, componentNames, pageRoots, null, false);
    }

    public static List<ComponentProperties> getPageListInfo(PageContext pageContext, PageManager pageManager, ResourceResolver resourceResolver, String[] paths, String[] componentNames, String[] pageRoots, Integer collectChildrenFromRoot, Boolean ignoreHidden) {
        List<ComponentProperties> pages = new ArrayList<ComponentProperties>();

        for (String path : paths) {
            Page child = pageManager.getPage(path);

            if (child != null) {
                if (ignoreHidden && child.isHideInNav()) {
                    continue;
                }

                pages.add(getPageInfo(pageContext, child, resourceResolver, componentNames, pageRoots, collectChildrenFromRoot));
            }
        }

        return pages;
    }

    public static List<ComponentProperties> getPageListInfo(PageContext pageContext, PageManager pageManager, ResourceResolver resourceResolver, Iterator<Page> pageList) {
        return getPageListInfo(pageContext, pageManager, resourceResolver, pageList, DEFAULT_LIST_DETAILS_SUFFIX, DEFAULT_LIST_PAGE_CONTENT);
    }

    public static List<ComponentProperties> getPageListInfo(PageContext pageContext, PageManager pageManager, ResourceResolver resourceResolver, Iterator<Page> pageList, String[] detailsComponentName, String[] pageRoots) {
        return getPageListInfo(pageContext, pageManager, resourceResolver, pageList, detailsComponentName, pageRoots, null, false);
    }

    public static List<ComponentProperties> getPageListInfo(PageContext pageContext, PageManager pageManager, ResourceResolver resourceResolver, Iterator<Page> pageList, String[] detailsComponentName, String[] pageRoots, Integer collectChildrenFromRoot, Boolean ignoreHidden) {
        List<ComponentProperties> pages = new ArrayList<ComponentProperties>();

        if (pageList != null) {
            while (pageList.hasNext()) {
                Page child = pageList.next();

                if (ignoreHidden && child.isHideInNav()) {
                    continue;
                }

                pages.add(getPageInfo(pageContext, child, resourceResolver, detailsComponentName, pageRoots, collectChildrenFromRoot));
            }
        }

        return pages;
    }

    /***
     * return pge info without children
     * @param pageContext
     * @param page
     * @param resourceResolver
     * @param componentNames
     * @param pageRoots
     * @return
     */
    public static ComponentProperties getPageInfo(PageContext pageContext, Page page, ResourceResolver resourceResolver, String[] componentNames, String[] pageRoots) {
        return getPageInfo(pageContext,page,resourceResolver,componentNames,pageRoots,null);

    }

    /***
     * Get page info from list of page paths
     *
     * @param page page to get info from
     * @param pageContext
     * @param page
     * @param resourceResolver
     * @param componentNames
     * @param pageRoots
     * @param collectChildrenFromRoot how many levels down to collect children
     * @return
     */
    public static ComponentProperties getPageInfo(PageContext pageContext, Page page, ResourceResolver resourceResolver, String[] componentNames, String[] pageRoots, Integer collectChildrenFromRoot) {
        ComponentProperties componentProperties = getNewComponentProperties(pageContext);

        if (page!=null) {

            String detailsNodePath = findComponentInPage(page, componentNames, pageRoots);

            if (isNotEmpty(detailsNodePath)) {

                Resource detailsNodeResource = resourceResolver.resolve(detailsNodePath);

                if (!ResourceUtil.isNonExistingResource(detailsNodeResource)) {

                    componentProperties = getComponentProperties(
                            pageContext,
                            detailsNodeResource,
                            DEFAULT_FIELDS_DETAILS_OPTIONS
                    );

                    componentProperties.put("detailsPath",detailsNodeResource.getPath());


                    componentProperties.putAll(getAssetInfo(resourceResolver,
                            getResourceImagePath(detailsNodeResource,DEFAULT_SECONDARY_IMAGE_NODE_NAME),
                            FIELD_PAGE_SECONDARY_IMAGE));

                    componentProperties.putAll(getAssetInfo(resourceResolver,
                            getResourceImagePath(detailsNodeResource,DEFAULT_BACKGROUND_IMAGE_NODE_NAME),
                            FIELD_PAGE_BACKGROUND_IMAGE));
                }

            }

            componentProperties.put("title", page.getTitle());
            componentProperties.put("pageTags", getPageTags(page));
            componentProperties.put("description", page.getDescription());
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

            String contentNode = getComponentNodePath(page, pageRoots);

            if (isNotEmpty(contentNode)) {
                componentProperties.put("pageContent",contentNode);
            }

            //check if current page is in request page hierarchy
            Page selectedPage = (com.day.cq.wcm.api.Page) pageContext.getAttribute("currentPage");
            if (selectedPage != null ) {
                String selectedPagePath = selectedPage.getPath();
                String thisPagePath = page.getPath();

                boolean current = false;
                if (thisPagePath != null && selectedPagePath.equals(thisPagePath)) {
                    current = true;
                }

                componentProperties.put("current", current);
            }

            //get children
            if (collectChildrenFromRoot != null && collectChildrenFromRoot > 0 ) {
                //keep going
                List<Map> childrenList = new ArrayList<Map>();

                SlingHttpServletRequest req = (SlingHttpServletRequest) pageContext.getAttribute("slingRequest");


                if (req != null) {
                    Iterator<Page> children = page.listChildren(new com.day.cq.wcm.api.PageFilter(req));

                    if (children != null) {

                        componentProperties.put("hasChildren", children.hasNext());

                        while (children.hasNext()) {
                            Page nextchild = children.next();

                            childrenList.add(getPageInfo(pageContext, nextchild, resourceResolver, componentNames, pageRoots, collectChildrenFromRoot-1));
                        }

                        componentProperties.put("children", childrenList);

                    }
                }

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
    public static ComponentProperties processBadgeRequestConfig(ComponentProperties componentProperties, ResourceResolver resourceResolver, HttpServletRequest request) {

        ComponentProperties badgeConfig = (ComponentProperties)request.getAttribute(BADGE_REQUEST_ATTRIBUTES);

        //quick fail
        if (badgeConfig == null || resourceResolver == null || request == null || componentProperties == null) {
            return new ComponentProperties();
        }

        try {


            int thumbnailWidth = componentProperties.get(DETAILS_THUMBNAIL_WIDTH, DEFAULT_THUMB_WIDTH_SM);


            String badgeThumbnailType = componentProperties.get(DETAILS_THUMBNAIL_TYPE, IMAGE_OPTION_RENDITION);

            //page and page details properties
            String pageImage = componentProperties.get(FIELD_PAGE_IMAGE, ""); //page image
            String pageImage_Thumbnail = componentProperties.get(FIELD_PAGE_IMAGE_THUMBNAIL, ""); //page image - thumbnail

            String pageSecondaryImage = componentProperties.get(FIELD_PAGE_SECONDARY_IMAGE, ""); //page details - secondary
            String pageSecondaryImage_Thumbnail = componentProperties.get(FIELD_PAGE_SECONDARY_IMAGE_THUMBNAIL, ""); //page details - secondary image - thumbnail

            String pageThumbnailImage = componentProperties.get(FIELD_PAGE_THUMBNAIL_IMAGE, ""); //page details - thumbnail
            String pageThumbnailImage_Thumbnail = componentProperties.get(FIELD_PAGE_THUMBNAIL_IMAGE_THUMBNAIL, ""); //page details - thumbnail image - thumbnail

            String badgeThumbnail = badgeConfig.get(DETAILS_THUMBNAIL, ""); //list badge thumbnail
            String badgeThumbnail_Thumbnail = "";

            int badgeThumbnailWidth = badgeConfig.get(DETAILS_THUMBNAIL_WIDTH, thumbnailWidth);

            //set default straight away.
            badgeConfig.put(FIELD_PAGE_THUMBNAIL, DEFAULT_IMAGE_BLANK);

            //get rendition for badge thumbnail
            if (isNotEmpty(badgeThumbnail)) {
                Resource badgeThumbnailResource = resourceResolver.resolve(badgeThumbnail);

                if (!ResourceUtil.isNonExistingResource(badgeThumbnailResource)) {
                    com.adobe.granite.asset.api.Asset badgeThumbnailAsset = badgeThumbnailResource.adaptTo(com.adobe.granite.asset.api.Asset.class);

                    if (badgeThumbnailAsset != null) {
                        com.adobe.granite.asset.api.Rendition bestRendition = getBestFitRendition(badgeThumbnailWidth, badgeThumbnailAsset);

                        if (bestRendition != null) {
                            badgeThumbnail_Thumbnail = bestRendition.getPath();
                        }
                    }
                }
            }

//            String pageThumbnailType = componentProperties.get(FIELD_PAGE_IMAGE, IMAGE_OPTION_RENDITION);

            //use primary image as thumbnail
            if (isNotEmpty(pageImage_Thumbnail)) {
                badgeConfig.put(FIELD_PAGE_THUMBNAIL,
                        getBestFitRendition(
                                pageImage,
                                badgeThumbnailWidth,
                                resourceResolver
                        ));
            }

            //use secondary image as thumbnail
            if (isNotEmpty(pageSecondaryImage_Thumbnail)) {
                badgeConfig.put(FIELD_PAGE_THUMBNAIL,
                        getBestFitRendition(
                                pageSecondaryImage,
                                badgeThumbnailWidth,
                                resourceResolver
                        ));
            }

            //use thumbnail image as thumbnial
            if (isNotEmpty(pageThumbnailImage_Thumbnail)) {
                badgeConfig.put(FIELD_PAGE_THUMBNAIL,
                        getBestFitRendition(
                                pageThumbnailImage,
                                badgeThumbnailWidth,
                                resourceResolver
                        ));
            }

            //use badge override as thumbnail
            if (isNotEmpty(badgeThumbnail_Thumbnail)) {
                badgeConfig.put(FIELD_PAGE_THUMBNAIL,
                        getBestFitRendition(
                                badgeThumbnail,
                                badgeThumbnailWidth,
                                resourceResolver
                        ));
            }

        } catch (Exception ex) {
            LOGGER.error("processBadgeRequestConfig: could not process {}",ex.toString());
        }
        return badgeConfig;
    }
}
