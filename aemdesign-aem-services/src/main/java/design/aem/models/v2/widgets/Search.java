package design.aem.models.v2.widgets;

import com.day.cq.i18n.I18n;
import design.aem.components.ComponentProperties;
import design.aem.models.ModelProxy;
import design.aem.utils.components.ComponentsUtil;

import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.I18nUtil.getDefaultLabelIfEmpty;

public class Search extends ModelProxy {

    protected ComponentProperties componentProperties = null;
    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }

    protected void ready() {
        I18n i18n = new I18n(getRequest());

        final String DEFAULT_I18N_CATEGORY = "search";
        final String DEFAULT_I18N_CODE_PLACEHOLDER = "placeholderText";
        final String DEFAULT_I18N_CODE_LEGEND = "legendText";
        final String DEFAULT_I18N_CODE_LEBEL = "labelText";
        final String DEFAULT_I18N_CODE_SEARCH = "searchButtonText";

        //not using lamda is available so this is the best that can be done
        setComponentFields(new Object[][]{
                {FIELD_VARIANT, DEFAULT_VARIANT},
                {"formAction", "/en/search"},
                {"formMethod", "get"},
                {"formParameterName", "q"},
                {"feedUrl", new String[0],"data-feed-urls"},
                {DEFAULT_I18N_CODE_PLACEHOLDER, ""},
                {DEFAULT_I18N_CODE_LEGEND, ""},
                {DEFAULT_I18N_CODE_LEBEL, ""},
                {DEFAULT_I18N_CODE_SEARCH, ""},
        });

        componentProperties = ComponentsUtil.getComponentProperties(
                this,
                componentFields,
                DEFAULT_FIELDS_STYLE,
                DEFAULT_FIELDS_ACCESSIBILITY);

        String placeholderText = componentProperties.get(DEFAULT_I18N_CODE_PLACEHOLDER,"");
        String legendText = componentProperties.get(DEFAULT_I18N_CODE_LEGEND,"");
        String labelText = componentProperties.get(DEFAULT_I18N_CODE_LEBEL,"");
        String searchButtonText = componentProperties.get(DEFAULT_I18N_CODE_SEARCH,"");

        componentProperties.put("placeholderText", getDefaultLabelIfEmpty(placeholderText,DEFAULT_I18N_CATEGORY,DEFAULT_I18N_CODE_PLACEHOLDER,DEFAULT_I18N_CATEGORY,i18n));
        componentProperties.put("legendText", getDefaultLabelIfEmpty(legendText,DEFAULT_I18N_CATEGORY,DEFAULT_I18N_CODE_LEGEND,DEFAULT_I18N_CATEGORY,i18n));
        componentProperties.put("labelText", getDefaultLabelIfEmpty(labelText,DEFAULT_I18N_CATEGORY,DEFAULT_I18N_CODE_LEBEL,DEFAULT_I18N_CATEGORY,i18n));
        componentProperties.put("searchButtonText", getDefaultLabelIfEmpty(searchButtonText,DEFAULT_I18N_CATEGORY,DEFAULT_I18N_CODE_SEARCH,DEFAULT_I18N_CATEGORY,i18n));
    }
}