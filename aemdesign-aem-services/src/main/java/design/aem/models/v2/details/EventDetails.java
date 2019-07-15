package design.aem.models.v2.details;

import com.day.cq.i18n.I18n;
import com.day.cq.tagging.TagConstants;
import com.day.cq.wcm.api.NameConstants;
import design.aem.components.ComponentProperties;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.jackrabbit.vault.util.JcrConstants;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static design.aem.utils.components.CommonUtil.*;
import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.ConstantsUtil.*;
import static design.aem.utils.components.I18nUtil.getDefaultLabelIfEmpty;
import static design.aem.utils.components.TagUtil.getTagValueAsAdmin;
import static design.aem.utils.components.TagUtil.getTagsAsAdmin;

public class EventDetails extends GenericDetails {
    protected static final Logger LOGGER = LoggerFactory.getLogger(EventDetails.class);

    // default values for the component
    final String DEFAULT_TITLE = "Event Title";
    final String DEFAULT_DESCRIPTION = "";
    final String DEFAULT_HIDE_SITE_TITLE = "false";
    final String DEFAULT_HIDE_SEPARATOR = "false";
    final String DEFAULT_HIDE_SUMMARY = "false";
    final String EVENT_DISPLAY_DATE_FORMAT = "EEE d MMMMM";
    final String EVENT_DISPLAY_DATE_FORMAT_ISO = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    final String EVENT_TIME_DEFAULT_FORMAT = "h:mm a";
    final String HOURS_TIME_FORMAT = "h a";
    final String MINUTES_TIME_FORMAT = "mm";
    final String TIME_ZERO_FORMAT = "00";
    final Calendar DEFAULT_EVENT_START_DATE = Calendar.getInstance();
    final Calendar DEFAULT_EVENT_END_DATE = Calendar.getInstance();
    final String DEFAULT_EVENT_LOC = "";
    final String DEFAULT_EVENT_REF_LABEL = "";
    final String DEFAULT_EVENT_REF_LINK = "";
    final String DEFAULT_META_DATA_SEP = "";
    final Boolean DEFAULT_SHOW_BREADCRUMB = true;
    final Boolean DEFAULT_SHOW_TOOLBAR = true;
    final String DEFAULT_FORMAT_TITLE = "${title}";
    final String FIELD_FORMAT_TITLE = "titleFormat";
    final String FIELD_FORMATTED_TITLE = "titleFormatted";
    final String FIELD_FORMATTED_TITLE_TEXT = "titleFormattedText";
    final String DEFAULT_FORMAT_SUBTITLE = "${eventStartDateText} to ${eventEndDateText}";
    final String DEFAULT_FORMAT_DISPLAYDATE = "${eventStartDateText} to ${eventEndDateText}";
    final String DEFAULT_FORMAT_DISPLAYTIME = "${eventStartTimeText} to ${eventEndTimeText}";
    final String I18N_CATEGORY = "event-detail";

    final String I18N_READMORE = "readMoreAboutText";
    final String I18N_FILTERBYTEXT = "filterByText";
    final String SECONDARY_IMAGE_PATH = "article/par/event-details/secondaryImage";

    final String COMPONENT_DETAILS_NAME = "event-details";
    final String componentPath = "./"+PATH_DEFAULT_CONTENT+"/" + COMPONENT_DETAILS_NAME;

    final String FIELD_EVENT_START_DATE = "eventStartDate";
    final String FIELD_EVENT_END_DATE = "eventEndDate";


