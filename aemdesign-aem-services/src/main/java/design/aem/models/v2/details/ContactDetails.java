package design.aem.models.v2.details;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.TagUtil.getTagValueAsAdmin;
import static org.eclipse.jetty.util.LazyList.isEmpty;

public class ContactDetails extends GenericDetails {
    protected static final Logger LOGGER = LoggerFactory.getLogger(ContactDetails.class);

    protected static String FIELD_CONTACT_NUMBER = "contactNumber";
    protected static String FIELD_EMAIL = "email";
    protected static String FIELD_EMPLOYEE = "employee";
    protected static String FIELD_FAMILY_NAME = "familyName";
    protected static String FIELD_GIVEN_NAME = "givenName";
    protected static String FIELD_HONORIFIC_PREFIX = "honorificPrefix";
    protected static String FIELD_JOB_TITLE = "jobTitle";

    static {
        COMPONENT_DETAILS_NAME = "contact-details";

        DEFAULT_TITLE_FORMAT = getFormatExpression(FIELD_HONORIFIC_PREFIX, FIELD_GIVEN_NAME, FIELD_FAMILY_NAME);
//        TODO: Find out why this was a div instead of a heading
//        DEFAULT_TITLE_TAG_TYPE = "div";

        DEFAULT_I18N_CATEGORY = "contact-details";
    }

    @Override
    protected void ready() {
        setComponentFields(new Object[][]{
            {FIELD_CONTACT_NUMBER, StringUtils.EMPTY},
            {FIELD_EMAIL, StringUtils.EMPTY},
            {FIELD_EMPLOYEE, StringUtils.EMPTY},
            {FIELD_FAMILY_NAME, StringUtils.EMPTY},
            {FIELD_GIVEN_NAME, StringUtils.EMPTY},
            {FIELD_HONORIFIC_PREFIX, StringUtils.EMPTY}, // Tag path
            {FIELD_JOB_TITLE, StringUtils.EMPTY},
        });

        generateComponentFields();

        // Grab value for prefix
        componentProperties.put(FIELD_HONORIFIC_PREFIX, getTagValueAsAdmin(
            componentProperties.get(FIELD_HONORIFIC_PREFIX, StringUtils.EMPTY),
            getSlingScriptHelper()));

        componentProperties.put(DETAILS_DESCRIPTION,
            componentProperties.get(FIELD_DESCRIPTION_FORMATTED, StringUtils.EMPTY));

        // Ensure the formatted titles have values
        // TODO: Moves these into GenericDetails?
        if (isEmpty(componentProperties.get(FIELD_TITLE_FORMATTED, ""))) {
            componentProperties.put(FIELD_TITLE_FORMATTED,
                componentProperties.get(FIELD_TITLE, StringUtils.EMPTY));

            componentProperties.put(FIELD_TITLE_FORMATTED_TEXT,
                componentProperties.get(FIELD_TITLE, StringUtils.EMPTY));
        }
    }
}
