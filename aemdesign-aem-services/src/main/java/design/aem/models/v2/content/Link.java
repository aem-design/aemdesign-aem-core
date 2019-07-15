package design.aem.models.v2.content;

import com.day.cq.i18n.I18n;
import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.NameConstants;
import design.aem.components.ComponentProperties;
import design.aem.models.ModelProxy;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;

import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.ConstantsUtil.DEFAULT_EXTENTION;
import static design.aem.utils.components.I18nUtil.getDefaultLabelIfEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class Link extends ModelProxy {

    protected ComponentProperties componentProperties = null;
    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }

    protected void ready() {
        I18n i18n = new I18n(getRequest());

        final String FIELD_LINKURL = "linkUrl";
        final String DEFAULT_LINKURL = "#";
        final String DEFAULT_LINK_ICON_POSITION = "left";
        final String DEFAULT_I18N_CATEGORY = "link";
        final String DEFAULT_I18N_LABEL = "linklabel";

        /*
          Component Fields Helper

          Structure:
          1 required - property name,
          2 required - default value,
          3 optional - name of component attribute to add value into
          4 optional - canonical name of class for handling multivalues, String or Tag
         */
        setComponentFields(new Object[][]{
                {"linkTarget", StringUtils.EMPTY, "target"},
                {FIELD_LINKURL, StringUtils.EMPTY},
                {FIELD_VARIANT, DEFAULT_VARIANT},
                {"linkId", getResource().getPath()},
                {"linkIcon", new String[]{}, "", Tag.class.getCanonicalName()},
                {"linkIconPosition", DEFAULT_LINK_ICON_POSITION},
                {"label", getDefaultLabelIfEmpty("",DEFAULT_I18N_CATEGORY,DEFAULT_I18N_LABEL,DEFAULT_I18N_CATEGORY,i18n)},
        });

        componentProperties = ComponentsUtil.getComponentProperties(
                this,
                componentFields,
                DEFAULT_FIELDS_STYLE,
                DEFAULT_FIELDS_ACCESSIBILITY,
                DEFAULT_FIELDS_ANALYTICS,
                DEFAULT_FIELDS_ATTRIBUTES);

        String linkUrl = componentProperties.get(FIELD_LINKURL, StringUtils.EMPTY);

        if (isNotEmpty(linkUrl)) {
            Resource linkResource = getResourceResolver().resolve(linkUrl);
            if (!ResourceUtil.isNonExistingResource(linkResource) && linkResource.isResourceType(NameConstants.NT_PAGE)) {
                if (!linkUrl.endsWith(DEFAULT_EXTENTION) && !linkUrl.contains(DEFAULT_LINKURL)) {
                    linkUrl = linkUrl.concat(DEFAULT_EXTENTION);
                }
            }

            componentProperties.attr.add("href", linkUrl);

            componentProperties.put(COMPONENT_ATTRIBUTES, buildAttributesString(componentProperties.attr.getData(), null));
        }
    }
}