    @Override
    @SuppressWarnings("Duplicates")
    protected void ready() {
        I18n i18n = new I18n(getRequest());

        final String DEFAULT_ARIA_ROLE = "banner";
        final String DEFAULT_TITLE_TAG_TYPE = "h1";
        final String DEFAULT_I18N_CATEGORY = "event-detail";
        final String DEFAULT_I18N_LABEL = "variantHiddenLabel";

        // default values for the component
        final String DEFAULT_TITLE = getPageTitle(getResourcePage(), getResource());
        final String DEFAULT_DESCRIPTION = getResourcePage().getDescription();
        final String DEFAULT_SUBTITLE = getResourcePage().getProperties().get(FIELD_PAGE_TITLE_SUBTITLE,"");
        final Boolean DEFAULT_HIDE_TITLE = false;
        final Boolean DEFAULT_HIDE_DESCRIPTION = false;
        final Boolean DEFAULT_SHOW_BREADCRUMB = true;
        final Boolean DEFAULT_SHOW_TOOLBAR = true;
        final Boolean DEFAULT_SHOW_PAGE_DATE = true;
        final Boolean DEFAULT_SHOW_PARSYS = true;

        //not using lamda is available so this is the best that can be done
        setComponentFields(new Object[][]{
                {FIELD_VARIANT, DEFAULT_VARIANT},
                {"title", DEFAULT_TITLE},
                {"description", DEFAULT_DESCRIPTION},
                {FIELD_EVENT_START_DATE, getResourcePage().getProperties().get(NameConstants.PN_ON_TIME, getResourcePage().getProperties().get(JcrConstants.JCR_CREATED,Calendar.getInstance()))},
                {FIELD_EVENT_END_DATE, getResourcePage().getProperties().get(NameConstants.PN_OFF_TIME, getResourcePage().getProperties().get(JcrConstants.JCR_CREATED,Calendar.getInstance()))},
                {"eventLoc", ""},
                {"eventRefLabel", ""},
                {"eventRefLink", ""},
                {"eventRefLabel2", ""},
                {"eventRefLink2", ""},
                {"useParentPageTitle", false},
                {"showBreadcrumb", DEFAULT_SHOW_BREADCRUMB},
                {"showToolbar", DEFAULT_SHOW_TOOLBAR},
                {FIELD_FORMAT_TITLE,""},
                {"subTitleFormat",""},
                {"eventDisplayDateFormat",""},
                {"eventDisplayTimeFormat", ""},
                {"eventTimeFormat", EVENT_TIME_DEFAULT_FORMAT},
                {"cq:tags", new String[]{}},
                {"menuColor", StringUtils.EMPTY},
                {"showTags", false},
                {"subCategory", StringUtils.EMPTY},
                {FIELD_PAGE_URL, getPageUrl(getResourcePage())},
                {FIELD_PAGE_TITLE, DEFAULT_TITLE},
                {FIELD_PAGE_TITLE_NAV, getPageNavTitle(getResourcePage())},
                {FIELD_TITLE_TAG_TYPE, DEFAULT_TITLE_TAG_TYPE},
                {"variantHiddenLabel", getDefaultLabelIfEmpty("",DEFAULT_I18N_CATEGORY,DEFAULT_I18N_LABEL,DEFAULT_I18N_CATEGORY,i18n)},
        });

        componentProperties = ComponentsUtil.getComponentProperties(
                this,
                componentFields,
                DEFAULT_FIELDS_STYLE,
                DEFAULT_FIELDS_ACCESSIBILITY,
                DEFAULT_FIELDS_ANALYTICS,
                DEFAULT_FIELDS_DETAILS_OPTIONS);

        String[] tags = componentProperties.get(TagConstants.PN_TAGS, new String[]{});
        componentProperties.put("category",getTagsAsAdmin(getSlingScriptHelper(), tags, getRequest().getLocale()));

        componentProperties.putAll(processComponentFields(componentProperties,i18n,getSlingScriptHelper()), false);

        if (componentProperties.get("isPastEventDate", false)) {
            componentProperties.put(DETAILS_CARD_ADDITIONAL, "finished");
        }

        processCommonFields();
    }

