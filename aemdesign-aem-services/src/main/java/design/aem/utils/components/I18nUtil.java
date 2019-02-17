package design.aem.utils.components;

import com.day.cq.commons.Language;
import com.day.cq.i18n.I18n;
import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.LanguageManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.sling.xss.XSSAPI;

import javax.jcr.Node;
import java.util.*;

import static design.aem.utils.components.SecurityUtil.closeAdminResourceResolver;
import static design.aem.utils.components.SecurityUtil.openAdminResourceResolver;
import static design.aem.utils.components.TagUtil.TAG_VALUE;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class I18nUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(I18nUtil.class);

    public static String LANGUAGE_TAG_PATH = "/content/cq:tags/language";
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
        if (isEmpty(currentLabel)) {
            label = i18n.get(defaultCode, defaultCategory, params);
        } else {
            label = i18n.get(currentLabel, currentCategory, params);
        }

        if (isEmpty(label)) {
            label = LANGUAGE_DEFAULT_LABEL;
        }

        return label;
    }


    public static String getPageLanguage(SlingScriptHelper sling, Page page) {
        /**
         * Locale in BCP 47
         * http://www.w3.org/International/articles/bcp47/
         */
        String bcp47Lang = page.getLanguage(true).getLanguage();

        ResourceResolver adminResourceResolver = openAdminResourceResolver(sling);

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
                LOGGER.error("default language node is missing");
                return "en";
            }
            Tag lang = res.adaptTo(Tag.class);

            if (lang != null) {
                Node langProperty = lang.adaptTo(Node.class);

                if (langProperty.hasProperty(TAG_VALUE)) {
                    bcp47Lang = langProperty.getProperty(TAG_VALUE).getValue().getString();
                } else {
                    bcp47Lang = lang.getName();
                }
            }

        } catch (Exception ex) {
            LOGGER.error("getPageLanguage: error {}", ex.toString());
        } finally {
            closeAdminResourceResolver(adminResourceResolver);
        }
        return bcp47Lang;
    }

    /**
     * Convert String Language such as ru, de-ch to Language Object.
     * @param tagsMap map of tags
     * @param separator separator
     * @return map of string
     */
    public static Set<Language> convertLanguageStringToLanguageSet(String tagsMap, final String separator) {

        Set<Language> languageSet = new LinkedHashSet<Language>();

        if (StringUtils.isNotEmpty(tagsMap)) {

            String[] tagMapArray = StringUtils.split(tagsMap, separator);

            for (String tag : tagMapArray) {

                languageSet.add(new Language(tag));
            }
        }

        return languageSet;
    }

    public static Map<Locale, Map<String, String>> getLanguageList(SlingScriptHelper sling, Set<Language> languageSet, Page page, LanguageManager languageManager, boolean isShowRoot, PageManager pageManager, I18n i18n) {
        Map<Locale, Map<String, String>> languageToggleMap = new LinkedHashMap<Locale, Map<String, String>>();
        ResourceResolver adminResourceResolver = openAdminResourceResolver(sling);
        try {
            languageToggleMap = getLanguageList(languageSet, page, adminResourceResolver, languageManager, isShowRoot, pageManager, i18n);
        } catch (Exception ex) {
            LOGGER.error("event-details: " + ex.getMessage(), ex);
            //out.write( Throwables.getStackTraceAsString(ex) );
        } finally {
            closeAdminResourceResolver(adminResourceResolver);
        }
        return languageToggleMap;
    }

    /**
     * Get the Language List according the set of Languages and its ordering.
     * @param languageSet language set to use
     * @param page current page
     * @param resourceResolver resource resolver
     * @param languageManager language manager
     * @param isShowRoot show root
     * @param pageManager pae manager
     * @param i18n i18n
     * @return map of languages
     */
    public static Map<Locale, Map<String, String>> getLanguageList(Set<Language> languageSet, Page page, ResourceResolver resourceResolver, LanguageManager languageManager, boolean isShowRoot, PageManager pageManager, I18n i18n) {

        Map<Locale, Map<String, String>> languageToggleMap = new LinkedHashMap<Locale, Map<String, String>>();

        if (languageSet != null && languageSet.size() > 0) {

            Map<Language, LanguageManager.Info> adjacentLang = languageManager.getAdjacentLanguageInfo(resourceResolver, page.getPath());


            for (Language language : languageSet) {


                for (Language l : adjacentLang.keySet()) {
                    if (l.getLocale().equals(language.getLocale())) {
                        language = l;
                    }
                }

                LanguageManager.Info information = adjacentLang.get(language);

                if (information != null) {

                    if (!information.exists() || !information.hasContent()) {

                        Page p = findMissingLanguagePath(page, resourceResolver, languageManager, isShowRoot, language, pageManager);

                        if (p != null) {
                            Map<String, String> aLanguage = new HashMap<String, String>();

                            aLanguage.put("path", ResolverUtil.mappedUrl(resourceResolver, p.getPath()).concat(ConstantsUtil.DEFAULT_EXTENTION));
                            aLanguage.put("langSwitchTo", i18n.get("langSwitchTo_" + language.getLocale().toString(), "langSelect"));
                            aLanguage.put("langSimpleTo", i18n.get("langSimpleTo_" + language.getLocale().toString(), "langSelect"));

                            String hrefLang = ConvertLocaleToLanguageBcp47(resourceResolver, language);
                            aLanguage.put("hreflang", hrefLang);

                            languageToggleMap.put(language.getLocale(), aLanguage);
                        }
                    } else {

                        Page p = pageManager.getPage(information.getPath());

                        Map<String, String> aLanguage = new HashMap<String, String>();
                        aLanguage.put("langSwitchTo", i18n.get("langSwitchTo_" + language.getLocale().toString(), "langSelect"));
                        aLanguage.put("langSimpleTo", i18n.get("langSimpleTo_" + language.getLocale().toString(), "langSelect"));
                        aLanguage.put("hreflang", ConvertLocaleToLanguageBcp47(resourceResolver, language));

                        if (p != null && p.isValid() && !p.isHideInNav()) {
                            aLanguage.put("path", ResolverUtil.mappedUrl(resourceResolver, p.getPath()).concat(ConstantsUtil.DEFAULT_EXTENTION));
                        } else {
                            p = findMissingLanguagePath(page, resourceResolver, languageManager, isShowRoot, language, pageManager);
                            if (p != null) {
                                aLanguage.put("path", ResolverUtil.mappedUrl(resourceResolver, p.getPath()).concat(ConstantsUtil.DEFAULT_EXTENTION));
                            }
                        }

                        languageToggleMap.put(language.getLocale(), aLanguage);
                    }
                }

            }

        }

        return languageToggleMap;
    }


    public static String ConvertLocaleToLanguageBcp47(ResourceResolver resourceResolver, Language language) {

        String bcp47Lang = "";

        Resource rootLanguage = resourceResolver.getResource("/content/cq:tags/language");

        Iterator iterator = rootLanguage.listChildren();

        while (iterator.hasNext()) {
            Resource res = (Resource) iterator.next();
            Tag lang = res.adaptTo(Tag.class);
            if (lang != null && lang.getName().equals(language.getLocale().toString())) {
                Node langProperty = lang.adaptTo(Node.class);
                try {

                    if (langProperty != null && langProperty.hasProperty("value")) {
                        bcp47Lang = langProperty.getProperty("value").getValue().getString();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        return bcp47Lang;
    }

    public static String ConvertLocaleToLanguageBcp47(SlingScriptHelper sling, Language language) {
        ResourceResolver adminResourceResolver = openAdminResourceResolver(sling);
        String returnS = "";
        try {
            returnS = ConvertLocaleToLanguageBcp47(adminResourceResolver, language);
        } catch (Exception ex) {
            LOGGER.error("event-details: " + ex.getMessage(), ex);
            //out.write( Throwables.getStackTraceAsString(ex) );
        } finally {
            closeAdminResourceResolver(adminResourceResolver);
        }

        return returnS;
    }

    public static Page findMissingLanguagePath(Page page, ResourceResolver resourceResolver, LanguageManager languageManager, boolean isShowRoot, Language targetLanguage, PageManager pageManager) {

        Page pageParent = page.getParent();

        if (pageParent != null) {
            Map<Language, LanguageManager.Info> adjacentLang = languageManager.getAdjacentLanguageInfo(resourceResolver, pageParent.getPath());

            if (adjacentLang != null && adjacentLang.size() > 0) {
                for (Language language : adjacentLang.keySet()) {

                    LanguageManager.Info info = adjacentLang.get(language);

                    if (info == null) continue;

                    Page p = pageManager.getPage(info.getPath());

                    if (language.getLocale().equals(targetLanguage.getLocale())) {
                        if (isShowRoot) {
                            if (p == null || p.isHideInNav() || !p.isValid()) {
                                pageParent = findMissingLanguagePath(pageParent, resourceResolver, languageManager, isShowRoot, targetLanguage, pageManager);
                            } else {
                                Resource res = pageManager.getPage(info.getPath()).adaptTo(Resource.class);
                                pageParent = languageManager.getLanguageRoot(res);
                            }

                        } else {
                            if (info.exists() && info.hasContent() && p.isValid() && !p.isHideInNav()) {
                                pageParent = pageManager.getPage(info.getPath());
                            } else {
                                pageParent = findMissingLanguagePath(pageParent, resourceResolver, languageManager, isShowRoot, targetLanguage, pageManager);
                            }

                        }
                    }

                }
            }
        }

        return pageParent;
    }

    /**
     * A shortcut for <code>xssAPI.encodeForHTML(i18n.getVar(text))</code>.
     */
    public static final String outVar(XSSAPI xssAPI, I18n i18n, String text) {
        return xssAPI.encodeForHTML(i18n.getVar(text));
    }

    /**
     * A shortcut for <code>xssAPI.encodeForHTMLAttr(i18n.getVar(text))</code>.
     */
    public static final String outAttrVar(XSSAPI xssAPI, I18n i18n, String text) {
        return xssAPI.encodeForHTMLAttr(i18n.getVar(text));
    }


}
