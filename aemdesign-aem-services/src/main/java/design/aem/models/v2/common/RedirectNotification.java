package design.aem.models.v2.common;

import com.day.cq.wcm.api.Page;
import design.aem.models.BaseComponent;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.StringUtils;

import static design.aem.utils.components.CommonUtil.PN_REDIRECT_TARGET;
import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.ConstantsUtil.DEFAULT_EXTENTION;
import static design.aem.utils.components.I18nUtil.getDefaultLabelIfEmpty;

public class RedirectNotification extends BaseComponent {
    protected static final String DEFAULT_I18N_CATEGORY = "redirectnotification";
    protected static final String DEFAULT_I18N_LABEL_REDIRECT_IS_SET = "redirectIsSet";
    protected static final String DEFAULT_I18N_LABEL_REDIRECT_IS_NOT_SET = "redirectIsNotSet";
    protected static final String FIELD_REDIRECT_TITLE = "redirectTitle";
    protected static final String FIELD_REDIRECT_URL = "redirectUrl";

    protected void ready() {
        componentProperties = ComponentsUtil.getComponentProperties(
            this,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);

        String redirectTarget = componentProperties.get(FIELD_REDIRECT_URL, StringUtils.EMPTY);
        String redirectTitle = componentProperties.get(FIELD_REDIRECT_TITLE, StringUtils.EMPTY);
        String currentTitle = redirectTarget;

        if (StringUtils.isNotEmpty(redirectTitle)) {
            currentTitle = redirectTitle;
        }

        if (StringUtils.isNotEmpty(redirectTarget) && redirectTarget.startsWith("/content")) {
            Page targetPage = getPageManager().getPage(redirectTarget);

            if (targetPage != null) {
                componentProperties.put(FIELD_REDIRECT_URL, redirectTarget.concat(DEFAULT_EXTENTION));
                componentProperties.put(FIELD_REDIRECT_TITLE, currentTitle);
            }
        } else if (StringUtils.isNotEmpty(redirectTarget)) {
            componentProperties.put(FIELD_REDIRECT_TITLE, currentTitle);
            componentProperties.put(FIELD_REDIRECT_URL, redirectTarget);
        } else {
            componentProperties.put(FIELD_REDIRECT_TITLE, currentTitle);
        }

        componentProperties.put(DEFAULT_I18N_LABEL_REDIRECT_IS_SET, getDefaultLabelIfEmpty(
            StringUtils.EMPTY,
            DEFAULT_I18N_CATEGORY,
            DEFAULT_I18N_LABEL_REDIRECT_IS_SET,
            DEFAULT_I18N_CATEGORY,
            i18n));

        componentProperties.put(DEFAULT_I18N_LABEL_REDIRECT_IS_NOT_SET, getDefaultLabelIfEmpty(
            StringUtils.EMPTY,
            DEFAULT_I18N_CATEGORY,
            DEFAULT_I18N_LABEL_REDIRECT_IS_NOT_SET,
            DEFAULT_I18N_CATEGORY,
            i18n));
    }

    @Override
    protected void setFields() {
        setComponentFields(new Object[][]{
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {FIELD_REDIRECT_TITLE, StringUtils.EMPTY},
            {FIELD_REDIRECT_URL, "#"},
            {FIELD_REDIRECT_TARGET, getResourcePage().getProperties().get(PN_REDIRECT_TARGET, StringUtils.EMPTY)},
        });
    }
}
