package design.aem.models.v2.content;

import design.aem.models.BaseComponent;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.StringUtils;

import static design.aem.utils.components.ComponentsUtil.*;
import static java.text.MessageFormat.format;

public class Text extends BaseComponent {
    protected void ready() {
        componentProperties = ComponentsUtil.getComponentProperties(
            this,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ANALYTICS,
            DEFAULT_FIELDS_ACCESSIBILITY);

        String variant = componentProperties.get(FIELD_VARIANT, DEFAULT_VARIANT);

        //simple is default
        if (variant.equals("simple")) {
            variant = DEFAULT_VARIANT;
        }

        componentProperties.put(FIELD_VARIANT, variant);

        //compile variantTemplate param
        componentProperties.put(COMPONENT_VARIANT_TEMPLATE, format(COMPONENT_VARIANT_TEMPLATE_FORMAT, variant));
    }

    @Override
    protected void setFields() {
        setComponentFields(new Object[][]{
            {"text", StringUtils.EMPTY},
            {FIELD_VARIANT, DEFAULT_VARIANT},
        });
    }
}
