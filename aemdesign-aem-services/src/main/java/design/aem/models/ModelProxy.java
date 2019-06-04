package design.aem.models;

import com.adobe.cq.sightly.WCMUsePojo;

public abstract class ModelProxy extends WCMUsePojo {
    protected Object[][] componentFields = {};

    protected abstract void ready() throws Exception;

    @Override
    public void activate() throws Exception {
        ready();
    }

    protected void setComponentFields(Object[][]... fields) {
        if (fields != null && fields.length > 0) {
            if (componentFields.length > 0) {
                componentFields = new Object[][]{
                        componentFields,
                        fields,
                };
            } else {
                componentFields = fields;
            }
        }
    }
}
