package design.aem.models.v2.details;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PageDetails extends GenericDetails {
    protected static final Logger LOGGER = LoggerFactory.getLogger(PageDetails.class);

    static {
        COMPONENT_DETAILS_NAME = "page-details";

        USE_SIDE_EFFECTS = false;
    }
}
