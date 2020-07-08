package design.aem.models.v2.details;

import com.day.cq.i18n.I18n;
import design.aem.components.ComponentProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import static design.aem.utils.components.CommonUtil.getPageCreated;

public class NewsDetails extends GenericDetails {
    protected static final Logger LOGGER = LoggerFactory.getLogger(NewsDetails.class);

    static {
        COMPONENT_DETAILS_NAME = "news-details";

        DEFAULT_I18N_CATEGORY = "news-details";

        USE_SIDE_EFFECTS = false;
    }

    protected static final String FIELD_AUTHOR = "author";
    protected static final String FIELD_PUBLISH_DATE = "publishDate";

    @Override
    protected void setAdditionalComponentFields() {
        setComponentFields(new Object[][]{
            {FIELD_AUTHOR, StringUtils.EMPTY},
            {FIELD_PUBLISH_DATE, getPageCreated(getPageProperties())},
        });
    }

    @Override
    protected void processAdditionalComponentFields(
        ComponentProperties componentProperties,
        I18n i18n,
        SlingScriptHelper sling,
        Map<String, Object> newFields
    ) {
        long publishDateLong = componentProperties.get(FIELD_PUBLISH_DATE, 0L);

        Calendar publishDate = Calendar.getInstance();
        publishDate.setTimeInMillis(publishDateLong);

        //get format strings from dictionary
        String dateFormatString = i18n.get("publishDateFormat", DEFAULT_I18N_CATEGORY);
        String dateDisplayFormatString = i18n.get("publishDateDisplayFormat", DEFAULT_I18N_CATEGORY);

        //format date into formatted date
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatString);
        String publishDateText = dateFormat.format(publishDate.getTime());

        //format date into display date
        dateFormat = new SimpleDateFormat(dateDisplayFormatString);
        String publishDisplayDateText = dateFormat.format(publishDate.getTime());

        newFields.put("publishDateText", publishDateText);
        newFields.put("publishDisplayDateText", publishDisplayDateText);

        //get full published date display text
        String newsDateStatusText = i18n.get("newsDateStatusText", DEFAULT_I18N_CATEGORY, publishDateText, publishDisplayDateText);
        newFields.put("newsDateStatusText", newsDateStatusText);
    }
}
