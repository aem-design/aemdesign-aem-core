package design.aem.models.v2.content;

import design.aem.components.ComponentProperties;
import design.aem.models.ModelProxy;
import design.aem.utils.components.ComponentsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static design.aem.utils.components.ComponentsUtil.*;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class EmbedSource extends ModelProxy {
    protected static final Logger LOGGER = LoggerFactory.getLogger(EmbedSource.class);

    protected ComponentProperties componentProperties = null;
    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }

    protected void ready() {
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
                {"html", ""},
                {FIELD_VARIANT, DEFAULT_VARIANT}
        });

        componentProperties = ComponentsUtil.getComponentProperties(
                this,
                componentFields,
                DEFAULT_FIELDS_STYLE,
                DEFAULT_FIELDS_ACCESSIBILITY);

        String html = componentProperties.get("html","");

        if (isNotEmpty(html)) {
            html = html.replaceAll("&nbsp;", " ");
            html = html.replaceAll("\\s+", " ");
            html = html.replaceAll(" = ", "=");
            html = org.apache.commons.lang.StringEscapeUtils.unescapeHtml(html);
        }

        componentProperties.put("html", html);
    }
}