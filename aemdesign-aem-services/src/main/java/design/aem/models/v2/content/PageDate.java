package design.aem.models.v2.content;

import com.day.cq.i18n.I18n;
import design.aem.models.BaseComponent;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.jackrabbit.vault.util.JcrConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

import static design.aem.utils.components.CommonUtil.getPageCreated;
import static design.aem.utils.components.ComponentsUtil.*;

public class PageDate extends BaseComponent {
    protected static final Logger LOGGER = LoggerFactory.getLogger(PageDate.class);

    @SuppressWarnings({"squid:S3008"})
    private static final String PUBLISH_DATE_FORMAT = "yyyy-MM-dd";

    private static final String FIELD_PUBLISH_DATE = "publishDate";

    @SuppressWarnings({"squid:S3008"})
    private static final String PUBLISH_DATE_DISPLAY_FORMAT = "EEEE dd MMMM YYYY";

    public void ready() {
        com.day.cq.i18n.I18n i18n = new I18n(getRequest());

        final String DEFAULT_I18N_CATEGORY = "pagedate";

        setComponentFields(new Object[][]{
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {FIELD_PUBLISH_DATE, getPageCreated(getPageProperties())},
            {JcrConstants.JCR_CREATED, ""}
        });

        componentProperties = ComponentsUtil.getComponentProperties(
            this,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY,
            DEFAULT_FIELDS_DETAILS_OPTIONS);

        long publishDateLong = componentProperties.get(FIELD_PUBLISH_DATE, 0L);
        Calendar publishDate = Calendar.getInstance();
        publishDate.setTimeInMillis(publishDateLong);

        //get format strings from dictionary
        String dateFormatString = i18n.get("publishDateFormat", DEFAULT_I18N_CATEGORY);
        String dateDisplayFormatString = i18n.get("publishDateDisplayFormat", DEFAULT_I18N_CATEGORY);

        //could not read dictionary
        if (dateFormatString.equals("publishDateFormat")) {
            dateFormatString = PUBLISH_DATE_FORMAT;
        }

        //could not read dictionary
        if (dateDisplayFormatString.equals("publishDateDisplayFormat")) {
            dateDisplayFormatString = PUBLISH_DATE_DISPLAY_FORMAT;
        }

        try {

            //format date into formatted date
            FastDateFormat dateFormat = FastDateFormat.getInstance(dateFormatString);
            String publishDateText = dateFormat.format(publishDate.getTime());

            //format date into display date
            dateFormat = FastDateFormat.getInstance(dateDisplayFormatString);
            String publishDisplayDateText = dateFormat.format(publishDate.getTime());

            componentProperties.put("publishDateText", publishDateText);
            componentProperties.put("publishDisplayDateText", publishDisplayDateText);

            componentProperties.attr.add("datetime", publishDateText);

        } catch (Exception ex) {
            LOGGER.error("PageDate: dateFormatString={},dateDisplayFormatString={},publishDate={},path={},ex.message={},ex={}", dateFormatString, dateDisplayFormatString, publishDate, getResource().getPath(), ex.getMessage(), ex);
        }

        componentProperties.put(COMPONENT_ATTRIBUTES,
            buildAttributesString(componentProperties.attr.getAttributes(), xss));
    }
}
