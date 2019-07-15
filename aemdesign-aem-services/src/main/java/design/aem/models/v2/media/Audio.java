package design.aem.models.v2.media;

import design.aem.components.ComponentProperties;
import design.aem.models.ModelProxy;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.ResolverUtil.mappedUrl;

public class Audio extends ModelProxy {
    protected static final Logger LOGGER = LoggerFactory.getLogger(Audio.class);

    protected ComponentProperties componentProperties = null;
    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }

    protected void ready() {

        final String DEFAULT_ARIA_ROLE = "button";
        final String DEFAULT_ARIA_LABEL = "Audio Fragment";

        /**
         * Component Fields Helper
         *
         * Structure:
         * 1 required - property name,
         * 2 required - default value,
         * 3 optional - name of component attribute to add value into
         * 4 optional - canonical name of class for handling multivalues, String or Tag
         */
        setComponentFields(new Object[][]{
                {"audioUrl",""},
                {FIELD_ARIA_ROLE,DEFAULT_ARIA_ROLE},
                {FIELD_ARIA_LABEL,DEFAULT_ARIA_LABEL},
                {FIELD_VARIANT, DEFAULT_VARIANT},
        });

        componentProperties = ComponentsUtil.getComponentProperties(
                this,
                componentFields,
                DEFAULT_FIELDS_STYLE,
                DEFAULT_FIELDS_ACCESSIBILITY);

        String audioUrl = componentProperties.get("audioUrl", "");

        if (StringUtils.isNotEmpty(audioUrl)) {
            if (audioUrl.startsWith("/content")) {
                audioUrl = mappedUrl(getResourceResolver(), audioUrl);
            }
        }

        componentProperties.put("audioUrl", audioUrl);
    }
}