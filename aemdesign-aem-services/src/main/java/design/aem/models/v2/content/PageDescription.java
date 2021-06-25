package design.aem.models.v2.content;

import design.aem.models.BaseComponent;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.vault.util.JcrConstants;

import static design.aem.utils.components.ComponentsUtil.*;

public class PageDescription extends BaseComponent {
    protected void ready() {
        componentProperties = ComponentsUtil.getComponentProperties(
            this,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY,
            DEFAULT_FIELDS_DETAILS_OPTIONS);

        String jcrDescription = getResourcePage().getProperties().get(JcrConstants.JCR_DESCRIPTION, StringUtils.EMPTY);
        String overrideDescription = componentProperties.get(JcrConstants.JCR_DESCRIPTION, StringUtils.EMPTY);

        componentProperties.put("pagedescription",
            StringUtils.isEmpty(overrideDescription) ? jcrDescription : overrideDescription);
    }

    @Override
    protected void setFields() {
        setComponentFields(new Object[][]{
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {JcrConstants.JCR_DESCRIPTION, StringUtils.EMPTY},
        });
    }
}
