<%@ page import="com.day.cq.wcm.api.Page" %>
<%@ page import="com.day.cq.i18n.I18n" %>

<%!
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

                            aLanguage.put("path", mappedUrl(resourceResolver, p.getPath()) + ".html");
                            aLanguage.put("langSwitchTo", i18n.get("langSwitchTo"+language.getLocale().toString(), "langSelect"));
                            aLanguage.put("langSimpleTo", i18n.get("langSimpleTo"+language.getLocale().toString(), "langSelect"));

                            String hrefLang = ConvertLocaleToLanguageBcp47(resourceResolver, language);
                            aLanguage.put("hreflang", hrefLang);

                            languageToggleMap.put(language.getLocale(), aLanguage);
                        }
                    } else {

                        Page p = pageManager.getPage(information.getPath());

                        Map<String, String> aLanguage = new HashMap<String, String>();
                        aLanguage.put("langSwitchTo", i18n.get("langSwitchTo"+language.getLocale().toString(), "langSelect"));
                        aLanguage.put("langSimpleTo", i18n.get("langSimpleTo"+language.getLocale().toString(), "langSelect"));
                        aLanguage.put("hreflang", ConvertLocaleToLanguageBcp47(resourceResolver, language));

                        if (p != null && p.isValid() && !p.isHideInNav()){
                            aLanguage.put("path", mappedUrl(resourceResolver, p.getPath()) + ".html");
                        }else{
                            p = findMissingLanguagePath(currentPage, resourceResolver, languageManager, isShowRoot, language,  pageManager);
                            if (p != null) {
                                aLanguage.put("path", mappedUrl(resourceResolver, p.getPath()) + ".html");
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