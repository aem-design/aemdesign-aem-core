package design.aem.models.v2.details;

import com.day.cq.tagging.TagConstants;
import com.day.cq.wcm.api.NameConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.jackrabbit.vault.util.JcrConstants;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.TagUtil.getTagValueAsAdmin;

public class EventDetails extends GenericDetails {
    protected static final Logger LOGGER = LoggerFactory.getLogger(EventDetails.class);

    protected static final String FIELD_EVENT_START_DATE = "eventStartDate";
    protected static final String FIELD_EVENT_END_DATE = "eventEndDate";

    protected static final String EVENT_DISPLAY_DATE_FORMAT = "EEE d MMMMM";
    protected static final String EVENT_DISPLAY_DATE_FORMAT_ISO = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    protected static final String EVENT_TIME_DEFAULT_FORMAT = "h:mm a";
    protected static final String HOURS_TIME_FORMAT = "h a";
    protected static final String MINUTES_TIME_FORMAT = "mm";
    protected static final String TIME_ZERO_FORMAT = "00";
    protected static final String DEFAULT_FORMAT_TITLE = "${title}";
    protected static final String DEFAULT_FORMAT_SUBTITLE = "${eventStartDateText} to ${eventEndDateText}";
    protected static final String DEFAULT_FORMAT_DISPLAYDATE = "${eventStartDateText} to ${eventEndDateText}";
    protected static final String DEFAULT_FORMAT_DISPLAYTIME = "${eventStartTimeText} to ${eventEndTimeText}";

    @Override
    protected void ready() {
        super.ready();

        if (Boolean.TRUE.equals(componentProperties.get("isPastEventDate", false))) {
            componentProperties.put(DETAILS_CARD_ADDITIONAL, "finished");
        }
    }

    @Override
    protected void setFields() {
        super.setFields();

        setComponentFields(new Object[][]{
            {FIELD_EVENT_START_DATE, componentDefaults.get(FIELD_EVENT_END_DATE)},
            {FIELD_EVENT_END_DATE, componentDefaults.get(FIELD_EVENT_END_DATE)},
            {"eventLoc", StringUtils.EMPTY},
            {"eventRefLabel", StringUtils.EMPTY},
            {"eventRefLink", StringUtils.EMPTY},
            {"eventRefLabel2", StringUtils.EMPTY},
            {"eventRefLink2", StringUtils.EMPTY},
            {"useParentPageTitle", false},
            {"subTitleFormat", StringUtils.EMPTY},
            {"eventDisplayDateFormat", StringUtils.EMPTY},
            {"eventDisplayTimeFormat", StringUtils.EMPTY},
            {"eventTimeFormat", EVENT_TIME_DEFAULT_FORMAT},
            {DETAILS_DATA_SCHEMA_ITEMSCOPE, DETAILS_DATA_SCHEMA_ITEMSCOPE, DETAILS_DATA_SCHEMA_ITEMSCOPE},
            {DETAILS_DATA_SCHEMA_ITEMTYPE, "http://schema.org/Event", DETAILS_DATA_SCHEMA_ITEMTYPE},
        });
    }

    @Override
    protected void setFieldDefaults() {
        super.setFieldDefaults();

        componentDefaults.put(FIELD_EVENT_START_DATE, getResourcePage().getProperties().get(
            NameConstants.PN_ON_TIME,
            getResourcePage().getProperties().get(JcrConstants.JCR_CREATED, Calendar.getInstance())));

        componentDefaults.put(FIELD_EVENT_END_DATE, getResourcePage().getProperties().get(
            NameConstants.PN_OFF_TIME,
            getResourcePage().getProperties().get(JcrConstants.JCR_CREATED, Calendar.getInstance())));
            }

    @Override
    protected String getComponentCategory() {
        return "event-detail";
    }

