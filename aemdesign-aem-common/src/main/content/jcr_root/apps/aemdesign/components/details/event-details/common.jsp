<%@ page import="java.text.SimpleDateFormat" %><%!

    // default values for the component
    final String DEFAULT_TITLE = "Event Title";
    final String DEFAULT_DESCRIPTION = "";
    final String DEFAULT_HIDE_SITE_TITLE = "false";
    final String DEFAULT_HIDE_SEPARATOR = "false";
    final String DEFAULT_HIDE_SUMMARY = "false";
    final String DEFAULT_EVENT_DISPLAY_DATE = "";
    final Calendar DEFAULT_EVENT_START_DATE = Calendar.getInstance();
    final Calendar DEFAULT_EVENT_END_DATE = Calendar.getInstance();
    final String DEFAULT_EVENT_TIME = "";
    final String DEFAULT_EVENT_LOC = "";
    final String DEFAULT_EVENT_REF_LABEL = "";
    final String DEFAULT_EVENT_REF_LINK = "";
    final String DEFAULT_META_DATA_SEP = "";
    final Boolean DEFAULT_SHOW_BREADCRUMB = true;
    final Boolean DEFAULT_SHOW_TOOLBAR = true;
    final String DEFAULT_FORMAT_TITLE = "${title}";
    final String DEFAULT_FORMAT_SUBTITLE = "${eventStartDateText} to ${eventEndDateText}";
    final String DEFAULT_FORMAT_DISPLAYDATE = "${eventStartDateText} to ${eventEndDateText}";
    final String DEFAULT_FORMAT_DISPLAYTIME = "${eventStartTimeText} to ${eventEndTimeText}";
    final String I18N_CATEGORY = "event-detail";

    final String COMPONENT_DETAILS_NAME = "page-details";
    final String componentPath = "./"+PATH_DEFAULT_CONTENT+"/" + COMPONENT_DETAILS_NAME;


    public Map processComponentFields(ComponentProperties componentProperties, com.day.cq.i18n.I18n i18n, SlingScriptHelper sling) {
        Map newFields = new HashMap();

        Calendar eventStartDate = componentProperties.get("eventStartDate",Calendar.getInstance());
        Calendar eventEndDate = componentProperties.get("eventEndDate",Calendar.getInstance());

        newFields.put("isPastEventDate", eventEndDate.before(Calendar.getInstance()));

        newFields.put("eventStartDate",eventStartDate);
        newFields.put("eventEndDate",eventEndDate);

        String dateFormatString = i18n.get("dateDisplayFormat",I18N_CATEGORY);
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatString);
        String eventStartDateText = dateFormat.format(eventStartDate.getTime());
        String eventEndDateText = dateFormat.format(eventEndDate.getTime());

        newFields.put("eventStartDateText",eventStartDateText);
        newFields.put("eventEndDateText",eventEndDateText);

        String timeFormatString = i18n.get("timeDisplayFormat",I18N_CATEGORY);
        SimpleDateFormat timeFormat = new SimpleDateFormat(timeFormatString);
        String eventStartTimeText = timeFormat.format(eventStartDate.getTime());
        String eventEndTimeText = timeFormat.format(eventEndDate.getTime());

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
