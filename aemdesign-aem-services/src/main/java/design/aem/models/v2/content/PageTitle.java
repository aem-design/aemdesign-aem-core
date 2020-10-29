package design.aem.models.v2.content;

import design.aem.components.ComponentProperties;
import design.aem.models.BaseComponent;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.vault.util.JcrConstants;

import static design.aem.utils.components.ComponentsUtil.*;

public class PageTitle extends BaseComponent {
    protected static final String DEFAULT_TAG_TYPE = "span";

    protected void ready() {
        componentProperties = ComponentsUtil.getComponentProperties(
            this,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY,
            DEFAULT_FIELDS_DETAILS_OPTIONS);

        String jcrTitle = getPageProperties().get(JcrConstants.JCR_TITLE, StringUtils.EMPTY);
        String overrideTitle = componentProperties.get(JcrConstants.JCR_TITLE, StringUtils.EMPTY);

        componentProperties.put("pagetitle", StringUtils.isEmpty(overrideTitle) ? jcrTitle : overrideTitle);
    }

    @Override
    protected void setFields() {
        setComponentFields(new Object[][]{
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {JcrConstants.JCR_TITLE, StringUtils.EMPTY},
            {FIELD_TITLE_TAG_TYPE, DEFAULT_TAG_TYPE}
        });
    }
}
