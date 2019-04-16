package design.aem.models.v2.content;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.i18n.I18n;
import design.aem.components.ComponentProperties;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.vault.util.JcrConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static design.aem.utils.components.ComponentsUtil.*;

public class PageDescription extends WCMUsePojo {

    private static final Logger LOGGER = LoggerFactory.getLogger(PageDescription.class);

    private ComponentProperties componentProperties = null;
    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }

    @Override
    public void activate() throws Exception {

        com.day.cq.i18n.I18n _i18n = new I18n(getRequest());

        Object[][] componentFields = {
                {FIELD_VARIANT, DEFAULT_VARIANT},
                {JcrConstants.JCR_DESCRIPTION, ""}
        };

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