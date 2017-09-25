<%!

    final String DEFAULT_FORMAT_TITLE = "${title}";
    final String FIELD_FORMAT_TITLE = "titleFormat";
    final String FIELD_FORMATTED_TITLE = "titleFormatted";

    public Map processComponentFields(ComponentProperties componentProperties, com.day.cq.i18n.I18n i18n, SlingScriptHelper sling){
        Map newFields = new HashMap();

        newFields.put(FIELD_FORMATTED_TITLE,
                compileComponentMessage(FIELD_FORMAT_TITLE,DEFAULT_FORMAT_TITLE,componentProperties,sling)
        );

        return newFields;
    }
%>