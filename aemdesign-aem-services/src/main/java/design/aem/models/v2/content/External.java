package design.aem.models.v2.content;

import design.aem.models.BaseComponent;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.StringUtils;

import static design.aem.utils.components.CommonUtil.getUrlContent;
import static design.aem.utils.components.ComponentsUtil.*;
import static java.text.MessageFormat.format;

public class External extends BaseComponent {
    protected void ready() {
        componentProperties = ComponentsUtil.getComponentProperties(
            this,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);

        String variant = componentProperties.get(FIELD_VARIANT, DEFAULT_VARIANT);

        if (variant.equals("iframe")) {
            variant = DEFAULT_VARIANT;
        }

        if (variant.equals("import")) {
            String target = componentProperties.get("target", StringUtils.EMPTY);

            try {
                componentProperties.put("importHtml", getUrlContent(target));
            } catch (Exception ex) {
                componentProperties.put("importError", ex);
            }
        }

        componentProperties.put(FIELD_VARIANT, variant);
        componentProperties.put(COMPONENT_VARIANT_TEMPLATE, format(COMPONENT_VARIANT_TEMPLATE_FORMAT, variant));
    }

    @Override
    protected void setFields() {
        setComponentFields(new Object[][]{
            {"target", StringUtils.EMPTY, "src"},
            {"height", StringUtils.EMPTY, "height"},
            {"width", StringUtils.EMPTY, "width"},
            {"showScrollbar", "yes", "scrolling"},
            {FIELD_VARIANT, "iframe"}
        });

    }
}
