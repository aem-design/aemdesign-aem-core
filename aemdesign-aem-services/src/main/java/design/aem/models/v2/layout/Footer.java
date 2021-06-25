package design.aem.models.v2.layout;

import design.aem.models.BaseComponent;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.StringUtils;

import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.ConstantsUtil.INHERITED_RESOURCE;
import static design.aem.utils.components.I18nUtil.*;
import static design.aem.utils.components.ImagesUtil.DEFAULT_BACKGROUND_IMAGE_NODE_NAME;
import static design.aem.utils.components.ImagesUtil.getBackgroundImageRenditions;

public class Footer extends BaseComponent {
    protected static final String DEFAULT_ARIA_ROLE = "contentinfo";

    @SuppressWarnings("Duplicates")
    protected void ready() {
        componentProperties = ComponentsUtil.getComponentProperties(
            this,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);

        componentProperties.put(INHERITED_RESOURCE,
            findInheritedResource(getResourcePage(), getComponentContext()));

        componentProperties.put(DEFAULT_I18N_INHERIT_LABEL_PARENTNOTFOUND, getDefaultLabelIfEmpty(
            StringUtils.EMPTY,
            DEFAULT_I18N_INHERIT_CATEGORY,
            DEFAULT_I18N_INHERIT_LABEL_PARENTNOTFOUND,
            DEFAULT_I18N_INHERIT_CATEGORY,
            i18n));

        componentProperties.put(DEFAULT_BACKGROUND_IMAGE_NODE_NAME,
            getBackgroundImageRenditions(this));
    }

    @Override
    protected void setFields() {
        setComponentFields(new Object[][]{
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {FIELD_ARIA_ROLE, DEFAULT_ARIA_ROLE, DEFAULT_ARIA_ROLE_ATTRIBUTE},
            {COMPONENT_CANCEL_INHERIT_PARENT, false},
        });
    }
}
