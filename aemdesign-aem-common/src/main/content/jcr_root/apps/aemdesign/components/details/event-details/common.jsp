<%@ page import="java.text.SimpleDateFormat" %><%!

    // default values for the component
    final String DEFAULT_TITLE = "Event Title";
    final String DEFAULT_DESCRIPTION = "";
    final String DEFAULT_HIDE_SITE_TITLE = "false";
    final String DEFAULT_HIDE_SEPARATOR = "false";
    final String DEFAULT_HIDE_SUMMARY = "false";
    final String EVENT_DISPLAY_DATE_FORMAT = "EEE d MMMMM";
    final String EVENT_DISPLAY_TIME_FORMAT = "ha";
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

    /***
     * substitute formatted field template with fields from component
     * @param componentProperties source map with fields
     * @param i18n
     * @param sling
     * @return returns map with new values
     */

    public Map processComponentFields(ComponentProperties componentProperties, com.day.cq.i18n.I18n i18n, SlingScriptHelper sling) {
        Map newFields = new HashMap();

        String formattedTitle = compileComponentMessage(FIELD_FORMAT_TITLE, DEFAULT_FORMAT_TITLE, componentProperties, sling);
        Document fragment = Jsoup.parse(formattedTitle);
        String formattedTitleText = fragment.text();

        newFields.put(FIELD_FORMATTED_TITLE,
                formattedTitle.trim()
        );
        newFields.put(FIELD_FORMATTED_TITLE_TEXT,
                formattedTitleText.trim()
        );
        Calendar eventStartDate = componentProperties.get("eventStartDate",Calendar.getInstance());
        Calendar eventEndDate = componentProperties.get("eventEndDate",Calendar.getInstance());

        newFields.put("isPastEventDate", eventEndDate.before(Calendar.getInstance()));

        newFields.put("eventStartDate",eventStartDate);
        newFields.put("eventEndDate",eventEndDate);

        SimpleDateFormat dateFormat = new SimpleDateFormat(EVENT_DISPLAY_DATE_FORMAT);
        String eventStartDateText = dateFormat.format(eventStartDate.getTime());
        String eventEndDateText = dateFormat.format(eventEndDate.getTime());

        newFields.put("eventStartDateText",eventStartDateText);
        newFields.put("eventEndDateText",eventEndDateText);

        SimpleDateFormat timeFormat = new SimpleDateFormat(EVENT_DISPLAY_TIME_FORMAT);
        String eventStartTimeText = timeFormat.format(eventStartDate.getTime()).toLowerCase();
        String eventEndTimeText = timeFormat.format(eventEndDate.getTime()).toLowerCase();

        newFields.put("eventStartTimeText",eventStartTimeText);
        newFields.put("eventEndTimeText",eventEndTimeText);

        componentProperties.putAll(newFields);


        newFields.put("titleFormatted",compileComponentMessage("titleFormat",DEFAULT_FORMAT_TITLE,componentProperties,sling));
        newFields.put("subTitleFormatted",compileComponentMessage("subTitleFormat",DEFAULT_FORMAT_SUBTITLE,componentProperties,sling));
        newFields.put("eventDisplayDateFormatted",compileComponentMessage("eventDisplayDateFormat",DEFAULT_FORMAT_DISPLAYDATE,componentProperties,sling));
        newFields.put("eventDisplayTimeFormatted",compileComponentMessage("eventDisplayTimeFormat",DEFAULT_FORMAT_DISPLAYTIME,componentProperties,sling));


        return newFields;
    }
%>
