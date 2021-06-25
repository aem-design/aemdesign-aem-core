package design.aem.models.v2.content;

import design.aem.models.BaseComponent;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.StringUtils;

import static design.aem.utils.components.ComponentsUtil.*;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class EmbedSource extends BaseComponent {
    protected void ready() {
        componentProperties = ComponentsUtil.getComponentProperties(
            this,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);

        String html = componentProperties.get("html", StringUtils.EMPTY);

        if (isNotEmpty(html)) {
            html = html.replace("&nbsp;", " ");
            html = html.replace("\\s+", " ");
            html = html.replace(" = ", "=");
            html = org.apache.commons.lang.StringEscapeUtils.unescapeHtml(html);
        }

        componentProperties.put("html", html);
    }

    @Override
    protected void setFields() {
        setComponentFields(new Object[][]{
            {"html", StringUtils.EMPTY},
            {FIELD_VARIANT, DEFAULT_VARIANT}
        });
    }
}
