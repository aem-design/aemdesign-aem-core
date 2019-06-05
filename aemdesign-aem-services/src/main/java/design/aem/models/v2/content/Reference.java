package design.aem.models.v2.content;

import com.day.cq.i18n.I18n;
import design.aem.components.ComponentProperties;
import design.aem.models.ModelProxy;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static design.aem.utils.components.ComponentsUtil.*;
import static java.text.MessageFormat.format;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class Reference extends ModelProxy {
    protected static final Logger LOGGER = LoggerFactory.getLogger(Reference.class);

    protected ComponentProperties componentProperties = null;
    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }

    protected void ready() {
        I18n _i18n = new I18n(getRequest());

        //not using lamda is available so this is the best that can be done
        setComponentFields(new Object[][]{
                {"path", StringUtils.EMPTY},
                {FIELD_VARIANT, DEFAULT_VARIANT}
        });

        componentProperties = ComponentsUtil.getComponentProperties(
                this,
                componentFields,
                DEFAULT_FIELDS_STYLE,
                DEFAULT_FIELDS_ACCESSIBILITY);


        String path = componentProperties.get("path","");
        if (isNotEmpty(path)) {
            componentProperties.put("pathUrl",path.concat(".html"));
        }

        String variant = componentProperties.get(FIELD_VARIANT,DEFAULT_VARIANT);

        //render is default, render resource in its own context
        if (variant.equals("render")) {
            variant = DEFAULT_VARIANT;
        }

        componentProperties.put(FIELD_VARIANT,variant);

        //compile variantTemplate param
        componentProperties.put(COMPONENT_VARIANT_TEMPLATE, format(COMPONENT_VARIANT_TEMPLATE_FORMAT,variant));
    }
}