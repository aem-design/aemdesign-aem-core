package design.aem.models.v2.content;

import design.aem.models.BaseComponent;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.jackrabbit.vault.util.JcrConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

import static design.aem.utils.components.CommonUtil.getPageCreated;
import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.TagUtil.getTagValueAsAdmin;

public class PageDate extends BaseComponent {
    protected static final Logger LOGGER = LoggerFactory.getLogger(PageDate.class);

    protected static final String FIELD_PUBLISH_DATE = "publishDate";
    private static final String FIELD_FORMAT_DATE = "dateFormat";
    private static final String FIELD_FORMAT_DATE_DISPLAY = "dateDisplayFormat";

    @SuppressWarnings({"squid:S3008"})
    protected static String PUBLISH_DATE_FORMAT = "yyyy-MM-dd";
    @SuppressWarnings({"squid:S3008"})
    protected static String PUBLISH_DATE_DISPLAY_FORMAT = "EEEE dd MMMM yyyy";

    protected static final String DEFAULT_I18N_CATEGORY = "pagedate";

    protected void ready() {
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
            LOGGER.error("PageDate: dateFormatString={},dateDisplayFormatString={},publishDate={},path={},ex.message={},ex={}",
                dateFormatString,
                dateDisplayFormatString,
                publishDate,
                getResource().getPath(),
                ex.getMessage(),
                ex);
        }

        componentProperties.put(COMPONENT_ATTRIBUTES,
            buildAttributesString(componentProperties.attr.getData(), null));
    }

    @Override
    protected void setFields() {
        setComponentFields(new Object[][]{
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {FIELD_FORMAT_DATE, StringUtils.EMPTY},
            {FIELD_FORMAT_DATE_DISPLAY, StringUtils.EMPTY},
            {FIELD_PUBLISH_DATE, componentDefaults.get(FIELD_PUBLISH_DATE)},
            {JcrConstants.JCR_CREATED, StringUtils.EMPTY},
        });
    }

    @Override
    protected void setFieldDefaults() {
        componentDefaults.put(FIELD_PUBLISH_DATE, getPageCreated(getResourcePage().getProperties()));
    }
}
