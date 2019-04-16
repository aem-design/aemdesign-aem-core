package design.aem.models.v2.content;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.i18n.I18n;
import design.aem.components.ComponentProperties;
import design.aem.utils.components.ComponentsUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import static design.aem.utils.components.CommonUtil.getUrlContent;
import static design.aem.utils.components.ComponentsUtil.*;
import static java.text.MessageFormat.format;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class External extends WCMUsePojo {

    private static final Logger LOGGER = LoggerFactory.getLogger(External.class);

    private ComponentProperties componentProperties = null;
    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }

    @Override
    public void activate() throws Exception {

        I18n _i18n = new I18n(getRequest());

        // {
        //   1 required - property name,
        //   2 required - default value,
        //   3 optional - compile into a data-{name} attribute
        // }

        Object[][] componentFields = {
                {"target", "", "src"},
                {"height", "", "height"},
                {"width", "", "width"},
                {"showScrollbar", "yes", "scrolling"},
                {FIELD_VARIANT, "iframe"}
        };

        componentProperties = ComponentsUtil.getComponentProperties(
                this,
                componentFields,
                DEFAULT_FIELDS_STYLE,
                DEFAULT_FIELDS_ACCESSIBILITY);

        String variant = componentProperties.get(FIELD_VARIANT,DEFAULT_VARIANT);

        if (variant.equals("iframe")) {
            variant = DEFAULT_VARIANT;
        }

        if (variant.equals("import")) {
            String target = componentProperties.get("target","");

            try {
                componentProperties.put("importHtml",getUrlContent(target));
            } catch (Exception ex) {
                componentProperties.put("importError",ex);
            }
        }

        componentProperties.put(FIELD_VARIANT,variant);
        componentProperties.put(COMPONENT_VARIANT_TEMPLATE, format(COMPONENT_VARIANT_TEMPLATE_FORMAT,variant));


    }



}