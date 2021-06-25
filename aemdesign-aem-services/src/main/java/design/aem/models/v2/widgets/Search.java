package design.aem.models.v2.widgets;

import design.aem.models.BaseComponent;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.StringUtils;

import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.I18nUtil.getDefaultLabelIfEmpty;

public class Search extends BaseComponent {
    protected static final String DEFAULT_I18N_CATEGORY = "search";
    protected static final String DEFAULT_I18N_CODE_PLACEHOLDER = "placeholderText";
    protected static final String DEFAULT_I18N_CODE_LEGEND = "legendText";
    protected static final String DEFAULT_I18N_CODE_LEBEL = "labelText";
    protected static final String DEFAULT_I18N_CODE_SEARCH = "searchButtonText";

    protected void ready() {
        componentProperties = ComponentsUtil.getComponentProperties(
            this,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);

        String placeholderText = componentProperties.get(DEFAULT_I18N_CODE_PLACEHOLDER, StringUtils.EMPTY);
        String legendText = componentProperties.get(DEFAULT_I18N_CODE_LEGEND, StringUtils.EMPTY);
        String labelText = componentProperties.get(DEFAULT_I18N_CODE_LEBEL, StringUtils.EMPTY);
        String searchButtonText = componentProperties.get(DEFAULT_I18N_CODE_SEARCH, StringUtils.EMPTY);

        componentProperties.put("placeholderText",
            getDefaultLabelIfEmpty(placeholderText, DEFAULT_I18N_CATEGORY, DEFAULT_I18N_CODE_PLACEHOLDER, DEFAULT_I18N_CATEGORY, i18n));

        componentProperties.put("legendText",
            getDefaultLabelIfEmpty(legendText, DEFAULT_I18N_CATEGORY, DEFAULT_I18N_CODE_LEGEND, DEFAULT_I18N_CATEGORY, i18n));

        componentProperties.put("labelText",
            getDefaultLabelIfEmpty(labelText, DEFAULT_I18N_CATEGORY, DEFAULT_I18N_CODE_LEBEL, DEFAULT_I18N_CATEGORY, i18n));

        componentProperties.put("searchButtonText",
            getDefaultLabelIfEmpty(searchButtonText, DEFAULT_I18N_CATEGORY, DEFAULT_I18N_CODE_SEARCH, DEFAULT_I18N_CATEGORY, i18n));
    }

    @Override
    protected void setFields() {
        setComponentFields(new Object[][]{
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {"formAction", "/en/search"},
            {"formMethod", "get"},
            {"formParameterName", "q"},
            {"feedUrl", new String[0], "data-feed-urls"},
            {DEFAULT_I18N_CODE_PLACEHOLDER, StringUtils.EMPTY},
            {DEFAULT_I18N_CODE_LEGEND, StringUtils.EMPTY},
            {DEFAULT_I18N_CODE_LEBEL, StringUtils.EMPTY},
            {DEFAULT_I18N_CODE_SEARCH, StringUtils.EMPTY},
        });
    }
}
