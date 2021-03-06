package design.aem.models.v2.details;

import com.day.cq.tagging.TagConstants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import static design.aem.utils.components.CommonUtil.getPageCreated;
import static design.aem.utils.components.ComponentsUtil.DETAILS_DATA_SCHEMA_ITEMSCOPE;
import static design.aem.utils.components.ComponentsUtil.DETAILS_DATA_SCHEMA_ITEMTYPE;
import static design.aem.utils.components.TagUtil.getTagValueAsAdmin;

public class NewsDetails extends GenericDetails {
    protected static final Logger LOGGER = LoggerFactory.getLogger(NewsDetails.class);

    private static final String COMPONENT_DETAILS_NAME = "news-details";

    private static final String FIELD_AUTHOR = "author";
    private static final String FIELD_PUBLISH_DATE = "publishDate";
    private static final String FIELD_FORMAT_DATE = "dateFormat";
    private static final String FIELD_FORMAT_DATE_DISPLAY = "dateDisplayFormat";

    @SuppressWarnings({"squid:S3008"})
    protected static String PUBLISH_DATE_FORMAT = "yyyy-MM-dd";
    @SuppressWarnings({"squid:S3008"})
    protected static String PUBLISH_DATE_DISPLAY_FORMAT = "EEEE dd MMMM yyyy";

    protected static final String DEFAULT_I18N_CATEGORY = "news-detail";

    @Override
    protected void ready() {
        super.ready();

    }

    @Override
    protected void setFields() {
        super.setFields();

        setComponentFields(new Object[][]{
            {FIELD_AUTHOR, StringUtils.EMPTY},
            {FIELD_FORMAT_DATE, StringUtils.EMPTY},
            {FIELD_FORMAT_DATE_DISPLAY, StringUtils.EMPTY},
            {FIELD_PUBLISH_DATE, componentDefaults.get(FIELD_PUBLISH_DATE)},
            {DETAILS_DATA_SCHEMA_ITEMSCOPE, DETAILS_DATA_SCHEMA_ITEMSCOPE, DETAILS_DATA_SCHEMA_ITEMSCOPE},
            {DETAILS_DATA_SCHEMA_ITEMTYPE, "http://schema.org/NewsArticle", DETAILS_DATA_SCHEMA_ITEMTYPE},
        });
    }

    @Override
    protected void setFieldDefaults() {
        super.setFieldDefaults();

        componentDefaults.put(FIELD_PUBLISH_DATE, getPageCreated(getResourcePage().getProperties()));
    }

    @Override
    protected String getComponentCategory() {
        return DEFAULT_I18N_CATEGORY;
    }

    @Override
    protected Map<String, Object> processComponentFields() {
        Map<String, Object> newFields = super.processComponentFields();
        try {
            long publishDateLong = componentProperties.get(FIELD_PUBLISH_DATE, 0L);

            Calendar publishDate = Calendar.getInstance();
            publishDate.setTimeInMillis(publishDateLong);

            // Get format strings from dictionary
            //get format strings from dictionary
            String dateFormatString = getTagValueAsAdmin(
                componentProperties.get(FIELD_FORMAT_DATE, StringUtils.EMPTY),
                getSlingScriptHelper());
            String dateDisplayFormatString = getTagValueAsAdmin(
                componentProperties.get(FIELD_FORMAT_DATE_DISPLAY, StringUtils.EMPTY),
                getSlingScriptHelper());

            //could not read dictionary
            if (StringUtils.isEmpty(dateFormatString)) {
                dateFormatString = PUBLISH_DATE_FORMAT;
            }

            //could not read dictionary
            if (StringUtils.isEmpty(dateDisplayFormatString)) {
                dateDisplayFormatString = PUBLISH_DATE_DISPLAY_FORMAT;
            }

            // Format date into formatted date
            SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatString);
            String publishDateText = dateFormat.format(publishDate.getTime());

            // Format date into display date
            dateFormat = new SimpleDateFormat(dateDisplayFormatString);
            String publishDisplayDateText = dateFormat.format(publishDate.getTime());

            newFields.put("publishDateText", publishDateText);
            newFields.put("publishDisplayDateText", publishDisplayDateText);

            // Get full published date display text
            String newsDateStatusText = i18n.get(
                "newsDateStatusText",
                DEFAULT_I18N_CATEGORY,
                publishDateText,
                publishDisplayDateText);

            newFields.put("newsDateStatusText", newsDateStatusText);
        } catch (Exception ex) {
            LOGGER.error("Could not process component fields in {}", COMPONENT_DETAILS_NAME);
        }

        return newFields;
    }
}
