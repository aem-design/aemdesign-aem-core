package design.aem.models;

import com.adobe.cq.sightly.WCMUsePojo;
import org.apache.commons.lang3.ArrayUtils;

public abstract class ModelProxy extends WCMUsePojo {
    protected Object[][] componentFields = {};

    protected abstract void ready() throws Exception;

    @Override
    public void activate() throws Exception {
        ready();
    }

    protected void setComponentFields(Object[][] fields) {
        if (fields != null && fields.length > 0) {
            for (Object[] field : fields) {
                componentFields = ArrayUtils.add(componentFields, field);
            }
        }
    }
}
