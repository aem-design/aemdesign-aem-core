package design.aem.models.v2.details;

import com.day.cq.tagging.TagConstants;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static design.aem.utils.components.CommonUtil.getPageCreated;
import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.ConstantsUtil.FIELD_PAGE_TITLE;
import static design.aem.utils.components.ConstantsUtil.FIELD_PAGE_TITLE_NAV;
import static design.aem.utils.components.TagUtil.getTagValueAsAdmin;

public class ContactDetails extends GenericDetails {
    protected static final Logger LOGGER = LoggerFactory.getLogger(ContactDetails.class);

    protected static final String COMPONENT_DETAILS_NAME = "contact-details";

    protected static final String FIELD_HONORIFIC_PREFIX = "honorificPrefix";

    protected static final String DEFAULT_FORMAT_TITLE = "${honorificPrefix} ${givenName} ${familyName}";
    protected static final String DEFAULT_FORMAT_DESCRIPTION = "${jobTitle}";

    @Override
    protected void ready() {
        super.ready();

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
            {"contactNumberMobile", StringUtils.EMPTY},
            {DETAILS_DATA_SCHEMA_ITEMSCOPE, DETAILS_DATA_SCHEMA_ITEMSCOPE, DETAILS_DATA_SCHEMA_ITEMSCOPE},
            {DETAILS_DATA_SCHEMA_ITEMTYPE, "http://schema.org/Person", DETAILS_DATA_SCHEMA_ITEMTYPE},
        });
    }

    @Override
    protected void setFieldDefaults() {
        super.setFieldDefaults();

        //this component has category field
        componentDefaults.put(TagConstants.PN_TAGS, new String[]{});
    }


    @Override
    protected String getComponentCategory() {
        return "contact-detail";
    }

    @Override
    protected Map<String, Object> processComponentFields() {
        Map<String, Object> newFields = super.processComponentFields();

        try {

            // Grab value for prefix
            newFields.put(FIELD_HONORIFIC_PREFIX, getTagValueAsAdmin(
                componentProperties.get(FIELD_HONORIFIC_PREFIX, StringUtils.EMPTY),
                getSlingScriptHelper()));

            String formattedTitle = compileComponentMessage(FIELD_FORMAT_TITLE, DEFAULT_FORMAT_TITLE, componentProperties, slingScriptHelper);
            Document fragment = Jsoup.parse(formattedTitle);
            String formattedTitleText = fragment.text();

            newFields.put(FIELD_FORMATTED_TITLE,
                formattedTitle.trim()
            );
            newFields.put(FIELD_FORMATTED_TITLE_TEXT,
                formattedTitleText.trim()
            );

            newFields.put(FIELD_TITLE, formattedTitleText.trim());
            newFields.put(FIELD_PAGE_TITLE, formattedTitleText.trim());
            newFields.put(FIELD_PAGE_TITLE_NAV, formattedTitleText.trim());
            newFields.put(DETAILS_LINK_TEXT, formattedTitleText.trim());
            newFields.put(DETAILS_BADGE_TITLE, formattedTitleText.trim());
            newFields.put(DETAILS_LINK_TITLE, formattedTitleText.trim());
            newFields.put(DETAILS_ANALYTICS_LABEL, formattedTitleText.trim());
            newFields.put(DETAILS_BADGE_ANALYTICS_LABEL, formattedTitleText.trim());

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
