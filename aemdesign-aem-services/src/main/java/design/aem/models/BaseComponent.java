package design.aem.models;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.i18n.I18n;
import design.aem.components.ComponentProperties;
import design.aem.utils.components.TenantUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.apache.sling.settings.SlingSettingsService;
import org.apache.sling.xss.XSSAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static design.aem.utils.components.ComponentsUtil.*;

public abstract class BaseComponent extends WCMUsePojo {
    protected static final Logger LOGGER = LoggerFactory.getLogger(BaseComponent.class);

    public Object[][] analyticsFields = {}; // NOSONAR this needs to be accessible directly
    public Object[][] componentFields = {}; // NOSONAR this needs to be accessible directly

    private static final String ANALYTICS_FIELDS = "analyticsFields";
    private static final String COMPONENT_FIELDS = "componentFields";

    protected Map<String, Object> componentDefaults = new HashMap<>();
    protected ComponentProperties componentProperties = null;
    protected I18n i18n;
    protected SlingScriptHelper slingScriptHelper;
    protected SlingSettingsService slingSettingsService;
    protected XSSAPI xssAPI;

    protected abstract void ready() throws Exception; // NOSONAR generic exception is fine

    /***
     * runs as a final step after all activate steps have finished
     * @throws Exception
     */
    protected void done() throws Exception {

        if (componentProperties != null) {

            //re-evaluate expression fields after all data is ready
            componentProperties.evaluateAllExpressionValues();

            //re-evaluate expression fields after all data is ready
            componentProperties.evaluateExpressionFields();

            //update component attributes after evaluation of fields
            componentProperties.put(COMPONENT_ATTRIBUTES,
                buildAttributesString(componentProperties.attr.getData(), null));

        }

    }; // NOSONAR generic exception is fine

    @Override
    public final void activate() throws Exception {
        i18n = new I18n(getRequest());
        slingScriptHelper = getSlingScriptHelper();
        slingSettingsService = slingScriptHelper.getService(SlingSettingsService.class);
        xssAPI = getSlingScriptHelper().getService(XSSAPI.class);

        //let component set its field defaults
        setFieldDefaults();

        //let component set its fields
        setFields();

        //let component to process its data
        ready();

        //let component to override last step
        done();
    }

    protected void setFields() {
        // Nothing to do!
    }

    protected void setFieldDefaults() {
        // Nothing to do!
    }

    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }

    private void setFields(Object[][] fields, String target) {
        if (fields != null && fields.length > 0) {
            try {
                Field classField = this.getClass().getField(target);

                for (Object[] field : fields) {
                    classField.set(this, ArrayUtils.add((Object[][]) classField.get(this), field)); // NOSONAR adding array fields into a field in this class
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

    /**
     * Return the current tenant based on the current resource path. If this cannot be determined the path
     * falls back to 'aemdesign'.
     *
     * @return {@code String}
     */
    protected final String getCurrentTenant() {
        String currentTenant = TenantUtil.resolveTenantIdFromPath(getResource().getPath());

        if (StringUtils.isEmpty(currentTenant)) {
            currentTenant = DEFAULT_TENANT;
        }

        return currentTenant;
    }
}