    /***
     * substitute formatted field template with fields from component.
     * @param componentProperties source map with fields
     * @param i18n
     * @param sling
     * @return returns map with new values
     */
    @SuppressWarnings("Duplicates")
    public Map<String, Object> processComponentFields(ComponentProperties componentProperties, I18n i18n, SlingScriptHelper sling) {
        Map<String, Object> newFields = new HashMap<>();

        String formattedTitle = compileComponentMessage(FIELD_FORMAT_TITLE, DEFAULT_FORMAT_TITLE, componentProperties, sling);
        Document fragment = Jsoup.parse(formattedTitle);
        String formattedTitleText = fragment.text();

        newFields.put(FIELD_FORMATTED_TITLE,
                formattedTitle.trim()
        );
        newFields.put(FIELD_FORMATTED_TITLE_TEXT,
                formattedTitleText.trim()
        );

        Calendar eventStartDate = componentProperties.get(FIELD_EVENT_START_DATE,Calendar.getInstance());
        Calendar eventEndDate = componentProperties.get(FIELD_EVENT_END_DATE,Calendar.getInstance());
        String selectedEventTimeFormat = componentProperties.get("eventTimeFormat", StringUtils.EMPTY);

        newFields.put("isPastEventDate", eventEndDate.before(Calendar.getInstance()));

        newFields.put(FIELD_EVENT_START_DATE,eventStartDate);
        newFields.put(FIELD_EVENT_END_DATE,eventEndDate);

        FastDateFormat dateFormatString = FastDateFormat.getInstance(EVENT_DISPLAY_DATE_FORMAT_ISO);

        Date startDateTime = eventStartDate.getTime();
        Date endDateTime = eventEndDate.getTime();

        newFields.put("eventStartDateISO",dateFormatString.format(startDateTime));
        newFields.put("eventEndDateISO",dateFormatString.format(endDateTime));

        FastDateFormat dateFormat = FastDateFormat.getInstance(EVENT_DISPLAY_DATE_FORMAT);
        String eventStartDateText = dateFormat.format(startDateTime);
        String eventEndDateText = dateFormat.format(endDateTime);

        String eventStartDateUppercase = dateFormat.format(startDateTime).toUpperCase();
        String eventEndDateUppercase = dateFormat.format(endDateTime).toUpperCase();

        String eventStartDateLowercase = dateFormat.format(startDateTime).toLowerCase();
        String eventEndDateLowercase = dateFormat.format(endDateTime).toLowerCase();

        newFields.put("eventStartDateText",eventStartDateText);
        newFields.put("eventEndDateText",eventEndDateText);

        newFields.put("eventStartDateUppercase",eventStartDateUppercase);
        newFields.put("eventEndDateUppercase",eventEndDateUppercase);

        newFields.put("eventStartDateLowercase",eventStartDateLowercase);
        newFields.put("eventEndDateLowercase",eventEndDateLowercase);

        if(!selectedEventTimeFormat.equals(EVENT_TIME_DEFAULT_FORMAT)){
            selectedEventTimeFormat = getTagValueAsAdmin(selectedEventTimeFormat,sling);
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

        newFields.put("eventStartTimeText",eventStartTimeText);
        newFields.put("eventEndTimeText",eventEndTimeText);

        newFields.put("eventStartTimeUppercase",eventStartTimeText.toUpperCase());
        newFields.put("eventEndTimeUppercase",eventEndTimeText.toUpperCase());

        newFields.put("eventStartTimeLowercase",eventStartTimeText.toLowerCase());
        newFields.put("eventEndTimeLowercase",eventEndTimeText.toLowerCase());

        newFields.put("eventStartTimeMinFormatted",eventStartTimeMinFormatted);
        newFields.put("eventEndTimeMinFormatted",eventEndTimeMinFormatted);

        newFields.put("eventStartTimeMinLowerFormatted",eventStartTimeMinFormatted.toLowerCase());
        newFields.put("eventEndTimeMinLowerFormatted",eventEndTimeMinFormatted.toLowerCase());

        newFields.put("eventStartTimeMinUpperFormatted",eventStartTimeMinFormatted.toUpperCase());
        newFields.put("eventEndTimeMinUpperFormatted",eventEndTimeMinFormatted.toUpperCase());

        componentProperties.putAll(newFields);

        newFields.put("titleFormatted",compileComponentMessage(FIELD_FORMAT_TITLE,DEFAULT_FORMAT_TITLE,componentProperties,sling));
        newFields.put("subTitleFormatted",compileComponentMessage("subTitleFormat",DEFAULT_FORMAT_SUBTITLE,componentProperties,sling));
        newFields.put("eventDisplayDateFormatted",compileComponentMessage("eventDisplayDateFormat",DEFAULT_FORMAT_DISPLAYDATE,componentProperties,sling));
        newFields.put("eventDisplayTimeFormatted",compileComponentMessage("eventDisplayTimeFormat",DEFAULT_FORMAT_DISPLAYTIME,componentProperties,sling));

        return newFields;
    }
}
