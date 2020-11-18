package design.aem.models.v2.content;

import design.aem.models.BaseComponent;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.StringUtils;

import static design.aem.utils.components.ComponentsUtil.*;

public class Tooltip extends BaseComponent {
    protected static final String DEFAULT_ARIA_ROLE = "tooltip";

    protected void ready() {
        componentProperties = ComponentsUtil.getComponentProperties(
            this,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ANALYTICS,
            DEFAULT_FIELDS_ACCESSIBILITY);
    }

    @Override
    protected void setFields() {
        setComponentFields(new Object[][]{
            {"title", StringUtils.EMPTY, "data-title"},
            {"description", StringUtils.EMPTY, "data-description"},
            {FIELD_ARIA_ROLE, DEFAULT_ARIA_ROLE, FIELD_ARIA_DATA_ATTRIBUTE_ROLE},
            {FIELD_VARIANT, DEFAULT_VARIANT},
        });
    }
}
