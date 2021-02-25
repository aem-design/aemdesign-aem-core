package design.aem.models.v2.content;

import design.aem.components.ComponentProperties;
import design.aem.models.BaseComponent;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.jexl3.*;
import org.apache.commons.lang3.StringUtils;

import static design.aem.utils.components.ComponentsUtil.*;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class ContentTemplate extends BaseComponent {
    protected ComponentProperties requestComponentProperties = null;

    public static final String REQUEST_COMPONENT_PROPERTIES = "design.aem.models.v2.content.ContentTemplate.componentProperties";
    public static final String FIELD_CUSTOM_TEMPLATE_JEXL = "customTemplateJEXL";

    protected void ready() {
        componentProperties = ComponentsUtil.getComponentProperties(
            this,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);

        String customTemplate = componentProperties.get(FIELD_CUSTOM_TEMPLATE_JEXL, StringUtils.EMPTY);
        String customTemplateOutput = StringUtils.EMPTY;

        requestComponentProperties = (ComponentProperties) getRequest().getAttribute(REQUEST_COMPONENT_PROPERTIES);

        if (requestComponentProperties == null) {
            LOGGER.warn("request component properties not present for content template to work, please add use this in details component.");
        }

        if (isNotEmpty(customTemplate) && requestComponentProperties != null) {
            JexlEngine jexl = new JexlBuilder().create();
            JxltEngine jxlt = jexl.createJxltEngine();
            JexlContext jc = new MapContext(requestComponentProperties);

            try {
                JxltEngine.Expression expr = jxlt.createExpression(customTemplate);

                //add componentProperties as a variable as well
                jc.set("componentProperties", requestComponentProperties);

                customTemplateOutput = (String) expr.evaluate(jc);

            } catch (JexlException jex) {
                LOGGER.warn("could not evaluate default value expression customTemplate={}, requestComponentProperties={}, jex.info={}",
                    customTemplate,
                    requestComponentProperties,
                    jex.getInfo());
            } catch (Exception ex) {
                LOGGER.error("could not evaluate default value expression customTemplate={}, requestComponentProperties={}, error={}",
                    customTemplate,
                    requestComponentProperties,
                    ex);
            }
        }

        componentProperties.put("customTemplateJEXLOutput", customTemplateOutput);
    }

    @Override
    protected void setFields() {
        setComponentFields(new Object[][]{
            {"templatePath", StringUtils.EMPTY},
            {FIELD_CUSTOM_TEMPLATE_JEXL, StringUtils.EMPTY},
            {"templateUse", "templatePath"},
            {FIELD_VARIANT, DEFAULT_VARIANT}
        });
    }

    public ComponentProperties getRequestComponentProperties() {
        return this.requestComponentProperties;
    }
}
