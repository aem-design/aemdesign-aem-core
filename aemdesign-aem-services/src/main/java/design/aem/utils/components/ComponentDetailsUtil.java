package design.aem.utils.components;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.tagging.TagConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import design.aem.components.ComponentProperties;
import design.aem.models.GenericModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import java.util.*;

import static design.aem.utils.components.CommonUtil.*;
import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.ConstantsUtil.*;
import static design.aem.utils.components.ImagesUtil.*;
import static design.aem.utils.components.TagUtil.getPageTags;
import static design.aem.utils.components.TagUtil.getTagsAsValuesAsAdmin;
import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class ComponentDetailsUtil {

    public static final Logger LOGGER = LoggerFactory.getLogger(ComponentDetailsUtil.class);

    //A1 -> A2 WCMUsePojo
    @SuppressWarnings("Duplicates")
    public static List<ComponentProperties> getPageListInfo(WCMUsePojo wcmUsePojoModel, PageManager pageManager, ResourceResolver resourceResolver, String[] paths) {

        try {

            return getPageListInfo(wcmUsePojoModel, pageManager, resourceResolver, paths, DEFAULT_LIST_DETAILS_SUFFIX, DEFAULT_LIST_PAGE_CONTENT);

        } catch (Exception ex) {
            LOGGER.error("getPageListInfo(WCMUsePojo) /A1 -> A2 could not read required objects: {}, error: {}", wcmUsePojoModel, ex);
        }

        return new ArrayList<>();
    }

    //A1 -> A2 PageContext
    @SuppressWarnings("Duplicates")
    public static List<ComponentProperties> getPageListInfo(PageContext pageContext, PageManager pageManager, ResourceResolver resourceResolver, String[] paths) {

        try {

            return getPageListInfo(pageContext, pageManager, resourceResolver, paths, DEFAULT_LIST_DETAILS_SUFFIX, DEFAULT_LIST_PAGE_CONTENT);

        } catch (Exception ex) {
            LOGGER.error("getPageListInfo(PageContext) A1 -> A2 PageContext could not read required objects {}", ex);
        }

        return new ArrayList<>();

    }

    //A2 -> A3 WCMUsePojo
    @SuppressWarnings("Duplicates")
    public static List<ComponentProperties> getPageListInfo(WCMUsePojo wcmUsePojoModel, PageManager pageManager, ResourceResolver resourceResolver, String[] paths, String[] componentNames, String[] pageRoots) {

        try {

            return getPageListInfo(wcmUsePojoModel, pageManager, resourceResolver, paths, componentNames, pageRoots, null, false);

        } catch (Exception ex) {
            LOGGER.error("getPageListInfo(WCMUsePojo) A2 -> A3 could not read required objects: {}, error: {}", wcmUsePojoModel, ex);
        }

        return new ArrayList<>();
    }

    //A2 -> A3 PageContext
    @SuppressWarnings("Duplicates")
    public static List<ComponentProperties> getPageListInfo(PageContext pageContext, PageManager pageManager, ResourceResolver resourceResolver, String[] paths, String[] componentNames, String[] pageRoots) {

        try {

            return getPageListInfo(pageContext, pageManager, resourceResolver, paths, componentNames, pageRoots, null, false);

        } catch (Exception ex) {
            LOGGER.error("getPageListInfo(PageContext) A2 -> A3 PageContext could not read required objects {}", ex);
        }

        return new ArrayList<>();

    }

    //A3 WCMUsePojo
    @SuppressWarnings("Duplicates")
    public static List<ComponentProperties> getPageListInfo(WCMUsePojo wcmUsePojoModel, PageManager pageManager, ResourceResolver resourceResolver, String[] paths, String[] componentNames, String[] pageRoots, Integer collectChildrenFromRoot, Boolean ignoreHidden) {

        try {

            return getPageListInfo(getContextObjects(wcmUsePojoModel), pageManager, resourceResolver, paths, componentNames, pageRoots, null, false);

        } catch (Exception ex) {
            LOGGER.error("getPageListInfo(WCMUsePojo) A3 could not read required objects: {},error={},pageManager={},resourceResolver={},pageList={},detailsComponentName={},pageRoots={}",wcmUsePojoModel, ex, pageManager, resourceResolver, paths, componentNames, pageRoots);
        }

        return new ArrayList<>();
    }

    //A3 PageContext
    @SuppressWarnings("Duplicates")
    public static List<ComponentProperties> getPageListInfo(PageContext pageContext, PageManager pageManager, ResourceResolver resourceResolver, String[] paths, String[] componentNames, String[] pageRoots, Integer collectChildrenFromRoot, Boolean ignoreHidden) {

        try {

            return getPageListInfo(getContextObjects(pageContext), pageManager, resourceResolver, paths, componentNames, pageRoots, null, false);

        } catch (Exception ex) {
            LOGGER.error("getPageListInfo(PageContext) A3 PageContext could not read required objects {}", ex);
        }

        return new ArrayList<>();

    }
    
    //A3 MAP
    public static List<ComponentProperties> getPageListInfo(Map<String, Object> pageContext, PageManager pageManager, ResourceResolver resourceResolver, String[] paths, String[] componentNames, String[] pageRoots, Integer collectChildrenFromRoot, Boolean ignoreHidden) {
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


    //B1 -> B2 WCMUsePojo
    @SuppressWarnings("Duplicates")
    public static List<ComponentProperties> getPageListInfo(WCMUsePojo wcmUsePojoModel, PageManager pageManager, ResourceResolver resourceResolver, Iterator<Page> pageList) {

        try {

            return getPageListInfo(wcmUsePojoModel, pageManager, resourceResolver, pageList, DEFAULT_LIST_DETAILS_SUFFIX, DEFAULT_LIST_PAGE_CONTENT);

        } catch (Exception ex) {
            LOGGER.error("getPageListInfo(WCMUsePojo) B1 -> B2 could not read required objects: {}, error: {}", wcmUsePojoModel, ex);
        }

        return new ArrayList<>();
    }

    //B1 -> B2 PageContext
    @SuppressWarnings("Duplicates")
    public static List<ComponentProperties> getPageListInfo(PageContext pageContext, PageManager pageManager, ResourceResolver resourceResolver, Iterator<Page> pageList) {

        try {

            return getPageListInfo(pageContext, pageManager, resourceResolver, pageList, DEFAULT_LIST_DETAILS_SUFFIX, DEFAULT_LIST_PAGE_CONTENT);

        } catch (Exception ex) {
            LOGGER.error("getPageListInfo(PageContext) B1 -> B2 PageContext could not read required objects {}", ex);
        }

        return new ArrayList<>();

    }





    //B2 -> B3 WCMUsePojo
    @SuppressWarnings("Duplicates")
    public static List<ComponentProperties> getPageListInfo(WCMUsePojo wcmUsePojoModel, PageManager pageManager, ResourceResolver resourceResolver, Iterator<Page> pageList, String[] detailsComponentName, String[] pageRoots) {

        try {

            return getPageListInfo(wcmUsePojoModel, pageManager, resourceResolver, pageList, detailsComponentName, pageRoots, null, false);

        } catch (Exception ex) {
            LOGGER.error("getPageListInfo(WCMUsePojo) B2 -> B3 could not read required objects: {}, error: {}", wcmUsePojoModel, ex);
        }

        return new ArrayList<>();
    }

    //B2 -> B3 PageContext
    @SuppressWarnings("Duplicates")
    public static List<ComponentProperties> getPageListInfo(PageContext pageContext, PageManager pageManager, ResourceResolver resourceResolver, Iterator<Page> pageList, String[] detailsComponentName, String[] pageRoots) {

        try {

            return getPageListInfo(pageContext, pageManager, resourceResolver, pageList, detailsComponentName, pageRoots, null, false);

        } catch (Exception ex) {
            LOGGER.error("getPageListInfo(PageContext) B2 -> B3 could not read required objects", ex);
        }

        return new ArrayList<>();

    }


    //B3 WCMUsePojo
    @SuppressWarnings("Duplicates")
    public static List<ComponentProperties> getPageListInfo(WCMUsePojo wcmUsePojoModel, PageManager pageManager, ResourceResolver resourceResolver, Iterator<Page> pageList, String[] detailsComponentName, String[] pageRoots, Integer collectChildrenFromRoot, Boolean ignoreHidden) {

        try {

            return getPageListInfo(getContextObjects(wcmUsePojoModel), pageManager, resourceResolver, pageList, detailsComponentName, pageRoots, null, false);

        } catch (Exception ex) {
            LOGGER.error("getPageListInfo(WCMUsePojo) B3 could not read required objects: {},error={},pageManager={},resourceResolver={},pageList={},detailsComponentName={},pageRoots={}",wcmUsePojoModel, ex, pageManager, resourceResolver, pageList, detailsComponentName, pageRoots);
        }

        return new ArrayList<>();
    }

    //B3 PageContext
    @SuppressWarnings("Duplicates")
    public static List<ComponentProperties> getPageListInfo(PageContext pageContext, PageManager pageManager, ResourceResolver resourceResolver, Iterator<Page> pageList, String[] detailsComponentName, String[] pageRoots, Integer collectChildrenFromRoot, Boolean ignoreHidden) {

        try {

            return getPageListInfo(getContextObjects(pageContext), pageManager, resourceResolver, pageList, detailsComponentName, pageRoots, null, false);

        } catch (Exception ex) {
            LOGGER.error("getPageListInfo(PageContext) B3 could not read required objects", ex);
        }

        return new ArrayList<>();

    }

    //B3 MAP
    public static List<ComponentProperties> getPageListInfo(Map<String, Object> pageContext, PageManager pageManager, ResourceResolver resourceResolver, Iterator<Page> pageList, String[] detailsComponentName, String[] pageRoots, Integer collectChildrenFromRoot, Boolean ignoreHidden) {
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
     * check if page is under same path as current page.
     * @param page page to check
     * @param currentPage current page to yse
     * @param resourceResolver resource resolved
     * @return is page matching or under current page
     */
    public static boolean checkSelected(Page page, Page currentPage, ResourceResolver resourceResolver) {
        return currentPage.equals(page) ||
                currentPage.getPath().startsWith(page.getPath() + "/") ||
                currentPageIsRedirectTarget(page, currentPage, resourceResolver);
    }

    /***
     * check if page has redirect target set.
     * @param page page to check
     * @param currentPage current page
     * @param resourceResolver resource resolver
     * @return is page being redirected
     */
    public static boolean currentPageIsRedirectTarget(Page page, Page currentPage, ResourceResolver resourceResolver) {
        boolean currentPageIsRedirectTarget = false;
        Resource contentResource = page.getContentResource();
        if (contentResource != null) {
            ValueMap valueMap = contentResource.getValueMap();
            String redirectTarget = valueMap.get(PN_REDIRECT_TARGET, String.class);
            if(StringUtils.isNotBlank(redirectTarget)) {
                PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
                if (pageManager != null) {
                    Page redirectPage = pageManager.getPage(redirectTarget);
                    if (currentPage.equals(redirectPage)) {
                        currentPageIsRedirectTarget = true;
                    }
                }
            }
        }
        return currentPageIsRedirectTarget;
    }


    /***
     * Get page info from list of page paths.
     *
     * @param page page to get info from.
     * @param pageContext page content
     * @param page page to use
     * @param resourceResolver resource resolver
     * @param componentNames component names to look for
     * @param pageRoots parent to search
     * @param collectChildrenFromRoot how many levels down to collect children
     * @return map of attributes
     */
    public static ComponentProperties getPageInfo(Map<String, Object> pageContext, Page page, ResourceResolver resourceResolver, String[] componentNames, String[] pageRoots, Integer collectChildrenFromRoot) {
        return getPageInfo(pageContext,page,resourceResolver,componentNames,pageRoots,collectChildrenFromRoot,1);
    }

    /***
     * Get page info from list of page paths.
     *
     * @param page page to get info from.
     * @param pageContext page content
     * @param page page to use
     * @param resourceResolver resource resolver
     * @param componentNames component names to look for
     * @param pageRoots parent to search
     * @param collectChildrenFromRoot how many levels down to collect children
     * @param depth current depth of children
     * @return map of attributes
     */
    public static ComponentProperties getPageInfo(Map<String, Object> pageContext, Page page, ResourceResolver resourceResolver, String[] componentNames, String[] pageRoots, Integer collectChildrenFromRoot, Integer depth) {
        ComponentProperties componentProperties = getNewComponentProperties(pageContext);

        try {
            if (page != null) {

                String detailsNodePath = findComponentInPage(page, componentNames, pageRoots);

                if (isNotEmpty(detailsNodePath)) {

                    Resource detailsNodeResource = resourceResolver.resolve(detailsNodePath);

                    if (!ResourceUtil.isNonExistingResource(detailsNodeResource)) {

                        Object source = pageContext.get(PAGECONTEXTMAP_SOURCE);
                        String sourcetype = pageContext.get(PAGECONTEXTMAP_SOURCE_TYPE).toString();

                        Map<String, Object> pageContextMap = new HashMap<>();

                        switch (sourcetype) {
                            case PAGECONTEXTMAP_SOURCE_TYPE_PAGECONTEXT:
                                pageContextMap = getContextObjects((PageContext) source);
                                break;
                            case PAGECONTEXTMAP_SOURCE_TYPE_WCMUSEPOJO:
                                pageContextMap = getContextObjects((WCMUsePojo) source);
                                break;
                            case PAGECONTEXTMAP_SOURCE_TYPE_SLINGMODEL:
                                pageContextMap = ((GenericModel) source).getPageContextMap();
                                break;
                            default:
                                LOGGER.error("getPageInfo: invalid pageContext", pageContext);
                                return componentProperties;
                        }

                        Object[][] componentFields = {
                                {TagConstants.PN_TAGS, new String[]{}},
                                {FIELD_PAGE_TITLE, getPageTitle(page, detailsNodeResource)},
                                {FIELD_PAGE_TITLE_NAV, getPageNavTitle(page)},

                        };

                        componentProperties = getComponentProperties(
                                pageContextMap,
                                detailsNodeResource,
                                false,
                                componentFields,
                                DEFAULT_FIELDS_DETAILS_OPTIONS
                        );

                        componentProperties.put("detailsPath", detailsNodeResource.getPath());


                        componentProperties.putAll(getAssetInfo(resourceResolver,
                                getResourceImagePath(detailsNodeResource, DEFAULT_SECONDARY_IMAGE_NODE_NAME),
                                FIELD_PAGE_SECONDARY_IMAGE));

                        componentProperties.putAll(getAssetInfo(resourceResolver,
                                getResourceImagePath(detailsNodeResource, DEFAULT_BACKGROUND_IMAGE_NODE_NAME),
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
                componentProperties.put("depth", depth);
                componentProperties.put("vanityPath", defaultIfEmpty(page.getVanityUrl(), ""));
                componentProperties.putAll(getAssetInfo(resourceResolver,
                        getPageImgReferencePath(page),
                        FIELD_PAGE_IMAGE));

                String[] tags = componentProperties.get(TagConstants.PN_TAGS, new String[]{});
                componentProperties.put("category", getTagsAsValuesAsAdmin((SlingScriptHelper) pageContext.get(PAGECONTEXTMAP_OBJECT_SLING), ",", tags));

                String contentNode = getComponentNodePath(page, pageRoots);

                if (isNotEmpty(contentNode)) {
                    componentProperties.put("pageContent", contentNode);
                }

                //check if current page is in request page hierarchy
                Page selectedPage = (com.day.cq.wcm.api.Page) pageContext.get(PAGECONTEXTMAP_OBJECT_CURRENTPAGE);
                if (selectedPage != null) {
                    componentProperties.put("current", checkSelected(page, selectedPage, (ResourceResolver) pageContext.get(PAGECONTEXTMAP_OBJECT_RESOURCERESOLVER)));
                }


                //get children
                if (collectChildrenFromRoot != null && collectChildrenFromRoot > 0) {
                    //keep going
                    List<Map> childrenList = new ArrayList<Map>();

                    SlingHttpServletRequest req = (SlingHttpServletRequest) pageContext.get(PAGECONTEXTMAP_OBJECT_SLINGREQUEST);

                    if (req != null) {
                        Iterator<Page> children = page.listChildren(new com.day.cq.wcm.api.PageFilter(req));

                        if (children != null) {

                            componentProperties.put("hasChildren", children.hasNext());

                            while (children.hasNext()) {
                                Page nextchild = children.next();

                                childrenList.add(getPageInfo(pageContext, nextchild, resourceResolver, componentNames, pageRoots, collectChildrenFromRoot - 1,depth + 1));
                            }

                            componentProperties.put("children", childrenList);

                        }
                    }

                }

            }
        } catch (Exception ex) {
            LOGGER.error("getPageInfo: error={}",ex);
        }
        return componentProperties;
    }



    /***
     * get request fields passed by list and translate to.
     * @param componentProperties current component properties
     * @param resourceResolver resource resolver
     * @param request http request
     * @return map of attributes
     */
    public static ComponentProperties processBadgeRequestConfig(ComponentProperties componentProperties, ResourceResolver resourceResolver, HttpServletRequest request) {

        ComponentProperties badgeConfig = (ComponentProperties)request.getAttribute(BADGE_REQUEST_ATTRIBUTES);

        //quick fail
        if (badgeConfig == null || resourceResolver == null || request == null || componentProperties == null) {
            badgeConfig = new ComponentProperties();
            badgeConfig.put(COMPONENT_BADGE_CONFIG_SET,false);
            return badgeConfig;
        }

        try {

            badgeConfig.put(COMPONENT_BADGE_CONFIG_SET,true);

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
            LOGGER.error("processBadgeRequestConfig: could not process {}",ex);
        }
        return badgeConfig;
    }
}
