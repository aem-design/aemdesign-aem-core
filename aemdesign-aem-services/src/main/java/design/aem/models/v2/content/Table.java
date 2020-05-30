package design.aem.models.v2.content;

import design.aem.models.BaseComponent;
import design.aem.utils.components.ComponentsUtil;

import static design.aem.utils.components.ComponentsUtil.DEFAULT_FIELDS_ACCESSIBILITY;
import static design.aem.utils.components.ComponentsUtil.DEFAULT_FIELDS_STYLE;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class Table extends BaseComponent {
    protected void ready() {

        /*
          Component Fields Helper

          Structure:
          1 required - property name,
          2 required - default value,
          3 optional - name of component attribute to add value into
          4 optional - canonical name of class for handling multivalues, String or Tag
         */
        setComponentFields(new Object[][]{
            {"text", ""},
            {"tableData", ""}
        });

        componentProperties = ComponentsUtil.getComponentProperties(
            this,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);

        //backwards compatibility for components that use textData
        String tableData = componentProperties.get("tableData", "");
        String text = componentProperties.get("text", "");

        if (isEmpty(text) && isNotEmpty(tableData)) {
            componentProperties.put("text", tableData);
        }
    }
}
