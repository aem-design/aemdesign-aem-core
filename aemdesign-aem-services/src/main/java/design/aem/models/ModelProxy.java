package design.aem.models;

import com.adobe.cq.sightly.WCMUsePojo;
import design.aem.components.ComponentProperties;
import design.aem.utils.components.TenantUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

import static design.aem.utils.components.ComponentsUtil.DEFAULT_TENANT;

public abstract class ModelProxy extends WCMUsePojo {
    protected static final Logger LOGGER = LoggerFactory.getLogger(ModelProxy.class);

    protected ComponentProperties componentProperties = null;

    public Object[][] analyticsFields = {}; //NOSONAR this needs to be accessible directly
    public Object[][] componentFields = {}; //NOSONAR this needs to be accessible directly

    private static final String ANALYTICS_FIELDS = "analyticsFields";
    private static final String COMPONENT_FIELDS = "componentFields";

    protected abstract void ready() throws Exception; //NOSONAR generic exception is fine

    @Override
    public void activate() throws Exception {
        ready();
    }

    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }

    private void setFields(Object[][] fields, String target) {
        if (fields != null && fields.length > 0) {
            try {
                Field classField = this.getClass().getField(target);

                for (Object[] field : fields) {
                    classField.set(this, ArrayUtils.add((Object[][]) classField.get(this), field)); //NOSONAR adding array fields into a field in this class
                }
            } catch (Exception ex) {
                LOGGER.error("ModelProxy: something went wrong while parsing the field! {}", ex.getLocalizedMessage());
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
    protected String getCurrentTenant() {
        String currentTenant = TenantUtil.resolveTenantIdFromPath(getResource().getPath());

        if (StringUtils.isEmpty(currentTenant)) {
            currentTenant = DEFAULT_TENANT;
        }

        return currentTenant;
    }
}
