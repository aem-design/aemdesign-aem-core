package design.aem.models.v2.content;

import com.day.cq.tagging.TagConstants;
import design.aem.components.ComponentProperties;
import design.aem.models.ModelProxy;
import design.aem.utils.components.ComponentsUtil;

import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.TagUtil.getTagsAsAdmin;

public class PageTags extends ModelProxy {

    protected ComponentProperties componentProperties = null;
    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }

    protected void ready() {

        setComponentFields(new Object[][]{
                {FIELD_VARIANT, DEFAULT_VARIANT},
                {"jcr:created", ""}
        });

        componentProperties = ComponentsUtil.getComponentProperties(
                this,
                componentFields,
                DEFAULT_FIELDS_STYLE,
                DEFAULT_FIELDS_ACCESSIBILITY,
                DEFAULT_FIELDS_DETAILS_OPTIONS);

        componentProperties.put("pagetags", getTagsAsAdmin(getSlingScriptHelper(), getPageProperties().get(TagConstants.PN_TAGS,new String[]{}), getRequest().getLocale()));
    }
}