package design.aem.models.v2.content;

import com.day.cq.i18n.I18n;
import com.day.cq.tagging.TagConstants;
import design.aem.components.ComponentProperties;
import design.aem.models.ModelProxy;
import design.aem.utils.components.ComponentsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.TagUtil.getTagsAsAdmin;

public class PageTags extends ModelProxy {
    protected static final Logger LOGGER = LoggerFactory.getLogger(PageTags.class);

    protected ComponentProperties componentProperties = null;
    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }

    protected void ready() {
        I18n _i18n = new I18n(getRequest());

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