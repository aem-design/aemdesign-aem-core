package design.aem.models;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.designer.Style;
import design.aem.components.ComponentProperties;
import design.aem.utils.components.TenantUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.sling.settings.SlingSettingsService;
import org.apache.sling.xss.XSSAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

import static design.aem.utils.components.ComponentsUtil.DEFAULT_TENANT;

public abstract class BaseComponent extends WCMUsePojo {
    protected static final Logger LOGGER = LoggerFactory.getLogger(BaseComponent.class);

    protected static final String ANALYTICS_FIELDS = "analyticsFields";
    protected static final String COMPONENT_FIELDS = "componentFields";

    protected ComponentProperties componentProperties = null;
    protected Style currentStyle;
    protected SlingSettingsService slingSettingsService;
    protected XSSAPI xss;

    public Object[][] analyticsFields = {}; // NOSONAR this needs to be accessible directly
    public Object[][] componentFields = {}; // NOSONAR this needs to be accessible directly

    public abstract void ready() throws Exception; // NOSONAR generic exception is fine

    @Override
    public void activate() throws Exception {
        currentStyle = getCurrentStyle();
        slingSettingsService = getSlingScriptHelper().getService(SlingSettingsService.class);
        xss = getSlingScriptHelper().getService(XSSAPI.class);

        ready();
    }

    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }

    /**
     * Define an {@code Object} array of fields which will be merged with the pre-existing values.
     *
     * @param fields An {@code Object[][]} array of fields to set
     * @param target Which property should the array be applied to?
     */
    protected void setFields(Object[][] fields, String target) {
        if (fields != null && fields.length > 0) {
            try {
                Field classField = this.getClass().getField(target);

                for (Object[] field : fields) {
                    classField.set(this, ArrayUtils.add((Object[][]) classField.get(this), field)); // NOSONAR adding array fields into a field in this class
                }
            } catch (Exception ex) {
                LOGGER.error("ModelProxy: something went wrong while parsing the field! {}", ex.getLocalizedMessage());
            }
        }
    }

    /**
     * A shortcut to {@link #setFields} to set analytics field values.
     *
     * @param fields An {@code Object[][]} array of fields to set
     */
    protected final void setAnalyticsFields(Object[][] fields) {
        setFields(fields, ANALYTICS_FIELDS);
    }

    /**
     * A shortcut to {@link #setFields} to set component field values.
     *
     * @param fields An {@code Object[][]} array of fields to set
     */
    protected final void setComponentFields(Object[][] fields) {
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
