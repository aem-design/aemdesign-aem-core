package design.aem.models.v2.details;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PageDetails extends GenericDetails {
    protected static final Logger LOGGER = LoggerFactory.getLogger(PageDetails.class);

    private static final String COMPONENT_DETAILS_NAME = "page-details";

    @Override
    protected String getComponentCategory() {
        return "page-detail";
    }
}
