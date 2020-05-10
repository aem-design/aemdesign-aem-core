package design.aem.models.v2.common;

import org.apache.jackrabbit.vault.util.JcrConstants;
import com.day.cq.i18n.I18n;
import design.aem.components.ComponentProperties;
import design.aem.models.ModelProxy;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.StringUtils;

import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.ConstantsUtil.INHERITED_RESOURCE;
import static design.aem.utils.components.ConstantsUtil.SITE_INCLUDE_PATHS;
import static design.aem.utils.components.I18nUtil.*;

public class StaticInclude extends ModelProxy {

    protected ComponentProperties componentProperties = null;

    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }

    protected void ready() {
        I18n i18n = new I18n(getRequest());

        final String FIELD_SHOW_CONTENT = "showContent";
        final String FIELD_SHOW_CONTENT_PREVIEW = "showContentPreview";

        /*
          Component Fields Helper

          Structure:
          1 required - property name,
          2 required - default value,
          3 optional - name of component attribute to add value into
          4 optional - canonical name of class for handling multivalues, String or Tag
         */
        setComponentFields(new Object[][]{
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

        componentProperties.put("componentName", getComponent().getProperties().get(JcrConstants.JCR_TITLE, ""));

        String[] includePaths = getProperties().get(SITE_INCLUDE_PATHS, new String[0]);

        componentProperties.put("includePaths", StringUtils.join(includePaths, ","));

        Boolean showContentPreview = Boolean.parseBoolean(getProperties().get(FIELD_SHOW_CONTENT_PREVIEW, "false"));
        Boolean showContent = Boolean.parseBoolean(getProperties().get(FIELD_SHOW_CONTENT, "false"));
        componentProperties.put(FIELD_SHOW_CONTENT_PREVIEW, showContentPreview);
        componentProperties.put(FIELD_SHOW_CONTENT, showContent);
        componentProperties.put("showContentSet", showContent);

        String includeContents = "";

        includeContents = getResourceContent(getResourceResolver(), includePaths, "");
        componentProperties.put("includeContents", includeContents);
        componentProperties.put("hasContent", StringUtils.isNotEmpty(includeContents));

        //only allow hiding when in edit mode
        if (getWcmMode().isEdit()) {
            componentProperties.put(FIELD_SHOW_CONTENT, showContentPreview);
        }
    }
}
