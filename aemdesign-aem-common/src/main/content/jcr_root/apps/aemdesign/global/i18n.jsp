<%@ page import="com.day.cq.i18n.I18n" %>
<%@page session="false"%>
<%!

    public static String LANGUAGE_TAG_PATH = "/etc/tags/language";
    public static String LANGUAGE_DEFAULT = Locale.ENGLISH.getLanguage();
    public static String LANGUAGE_DEFAULT_LABEL = "Missing Label";


    public static String getDefaultLabelIfEmpty(String currentLabel, String currentCategory, String defaultCode, String defaultCategory, com.day.cq.i18n.I18n i18n, String... params) {
        String label = "";
        if (StringUtils.isEmpty(currentLabel)) {
            label = i18n.get(defaultCode, defaultCategory, params);
        } else {
            label = i18n.get(currentLabel, currentCategory, params);
        }

        if (isEmpty(label)) {
            label = LANGUAGE_DEFAULT_LABEL;
        }

        return label;
    }


    public String getPageLanguage(SlingScriptHelper sling, Page page) throws RepositoryException {
        /**
         * Locale in BCP 47
         * http://www.w3.org/International/articles/bcp47/
         */
        String bcp47Lang = page.getLanguage(true).getLanguage();

        ResourceResolver adminResourceResolver  = openAdminResourceResolver(sling);

        try {

            //try to get an override value for language as per BCP47 spec
            Resource rootLanguage = adminResourceResolver.getResource(LANGUAGE_TAG_PATH);

            String localeString = LANGUAGE_DEFAULT;
            Locale pageLocale = page.getLanguage(true);
            if (pageLocale != null) {
                localeString = pageLocale.toString();
            }

            Resource res = rootLanguage.getChild(localeString);

            //get default if not found
            if (res == null) {
                res = rootLanguage.getChild(LANGUAGE_DEFAULT);
            }

            Tag lang = res.adaptTo(Tag.class);

            if (lang != null){
                Node langProperty = lang.adaptTo(Node.class);

                if (langProperty.hasProperty(TAG_VALUE)) {
                    bcp47Lang = langProperty.getProperty(TAG_VALUE).getValue().getString();
                } else {
                    bcp47Lang = lang.getName();
                }
            }

        } catch (Exception ex) {
            throw ex;
        } finally {
            closeAdminResourceResolver(adminResourceResolver);
        }
        return bcp47Lang;
    }

%>
