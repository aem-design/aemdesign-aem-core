package design.aem.models;

import com.adobe.cq.sightly.WCMUsePojo;
import design.aem.components.ComponentProperties;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.sling.xss.XSSAPI;

import java.lang.reflect.Field;

public abstract class ModelProxy extends WCMUsePojo {
    protected static final Logger LOGGER = LoggerFactory.getLogger(ModelProxy.class);

    public Object[][] analyticsFields = {}; //NOSONAR this needs to be accessible directly
    public Object[][] componentFields = {}; //NOSONAR this needs to be accessible directly

    private static final String ANALYTICS_FIELDS = "analyticsFields";
    private static final String COMPONENT_FIELDS = "componentFields";

    protected ComponentProperties componentProperties = null;
    protected SlingSettingsService slingSettingsService;
    protected XSSAPI xssAPI;

    protected abstract void ready() throws Exception; //NOSONAR generic exception is fine

    @Override
    public final void activate() throws Exception {
        slingSettingsService = getSlingScriptHelper().getService(SlingSettingsService.class);
        xssAPI = getSlingScriptHelper().getService(XSSAPI.class);

        ready();
    }

    private void setFields(Object[][] fields, String target) {
        if (fields != null && fields.length > 0) {
            try {
                Field classField = this.getClass().getField(target);

                for (Object[] field : fields) {
                    classField.set(this, ArrayUtils.add((Object[][]) classField.get(this), field)); //NOSONAR adding array fields into a field in this class
                }
            } catch (Exception ex) {
                LOGGER.error("ModelProxy: something went wrong while parsing the field! {}", ex);
            }
        }
    }

    protected void setAnalyticsFields(Object[][] fields) {
        setFields(fields, ANALYTICS_FIELDS);
    }

    protected void setComponentFields(Object[][] fields) {
        setFields(fields, COMPONENT_FIELDS);
    }
}
