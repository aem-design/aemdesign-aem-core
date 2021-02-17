package design.aem.models.v2.widgets;

import com.day.cq.tagging.Tag;
import design.aem.models.BaseComponent;
import design.aem.utils.components.ComponentsUtil;

import static design.aem.utils.components.ComponentsUtil.*;

public class ShareButton extends BaseComponent {
    protected static final String DEFAULT_ARIA_ROLE = "banner";
    protected static final String DEFAULT_CLOUDCONFIG_ADDTHIS = "addthisconnect";
    protected static final String DEFAULT_CLOUDCONFIG_ADDTHIS_ID = "pubId";
    protected static final String DEFAULT_TOOLID = "toolId";
    protected static final String DEFAULT_MODULE_TAG = "aemdesign:component-style-theme/widgets/sharebutton";

    protected void ready() {
        componentProperties = ComponentsUtil.getComponentProperties(
            this,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);
    }

    @Override
    protected void setFields() {
        setComponentFields(new Object[][]{
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {FIELD_ARIA_ROLE, DEFAULT_ARIA_ROLE, FIELD_ARIA_DATA_ATTRIBUTE_ROLE},
            {FIELD_STYLE_COMPONENT_MODULES, new String[]{DEFAULT_MODULE_TAG}, "data-modules", Tag.class.getCanonicalName()},
            {FIELD_STYLE_COMPONENT_THEME, new String[]{}, "class", Tag.class.getCanonicalName()},
            {DEFAULT_TOOLID, componentDefaults.get(DEFAULT_TOOLID), "data-toolid"},
            {DEFAULT_CLOUDCONFIG_ADDTHIS_ID, componentDefaults, "data-pubid"},
        });
    }

    @Override
    protected void setFieldDefaults() {
        componentDefaults.put(DEFAULT_TOOLID, getCloudConfigProperty(
            getInheritedPageProperties(),
            DEFAULT_CLOUDCONFIG_ADDTHIS,
            DEFAULT_CLOUDCONFIG_ADDTHIS_ID,
            getSlingScriptHelper()));
    }
}
