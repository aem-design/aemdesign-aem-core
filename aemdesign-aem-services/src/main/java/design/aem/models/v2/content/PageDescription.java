package design.aem.models.v2.content;

import design.aem.components.ComponentProperties;
import design.aem.models.ModelProxy;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.vault.util.JcrConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static design.aem.utils.components.ComponentsUtil.*;

public class PageDescription extends ModelProxy {
    protected static final Logger LOGGER = LoggerFactory.getLogger(PageDescription.class);

    protected ComponentProperties componentProperties = null;
    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }

    protected void ready() {

        setComponentFields(new Object[][]{
                {FIELD_VARIANT, DEFAULT_VARIANT},
                {JcrConstants.JCR_DESCRIPTION, ""}
        });

        componentProperties = ComponentsUtil.getComponentProperties(
                this,
                componentFields,
                DEFAULT_FIELDS_STYLE,
                DEFAULT_FIELDS_ACCESSIBILITY,
                DEFAULT_FIELDS_DETAILS_OPTIONS);

        String jcrDescription = getPageProperties().get(JcrConstants.JCR_DESCRIPTION, "");
        String overrideDescription = componentProperties.get(JcrConstants.JCR_DESCRIPTION, "");

        componentProperties.put("pagedescription", StringUtils.isEmpty(overrideDescription) ? jcrDescription : overrideDescription);
    }
}