package design.aem.models.v2.layout;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.i18n.I18n;
import com.day.cq.wcm.api.Page;
import design.aem.components.ComponentProperties;
import design.aem.utils.components.ComponentsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static design.aem.utils.components.CommonUtil.DEFAULT_LIST_DETAILS_SUFFIX;
import static design.aem.utils.components.CommonUtil.DEFAULT_LIST_PAGE_CONTENT;
import static design.aem.utils.components.ComponentsUtil.*;
import static java.text.MessageFormat.format;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class ContentTabs extends WCMUsePojo {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentTabs.class);

    private ComponentProperties componentProperties = null;
    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }

    @Override
    public void activate() throws Exception {

        I18n _i18n = new I18n(getRequest());

        final String DEFAULT_LISTFROM_CHILDREN = "children";
        final String DEFAULT_LISTFROM_STATIC = "static";
        final String FIELD_LISTFROM = "listFrom";
        final String FIELD_TABPAGES = "pages";
        final String FIELD_PATHTOPARENT = "parentPage";
        final String FIELD_TABPOSITION = "tabPosition";

        // {
        //   1 required - property name,
        //   2 required - default value,
        //   3 optional - name of component attribute to add value into
        //   4 optional - canonical name of class for handling multivalues, String or Tag
        // }
        Object[][] componentFields = {
                {FIELD_VARIANT, DEFAULT_VARIANT},
                {FIELD_LISTFROM, ""},
                {FIELD_TABPAGES, new String[0]},
                {FIELD_PATHTOPARENT, ""},
                {FIELD_TABPOSITION, "top"},
        };


        componentProperties = ComponentsUtil.getComponentProperties(
                this,
                componentFields,
                DEFAULT_FIELDS_STYLE,
                DEFAULT_FIELDS_ACCESSIBILITY);

        // init
        Map<String, Object> tabs = new HashMap<String, Object>();

        List<ComponentProperties> tabPagesInfo = null;

        String[] supportedDetails = DEFAULT_LIST_DETAILS_SUFFIX;
        String[] supportedRoots = DEFAULT_LIST_PAGE_CONTENT;

        if (componentProperties.get(FIELD_LISTFROM,"").equals(DEFAULT_LISTFROM_CHILDREN)) {
            String pathToParent = componentProperties.get(FIELD_PATHTOPARENT,"");
            Page tabsParentPage = getCurrentPage();
            if (isNotEmpty(pathToParent)) {
                Page foundPage = tabsParentPage = getPageManager().getPage(pathToParent);
                if (foundPage !=null ) {
                    tabsParentPage = foundPage;
                }
            }

            if (tabsParentPage != null) {
                tabPagesInfo = getPageListInfo(this, getPageManager(), getResourceResolver(), tabsParentPage.listChildren(), supportedDetails, supportedRoots, null, true);
            }
        } else if (componentProperties.get(FIELD_LISTFROM,"").equals(DEFAULT_LISTFROM_STATIC)) {
            String[] tabPages =  componentProperties.get(FIELD_TABPAGES, new String[0]);

            if (tabPages.length != 0) {
                tabPagesInfo = getPageListInfo(this, getPageManager(), getResourceResolver(), tabPages, supportedDetails, supportedRoots, null, true);
            }
        }

        componentProperties.put("tabPagesInfo",tabPagesInfo);


        if (tabPagesInfo == null || tabPagesInfo.size() == 0) {
            String variantTemplate = format(COMPONENT_VARIANT_TEMPLATE_FORMAT, "empty");
            componentProperties.put(COMPONENT_VARIANT_TEMPLATE, variantTemplate);
        }
    }




}