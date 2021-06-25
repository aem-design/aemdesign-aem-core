package design.aem.models.v2.layout;

import design.aem.models.BaseComponent;
import design.aem.utils.components.ComponentsUtil;

import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.ImagesUtil.DEFAULT_BACKGROUND_IMAGE_NODE_NAME;
import static design.aem.utils.components.ImagesUtil.getBackgroundImageRenditions;

public class Article extends BaseComponent {
    protected static final String DEFAULT_ARIA_ROLE = "article";

    protected void ready() {
        componentProperties = ComponentsUtil.getComponentProperties(
            this,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);

        componentProperties.put(DEFAULT_BACKGROUND_IMAGE_NODE_NAME,
            getBackgroundImageRenditions(this));
    }

    @Override
    protected void setFields() {
        setComponentFields(new Object[][]{
            {FIELD_ARIA_ROLE, DEFAULT_ARIA_ROLE, DEFAULT_ARIA_ROLE_ATTRIBUTE},
            {FIELD_VARIANT, DEFAULT_VARIANT},
        });
    }
}
