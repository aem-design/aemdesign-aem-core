package design.aem.models.v2.widgets;

import com.day.cq.i18n.I18n;
import design.aem.components.ComponentProperties;
import design.aem.models.ModelProxy;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.ConstantsUtil.*;
import static design.aem.utils.components.ResolverUtil.checkResourceHasChildResource;

public class OnlineMedia extends ModelProxy {
    protected static final Logger LOGGER = LoggerFactory.getLogger(OnlineMedia.class);

    protected ComponentProperties componentProperties = null;
    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }

    protected void ready() {
        I18n _i18n = new I18n(getRequest());

        final String DEFAULT_VARIANT = "default";
        final String RESOURCE_EXISTS = "resourceExists";

        setComponentFields(new Object[][]{
                {FIELD_VARIANT, DEFAULT_VARIANT},
                {FIELD_MEDIA_PROVIDER, StringUtils.EMPTY, FIELD_DATA_MEDIA_PROVIDER},
                {FIELD_MEDIA_TITLE, StringUtils.EMPTY, FIELD_DATA_MEDIA_TITLE},
                {FIELD_MEDIA_ID, StringUtils.EMPTY, FIELD_DATA_MEDIA_ID},
                {FIELD_MEDIA_PARTNER_ID, StringUtils.EMPTY, FIELD_DATA_MEDIA_PARTNER_ID},
                {FIELD_MEDIA_PLAYER_ID, StringUtils.EMPTY, FIELD_DATA_MEDIA_PLAYER_ID},
                {FIELD_MEDIA_PROVIDER_URL, StringUtils.EMPTY, FIELD_SOURCE_ATTRIBUTE},
        });

        componentProperties = ComponentsUtil.getComponentProperties(
                this,
                componentFields,
                DEFAULT_FIELDS_STYLE,
                DEFAULT_FIELDS_ACCESSIBILITY);

        String field_variant = componentProperties.get(FIELD_VARIANT,StringUtils.EMPTY);
        String resourceName = String.format("%s.%s.%s", FIELD_VARIANT, field_variant, EXTENSION_JSP);

        componentProperties.put(RESOURCE_EXISTS, checkResourceHasChildResource(resourceName, getResource(), getResourceResolver()));
    }
}