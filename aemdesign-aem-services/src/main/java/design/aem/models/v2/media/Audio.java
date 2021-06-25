package design.aem.models.v2.media;

import design.aem.models.BaseComponent;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.StringUtils;

import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.ResolverUtil.mappedUrl;

public class Audio extends BaseComponent {
    protected static final String DEFAULT_ARIA_ROLE = "button";
    protected static final String DEFAULT_ARIA_LABEL = "Audio Fragment";
    protected static final String FIELD_AUDIO_URL = "audioUrl";

    protected void ready() {
        componentProperties = ComponentsUtil.getComponentProperties(
                this,
                componentFields,
                DEFAULT_FIELDS_STYLE,
                DEFAULT_FIELDS_ACCESSIBILITY);

        String audioUrl = componentProperties.get(FIELD_AUDIO_URL, StringUtils.EMPTY);

        if (StringUtils.isNotEmpty(audioUrl) && audioUrl.startsWith("/content")) {
            audioUrl = mappedUrl(getResourceResolver(), audioUrl);
        }

        componentProperties.put(FIELD_AUDIO_URL, audioUrl);
    }

    @Override
    protected void setFields() {
        setComponentFields(new Object[][]{
            {FIELD_AUDIO_URL,StringUtils.EMPTY},
            {FIELD_ARIA_ROLE,DEFAULT_ARIA_ROLE},
            {FIELD_ARIA_LABEL,DEFAULT_ARIA_LABEL},
            {FIELD_VARIANT, DEFAULT_VARIANT},
        });
    }
}
