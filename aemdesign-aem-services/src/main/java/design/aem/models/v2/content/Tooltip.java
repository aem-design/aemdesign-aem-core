package design.aem.models.v2.content;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.i18n.I18n;
import design.aem.components.ComponentProperties;
import design.aem.utils.components.ComponentsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static design.aem.utils.components.ComponentsUtil.*;

public class Tooltip extends WCMUsePojo {

    protected static final Logger LOGGER = LoggerFactory.getLogger(Tooltip.class);

    protected ComponentProperties componentProperties = null;
    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }

    @Override
    public void activate() throws Exception {

        I18n _i18n = new I18n(getRequest());

        final String DEFAULT_ARIA_ROLE = "tooltip";

        Object[][] componentFields = {
                {"title", "", "data-title"},
                {"description", "", "data-description"},
                {FIELD_ARIA_ROLE, DEFAULT_ARIA_ROLE, FIELD_ARIA_DATA_ATTRIBUTE_ROLE},
                {FIELD_VARIANT, DEFAULT_VARIANT},
        };

        componentProperties = ComponentsUtil.getComponentProperties(
                this,
                componentFields,
                DEFAULT_FIELDS_STYLE,
                DEFAULT_FIELDS_ANALYTICS,
                DEFAULT_FIELDS_ACCESSIBILITY);

    }



}