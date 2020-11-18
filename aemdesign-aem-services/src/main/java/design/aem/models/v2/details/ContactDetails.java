package design.aem.models.v2.details;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static design.aem.utils.components.ComponentsUtil.DETAILS_DESCRIPTION;
import static design.aem.utils.components.ComponentsUtil.compileComponentMessage;
import static design.aem.utils.components.TagUtil.getTagValueAsAdmin;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class ContactDetails extends GenericDetails {
    protected static final Logger LOGGER = LoggerFactory.getLogger(ContactDetails.class);

    protected static final String COMPONENT_DETAILS_NAME = "contact-details";

    protected static final String FIELD_FORMAT_TITLE = "titleFormat";
    protected static final String FIELD_FORMAT_DESCRIPTION = "descriptionFormat";
    protected static final String FIELD_FORMATTED_TITLE = "titleFormatted";
    protected static final String FIELD_FORMATTED_TITLE_TEXT = "titleFormattedText";
    protected static final String FIELD_FORMATTED_DESCRIPTION = "descriptionFormatted";
    protected static final String FIELD_HONORIFIC_PREFIX = "honorificPrefix";

    protected static final String DEFAULT_FORMAT_TITLE = "${honorificPrefix} ${givenName} ${familyName}";
    protected static final String DEFAULT_FORMAT_DESCRIPTION = "${jobTitle}";

    @Override
    protected void ready() {
        super.ready();

        // Grab value for prefix
        componentProperties.put(FIELD_HONORIFIC_PREFIX, getTagValueAsAdmin(
            componentProperties.get(FIELD_HONORIFIC_PREFIX, StringUtils.EMPTY),
            getSlingScriptHelper()));

        componentProperties.put(DETAILS_DESCRIPTION,
            componentProperties.get(FIELD_FORMATTED_DESCRIPTION, StringUtils.EMPTY));

        // Set something if title formatted is empty
        if (isEmpty(componentProperties.get(FIELD_FORMATTED_TITLE, StringUtils.EMPTY))) {
            componentProperties.put(FIELD_FORMATTED_TITLE, componentProperties.get(FIELD_TITLE, StringUtils.EMPTY));
            componentProperties.put(FIELD_FORMATTED_TITLE_TEXT, componentProperties.get(FIELD_TITLE, StringUtils.EMPTY));
        }
    }

    @Override
    protected void setFields() {
        super.setFields();

        setComponentFields(new Object[][]{
            {FIELD_HONORIFIC_PREFIX, StringUtils.EMPTY},
            {"givenName", StringUtils.EMPTY},
            {"familyName", StringUtils.EMPTY},
            {"jobTitle", StringUtils.EMPTY},
            {"employee", StringUtils.EMPTY},
            {"email", StringUtils.EMPTY},
            {"contactNumber", StringUtils.EMPTY},
        });
    }

    @Override
    protected String getComponentCategory() {
        return "contact-detail";
    }

    @Override
    protected Map<String, Object> processComponentFields() {
        Map<String, Object> newFields = super.processComponentFields();

        try {
            newFields.put(FIELD_FORMATTED_DESCRIPTION, compileComponentMessage(
                FIELD_FORMAT_DESCRIPTION,
                DEFAULT_FORMAT_DESCRIPTION,
                componentProperties,
                slingScriptHelper
            ).trim());
        } catch (Exception ex) {
            LOGGER.error("Could not process component fields in {}", COMPONENT_DETAILS_NAME);
        }

        return newFields;
    }
}
