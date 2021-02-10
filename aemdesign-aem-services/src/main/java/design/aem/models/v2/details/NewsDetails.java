package design.aem.models.v2.details;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import static design.aem.utils.components.CommonUtil.getPageCreated;
import static design.aem.utils.components.ComponentsUtil.DETAILS_DATA_SCHEMA_ITEMSCOPE;
import static design.aem.utils.components.ComponentsUtil.DETAILS_DATA_SCHEMA_ITEMTYPE;

public class NewsDetails extends GenericDetails {
    protected static final Logger LOGGER = LoggerFactory.getLogger(NewsDetails.class);

    private static final String COMPONENT_DETAILS_NAME = "news-details";

    private static final String FIELD_AUTHOR = "author";
    private static final String FIELD_PUBLISH_DATE = "publishDate";

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
            {FIELD_PUBLISH_DATE, componentDefaults.get(FIELD_PUBLISH_DATE)},
            {DETAILS_DATA_SCHEMA_ITEMSCOPE, DETAILS_DATA_SCHEMA_ITEMSCOPE, DETAILS_DATA_SCHEMA_ITEMSCOPE},
            {DETAILS_DATA_SCHEMA_ITEMTYPE, "http://schema.org/NewsArticle", DETAILS_DATA_SCHEMA_ITEMTYPE},
        });
    }

    @Override
    protected void setFieldDefaults() {
        super.setFieldDefaults();

        componentDefaults.put(FIELD_PUBLISH_DATE, getPageCreated(getPageProperties()));
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
            String dateFormatString = i18n.get("publishDateFormat", DEFAULT_I18N_CATEGORY);
            String dateDisplayFormatString = i18n.get("publishDateDisplayFormat", DEFAULT_I18N_CATEGORY);

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
