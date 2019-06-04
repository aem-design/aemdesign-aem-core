package design.aem.models.v2.content;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.i18n.I18n;
import design.aem.components.ComponentProperties;
import design.aem.utils.components.ComponentsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static design.aem.utils.components.ComponentsUtil.DEFAULT_FIELDS_ACCESSIBILITY;
import static design.aem.utils.components.ComponentsUtil.DEFAULT_FIELDS_STYLE;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class Table extends WCMUsePojo {

    protected static final Logger LOGGER = LoggerFactory.getLogger(Table.class);

    protected ComponentProperties componentProperties = null;
    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }

    @Override
    public void activate() throws Exception {

        I18n _i18n = new I18n(getRequest());

        // {
        //   1 required - property name,
        //   2 required - default value,
        //   3 optional - name of component attribute to add value into
        //   4 optional - canonical name of class for handling multivalues, String or Tag
        // }
        Object[][] componentFields = {
                {"text",""},
                {"tableData",""}
        };

        componentProperties = ComponentsUtil.getComponentProperties(
                this,
                componentFields,
                DEFAULT_FIELDS_STYLE,
                DEFAULT_FIELDS_ACCESSIBILITY);

        //backwards compatibility for components that use textData
        String tableData = componentProperties.get("tableData","");
        String text = componentProperties.get("text","");

        if (isEmpty(text) && isNotEmpty(tableData)) {
            componentProperties.put("text",tableData);
        }

    }



}