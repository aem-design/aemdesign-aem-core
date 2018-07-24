<%!

    final String COMPONENT_DETAILS_NAME = "page-details";
    final String componentPath = "./" + PATH_DEFAULT_CONTENT + "/" + COMPONENT_DETAILS_NAME;

    final String DEFAULT_FORMAT_TITLE = "${title}";
    final String FIELD_FORMAT_TITLE = "titleFormat";
    final String FIELD_FORMATTED_TITLE = "titleFormatted";
    final String FIELD_FORMATTED_TITLE_TEXT = "titleFormattedText";
    final String I18N_CATEGORY = "page-detail";
    final String I18N_READMORE = "readMoreAboutText";
    final String I18N_FILTERBYTEXT = "filterByText";
    final String PAGE_CONTENT_SECONDARY_IMAGE_PATH = "article/par/page-details/secondaryImage";

    /***
     * substitute formatted field template with fields from component
     * @param componentProperties source map with fields
     * @param i18n
     * @param sling
     * @return returns map with new values
     */
    public Map processComponentFields(ComponentProperties componentProperties, com.day.cq.i18n.I18n i18n, SlingScriptHelper sling){
        Map newFields = new HashMap();

        try {

            String formattedTitle = compileComponentMessage(FIELD_FORMAT_TITLE, DEFAULT_FORMAT_TITLE, componentProperties, sling);
            Document fragment = Jsoup.parse(formattedTitle);
            String formattedTitleText = fragment.text();

            newFields.put(FIELD_FORMATTED_TITLE,
                    formattedTitle.trim()
            );
            newFields.put(FIELD_FORMATTED_TITLE_TEXT,
                    formattedTitleText.trim()
            );

        } catch (Exception ex) {
            getLogger().error("Could not process component fields in " + COMPONENT_DETAILS_NAME);
        }
        return newFields;
    }

%>