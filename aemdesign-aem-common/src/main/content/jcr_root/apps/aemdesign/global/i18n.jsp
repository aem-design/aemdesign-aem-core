<%@ page import="com.day.cq.i18n.I18n" %>
<%@ page import="com.day.cq.wcm.api.Page" %>
<%@ page import="com.day.cq.commons.Language" %>
<%@page session="false"%>
<%!

    public static String LANGUAGE_TAG_PATH = "/etc/tags/language";
    public static String LANGUAGE_DEFAULT = Locale.ENGLISH.getLanguage();
    public static String LANGUAGE_DEFAULT_LABEL = "Missing Label";

    public static String DEFAULT_I18N_LIST_LINK_TITLE = "listLinkTitle";
    public static String DEFAULT_I18N_LIST_LINK_TEXT = "listLinkText";

    public static String DEFAULT_I18N_LIST_ITEM_LINK_TITLE = "listItemLinkTitle";
    public static String DEFAULT_I18N_LIST_ITEM_LINK_TEXT = "listItemLinkText";

    public static String DEFAULT_I18N_LIST_ITEM_TITLE_LENGTH_MAX = "listItemTitleLengthMax";
    public static String DEFAULT_I18N_LIST_ITEM_TITLE_LENGTH_MAX_SUFFIX = "listItemTitleLengthMaxSuffix";

    public static String DEFAULT_I18N_LIST_ITEM_SUMMARY_LENGTH_MAX = "listItemSummaryLengthMax";
    public static String DEFAULT_I18N_LIST_ITEM_SUMMARY_LENGTH_MAX_SUFFIX = "listItemSummaryLengthMaxSuffix";

    public static final String DEFAULT_I18N_INHERIT_CATEGORY = "inherit";
    public static final String DEFAULT_I18N_INHERIT_LABEL_PARENTNOTFOUND = "parentnotfound";



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


    public String getPageLanguage(SlingScriptHelper sling, Page page) {
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

            if (res == null) {
                LOG.error("default language node is missing");
                return "en";
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
            LOG.error("getPageLanguage: error {}", ex.toString());
        } finally {
            closeAdminResourceResolver(adminResourceResolver);
        }
        return bcp47Lang;
    }

    /**
     * Convert String Language such as ru, de-ch to Language Object
     * @param tagsMap
     * @param separator
     * @return
     */
    private Set<Language> convertLanguageStringToLanguageSet(String tagsMap, final String separator){

        Set<Language> languageSet = new LinkedHashSet<Language>();

        if (StringUtils.isNotEmpty(tagsMap)){

            String [] tagMapArray = StringUtils.split(tagsMap, separator);

            for (String tag : tagMapArray){

                languageSet.add(new Language(tag));
            }
        }

        return languageSet;
    }

    private Map<Locale, Map<String, String>> getLanguageList(SlingScriptHelper sling, Set<Language> languageSet ,  Page currentPage,  LanguageManager languageManager, boolean isShowRoot, PageManager pageManager, I18n i18n ) {
        Map<Locale, Map<String, String>> languageToggleMap = new LinkedHashMap<Locale, Map<String, String>>();
        ResourceResolver adminResourceResolver  = openAdminResourceResolver(sling);
        try {
            languageToggleMap = getLanguageList(languageSet,currentPage,adminResourceResolver,languageManager,isShowRoot,pageManager,i18n);
        } catch (Exception ex) {
            LOG.error("event-details: " + ex.getMessage(), ex);
            //out.write( Throwables.getStackTraceAsString(ex) );
        } finally {
            closeAdminResourceResolver(adminResourceResolver);
        }
        return languageToggleMap;
    }

    /**
     * Get the Language List according the set of Languages and its ordering
     * @param languageSet
     * @param currentPage
     * @param resourceResolver
     * @param languageManager
     * @param isShowRoot
     * @param pageManager
     * @param i18n
     * @return Map<Locale, Map<String, String>>
     */
    private Map<Locale, Map<String, String>> getLanguageList(Set<Language> languageSet ,  Page currentPage, ResourceResolver resourceResolver, LanguageManager languageManager, boolean isShowRoot, PageManager pageManager, I18n i18n ){

        Map<Locale, Map<String, String>> languageToggleMap = new LinkedHashMap<Locale, Map<String, String>>();

        if (languageSet != null && languageSet.size() > 0){

            Map<Language,LanguageManager.Info> adjacentLang = languageManager.getAdjacentLanguageInfo(resourceResolver, currentPage.getPath());




            for (Language language : languageSet){


                for (Language l : adjacentLang.keySet()){
                    if (l.getLocale().equals(language.getLocale())){
                        language = l;
                    }
                }

                LanguageManager.Info information = adjacentLang.get(language);

                if (information != null){

                    if (!information.exists() || !information.hasContent()){

                        Page p = findMissingLanguagePath(currentPage, resourceResolver, languageManager, isShowRoot, language,  pageManager);

                        if (p != null){
                            Map<String, String> aLanguage = new HashMap<String, String>();

                            aLanguage.put("path", mappedUrl(resourceResolver, p.getPath()).concat(DEFAULT_EXTENTION));
                            aLanguage.put("langSwitchTo", i18n.get("langSwitchTo_"+language.getLocale().toString(), "langSelect"));
                            aLanguage.put("langSimpleTo", i18n.get("langSimpleTo_"+language.getLocale().toString(), "langSelect"));

                            String hrefLang = ConvertLocaleToLanguageBcp47(resourceResolver, language);
                            aLanguage.put("hreflang", hrefLang);

                            languageToggleMap.put(language.getLocale(), aLanguage);
                        }
                    } else {

                        Page p = pageManager.getPage(information.getPath());

                        Map<String, String> aLanguage = new HashMap<String, String>();
                        aLanguage.put("langSwitchTo", i18n.get("langSwitchTo_"+language.getLocale().toString(), "langSelect"));
                        aLanguage.put("langSimpleTo", i18n.get("langSimpleTo_"+language.getLocale().toString(), "langSelect"));
                        aLanguage.put("hreflang", ConvertLocaleToLanguageBcp47(resourceResolver, language));

                        if (p != null && p.isValid() && !p.isHideInNav()){
                            aLanguage.put("path", mappedUrl(resourceResolver, p.getPath()).concat(DEFAULT_EXTENTION));
                        }else{
                            p = findMissingLanguagePath(currentPage, resourceResolver, languageManager, isShowRoot, language,  pageManager);
                            if (p != null) {
                                aLanguage.put("path", mappedUrl(resourceResolver, p.getPath()).concat(DEFAULT_EXTENTION));
                            }
                        }

                        languageToggleMap.put(language.getLocale(), aLanguage);
                    }
                }

            }

        }

        return languageToggleMap;
    }


    private String ConvertLocaleToLanguageBcp47(ResourceResolver resourceResolver , Language language){

        String bcp47Lang = "";

        Resource rootLanguage = resourceResolver.getResource("/etc/tags/language");

        Iterator iterator = rootLanguage.listChildren();

        while (iterator.hasNext()){
            Resource res = (Resource)iterator.next();
            Tag lang = res.adaptTo(Tag.class);
            if (lang != null && lang.getName().equals(language.getLocale().toString())){
                Node langProperty = lang.adaptTo(Node.class);
                try{

                    if (langProperty != null && langProperty.hasProperty("value")){
                        bcp47Lang = langProperty.getProperty("value").getValue().getString();
                    }
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        }

        return bcp47Lang;
    }

    private String ConvertLocaleToLanguageBcp47(SlingScriptHelper sling, Language language){
        ResourceResolver adminResourceResolver  = openAdminResourceResolver(sling);
        String returnS = "";
        try {
            returnS = ConvertLocaleToLanguageBcp47(adminResourceResolver,language);
        } catch (Exception ex) {
            LOG.error("event-details: " + ex.getMessage(), ex);
            //out.write( Throwables.getStackTraceAsString(ex) );
        } finally {
            closeAdminResourceResolver(adminResourceResolver);
        }

        return returnS;
    }

    private Page findMissingLanguagePath(Page currentPage, ResourceResolver resourceResolver, LanguageManager languageManager, boolean isShowRoot, Language targetLanguage, PageManager pageManager){

        Page page = currentPage.getParent();

        if (page != null){
            Map<Language,LanguageManager.Info> adjacentLang = languageManager.getAdjacentLanguageInfo(resourceResolver, page.getPath());

            if (adjacentLang!=null && adjacentLang.size() > 0 ) {
                for (Language language : adjacentLang.keySet()) {

                    LanguageManager.Info info = adjacentLang.get(language);

                    if (info == null) continue;

                    Page p = pageManager.getPage(info.getPath());

                    if (language.getLocale().equals(targetLanguage.getLocale())) {
                        if (isShowRoot) {
                            if (p == null || p.isHideInNav() || !p.isValid()) {
                                page = findMissingLanguagePath(page, resourceResolver, languageManager, isShowRoot, targetLanguage, pageManager);
                            } else {
                                Resource res = pageManager.getPage(info.getPath()).adaptTo(Resource.class);
                                page = languageManager.getLanguageRoot(res);
                            }

                        } else {
                            if (info.exists() && info.hasContent() && p.isValid() && !p.isHideInNav()) {
                                page = pageManager.getPage(info.getPath());
                            } else {
                                page = findMissingLanguagePath(page, resourceResolver, languageManager, isShowRoot, targetLanguage, pageManager);
                            }

                        }
                    }

                }
            }
        }

        return page;
    }
%>
