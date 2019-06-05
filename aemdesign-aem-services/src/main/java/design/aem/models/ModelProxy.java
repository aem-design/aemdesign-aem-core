package design.aem.models;

import com.adobe.cq.sightly.WCMUsePojo;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

public abstract class ModelProxy extends WCMUsePojo {
    protected static final Logger LOGGER = LoggerFactory.getLogger(ModelProxy.class);

    public Object[][] analyticsFields = {};
    public Object[][] componentFields = {};

    private static final String ANALYTICS_FIELDS = "analyticsFields";
    private static final String COMPONENT_FIELDS = "componentFields";

    protected abstract void ready() throws Exception;

    @Override
    public void activate() throws Exception {
        ready();
    }

    private void setFields(Object[][] fields, String target) {
        if (fields != null && fields.length > 0) {
            try {
                Field classField = this.getClass().getField(target);

                for (Object[] field : fields) {
                    classField.set(this, ArrayUtils.add((Object[][]) classField.get(this), field));
                }
            } catch (Exception ex) {
                LOGGER.error("ModelProxy: something went wrong while parsing the field!");
                ex.printStackTrace();
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