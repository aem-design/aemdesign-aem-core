<%!

    final String DEFAULT_FORMAT_TITLE = "${title}";
    final String DEFAULT_FORMAT_SUBTITLE = "${subtitle}";

    public Map processComponentFields(ComponentProperties componentProperties, com.day.cq.i18n.I18n i18n, SlingScriptHelper sling){
        Map newFields = new HashMap();

        newFields.put("titleFormatted",compileComponentMessage("titleFormat",DEFAULT_FORMAT_TITLE,componentProperties,sling));

        return newFields;
    }
%>