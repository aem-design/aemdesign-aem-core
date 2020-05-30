package design.aem.models.v2.content;

import design.aem.models.BaseComponent;
import design.aem.utils.components.ComponentsUtil;

import static design.aem.utils.components.ComponentsUtil.*;

public class Tooltip extends BaseComponent {
    protected void ready() {
        final String DEFAULT_ARIA_ROLE = "tooltip";

        setComponentFields(new Object[][]{
            {"title", "", "data-title"},
            {"description", "", "data-description"},
            {FIELD_ARIA_ROLE, DEFAULT_ARIA_ROLE, FIELD_ARIA_DATA_ATTRIBUTE_ROLE},
            {FIELD_VARIANT, DEFAULT_VARIANT},
        });

        componentProperties = ComponentsUtil.getComponentProperties(
            this,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ANALYTICS,
            DEFAULT_FIELDS_ACCESSIBILITY);
    }
}
