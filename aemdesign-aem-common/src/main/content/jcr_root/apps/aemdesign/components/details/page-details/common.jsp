<%!

    final String COMPONENT_DETAILS_NAME = "page-details";
    final String componentPath = "./" + PATH_DEFAULT_CONTENT + "/" + COMPONENT_DETAILS_NAME;

    final String DEFAULT_FORMAT_TITLE = "${title}";
    final String FIELD_FORMAT_TITLE = "titleFormat";
    final String FIELD_FORMATTED_TITLE = "titleFormatted";
    final String I18N_CATEGORY = "page-detail";
    final String I18N_READMORE = "readMoreAboutText";
    final String I18N_FILTERBYTEXT = "filterByText";
    final String PAGE_CONTENT_SECONDARY_IMAGE_PATH = "article/par/page-details/secondaryImage";

    public Map processComponentFields(ComponentProperties componentProperties, com.day.cq.i18n.I18n i18n, SlingScriptHelper sling){
        Map newFields = new HashMap();

        newFields.put(FIELD_FORMATTED_TITLE,
                compileComponentMessage(FIELD_FORMAT_TITLE,DEFAULT_FORMAT_TITLE,componentProperties,sling)
        );

        return newFields;
    }


%>