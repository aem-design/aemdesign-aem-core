package design.aem.models.v2.details;

import com.day.cq.i18n.I18n;
import com.day.cq.wcm.api.NameConstants;
import design.aem.components.ComponentProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.jackrabbit.vault.util.JcrConstants;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import static design.aem.utils.components.ComponentsUtil.DETAILS_CARD_ADDITIONAL;
import static design.aem.utils.components.ComponentsUtil.compileComponentMessage;
import static design.aem.utils.components.TagUtil.getTagValueAsAdmin;

public class EventDetails extends GenericDetails {
    protected static final Logger LOGGER = LoggerFactory.getLogger(EventDetails.class);

    static {
        COMPONENT_DETAILS_NAME = "event-details";

        DEFAULT_I18N_CATEGORY = "event-details";

        USE_SIDE_EFFECTS = false;
    }

    protected static final String EVENT_FINISHED_CLASS = "finished";
    protected static final String EVENT_DISPLAY_DATE_FORMAT = "EEE d MMMMM";
    protected static final String EVENT_DISPLAY_DATE_FORMAT_ISO = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    protected static final String EVENT_TIME_DEFAULT_FORMAT = "h:mm a";
    protected static final String HOURS_TIME_FORMAT = "h a";
    protected static final String MINUTES_TIME_FORMAT = "mm";
    protected static final String TIME_ZERO_FORMAT = "00";

    protected static final String FIELD_EVENT_LOCATION = "eventLoc";
    protected static final String FIELD_EVENT_START_DATE = "eventStartDate";
    protected static final String FIELD_EVENT_END_DATE = "eventEndDate";
    protected static final String FIELD_SHOW_TAGS = "showTags";

    protected static Calendar DEFAULT_END_DATE = null;
    protected static Calendar DEFAULT_START_DATE = null;

    protected static String DEFAULT_FORMAT_DISPLAYDATE = "${eventStartDateText} to ${eventEndDateText}";
    protected static String DEFAULT_FORMAT_DISPLAYTIME = "${eventStartTimeText} to ${eventEndTimeText}";
    protected static String DEFAULT_FORMAT_SUBTITLE = "${eventStartDateText} to ${eventEndDateText}";

    @Override
    public void ready() {
        super.ready();

        if (Boolean.TRUE.equals(componentProperties.get("isPastEventDate", false))) {
            componentProperties.put(DETAILS_CARD_ADDITIONAL, EVENT_FINISHED_CLASS);
        }
    }

    @Override
    protected void setComponentFieldsDefaults() {
        super.setComponentFieldsDefaults();

        DEFAULT_END_DATE = getResourcePage().getProperties().get(
            NameConstants.PN_OFF_TIME,
            getResourcePage().getProperties().get(JcrConstants.JCR_CREATED, Calendar.getInstance()));

        DEFAULT_START_DATE = getResourcePage().getProperties().get(
            NameConstants.PN_ON_TIME,
            getResourcePage().getProperties().get(JcrConstants.JCR_CREATED, Calendar.getInstance()));
    }

    @Override
    protected void setAdditionalComponentFields() {
        setComponentFields(new Object[][]{
            {FIELD_EVENT_END_DATE, DEFAULT_END_DATE},
            {FIELD_EVENT_START_DATE, DEFAULT_START_DATE},
            {FIELD_EVENT_LOCATION, StringUtils.EMPTY},
            {"eventRefLabel", StringUtils.EMPTY},
            {"eventRefLink", StringUtils.EMPTY},
            {"eventRefLabel2", StringUtils.EMPTY},
            {"eventRefLink2", StringUtils.EMPTY},
            {"useParentPageTitle", false},
            {"subTitleFormat", StringUtils.EMPTY},
            {"eventDisplayDateFormat", StringUtils.EMPTY},
            {"eventDisplayTimeFormat", StringUtils.EMPTY},
            {"eventTimeFormat", EVENT_TIME_DEFAULT_FORMAT},
            {"menuColor", StringUtils.EMPTY},
            {FIELD_SHOW_TAGS, false},
        });
    }

    @Override
    protected void processAdditionalComponentFields(
        ComponentProperties componentProperties,
        I18n i18n,
        SlingScriptHelper sling,
        Map<String, Object> newFields
    ) {
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
            selectedEventTimeFormat = getTagValueAsAdmin(selectedEventTimeFormat, sling);
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

        newFields.put("subTitleFormatted", compileComponentMessage(
            "subTitleFormat",
            DEFAULT_FORMAT_SUBTITLE,
            componentProperties,
            sling));

        newFields.put("eventDisplayDateFormatted", compileComponentMessage(
            "eventDisplayDateFormat",
            DEFAULT_FORMAT_DISPLAYDATE,
            componentProperties,
            sling));

        newFields.put("eventDisplayTimeFormatted", compileComponentMessage(
            "eventDisplayTimeFormat",
            DEFAULT_FORMAT_DISPLAYTIME,
            componentProperties,
            sling));
    }
}
