package design.aem.models.v2.content;

import design.aem.models.BaseComponent;
import design.aem.utils.components.ComponentsUtil;


import static design.aem.utils.components.ComponentsUtil.*;
import static java.text.MessageFormat.format;

public class Text extends BaseComponent {
    public void ready() {

        /*
          Component Fields Helper

          Structure:
          1 required - property name,
          2 required - default value,
          3 optional - name of component attribute to add value into
          4 optional - canonical name of class for handling multivalues, String or Tag
         */
        setComponentFields(new Object[][]{
            {"text", ""},
            {FIELD_VARIANT, DEFAULT_VARIANT}
        });

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
}
