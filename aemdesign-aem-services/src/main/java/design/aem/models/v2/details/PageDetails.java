package design.aem.models.v2.details;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static design.aem.utils.components.ComponentsUtil.DETAILS_DATA_SCHEMA_ITEMSCOPE;
import static design.aem.utils.components.ComponentsUtil.DETAILS_DATA_SCHEMA_ITEMTYPE;

public class PageDetails extends GenericDetails {
    protected static final Logger LOGGER = LoggerFactory.getLogger(PageDetails.class);

    private static final String COMPONENT_DETAILS_NAME = "page-details";

    @Override
    protected String getComponentCategory() {
        return "page-detail";
    }

    @Override
    protected void setFields() {
        super.setFields();

        setComponentFields(new Object[][]{
            {DETAILS_DATA_SCHEMA_ITEMSCOPE, "true", DETAILS_DATA_SCHEMA_ITEMSCOPE},
            {DETAILS_DATA_SCHEMA_ITEMTYPE, "http://schema.org/WebPage"},
        });
    }

}
