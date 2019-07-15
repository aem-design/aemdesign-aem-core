package design.aem.models.v2.lists;

import com.day.cq.commons.LanguageUtil;
import com.day.cq.wcm.api.Page;
import design.aem.components.ComponentProperties;
import design.aem.models.ModelProxy;
import design.aem.utils.components.ComponentsUtil;
import design.aem.utils.components.ConstantsUtil;
import design.aem.utils.components.ResolverUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.TagUtil.getTagsAsAdmin;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class LanguageNavigation extends ModelProxy {
    protected static final Logger LOGGER = LoggerFactory.getLogger(LanguageNavigation.class);

    protected ComponentProperties componentProperties = null;
    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }

    private static final String SEARCH_LOGIC = "searchlogic";
    private static final String SEARCH_LOGIC_DEFAULT = "";

    @SuppressWarnings("Duplicates")
    protected void ready() {
        /**
         * Component Fields Helper
         *
         * Structure:
         * 1 required - property name,
         * 2 required - default value,
         * 3 optional - name of component attribute to add value into
         * 4 optional - canonical name of class for handling multivalues, String or Tag
         */
        setComponentFields(new Object[][]{
                {FIELD_VARIANT, DEFAULT_VARIANT},
                {"languageSet", new String[]{}},
                {SEARCH_LOGIC, SEARCH_LOGIC_DEFAULT}
        });

        componentProperties = ComponentsUtil.getComponentProperties(
                this,
                componentFields,
                DEFAULT_FIELDS_STYLE,
                DEFAULT_FIELDS_ACCESSIBILITY);

        Map<String, Map<String, String>> languageToggleMap = new LinkedHashMap<String, Map<String, String>>();

        String appearanceOption = componentProperties.get("searchlogic", String.class);

        boolean isShowRoot =  ("showRoot").equals(appearanceOption);

        boolean isShowNothing =  ("showNothing").equals(appearanceOption);

        String[] tagsFilterList = componentProperties.get("languageSet", new String[]{});

        LinkedHashMap<String, Map> languageMap = getTagsAsAdmin(getSlingScriptHelper(), tagsFilterList, getRequest().getLocale());

        if (isShowNothing == false && languageMap != null || !languageMap.isEmpty()){

            //get info on current page
            Page currentPage = getResourcePage();
            String languageRoot = "";
            String pagePath = "";
            if (currentPage != null) {
                pagePath = currentPage.getPath();
                languageRoot = LanguageUtil.getLanguageRoot(getResourcePage().getPath());

                //get language sub path
                pagePath = pagePath.substring(languageRoot.length());

            }


            String languageSiteParentPath = StringUtils.EMPTY;
            Resource languageSiteRootPage = null;
            //for each configuref language
            for (String key : languageMap.keySet()){

                //get language tag info
                Map<String, String> langTag = languageMap.get(key);
                String tagValue = langTag.get("value");
                String tagTitle = langTag.get("title");
                String tagDescription = langTag.get("description");

                if (isEmpty(languageSiteParentPath)) {
                    //remove country and language from end
                    languageRoot = languageRoot.substring(0, languageRoot.indexOf(tagValue));

                    //get new path
                    languageSiteRootPage = getResourceResolver().resolve(languageRoot);

                    //if valid path
                    if (!ResourceUtil.isNonExistingResource(languageSiteRootPage)) {
                        languageSiteParentPath = languageSiteRootPage.getPath();
                    }
                }


                String langPageRootPath = MessageFormat.format("{0}/{1}", languageSiteParentPath, tagValue);
                String langPagePath = MessageFormat.format("{0}/{1}{2}", languageSiteParentPath, tagValue,pagePath);


                //get language root and matching language page
                Resource langPageRoot = getResourceResolver().resolve(langPageRootPath);
                Resource langPage = getResourceResolver().resolve(langPagePath);

                if (ResourceUtil.isNonExistingResource(langPageRoot)) {
                    langPageRoot = null;
                }

                if (ResourceUtil.isNonExistingResource(langPage)) {
                    langPage = null;
                }


                //if page and root is found
                if (langPage != null && langPageRoot !=null) {


                    Map<String, String> pageInfo = new HashMap<>();
                    pageInfo.put("path", ResolverUtil.mappedUrl(getResourceResolver(), langPage.getPath()).concat(ConstantsUtil.DEFAULT_EXTENTION));
                    pageInfo.put("description", tagDescription);
                    pageInfo.put("displayTitle", tagTitle);

                    if (langPagePath.startsWith(languageRoot)) {
                        pageInfo.put("current", "true");
                    }

                    Page langPageRootPage = langPageRoot.adaptTo(Page.class);
                    if (langPageRootPage != null) {
                        String hrefLang = langPageRootPage.getLanguage().toLanguageTag();
                        String language = langPageRootPage.getLanguage().toString();
                        pageInfo.put("hreflang", hrefLang);
                        pageInfo.put("language", language);
                    }

                    languageToggleMap.put(tagValue, pageInfo);

                } else if (isShowRoot && langPageRoot != null) {
                    //if page is not found and root is found and showroot is set
                    Page langPageRootPage = langPageRoot.adaptTo(Page.class);
                    if (langPageRootPage != null) {
                        String hrefLang = langPageRootPage.getLanguage().toLanguageTag();
                        String language = langPageRootPage.getLanguage().toString();

                        Map<String, String> pageInfo = new HashMap<>();
                        pageInfo.put("path", ResolverUtil.mappedUrl(getResourceResolver(), langPageRootPage.getPath()).concat(ConstantsUtil.DEFAULT_EXTENTION));
                        pageInfo.put("description", langPageRootPage.getDescription());
                        pageInfo.put("displayTitle", langPageRootPage.getTitle());
                        pageInfo.put("hreflang", hrefLang);
                        pageInfo.put("language", language);

                        if (langPagePath.startsWith(languageRoot)) {
                            pageInfo.put("current", "true");
                        }

                        languageToggleMap.put(langPageRootPage.getName(),pageInfo);
                    }
                }


            }

        }

        componentProperties.put("languageMap", languageToggleMap);
        
    }

}