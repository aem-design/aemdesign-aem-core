package design.aem.models.v2.layout;

import com.day.cq.wcm.api.Page;
import design.aem.components.ComponentProperties;
import design.aem.models.BaseComponent;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static design.aem.utils.components.CommonUtil.DEFAULT_LIST_DETAILS_SUFFIX;
import static design.aem.utils.components.CommonUtil.DEFAULT_LIST_PAGE_CONTENT;
import static design.aem.utils.components.ComponentDetailsUtil.getPageListInfo;
import static design.aem.utils.components.ComponentsUtil.*;
import static java.text.MessageFormat.format;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class ContentTabs extends BaseComponent {
    protected static final String DEFAULT_LISTFROM_CHILDREN = "children";
    protected static final String DEFAULT_LISTFROM_STATIC = "static";
    protected static final String FIELD_LISTFROM = "listFrom";
    protected static final String FIELD_TABPAGES = "pages";
    protected static final String FIELD_PATHTOPARENT = "parentPage";
    protected static final String FIELD_TABPOSITION = "tabPosition";

    protected void ready() {
        componentProperties = ComponentsUtil.getComponentProperties(
            this,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);

        List<ComponentProperties> tabPagesInfo = null;

        String[] supportedDetails = DEFAULT_LIST_DETAILS_SUFFIX;
        String[] supportedRoots = DEFAULT_LIST_PAGE_CONTENT;

        if (componentProperties.get(FIELD_LISTFROM, StringUtils.EMPTY).equals(DEFAULT_LISTFROM_CHILDREN)) {
            String pathToParent = componentProperties.get(FIELD_PATHTOPARENT, StringUtils.EMPTY);
            Page tabsParentPage = getCurrentPage();

            if (isNotEmpty(pathToParent)) {
                tabsParentPage = getPageManager().getPage(pathToParent);
            }

            if (tabsParentPage != null) {
                tabPagesInfo = getPageListInfo(
                    this,
                    getPageManager(),
                    getResourceResolver(),
                    tabsParentPage.listChildren(),
                    supportedDetails,
                    supportedRoots,
                    null,
                    true);
            }
        } else if (componentProperties.get(FIELD_LISTFROM, StringUtils.EMPTY).equals(DEFAULT_LISTFROM_STATIC)) {
            String[] tabPages = componentProperties.get(FIELD_TABPAGES, new String[0]);

            if (tabPages.length != 0) {
                tabPagesInfo = getPageListInfo(
                    this,
                    getPageManager(),
                    getResourceResolver(),
                    tabPages,
                    supportedDetails,
                    supportedRoots,
                    null,
                    true);
            }
        }

        componentProperties.put("tabPagesInfo", tabPagesInfo);

        if (tabPagesInfo == null || tabPagesInfo.isEmpty()) {
            String variantTemplate = format(COMPONENT_VARIANT_TEMPLATE_FORMAT, "empty");

            componentProperties.put(COMPONENT_VARIANT_TEMPLATE, variantTemplate);
        }
    }

    @Override
    protected void setFields() {
        setComponentFields(new Object[][]{
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {FIELD_LISTFROM, StringUtils.EMPTY},
            {FIELD_TABPAGES, new String[0]},
            {FIELD_PATHTOPARENT, StringUtils.EMPTY},
            {FIELD_TABPOSITION, "top"},
        });
    }
}
