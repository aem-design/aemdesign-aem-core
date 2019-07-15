package design.aem.models.v2.content;

import com.day.cq.i18n.I18n;
import design.aem.components.ComponentProperties;
import design.aem.models.ModelProxy;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.vault.util.JcrConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static design.aem.utils.components.ComponentsUtil.*;

public class PageTitle extends ModelProxy {
    protected static final Logger LOGGER = LoggerFactory.getLogger(PageTitle.class);

    protected ComponentProperties componentProperties = null;
    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }

    protected void ready() {

        final String DEFAULT_TAG_TYPE = "span";

        setComponentFields(new Object[][]{
                {FIELD_VARIANT, DEFAULT_VARIANT},
                {JcrConstants.JCR_TITLE, ""},
                {FIELD_TITLE_TAG_TYPE, DEFAULT_TAG_TYPE}
        });

        componentProperties = ComponentsUtil.getComponentProperties(
                this,
                componentFields,
                DEFAULT_FIELDS_STYLE,
                DEFAULT_FIELDS_ACCESSIBILITY,
                DEFAULT_FIELDS_DETAILS_OPTIONS);

        String jcrTitle = getPageProperties().get(JcrConstants.JCR_TITLE, "");
        String overrideTitle = componentProperties.get(JcrConstants.JCR_TITLE, "");

        componentProperties.put("pagetitle", StringUtils.isEmpty(overrideTitle) ? jcrTitle : overrideTitle);
    }
}