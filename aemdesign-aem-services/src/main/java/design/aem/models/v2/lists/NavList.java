package design.aem.models.v2.lists;

import com.day.cq.wcm.api.Page;
import design.aem.components.ComponentProperties;
import design.aem.models.BaseComponent;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.vault.util.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static design.aem.utils.components.CommonUtil.DEFAULT_LIST_DETAILS_SUFFIX;
import static design.aem.utils.components.CommonUtil.DEFAULT_LIST_PAGE_CONTENT;
import static design.aem.utils.components.ComponentDetailsUtil.getPageListInfo;
import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.ConstantsUtil.INHERITED_RESOURCE;
import static design.aem.utils.components.I18nUtil.*;

public class NavList extends BaseComponent {
    protected static final Logger LOGGER = LoggerFactory.getLogger(NavList.class);

    protected static final String DEFAULT_LISTFROM = "children";
    protected static final String LISTFROM_CHILDREN = "children";
    protected static final String DEFAULT_VARIANT = "default";

    protected static final int DEFAULT_DEPTH_FROM_ROOT = 2;

    protected void ready() {
        componentProperties = ComponentsUtil.getComponentProperties(
            this,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY,
            DEFAULT_FIELDS_ANALYTICS);

        String[] supportedDetails = DEFAULT_LIST_DETAILS_SUFFIX;
        String[] supportedRoots = DEFAULT_LIST_PAGE_CONTENT;

        // Set default level to be 5
        int depthFromRoot = componentProperties.get("depthFromRoot", DEFAULT_DEPTH_FROM_ROOT);

        if (depthFromRoot > 5) {
            depthFromRoot = 5;
        } else if (depthFromRoot < 0) {
            depthFromRoot = 0;
        }

        java.util.List<ComponentProperties> pagesInfo = null;

        if (componentProperties.get("listFrom", DEFAULT_LISTFROM).equals(LISTFROM_CHILDREN)) {
            Page parentPage = getPageManager().getPage(componentProperties.get("parentPage", StringUtils.EMPTY));

            if (parentPage != null) {
                pagesInfo = getPageListInfo(
                    getContextObjects(this),
                    getPageManager(),
                    getResourceResolver(),
                    parentPage.listChildren(),
                    supportedDetails,
                    supportedRoots,
                    depthFromRoot,
                    true);
            }
        } else {
            String[] paths = componentProperties.get("pages", new String[0]);

            if (paths.length != 0) {
                pagesInfo = getPageListInfo(
                    getContextObjects(this),
                    getPageManager(),
                    getResourceResolver(),
                    paths,
                    supportedDetails,
                    supportedRoots,
                    depthFromRoot,
                    true);
            }
        }

        componentProperties.put("menuItems", pagesInfo);

        componentProperties.put(INHERITED_RESOURCE, findInheritedResource(getResourcePage(), getComponentContext()));

        componentProperties.put(DEFAULT_I18N_INHERIT_LABEL_PARENTNOTFOUND, getDefaultLabelIfEmpty(
            StringUtils.EMPTY,
            DEFAULT_I18N_INHERIT_CATEGORY,
            DEFAULT_I18N_INHERIT_LABEL_PARENTNOTFOUND,
            DEFAULT_I18N_INHERIT_CATEGORY,
            i18n));
    }

    @Override
    protected void setFields() {
        setComponentFields(new Object[][]{
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {"pages", new String[0]},
            {"depthFromRoot", DEFAULT_DEPTH_FROM_ROOT},
            {"listFrom", DEFAULT_LISTFROM},
            {"currentPage", getCurrentPage()},
            {"menuTitle", i18n.get("menuTitle", "navlist")},
            {"parentPage", getPrimaryPath(getRequest())},
            {"linkTitlePrefix", i18n.get("linkTitlePrefix", "navlist")},
            {COMPONENT_CANCEL_INHERIT_PARENT, false},
        });
    }

    /**
     * return path before /JCR:CONTENT
     * @param slingRequest sling request instance
     * @return path
     */
    private String getPrimaryPath(SlingHttpServletRequest slingRequest) {
        String requestPath = slingRequest.getRequestPathInfo().getResourcePath();

        if (requestPath.contains(JcrConstants.JCR_CONTENT)) {
            return requestPath.substring(0, requestPath.indexOf(JcrConstants.JCR_CONTENT) - 1);
        }

        return StringUtils.EMPTY;
    }
}
