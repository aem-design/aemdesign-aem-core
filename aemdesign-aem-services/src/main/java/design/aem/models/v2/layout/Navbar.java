package design.aem.models.v2.layout;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.i18n.I18n;
import design.aem.components.ComponentProperties;
import design.aem.utils.components.ComponentsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.ConstantsUtil.INHERITED_RESOURCE;
import static design.aem.utils.components.I18nUtil.*;
import static design.aem.utils.components.ImagesUtil.DEFAULT_BACKGROUND_IMAGE_NODE_NAME;
import static design.aem.utils.components.ImagesUtil.getBackgroundImageRenditions;

public class Navbar extends WCMUsePojo {

    private static final Logger LOGGER = LoggerFactory.getLogger(Navbar.class);

    private ComponentProperties componentProperties = null;
    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }

    @Override
    public void activate() throws Exception {

        I18n _i18n = new I18n(getRequest());

        final String DEFAULT_ARIA_ROLE = "navigation";

        // {
        //   1 required - property name,
        //   2 required - default value,
        //   3 optional - compile into a data-{name} attribute
        // }
        Object[][] componentFields = {
                {FIELD_ARIA_ROLE,DEFAULT_ARIA_ROLE, DEFAULT_ARIA_ROLE_ATTRIBUTE},
                {FIELD_VARIANT, DEFAULT_VARIANT},
                {COMPONENT_CANCEL_INHERIT_PARENT, false},
        };

        componentProperties = ComponentsUtil.getComponentProperties(
                this,
                componentFields,
                DEFAULT_FIELDS_STYLE,
                DEFAULT_FIELDS_ACCESSIBILITY);

        componentProperties.put(INHERITED_RESOURCE,findInheritedResource(getCurrentPage(),getComponentContext()));
        componentProperties.put(DEFAULT_I18N_INHERIT_LABEL_PARENTNOTFOUND,getDefaultLabelIfEmpty("",DEFAULT_I18N_INHERIT_CATEGORY,DEFAULT_I18N_INHERIT_LABEL_PARENTNOTFOUND,DEFAULT_I18N_INHERIT_CATEGORY,_i18n));

        componentProperties.put(DEFAULT_BACKGROUND_IMAGE_NODE_NAME,getBackgroundImageRenditions(this));

    }




}