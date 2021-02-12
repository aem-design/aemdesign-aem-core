package design.aem.models.v2.content;

import com.day.cq.tagging.TagConstants;
import design.aem.models.BaseComponent;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.vault.util.JcrConstants;

import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.TagUtil.getTagsAsAdmin;

public class PageTags extends BaseComponent {
    protected void ready() {
        componentProperties = ComponentsUtil.getComponentProperties(
            this,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY,
            DEFAULT_FIELDS_DETAILS_OPTIONS);

        componentProperties.put("pagetags", getTagsAsAdmin(
            getSlingScriptHelper(),
            getResourcePage().getProperties().get(TagConstants.PN_TAGS, new String[]{}),
            getRequest().getLocale()));
    }

    @Override
    protected void setFields() {
        setComponentFields(new Object[][]{
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {JcrConstants.JCR_CREATED, StringUtils.EMPTY},
        });
    }
}