    @Override
    protected Map<String, Object> processComponentFields() {
        Map<String, Object> newFields = super.processComponentFields();

        try {
            Calendar eventStartDate = componentProperties.get(FIELD_EVENT_START_DATE, Calendar.getInstance());
            Calendar eventEndDate = componentProperties.get(FIELD_EVENT_END_DATE, Calendar.getInstance());
            String selectedEventTimeFormat = componentProperties.get("eventTimeFormat", StringUtils.EMPTY);

            newFields.put("isPastEventDate", eventEndDate.before(Calendar.getInstance()));

            newFields.put(FIELD_EVENT_START_DATE, eventStartDate);
            newFields.put(FIELD_EVENT_END_DATE, eventEndDate);

            FastDateFormat dateFormatString = FastDateFormat.getInstance(EVENT_DISPLAY_DATE_FORMAT_ISO);

            Date startDateTime = eventStartDate.getTime();
            Date endDateTime = eventEndDate.getTime();

            newFields.put("eventStartDateISO", dateFormatString.format(startDateTime));
            newFields.put("eventEndDateISO", dateFormatString.format(endDateTime));

            FastDateFormat dateFormat = FastDateFormat.getInstance(EVENT_DISPLAY_DATE_FORMAT);
            String eventStartDateText = dateFormat.format(startDateTime);
            String eventEndDateText = dateFormat.format(endDateTime);

            String eventStartDateUppercase = dateFormat.format(startDateTime).toUpperCase();
            String eventEndDateUppercase = dateFormat.format(endDateTime).toUpperCase();

            String eventStartDateLowercase = dateFormat.format(startDateTime).toLowerCase();
            String eventEndDateLowercase = dateFormat.format(endDateTime).toLowerCase();

            newFields.put("eventStartDateText", eventStartDateText);
            newFields.put("eventEndDateText", eventEndDateText);

            newFields.put("eventStartDateUppercase", eventStartDateUppercase);
            newFields.put("eventEndDateUppercase", eventEndDateUppercase);

            newFields.put("eventStartDateLowercase", eventStartDateLowercase);
            newFields.put("eventEndDateLowercase", eventEndDateLowercase);

            if (!selectedEventTimeFormat.equals(EVENT_TIME_DEFAULT_FORMAT)) {
                selectedEventTimeFormat = getTagValueAsAdmin(selectedEventTimeFormat, slingScriptHelper);
            }

            FastDateFormat timeFormat = FastDateFormat.getInstance(selectedEventTimeFormat);
            FastDateFormat minTimeFormat = FastDateFormat.getInstance(MINUTES_TIME_FORMAT);
            FastDateFormat hourTimeFormat = FastDateFormat.getInstance(HOURS_TIME_FORMAT);

            String eventStartTimeText = timeFormat.format(startDateTime);
            String eventEndTimeText = timeFormat.format(endDateTime);

            String startTimeMinutes = minTimeFormat.format(startDateTime);
            String endTimeMinutes = minTimeFormat.format(endDateTime);

            String eventStartTimeMinFormatted = timeFormat.format(startDateTime).toLowerCase();
            String eventEndTimeMinFormatted = timeFormat.format(endDateTime).toLowerCase();

            if (startTimeMinutes.equals(TIME_ZERO_FORMAT)) {
                eventStartTimeMinFormatted = hourTimeFormat.format(startDateTime);
            }

            if (endTimeMinutes.equals(TIME_ZERO_FORMAT)) {
                eventEndTimeMinFormatted = hourTimeFormat.format(endDateTime);
            }

            newFields.put("eventStartTimeText", eventStartTimeText);
            newFields.put("eventEndTimeText", eventEndTimeText);

            newFields.put("eventStartTimeUppercase", eventStartTimeText.toUpperCase());
            newFields.put("eventEndTimeUppercase", eventEndTimeText.toUpperCase());

            newFields.put("eventStartTimeLowercase", eventStartTimeText.toLowerCase());
            newFields.put("eventEndTimeLowercase", eventEndTimeText.toLowerCase());

            newFields.put("eventStartTimeMinFormatted", eventStartTimeMinFormatted);
            newFields.put("eventEndTimeMinFormatted", eventEndTimeMinFormatted);

            newFields.put("eventStartTimeMinLowerFormatted", eventStartTimeMinFormatted.toLowerCase());
            newFields.put("eventEndTimeMinLowerFormatted", eventEndTimeMinFormatted.toLowerCase());

            newFields.put("eventStartTimeMinUpperFormatted", eventStartTimeMinFormatted.toUpperCase());
            newFields.put("eventEndTimeMinUpperFormatted", eventEndTimeMinFormatted.toUpperCase());

            newFields.put(FIELD_FORMATTED_TITLE, compileComponentMessage(
                FIELD_FORMAT_TITLE,
                DEFAULT_FORMAT_TITLE,
                componentProperties,
                slingScriptHelper));

            newFields.put("subTitleFormatted", compileComponentMessage(
                "subTitleFormat",
                DEFAULT_FORMAT_SUBTITLE,
                componentProperties,
                slingScriptHelper));

            newFields.put("eventDisplayDateFormatted", compileComponentMessage(
                "eventDisplayDateFormat",
                DEFAULT_FORMAT_DISPLAYDATE,
                componentProperties,
                slingScriptHelper));

            newFields.put("eventDisplayTimeFormatted", compileComponentMessage(
                "eventDisplayTimeFormat",
                DEFAULT_FORMAT_DISPLAYTIME,
                componentProperties,
                slingScriptHelper));

            //start date for generic details / startdate
            String startDateFormatted = compileComponentMessage(
                "eventDisplayDateFormat",
                DEFAULT_FORMAT_DISPLAYDATE,
                componentProperties,
                slingScriptHelper);
            Document fragment = Jsoup.parse(startDateFormatted);
            String startDateFormattedText = fragment.text();

            newFields.put("startDateFormatted",
                startDateFormatted.trim()
            );
            newFields.put("startDateFormattedText",
                startDateFormattedText.trim()
            );

            //start time for generic details / starttime
            String startTimeFormatted = compileComponentMessage(
                "eventDisplayTimeFormat",
                DEFAULT_FORMAT_DISPLAYTIME,
                componentProperties,
                slingScriptHelper);

            Document startTimeFormattedFragment = Jsoup.parse(startDateFormatted);
            String startTimeFormattedText = startTimeFormattedFragment.text();

            newFields.put("startTimeFormatted",
                startTimeFormatted.trim()
            );
            newFields.put("startTimeFormattedText",
                startTimeFormattedText.trim()
            );

            //startDate for generic details / starttime & startdate
            newFields.put("startDate", eventStartDate);
            newFields.put("endDate", eventEndDate);


        } catch (Exception ex) {
            LOGGER.error("Could not process component fields in {}", COMPONENT_DETAILS_NAME);
        }

        return newFields;
    }
}
