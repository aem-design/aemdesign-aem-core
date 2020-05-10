package design.aem.models.v2.content;

import design.aem.components.ComponentProperties;
import design.aem.models.ModelProxy;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.jexl3.*;

import static design.aem.utils.components.ComponentsUtil.*;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class ContentTemplate extends ModelProxy {

    protected ComponentProperties componentProperties = null;
    protected ComponentProperties requestComponentProperties = null;

    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }

    public ComponentProperties getRequestComponentProperties() {
        return this.requestComponentProperties;
    }

    public static final String REQUEST_COMPONENT_PROPERTIES = "design.aem.models.v2.content.ContentTemplate.componentProperties";
    public static final String FIELD_CUSTOM_TEMPLATE_JEXL = "customTemplateJEXL";

    protected void ready() {
        /*
          Component Fields Helper

          Structure:
          1 required - property name,
          2 required - default value,
          3 optional - name of component attribute to add value into
          4 optional - canonical name of class for handling multivalues, String or Tag
         */
        setComponentFields(new Object[][]{
            {"templatePath", ""},
            {FIELD_CUSTOM_TEMPLATE_JEXL, ""},
            {"templateUse", "templatePath"},
            {FIELD_VARIANT, DEFAULT_VARIANT}
        });

        componentProperties = ComponentsUtil.getComponentProperties(
            this,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);

        String customTemplate = componentProperties.get(FIELD_CUSTOM_TEMPLATE_JEXL, "");
        String customTemplateOutput = "";

        requestComponentProperties = (ComponentProperties) getRequest().getAttribute(REQUEST_COMPONENT_PROPERTIES);

        if (isNotEmpty(customTemplate) && requestComponentProperties != null) {

            JexlEngine jexl = new JexlBuilder().create();
            JxltEngine jxlt = jexl.createJxltEngine();
            JexlContext jc = new MapContext(requestComponentProperties);

            //try to evaluate default value expression
            try {
                JxltEngine.Expression expr = jxlt.createExpression(customTemplate);

                //add componentProperties as a variable as well
                jc.set("componentProperties", requestComponentProperties);

                customTemplateOutput = (String) expr.evaluate(jc);

            } catch (JexlException jex) {
                LOGGER.warn("could not evaluate default value expression customTemplate={}, requestComponentProperties={}, jex.info={}", customTemplate, requestComponentProperties, jex.getInfo());
            } catch (Exception ex) {
                LOGGER.error("could not evaluate default value expression customTemplate={}, requestComponentProperties={}, error={}", customTemplate, requestComponentProperties, ex);
            }

        }
        componentProperties.put("customTemplateJEXLOutput", customTemplateOutput);

    }
}
