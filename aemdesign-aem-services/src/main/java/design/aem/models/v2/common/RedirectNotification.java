package design.aem.models.v2.common;

import com.day.cq.i18n.I18n;
import com.day.cq.wcm.api.Page;
import design.aem.components.ComponentProperties;
import design.aem.models.ModelProxy;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static design.aem.utils.components.CommonUtil.PN_REDIRECT_TARGET;
import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.ConstantsUtil.DEFAULT_EXTENTION;
import static design.aem.utils.components.I18nUtil.getDefaultLabelIfEmpty;

public class RedirectNotification extends ModelProxy {
    protected static final Logger LOGGER = LoggerFactory.getLogger(RedirectNotification.class);

    protected ComponentProperties componentProperties = null;
    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }

    protected void ready() {
        I18n i18n = new I18n(getRequest());

        final String DEFAULT_I18N_CATEGORY = "redirectnotification";
        final String DEFAULT_I18N_LABEL_REDIRECT_IS_SET = "redirectIsSet";
        final String DEFAULT_I18N_LABEL_REDIRECT_IS_NOT_SET = "redirectIsNotSet";

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
                {FIELD_VARIANT, DEFAULT_VARIANT},
                {"redirectTitle", ""},
                {"redirectUrl", "#"},
                {"redirectTarget", getPageProperties().get(PN_REDIRECT_TARGET, "")},
        });

        componentProperties = ComponentsUtil.getComponentProperties(
                this,
                componentFields,
                DEFAULT_FIELDS_STYLE,
                DEFAULT_FIELDS_ACCESSIBILITY);

        String redirectTarget = componentProperties.get("redirectUrl", "");
        String redirectTitle = componentProperties.get("redirectTitle", "");
        String currentTitle = redirectTarget;

        if (StringUtils.isNotEmpty(redirectTitle)) {
            currentTitle = redirectTitle;
        }

        if (StringUtils.isNotEmpty(redirectTarget) && redirectTarget.startsWith("/content")) {
            Page targetPage = getPageManager().getPage(redirectTarget);

            if (targetPage != null) {
                componentProperties.put("redirectUrl",redirectTarget.concat(DEFAULT_EXTENTION));
                componentProperties.put("redirectTitle",currentTitle);
            }
        } else if (StringUtils.isNotEmpty(redirectTarget)) {
            componentProperties.put("redirectTitle",currentTitle);
            componentProperties.put("redirectUrl",redirectTarget);
        } else {
            componentProperties.put("redirectTitle",currentTitle);
        }

        componentProperties.put(DEFAULT_I18N_LABEL_REDIRECT_IS_SET,getDefaultLabelIfEmpty("",DEFAULT_I18N_CATEGORY,DEFAULT_I18N_LABEL_REDIRECT_IS_SET,DEFAULT_I18N_CATEGORY,i18n));
        componentProperties.put(DEFAULT_I18N_LABEL_REDIRECT_IS_NOT_SET,getDefaultLabelIfEmpty("",DEFAULT_I18N_CATEGORY,DEFAULT_I18N_LABEL_REDIRECT_IS_NOT_SET,DEFAULT_I18N_CATEGORY,i18n));
    }
}