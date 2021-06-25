package design.aem.models.v2.content;

import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.NameConstants;
import design.aem.models.BaseComponent;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;

import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.ConstantsUtil.DEFAULT_EXTENTION;
import static design.aem.utils.components.I18nUtil.getDefaultLabelIfEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class Link extends BaseComponent {
    protected static final String FIELD_LINK_URL = "linkUrl";
    protected static final String FIELD_LINK_TARGET = "linkTarget";
    protected static final String FIELD_LINK_ID = "linkId";
    protected static final String FIELD_LINK_ICON = "linkIcon";
    protected static final String FIELD_LINK_ICON_POSITION = "linkIconPosition";
    protected static final String FIELD_LABEL = "label";

    protected static final String DEFAULT_LINK_URL = "#";
    protected static final String DEFAULT_LINK_ICON_POSITION = "left";
    protected static final String DEFAULT_I18N_CATEGORY = "link";
    protected static final String DEFAULT_I18N_LABEL = "linklabel";

    protected void ready() {
        componentProperties = ComponentsUtil.getComponentProperties(
            this,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY,
            DEFAULT_FIELDS_ANALYTICS,
            DEFAULT_FIELDS_ATTRIBUTES);

        String linkUrl = componentProperties.get(FIELD_LINK_URL, StringUtils.EMPTY);

        if (isNotEmpty(linkUrl)) {
            Resource linkResource = getResourceResolver().resolve(linkUrl);

            if (!ResourceUtil.isNonExistingResource(linkResource)
                && linkResource.isResourceType(NameConstants.NT_PAGE)
                && !linkUrl.endsWith(DEFAULT_EXTENTION)
                && !linkUrl.contains(DEFAULT_LINK_URL)) {
                linkUrl = linkUrl.concat(DEFAULT_EXTENTION);
            }

            componentProperties.attr.add("href", linkUrl);

            componentProperties.put(COMPONENT_ATTRIBUTES,
                buildAttributesString(componentProperties.attr.getData(), null));
        }
    }

    @Override
    protected void setFields() {
        setComponentFields(new Object[][]{
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {FIELD_LINK_URL, StringUtils.EMPTY},
            {FIELD_LINK_TARGET, StringUtils.EMPTY, "target"},
            {FIELD_LINK_ID, getResource().getPath()},
            {FIELD_LINK_ICON, new String[]{}, StringUtils.EMPTY, Tag.class.getCanonicalName()},
            {FIELD_LINK_ICON_POSITION, DEFAULT_LINK_ICON_POSITION},
            {FIELD_LABEL, componentDefaults.get(FIELD_LABEL)},
        });
    }

    @Override
    protected void setFieldDefaults() {
        componentDefaults.put(FIELD_LABEL, getDefaultLabelIfEmpty(
            StringUtils.EMPTY,
            DEFAULT_I18N_CATEGORY,
            DEFAULT_I18N_LABEL,
            DEFAULT_I18N_CATEGORY,
            i18n));
    }
}
