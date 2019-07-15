package design.aem.models.v2.media;

import design.aem.components.ComponentProperties;
import design.aem.models.ModelProxy;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.StringUtils;

import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.ResolverUtil.mappedUrl;

public class Audio extends ModelProxy {

    protected ComponentProperties componentProperties = null;
    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }

    protected void ready() {

        final String DEFAULT_ARIA_ROLE = "button";
        final String DEFAULT_ARIA_LABEL = "Audio Fragment";
        final String FIELD_AUDIO_URL = "audioUrl";

        /*
          Component Fields Helper

          Structure:
          1 required - property name,
          2 required - default value,
          3 optional - name of component attribute to add value into
          4 optional - canonical name of class for handling multivalues, String or Tag
         */
        setComponentFields(new Object[][]{
                {FIELD_AUDIO_URL,""},
                {FIELD_ARIA_ROLE,DEFAULT_ARIA_ROLE},
                {FIELD_ARIA_LABEL,DEFAULT_ARIA_LABEL},
                {FIELD_VARIANT, DEFAULT_VARIANT},
        });

        componentProperties = ComponentsUtil.getComponentProperties(
                this,
                componentFields,
                DEFAULT_FIELDS_STYLE,
                DEFAULT_FIELDS_ACCESSIBILITY);

        String audioUrl = componentProperties.get(FIELD_AUDIO_URL, "");

        if (StringUtils.isNotEmpty(audioUrl)) {
            if (audioUrl.startsWith("/content")) {
                audioUrl = mappedUrl(getResourceResolver(), audioUrl);
            }
        }

        componentProperties.put(FIELD_AUDIO_URL, audioUrl);
    }
}