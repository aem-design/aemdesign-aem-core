package design.aem.models.v2.content;

import com.day.cq.wcm.api.WCMMode;
import design.aem.components.ComponentProperties;
import design.aem.models.ModelProxy;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;

import static design.aem.utils.components.ComponentsUtil.*;
import static java.text.MessageFormat.format;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class Reference extends ModelProxy {

    protected ComponentProperties componentProperties = null;
    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }

    protected void ready() {

        setComponentFields(new Object[][]{
                {"path", StringUtils.EMPTY},
                {"selectors", StringUtils.EMPTY},
                {"wcmmodeName", "DISABLED"},
                {FIELD_VARIANT, DEFAULT_VARIANT}
        });

        componentProperties = ComponentsUtil.getComponentProperties(
                this,
                componentFields,
                DEFAULT_FIELDS_STYLE,
                DEFAULT_FIELDS_ACCESSIBILITY);


        String wcmmodeName = componentProperties.get("wcmmodeName","DISABLED");
        WCMMode wcmMode = WCMMode.valueOf(wcmmodeName);
        componentProperties.put("wcmmode", wcmMode);

        String path = componentProperties.get("path","");

        Resource referenceResource = getResourceResolver().getResource(path);
        if (referenceResource == null || ResourceUtil.isNonExistingResource(referenceResource)) {
            LOGGER.error("reference path does not exist {}", path);
            path = "";
        }

        if (isNotEmpty(path)) {
            componentProperties.put("pathUrl",path.concat(".html"));
        } else {
            componentProperties.put("isEmpty", true);
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
