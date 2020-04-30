package design.aem.models.v2.widgets;

import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.tagging.Tag;
import design.aem.components.ComponentProperties;
import design.aem.models.ModelProxy;
import design.aem.utils.components.ComponentsUtil;

import static design.aem.utils.components.ComponentsUtil.*;

public class ShareButton extends ModelProxy {
    protected ComponentProperties componentProperties = null;

    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }

    protected void ready() {
        final String DEFAULT_ARIA_ROLE = "banner";
        final String DEFAULT_CLOUDCONFIG_ADDTHIS = "addthisconnect";
        final String DEFAULT_CLOUDCONFIG_ADDTHIS_ID = "pubId";
        final String DEFAULT_TOOLID = "toolId";
        final String DEFAULT_MODULE_TAG = "aemdesign:component-style-theme/widgets/sharebutton";

        setComponentFields(new Object[][]{
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {FIELD_ARIA_ROLE, DEFAULT_ARIA_ROLE, FIELD_ARIA_DATA_ATTRIBUTE_ROLE},
            {FIELD_STYLE_COMPONENT_MODULES, new String[]{DEFAULT_MODULE_TAG}, "data-modules", Tag.class.getCanonicalName()},
            {FIELD_STYLE_COMPONENT_THEME, new String[]{}, "class", Tag.class.getCanonicalName()},
            {DEFAULT_TOOLID, "data-toolid"},
            {DEFAULT_CLOUDCONFIG_ADDTHIS_ID,
                getCloudConfigProperty((InheritanceValueMap) getPageProperties(),
                    DEFAULT_CLOUDCONFIG_ADDTHIS,
                    DEFAULT_CLOUDCONFIG_ADDTHIS_ID,
                    getSlingScriptHelper()),
                "data-pubid"},
        });

        componentProperties = ComponentsUtil.getComponentProperties(
            this,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);
    }
}
