package design.aem.models.v2.layout;

import com.day.cq.i18n.I18n;
import design.aem.components.ComponentProperties;
import design.aem.models.ModelProxy;
import design.aem.utils.components.ComponentsUtil;

import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.ConstantsUtil.INHERITED_RESOURCE;
import static design.aem.utils.components.I18nUtil.*;
import static design.aem.utils.components.ImagesUtil.DEFAULT_BACKGROUND_IMAGE_NODE_NAME;
import static design.aem.utils.components.ImagesUtil.getBackgroundImageRenditions;

@SuppressWarnings("common-java:DuplicatedBlocks")
public class Navbar extends ModelProxy {

    protected ComponentProperties componentProperties = null;

    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }

    @SuppressWarnings("Duplicates")
    protected void ready() {
        I18n i18n = new I18n(getRequest());

        final String DEFAULT_ARIA_ROLE = "navigation";

        /*
          Component Fields Helper

          Structure:
          1 required - property name,
          2 required - default value,
          3 optional - name of component attribute to add value into
          4 optional - canonical name of class for handling multivalues, String or Tag
         */
        setComponentFields(new Object[][]{
            {FIELD_ARIA_ROLE, DEFAULT_ARIA_ROLE, DEFAULT_ARIA_ROLE_ATTRIBUTE},
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {COMPONENT_CANCEL_INHERIT_PARENT, false},
        });

        componentProperties = ComponentsUtil.getComponentProperties(
            this,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);

        componentProperties.put(INHERITED_RESOURCE, findInheritedResource(getResourcePage(), getComponentContext()));
        componentProperties.put(DEFAULT_I18N_INHERIT_LABEL_PARENTNOTFOUND, getDefaultLabelIfEmpty("", DEFAULT_I18N_INHERIT_CATEGORY, DEFAULT_I18N_INHERIT_LABEL_PARENTNOTFOUND, DEFAULT_I18N_INHERIT_CATEGORY, i18n));

        componentProperties.put(DEFAULT_BACKGROUND_IMAGE_NODE_NAME, getBackgroundImageRenditions(this));
    }
}
