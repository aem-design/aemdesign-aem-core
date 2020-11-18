package design.aem.models.v2.content;

import design.aem.models.BaseComponent;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.StringUtils;

import static design.aem.utils.components.ComponentsUtil.DEFAULT_FIELDS_ACCESSIBILITY;
import static design.aem.utils.components.ComponentsUtil.DEFAULT_FIELDS_STYLE;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class Table extends BaseComponent {
    protected void ready() {
        componentProperties = ComponentsUtil.getComponentProperties(
            this,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);

        //backwards compatibility for components that use textData
        String tableData = componentProperties.get("tableData", StringUtils.EMPTY);
        String text = componentProperties.get("text", StringUtils.EMPTY);

        if (isEmpty(text) && isNotEmpty(tableData)) {
            componentProperties.put("text", tableData);
        }
    }

    @Override
    protected void setFields() {
        setComponentFields(new Object[][]{
            {"text", StringUtils.EMPTY},
            {"tableData", StringUtils.EMPTY},
        });
    }
}
