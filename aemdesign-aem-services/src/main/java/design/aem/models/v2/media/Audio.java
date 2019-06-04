package design.aem.models.v2.media;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.i18n.I18n;
import design.aem.components.ComponentProperties;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.ResolverUtil.mappedUrl;

public class Audio extends WCMUsePojo {

    protected static final Logger LOGGER = LoggerFactory.getLogger(Audio.class);

    protected ComponentProperties componentProperties = null;
    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }

    @Override
    public void activate() throws Exception {

        I18n _i18n = new I18n(getRequest());


        final String DEFAULT_ARIA_ROLE = "button";
        final String DEFAULT_ARIA_LABEL = "Audio Fragment";

        // {
        //   1 required - property name,
        //   2 required - default value,
        //   3 optional - compile into a data-{name} attribute
        // }
        Object[][] componentFields = {
                {"audioUrl",""},
                {FIELD_ARIA_ROLE,DEFAULT_ARIA_ROLE},
                {FIELD_ARIA_LABEL,DEFAULT_ARIA_LABEL},
                {FIELD_VARIANT, DEFAULT_VARIANT},
        };

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