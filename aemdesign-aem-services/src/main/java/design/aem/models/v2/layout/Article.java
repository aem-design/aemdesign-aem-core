package design.aem.models.v2.layout;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.i18n.I18n;
import design.aem.components.ComponentProperties;
import design.aem.utils.components.ComponentsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.ImagesUtil.DEFAULT_BACKGROUND_IMAGE_NODE_NAME;
import static design.aem.utils.components.ImagesUtil.getBackgroundImageRenditions;

public class Article extends WCMUsePojo {

    protected static final Logger LOGGER = LoggerFactory.getLogger(Article.class);

    protected ComponentProperties componentProperties = null;
    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }

    @Override
    public void activate() throws Exception {

        com.day.cq.i18n.I18n _i18n = new I18n(getRequest());


        final String DEFAULT_ARIA_ROLE = "article";

        // {
        //   { name, defaultValue, attributeName, valueTypeClass }
        // }
        Object[][] componentFields = {
                {FIELD_ARIA_ROLE,DEFAULT_ARIA_ROLE, DEFAULT_ARIA_ROLE_ATTRIBUTE},
                {FIELD_VARIANT, DEFAULT_VARIANT},
        };

        componentProperties = ComponentsUtil.getComponentProperties(
                this,
                componentFields,
                DEFAULT_FIELDS_STYLE,
                DEFAULT_FIELDS_ACCESSIBILITY);

        componentProperties.put(DEFAULT_BACKGROUND_IMAGE_NODE_NAME,getBackgroundImageRenditions(this));

    }



}